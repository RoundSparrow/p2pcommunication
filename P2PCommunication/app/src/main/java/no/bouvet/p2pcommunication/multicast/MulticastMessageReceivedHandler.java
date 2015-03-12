package no.bouvet.p2pcommunication.multicast;

import android.os.Handler;
import android.os.Message;

import no.bouvet.p2pcommunication.listener.multicast.MulticastMessageReceivedListener;
import no.bouvet.p2pcommunication.util.NetworkUtil;

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
        MulticastMessage multicastMessage = createMulticastMessage(receivedMessage, senderIpAddress);
        multicastMessageReceivedListener.onMulticastMessageReceived(multicastMessage);
    }

    private String getSenderIpAddress(Message message) {
        return message.getData().getString(SENDER_IP_ADDRESS);
    }

    private String getReceivedMessage(Message message) {
        return message.getData().getString(RECEIVED_MESSAGE);
    }

    private MulticastMessage createMulticastMessage(String receivedMessage, String senderIpAddress) {
        MulticastMessage multicastMessage = new MulticastMessage(receivedMessage, senderIpAddress);
        if (senderIpAddress.equals(NetworkUtil.getMyWifiP2pIpAddress())) {
            multicastMessage.setSentByMe(true);
        }
        return multicastMessage;
    }
}
