package LWM2MServer;
import Objects.ConnectionEvent;
import Objects.ObjectModelSerDes;
import Objects.ReadEvent;
import Objects.UpdateEvent;
import Webscket.WebSocket;
import com.maxmind.geoip2.record.City;
import org.eclipse.californium.core.network.EndpointContextMatcherFactory.MatcherMode;
import org.eclipse.californium.core.network.config.NetworkConfig;

import org.eclipse.leshan.Link;
import org.eclipse.leshan.core.model.LwM2mModel;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.node.*;
import org.eclipse.leshan.core.node.codec.DefaultLwM2mNodeEncoder;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.request.ObserveRequest;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.response.*;
import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.model.LwM2mModelProvider;
import org.eclipse.leshan.server.observation.ObservationListener;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.RegistrationService;
import org.eclipse.leshan.server.registration.RegistrationUpdate;
import org.eclipse.leshan.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.*;
import org.springframework.context.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.leshan.server.model.VersionedModelProvider;

import javax.annotation.PreDestroy;

@Component
public class Lwm2mServer implements ApplicationEventPublisherAware{


    static ApplicationEventPublisher publisher;
    private static ObjectModelSerDes serializer;
    private static RegistrationService ser;
    private static LwM2mModelProvider modelProvider;
    static LeshanServer server;
    private final static String[] modelPaths = new String[] { "31024.xml",

            "10241.xml", "10242.xml", "10243.xml", "10244.xml", "10245.xml", "10246.xml", "10247.xml",
            "10248.xml", "10249.xml", "10250.xml",

            "2048.xml", "2049.xml", "2050.xml", "2051.xml", "2052.xml", "2053.xml", "2054.xml",
            "2055.xml", "2056.xml", "2057.xml",

            "3200.xml", "3201.xml", "3202.xml", "3203.xml", "3300.xml", "3301.xml", "3302.xml",
            "3303.xml", "3304.xml", "3305.xml", "3306.xml", "3308.xml", "3310.xml", "3311.xml",
            "3312.xml", "3313.xml", "3314.xml", "3315.xml", "3316.xml", "3317.xml", "3318.xml",
            "3319.xml", "3320.xml", "3321.xml", "3322.xml", "3323.xml", "3324.xml", "3325.xml",
            "3326.xml", "3327.xml", "3328.xml", "3329.xml", "3330.xml", "3331.xml", "3332.xml",
            "3333.xml", "3334.xml", "3335.xml", "3336.xml", "3337.xml", "3338.xml", "3339.xml",
            "3340.xml", "3341.xml", "3342.xml", "3343.xml", "3344.xml", "3345.xml", "3346.xml",
            "3347.xml", "3348.xml", "3349.xml", "3350.xml",

            "Communication_Characteristics-V1_0.xml",

            "LWM2M_Lock_and_Wipe-V1_0.xml", "LWM2M_Cellular_connectivity-v1_0.xml",
            "LWM2M_APN_connection_profile-v1_0.xml", "LWM2M_WLAN_connectivity4-v1_0.xml",
            "LWM2M_Bearer_selection-v1_0.xml", "LWM2M_Portfolio-v1_0.xml", "LWM2M_DevCapMgmt-v1_0.xml",
            "LWM2M_Software_Component-v1_0.xml", "LWM2M_Software_Management-v1_0.xml",

            "Non-Access_Stratum_NAS_configuration-V1_0.xml" };

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        System.out.println("publisher: " + applicationEventPublisher);
        publisher = applicationEventPublisher;
    }

    public static void getDeviceInfo(String endpoint){
        Registration registration = ser.getByEndpoint(endpoint);
            if(registration==null){
                return;
            }





        System.out.println("\n\n\n"+registration.getAddress().getHostAddress()+"\n\n");
          ClientData result= new ClientData(null);
//          result.setIp(registration.getAddress().getHostAddress());
//          result.setHostname(registration.getAddress().getHostName());
                server.send(registration, new ReadRequest(6), 9000, new ResponseCallback<ReadResponse>() {
                    @Override
                    public void onResponse(ReadResponse response) {
                        if (response.isSuccess()) {
                            LwM2mObject obj = (LwM2mObject) response.getContent();
                            System.out.println((obj.getInstances().values()));
                            publisher.publishEvent(new ReadEvent(Lwm2mServer.class, registration, response));

                        } else {
                            System.out.println("Failed to read:" + response.getCode() + " " + response.getErrorMessage());
                        }
                    }

                    ;
                }, new ErrorCallback() {

                    @Override
                    public void onError(Exception e) {
                        System.out.println("Failed to read:");
                        e.printStackTrace();
                    }
                });


                    server.send(registration, new ReadRequest(3), 9000, new ResponseCallback<ReadResponse>() {
                        @Override
                        public void onResponse(ReadResponse response) {
                            LwM2mObject obj = (LwM2mObject) response.getContent();

                            if (response.isSuccess()) {
                                System.out.println((obj.getInstances().values()));
                                publisher.publishEvent(new ReadEvent(Lwm2mServer.class, registration, response));
                            } else {
                                System.out.println("Failed to read:" + response.getCode() + " " + response.getErrorMessage());
                            }
                        }

                        ;
                    }, new ErrorCallback() {

                        @Override
                        public void onError(Exception e) {
                            System.out.println("Failed to read:");
                            e.printStackTrace();
                        }
                    });

                }
    public static CompletableFuture<String> getLocation(String ip){
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder().uri(new URI("http://api.ipinfodb.com/v3/ip-city/?key=594300d16f6816d07e7de144a366caf0d64276db7ece09e3c72e12f785f269e5&ip="+ ip)).build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return client.sendAsync(request, BodyHandlers.ofString()).thenApply(HttpResponse::body);

    }
        public Lwm2mServer() {
            List<ObjectModel> models = ObjectLoader.loadDefault();


            models.addAll(ObjectLoader.loadDdfResources("/models/", modelPaths));
            LwM2mModelProvider pro = new VersionedModelProvider(models);
            LeshanServerBuilder builder = new LeshanServerBuilder();
            builder.setObjectModelProvider(pro);
            NetworkConfig coapConfig = LeshanServerBuilder.createDefaultNetworkConfig();
            coapConfig.set(NetworkConfig.Keys.RESPONSE_MATCHING, MatcherMode.RELAXED);
            builder.setCoapConfig(coapConfig);
            builder.setEncoder(new DefaultLwM2mNodeEncoder(new MagicLwM2mValueConverter()));

            serializer = new ObjectModelSerDes();

             server = builder.build();

            modelProvider = server.getModelProvider();
            ser = server.getRegistrationService();


            // Registration Listener for new devices
            // Observation listener
            server.getObservationService().addListener(new ObservationListener() {
                @Override
                public void newObservation(Observation observation, Registration registration) {
                    System.out.println("NEWWW");
                }

                @Override
                public void cancelled(Observation observation) {

                }

                @Override
                public void onResponse(Observation observation, Registration registration, ObserveResponse response) {
                    System.out.println(response.getContent().getId());
                    System.out.println(response.getContent());
                    // TODO: Need to finish UpdateEvent in the Objects folder first
                    //publisher.publishEvent(new ConnectionEvent(this, "tempUpdate", new String(serializer.bSerialize(model.getObjectModels())), registration, model));
                    publisher.publishEvent(new UpdateEvent(this, observation,registration,response));
                }

                @Override
                public void onError(Observation observation, Registration registration, Exception error) {
                    System.out.println(error);
                }
            });

            server.getRegistrationService().addListener(new RegistrationListener() {

                public void registered(Registration registration, Registration previousReg,
                                       Collection<Observation> previousObsersations) {
                    System.out.println("new device: " + registration.getEndpoint());
                    LwM2mModel model = modelProvider.getObjectModel(registration);

                    //String fullpath = StringUtils.removeEnd("/", "/") + "/" + StringUtils.removeStart("GeoLite2- City.mmdb", "/");
                    InputStream input = ObjectLoader.class.getResourceAsStream("/db/GeoLite2-City.mmdb");
                    if ( input== null){
                        System.out.println("\n\nDIDNT WORK\n\n");
                    } else {
                        System.out.println("\n\n WORKKKK\n\n");
                    }
                    //System.out.println(new String(serializer.bSerialize(model.getObjectModels())));
                    publisher.publishEvent(new ConnectionEvent(Lwm2mServer.class, "Register", new String(serializer.bSerialize(model.getObjectModels())), registration, model));

                        ObserveRequest request = new ObserveRequest(3303, 0, 5700);

                        server.send(registration, request, 9000, new ResponseCallback<ObserveResponse>() {
                            @Override
                            public void onResponse(ObserveResponse response) {
                                    publisher.publishEvent(new UpdateEvent(this, response.getObservation(),registration,response));
                            }
                        }, new ErrorCallback() {

                            @Override
                            public void onError(Exception e) {
                                System.out.println("Failed to read:");
                                e.printStackTrace();
                            }
                        });
                    getDeviceInfo(registration.getEndpoint());
                }

                public void updated(RegistrationUpdate update, Registration updatedReg, Registration previousReg) {
                    System.out.println("device is still here: " + updatedReg.getEndpoint());
                    getDeviceInfo(updatedReg.getEndpoint());
                }

                public void unregistered(Registration registration, Collection<Observation> observations, boolean expired,
                                         Registration newReg) {
                    System.out.println("device left: " + registration.getEndpoint());
                }
            });



            server.start();

        }
        @PreDestroy
        public void destroy() throws Exception {
            server.destroy();
        }
}


