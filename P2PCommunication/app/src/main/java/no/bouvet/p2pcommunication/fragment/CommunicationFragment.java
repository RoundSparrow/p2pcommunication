package no.bouvet.p2pcommunication.fragment;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
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
import no.bouvet.p2pcommunication.listener.MulticastMessageReceivedListener;
import no.bouvet.p2pcommunication.listener.MulticastMessageSentListener;
import no.bouvet.p2pcommunication.multicast.MulticastMessageReceivedHandler;
import no.bouvet.p2pcommunication.multicast.MulticastMessageReceiverService;
import no.bouvet.p2pcommunication.multicast.SendMulticastMessageButtonOnClickListener;
import no.bouvet.p2pcommunication.multicast.UserInputHandler;

public class CommunicationFragment extends Fragment implements UserInputHandler, MulticastMessageReceivedListener, MulticastMessageSentListener, ConnectionInfoListener {

    public static final String TAG = "CommunicationFragment";
    private static CommunicationFragment communicationFragment;
    private View communicationFragmentView;
    private TextView multicastMessageLogTextView;
    private EditText multicastMessageUserInputEditText;
    private Intent multicastReceiverServiceIntent;

    public static Fragment getInstance() {
        if (communicationFragment == null) {
            communicationFragment = new CommunicationFragment();
        }
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
        findAndSetMulticastMessageLogAndMulticastMessageInputViews();
        setSendMulticastButtonOnClickListener();
    }

    @Override
    public void onMessageFailedToBeMulticasted() {
        multicastMessageLogTextView.setText(multicastMessageLogTextView.getText() + getString(R.string.message_not_multicasted) + "\n");
    }

    @Override
    public String getMulticastMessageFromUserInput() {
        return multicastMessageUserInputEditText.getText().toString();
    }

    @Override
    public void clearUserInput() {
        multicastMessageUserInputEditText.setText("");
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        setGroupHostInfo(wifiP2pInfo);
        multicastReceiverServiceIntent = createMulticastReceiverServiceIntent();
        getActivity().startService(multicastReceiverServiceIntent);
    }

    @Override
    public void onMulticastMessageReceived(String receivedMessage, String senderIpAddress) {
        multicastMessageLogTextView.setText(multicastMessageLogTextView.getText() + senderIpAddress + ": " + receivedMessage + "\n");
    }

    public void stopMulticastReceiverService() {
        if (multicastReceiverServiceIntent != null) {
            getActivity().stopService(multicastReceiverServiceIntent);
        }
    }

    private void findAndSetMulticastMessageLogAndMulticastMessageInputViews() {
        multicastMessageLogTextView = (TextView) communicationFragmentView.findViewById(R.id.multicast_message_log_text_view);
        multicastMessageUserInputEditText = (EditText) communicationFragmentView.findViewById(R.id.multicast_message_user_input_edit_text);
    }

    private void setSendMulticastButtonOnClickListener() {
        Button sendMulticastButton = (Button) communicationFragmentView.findViewById(R.id.send_multicast_button);
        sendMulticastButton.setOnClickListener(new SendMulticastMessageButtonOnClickListener(this, this));
    }

    private void setGroupHostInfo(WifiP2pInfo wifiP2pInfo) {
        TextView groupOwnerQuestionTextView = (TextView) getActivity().findViewById(R.id.am_i_group_owner_question_text_view);
        TextView groupOwnerIpTextView = (TextView) getActivity().findViewById(R.id.group_owner_ip_text_view);
        groupOwnerQuestionTextView.setText(getResources().getString(R.string.am_i_host_question));
        if (wifiP2pInfo.isGroupOwner) {
            groupOwnerQuestionTextView.setText(groupOwnerQuestionTextView.getText() + " " + getResources().getString(R.string.yes));
            groupOwnerIpTextView.setText("IP: " + wifiP2pInfo.groupOwnerAddress.getHostAddress());
        } else {
            groupOwnerQuestionTextView.setText(groupOwnerQuestionTextView.getText() + " " + getResources().getString(R.string.no));
            groupOwnerIpTextView.setText("");
        }
    }

    private Intent createMulticastReceiverServiceIntent() {
        Intent multicastReceiverServiceIntent = new Intent(getActivity(), MulticastMessageReceiverService.class);
        multicastReceiverServiceIntent.setAction(MulticastMessageReceiverService.ACTION_LISTEN_FOR_MULTICAST);
        MulticastMessageReceivedHandler multicastMessageReceivedHandler = new MulticastMessageReceivedHandler(this);
        multicastReceiverServiceIntent.putExtra(MulticastMessageReceiverService.EXTRA_MESSENGER, new Messenger(multicastMessageReceivedHandler));
        return multicastReceiverServiceIntent;
    }


}
