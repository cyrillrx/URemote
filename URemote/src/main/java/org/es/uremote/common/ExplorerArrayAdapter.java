package org.es.uremote.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.es.uremote.R;
import org.es.uremote.exchange.ExchangeMessages.DirContent;
import org.es.utils.FileUtils;

import java.io.File;
import java.util.List;

import static org.es.uremote.exchange.ExchangeMessages.DirContent.File.FileType.DIRECTORY;
import static org.es.uremote.exchange.ExchangeMessages.DirContent.File.FileType.FILE;

/**
 * Adapter used to display an explorer view.
 *
 * @author Cyril Leroux
 * Created on 03/09/13.
 */
public class ExplorerArrayAdapter extends ArrayAdapter<File> {

	private LayoutInflater mInflater;

	/**
	 * Default constructor
	 *
	 * @param context the application context.
	 * @param entries the files to display
	 */
	public ExplorerArrayAdapter(Context context, List<File> entries) {
		super(context, 0, entries);
		mInflater	= LayoutInflater.from(context);
	}

	/** Template for the list items. */
	public static class ViewHolder {
		ImageView ivIcon;
		TextView tvName;
		TextView tvSize;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.explorer_item, null);
			holder = new ViewHolder();
			holder.ivIcon	= (ImageView) convertView.findViewById(R.id.ivIcon);
			holder.tvName	= (TextView) convertView.findViewById(R.id.tvName);
			holder.tvSize	= (TextView) convertView.findViewById(R.id.tvSize);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final File file = getItem(position);

		int iconRes = R.drawable.filemanager_blank;

		if (file.isDirectory()) {
			iconRes = R.drawable.filemanager_folder;
		} else if (FileUtils.isAVideo(file.getName())) {
			iconRes = R.drawable.filemanager_video;
		}

		holder.ivIcon.setImageResource(iconRes);
		holder.tvName.setText(file.getName());
		holder.tvSize.setText(String.valueOf(file.getTotalSpace()));

		return convertView;
	}
}
