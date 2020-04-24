package app.service;

import app.common.SMessageSource;
import app.database.entity.Booking;
import app.database.entity.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    public final static String CONFIRM_CODE_PARAM = "code";
    public final static String EMAIL_PARAM = "email";

    @Value("${back.url}")
    private String backUrl;

    private final EmailSender emailSender;


    public void sendEmailConfirmation(String to, String code) {
        String url = backUrl + "/api/auth/registration/confirm" +
                "?" + CONFIRM_CODE_PARAM + "=" + code +
                "&" + EMAIL_PARAM + "=" + to;
        String text = SMessageSource.get("account.confirm", url);
        emailSender.sendMessage(to, "Подтверждение аккаунта", text);
    }

    public void sendBookingApprove(Booking booking) {
        UserInfo owner = booking.getOwner();
        UserInfo renter = booking.getRenter();
        String text = SMessageSource.get("booking.approve", booking.getId(), booking.getBerthPlace().getBerth().getName(),
                owner.getFirstName(), owner.getLastName());

        emailSender.sendMessage(renter.getAccount().getEmail(), "Бронирование подтверждено", text);
    }

    public void sendBookingCreate(Booking booking) {
        UserInfo renter = booking.getRenter();
        UserInfo owner = booking.getOwner();

        String text = SMessageSource.get("booking.create", renter.getFirstName(), renter.getLastName(),
                booking.getBerthPlace().getBerth().getName());

        emailSender.sendMessage(owner.getAccount().getEmail(), "Новая заявка на бронирование", text);
    }
}
