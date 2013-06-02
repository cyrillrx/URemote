package org.es.uremote.components;

import java.util.List;

import org.es.uremote.R;
import org.es.uremote.objects.BTDevice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Adapter used to display bluetooth device list.
 * @author Cyril Leroux
 */
public class BTDeviceListAdapter extends BaseAdapter {

	private final LayoutInflater mInflater;
	private final List<BTDevice> mBtDevices;

	/**
	 * Adapter constructor
	 * @param context The application context.
	 * @param devices The list of {@link BTDevice} to display.
	 */
	public BTDeviceListAdapter(Context context, final List<BTDevice> devices) {
		mInflater = LayoutInflater.from(context);
		mBtDevices = devices;
	}

	@Override
	public int getCount() {
		if (mBtDevices == null) {
			return 0;
		}
		return mBtDevices.size();
	}

	@Override
	public Object getItem(int position) {
		if (mBtDevices == null) {
			return null;
		}
		return mBtDevices.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
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
			holder.tvDeviceName		= (TextView) convertView.findViewById(R.id.tvDeviceName);
			holder.tvDeviceAddress	= (TextView) convertView.findViewById(R.id.tvDeviceAddress);
			holder.tvDeviceType		= (TextView) convertView.findViewById(R.id.tvDeviceType);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		BTDevice device = mBtDevices.get(position);
		holder.tvDeviceName.setText(device.getName());
		holder.tvDeviceAddress.setText(device.getAddress());
		holder.tvDeviceType.setText(device.getType());

		return convertView;
	}
}
