package com.example.easterncourier.easterncourier;

public class admin_couriers_item {
    int courierPicture;
    String courierName,numOfDesignatedClients;

    public admin_couriers_item(int courierPicture, String courierName, String numOfDesignatedClients) {

        this.courierPicture = courierPicture;
        this.courierName = courierName;
        this.numOfDesignatedClients = numOfDesignatedClients;
    }

    public int getCourierPicture() {
        return courierPicture;
    }

    public String getCourierName() {
        return courierName;
    }

    public String getNumOfDesignatedClients() {
        return numOfDesignatedClients;
    }

    public void setCourierPicture(int courierPicture) {
        this.courierPicture = courierPicture;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public void setNumOfDesignatedClients(String numOfDesignatedClients) {
        this.numOfDesignatedClients = numOfDesignatedClients;
    }
}
