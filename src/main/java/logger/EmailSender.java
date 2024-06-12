package logger;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {
    static public void emailMsg(String title, String msg){
        // Замініть дані from/to а також appPassword на свої почти, звідки та куди будуть надсилатись листи про критичні помилки. 
        // appPassword - Це пароль додатку для надсилання листів з 'from' почти, він створюється в Gmail >>> Безпека >>> Двохетапна перевірка >>> Паролі додатків
        String from = "fromYourEmail@example.com";
        String appPassword = "app password for 'from' email"; //
        String to = "toYourEmail@example.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator()
        {
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(from, appPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(title);
            message.setText(msg);

            Transport.send(message);

            System.out.println("Email sent successfully.");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
