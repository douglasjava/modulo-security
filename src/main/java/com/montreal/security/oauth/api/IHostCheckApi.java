package com.montreal.security.oauth.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.UnknownHostException;

@RequestMapping(value = "hostcheck", produces = MediaType.APPLICATION_JSON_VALUE)
public interface IHostCheckApi {

    @GetMapping()
    String checkHost() throws UnknownHostException;

}
