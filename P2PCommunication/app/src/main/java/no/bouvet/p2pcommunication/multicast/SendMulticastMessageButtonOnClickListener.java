package no.bouvet.p2pcommunication.multicast;

import android.view.View;

import no.bouvet.p2pcommunication.listener.MulticastMessageSentListener;

public class SendMulticastMessageButtonOnClickListener implements View.OnClickListener {

    private MulticastMessageSentListener multicastMessageSentListener;
    private UserInputHandler userInputHandler;

    public SendMulticastMessageButtonOnClickListener(MulticastMessageSentListener multicastMessageSentListener, UserInputHandler userInputHandler) {
        this.multicastMessageSentListener = multicastMessageSentListener;
        this.userInputHandler = userInputHandler;
    }

    @Override
    public void onClick(View v) {
        new SendMulticastMessageAsyncTask(multicastMessageSentListener, userInputHandler).execute();
    }
}
