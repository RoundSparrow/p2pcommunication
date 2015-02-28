package no.bouvet.p2pcommunication.listener.multicast;

public interface MulticastMessageReceivedListener {

    public void onMulticastMessageReceived(String receivedMessage, String senderIpAddress);
}
