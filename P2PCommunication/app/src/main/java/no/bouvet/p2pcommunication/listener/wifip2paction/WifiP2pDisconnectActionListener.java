package no.bouvet.p2pcommunication.listener.wifip2paction;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.wifip2p.P2pCommunicationWifiP2pManager;

public class WifiP2pDisconnectActionListener implements WifiP2pManager.ActionListener {

    private static final String TAG = WifiP2pDisconnectActionListener.class.getSimpleName();
    private final Context context;

    public WifiP2pDisconnectActionListener(Context context) {
        this.context = context;
    }


    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailure(int reasonCode) {
        String reason = context.getString(R.string.disconnect_failed) + ": ";
        reason += P2pCommunicationWifiP2pManager.getFailureReason(context, reasonCode);
        Toast.makeText(context, reason, Toast.LENGTH_SHORT).show();
        Log.w(TAG, reason);
    }

}
