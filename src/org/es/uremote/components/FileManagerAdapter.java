package org.es.uremote.components;

import static org.es.network.ExchangeProtos.DirContent.File.FileType.DIRECTORY;
import static org.es.network.ExchangeProtos.DirContent.File.FileType.FILE;

import org.es.network.ExchangeProtos.DirContent;
import org.es.uremote.R;
import org.es.uremote.utils.FileUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter used to display an explorer view.
 * 
 * @author Cyril Leroux
 *
 */
public class FileManagerAdapter extends BaseAdapter {
	private final DirContent mDirContent;
	private final LayoutInflater mInflater;

	/**
	 * Default constructor
	 * @param _context the application context.
	 * @param _dirContent the files to display
	 */
	public FileManagerAdapter(Context _context,  DirContent _dirContent) {
		mInflater = LayoutInflater.from(_context);
		mDirContent = _dirContent;
	}

	@Override
	public int getCount() {
		if (mDirContent == null) {
			return 0;
		}
		return mDirContent.getFileCount();
	}

	@Override
	public Object getItem(int _position) {
		if (mDirContent == null) {
			return null;
		}
		return mDirContent.getFile(_position);
	}

	@Override
	public long getItemId(int _position) { return _position; }

	/**
	 * The view holder is the template for the items of the list.
	 * @author Cyril Leroux
	 *
	 */
	public static class ViewHolder {
		ImageView ivIcon;
		TextView tvName;
		TextView tvSize;
	}

	@Override
	public View getView(int _position, View _convertView, ViewGroup _parent) {
		ViewHolder holder;
		if (_convertView == null) {
			_convertView = mInflater.inflate(R.layout.filemanager_item, null);
			holder = new ViewHolder();
			holder.ivIcon	= (ImageView) _convertView.findViewById(R.id.ivIcon);
			holder.tvName	= (TextView) _convertView.findViewById(R.id.tvName);
			holder.tvSize	= (TextView) _convertView.findViewById(R.id.tvSize);
			_convertView.setTag(holder);
		} else {
			holder = (ViewHolder) _convertView.getTag();
		}

		final DirContent.File file = mDirContent.getFile(_position);

		int iconRes = R.drawable.filemanager_blank;

		if (DIRECTORY.equals(file.getType())) {
			iconRes = R.drawable.filemanager_folder;
		} else if (FILE.equals(file.getType()) && FileUtils.isAVideo(file.getName())) {
			iconRes = R.drawable.filemanager_video;
		}

		holder.ivIcon.setImageResource(iconRes);
		holder.tvName.setText(file.getName());
		holder.tvSize.setText(String.valueOf(file.getSize()));

		return _convertView;
	}
}
