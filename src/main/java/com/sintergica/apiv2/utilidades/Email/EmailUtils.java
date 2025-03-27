package com.sintergica.apiv2.utilidades.Email;

import com.sintergica.apiv2.configuration.EmailConfig;
import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author panther
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmailUtils {
  /**
   * Sets the properties given by the {@code Email} object
   *
   * @param email recives an {@code Email} object
   * @return A {@code Properties} object
   */
  private static Properties getProperties(Email email) {
    Properties properties = new Properties();

    properties.put("mail.smtp.host", email.getServer().getHost());
    properties.put("mail.smtp.port", String.valueOf(email.getServer().getSmtpPort()));
    properties.put("mail.smtp.auth", String.valueOf(email.getServer().isEnableAuth()));
    properties.put("mail.smtp.starttls.enable", String.valueOf(email.getServer().isEnableTLS()));
    properties.put("mail.smtp.socketFactory.port", String.valueOf(email.getServer().getSslPort()));
    properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    return properties;
  }

  /**
   * Sets the properties given by the environment
   *
   * @param config recives an {@code EmailConfig} object
   * @return A {@code Properties} object
   */
  private static Properties getProperties(EmailConfig config) {
    Properties properties = new Properties();

    properties.put("mail.smtp.host", config.getServer());
    properties.put("mail.smtp.port", String.valueOf(config.getSmtp_port()));
    properties.put("mail.smtp.auth", String.valueOf(config.isEnable_auth()));
    properties.put("mail.smtp.starttls.enable", String.valueOf(config.isEnable_tls()));
    properties.put("mail.smtp.socketFactory.port", String.valueOf(config.getSsl_port()));
    properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    return properties;
  }

  /**
   * Sends an email with the given configuration
   *
   * @param properties The properties of the email provider
   * @param authenticator The credentials for the email provider
   * @param email The email to send
   * @return {@code true} if the email was sent successfully or {@code false} if the email wasn't
   *     sent
   */
  private static boolean sendEmail(
      Properties properties, Authenticator authenticator, Email email) {
    Session session = Session.getInstance(properties, authenticator);
    boolean isSuccess = false;

    try {
      MimeMessage message = new MimeMessage(session);

      message.addHeader("Content-type", "text/HTML; charset=UTF-8");
      message.addHeader("format", "flowed");
      message.addHeader("Content-Transfer-Encoding", "8bit");

      message.setFrom(new InternetAddress(email.getFromEmail()));
      message.setSubject(email.getMessage().getSubject(), "UTF-8");
      message.setText(email.getMessage().getBody(), "UTF-8");
      message.setSentDate(new Date());
      message.setRecipients(
          Message.RecipientType.TO,
          InternetAddress.parse(email.getMessage().getRecipients(), false));

      Transport.send(message);
      isSuccess = true;
    } catch (MessagingException e) {
      e.printStackTrace();
    }

    return isSuccess;
  }

  /**
   * Sends an email with the given configuration
   *
   * @param email The email to send
   * @param config The email provider configuration from the enviroment
   * @return {@code true} if the email was sent successfully or {@code false} if the email wasn't
   *     sent
   */
  public static boolean sendEmail(Email email, EmailConfig config) {
    Properties properties = getProperties(config);

    Authenticator authenticator =
        new Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(config.getFrom_email(), config.getEmail_password());
          }
        };

    email.setFromEmail(config.getFrom_email());
    email.setEmailPassword(config.getEmail_password());
    return sendEmail(properties, authenticator, email);
  }

  /**
   * Sends an email
   *
   * @param email The email to send
   * @return {@code true} if the email was sent successfully or {@code false} if the email wasn't
   *     sent
   */
  public static boolean sendEmail(Email email) {
    Properties properties = getProperties(email);

    Authenticator authenticator =
        new Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(email.getFromEmail(), email.getEmailPassword());
          }
        };

    return sendEmail(properties, authenticator, email);
  }
}
