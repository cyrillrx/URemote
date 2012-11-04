package org.es.uremote.components;

import java.util.List;

import org.es.uremote.R;
import org.es.uremote.objects.ActionItem;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter used to display an action list.
 * 
 * @author Cyril Leroux
 *
 */
public class ActionListAdapter extends BaseAdapter {
	private final List<ActionItem> mActionList;
	private final LayoutInflater mInflater;
	private final Typeface mTypeface;

	/**
	 * Default constructor
	 * @param _context The application context.
	 * @param _actions The action list.
	 * @param _typeface The type face to use.
	 */
	public ActionListAdapter(final Context _context, final List<ActionItem> _actions, Typeface _typeface) {
		mInflater	= LayoutInflater.from(_context);
		mActionList = _actions;
		mTypeface	= _typeface;
	}

	@Override
	public int getCount() {
		if (mActionList == null) {
			return 0;
		}
		return mActionList.size();
	}

	@Override
	public Object getItem(int _position) {
		if (mActionList == null) {
			return null;
		}
		return mActionList.get(_position);
	}

	@Override
	public long getItemId(int _position) {
		return _position;
	}

	/**
	 * The view holder is the template for the items of the list.
	 * @author Cyril Leroux
	 *
	 */
	public static class ViewHolder {
		ImageView ivActionIcon;
		TextView tvActionTitle;
		TextView tvActionSummary;
	}

	@Override
	public View getView(int _position, View _convertView, ViewGroup _parent) {
		ViewHolder holder;
		if (_convertView == null) {
			_convertView = mInflater.inflate(R.layout.action_item, null);
			holder = new ViewHolder();
			holder.ivActionIcon		= (ImageView) _convertView.findViewById(R.id.ivActionIcon);
			holder.tvActionTitle	= (TextView) _convertView.findViewById(R.id.tvActionTitle);
			holder.tvActionSummary	= (TextView) _convertView.findViewById(R.id.tvActionSummary);
			_convertView.setTag(holder);
		} else {
			holder = (ViewHolder) _convertView.getTag();
		}

		final ActionItem action = mActionList.get(_position);

		holder.ivActionIcon.setImageResource(action.getImageResource());
		holder.tvActionTitle.setText(action.getTitle());
		holder.tvActionTitle.setTypeface(mTypeface);
		holder.tvActionSummary.setText(action.getSummary());

		return _convertView;
	}
}
