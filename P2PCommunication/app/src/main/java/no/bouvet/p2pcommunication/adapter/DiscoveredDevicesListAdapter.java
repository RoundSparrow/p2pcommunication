package no.bouvet.p2pcommunication.adapter;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import no.bouvet.p2pcommunication.R;

public class DiscoveredDevicesListAdapter extends ArrayAdapter<WifiP2pDevice> {

    private Context context;
    private List<WifiP2pDevice> deviceList;

    public DiscoveredDevicesListAdapter(Context context, int resource, List<WifiP2pDevice> deviceList) {
        super(context, resource, deviceList);
        this.context = context;
        this.deviceList = deviceList;
    }

    static class ViewHolder {
        protected TextView deviceNameTextView;
        protected TextView deviceStatusTextView;
        protected CheckBox checkBox;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.discovery_and_connection_list_row, null);
            viewHolder = createViewHolderAndFindViews(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final WifiP2pDevice wifiP2pDevice = deviceList.get(position);
        viewHolder.deviceNameTextView.setText(wifiP2pDevice.deviceName);
        viewHolder.deviceStatusTextView.setText(getDeviceStatus(wifiP2pDevice.status));

        return convertView;
    }

    private ViewHolder createViewHolderAndFindViews(View convertView) {
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.deviceNameTextView = (TextView) convertView.findViewById(R.id.discovered_device_name_text_view);
        viewHolder.deviceStatusTextView = (TextView) convertView.findViewById(R.id.discovered_device_status_text_view);
        viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
        return viewHolder;
    }

    private String getDeviceStatus(int deviceStatus) {
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return context.getString(R.string.tap_to_connect);
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
