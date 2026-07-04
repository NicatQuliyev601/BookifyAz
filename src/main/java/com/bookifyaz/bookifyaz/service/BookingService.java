package com.bookifyaz.bookifyaz.service;

import com.bookifyaz.bookifyaz.dto.request.BookingRequest;
import com.bookifyaz.bookifyaz.dto.response.BookingResponse;
import com.bookifyaz.bookifyaz.email.NotificationService;
import com.bookifyaz.bookifyaz.entity.*;
import com.bookifyaz.bookifyaz.redis.SlotLockService;
import com.bookifyaz.bookifyaz.repository.*;
import com.bookifyaz.bookifyaz.tenant.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);
    private final WorkingHoursRepository workingHoursRepository;
    private final TimeOffRepository timeOffRepository;
    private final ServiceRepository serviceRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final StaffRepository staffRepository;
    private final AuthorityRepository authorityRepository;
    private final TenantSubscriptionRepository tenantSubscriptionRepository;
    private final SlotLockService slotLockService;
    private final NotificationService notificationService;

    public BookingService(WorkingHoursRepository workingHoursRepository, TimeOffRepository timeOffRepository, ServiceRepository serviceRepository, BookingRepository bookingRepository, UserRepository userRepository, TenantRepository tenantRepository, StaffRepository staffRepository, AuthorityRepository authorityRepository, TenantSubscriptionRepository tenantSubscriptionRepository, SlotLockService slotLockService, NotificationService notificationService) {
        this.workingHoursRepository = workingHoursRepository;
        this.timeOffRepository = timeOffRepository;
        this.serviceRepository = serviceRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.staffRepository = staffRepository;
        this.authorityRepository = authorityRepository;
        this.tenantSubscriptionRepository = tenantSubscriptionRepository;
        this.slotLockService = slotLockService;
        this.notificationService = notificationService;
    }

    public List<LocalTime> getAvailableSlots(int serviceId, int staffId, LocalDate date) {
        //1. worker is working or not
        int dayOfTheWeek = date.getDayOfWeek().getValue();
        WorkingHours workingHours = workingHoursRepository.findByStaffIdAndDayOfWeek(staffId, dayOfTheWeek).orElseThrow(
                () -> new RuntimeException("Staff dont work this day.")
        );
        //2. check if staff is of or not this day.
        if (workingHours.isOff())
            throw new RuntimeException("Staff is off this day.");

        //3. check if worker on leave or not
        boolean staffOnLeave = timeOffRepository.isStaffOnLeave(staffId, date);
        if (staffOnLeave) throw new RuntimeException("Staff is on leave this day.");

        com.bookifyaz.bookifyaz.entity.Service service = serviceRepository.findById(serviceId).orElseThrow();
        int minDuration = service.getMinDuration();

        List<Booking> bookings = bookingRepository
                .findByStaffIdAndDateAndStatusIn(staffId,
                        date,
                        List.of("PENDING", "CONFIRMED"));

        LocalTime start = workingHours.getStartTime().withSecond(0).withNano(0);
        LocalTime end = workingHours.getEndTime().withSecond(0).withNano(0);

        List<LocalTime> slots = calculateSlots(start, end, minDuration, bookings);

        Integer tenantId = TenantContext.getCurrentTenantId();
        if (tenantId != null) {
            slots = slots.stream()
                    .filter(slot -> !slotLockService.isLocked(tenantId, staffId, date, slot))
                    .toList();
        }

        return slots;
    }

    private List<LocalTime> calculateSlots(
            LocalTime start, LocalTime end,
            int duration, List<Booking> bookings
    ) {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime current = start;

        while (current.plusMinutes(duration).compareTo(end) <= 0) {
            LocalTime slotEnd = current.plusMinutes(duration);
            LocalTime slotStart = current;

            boolean isBooked = bookings.stream().anyMatch(b ->
                    b.getStartAt().toLocalTime().isBefore(slotEnd) &&
                            b.getEndAt().toLocalTime().isAfter(slotStart)
            );

            if (!isBooked) slots.add(current);
            current = current.plusMinutes(duration);
        }
        return slots;
    }

    public BookingResponse createBooking(BookingRequest bookingRequest) {
        Integer tenantId = TenantContext.getCurrentTenantId();             // get From context
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(
                () -> new RuntimeException("Tenant not found")
        );

        slotLockService.validateAndReleaseLock(
                tenantId,
                bookingRequest.staffId(),
                bookingRequest.date(),
                bookingRequest.time(),
                bookingRequest.lockToken()
        );

        checkSubscriptionLimit(tenant);

        User client = userRepository.findByPhone(bookingRequest.phoneNumber())
                .orElseGet(() -> createGuestUser(bookingRequest.clientName(), bookingRequest.email(), bookingRequest.phoneNumber()));
        com.bookifyaz.bookifyaz.entity.Service service = serviceRepository.findById(bookingRequest.serviceId()).orElseThrow(
                () -> new RuntimeException("Not found Service")
        );
        LocalDateTime startsAt = LocalDateTime.of(bookingRequest.date(), bookingRequest.time());
        LocalDateTime endAt = startsAt.plusMinutes(service.getMinDuration());

        Staff staff = staffRepository.findById(bookingRequest.staffId()).orElseThrow(
                () -> new RuntimeException("Staff Not found")
        );

        Booking booking = new Booking();
        booking.setTenant(tenant);
        booking.setClient(client);
        booking.setStaff(staff);
        booking.setService(service);
        booking.setStartAt(startsAt);
        booking.setEndAt(endAt);
        booking.setStatus("PENDING");
        booking.setNotes(bookingRequest.notes());

        Booking savedBooking = bookingRepository.save(booking);

        notificationService.sendBookingConfirmation(savedBooking, tenant.getName());

        return new BookingResponse(
                savedBooking.getId(),
                savedBooking.getClient().getFullName(),
                savedBooking.getClient().getPhone(),
                savedBooking.getStaff().getUser().getFullName(),
                savedBooking.getService().getName(),
                savedBooking.getStartAt(),
                savedBooking.getEndAt(),
                savedBooking.getStatus(),
                savedBooking.getNotes()
        );
    }

    private User createGuestUser(String name, String email, String phone) {
        Authority ownerAuthority = authorityRepository.findByAuthority(UserAuthority.CLIENT)
                .orElseGet(() -> {
                    Authority authority = new Authority();
                    authority.setAuthority(UserAuthority.OWNER);
                    return authorityRepository.save(authority);
                });

        User user = new User(name, email, phone, List.of(ownerAuthority));
        userRepository.save(user);
        return user;
    }

    private void checkSubscriptionLimit(Tenant tenant) {
        TenantSubscription sub = tenantSubscriptionRepository
                .findActiveStatusById(tenant.getId()).orElseThrow(
                        () -> new RuntimeException("There are not active subscriptions")
                );

        int limit = sub.getSubscriptionPlan().getBookingLimit();
        if (limit == -1) return;

        int currentMonth = bookingRepository.countByTenantAndCurrentMonth(tenant.getId());
        if (currentMonth >= limit) {
            throw new RuntimeException("Monthly booking limit reached");
        }
    }
}
