package com.sintergica.apiv2.utilidades.email;

import lombok.Data;

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
