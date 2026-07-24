package com.evelyn.model;

/* This class stores the information about a reported water leak.
 * It is used to manage leak reports before sending data with gRPC services. */
public class LeakReport {

    private String location;
    private int severity;
    private String status;

    /* Creates an empty leak report.
     * The values can be added later using the setter methods. */
    public LeakReport() {
    }

    /* Creates a leak report with all required information.
     * This constructor is used when all values are available. */
    public LeakReport(String location,
                      int severity,
                      String status) {

        this.location = location;
        this.severity = severity;
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /* Returns the object information as a text.
     * This method is useful for testing and debugging. */
    @Override
    public String toString() {
        return "LeakReport{"
                + "location='" + location + '\''
                + ", severity=" + severity
                + ", status='" + status + '\''
                + '}';
    }

}