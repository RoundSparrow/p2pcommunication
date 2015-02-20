package no.bouvet.p2pcommunication;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import no.bouvet.p2pcommunication.broadcast_receivers.WiFiP2pBroadcastReceiver;
import no.bouvet.p2pcommunication.fragments.DiscoveredDevicesListFragment;
import no.bouvet.p2pcommunication.listeners.WifiP2pConnectActionListener;
import no.bouvet.p2pcommunication.listeners.WifiP2pDisconnectActionListener;
import no.bouvet.p2pcommunication.listeners.WifiP2pDiscoverActionListener;
import no.bouvet.p2pcommunication.listeners.WifiP2pListener;

public class P2PCommunicationActivity extends ActionBarActivity implements WifiP2pListener {

    public static final String TAG = "P2PCommunicationActivity";
    private IntentFilter wifiP2pIntentFilter;
    private WifiP2pManager wifiP2pManager;
    private Channel wifiP2pChannel;
    private WiFiP2pBroadcastReceiver wiFiP2pBroadcastReceiver;
    private boolean wifiP2pEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        WifiManager wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
        if(wifiManager != null){
            WifiManager.MulticastLock multicastLock = wifiManager.createMulticastLock(TAG);
            multicastLock.acquire();
        }
        wifiP2pIntentFilter = createWifiP2pIntentFilter();
        wifiP2pManager = getWifiP2pManager();
        wifiP2pChannel = createWifiP2pChannel();
    }

    @Override
    public void onResume() {
        super.onResume();
        createAndRegisterWifiP2pBroadcastReceiver();
    }


    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(wiFiP2pBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.enable_button:
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                return true;
            case R.id.discover_button:
                if (wifiP2pEnabled) {
                    discoverDevices();
                } else {
                    Toast.makeText(this, R.string.p2p_disabled_please_activate, Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onWifiP2pStateEnabled() {
        wifiP2pEnabled = true;
    }

    @Override
    public void onWifiP2pStateDisabled() {
        wifiP2pEnabled = false;
    }

    @Override
    public void onDisconnect() {
        disconnectFromWifiP2pNetwork();
        onClearDiscoveredDevices();
    }

    @Override
    public void onConnect(WifiP2pDevice wifiP2pDevice) {
        connectToWifiP2pDevice(wifiP2pDevice);
    }

    @Override
    public void onRequestPeers() {
        requestPeers();
    }


    @Override
    public void onThisDeviceChanged(WifiP2pDevice wifiP2pDevice) {
        updateThisDevice(wifiP2pDevice);
    }

    @Override
    public void onRequestConnectionInfo() {
        requestConnectionInfo();
    }


    @Override
    public void onClearDiscoveredDevices() {
        getDiscoveredDevicesListFragment().clearDiscoveredDevices();
    }

    private IntentFilter createWifiP2pIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        return intentFilter;
    }

    private WifiP2pManager getWifiP2pManager() {
        return (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
    }

    private Channel createWifiP2pChannel() {
        return wifiP2pManager.initialize(this, getMainLooper(), null);
    }

    private void createAndRegisterWifiP2pBroadcastReceiver() {
        wiFiP2pBroadcastReceiver = new WiFiP2pBroadcastReceiver(getApplicationContext(), this);
        registerReceiver(wiFiP2pBroadcastReceiver, wifiP2pIntentFilter);
    }

    private void discoverDevices() {
        wifiP2pManager.discoverPeers(wifiP2pChannel, new WifiP2pDiscoverActionListener(getApplicationContext()));
        onClearDiscoveredDevices();
    }

    private void disconnectFromWifiP2pNetwork() {
        wifiP2pManager.removeGroup(wifiP2pChannel, new WifiP2pDisconnectActionListener(this));
    }

    private void connectToWifiP2pDevice(WifiP2pDevice wifiP2pDevice) {
        wifiP2pManager.connect(wifiP2pChannel, createWifiP2pConfig(wifiP2pDevice), new WifiP2pConnectActionListener(this));
    }

    private void requestPeers() {
        PeerListListener peerListListener = getDiscoveredDevicesListFragment();
        wifiP2pManager.requestPeers(wifiP2pChannel, peerListListener);
    }

    private void requestConnectionInfo() {
        ConnectionInfoListener connectionInfoListener = getDiscoveredDevicesListFragment();
        wifiP2pManager.requestConnectionInfo(wifiP2pChannel, connectionInfoListener);
    }

    private void updateThisDevice(WifiP2pDevice wifiP2pDevice) {
        TextView textView = (TextView) findViewById(R.id.my_device_name_text_view);
        textView.setText(wifiP2pDevice.deviceName);
        textView = (TextView) findViewById(R.id.my_device_status_text_view);
        textView.setText(getDeviceStatus(wifiP2pDevice.status));
    }

    private WifiP2pConfig createWifiP2pConfig(WifiP2pDevice wifiP2pDevice) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = wifiP2pDevice.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        return config;
    }

    private DiscoveredDevicesListFragment getDiscoveredDevicesListFragment() {
        return (DiscoveredDevicesListFragment) getFragmentManager().findFragmentById(R.id.discovered_devices_list_fragment);
    }


    private String getDeviceStatus(int deviceStatus) {
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return getString(R.string.available);
            case WifiP2pDevice.INVITED:
                return getString(R.string.invited);
            case WifiP2pDevice.CONNECTED:
                return getString(R.string.connected);
            case WifiP2pDevice.FAILED:
                return getString(R.string.failed);
            case WifiP2pDevice.UNAVAILABLE:
                return getString(R.string.unavailable);
            default:
                return getString(R.string.unknown);
        }
    }

}
