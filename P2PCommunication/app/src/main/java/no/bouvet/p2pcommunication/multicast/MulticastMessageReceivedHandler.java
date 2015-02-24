package no.bouvet.p2pcommunication.multicast;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import no.bouvet.p2pcommunication.listener.MulticastMessageReceivedListener;

public class MulticastMessageReceivedHandler extends Handler {

    public static final String RECEIVED_MESSAGE = "RECEIVED_MESSAGE";
    public static final String SENDER_IP_ADDRESS = "SENDER_IP_ADDRESS";
    private MulticastMessageReceivedListener multicastMessageReceivedListener;

    public MulticastMessageReceivedHandler(MulticastMessageReceivedListener multicastMessageReceivedListener) {
        this.multicastMessageReceivedListener = multicastMessageReceivedListener;
    }

    @Override
    public void handleMessage(Message message) {
        Bundle receivedData = message.getData();
        String receivedMessage = receivedData.getString(RECEIVED_MESSAGE);
        String senderIpAddress = receivedData.getString(SENDER_IP_ADDRESS);
        multicastMessageReceivedListener.onMulticastMessageReceived(receivedMessage, senderIpAddress);
    }
}
