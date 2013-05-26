package org.es.uremote.components;

import java.util.List;

import org.es.uremote.objects.ServerInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Adapter used to display server list.
 * @author Cyril Leroux
 */
public class ServerAdapter extends BaseAdapter {
	private List<ServerInfo> mServers = null;
	private final LayoutInflater mInflater;

	/**
	 * Default constructor
	 * @param context
	 * @param servers
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

	//	public static class ViewHolder {
	//		ImageView ivThumbnail;
	//		TextView tvTitle;
	//		TextView tvSubject;
	//		TextView tvMessage;
	//	}
	//
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//		ViewHolder holder;
		//		if (_convertView == null) {
		//			_convertView = mInflater.inflate(R.layout.card_item, null);
		//			holder = new ViewHolder();
		//			holder.ivThumbnail	= (ImageView) _convertView.findViewById(R.id.ivThumbnail);
		//			holder.tvTitle		= (TextView) _convertView.findViewById(R.id.tvTitle);
		//			holder.tvSubject	= (TextView) _convertView.findViewById(R.id.tvSubject);
		//			holder.tvMessage	= (TextView) _convertView.findViewById(R.id.tvMessage);
		//			_convertView.setTag(holder);
		//		} else {
		//			holder = (ViewHolder) _convertView.getTag();
		//		}
		//
		//		final Card card = mCards.get(_position);
		//		final Bitmap bmp = card.getThumbBitmap();
		//		if (bmp != null && !bmp.isRecycled()) {
		//			holder.ivThumbnail.setImageBitmap(bmp);
		//		} else {
		//			holder.ivThumbnail.setVisibility(View.GONE);
		//		}
		//
		//		holder.tvTitle.setText(card.getTitle());
		//		holder.tvSubject.setText(card.getSubject());
		//		holder.tvMessage.setText(card.getMessage());
		//		return _convertView;
		return null;
	}
}
