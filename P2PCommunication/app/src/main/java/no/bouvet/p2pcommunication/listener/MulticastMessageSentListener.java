package no.bouvet.p2pcommunication.listener;

public interface MulticastMessageSentListener {

    public void onMessageFailedToBeMulticasted();

    public String getMulticastMessageFromInputEditText();

    public void clearInputEditText();
}
