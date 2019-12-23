package com.Luis.Server.Dashboard;

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


    public String message = "Hell FROM THYME!";
    public static Map<String,ConnectionEvent> events = new HashMap<>();
    public static ArrayList<Double> temp = new ArrayList<Double>();
    double lat;
    double lon;
    private String errorMessage;

    @RequestMapping("/")
    public String viewHome(Model model) {
        model.addAttribute("message", message);
        model.addAttribute("links", events);
        model.addAttribute("tempArr", temp);
        model.addAttribute("lat", lat);
        model.addAttribute("lon", lon);
        //String a = events.get(0).getModel().getObjectModels();
        return "index";
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        if (event instanceof ConnectionEvent) {

            events.put(((ConnectionEvent) event).getRegistration().getEndpoint(), ((ConnectionEvent) event));
        }
        else if (event instanceof UpdateEvent) {
            System.out.println(((UpdateEvent) event).getResponse().getCoapResponse());
            String data= (((UpdateEvent) event).getResponse().getContent().toString());
            System.out.println(data);
            Pattern pattern = Pattern.compile("value=(.*?),");
            Matcher matcher = pattern.matcher(data);

            if (matcher.find())
            {
                System.out.println(matcher.group(1));
               Double chicken = Double.parseDouble(matcher.group(1));
               if(temp.size() < 20)
                    temp.add(chicken);
               else {
                   temp.remove(0);
                  temp.add(chicken);
               }
               //System.out.println(chicken);
            }
            //temp.add(Double.parseDouble(((UpdateEvent) event).getResponse().getContent().accept();));
            System.out.println("NFRJKH");
        }
        else if (event instanceof ReadEvent){
            System.out.println("CHICKEN");
            ArrayList<LwM2mObjectInstance> te = new ArrayList<LwM2mObjectInstance> (((ReadEvent)event).getValues());
            lat = (Double) te.get(0).getResources().get(0).getValue();
            lon = (Double) te.get(0).getResources().get(1).getValue();
        }

    }
}
