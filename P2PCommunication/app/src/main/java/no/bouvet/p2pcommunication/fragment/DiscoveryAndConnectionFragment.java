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

import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.adapter.DiscoveryListAdapter;
import no.bouvet.p2pcommunication.listener.WifiP2pListener;
import no.bouvet.p2pcommunication.listener.multicast.MulticastListener;
import no.bouvet.p2pcommunication.listener.onclick.WifiP2pCancelInvitationOnClickListener;
import no.bouvet.p2pcommunication.listener.onclick.WifiP2pDisconnectOnClickListener;
import no.bouvet.p2pcommunication.listener.onclick.WifiP2pMultiConnectOnClickListener;
import no.bouvet.p2pcommunication.listener.onclick.WifiP2pStartDiscoveryOnClickListener;
import no.bouvet.p2pcommunication.listener.onclick.WifiP2pStopDiscoveryOnClickListener;
import no.bouvet.p2pcommunication.listener.state.ConnectionStateListener;
import no.bouvet.p2pcommunication.listener.state.DiscoveryStateListener;

public class DiscoveryAndConnectionFragment extends ListFragment implements DiscoveryStateListener, PeerListListener, ConnectionStateListener, ConnectionInfoListener {

    public static final String TAG = DiscoveryAndConnectionFragment.class.getSimpleName();
    private View discoveryAndConnectionFragmentView;
    private DiscoveryListAdapter discoveryListAdapter;
    private boolean activityCreated;

    public static Fragment newInstance() {
        DiscoveryAndConnectionFragment discoveryAndConnectionFragment = new DiscoveryAndConnectionFragment();
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

        discoveryListAdapter = new DiscoveryListAdapter(getActivity(), R.layout.discovery_and_connection_list_row);
        setListAdapter(discoveryListAdapter);
        updateButton(R.id.left_bottom_button, getString(R.string.discover), new WifiP2pStartDiscoveryOnClickListener(((WifiP2pListener) getActivity())));
        updateButton(R.id.right_bottom_button, getString(R.string.create_group), new WifiP2pMultiConnectOnClickListener(((WifiP2pListener) getActivity())));
        activityCreated = true;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        WifiP2pDevice wifiP2pDevice = discoveryListAdapter.getItem(position);
        ((WifiP2pListener) getActivity()).onConnect(wifiP2pDevice);
    }

    @Override
    public void onStartedDiscovery() {
        clearDiscoveryList();
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
        discoveryListAdapter.clear();
        discoveryListAdapter.addAll(peerList.getDeviceList());
        discoveryListAdapter.notifyDataSetChanged();
        updateTextViewVisibility(R.id.no_devices_found_text_view, View.GONE);
        if (discoveryListAdapter.isEmpty()) {
            updateTextViewVisibility(R.id.no_devices_found_text_view, View.VISIBLE);
        }
    }

    @Override
    public void onSentInvitationToConnect() {
        updateButton(R.id.right_bottom_button, getString(R.string.cancel_invitation), new WifiP2pCancelInvitationOnClickListener(((WifiP2pListener) getActivity())));
    }

    @Override
    public void onIsDisconnected() {
        if (activityCreated) {
            updateGroupHostInfo(null);
            updateButton(R.id.right_bottom_button, getString(R.string.create_group), new WifiP2pMultiConnectOnClickListener(((WifiP2pListener) getActivity())));
            ((MulticastListener) getActivity()).onStopReceivingMulticastMessages();
            Log.i(TAG, getString(R.string.data_has_been_reset));
        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        ((MulticastListener) getActivity()).onStartReceivingMulticastMessages();
        ((WifiP2pListener) getActivity()).onStopPeerDiscovery();
        updateGroupHostInfo(wifiP2pInfo);
        updateButton(R.id.right_bottom_button, getString(R.string.disconnect), new WifiP2pDisconnectOnClickListener(((WifiP2pListener) getActivity())));
    }

    private void clearDiscoveryList() {
        discoveryListAdapter.clear();
        discoveryListAdapter.notifyDataSetChanged();
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
