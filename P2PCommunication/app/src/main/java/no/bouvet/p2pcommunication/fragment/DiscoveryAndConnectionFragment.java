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
import android.widget.ListView;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.adapter.DiscoveryListAdapter;
import no.bouvet.p2pcommunication.adapter.P2pCommunicationFragmentPagerAdapter;
import no.bouvet.p2pcommunication.listener.discovery.DiscoveryStateListener;
import no.bouvet.p2pcommunication.listener.invitation.InvitationToConnectListener;
import no.bouvet.p2pcommunication.listener.multicast.MulticastListener;
import no.bouvet.p2pcommunication.listener.onclick.WifiP2pCancelInvitationOnClickListener;
import no.bouvet.p2pcommunication.listener.onclick.WifiP2pCreateGroupOnClickListener;
import no.bouvet.p2pcommunication.listener.onclick.WifiP2pDisconnectOnClickListener;
import no.bouvet.p2pcommunication.listener.onclick.WifiP2pStartDiscoveryOnClickListener;
import no.bouvet.p2pcommunication.listener.onclick.WifiP2pStopDiscoveryOnClickListener;
import no.bouvet.p2pcommunication.listener.wifip2p.WifiP2pListener;

public class DiscoveryAndConnectionFragment extends ListFragment implements DiscoveryStateListener, PeerListListener, InvitationToConnectListener, ConnectionInfoListener {

    public static final String TAG = DiscoveryAndConnectionFragment.class.getSimpleName();
    private DiscoveryListAdapter discoveryListAdapter;
    private WifiP2pListener wifiP2pListener;
    private boolean viewsInjected;

    @InjectView(R.id.search_layout) RelativeLayout searchLayout;
    @InjectView(R.id.no_devices_found_layout) RelativeLayout noDevicesFoundLayout;
    @InjectView(R.id.left_bottom_button) Button leftBottomButton;
    @InjectView(R.id.right_bottom_button) Button rightBottomButton;

    public static Fragment newInstance() {
        DiscoveryAndConnectionFragment discoveryAndConnectionFragment = new DiscoveryAndConnectionFragment();
        Bundle fragmentArguments = new Bundle();
        fragmentArguments.putString(P2pCommunicationFragmentPagerAdapter.FRAGMENT_TITLE, "AVAILABLE DEVICES");
        discoveryAndConnectionFragment.setArguments(fragmentArguments);
        return discoveryAndConnectionFragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View discoveryAndConnectionFragmentView = layoutInflater.inflate(R.layout.discovery_and_connection_fragment, container, false);
        ButterKnife.inject(this, discoveryAndConnectionFragmentView);
        viewsInjected = true;
        return discoveryAndConnectionFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        wifiP2pListener = (WifiP2pListener) getActivity();
        discoveryListAdapter = new DiscoveryListAdapter(getActivity(), R.layout.discovery_and_connection_list_row);
        setListAdapter(discoveryListAdapter);
        updateButton(leftBottomButton, getString(R.string.discover), new WifiP2pStartDiscoveryOnClickListener(wifiP2pListener));
        updateButton(rightBottomButton, getString(R.string.create_group), new WifiP2pCreateGroupOnClickListener(wifiP2pListener));

    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        WifiP2pDevice wifiP2pDevice = discoveryListAdapter.getItem(position);
        wifiP2pListener.onConnect(wifiP2pDevice);
    }

    @Override
    public void onStartedDiscovery() {
        clearDiscoveryList();
        searchLayout.setVisibility(View.VISIBLE);
        noDevicesFoundLayout.setVisibility(View.GONE);
        updateButton(leftBottomButton, getString(R.string.stop), new WifiP2pStopDiscoveryOnClickListener(wifiP2pListener));
    }

    @Override
    public void onStoppedDiscovery() {
        searchLayout.setVisibility(View.GONE);
        updateButton(leftBottomButton, getString(R.string.discover), new WifiP2pStartDiscoveryOnClickListener(wifiP2pListener));
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        clearDiscoveryList();
        addAllDiscoveredDevicesToDiscoveryList(wifiP2pDeviceList);
        if (discoveryListAdapter.isEmpty()) {
            noDevicesFoundLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSentInvitationToConnect() {
        updateButton(rightBottomButton, getString(R.string.cancel_invitation), new WifiP2pCancelInvitationOnClickListener(wifiP2pListener));
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        wifiP2pListener.onStopPeerDiscovery();
        wifiP2pListener.onGroupHostInfoChanged(wifiP2pInfo);
        ((MulticastListener) getActivity()).onStartReceivingMulticastMessages();
        updateButton(rightBottomButton, getString(R.string.disconnect), new WifiP2pDisconnectOnClickListener(wifiP2pListener));
    }

    public void resetData() {
        if (viewsInjected) {
            wifiP2pListener.onGroupHostInfoChanged(null);
            updateButton(rightBottomButton, getString(R.string.create_group), new WifiP2pCreateGroupOnClickListener(wifiP2pListener));
            Log.i(TAG, getString(R.string.data_has_been_reset));
        }
    }

    private void updateButton(Button button, String text, OnClickListener onClickListener) {
        button.setText(text);
        button.setOnClickListener(onClickListener);
    }

    private void addAllDiscoveredDevicesToDiscoveryList(WifiP2pDeviceList wifiP2pDeviceList) {
        discoveryListAdapter.addAll(wifiP2pDeviceList.getDeviceList());
        discoveryListAdapter.notifyDataSetChanged();
    }

    private void clearDiscoveryList() {
        discoveryListAdapter.clear();
        discoveryListAdapter.notifyDataSetChanged();
    }


}
