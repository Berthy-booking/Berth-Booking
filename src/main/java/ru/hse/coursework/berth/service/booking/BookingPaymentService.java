package ru.hse.coursework.berth.service.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hse.coursework.berth.config.exception.impl.AccessException;
import ru.hse.coursework.berth.config.exception.impl.NotFoundException;
import ru.hse.coursework.berth.config.security.OperationContext;
import ru.hse.coursework.berth.database.entity.Booking;
import ru.hse.coursework.berth.database.repository.BookingRepository;
import ru.hse.coursework.berth.service.booking.dto.BookingPayLinkResp;
import ru.hse.coursework.berth.service.booking.fsm.BookingEvent;
import ru.hse.coursework.berth.service.booking.fsm.BookingFSMHandler;
import ru.hse.coursework.berth.service.event.booking.PayedBookingEvent;

@Service
@RequiredArgsConstructor
public class BookingPaymentService {

    private final BookingRepository bookingRepository;
    private final BookingFSMHandler bookingFSMHandler;

    @Transactional
    public BookingPayLinkResp formPaymentLink(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(NotFoundException::new);
        checkAccess(booking);

        bookingFSMHandler.sendEvent(booking, BookingEvent.PAY_PREPARE);
        Double sum = booking.getServiceFee();

        return BookingPayLinkResp.of("link");
    }

    @EventListener
    @Transactional
    public void bookingPayed(PayedBookingEvent payedBookingEvent) {
        Booking booking = bookingRepository.findById(payedBookingEvent.getBookingId()).orElseThrow(NotFoundException::new);
        bookingFSMHandler.sendEvent(booking, BookingEvent.PAY);
    }

    private void checkAccess(Booking booking) {
        if (!booking.getRenter().getId().equals(OperationContext.accountId())) {
            throw new AccessException();
        }
    }
}