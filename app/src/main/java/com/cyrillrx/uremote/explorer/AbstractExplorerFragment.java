package com.cyrillrx.uremote.explorer;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyrillrx.android.binding.RequestLifecycle;
import com.cyrillrx.android.toolbox.OnDataClickListener;
import com.cyrillrx.uremote.R;
import com.cyrillrx.uremote.request.FileInfoFactory;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand.FileInfo;
import com.cyrillrx.uremote.utils.IntentKeys;

import java.io.File;

/**
 * File explorer fragment.<br />
 * This fragment allow you to browse a list of files.
 *
 * @author Cyril Leroux
 *         Created on 29/08/13.
 */
public abstract class AbstractExplorerFragment extends Fragment
        implements RequestLifecycle {

    protected TextView tvPath;
    protected TextView tvEmpty;
    protected View errorLayout;

    protected RecyclerView recyclerView;
    protected ExplorerAdapter adapter;

    protected FileInfo currentFileInfo;

    protected Explorer explorer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.server_frag_explorer, container, false);
        initView(view);
        return view;
    }

    protected void initView(View root) {

        tvPath = root.findViewById(R.id.tv_path);
        tvEmpty = root.findViewById(R.id.tv_empty);

        errorLayout = root.findViewById(R.id.error_layout);

        recyclerView = root.findViewById(R.id.recycler_view);

        root.findViewById(R.id.btn_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                explorer.reload();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupRecycler();

        if (explorer == null) {
            explorer = createExplorer();
            final Intent intent = getActivity().getIntent();
            if (intent.hasExtra(IntentKeys.DIRECTORY_PATH)) {
                final String root = intent.getStringExtra(IntentKeys.DIRECTORY_PATH);
                explorer.changeRoot(root);
                return;
            }
        }

        // Restoring current directory content
        if (savedInstanceState != null && savedInstanceState.containsKey(Explorer.KEY_DIRECTORY_CONTENT)) {
            byte[] dirContentAsByteArray = savedInstanceState.getByteArray(Explorer.KEY_DIRECTORY_CONTENT);
            final FileInfo savedDir = FileInfoFactory.createFromByteArray(dirContentAsByteArray);
            updateView(savedDir);
        }
    }

    protected abstract Explorer createExplorer();

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (currentFileInfo != null) {
            outState.putByteArray(Explorer.KEY_DIRECTORY_CONTENT, currentFileInfo.toByteArray());
        }
        super.onSaveInstanceState(outState);
    }

    protected void setupRecycler() {

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final OnDataClickListener<FileInfo> clickListener = new OnDataClickListener<FileInfo>() {
            @Override
            public void onDataClick(FileInfo file) {
                final String filename = file.getFilename();
                final String fullPath = currentFileInfo.getAbsoluteFilePath() + File.separator + filename;

                if (file.getIsDirectory()) {
                    if (Explorer.PREVIOUS_DIRECTORY_PATH.equals(filename)) {
                        explorer.navigateUp();
                    } else {
                        onDirectoryClick(fullPath);
                    }

                } else {
                    onFileClick(fullPath);
                }
            }
        };
        adapter = new ExplorerAdapter(clickListener);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStartLoading() {
        if (adapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
//            loader.setVisibility(View.VISIBLE);
        }
        tvEmpty.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
    }

    @Override
    public void onStopLoading() {
//        loader.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);

        if (adapter.getItemCount() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestFailure() {
        recyclerView.setVisibility(View.GONE);
//        loader.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Updates the view with the content passed directory.
     *
     * @param dirContent The object that represents the directory content.
     */
    protected void updateView(final FileInfo dirContent) {

        currentFileInfo = dirContent;

        adapter.bind(dirContent);

        tvPath.setText(dirContent.getAbsoluteFilePath());
    }

    /**
     * Callback triggered when the user clicks on a directory.
     *
     * @param dirPath The path of the clicked directory.
     */
    protected abstract void onDirectoryClick(String dirPath);

    /**
     * Callback triggered when the user clicks on a file.
     *
     * @param filename The path of the clicked file.
     */
    protected abstract void onFileClick(String filename);
}
