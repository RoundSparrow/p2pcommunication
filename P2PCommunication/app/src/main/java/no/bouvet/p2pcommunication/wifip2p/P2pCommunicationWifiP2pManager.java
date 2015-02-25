package no.bouvet.p2pcommunication.wifip2p;

import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;

import no.bouvet.p2pcommunication.listener.WifiP2pConnectActionListener;
import no.bouvet.p2pcommunication.listener.WifiP2pDisconnectActionListener;
import no.bouvet.p2pcommunication.listener.WifiP2pDiscoverActionListener;

public class P2pCommunicationWifiP2pManager {

    private Context context;
    private WifiP2pManager wifiP2pManager;
    private Channel wifiP2pChannel;

    public P2pCommunicationWifiP2pManager(Context context) {
        this.context = context;
        this.wifiP2pManager = getWifiP2pManager();
        this.wifiP2pChannel = createWifiP2pChannel();
    }

    public void onDisconnect() {
        wifiP2pManager.removeGroup(wifiP2pChannel, new WifiP2pDisconnectActionListener(context));
    }

    public void onConnect(WifiP2pDevice wifiP2pDevice) {
        wifiP2pManager.connect(wifiP2pChannel, createWifiP2pConfig(wifiP2pDevice), new WifiP2pConnectActionListener(context));
    }

    public void discoverPeers() {
        wifiP2pManager.discoverPeers(wifiP2pChannel, new WifiP2pDiscoverActionListener(context));
    }

    public void requestPeers(PeerListListener peerListListener) {
        wifiP2pManager.requestPeers(wifiP2pChannel, peerListListener);
    }

    public void requestConnectionInfo(ConnectionInfoListener connectionInfoListener) {
        wifiP2pManager.requestConnectionInfo(wifiP2pChannel, connectionInfoListener);
    }

    private WifiP2pManager getWifiP2pManager() {
        return (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
    }

    private Channel createWifiP2pChannel() {
        return wifiP2pManager.initialize(context, context.getMainLooper(), null);
    }

    private WifiP2pConfig createWifiP2pConfig(WifiP2pDevice wifiP2pDevice) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = wifiP2pDevice.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        return config;
    }
}
