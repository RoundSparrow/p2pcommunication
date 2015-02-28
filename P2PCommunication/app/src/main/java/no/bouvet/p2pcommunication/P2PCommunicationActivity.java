package no.bouvet.p2pcommunication;


import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import no.bouvet.p2pcommunication.adapter.P2pCommunicationFragmentPagerAdapter;
import no.bouvet.p2pcommunication.broadcastreceiver.WifiP2pBroadcastReceiver;
import no.bouvet.p2pcommunication.listener.WifiP2pListener;
import no.bouvet.p2pcommunication.listener.multicast.MulticastListener;
import no.bouvet.p2pcommunication.listener.onpagechange.ViewPagerOnPageChangeListener;
import no.bouvet.p2pcommunication.listener.state.ConnectionStateListener;
import no.bouvet.p2pcommunication.listener.state.DiscoveryStateListener;
import no.bouvet.p2pcommunication.wifip2p.P2pCommunicationWifiP2pManager;

public class P2PCommunicationActivity extends FragmentActivity implements WifiP2pListener, MulticastListener {

    public static final String TAG = "P2PCommunicationActivity";
    private P2pCommunicationWifiP2pManager p2pCommunicationWifiP2pManager;
    private WifiP2pBroadcastReceiver wifiP2pBroadcastReceiver;
    private P2pCommunicationFragmentPagerAdapter p2pCommunicationFragmentPagerAdapter;
    private boolean wifiP2pEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        createAndAcquireMulticastLock();
        p2pCommunicationWifiP2pManager = new P2pCommunicationWifiP2pManager(getApplicationContext());
        wifiP2pBroadcastReceiver = new WifiP2pBroadcastReceiver(getApplicationContext(), this);
        p2pCommunicationFragmentPagerAdapter = new P2pCommunicationFragmentPagerAdapter(getSupportFragmentManager());
        setViewPager(R.id.view_pager, p2pCommunicationFragmentPagerAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(wifiP2pBroadcastReceiver, createWifiP2pIntentFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(wifiP2pBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.enable_p2p_menu_item:
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
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
    public void onStartPeerDiscovery() {
        if (wifiP2pEnabled) {
            DiscoveryStateListener discoveryStateListener = p2pCommunicationFragmentPagerAdapter.getDiscoveryAndConnectionFragment();
            p2pCommunicationWifiP2pManager.startPeerDiscovery(discoveryStateListener);
        } else {
            Toast.makeText(this, R.string.p2p_disabled_please_activate, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStopPeerDiscovery() {
        DiscoveryStateListener discoveryStateListener = p2pCommunicationFragmentPagerAdapter.getDiscoveryAndConnectionFragment();
        p2pCommunicationWifiP2pManager.stopPeerDiscovery(discoveryStateListener);
    }

    @Override
    public void onRequestPeers() {
        PeerListListener peerListListener = p2pCommunicationFragmentPagerAdapter.getDiscoveryAndConnectionFragment();
        p2pCommunicationWifiP2pManager.requestPeers(peerListListener);
    }

    @Override
    public void onClearDiscoveredPeers() {
        p2pCommunicationFragmentPagerAdapter.getDiscoveryAndConnectionFragment().clearDiscoveredWifiP2pDevices();
    }

    @Override
    public void onConnect(WifiP2pDevice wifiP2pDevice) {
        p2pCommunicationWifiP2pManager.connectToWifiP2pDevice(wifiP2pDevice);
    }

    @Override
    public void onDisconnect() {
        ConnectionStateListener connectionStateListener = p2pCommunicationFragmentPagerAdapter.getDiscoveryAndConnectionFragment();
        p2pCommunicationWifiP2pManager.disconnectFromWifiP2pNetwork(connectionStateListener);
    }

    @Override
    public void onMultiConnect() {
        p2pCommunicationWifiP2pManager.createGroup();
    }

    @Override
    public void onRequestConnectionInfo() {
        ConnectionInfoListener connectionInfoListener = p2pCommunicationFragmentPagerAdapter.getDiscoveryAndConnectionFragment();
        p2pCommunicationWifiP2pManager.requestConnectionInfo(connectionInfoListener);
    }

    @Override
    public void onThisDeviceChanged(WifiP2pDevice wifiP2pDevice) {
        updateMyDeviceTextViews(wifiP2pDevice);
    }

    @Override
    public void onStartReceivingMulticastMessages() {
        MulticastListener multicastListener = p2pCommunicationFragmentPagerAdapter.getCommunicationFragment();
        multicastListener.onStartReceivingMulticastMessages();
    }

    @Override
    public void onStopReceivingMulticastMessages() {
        MulticastListener multicastListener = p2pCommunicationFragmentPagerAdapter.getCommunicationFragment();
        multicastListener.onStopReceivingMulticastMessages();
    }

    private void createAndAcquireMulticastLock() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        if (wifiManager != null) {
            WifiManager.MulticastLock multicastLock = wifiManager.createMulticastLock(TAG);
            multicastLock.acquire();
        }
    }

    private void setViewPager(int resourceId, PagerAdapter adapter) {
        ViewPager viewPager = (ViewPager) findViewById(resourceId);
        viewPager.setOnPageChangeListener(new ViewPagerOnPageChangeListener(viewPager));
        viewPager.setAdapter(adapter);
    }

    private IntentFilter createWifiP2pIntentFilter() {
        IntentFilter wifiP2pIntentFilter = new IntentFilter();
        wifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        return wifiP2pIntentFilter;
    }

    private void updateMyDeviceTextViews(WifiP2pDevice wifiP2pDevice) {
        updateTextView(R.id.my_device_name_text_view, wifiP2pDevice.deviceName);
        updateTextView(R.id.my_device_status_text_view, getDeviceStatus(wifiP2pDevice.status));
    }

    private void updateTextView(int resourceId, String text) {
        TextView textView = (TextView) findViewById(resourceId);
        textView.setText(text);
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
