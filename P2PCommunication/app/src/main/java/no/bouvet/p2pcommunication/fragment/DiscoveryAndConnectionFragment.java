package no.bouvet.p2pcommunication.fragment;


import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import no.bouvet.p2pcommunication.P2PCommunicationActivity;
import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.adapter.DiscoveredDevicesListAdapter;
import no.bouvet.p2pcommunication.listener.WifiP2pBroadcastReceiverListener;

public class DiscoveryAndConnectionFragment extends ListFragment implements PeerListListener {

    public static final String TAG = "DiscoveryAndConnectionFragment";
    private static DiscoveryAndConnectionFragment discoveryAndConnectionFragment;
    private View discoveredDevicesFragmentView;
    private List<WifiP2pDevice> discoveredDevices;
    private DiscoveredDevicesListAdapter discoveredDevicesListAdapter;

    public static Fragment getInstance() {
        if (discoveryAndConnectionFragment == null) {
            discoveryAndConnectionFragment = new DiscoveryAndConnectionFragment();
        }
        return discoveryAndConnectionFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        discoveredDevicesFragmentView = inflater.inflate(R.layout.discovery_and_connection_fragment, null);
        return discoveredDevicesFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        discoveredDevices = new ArrayList<>();
        createAndSetListAdapter();
        setDisconnectButtonOnClickListener();
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        WifiP2pDevice wifiP2pDevice = (WifiP2pDevice) getListAdapter().getItem(position);
        ((WifiP2pBroadcastReceiverListener) getActivity()).onConnect(wifiP2pDevice);
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        discoveredDevices.clear();
        discoveredDevices.addAll(peerList.getDeviceList());
        discoveredDevicesListAdapter.notifyDataSetChanged();
        setNoDevicesFoundTextViewVisibility(View.GONE);
        if (discoveredDevices.size() == 0) {
            setNoDevicesFoundTextViewVisibility(View.VISIBLE);
            Log.i(DiscoveryAndConnectionFragment.TAG, getString(R.string.no_devices_found));
        }
    }

    public void clearDiscoveredDevices() {
        if (discoveredDevices != null) {
            discoveredDevices.clear();
            discoveredDevicesListAdapter.notifyDataSetChanged();
            Log.i(DiscoveryAndConnectionFragment.TAG, getString(R.string.discovered_devices_list_cleared));
        }
    }

    private void setDisconnectButtonOnClickListener() {
        Button disconnectButton = (Button) discoveredDevicesFragmentView.findViewById(R.id.disconnect_button);
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((WifiP2pBroadcastReceiverListener) getActivity()).onDisconnect();
            }
        });
    }

    private void createAndSetListAdapter() {
        discoveredDevicesListAdapter = new DiscoveredDevicesListAdapter(getActivity().getApplicationContext(), R.layout.discovery_and_connection_list_row, discoveredDevices);
        setListAdapter(discoveredDevicesListAdapter);
    }

    private void setNoDevicesFoundTextViewVisibility(int visibilityValue) {
        TextView textView = (TextView) discoveredDevicesFragmentView.findViewById(R.id.no_devices_found_text_view);
        textView.setVisibility(visibilityValue);
    }

}
