package com.cyrillrx.uremote.explorer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyrillrx.android.toolbox.OnDataClickListener;
import com.cyrillrx.android.utils.FileUtils;
import com.cyrillrx.uremote.R;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand;

public class ExplorerViewHolder extends RecyclerView.ViewHolder {

    private ImageView ivIcon;
    private TextView tvName;
    private TextView tvSize;

    public ExplorerViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explorer, null));

        ivIcon = itemView.findViewById(R.id.ivIcon);
        tvName = itemView.findViewById(R.id.tv_name);
        tvSize = itemView.findViewById(R.id.tv_size);
    }

    public void bind(final RemoteCommand.FileInfo file,
                     final OnDataClickListener<RemoteCommand.FileInfo> clickListener) {

        int iconRes;
        if (file.getIsDirectory()) {
            iconRes = R.drawable.ic_file_directory;
        } else if (FileUtils.isVideo(file.getFilename())) {
            iconRes = R.drawable.ic_file_video;
        } else {
            iconRes = R.drawable.ic_file_blank;
        }

        ivIcon.setImageResource(iconRes);
        tvName.setText(file.getFilename());
        tvSize.setText(String.valueOf(file.getSize()));

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { clickListener.onDataClick(file); }
        });
    }
}
