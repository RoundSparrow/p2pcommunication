package no.bouvet.p2pcommunication.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import no.bouvet.p2pcommunication.P2PCommunicationActivity;
import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.adapters.DiscoveredDevicesExpandableListAdapter;
import no.bouvet.p2pcommunication.helpers.MulticastInformationHelper;
import no.bouvet.p2pcommunication.listeners.WifiP2pListener;
import no.bouvet.p2pcommunication.multicast.MulticastListenerIntentService;

public class DiscoveredDevicesListFragment extends Fragment implements PeerListListener, ConnectionInfoListener {

    private View discoveredDevicesFragmentView;
    private List<WifiP2pDevice> discoveredDevices;
    private DiscoveredDevicesExpandableListAdapter discoveredDevicesExpandableListAdapter;
    private ExpandableListView discoveredDevicesExpandableListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        discoveredDevicesFragmentView = inflater.inflate(R.layout.discovered_devices_list_fragment, null);
        return discoveredDevicesFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        discoveredDevices = new ArrayList<>();
        createExpandableListViewAndSetAdapter();
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        discoveredDevices.clear();
        discoveredDevices.addAll(peerList.getDeviceList());
        discoveredDevicesExpandableListAdapter.notifyDataSetChanged();
        setNoDevicesFoundTextViewVisibility(View.GONE);
        if (discoveredDevices.size() == 0) {
            setNoDevicesFoundTextViewVisibility(View.VISIBLE);
            Log.i(P2PCommunicationActivity.TAG, getString(R.string.no_devices_found));
        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {

        discoveredDevicesExpandableListAdapter.setWifiP2pInfo(wifiP2pInfo);
        discoveredDevicesExpandableListAdapter.notifyDataSetChanged();

        Intent serviceIntent = new Intent(getActivity(), MulticastListenerIntentService.class);
        serviceIntent.setAction(MulticastListenerIntentService.ACTION_LISTEN_FOR_MULTICAST);
        serviceIntent.putExtra(MulticastListenerIntentService.EXTRA_NETWORK_INTERFACE, MulticastInformationHelper.NETWORK_INTERFACE);
        serviceIntent.putExtra(MulticastListenerIntentService.EXTRA_MULTICAST_GROUP_ADDRESS, MulticastInformationHelper.MULTICAST_GROUP_ADDRESS);
        serviceIntent.putExtra(MulticastListenerIntentService.EXTRA_MULTICAST_PORT, MulticastInformationHelper.MULTICAST_PORT);
        getActivity().startService(serviceIntent);

    }

    public void clearDiscoveredDevices() {
        discoveredDevices.clear();
        discoveredDevicesExpandableListAdapter.setWifiP2pInfo(null);
        discoveredDevicesExpandableListAdapter.notifyDataSetChanged();
        Log.i(P2PCommunicationActivity.TAG, getString(R.string.discovered_devices_list_cleared));
    }

    private void createExpandableListViewAndSetAdapter() {
        discoveredDevicesExpandableListView = (ExpandableListView) discoveredDevicesFragmentView.findViewById(android.R.id.list);
        discoveredDevicesExpandableListAdapter = new DiscoveredDevicesExpandableListAdapter(getActivity().getApplicationContext(), discoveredDevices, (WifiP2pListener) getActivity());
        discoveredDevicesExpandableListView.setAdapter(discoveredDevicesExpandableListAdapter);
    }

    private void setNoDevicesFoundTextViewVisibility(int visibilityValue) {
        TextView textView = (TextView) getActivity().findViewById(R.id.no_devices_found_text_view);
        textView.setVisibility(visibilityValue);
    }

}
