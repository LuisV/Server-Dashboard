package Webscket;

import LWM2MServer.ClientData;
import LWM2MServer.Lwm2mServer;
import com.Luis.Server.Dashboard.HomeController;
import Objects.ConnectionEvent;
import Objects.UpdateEvent;
import com.eclipsesource.json.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class WebSocket extends TextWebSocketHandler {


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
        JsonObject j = Json.parse(message.getPayload()).asObject();

         if("DeviceUpdate".equals(j.getString("action", "none"))){




                Lwm2mServer.getDeviceInfo(j.getString("endpoint", ""));
                JsonObject result = new JsonObject();

                ClientData client = HomeController.events.get(j.getString("endpoint",""));
                if(client != null){
                    result.add("device", j.getString("endpoint","") );
                    //result.add("lat",client.getLat());
                    //result.add("lon", client.getLon());
                    result.add("batteryLevel", client.getBatteryLevel());
                    result.add("lat",client.getLat());
                    result.add("lon", client.getLon());
                    result.add("batteryStatus", client.getBatteryStatus());
                    sendMessageToAll(result.toString());
                }
                }
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

    @EventListener
    public void handleConnectionEvent(ConnectionEvent event){
        JsonObject p = new JsonObject();
        p.add("newDevice", event.getRegistration().getEndpoint());
        p.add("device", event.getRegistration().getEndpoint() );
        this.sendMessageToAll(p.toString());
    }

    @EventListener
    public void handleUpdateEvent(UpdateEvent event){
        String data= event.getResponse().getContent().toString();

        Pattern pattern = Pattern.compile("value=(.*?),");
        Matcher matcher = pattern.matcher(data);
        double num = 0.0;
        if (matcher.find())
        {
            num = Double.parseDouble(matcher.group(1));
        }
        JsonObject j = new JsonObject();
        j.add("tempUpdate", num);
        j.add("device", ((UpdateEvent) event).getRegistration().getEndpoint() );
        this.sendMessageToAll(j.toString());
    }
}
