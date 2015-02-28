package no.bouvet.p2pcommunication.listener.wifip2paction;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import no.bouvet.p2pcommunication.P2PCommunicationActivity;
import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.listener.state.ConnectionStateListener;
import no.bouvet.p2pcommunication.wifip2p.P2pCommunicationWifiP2pManager;

public class WifiP2pDisconnectActionListener implements WifiP2pManager.ActionListener {

    private final Context context;
    private ConnectionStateListener connectionStateListener;

    public WifiP2pDisconnectActionListener(Context context, ConnectionStateListener connectionStateListener) {
        this.context = context;
        this.connectionStateListener = connectionStateListener;
    }


    @Override
    public void onSuccess() {
        connectionStateListener.onDisconnected();
        Toast.makeText(context, context.getString(R.string.disconnected), Toast.LENGTH_SHORT).show();
        Log.i(P2PCommunicationActivity.TAG, context.getString(R.string.disconnected));
    }

    @Override
    public void onFailure(int reasonCode) {
        String reason = context.getString(R.string.disconnect_failed) + ": ";
        reason += P2pCommunicationWifiP2pManager.getFailureReason(context, reasonCode);
        Toast.makeText(context, reason, Toast.LENGTH_SHORT).show();
        Log.w(P2PCommunicationActivity.TAG, reason);
    }

}
