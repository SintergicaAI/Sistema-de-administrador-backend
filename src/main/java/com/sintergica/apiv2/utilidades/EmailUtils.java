package com.sintergica.apiv2.utilidades;

import com.sintergica.apiv2.configuration.EmailConfig;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.Data;

public final class EmailUtils {

    @Data
    public static class Email {
        private String fromEmail;
        private String emailPassword;
        private String subject;
        private String body;
        private String recipients;
        private UUID token;

        private String server;
        private boolean enableAuth;
        private boolean enableTLS;
        private int smtpPort;
        private int sslPort;

        public void appendToBody(String message) {
            String newBody = this.getBody() + "\n" + message;
            this.setBody(newBody);
        }

        public String generateToken() {
            UUID token = UUID.randomUUID();
            this.setToken(token);
            return token.toString();
        }
    }

    public static boolean sendEmail(Email email, EmailConfig config) {
        boolean isSuccess = false;
        Properties properties = new Properties();

        properties.put("mail.smtp.host", config.getServer());
        properties.put("mail.smtp.port", String.valueOf(config.getSmtp_port()));
        properties.put("mail.smtp.auth", String.valueOf(config.isEnable_auth()));
        properties.put("mail.smtp.starttls.enable", String.valueOf(config.isEnable_tls()));
        properties.put("mail.smtp.socketFactory.port", String.valueOf(config.getSsl_port()));
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Authenticator authenticator =
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(config.getFrom_email(), config.getEmail_password());
                    }
                };

        Session session = Session.getInstance(properties, authenticator);

        try {
            MimeMessage message = new MimeMessage(session);

            message.addHeader("Content-type", "text/HTML; charset=UTF-8");
            message.addHeader("format", "flowed");
            message.addHeader("Content-Transfer-Encoding", "8bit");

            message.setFrom(new InternetAddress(config.getFrom_email()));
            message.setSubject(email.getSubject(), "UTF-8");
            message.setText(email.getBody(), "UTF-8");
            message.setSentDate(new Date());
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(email.getRecipients(), false));

            System.out.println("Message is ready: " + message.getSubject());
            Transport.send(message);
            System.out.println("Message was Sent");
            isSuccess = true;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public static boolean sendEmail(Email email) {
        boolean isSuccess = false;
        Properties properties = new Properties();

        properties.put("mail.smtp.host", email.getServer());
        properties.put("mail.smtp.port", String.valueOf(email.getSmtpPort()));
        properties.put("mail.smtp.auth", String.valueOf(email.isEnableAuth()));
        properties.put("mail.smtp.starttls.enable", String.valueOf(email.isEnableTLS()));
        properties.put("mail.smtp.socketFactory.port", String.valueOf(email.getSslPort()));
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Authenticator authenticator =
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(email.getFromEmail(), email.getEmailPassword());
                    }
                };

        Session session = Session.getInstance(properties, authenticator);

        try {
            MimeMessage message = new MimeMessage(session);

            message.addHeader("Content-type", "text/HTML; charset=UTF-8");
            message.addHeader("format", "flowed");
            message.addHeader("Content-Transfer-Encoding", "8bit");

            message.setFrom(new InternetAddress(email.getFromEmail()));
            message.setSubject(email.getSubject(), "UTF-8");
            message.setText(email.getBody(), "UTF-8");
            message.setSentDate(new Date());
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(email.getRecipients(), false));

            System.out.println("Message is ready: " + message.getSubject());
            Transport.send(message);
            System.out.println("Message was Sent");
            isSuccess = true;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
}
