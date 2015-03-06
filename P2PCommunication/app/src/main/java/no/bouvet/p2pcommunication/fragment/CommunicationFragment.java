package no.bouvet.p2pcommunication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.listener.multicast.MulticastMessageReceivedListener;
import no.bouvet.p2pcommunication.listener.multicast.MulticastMessageSentListener;
import no.bouvet.p2pcommunication.listener.onclick.SendMulticastMessageOnClickListener;
import no.bouvet.p2pcommunication.multicast.MulticastMessageReceivedHandler;
import no.bouvet.p2pcommunication.multicast.MulticastMessageReceiverService;
import no.bouvet.p2pcommunication.multicast.UserInputHandler;

public class CommunicationFragment extends Fragment implements MulticastMessageReceivedListener, MulticastMessageSentListener, UserInputHandler {

    public static final String TAG = CommunicationFragment.class.getSimpleName();
    private Intent multicastReceiverServiceIntent;

    @InjectView(R.id.multicast_message_log_text_view) TextView multicastMessageLogTextView;
    @InjectView(R.id.user_input_edit_text) EditText userInputEditText;
    @InjectView(R.id.send_button) Button sendButton;

    public static Fragment newInstance() {
        return new CommunicationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View communicationFragmentView = inflater.inflate(R.layout.communication_fragment, null);
        ButterKnife.inject(this, communicationFragmentView);
        return communicationFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sendButton.setOnClickListener(new SendMulticastMessageOnClickListener(this, this));
    }

    public void startReceivingMulticastMessages() {
        if (!MulticastMessageReceiverService.isRunning) {
            multicastReceiverServiceIntent = createMulticastReceiverServiceIntent();
            getActivity().startService(multicastReceiverServiceIntent);
            Log.i(TAG, getString(R.string.multicast_receiver_service_started));
        }
    }

    public void stopReceivingMulticastMessages() {
        if (multicastReceiverServiceIntent != null) {
            getActivity().stopService(multicastReceiverServiceIntent);
            Log.i(TAG, getString(R.string.multicast_receiver_service_stopped));
        }
    }

    @Override
    public void onMulticastMessageReceived(String receivedMessage, String senderIpAddress) {
        multicastMessageLogTextView.setText(multicastMessageLogTextView.getText() + senderIpAddress + ": " + receivedMessage + "\n");
    }

    @Override
    public void onMessageFailedToBeMulticasted() {
        multicastMessageLogTextView.setText(multicastMessageLogTextView.getText() + getString(R.string.message_not_multicasted) + "\n");
    }

    @Override
    public String getMessageToBeSentFromUserInput() {
        return userInputEditText.getText().toString();
    }

    @Override
    public void clearUserInput() {
        userInputEditText.setText("");
    }

    public void resetData() {
        multicastMessageLogTextView.setText("");
        stopReceivingMulticastMessages();
    }

    private Intent createMulticastReceiverServiceIntent() {
        Intent multicastReceiverServiceIntent = new Intent(getActivity(), MulticastMessageReceiverService.class);
        multicastReceiverServiceIntent.setAction(MulticastMessageReceiverService.ACTION_LISTEN_FOR_MULTICAST);
        MulticastMessageReceivedHandler multicastMessageReceivedHandler = new MulticastMessageReceivedHandler(this);
        multicastReceiverServiceIntent.putExtra(MulticastMessageReceiverService.EXTRA_MESSENGER, new Messenger(multicastMessageReceivedHandler));
        return multicastReceiverServiceIntent;
    }
}
