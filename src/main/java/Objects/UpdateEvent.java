package Objects;


import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.server.registration.Registration;
import org.springframework.context.ApplicationEvent;

public class UpdateEvent extends ApplicationEvent {

    Observation observation;
    Registration registration;
    ObserveResponse response;

    public UpdateEvent(Object source, Observation observation, Registration registration, ObserveResponse response) {
        super(source);
        this.observation = observation;
        this.registration = registration;
        this.response = response;
    }
}
