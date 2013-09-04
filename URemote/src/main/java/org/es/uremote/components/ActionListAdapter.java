package org.es.uremote.components;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.es.uremote.R;
import org.es.uremote.objects.ActionItem;

import java.util.List;

/**
 * Adapter used to display an action list.
 * Used for {@link org.es.uremote.Home} activity.
 *
 * @author Cyril Leroux
 * Created on 05/11/12.
 */
public class ActionListAdapter extends BaseAdapter {
	private final List<ActionItem> mActionList;
	private final LayoutInflater mInflater;
	private final Typeface mTypeface;

	/**
	 * Default constructor
	 *
	 * @param context The application context.
	 * @param actions The action list.
	 * @param typeface The type face to use.
	 */
	public ActionListAdapter(final Context context, final List<ActionItem> actions, Typeface typeface) {
		mInflater	= LayoutInflater.from(context);
		mActionList	= actions;
		mTypeface	= typeface;
	}

	@Override
	public int getCount() {
		if (mActionList == null) {
			return 0;
		}
		return mActionList.size();
	}

	@Override
	public Object getItem(int position) {
		if (mActionList == null) {
			return null;
		}
		return mActionList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * The view holder is the template for the items of the list.
	 *
	 * @author Cyril Leroux
	 */
	public static class ViewHolder {
		ImageView ivActionIcon;
		TextView tvActionTitle;
		TextView tvActionSummary;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.action_item, null);
			holder = new ViewHolder();
			holder.ivActionIcon		= (ImageView) convertView.findViewById(R.id.ivActionIcon);
			holder.tvActionTitle	= (TextView) convertView.findViewById(R.id.tvActionTitle);
			holder.tvActionSummary	= (TextView) convertView.findViewById(R.id.tvActionSummary);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ActionItem action = mActionList.get(position);

		holder.ivActionIcon.setImageResource(action.getImageResource());
		holder.tvActionTitle.setText(action.getTitle());
		holder.tvActionTitle.setTypeface(mTypeface);
		holder.tvActionSummary.setText(action.getSummary());

		return convertView;
	}
}
