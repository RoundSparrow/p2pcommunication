package no.bouvet.p2pcommunication.listener.wifip2paction;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.util.Log;
import android.widget.Toast;

import no.bouvet.p2pcommunication.P2PCommunicationActivity;
import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.listener.state.DiscoveryStateListener;
import no.bouvet.p2pcommunication.wifip2p.P2pCommunicationWifiP2pManager;

public class WifiP2pStopPeerDiscoveryActionListener implements ActionListener {

    private final Context context;
    private DiscoveryStateListener discoveryStateListener;

    public WifiP2pStopPeerDiscoveryActionListener(Context context, DiscoveryStateListener discoveryStateListener) {
        this.context = context;
        this.discoveryStateListener = discoveryStateListener;
    }

    @Override
    public void onSuccess() {
        discoveryStateListener.onStoppedDiscovery();
        Toast.makeText(context, context.getString(R.string.discovery_stopped), Toast.LENGTH_SHORT).show();
        Log.i(P2PCommunicationActivity.TAG, context.getString(R.string.discovery_stopped));
    }

    @Override
    public void onFailure(int reasonCode) {
        String reason = context.getString(R.string.could_not_stop_discovery) + ": ";
        reason += P2pCommunicationWifiP2pManager.getFailureReason(context, reasonCode);
        Toast.makeText(context, reason, Toast.LENGTH_SHORT).show();
        Log.w(P2PCommunicationActivity.TAG, reason);
    }
}
