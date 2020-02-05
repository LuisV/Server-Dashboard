# Server-Dashboard
Dashboard for LWM2M service
- To be used with https://github.com/LuisV/lwm2m-Client
- View demo here: http://ec2-18-219-244-119.us-east-2.compute.amazonaws.com:8080 `Must be on guest wifi to access`

## Dashboard for device information over LWM2M protocol
Springboot Application, build with Maven and deploy to a Tomcat Server

### Profiles
- Use the `dev` profile when running on local machine, it is on by default
- Use the `prod` when running on the cloud
This changes the websocket link but it could also be used to streamline the development process.

### How it works
The application initiates a Springboot and LWM2M server simulataneously. 
The Spring boot server provides a front end gui to the end user.
The LWM2M server provides an entry point to client devices.
These two processes communicate via application events.

There is also a websocket that establishes a connection between the server and end user to provide realtime data.
