package no.bouvet.p2pcommunication.multicast;

import android.view.View;

import no.bouvet.p2pcommunication.listener.MulticastMessageSentListener;

public class SendMulticastMessageButtonOnClickListener implements View.OnClickListener {

    private MulticastMessageSentListener multicastMessageSentListener;

    public SendMulticastMessageButtonOnClickListener(MulticastMessageSentListener multicastMessageSentListener) {
        this.multicastMessageSentListener = multicastMessageSentListener;
    }

    @Override
    public void onClick(View v) {
        new SendMulticastMessageAsyncTask(multicastMessageSentListener).execute();
        multicastMessageSentListener.clearInputEditText();
    }
}
