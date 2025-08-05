package util;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {
    private static final String HOST = "smtp.gmail.com";
    private static final String PORT = "587";
    private static final String USER = System.getenv("EMAIL_USER");
    private static final String PASS = System.getenv("EMAIL_PASS");

    public static void sendEmail(String to, String subject, String content) throws MessagingException {
        if (USER == null || PASS == null) {
            throw new MessagingException("Email credentials not configured");
        }
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USER, PASS);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(USER));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(content);
        Transport.send(message);
    }
}
