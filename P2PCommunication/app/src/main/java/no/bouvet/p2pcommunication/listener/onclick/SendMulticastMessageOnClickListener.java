package no.bouvet.p2pcommunication.listener.onclick;

import android.view.View;

import no.bouvet.p2pcommunication.listener.multicast.MulticastMessageSentListener;
import no.bouvet.p2pcommunication.multicast.SendMulticastMessageAsyncTask;
import no.bouvet.p2pcommunication.util.UserInputHandler;

public class SendMulticastMessageOnClickListener implements View.OnClickListener {

    private MulticastMessageSentListener multicastMessageSentListener;
    private UserInputHandler userInputHandler;

    public SendMulticastMessageOnClickListener(MulticastMessageSentListener multicastMessageSentListener, UserInputHandler userInputHandler) {
        this.multicastMessageSentListener = multicastMessageSentListener;
        this.userInputHandler = userInputHandler;
    }

    @Override
    public void onClick(View v) {
        new SendMulticastMessageAsyncTask(multicastMessageSentListener, userInputHandler).execute();
    }
}
