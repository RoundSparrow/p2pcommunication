package no.bouvet.p2pcommunication.wifip2p;

import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;

import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.listener.state.DiscoveryStateListener;
import no.bouvet.p2pcommunication.listener.wifip2paction.WifiP2pConnectActionListener;
import no.bouvet.p2pcommunication.listener.wifip2paction.WifiP2pCreateGroupActionListener;
import no.bouvet.p2pcommunication.listener.wifip2paction.WifiP2pDisconnectActionListener;
import no.bouvet.p2pcommunication.listener.wifip2paction.WifiP2pStartPeerDiscoveryActionListener;
import no.bouvet.p2pcommunication.listener.wifip2paction.WifiP2pStopPeerDiscoveryActionListener;

public class P2pCommunicationWifiP2pManager {

    private Context context;
    private WifiP2pManager wifiP2pManager;
    private Channel wifiP2pChannel;

    public P2pCommunicationWifiP2pManager(Context context) {
        this.context = context;
        this.wifiP2pManager = getWifiP2pManager();
        this.wifiP2pChannel = createWifiP2pChannel();
    }

    public void startPeerDiscovery(DiscoveryStateListener discoveryStateListener) {
        wifiP2pManager.discoverPeers(wifiP2pChannel, new WifiP2pStartPeerDiscoveryActionListener(context, discoveryStateListener));
    }

    public void stopPeerDiscovery(DiscoveryStateListener discoveryStateListener) {
        wifiP2pManager.stopPeerDiscovery(wifiP2pChannel, new WifiP2pStopPeerDiscoveryActionListener(context, discoveryStateListener));
    }

    public void requestPeers(WifiP2pManager.PeerListListener peerListListener) {
        wifiP2pManager.requestPeers(wifiP2pChannel, peerListListener);
    }

    public void connectToWifiP2pDevice(WifiP2pDevice wifiP2pDevice) {
        wifiP2pManager.connect(wifiP2pChannel, createWifiP2pConfig(wifiP2pDevice), new WifiP2pConnectActionListener(context));
    }

    public void disconnectFromWifiP2pNetwork() {
        wifiP2pManager.removeGroup(wifiP2pChannel, new WifiP2pDisconnectActionListener(context));
    }

    public void createGroup() {
        wifiP2pManager.createGroup(wifiP2pChannel, new WifiP2pCreateGroupActionListener(context));
    }

    public void requestConnectionInfo(ConnectionInfoListener connectionInfoListener) {
        wifiP2pManager.requestConnectionInfo(wifiP2pChannel, connectionInfoListener);
    }

    public static String getFailureReason(Context context, int reasonCode) {
        switch (reasonCode) {
            case WifiP2pManager.BUSY:
                return context.getString(R.string.busy);
            case WifiP2pManager.ERROR:
                return context.getString(R.string.internal_error);
            case WifiP2pManager.P2P_UNSUPPORTED:
                return context.getString(R.string.p2p_unsupported);
            default:
                return context.getString(R.string.unknown_error);
        }
    }

    private WifiP2pManager getWifiP2pManager() {
        return (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
    }

    private Channel createWifiP2pChannel() {
        return wifiP2pManager.initialize(context, context.getMainLooper(), null);
    }

    private WifiP2pConfig createWifiP2pConfig(WifiP2pDevice wifiP2pDevice) {
        WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();
        wifiP2pConfig.deviceAddress = wifiP2pDevice.deviceAddress;
        wifiP2pConfig.wps.setup = WpsInfo.PBC;
        return wifiP2pConfig;
    }
}
