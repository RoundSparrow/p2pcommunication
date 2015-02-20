package no.bouvet.p2pcommunication.listeners;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import no.bouvet.p2pcommunication.P2PCommunicationActivity;
import no.bouvet.p2pcommunication.R;

public class WifiP2pDisconnectActionListener implements WifiP2pManager.ActionListener {

    private final Context context;

    public WifiP2pDisconnectActionListener(Context context) {
        this.context = context;
    }


    @Override
    public void onSuccess() {
        Toast.makeText(context, context.getString(R.string.disconnected), Toast.LENGTH_SHORT).show();
        Log.i(P2PCommunicationActivity.TAG, context.getString(R.string.disconnected));
    }

    @Override
    public void onFailure(int reason) {
        Toast.makeText(context, context.getString(R.string.disconnect_failed), Toast.LENGTH_SHORT).show();
        Log.i(P2PCommunicationActivity.TAG, context.getString(R.string.disconnect_failed));
    }

}
