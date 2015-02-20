package no.bouvet.p2pcommunication.listeners;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import no.bouvet.p2pcommunication.P2PCommunicationActivity;
import no.bouvet.p2pcommunication.R;

public class WifiP2pDiscoverActionListener implements WifiP2pManager.ActionListener {

    private final Context context;

    public WifiP2pDiscoverActionListener(Context context) {
        this.context = context;
    }

    @Override
    public void onSuccess() {
        Toast.makeText(context, context.getString(R.string.discovery_initiated), Toast.LENGTH_SHORT).show();
        Log.i(P2PCommunicationActivity.TAG, context.getString(R.string.discovery_initiated));
    }

    @Override
    public void onFailure(int reasonCode) {
        String reason = context.getString(R.string.discovery_failed) + ": ";
        switch (reasonCode) {
            case WifiP2pManager.BUSY:
                reason += context.getString(R.string.busy);;
                break;
            case WifiP2pManager.ERROR:
                reason += context.getString(R.string.internal_error);
                break;
            case WifiP2pManager.P2P_UNSUPPORTED:
                reason += context.getString(R.string.p2p_unsupported);
                break;
            default:
                reason += context.getString(R.string.unknown_error);
                break;
        }
        Toast.makeText(context, reason, Toast.LENGTH_SHORT).show();
        Log.w(P2PCommunicationActivity.TAG, reason);
    }

}
