package com.example.scansaveradmin.admin.requests.requestlist;

public class RequestListModel {

    String subject, message, dateSubmitted, customerEmail, customerId;

    public RequestListModel() {}

    public RequestListModel(String subject, String message, String dateSubmitted, String customerEmail, String customerId) {
        this.subject = subject;
        this.message = message;
        this.dateSubmitted = dateSubmitted;
        this.customerEmail = customerEmail;
        this.customerId = customerId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(String dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
