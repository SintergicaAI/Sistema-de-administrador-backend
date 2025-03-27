package com.sintergica.apiv2.utilidades.Email;

import java.util.UUID;
import lombok.Data;

/**
 * @author panther
 */
@Data
public class Email {

  private String fromEmail;
  private String emailPassword;
  private UUID token;
  private Message message;
  private Server server;

  public Email() {
    this.fromEmail = "";
    this.emailPassword = "";
    this.token = null;
    this.message = new Message();
    this.server = new Server();
  }

  public Email(String fromEmail, String emailPassword, UUID token, Message message, Server server) {
    this.fromEmail = fromEmail;
    this.emailPassword = emailPassword;
    this.token = token;
    this.message = message;
    this.server = server;
  }

  public Email(String fromEmail, String emailPassword, UUID token, Message message) {
    this.fromEmail = fromEmail;
    this.emailPassword = emailPassword;
    this.token = token;
    this.message = message;
  }

  /** Generates a UUID and sets to the email */
  public void generateToken() {
    UUID token = UUID.randomUUID();
    this.setToken(token);
  }
}
