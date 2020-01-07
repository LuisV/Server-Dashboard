package com.Luis.Server.Dashboard;

import LWM2MServer.ClientData;
import Objects.ConnectionEvent;
import Objects.UpdateEvent;
import Objects.ReadEvent;
import org.eclipse.leshan.core.model.LwM2mModel;
import org.eclipse.leshan.core.node.LwM2mObjectInstance;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.LwM2mSingleResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.leshan.server.model.LwM2mModelProvider;

@Controller
public class HomeController implements ApplicationListener<ApplicationEvent>  {


    // Container for Client data mapped to their endpoint names
    public static Map<String, ClientData> events = new HashMap<>();
    @RequestMapping("/")
    public String viewHome(Model model) {

        // Thymeleaf mode attribute
        model.addAttribute("links", events);

        return "index";
    }

    // Listener for events, these events are implemented in com.Objects
    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        if (event instanceof ConnectionEvent) {
            // New Client Connection
            events.put(((ConnectionEvent) event).getRegistration().getEndpoint(), new ClientData ((ConnectionEvent) event));
        }
        else if (event instanceof UpdateEvent) {

            // New response to an observe request
            // Note = There must be a better way to do this
            // but this method works...
            String data= (((UpdateEvent) event).getResponse().getContent().toString());
            Pattern pattern = Pattern.compile("value=(.*?),");
            Matcher matcher = pattern.matcher(data);

            if (matcher.find())
            {

               Double num = Double.parseDouble(matcher.group(1));
                // Add to Temperature array in the events container
               events.get(((UpdateEvent) event).getRegistration().getEndpoint()).addToTempArray(num);
            }
        }
        else if (event instanceof ReadEvent) {
           // New response to a read request
            ArrayList<LwM2mObjectInstance> te = new ArrayList<LwM2mObjectInstance>(((ReadEvent) event).getValues());

            // There must be a better way to differentiate between requests
            // I simply used the size of the data
            if (te.get(0).getResources().size() == 16) {
                events.get(((ReadEvent) event).getRegistration().getEndpoint()).setBatteryLevel ((Long) te.get(0).getResources().get(9).getValue());
                String batteryStatus="";
               switch(Math.toIntExact((long) te.get(0).getResources().get(20).getValue()))
               {
                   case 0: {batteryStatus = "Normal"; break;}
                   case 1: {batteryStatus ="Charging"; break;}
                   case 2: {batteryStatus ="Charge Complete"; break;}
                   case 4: {batteryStatus = "Low Battery!"; break;}
               }

                events.get(((ReadEvent) event).getRegistration().getEndpoint()).setBatteryStatus (batteryStatus);
            } else {
                events.get(((ReadEvent) event).getRegistration().getEndpoint()).setLat ((Double) te.get(0).getResources().get(0).getValue());
                events.get(((ReadEvent) event).getRegistration().getEndpoint()).setLon ( (Double) te.get(0).getResources().get(1).getValue());
            }
        }

    }
}
