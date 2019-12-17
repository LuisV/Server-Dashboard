package com.Luis.Server.Dashboard;

import Objects.ConnectionEvent;
import org.eclipse.leshan.core.model.LwM2mModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.eclipse.leshan.server.model.LwM2mModelProvider;

@Controller
public class HomeController implements ApplicationListener<ConnectionEvent>  {


    public String message = "Hell FROM THYME!";
    public static List<ConnectionEvent> events = new ArrayList<>();
    double current;
    private String errorMessage;

    @RequestMapping("/")
    public String viewHome(Model model) {
        model.addAttribute("message", message);
        model.addAttribute("links", events);
        //model.addAttribute("links", events.get(0).getModel().getObjectModels());
        //String a = events.get(0).getModel().getObjectModels();
        return "index";
    }

    @Override
    public void onApplicationEvent(ConnectionEvent event) {
        events.add(event);
    }

}
