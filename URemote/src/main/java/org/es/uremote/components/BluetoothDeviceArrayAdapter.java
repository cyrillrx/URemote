package org.es.uremote.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.es.uremote.R;
import org.es.uremote.device.BluetoothDevice;

import java.util.List;

/**
 * Adapter used to display bluetooth device list.
 *
 * @author Cyril Leroux
 *         Created on 23/10/12.
 */
public class BluetoothDeviceArrayAdapter extends ArrayAdapter<BluetoothDevice> {

    private final LayoutInflater mInflater;

    /**
     * Adapter constructor
     *
     * @param context The application context.
     * @param devices The list of {@link org.es.uremote.device.BluetoothDevice} to display.
     */
    public BluetoothDeviceArrayAdapter(Context context, final List<BluetoothDevice> devices) {
        super(context, 0, devices);
        mInflater = LayoutInflater.from(context);
    }

    /** Device information view */
    public static class ViewHolder {
        TextView tvDeviceName;
        TextView tvDeviceAddress;
        TextView tvDeviceType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.bt_device_item, null);
            holder = new ViewHolder();
            holder.tvDeviceName = (TextView) convertView.findViewById(R.id.tvDeviceName);
            holder.tvDeviceAddress = (TextView) convertView.findViewById(R.id.tvDeviceAddress);
            holder.tvDeviceType = (TextView) convertView.findViewById(R.id.tvDeviceType);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BluetoothDevice device = getItem(position);
        holder.tvDeviceName.setText(device.getName());
        holder.tvDeviceAddress.setText(device.getAddress());
        holder.tvDeviceType.setText(device.getType());

        return convertView;
    }
}
