package com.Luis.Server.Dashboard;

import LWM2MServer.ClientData;
import Objects.ConnectionEvent;
import Objects.UpdateEvent;
import Objects.ReadEvent;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.node.LwM2mObjectInstance;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class HomeController {


    // Container for Client data mapped to their endpoint names
    public static Map<String, ClientData> events = new HashMap<>();
    @RequestMapping("/")
    public String viewHome(Model model) {

        // Thymeleaf mode attribute
        model.addAttribute("links", events);

        return "index";
    }

    // A new client has connected to the server
    // Save its data and get its location from IP
    @EventListener
    public void handleConnectionEvent(ConnectionEvent event){
        ClientData newClient = new ClientData (event);

        try {
            InputStream database = ObjectLoader.class.getResourceAsStream("/db/GeoLite2-City.mmdb");
            newClient.setIp(event.getRegistration().getAddress().getHostAddress());

            String addr = event.getRegistration().getAddress().getHostAddress();
            DatabaseReader reader = new DatabaseReader.Builder(database).build();
            InetAddress ip = InetAddress.getByName(addr);
            CityResponse city = reader.city(ip);
            newClient.setLat(city.getLocation().getLatitude());
            newClient.setLon(city.getLocation().getLongitude());
            newClient.setHostname(city.getCity().getName());
            reader.close();
        } catch ( IOException | GeoIp2Exception e) {
            System.out.println(e.getMessage());
            newClient.setLat(0.000);
            newClient.setLon(0.000);
            newClient.setHostname("Unknown");
        } finally {
            events.put(event.getRegistration().getEndpoint(), newClient);

        }
    }

    // Response to an observe request
    @EventListener
    public void handleUpdateEvent(UpdateEvent event){

        // Note - There must be a better way to do this
        // but this method works...
        String data= (event.getResponse().getContent().toString());
        Pattern pattern = Pattern.compile("value=(.*?),");
        Matcher matcher = pattern.matcher(data);

        if (matcher.find())
        {

            double num = Double.parseDouble(matcher.group(1));
            // Add to Temperature array in the events container
            events.get(event.getRegistration().getEndpoint()).addToTempArray(num);
        }
    }

    // Response to single read request
    @EventListener
    public void handleReadEvent(ReadEvent event){

        ArrayList<LwM2mObjectInstance> te = new ArrayList<>(((ReadEvent) event).getValues());

            events.get( event.getRegistration().getEndpoint()).setBatteryLevel((Long) te.get(0).getResources().get(9).getValue());
            String batteryStatus = "";
            switch (Math.toIntExact((long) te.get(0).getResources().get(20).getValue())) {
                case 0: {
                    batteryStatus = "Normal";
                    break;
                }
                case 1: {
                    batteryStatus = "Charging";
                    break;
                }
                case 2: {
                    batteryStatus = "Charge Complete";
                    break;
                }
                case 4: {
                    batteryStatus = "Low Battery!";
                    break;
                }
            }

            events.get(((ReadEvent) event).getRegistration().getEndpoint()).setBatteryStatus(batteryStatus);

    }
    // Listener for events, these events are implemented in Objects
//    @Override
//    public void onApplicationEvent(ApplicationEvent event) {
//
//        if (event instanceof ConnectionEvent) {
//            // New Client Connection
//            events.put(((ConnectionEvent) event).getRegistration().getEndpoint(), new ClientData ((ConnectionEvent) event));
//            InputStream database = ObjectLoader.class.getResourceAsStream("/db/GeoLite2-City.mmdb");
//
//            try {
//                events.get(((ConnectionEvent) event).getRegistration().getEndpoint()).setIp(((ConnectionEvent) event).getRegistration().getAddress().getHostAddress());
//
//                String addr = ((ConnectionEvent) event).getRegistration().getAddress().getHostAddress();
//                DatabaseReader reader = new DatabaseReader.Builder(database).build();
//                InetAddress ip = InetAddress.getByName(addr);
//                CityResponse city = reader.city(ip);
//                events.get(((ConnectionEvent) event).getRegistration().getEndpoint()).setLat(city.getLocation().getLatitude());
//                events.get(((ConnectionEvent) event).getRegistration().getEndpoint()).setLon(city.getLocation().getLongitude());
//                events.get(((ConnectionEvent) event).getRegistration().getEndpoint()).setHostname(city.getCity().getName());
//            } catch (IOException | GeoIp2Exception e) {
//                e.printStackTrace();
//                events.get(((ConnectionEvent) event).getRegistration().getEndpoint()).setLat(0.000);
//                events.get(((ConnectionEvent) event).getRegistration().getEndpoint()).setLon(0.000);
//                events.get(((ConnectionEvent) event).getRegistration().getEndpoint()).setHostname("Unknown");
//
//            }
//
//        }
//        else if (event instanceof UpdateEvent) {
//
//            // New response to an observe request
//            // Note = There must be a better way to do this
//            // but this method works...
//            String data= (((UpdateEvent) event).getResponse().getContent().toString());
//            Pattern pattern = Pattern.compile("value=(.*?),");
//            Matcher matcher = pattern.matcher(data);
//
//            if (matcher.find())
//            {
//
//               double num = Double.parseDouble(matcher.group(1));
//                // Add to Temperature array in the events container
//               events.get(((UpdateEvent) event).getRegistration().getEndpoint()).addToTempArray(num);
//            }
//        }
//        else if (event instanceof ReadEvent) {
//           // New response to a read request
//            ArrayList<LwM2mObjectInstance> te = new ArrayList<>(((ReadEvent) event).getValues());
//
//            // There must be a better way to differentiate between requests
//            // I simply used the size of the data
//            if (te.get(0).getResources().size() == 16) {
//                events.get(((ReadEvent) event).getRegistration().getEndpoint()).setBatteryLevel((Long) te.get(0).getResources().get(9).getValue());
//                String batteryStatus = "";
//                switch (Math.toIntExact((long) te.get(0).getResources().get(20).getValue())) {
//                    case 0: {
//                        batteryStatus = "Normal";
//                        break;
//                    }
//                    case 1: {
//                        batteryStatus = "Charging";
//                        break;
//                    }
//                    case 2: {
//                        batteryStatus = "Charge Complete";
//                        break;
//                    }
//                    case 4: {
//                        batteryStatus = "Low Battery!";
//                        break;
//                    }
//                }
//
//                events.get(((ReadEvent) event).getRegistration().getEndpoint()).setBatteryStatus(batteryStatus);
//            }
////            } else {
////                events.get(((ReadEvent) event).getRegistration().getEndpoint()).setLat ((Double) te.get(0).getResources().get(0).getValue());
////                events.get(((ReadEvent) event).getRegistration().getEndpoint()).setLon ( (Double) te.get(0).getResources().get(1).getValue());
////            }
//        }
//
//    }
}
