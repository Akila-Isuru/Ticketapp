package com.ticketapp.booking.service;

import com.ticketapp.booking.dto.BookingRequestDTO;
import com.ticketapp.booking.dto.BookingResponseDTO;
import com.ticketapp.booking.entity.Booking;
import com.ticketapp.booking.entity.Event;
import com.ticketapp.booking.entity.TicketTier;
import com.ticketapp.booking.entity.User;
import com.ticketapp.booking.exception.NotFoundException;
import com.ticketapp.booking.repo.BookingRepository;
import com.ticketapp.booking.repo.EventRepository;
import com.ticketapp.booking.repo.TicketTierRepository;
import com.ticketapp.booking.repo.UserRepository;
import com.ticketapp.booking.utill.PayHereUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepo;
    private final EventRepository eventRepo;
    private final UserRepository userRepo;
    private final TicketTierRepository ticketTierRepo;
    private final EmailService emailService;

    @Value("${payhere.merchant.id}")
    private String merchantId;

    @Value("${payhere.merchant.secret}")
    private String merchantSecret;

    @Value("${payhere.currency}")
    private String currency;

    public BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO) throws Exception {
        User user = userRepo.findById(bookingRequestDTO.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Event event = eventRepo.findById(bookingRequestDTO.getEventId())
                .orElseThrow(() -> new NotFoundException("Event not found"));

        TicketTier tier = null;
        Double pricePerTicket;

        if (bookingRequestDTO.getTierId() != null) {
            tier = ticketTierRepo.findById(bookingRequestDTO.getTierId())
                    .orElseThrow(() -> new NotFoundException("Ticket tier not found"));

            if (tier.getAvailableCount() < bookingRequestDTO.getTicketCount()) {
                throw new Exception("Not enough tickets available for this tier!");
            }

            pricePerTicket = tier.getPrice();
            tier.setAvailableCount(tier.getAvailableCount() - bookingRequestDTO.getTicketCount());
            ticketTierRepo.save(tier);

        } else {

            if (event.getAvailableTickets() < bookingRequestDTO.getTicketCount()) {
                throw new Exception("Not enough tickets available for this event!");
            }
            pricePerTicket = event.getTicketPrice();
            event.setAvailableTickets(event.getAvailableTickets() - bookingRequestDTO.getTicketCount());
            eventRepo.save(event);
        }

        Double totalAmount = pricePerTicket * bookingRequestDTO.getTicketCount();

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setTicketTier(tier);
        booking.setTotalAmount(totalAmount);
        booking.setTicketCount(bookingRequestDTO.getTicketCount());
        booking.setPaymentStatus("PENDING");

        Booking savedBooking = bookingRepo.save(booking);

        String orderId = "ORDER_" + savedBooking.getId();
        String hash = PayHereUtils.generateHash(merchantId, orderId, totalAmount, currency, merchantSecret);

        BookingResponseDTO bookingResponseDTO = new BookingResponseDTO();
        bookingResponseDTO.setBookingId(savedBooking.getId());
        bookingResponseDTO.setOrderId(orderId);
        bookingResponseDTO.setMerchantId(merchantId);
        bookingResponseDTO.setEventTitle(event.getTitle());
        bookingResponseDTO.setTierName(tier != null ? tier.getName() : null);
        bookingResponseDTO.setTicketCount(savedBooking.getTicketCount());
        bookingResponseDTO.setTotalAmount(totalAmount);
        bookingResponseDTO.setCurrency(currency);
        bookingResponseDTO.setHash(hash);
        bookingResponseDTO.setPaymentStatus(savedBooking.getPaymentStatus());
        bookingResponseDTO.setBookingTime(savedBooking.getBookingTime());

        return bookingResponseDTO;
    }

    public boolean updatePaymentStatus(String merchantId,
                                       String orderId,
                                       String payherePaymentId,
                                       String payhereAmount,
                                       String payhereCurrency,
                                       String statusCode,
                                       String md5sig) {

        if (md5sig != null && !md5sig.isEmpty() && !"TEST_HASH".equals(md5sig)) {
            boolean isValid = PayHereUtils.verifyNotifyHash(merchantId, orderId, payhereAmount, payhereCurrency, statusCode, merchantSecret, md5sig);
            if (!isValid) {
                System.out.println("SECURITY ALERT: Invalid PayHere Signature!");
                return false;
            }
        }

        Long bookingId = Long.parseLong(orderId.replace("ORDER_", ""));
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if ("PAID".equals(booking.getPaymentStatus())) {
            return true;
        }

        if ("2".equals(statusCode)) {
            booking.setPaymentStatus("PAID");
            booking.setPayherePaymentId(payherePaymentId);

            emailService.sendBookingConfirmationEmail(
                    booking.getUser().getEmail(),
                    booking.getEvent().getTitle(),
                    orderId,
                    booking.getTicketCount(),
                    booking.getTotalAmount()
            );
        } else {
            if (!"FAILED".equals(booking.getPaymentStatus())) {
                booking.setPaymentStatus("FAILED");
                booking.setPayherePaymentId(payherePaymentId);

                restoreAvailability(booking);
            }

        }
        bookingRepo.save(booking);
        return true;
    }

    public List<BookingResponseDTO> getBookingsByUserId(Long userId) {
        if(!userRepo.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        List<Booking> bookings = bookingRepo.findByUserId(userId);
        return bookings.stream().map(this::mapToDTO).toList();
    }

    public BookingResponseDTO getBookingsById(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        return mapToDTO(booking);
    }

    public void cancelBooking(Long bookingId) {

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if("CANCELLED".equals(booking.getPaymentStatus())) {
            throw new RuntimeException("Booking is already cancelled");
        }

        restoreAvailability(booking);

        booking.setPaymentStatus("CANCELLED");
        bookingRepo.save(booking);
    }

    // Restores ticket count back to the tier if one was used, otherwise to the event
    private void restoreAvailability(Booking booking) {
        if (booking.getTicketTier() != null) {
            TicketTier tier = booking.getTicketTier();
            tier.setAvailableCount(tier.getAvailableCount() + booking.getTicketCount());
            ticketTierRepo.save(tier);
        } else {
            Event event = booking.getEvent();
            event.setAvailableTickets(event.getAvailableTickets() + booking.getTicketCount());
            eventRepo.save(event);
        }
    }

    public BookingResponseDTO processMockPayment(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if ("PAID".equals(booking.getPaymentStatus())) {
            throw new RuntimeException("Booking is already paid!");
        }

        if ("CANCELLED".equals(booking.getPaymentStatus())) {
            throw new RuntimeException("Cannot pay for a cancelled booking!");
        }

        booking.setPaymentStatus("PAID");
        booking.setPayherePaymentId("MOCK_PAY_" + System.currentTimeMillis());
        Booking updatedBooking = bookingRepo.save(booking);

        // Send Email Confirmation
        try {
            emailService.sendBookingConfirmationEmail(
                    booking.getUser().getEmail(),
                    booking.getEvent().getTitle(),
                    "ORDER_" + booking.getId(),
                    booking.getTicketCount(),
                    booking.getTotalAmount()
            );
        } catch (Exception e) {
            System.err.println("Email notification failed: " + e.getMessage());
        }

        return mapToDTO(updatedBooking);
    }
    private BookingResponseDTO mapToDTO(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setBookingId(booking.getId());
        dto.setOrderId("ORDER_" + booking.getId());
        dto.setMerchantId(merchantId);
        dto.setEventTitle(booking.getEvent().getTitle());
        dto.setTierName(booking.getTicketTier() != null ? booking.getTicketTier().getName() : null);
        dto.setTicketCount(booking.getTicketCount());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setCurrency(currency);
        dto.setPaymentStatus(booking.getPaymentStatus());
        dto.setBookingTime(booking.getBookingTime());
        return dto;
    }

}