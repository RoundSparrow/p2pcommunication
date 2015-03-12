package no.bouvet.p2pcommunication.multicast;

public class MulticastMessage {

    private String message;
    private String senderIpAddress;
    private boolean sentByMe;

    public MulticastMessage(String message, String senderIpAddress) {
        this.message = message;
        this.senderIpAddress = senderIpAddress;
    }

    public MulticastMessage(String message, String senderIpAddress, boolean sentByMe) {
        this.message = message;
        this.senderIpAddress = senderIpAddress;
        this.sentByMe = sentByMe;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderIpAddress() {
        return senderIpAddress;
    }

    public boolean isSentByMe() {
        return sentByMe;
    }

    public void setSentByMe(boolean sentByMe) {
        this.sentByMe = sentByMe;
    }
}
