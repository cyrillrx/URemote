package org.es.uremote.components;

import java.util.List;

import org.es.uremote.R;
import org.es.uremote.objects.ServerInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter used to display server list.
 * @author Cyril Leroux
 */
public class ServerAdapter extends BaseAdapter {
	private List<ServerInfo> mServers = null;
	private final LayoutInflater mInflater;

	/**
	 * Default constructor
	 * @param context The application context.
	 * @param servers The list of {@link ServerInfo} to display.
	 */
	public ServerAdapter(Context context, List<ServerInfo> servers) {
		mInflater = LayoutInflater.from(context);
		mServers = servers;
	}

	@Override
	public int getCount() {
		if (mServers == null) {
			return 0;
		}

		return mServers.size();
	}

	@Override
	public Object getItem(int position) {
		if (mServers == null) {
			return null;
		}

		return mServers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * @author Cyril Leroux
	 *
	 */
	public static class ViewHolder {
		ImageView ivThumbnail;
		TextView tvName;
		TextView tvLocalhost;
		TextView tvRemoteHost;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.server_item, null);
			holder = new ViewHolder();
			//					holder.ivThumbnail	= (ImageView) convertView.findViewById(R.id.ivThumbnail);
			holder.tvName		= (TextView) convertView.findViewById(R.id.server_name);
			holder.tvLocalhost	= (TextView) convertView.findViewById(R.id.local_host);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ServerInfo server = mServers.get(position);
		//				final Bitmap bmp = card.getThumbBitmap();
		//				if (bmp != null && !bmp.isRecycled()) {
		//					holder.ivThumbnail.setImageBitmap(bmp);
		//				} else {
		//					holder.ivThumbnail.setVisibility(View.GONE);
		//				}

		holder.tvName.setText(server.getName());
		holder.tvLocalhost.setText(server.getFullLocal());
		return convertView;
	}
}
