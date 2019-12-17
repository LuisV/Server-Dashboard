package Webscket;

import LWM2MServer.Lwm2mServer;
import Objects.ConnectionEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.handler.TextWebSocketHandler;


import java.io.IOException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



@Component
public class WebSocket extends TextWebSocketHandler implements ApplicationListener<ConnectionEvent> {


    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private ObjectWriter objectWriter;

    public WebSocket(ObjectMapper objectMapper) {
        this.objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
        System.out.println("error occured at sender " + session + throwable);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println(String.format("Session %s closed because of " + session.getId(), status.getReason()));
        sessions.remove(session.getId());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Connected ... " + session.getId());
        sessions.put(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Handling message: {}" + message);
    }

    private void sendMessageToAll(String message) {
        TextMessage textMessage = new TextMessage(message);
        sessions.forEach((key, value) -> {
            try {
                value.sendMessage(textMessage);
                System.out.println("Send message {} to socketId: {}"+ message+ key);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onApplicationEvent(ConnectionEvent event) {
        System.out.println(event);
        this.sendMessageToAll(event.getEventType()+ event.getMessage());
    }
}
