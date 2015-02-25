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
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import no.bouvet.p2pcommunication.adapter.P2pCommunicationFragmentPagerAdapter;
import no.bouvet.p2pcommunication.broadcastreceiver.WiFiP2pBroadcastReceiver;
import no.bouvet.p2pcommunication.fragment.CommunicationFragment;
import no.bouvet.p2pcommunication.fragment.DiscoveryAndConnectionFragment;
import no.bouvet.p2pcommunication.listener.ViewPagerOnPageChangeListener;
import no.bouvet.p2pcommunication.listener.WifiP2pBroadcastReceiverListener;
import no.bouvet.p2pcommunication.wifip2p.P2pCommunicationWifiP2pManager;

public class P2PCommunicationActivity extends FragmentActivity implements WifiP2pBroadcastReceiverListener {

    public static final String TAG = "P2PCommunicationActivity";
    private P2pCommunicationWifiP2pManager p2pCommunicationWifiP2pManager;
    private WiFiP2pBroadcastReceiver wiFiP2pBroadcastReceiver;
    private P2pCommunicationFragmentPagerAdapter p2pCommunicationFragmentPagerAdapter;
    private boolean wifiP2pEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        createAndAcquireMulticastLock();
        p2pCommunicationWifiP2pManager = new P2pCommunicationWifiP2pManager(getApplicationContext());
        p2pCommunicationFragmentPagerAdapter = new P2pCommunicationFragmentPagerAdapter(getSupportFragmentManager());
        createViewPagerAndSetOnPageChangeListenerAndSetAdapter(p2pCommunicationFragmentPagerAdapter);
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
            case R.id.enable_p2p_button:
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                return true;
            case R.id.discover_peers_button:
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
        p2pCommunicationWifiP2pManager.onDisconnect();
    }

    @Override
    public void onConnect(WifiP2pDevice wifiP2pDevice) {
        p2pCommunicationWifiP2pManager.onConnect(wifiP2pDevice);
    }

    @Override
    public void onRequestPeers() {
       PeerListListener peerListListener = getDiscoveryAndConnectionFragment();
       p2pCommunicationWifiP2pManager.requestPeers(peerListListener);
    }

    @Override
    public void onThisDeviceChanged(WifiP2pDevice wifiP2pDevice) {
        updateMyDeviceTextViews(wifiP2pDevice);
    }

    @Override
    public void onRequestConnectionInfo() {
        ConnectionInfoListener connectionInfoListener = getCommunicationFragment();
        p2pCommunicationWifiP2pManager.requestConnectionInfo(connectionInfoListener);
    }

    @Override
    public void onClearDiscoveredDevices() {
        getDiscoveryAndConnectionFragment().clearDiscoveredDevices();
        getCommunicationFragment().stopMulticastReceiverService();
    }

    private void createAndAcquireMulticastLock() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        if (wifiManager != null) {
            WifiManager.MulticastLock multicastLock = wifiManager.createMulticastLock(TAG);
            multicastLock.acquire();
        }
    }

    private void createViewPagerAndSetOnPageChangeListenerAndSetAdapter(P2pCommunicationFragmentPagerAdapter p2pCommunicationFragmentPagerAdapter) {
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOnPageChangeListener(new ViewPagerOnPageChangeListener(viewPager));
        viewPager.setAdapter(p2pCommunicationFragmentPagerAdapter);
    }

    private void createAndRegisterWifiP2pBroadcastReceiver() {
        wiFiP2pBroadcastReceiver = new WiFiP2pBroadcastReceiver(getApplicationContext(), this);
        registerReceiver(wiFiP2pBroadcastReceiver, createWifiP2pIntentFilter());
    }

    private IntentFilter createWifiP2pIntentFilter() {
        IntentFilter wifiP2pIntentFilter = new IntentFilter();
        wifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        return wifiP2pIntentFilter;
    }

    private void discoverDevices() {
        p2pCommunicationWifiP2pManager.discoverPeers();
    }

    private void updateMyDeviceTextViews(WifiP2pDevice wifiP2pDevice) {
        TextView myDeviceNameTextView = (TextView) findViewById(R.id.my_device_name_text_view);
        myDeviceNameTextView.setText(wifiP2pDevice.deviceName);
        TextView myDeviceStatusTextView = (TextView) findViewById(R.id.my_device_status_text_view);
        myDeviceStatusTextView.setText(getDeviceStatus(wifiP2pDevice.status));
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

    private DiscoveryAndConnectionFragment getDiscoveryAndConnectionFragment() {
        return (DiscoveryAndConnectionFragment) p2pCommunicationFragmentPagerAdapter.getItem(0);
    }

    private CommunicationFragment getCommunicationFragment() {
        return (CommunicationFragment) p2pCommunicationFragmentPagerAdapter.getItem(1);
    }

}
