package org.es.uremote.components;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.es.uremote.R;
import org.es.uremote.exchange.Message.FileInfo;
import org.es.utils.FileUtils;

import java.util.List;

/**
 * Adapter used to display an explorer view.
 *
 * @author Cyril Leroux
 *         Created before first commit (08/04/12).
 */
public class ExplorerArrayAdapter extends ArrayAdapter<FileInfo> {

    private final LayoutInflater mInflater;
    private Typeface mTypeface;


    /**
     * Default constructor
     *
     * @param context  the application context.
     * @param entries  the files to display
     * @param typeface The Typeface to set.
     */
    public ExplorerArrayAdapter(Context context, final List<FileInfo> entries, final Typeface typeface) {
        super(context, 0, entries);
        mInflater = LayoutInflater.from(context);
        mTypeface = typeface;
    }

    /**
     * Default constructor
     *
     * @param context the application context.
     * @param entries the files to display
     */
    public ExplorerArrayAdapter(Context context, final List<FileInfo> entries) {
        this(context, entries, null);
    }

    /** Template for list items. */
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
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvSize = (TextView) convertView.findViewById(R.id.tvSize);
            if (mTypeface != null) {
                holder.tvName.setTypeface(mTypeface);
                holder.tvSize.setTypeface(mTypeface);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final FileInfo file = getItem(position);

        int iconRes;
        if (file.getIsDirectory()) {
            iconRes = R.drawable.filemanager_folder;
        } else if (FileUtils.isAVideo(file.getFilename())) {
            iconRes = R.drawable.filemanager_video;
        } else {
            iconRes = R.drawable.filemanager_blank;
        }

        holder.ivIcon.setImageResource(iconRes);
        holder.tvName.setText(file.getFilename());
        holder.tvSize.setText(String.valueOf(file.getSize()));

        return convertView;
    }
}
