package com.example.scansaverapp.users.helpcenter;

public class ComplaintHelper {

    String subject, message, dateSubmitted, customerEmail, customerId;

    public ComplaintHelper () {

    }

    public ComplaintHelper(String subject, String message, String dateSubmitted, String customerEmail, String customerId) {
        this.subject = subject;
        this.message = message;
        this.customerEmail = customerEmail;
        this.dateSubmitted = dateSubmitted;
        this.customerId = customerId;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getDateSubmitted() {
        return dateSubmitted;
    }

    public String getCustomerId() {
        return customerId;
    }
}
