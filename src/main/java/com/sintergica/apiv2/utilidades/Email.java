package com.sintergica.apiv2.utilidades;

import java.util.UUID;
import lombok.Data;

/**
 * @author panther
 */
@Data
public class Email {
  /**
   * @author panther
   */
  @Data
  public final class Message {
    private String subject;
    private String body;
    private String recipients;

    public Message() {
      this.subject = "";
      this.body = "";
      this.recipients = "";
    }

    public Message(String subject, String body, String recipients) {
      this.subject = subject;
      this.body = body;
      this.recipients = recipients;
    }

    /**
     * Appends a string to the body
     *
     * @param message The string to add
     */
    public void appendToBody(String message) {
      String newBody = this.getBody() + "\n" + message;
      this.setBody(newBody);
    }
  }

  /**
   * @author panther
   */
  @Data
  public final class Server {
    private String host;
    private boolean enableAuth;
    private boolean enableTLS;
    private int smtpPort;
    private int sslPort;

    public Server() {
      this.host = "";
      this.enableAuth = true;
      this.enableTLS = true;
      this.smtpPort = 465;
      this.sslPort = 465;
    }

    public Server(String host, boolean enableAuth, boolean enableTLS, int smtpPort, int sslPort) {
      this.host = host;
      this.enableAuth = enableAuth;
      this.enableTLS = enableTLS;
      this.smtpPort = smtpPort;
      this.sslPort = sslPort;
    }
  }

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

  /** Generates an UUID and sets to the email */
  public void generateToken() {
    UUID token = UUID.randomUUID();
    this.setToken(token);
  }
}
