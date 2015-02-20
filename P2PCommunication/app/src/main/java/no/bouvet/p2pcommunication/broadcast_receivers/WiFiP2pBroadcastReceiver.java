/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.bouvet.p2pcommunication.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import no.bouvet.p2pcommunication.P2PCommunicationActivity;
import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.listeners.WifiP2pListener;

public class WiFiP2pBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pListener wifiP2pListener;
    private final Context context;

    public WiFiP2pBroadcastReceiver(Context context, WifiP2pListener wifiP2pListener) {
        this.context = context;
        this.wifiP2pListener = wifiP2pListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)) {
            checkIfWifiP2pIsEnabledOrDisabled(intent);

        } else if (action.equals(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)) {
            requestPeers();

        } else if (action.equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)) {
            NetworkInfo networkInfo = getNetworkInfo(intent);
            checkIfConnectedOrDisconnectedFromAWifiP2pNetwork(networkInfo);

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            updateThisDevice(intent);
        }
    }

    private void checkIfWifiP2pIsEnabledOrDisabled(Intent intent) {
        int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            wifiP2pListener.onWifiP2pStateEnabled();
            Log.i(P2PCommunicationActivity.TAG, context.getString(R.string.p2p_enabled) + " (" + state + ")");
        } else {
            wifiP2pListener.onWifiP2pStateDisabled();
            Log.i(P2PCommunicationActivity.TAG, context.getString(R.string.p2p_disabled) + " (" + state + ")");
        }
    }

    private void requestPeers() {
        wifiP2pListener.onRequestPeers();
        Log.i(P2PCommunicationActivity.TAG, context.getString(R.string.number_of_available_p2p_peers_changed));
    }

    private NetworkInfo getNetworkInfo(Intent intent) {
        return intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
    }

    private void checkIfConnectedOrDisconnectedFromAWifiP2pNetwork(NetworkInfo networkInfo) {
        if (networkInfo.isConnected()) {
            wifiP2pListener.onRequestConnectionInfo();
            Log.i(P2PCommunicationActivity.TAG, context.getString(R.string.connected_to_p2p_network));
        } else {
            wifiP2pListener.onClearDiscoveredDevices();
            Log.i(P2PCommunicationActivity.TAG, context.getString(R.string.disconnected_from_p2p_network));
        }
    }

    private void updateThisDevice(Intent intent) {
        WifiP2pDevice wifiP2pDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
        wifiP2pListener.onThisDeviceChanged(wifiP2pDevice);
        Log.i(P2PCommunicationActivity.TAG, context.getString(R.string.details_about_this_device_updated));
    }




}
