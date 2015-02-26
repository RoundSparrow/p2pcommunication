package no.bouvet.p2pcommunication.listener;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import no.bouvet.p2pcommunication.P2PCommunicationActivity;
import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.wifip2p.P2pCommunicationWifiP2pManager;

public class WifiP2pDiscoverActionListener implements ActionListener {

    private final Context context;

    public WifiP2pDiscoverActionListener(Context context) {
        this.context = context;
    }

    @Override
    public void onSuccess() {
        if (context instanceof P2PCommunicationActivity) {
            ((P2PCommunicationActivity) context).setSearchLayoutVisibility(View.GONE);
        }
        Toast.makeText(context, context.getString(R.string.discovery_initiated), Toast.LENGTH_SHORT).show();
        Log.i(P2PCommunicationActivity.TAG, context.getString(R.string.discovery_initiated));
    }

    @Override
    public void onFailure(int reasonCode) {
        String reason = context.getString(R.string.discovery_failed) + ": ";
        reason += P2pCommunicationWifiP2pManager.getFailureReason(context, reasonCode);
        Toast.makeText(context, reason, Toast.LENGTH_SHORT).show();
        Log.w(P2PCommunicationActivity.TAG, reason);
    }

}
