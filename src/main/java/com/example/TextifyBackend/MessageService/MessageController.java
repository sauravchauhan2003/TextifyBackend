package com.example.TextifyBackend.MessageService;

import com.example.TextifyBackend.Authentication.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private AuthService authService;

    @GetMapping("/getmessages")
    public List<Message> getmessages(HttpServletRequest request){
        String auth_header=request.getHeader("Authorization");
        String token =null;
        String username=null;
        if(auth_header!=null && auth_header.startsWith("Bearer")){
            token=auth_header.substring(7);
            username=authService.extractUsername(token);
        }
        List<Message> messagesList=messageService.receiveMessage(username);
        if(messagesList.isEmpty()){
            return null;
        }
        else return messagesList;
    }

    @PostMapping("/sendmessage")
    public void sendmessage(@RequestBody Message message){
        messageService.sendMessage(message);
    }
}
