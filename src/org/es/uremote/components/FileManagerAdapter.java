package org.es.uremote.components;

import java.util.List;

import org.es.uremote.R;
import org.es.uremote.objects.FileManagerEntity;

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
	private final List<FileManagerEntity> mFileList;
	private final LayoutInflater mInflater;

	/**
	 * Default constructor
	 * @param _context the application context.
	 * @param _fileList the files to display
	 */
	public FileManagerAdapter(Context _context, List<FileManagerEntity> _fileList) {
		mInflater = LayoutInflater.from(_context);
		mFileList = _fileList;
	}

	@Override
	public int getCount() { return mFileList.size();	}

	@Override
	public Object getItem(int _position) { return mFileList.get(_position); }

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

		final FileManagerEntity file = mFileList.get(_position);

		int iconRes = R.drawable.filemanager_blank;

		if (file.isDirectory()) {
			iconRes = R.drawable.filemanager_folder;
		} else if (file.isVideo()) {
			iconRes = R.drawable.filemanager_video;
		}

		holder.ivIcon.setImageResource(iconRes);
		holder.tvName.setText(file.getName());
		holder.tvSize.setText(file.getSize());

		return _convertView;
	}
}
