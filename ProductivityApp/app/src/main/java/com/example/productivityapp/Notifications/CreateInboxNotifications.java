package com.example.productivityapp.Notifications;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateInboxNotifications {
    private String messageSubject;
    private String messageBody;
    private String timeStamp;

    public CreateInboxNotifications() {
        this.messageSubject = "";
        this.messageBody ="";
        this.timeStamp = "";
    }

    public CreateInboxNotifications(String messageSubject, String messageBody) {
        this.messageSubject = messageSubject;
        this.messageBody = messageBody;
        setTimeStamp();
    }

    public String getMessageSubject() {
        return messageSubject;
    }

    public void setMessageSubject(String messageSubject) {
        this.messageSubject = messageSubject;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public void setTimeStamp(){
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Define the date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        // Format the date as a string
        this.timeStamp = dateFormat.format(currentDate);

    }

    public String getTimeStamp() {
        return timeStamp;
    }
}
