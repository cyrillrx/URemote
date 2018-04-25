package com.cyrillrx.uremote.explorer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.cyrillrx.android.toolbox.OnDataClickListener;
import com.cyrillrx.logger.Logger;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand.FileInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter used to display an explorer view.
 *
 * @author Cyril Leroux
 *         Created before first commit (08/04/12).
 */
public class ExplorerAdapter extends RecyclerView.Adapter<ExplorerViewHolder> {

    private static final String TAG = ExplorerAdapter.class.getSimpleName();

    private final List<FileInfo> items;
    private final OnDataClickListener<FileInfo> clickListener;

    private FileInfo currentFileInfo;

    public ExplorerAdapter(OnDataClickListener<FileInfo> clickListener) {
        items = new ArrayList<>();
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ExplorerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExplorerViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ExplorerViewHolder holder, int position) {
        holder.bind(items.get(position), clickListener);
    }

    @Override
    public int getItemCount() { return items.size(); }

    public void bind(final FileInfo dirContent) {

        currentFileInfo = dirContent;

        clear();

        if (dirContent.getChildCount() == 0) {
            Logger.warning(TAG, "bind - directory is empty.");
        } else {
            addAll(dirContent.getChildList());
        }
    }

    public void clear() {

        final int itemCount = getItemCount();
        if (itemCount > 0) {
            items.clear();
            notifyItemRangeRemoved(0, itemCount);
        }
    }

    public void addAll(@Nullable List<FileInfo> files) {

        if (files == null || files.isEmpty()) { return; }

        final int itemCountBeforeAdd = getItemCount();
        items.addAll(files);
        notifyItemRangeInserted(itemCountBeforeAdd, files.size());
    }
}
