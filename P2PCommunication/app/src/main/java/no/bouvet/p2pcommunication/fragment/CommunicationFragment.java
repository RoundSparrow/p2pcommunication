package no.bouvet.p2pcommunication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.listener.multicast.MulticastListener;
import no.bouvet.p2pcommunication.listener.multicast.MulticastMessageReceivedListener;
import no.bouvet.p2pcommunication.listener.multicast.MulticastMessageSentListener;
import no.bouvet.p2pcommunication.multicast.MulticastMessageReceivedHandler;
import no.bouvet.p2pcommunication.multicast.MulticastMessageReceiverService;
import no.bouvet.p2pcommunication.listener.onclick.SendMulticastMessageOnClickListener;
import no.bouvet.p2pcommunication.multicast.UserInputHandler;

public class CommunicationFragment extends Fragment implements MulticastListener, MulticastMessageReceivedListener, MulticastMessageSentListener, UserInputHandler {

    public static final String TAG = CommunicationFragment.class.getSimpleName();
    private View communicationFragmentView;
    private TextView multicastMessageLogTextView;
    private EditText userInputEditText;
    private Intent multicastReceiverServiceIntent;

    public static Fragment newInstance() {
        CommunicationFragment communicationFragment = new CommunicationFragment();
        return communicationFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        communicationFragmentView = inflater.inflate(R.layout.communication_fragment, null);
        return communicationFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        multicastMessageLogTextView = (TextView) communicationFragmentView.findViewById(R.id.multicast_message_log_text_view);
        userInputEditText = (EditText) communicationFragmentView.findViewById(R.id.user_input_edit_text);
        Button sendMulticastMessageButton = (Button) communicationFragmentView.findViewById(R.id.send_multicast_message_button);
        sendMulticastMessageButton.setOnClickListener(new SendMulticastMessageOnClickListener(this, this));
    }

    @Override
    public void onStartReceivingMulticastMessages() {
        if (!MulticastMessageReceiverService.running) {
            multicastReceiverServiceIntent = createMulticastReceiverServiceIntent();
            getActivity().startService(multicastReceiverServiceIntent);
        }
    }

    @Override
    public void onStopReceivingMulticastMessages() {
        if (multicastReceiverServiceIntent != null) {
            getActivity().stopService(multicastReceiverServiceIntent);
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
    public String getMulticastMessageToBeSentFromUserInput() {
        return userInputEditText.getText().toString();
    }

    @Override
    public void clearUserInput() {
        userInputEditText.setText("");
    }

    private Intent createMulticastReceiverServiceIntent() {
        Intent multicastReceiverServiceIntent = new Intent(getActivity(), MulticastMessageReceiverService.class);
        multicastReceiverServiceIntent.setAction(MulticastMessageReceiverService.ACTION_LISTEN_FOR_MULTICAST);
        MulticastMessageReceivedHandler multicastMessageReceivedHandler = new MulticastMessageReceivedHandler(this);
        multicastReceiverServiceIntent.putExtra(MulticastMessageReceiverService.EXTRA_MESSENGER, new Messenger(multicastMessageReceivedHandler));
        return multicastReceiverServiceIntent;
    }
}
