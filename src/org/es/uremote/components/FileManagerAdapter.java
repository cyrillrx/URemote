package org.es.uremote.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.es.uremote.exchange.ExchangeMessages.DirContent;
import org.es.uremote.R;
import org.es.utils.FileUtils;

import static org.es.uremote.exchange.ExchangeMessages.DirContent.File.FileType.DIRECTORY;
import static org.es.uremote.exchange.ExchangeMessages.DirContent.File.FileType.FILE;

/**
 * Adapter used to display an explorer view.
 *
 * @author Cyril Leroux
 * Created before first commit (08/04/12).
 */
public class FileManagerAdapter extends BaseAdapter {
	private DirContent mDirContent;
	private final LayoutInflater mInflater;

	/**
	 * Default constructor
	 *
	 * @param context the application context.
	 * @param dirContent the files to display
	 */
	public FileManagerAdapter(Context context, DirContent dirContent) {
		mInflater = LayoutInflater.from(context);
		mDirContent = dirContent;
	}

	public void setDirContent(DirContent dirContent) {
		mDirContent = dirContent;
	}

	@Override
	public int getCount() {
		if (mDirContent == null) {
			return 0;
		}
		return mDirContent.getFileCount();
	}

	@Override
	public Object getItem(int position) {
		if (mDirContent == null) {
			return null;
		}
		return mDirContent.getFile(position);
	}

	@Override
	public long getItemId(int position) { return position; }

	/**
	 * The view holder is the template for the items of the list.
	 *
	 * @author Cyril Leroux
	 */
	public static class ViewHolder {
		ImageView ivIcon;
		TextView tvName;
		TextView tvSize;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.filemanager_item, null);
			holder = new ViewHolder();
			holder.ivIcon	= (ImageView) convertView.findViewById(R.id.ivIcon);
			holder.tvName	= (TextView) convertView.findViewById(R.id.tvName);
			holder.tvSize	= (TextView) convertView.findViewById(R.id.tvSize);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final DirContent.File file = mDirContent.getFile(position);

		int iconRes = R.drawable.filemanager_blank;

		if (DIRECTORY.equals(file.getType())) {
			iconRes = R.drawable.filemanager_folder;
		} else if (FILE.equals(file.getType()) && FileUtils.isAVideo(file.getName())) {
			iconRes = R.drawable.filemanager_video;
		}

		holder.ivIcon.setImageResource(iconRes);
		holder.tvName.setText(file.getName());
		holder.tvSize.setText(String.valueOf(file.getSize()));

		return convertView;
	}
}
