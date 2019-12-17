package Objects;

import org.eclipse.leshan.core.model.LwM2mModel;
import org.eclipse.leshan.server.registration.Registration;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


public class ConnectionEvent extends ApplicationEvent{

    private String eventType;
    private String message;
    private Registration reg;
    LwM2mModel model;
    Object data;
    public ConnectionEvent(Object source, String eventType,  String message, Registration reg, LwM2mModel model)
    {
        //Calling this super class constructor is necessary
        super(source);
        this.reg = reg;
        this.eventType = eventType;
        this.message = message;
        this.model = model;
    }

    public String getEventType() {
        return eventType;
    }

    public String getMessage() {
        return message;
    }
    public Registration getRegistration(){
        return reg;
    }
    public LwM2mModel getModel(){
        return model;
    }
}

