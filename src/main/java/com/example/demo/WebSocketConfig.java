package com.example.demo;

import com.example.demo.Authentication.WebsocketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private SimpleWebSocketHandler simpleWebSocketHandler;

    @Autowired
    private WebRtcHandler webRtcHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(simpleWebSocketHandler, "/ws")
                .setAllowedOrigins("*")
                .addInterceptors(new WebsocketInterceptor());

        registry
                .addHandler(webRtcHandler, "/rtc")
                .setAllowedOrigins("*")
                .addInterceptors(new WebsocketInterceptor());
    }
}
