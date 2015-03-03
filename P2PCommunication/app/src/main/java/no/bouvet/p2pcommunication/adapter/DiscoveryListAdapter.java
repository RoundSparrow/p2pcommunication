package no.bouvet.p2pcommunication.adapter;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import no.bouvet.p2pcommunication.R;

public class DiscoveryListAdapter extends ArrayAdapter<WifiP2pDevice> {

    private Context context;
    private List<WifiP2pDevice> deviceList;

    public DiscoveryListAdapter(Context context, int resource, List<WifiP2pDevice> deviceList) {
        super(context, resource, deviceList);
        this.context = context;
        this.deviceList = deviceList;
    }

    static class ViewHolder {
        @InjectView(R.id.discovered_device_name_text_view) protected TextView deviceNameTextView;
        @InjectView(R.id.discovered_device_status_text_view) protected TextView deviceStatusTextView;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.discovery_and_connection_list_row, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final WifiP2pDevice wifiP2pDevice = deviceList.get(position);
        viewHolder.deviceNameTextView.setText(wifiP2pDevice.deviceName);
        viewHolder.deviceStatusTextView.setText(getDeviceStatus(wifiP2pDevice.status));

        return convertView;
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
