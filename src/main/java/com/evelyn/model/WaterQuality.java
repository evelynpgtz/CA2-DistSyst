package com.evelyn.model;

/* This class stores the information about a water quality reading.
 * It is used by the application before sending data with gRPC services. */
public class WaterQuality {

    private String sensorId;
    private double phLevel;
    private double turbidity;
    private double temperature;
    private boolean safe;

    /* Creates an empty water quality object.
     * The values can be added later using the setter methods. */
    public WaterQuality() {
    }

    /* Creates a water quality object with all sensor information.
     * This constructor is used when all values are available. */
    public WaterQuality(String sensorId,
                        double phLevel,
                        double turbidity,
                        double temperature,
                        boolean safe) {

        this.sensorId = sensorId;
        this.phLevel = phLevel;
        this.turbidity = turbidity;
        this.temperature = temperature;
        this.safe = safe;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public double getPhLevel() {
        return phLevel;
    }

    public void setPhLevel(double phLevel) {
        this.phLevel = phLevel;
    }

    public double getTurbidity() {
        return turbidity;
    }

    public void setTurbidity(double turbidity) {
        this.turbidity = turbidity;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public boolean isSafe() {
        return safe;
    }

    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    /* Returns the object information as a text.
     * This method is useful for testing and debugging. */
    @Override
    public String toString() {
        return "WaterQuality{"
                + "sensorId='" + sensorId + '\''
                + ", phLevel=" + phLevel
                + ", turbidity=" + turbidity
                + ", temperature=" + temperature
                + ", safe=" + safe
                + '}';
    }

}