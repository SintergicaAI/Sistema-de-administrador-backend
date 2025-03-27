package com.sintergica.apiv2.utilidades.Email;

import lombok.Data;

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
        this.smtpPort = 0;
        this.sslPort = 0;
    }

    public Server(String host, boolean enableAuth, boolean enableTLS, int smtpPort, int sslPort) {
        this.host = host;
        this.enableAuth = enableAuth;
        this.enableTLS = enableTLS;
        this.smtpPort = smtpPort;
        this.sslPort = sslPort;
    }
}