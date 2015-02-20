package no.bouvet.p2pcommunication.listeners;

import android.net.wifi.p2p.WifiP2pDevice;

public interface WifiP2pListener {
    void onWifiP2pStateEnabled();

    void onWifiP2pStateDisabled();

    void onDisconnect();

    void onConnect(WifiP2pDevice wifiP2pDevice);

    void onRequestPeers();

    void onThisDeviceChanged(WifiP2pDevice wifiP2pDevice);

    void onRequestConnectionInfo();

    void onClearDiscoveredDevices();
}
