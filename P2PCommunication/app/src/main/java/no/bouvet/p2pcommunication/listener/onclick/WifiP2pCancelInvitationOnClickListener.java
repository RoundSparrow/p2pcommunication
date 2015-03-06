package no.bouvet.p2pcommunication.listener.onclick;

import android.view.View;

import no.bouvet.p2pcommunication.listener.wifip2p.WifiP2pListener;

public class WifiP2pCancelInvitationOnClickListener implements View.OnClickListener {

    private WifiP2pListener wifiP2pListener;

    public WifiP2pCancelInvitationOnClickListener(WifiP2pListener wifiP2pListener) {
        this.wifiP2pListener = wifiP2pListener;
    }

    @Override
    public void onClick(View v) {
        wifiP2pListener.onCancelConnect();
    }
}
