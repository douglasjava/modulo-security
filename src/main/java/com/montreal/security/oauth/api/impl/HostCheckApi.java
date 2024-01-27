package com.montreal.security.oauth.api.impl;

import com.montreal.security.oauth.api.IHostCheckApi;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

@RestController
public class HostCheckApi implements IHostCheckApi {

    @Override
    public String checkHost() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress()
                + " - " + LocalDateTime.now()
                + " - " + InetAddress.getLocalHost().getHostName();
    }

}