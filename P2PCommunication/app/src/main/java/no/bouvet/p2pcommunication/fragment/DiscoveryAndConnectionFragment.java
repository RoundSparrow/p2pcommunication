package no.bouvet.p2pcommunication.fragment;


import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import no.bouvet.p2pcommunication.P2PCommunicationActivity;
import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.adapter.DiscoveredDevicesListAdapter;
import no.bouvet.p2pcommunication.listener.WifiP2pListener;
import no.bouvet.p2pcommunication.listener.multicast.MulticastListener;
import no.bouvet.p2pcommunication.listener.onclick.WifiP2pDisconnectOnClickListener;
import no.bouvet.p2pcommunication.listener.onclick.WifiP2pMultiConnectOnClickListener;
import no.bouvet.p2pcommunication.listener.onclick.WifiP2pStartDiscoveryOnClickListener;
import no.bouvet.p2pcommunication.listener.onclick.WifiP2pStopDiscoveryOnClickListener;
import no.bouvet.p2pcommunication.listener.state.DiscoveryStateListener;

public class DiscoveryAndConnectionFragment extends ListFragment implements DiscoveryStateListener, PeerListListener, ConnectionInfoListener {

    private static final String TAG = DiscoveryAndConnectionFragment.class.getSimpleName();
    private static DiscoveryAndConnectionFragment discoveryAndConnectionFragment;
    private View discoveryAndConnectionFragmentView;
    private List<WifiP2pDevice> discoveredWifiP2pDevices;

    public static Fragment getInstance() {
        if (discoveryAndConnectionFragment == null) {
            discoveryAndConnectionFragment = new DiscoveryAndConnectionFragment();
        }
        return discoveryAndConnectionFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        discoveryAndConnectionFragmentView = inflater.inflate(R.layout.discovery_and_connection_fragment, null);
        return discoveryAndConnectionFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        discoveredWifiP2pDevices = new ArrayList<>();
        setListAdapter(new DiscoveredDevicesListAdapter(getActivity(), R.layout.discovery_and_connection_list_row, discoveredWifiP2pDevices));
        updateButton(R.id.left_bottom_button, getString(R.string.discover), new WifiP2pStartDiscoveryOnClickListener(((WifiP2pListener) getActivity())));
        updateButton(R.id.right_bottom_button, getString(R.string.multi_connect), new WifiP2pMultiConnectOnClickListener(((WifiP2pListener) getActivity())));
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        WifiP2pDevice wifiP2pDevice = (WifiP2pDevice) getListAdapter().getItem(position);
        ((WifiP2pListener) getActivity()).onConnect(wifiP2pDevice);
    }

    @Override
    public void onStartedDiscovery() {
        ((DiscoveredDevicesListAdapter) getListAdapter()).clear();
        updateSearchLayoutVisibility(View.VISIBLE);
        updateButton(R.id.left_bottom_button, getString(R.string.stop), new WifiP2pStopDiscoveryOnClickListener(((WifiP2pListener) getActivity())));
    }

    @Override
    public void onStoppedDiscovery() {
        updateSearchLayoutVisibility(View.GONE);
        updateButton(R.id.left_bottom_button, getString(R.string.discover), new WifiP2pStartDiscoveryOnClickListener(((WifiP2pListener) getActivity())));
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        discoveredWifiP2pDevices.clear();
        discoveredWifiP2pDevices.addAll(peerList.getDeviceList());
        ((DiscoveredDevicesListAdapter) getListAdapter()).notifyDataSetChanged();
        updateTextViewVisibility(R.id.no_devices_found_text_view, View.GONE);
        if (discoveredWifiP2pDevices.size() == 0) {
            updateTextViewVisibility(R.id.no_devices_found_text_view, View.VISIBLE);
            Log.i(P2PCommunicationActivity.TAG, getString(R.string.no_devices_found));
        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        ((MulticastListener) getActivity()).onStartReceivingMulticastMessages();
        ((WifiP2pListener) getActivity()).onStopPeerDiscovery();
        updateGroupHostInfo(wifiP2pInfo);
        updateButton(R.id.right_bottom_button, getString(R.string.disconnect), new WifiP2pDisconnectOnClickListener(((WifiP2pListener) getActivity())));
    }

    public void resetData() {
        if (discoveredWifiP2pDevices != null) {
            discoveredWifiP2pDevices.clear();
            ((DiscoveredDevicesListAdapter) getListAdapter()).notifyDataSetChanged();
            ((MulticastListener) getActivity()).onStopReceivingMulticastMessages();
            updateGroupHostInfo(null);
            updateButton(R.id.right_bottom_button, getString(R.string.multi_connect), new WifiP2pMultiConnectOnClickListener(((WifiP2pListener) getActivity())));
            Log.i(TAG, getString(R.string.discovered_devices_list_cleared));
        }

    }

    private void updateGroupHostInfo(WifiP2pInfo wifiP2pInfo) {
        if (wifiP2pInfo != null && wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
            updateHeaderTextView(R.id.am_i_host_question_text_view, getResources().getString(R.string.am_i_host_question) + " " + getResources().getString(R.string.yes));
            updateHeaderTextView(R.id.host_ip_text_view, getString(R.string.ip_capital_letters) + ": " + wifiP2pInfo.groupOwnerAddress.getHostAddress());
        } else if (wifiP2pInfo != null && wifiP2pInfo.groupFormed) {
            updateHeaderTextView(R.id.am_i_host_question_text_view, getResources().getString(R.string.am_i_host_question) + " " + getResources().getString(R.string.no));
            updateHeaderTextView(R.id.host_ip_text_view, "");
        } else {
            updateHeaderTextView(R.id.am_i_host_question_text_view, "");
            updateHeaderTextView(R.id.host_ip_text_view, "");
        }
    }

    private void updateButton(int resourceId, String text, OnClickListener onClickListener) {
        Button button = (Button) discoveryAndConnectionFragmentView.findViewById(resourceId);
        button.setText(text);
        button.setOnClickListener(onClickListener);
    }

    private void updateHeaderTextView(int resourceId, String text) {
        TextView textView = (TextView) getActivity().findViewById(resourceId);
        textView.setText(text);
    }

    private void updateTextViewVisibility(int resourceId, int visibility) {
        TextView textView = (TextView) discoveryAndConnectionFragmentView.findViewById(resourceId);
        textView.setVisibility(visibility);
    }

    private void updateSearchLayoutVisibility(int visibility) {
        LinearLayout searchLayout = (LinearLayout) discoveryAndConnectionFragmentView.findViewById(R.id.search_layout);
        searchLayout.setVisibility(visibility);
    }


}
