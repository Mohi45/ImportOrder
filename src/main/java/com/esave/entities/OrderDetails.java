package com.esave.entities;

public class OrderDetails {

    private String purveyorURL;

    private String userName;

    private String password;

    private String orderId;

    private final String purveyorId;

    private String deliverydate;
    private String accountNumber;

    public OrderDetails(String userName, String password, String orderId, String purveyorId, String deliverydate, String accountNumber) {
        super();
//		this.purveyorURL = purveyorURL;
        this.userName = userName;
        this.password = password;
        this.orderId = orderId;
        this.purveyorId = purveyorId;
        this.deliverydate = deliverydate;
        this.accountNumber = accountNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPurveyorURL() {
        return purveyorURL;
    }

    public void setPurveyorURL(String purveyorURL) {
        this.purveyorURL = purveyorURL;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPurveyorId() {
        return purveyorId;
    }


    public String getDeliverydate() {
        return deliverydate;
    }

    public void setDeliverydate(String deliverydate) {
        this.deliverydate = deliverydate;
    }

    @Override
    public String toString() {
        return "PurveyorDetails [purveyorId=" + purveyorId + ", userName=" + userName + ", password=" + password + ", deliverydate=" + deliverydate + ", accountNumber=" + accountNumber
                + "]";
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
