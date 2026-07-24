package com.evelyn.model;

/* This class stores the information about a water consumption record.
 * It is used to keep the water usage data for each household. */
public class ConsumptionRecord {

    private String householdId;
    private double litres;
    private String timestamp;

    /* Creates an empty consumption record.
     * The values can be added later using the setter methods. */
    public ConsumptionRecord() {
    }

    /* Creates a consumption record with all required information.
     * This constructor is used when all values are available. */
    public ConsumptionRecord(String householdId,
                             double litres,
                             String timestamp) {

        this.householdId = householdId;
        this.litres = litres;
        this.timestamp = timestamp;
    }

    public String getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(String householdId) {
        this.householdId = householdId;
    }

    public double getLitres() {
        return litres;
    }

    public void setLitres(double litres) {
        this.litres = litres;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /* Returns the object information as a text.
     * This method is useful for testing and debugging. */
    @Override
    public String toString() {
        return "ConsumptionRecord{"
                + "householdId='" + householdId + '\''
                + ", litres=" + litres
                + ", timestamp='" + timestamp + '\''
                + '}';
    }

}
