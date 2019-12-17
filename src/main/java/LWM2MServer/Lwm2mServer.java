package LWM2MServer;
import Objects.ConnectionEvent;
import Objects.ObjectModelSerDes;
import Webscket.WebSocket;
import org.eclipse.californium.core.network.EndpointContextMatcherFactory.MatcherMode;
import org.eclipse.californium.core.network.config.NetworkConfig;

import org.eclipse.leshan.Link;
import org.eclipse.leshan.core.model.LwM2mModel;
import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.request.ObserveRequest;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.model.LwM2mModelProvider;
import org.eclipse.leshan.server.observation.ObservationListener;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.RegistrationService;
import org.eclipse.leshan.server.registration.RegistrationUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.*;
import org.springframework.context.*;
import java.util.Collection;
import java.util.List;

import org.eclipse.leshan.server.model.VersionedModelProvider;

@Component
public class Lwm2mServer implements ApplicationEventPublisherAware{


    ApplicationEventPublisher publisher;
    private final ObjectModelSerDes serializer;
    private RegistrationService ser;
    private final LwM2mModelProvider modelProvider;


    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        System.out.println("publisher: " + applicationEventPublisher);
        this.publisher = applicationEventPublisher;
    }

        public Lwm2mServer() {
            List<ObjectModel> models = ObjectLoader.loadDefault();
            LwM2mModelProvider pro = new VersionedModelProvider(models);

            LeshanServerBuilder builder = new LeshanServerBuilder();
            builder.setObjectModelProvider(pro);
            NetworkConfig coapConfig = LeshanServerBuilder.createDefaultNetworkConfig();
            coapConfig.set(NetworkConfig.Keys.RESPONSE_MATCHING, MatcherMode.RELAXED);
            builder.setCoapConfig(coapConfig);

            serializer = new ObjectModelSerDes();

            final LeshanServer server = builder.build();

            modelProvider = server.getModelProvider();
            ser = server.getRegistrationService();


            // Registration Listener for new devices
            server.getRegistrationService().addListener(new RegistrationListener() {

                public void registered(Registration registration, Registration previousReg,
                                       Collection<Observation> previousObsersations) {
                    System.out.println("new device: " + registration.getEndpoint());
                    String temp="";
                    LwM2mModel model = modelProvider.getObjectModel(registration);

                    System.out.println(new String(serializer.bSerialize(model.getObjectModels())));
                    try {
                        //System.out.println("Supported Devices: ");
//                        for ( ObjectModel l: modelProvider.getObjectModel(registration).getObjectModels()
//                                ) {
//                            System.out.println(l);
//                            System.out.println(" ");
//                            temp+=l;
//                            temp+="\n";
//                        }

                        publisher.publishEvent(new ConnectionEvent(this, "Register", new String(serializer.bSerialize(model.getObjectModels())), registration, model));
                        // Make the Observe request here
                        ObserveRequest request = new ObserveRequest(6, 0, 0);
                        server.send(registration, request, 5000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                public void updated(RegistrationUpdate update, Registration updatedReg, Registration previousReg) {
                    System.out.println("device is still here: " + updatedReg.getEndpoint());
                  //  LwM2mModel model = modelProvider.getObjectModel(ser.getByEndpoint("endpoint me test"));
                    //update.
                   // System.out.println(new String(serializer.bSerialize(model.getObjectModels())));

                }

                public void unregistered(Registration registration, Collection<Observation> observations, boolean expired,
                                         Registration newReg) {
                    System.out.println("device left: " + registration.getEndpoint());
                }
            });


            //// Observation listener
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
                    System.out.println(response.getContent());
                    // TODO: Need to finish UpdateEvent in the Objects folder first
                    //publisher.publishEvent(new ConnectionEvent(this, "tempUpdate", new String(serializer.bSerialize(model.getObjectModels())), registration, model));
                }

                @Override
                public void onError(Observation observation, Registration registration, Exception error) {
                    System.out.println(error);
                }
            });
            server.start();
        }


}


