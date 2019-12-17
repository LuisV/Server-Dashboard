package com.Luis.Server.Dashboard;

import LWM2MServer.Lwm2mServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"Webscket"})
@ComponentScan({"Objects"})
@ComponentScan({"com.Luis.Server.Dashboard"})
@ComponentScan({"LWM2MServer"})
@SpringBootApplication
public class ServerDashboardApplication {

	@Autowired
	Lwm2mServer lser;

	public static void main(String[] args) {

		SpringApplication.run(ServerDashboardApplication.class, args);
	}

}
