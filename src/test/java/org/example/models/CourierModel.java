package org.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderModel {
    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("address")
    private String address;

    @JsonProperty("metroStation")
    private int metroStation;

    @JsonProperty("deliveryDate")
    private String deliveryDate;

    @JsonProperty("deliveryTime")
    private String deliveryTime;

    @JsonProperty("color")
    private String color;

    @JsonProperty("rentTime")
    private int rentTime;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("cost")
    private int cost;

    @JsonProperty("courierId")
    private String courierId;

    public OrderModel() {
    }

    public OrderModel(String firstName, String lastName, String address, int metroStation, String deliveryDate,
                      String deliveryTime, String color, int rentTime, String comment, int cost) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.deliveryDate = deliveryDate;
        this.deliveryTime = deliveryTime;
        this.color = color;
        this.rentTime = rentTime;
        this.comment = comment;
        this.cost = cost;
    }

    // Геттеры и сеттеры
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public int getMetroStation() { return metroStation; }
    public void setMetroStation(int metroStation) { this.metroStation = metroStation; }
    public String getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; }
    public String getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(String deliveryTime) { this.deliveryTime = deliveryTime; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public int getRentTime() { return rentTime; }
    public void setRentTime(int rentTime) { this.rentTime = rentTime; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }
    public String getCourierId() { return courierId; }
    public void setCourierId(String courierId) { this.courierId = courierId; }
}