package com.github.toy.constructor.selenium.test.steps;

import org.openqa.selenium.Alert;

public class MockAlert implements Alert {

    public static final String TEXT_OF_ALERT = "Text of the mocked alert";
    private boolean accepted;
    private boolean dismissed;
    private String sendKeys;


    @Override
    public void dismiss() {
        dismissed = true;
    }

    @Override
    public void accept() {
        accepted = true;
    }

    @Override
    public String getText() {
        return TEXT_OF_ALERT;
    }

    @Override
    public void sendKeys(String keysToSend) {
        sendKeys = keysToSend;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isDismissed() {
        return dismissed;
    }

    public void setDismissed(boolean dismissed) {
        this.dismissed = dismissed;
    }

    public String getSendKeys() {
        return sendKeys;
    }
}
