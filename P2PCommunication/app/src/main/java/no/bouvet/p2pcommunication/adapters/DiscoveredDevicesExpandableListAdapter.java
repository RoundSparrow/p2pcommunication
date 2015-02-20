package no.bouvet.p2pcommunication.adapters;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import no.bouvet.p2pcommunication.R;
import no.bouvet.p2pcommunication.helpers.MulticastInformationHelper;
import no.bouvet.p2pcommunication.listeners.WifiP2pListener;
import no.bouvet.p2pcommunication.multicast.MulticastAsyncTask;

public class DiscoveredDevicesExpandableListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private List<WifiP2pDevice> deviceList;
    private WifiP2pListener wifiP2pListener;
    private WifiP2pInfo wifiP2pInfo;

    public DiscoveredDevicesExpandableListAdapter(Context context, List<WifiP2pDevice> deviceList, WifiP2pListener wifiP2pListener) {
        super();
        this.context = context;
        this.deviceList = deviceList;
        this.wifiP2pListener = wifiP2pListener;
    }

    static class GroupViewHolder {
        protected TextView deviceNameTextView;
        protected TextView deviceStatusTextView;
    }

    static class ChildViewHolder {
        protected TextView groupOwnerQuestionTextView;
        protected TextView groupOwnerIpTextView;
        protected Button connectButton;
        protected Button disconnectButton;
    }

    @Override
    public int getGroupCount() {
        return deviceList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return deviceList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        GroupViewHolder groupViewHolder;

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.discovered_devices_list_group, null);
            groupViewHolder = createGroupViewHolderAndFindViews(convertView);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        final WifiP2pDevice wifiP2pDevice = (WifiP2pDevice) getGroup(groupPosition);
        if (wifiP2pDevice != null) {
            groupViewHolder.deviceNameTextView.setText(wifiP2pDevice.deviceName);
            groupViewHolder.deviceStatusTextView.setText(getDeviceStatus(wifiP2pDevice.status));
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ChildViewHolder childViewHolder;

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.discovered_devices_list_child, null);
            childViewHolder = createChildViewHolderAndFindViews(convertView);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        if (wifiP2pInfo != null) {
            setGroupOwnerInfoVisibility(childViewHolder, View.VISIBLE);
            setGroupOwnerInfoText(childViewHolder);
        } else {
            setGroupOwnerInfoVisibility(childViewHolder, View.GONE);
        }

        final WifiP2pDevice wifiP2pDevice = (WifiP2pDevice) getGroup(groupPosition);
        if (wifiP2pDevice != null) {
            setConnectOnClickListener(childViewHolder.connectButton, wifiP2pDevice);
            setDisconnectOnClickListener(childViewHolder.disconnectButton);
        }

        Button sendMulticastMessageButton = (Button) convertView.findViewById(R.id.send_multicast_button);
        sendMulticastMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MulticastAsyncTask(
                        MulticastInformationHelper.NETWORK_INTERFACE,
                        MulticastInformationHelper.MULTICAST_GROUP_ADDRESS,
                        MulticastInformationHelper.MULTICAST_PORT)
                        .execute();
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void setWifiP2pInfo(WifiP2pInfo wifiP2pInfo) {
        this.wifiP2pInfo = wifiP2pInfo;
    }

    private GroupViewHolder createGroupViewHolderAndFindViews(View convertView) {
        GroupViewHolder groupViewHolder = new GroupViewHolder();
        groupViewHolder.deviceNameTextView = (TextView) convertView.findViewById(R.id.discovered_device_name_text_view);
        groupViewHolder.deviceStatusTextView = (TextView) convertView.findViewById(R.id.discovered_device_status_text_view);
        return groupViewHolder;
    }

    private ChildViewHolder createChildViewHolderAndFindViews(View convertView) {
        ChildViewHolder childViewHolder = new ChildViewHolder();
        childViewHolder.groupOwnerQuestionTextView = (TextView) convertView.findViewById(R.id.group_owner_question_text_view);
        childViewHolder.groupOwnerIpTextView = (TextView) convertView.findViewById(R.id.group_owner_ip_text_view);
        childViewHolder.connectButton = (Button) convertView.findViewById(R.id.connect_button);
        childViewHolder.disconnectButton = (Button) convertView.findViewById(R.id.disconnect_button);
        return childViewHolder;
    }

    private void setGroupOwnerInfoText(ChildViewHolder childViewHolder) {
        childViewHolder.groupOwnerQuestionTextView.setText(context.getString(R.string.am_i_group_owner_question) + " " + ((wifiP2pInfo.isGroupOwner == true) ? context.getString(R.string.yes) : context.getString(R.string.no)));
        childViewHolder.groupOwnerIpTextView.setText(context.getString(R.string.group_owner_ip) + ": " + wifiP2pInfo.groupOwnerAddress.getHostAddress());
    }

    private void setGroupOwnerInfoVisibility(ChildViewHolder childViewHolder, int visibility) {
        childViewHolder.groupOwnerQuestionTextView.setVisibility(visibility);
        childViewHolder.groupOwnerIpTextView.setVisibility(visibility);
    }

    private void setDisconnectOnClickListener(Button disconnectButton) {
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiP2pListener.onDisconnect();
            }
        });
    }

    private void setConnectOnClickListener(Button connectButton, final WifiP2pDevice wifiP2pDevice) {
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiP2pListener.onConnect(wifiP2pDevice);
            }
        });
    }

    private String getDeviceStatus(int deviceStatus) {
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return context.getString(R.string.available);
            case WifiP2pDevice.INVITED:
                return context.getString(R.string.invited);
            case WifiP2pDevice.CONNECTED:
                return context.getString(R.string.connected);
            case WifiP2pDevice.FAILED:
                return context.getString(R.string.failed);
            case WifiP2pDevice.UNAVAILABLE:
                return context.getString(R.string.unavailable);
            default:
                return context.getString(R.string.unknown);
        }
    }

}
