package no.bouvet.p2pcommunication.listener.state;

public interface ConnectionStateListener {

    void onInvitationToConnectSent();

    void onIsDisconnected();
}
