package com.sintergica.apiv2.exceptions.globals;

import com.fasterxml.jackson.annotation.*;

import java.util.Date;

public record Warnings(String error,
                       @JsonFormat
                               (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") Date date) {}
