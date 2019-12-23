package Objects;


import org.eclipse.leshan.core.node.LwM2mObject;
import org.eclipse.leshan.core.node.LwM2mObjectInstance;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.server.registration.Registration;
import org.springframework.context.ApplicationEvent;

import java.util.Collection;

public class ReadEvent extends ApplicationEvent {


    Registration registration;
    ReadResponse response;
    Collection<LwM2mObjectInstance> values;
    public ReadEvent(Object source, Registration registration, ReadResponse response) {
        super(source);

        this.registration = registration;
        this.response = response;
        values = ((LwM2mObject)response.getContent()).getInstances().values();
    }

    public Registration getRegistration() {
        return registration;
    }

    public ReadResponse getResponse() {
        return response;
    }

    public Collection<LwM2mObjectInstance> getValues() {
        return values;
    }
}
