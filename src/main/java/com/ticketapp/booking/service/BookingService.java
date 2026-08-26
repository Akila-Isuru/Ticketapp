package com.ticketapp.booking.service;

import com.ticketapp.booking.dto.BookingRequestDTO;
import com.ticketapp.booking.dto.BookingResponseDTO;
import com.ticketapp.booking.entity.Booking;
import com.ticketapp.booking.entity.Event;
import com.ticketapp.booking.entity.User;
import com.ticketapp.booking.exception.NotFoundException;
import com.ticketapp.booking.repo.BookingRepository;
import com.ticketapp.booking.repo.EventRepository;
import com.ticketapp.booking.repo.UserRepository;
import com.ticketapp.booking.utill.PayHereUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepo;
    private final EventRepository eventRepo;
    private final UserRepository userRepo;

    @Value("${payhere.merchant.id}")
    private String merchantId;

    @Value("${payhere.merchant.secret}")
    private String merchantSecret;

    @Value("${payhere.currency}")
    private String currency;

    public BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO) throws Exception {
        User user = userRepo.findById(bookingRequestDTO.getUserId())
                .orElseThrow(()->new NotFoundException("User not found"));

        Event event = eventRepo.findById(bookingRequestDTO.getEventId())
                .orElseThrow(()->new NotFoundException("Event not found"));

        if(event.getAvailableTickets()<bookingRequestDTO.getTicketCount()){
            throw new Exception("Not enough tickets available for this event!");
        }
        Double totalAmount = event.getTicketPrice()*bookingRequestDTO.getTicketCount();

        event.setAvailableTickets(event.getAvailableTickets() - bookingRequestDTO.getTicketCount());
        eventRepo.save(event);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setTotalAmount(totalAmount);
        booking.setTicketCount(bookingRequestDTO.getTicketCount());
        booking.setPaymentStatus("PENDING");

        Booking savedBooking = bookingRepo.save(booking);

        String orderId = "ORDER_" + savedBooking.getId();
        String hash = PayHereUtils.generateHash(merchantId,orderId,totalAmount,currency,merchantSecret);

        BookingResponseDTO bookingResponseDTO = new BookingResponseDTO();
        bookingResponseDTO.setBookingId(savedBooking.getId());
        bookingResponseDTO.setOrderId(orderId);
        bookingResponseDTO.setMerchantId(merchantId);
        bookingResponseDTO.setEventTitle(event.getTitle());
        bookingResponseDTO.setTicketCount(savedBooking.getTicketCount());
        bookingResponseDTO.setTotalAmount(totalAmount);
        bookingResponseDTO.setCurrency(currency);
        bookingResponseDTO.setHash(hash);
        bookingResponseDTO.setPaymentStatus(savedBooking.getPaymentStatus());
        bookingResponseDTO.setBookingTime(savedBooking.getBookingTime());

        return bookingResponseDTO;
    }

    public void updatePaymentStatus(String orderId,String payherePaymentId,String status){

        Long bookingId = Long.parseLong(orderId.replace("ORDER_", ""));
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(()->new NotFoundException("Booking not found"));

        // PAID or FAILED
        booking.setPaymentStatus(status);
        booking.setPayherePaymentId(payherePaymentId);
        bookingRepo.save(booking);

    }
}
