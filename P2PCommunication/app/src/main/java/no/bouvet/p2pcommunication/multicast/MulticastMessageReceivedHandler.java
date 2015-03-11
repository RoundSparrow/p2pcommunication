package no.bouvet.p2pcommunication.multicast;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import no.bouvet.p2pcommunication.listener.multicast.MulticastMessageReceivedListener;

public class MulticastMessageReceivedHandler extends Handler {

    public static final String RECEIVED_MESSAGE = "RECEIVED_MESSAGE";
    public static final String SENDER_IP_ADDRESS = "SENDER_IP_ADDRESS";
    private MulticastMessageReceivedListener multicastMessageReceivedListener;

    public MulticastMessageReceivedHandler(MulticastMessageReceivedListener multicastMessageReceivedListener) {
        this.multicastMessageReceivedListener = multicastMessageReceivedListener;
    }

    @Override
    public void handleMessage(Message message) {
        String receivedMessage = getReceivedMessage(message);
        String senderIpAddress = getSenderIpAddress(message);
        multicastMessageReceivedListener.onMulticastMessageReceived(receivedMessage, senderIpAddress);
    }

    private String getSenderIpAddress(Message message) {
        return message.getData().getString(SENDER_IP_ADDRESS);
    }

    private String getReceivedMessage(Message message) {
        return message.getData().getString(RECEIVED_MESSAGE);
    }
}
