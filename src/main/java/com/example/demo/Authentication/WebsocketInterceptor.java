package com.example.demo.Authentication;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class WebsocketInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String token = getTokenFromRequest(request);
        if(token==null){
            return false;
        }
        else{
            if(!JWTUtil.isValidToken(token)){
                System.out.println(1);
                return false;
            }
            else{
                System.out.println(2);
                UserEntity user=JWTUtil.extractUser(token);
                attributes.put("user",user);
                return true;
            }
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
    private String getTokenFromRequest(ServerHttpRequest request) {
        // Assumes token is passed as a query parameter or header
        var headers = request.getHeaders();
        if (headers.containsKey("Authorization")) {
            String authHeader = headers.getFirst("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                System.out.println(authHeader.substring(7));
                return authHeader.substring(7);
            }
        }
        // fallback: get from query param
        String uri = request.getURI().toString();
        if (uri.contains("token=")) {
            return uri.substring(uri.indexOf("token=") + 6);
        }
        return null;
    }
}
