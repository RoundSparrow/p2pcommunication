package no.bouvet.p2pcommunication.listener;

public interface MulticastMessageReceivedListener {

    public void onMulticastMessageReceived(String receivedMessage, String senderIpAddress);
}
