package com.sintergica.apiv2.dto;

import lombok.Data;

import java.util.UUID;
@Data
public class EmailDTO {
    private String fromEmail;
    private String emailPassword;
    private UUID token;
    private String subject;
    private String body;
    private String recipients;
    private UUID groupId;
}


