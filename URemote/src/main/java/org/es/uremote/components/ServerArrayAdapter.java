package org.es.uremote.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.es.uremote.R;
import org.es.uremote.device.ServerSetting;
import org.es.uremote.graphics.ConnectedDeviceDrawable;

import java.util.List;

/**
 * Adapter used to display server list.
 *
 * @author Cyril Leroux
 * Created on 22/05/13.
 */
public class ServerArrayAdapter extends ArrayAdapter<ServerSetting> {

	private final LayoutInflater mInflater;

	/**
	 * Default constructor
	 *
	 * @param context The application context.
	 * @param servers The list of {@link org.es.uremote.device.ServerSetting} to display.
	 */
	public ServerArrayAdapter(Context context, List<ServerSetting> servers) {
		super(context, 0, servers);
		mInflater = LayoutInflater.from(context);
	}

	/** Template for the list items. */
	public static class ViewHolder {
		ImageView ivThumbnail;
		TextView tvName;
		TextView tvLocalhost;
		TextView tvRemoteHost;
		TextView tvMacAddress;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.server_item, null);
			holder = new ViewHolder();
			holder.ivThumbnail	= (ImageView) convertView.findViewById(R.id.thumbnail);
			holder.tvName		= (TextView) convertView.findViewById(R.id.server_name);
			holder.tvLocalhost	= (TextView) convertView.findViewById(R.id.local_host);
			holder.tvRemoteHost	= (TextView) convertView.findViewById(R.id.remote_host);
			holder.tvMacAddress	= (TextView) convertView.findViewById(R.id.mac_address);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ServerSetting server = getItem(position);

		holder.ivThumbnail.setImageDrawable(new ConnectedDeviceDrawable(server));
		holder.tvName.setText(server.getName());
		holder.tvLocalhost.setText(server.toStringLocal());
		holder.tvRemoteHost.setText(server.toStringRemote());
		holder.tvMacAddress.setText(server.getMacAddress());

		return convertView;
	}
}
