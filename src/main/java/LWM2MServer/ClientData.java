package LWM2MServer;

import Objects.ConnectionEvent;

import java.util.ArrayList;

public class ClientData {

    private String endPoint;
    private ConnectionEvent connection;
    double lat;
    double lon;
    private String batteryStatus;
    private Long batteryLevel;
    private  ArrayList<Double> temp = new ArrayList<Double>();

    public ClientData(ConnectionEvent connection) {
        this.connection = connection;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public ConnectionEvent getConnection() {
        return connection;
    }

    public void setConnection(ConnectionEvent connection) {
        this.connection = connection;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(String batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public void addToTempArray( double chicken){
        if(temp.size() < 20)
            temp.add(chicken);
        else {
            temp.remove(0);
            temp.add(chicken);
        }
    }
    public Long getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Long batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public ArrayList<Double> getTemp() {
        return temp;
    }

    public void setTemp(ArrayList<Double> temp) {
        this.temp = temp;
    }

    @Override
    public String toString() {
        return "ClientData{" +
                "endPoint='" + endPoint + '\'' +
                ", connection=" + connection +
                ", lat=" + lat +
                ", lon=" + lon +
                ", batteryStatus='" + batteryStatus + '\'' +
                ", batteryLevel=" + batteryLevel +
                ", temp=" + temp +
                '}';
    }
}
