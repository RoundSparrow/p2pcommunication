package no.bouvet.p2pcommunication.listener;

import android.net.wifi.p2p.WifiP2pDevice;

public interface WifiP2pListener {

    void onWifiP2pStateEnabled();

    void onWifiP2pStateDisabled();

    void onStartPeerDiscovery();

    void onStopPeerDiscovery();

    void onRequestPeers();

    void onClearDiscoveredPeers();

    void onConnect(WifiP2pDevice wifiP2pDevice);

    void onDisconnect();

    void onMultiConnect();

    void onRequestConnectionInfo();

    void onThisDeviceChanged(WifiP2pDevice wifiP2pDevice);
}
