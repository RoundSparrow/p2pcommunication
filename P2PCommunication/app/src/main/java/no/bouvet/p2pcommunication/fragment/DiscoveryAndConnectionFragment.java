package no.bouvet.p2pcommunication.fragment;


import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
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
import android.widget.Toast;

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
import no.bouvet.p2pcommunication.listener.state.ConnectionStateListener;
import no.bouvet.p2pcommunication.listener.state.DiscoveryStateListener;

public class DiscoveryAndConnectionFragment extends ListFragment implements DiscoveryStateListener, PeerListListener, ConnectionStateListener, GroupInfoListener, ConnectionInfoListener {

    private static DiscoveryAndConnectionFragment discoveryAndConnectionFragment;
    private View discoveryAndConnectionFragmentView;
    private List<WifiP2pDevice> discoveredWifiP2pDevices;
    private DiscoveredDevicesListAdapter discoveredWifiP2pDevicesListAdapter;

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
        discoveredWifiP2pDevicesListAdapter = new DiscoveredDevicesListAdapter(getActivity().getApplicationContext(), R.layout.discovery_and_connection_list_row, discoveredWifiP2pDevices);
        setListAdapter(discoveredWifiP2pDevicesListAdapter);

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
        updateButton(R.id.left_bottom_button, getString(R.string.stop), new WifiP2pStopDiscoveryOnClickListener(((WifiP2pListener) getActivity())));
        updateSearchLayoutVisibility(View.VISIBLE);
    }

    @Override
    public void onStoppedDiscovery() {
        updateButton(R.id.left_bottom_button, getString(R.string.discover), new WifiP2pStartDiscoveryOnClickListener(((WifiP2pListener) getActivity())));
        updateSearchLayoutVisibility(View.GONE);
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        discoveredWifiP2pDevices.clear();
        discoveredWifiP2pDevices.addAll(peerList.getDeviceList());
        discoveredWifiP2pDevicesListAdapter.notifyDataSetChanged();
        updateTextViewVisibility(R.id.no_devices_found_text_view, View.GONE);
        if (discoveredWifiP2pDevices.size() == 0) {
            updateTextViewVisibility(R.id.no_devices_found_text_view, View.VISIBLE);
            Log.i(P2PCommunicationActivity.TAG, getString(R.string.no_devices_found));
        }
    }

    @Override
    public void onConnected() {
        ((WifiP2pListener) getActivity()).onStopPeerDiscovery();
        updateButton(R.id.right_bottom_button, getString(R.string.disconnect), new WifiP2pDisconnectOnClickListener(((WifiP2pListener) getActivity())));
        ((MulticastListener) getActivity()).onStartReceivingMulticastMessages();
    }

    @Override
    public void onDisconnected() {
        updateButton(R.id.right_bottom_button, getString(R.string.multi_connect), new WifiP2pMultiConnectOnClickListener(((WifiP2pListener) getActivity())));
        ((MulticastListener) getActivity()).onStopReceivingMulticastMessages();
        TextView groupOwnerQuestionTextView = (TextView) getActivity().findViewById(R.id.am_i_group_owner_question_text_view);
        TextView groupOwnerIpTextView = (TextView) getActivity().findViewById(R.id.group_owner_ip_text_view);
        groupOwnerQuestionTextView.setText("");
        groupOwnerIpTextView.setText("");
    }

    @Override
    public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
        if (wifiP2pGroup.isGroupOwner()) {
            for (WifiP2pDevice wifiP2pDevice : discoveredWifiP2pDevices) {
                ((WifiP2pListener) getActivity()).onConnect(wifiP2pDevice);
            }
        } else {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.this_device_is_not_group_owner), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        updateGroupHostInfo(wifiP2pInfo);
        onConnected();
    }

    public void clearDiscoveredWifiP2pDevices() {
        if (discoveredWifiP2pDevices != null) {
            discoveredWifiP2pDevices.clear();
            discoveredWifiP2pDevicesListAdapter.notifyDataSetChanged();
            Log.i(P2PCommunicationActivity.TAG, getString(R.string.discovered_devices_list_cleared));
        }
    }

    private void updateGroupHostInfo(WifiP2pInfo wifiP2pInfo) {
        TextView groupOwnerQuestionTextView = (TextView) getActivity().findViewById(R.id.am_i_group_owner_question_text_view);
        TextView groupOwnerIpTextView = (TextView) getActivity().findViewById(R.id.group_owner_ip_text_view);
        groupOwnerQuestionTextView.setText(getResources().getString(R.string.am_i_host_question));
        if (wifiP2pInfo.isGroupOwner) {
            groupOwnerQuestionTextView.setText(groupOwnerQuestionTextView.getText() + " " + getResources().getString(R.string.yes));
            groupOwnerIpTextView.setText("IP: " + wifiP2pInfo.groupOwnerAddress.getHostAddress());
        } else {
            groupOwnerQuestionTextView.setText(groupOwnerQuestionTextView.getText() + " " + getResources().getString(R.string.no));
            groupOwnerIpTextView.setText("");
        }
    }

    private void updateButton(int resourceId, String text, OnClickListener onClickListener) {
        Button button = (Button) discoveryAndConnectionFragmentView.findViewById(resourceId);
        button.setText(text);
        button.setOnClickListener(onClickListener);
    }

    private void updateTextView(int resourceId, String text) {
        TextView textView = (TextView) discoveryAndConnectionFragmentView.findViewById(resourceId);
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
