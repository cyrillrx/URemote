package com.cyrillrx.uremote.explorer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyrillrx.android.binding.RequestLifecycle;
import com.cyrillrx.android.toolbox.OnDataClickListener;
import com.cyrillrx.android.utils.FileUtils;
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

    private static final String TAG = AbstractExplorerFragment.class.getSimpleName();

    private static final String PREVIOUS_DIRECTORY_PATH = "..";
    private static final String KEY_DIRECTORY_CONTENT = "DIRECTORY_CONTENT";
    private static final String DEFAULT_DIRECTORY_PATH = "default_path";

    protected TextView tvPath;
    protected TextView tvEmpty;
    protected View errorLayout;

    protected RecyclerView recyclerView;
    protected ExplorerAdapter adapter;

    protected String root;
    protected String path;

    protected FileInfo currentFileInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.server_frag_explorer, container, false);
        initView(view);
        return view;
    }

    protected void initView(View root) {

        tvPath = (TextView) root.findViewById(R.id.tv_path);
        tvEmpty = (TextView) root.findViewById(R.id.tv_empty);

        errorLayout = root.findViewById(R.id.error_layout);

        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);

        root.findViewById(R.id.btn_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                retry();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupRecycler();

        FileInfo dirContent = null;

        // Restoring current directory content
        if (savedInstanceState != null) {
            byte[] dirContentAsByteArray = savedInstanceState.getByteArray(KEY_DIRECTORY_CONTENT);
            dirContent = FileInfoFactory.createFromByteArray(dirContentAsByteArray);
        }

        // Get the directory content or update the one that already exists.
        if (dirContent == null) {
            final String path = getActivity().getIntent().getStringExtra(IntentKeys.DIRECTORY_PATH);
            if (root == null) {
                root = path;
            }

            this.path = (path == null) ? DEFAULT_DIRECTORY_PATH : path;
            navigateTo(this.path);

        } else {
            updateView(dirContent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (currentFileInfo != null) {
            outState.putByteArray(KEY_DIRECTORY_CONTENT, currentFileInfo.toByteArray());
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

                    if (PREVIOUS_DIRECTORY_PATH.equals(filename)) {
                        navigateUp();
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

    protected abstract void retry();

    /**
     * Lists the content of the passed directory.<br />
     * Updates the view once the data have been received.
     *
     * @param dirPath The path of the directory to display.
     */
    protected abstract void navigateTo(String dirPath);

    /**
     * Navigates up if possible.<br />
     * This method is supposed to be called from the parent Activity (most likely through the ActionBar).<br />
     * Updates the view once the data have been received from the server.
     *
     * @return True if we can navigate up from the current directory. False otherwise.
     */
    public boolean navigateUp() {

        if (canNavigateUp()) {
            doNavigateUp();
            return true;
        }
        return false;
    }

    /**
     * Call by the activity that holds the fragment if the back button is override.
     * If the function returns true, the back button is override to go up.
     * Else, it behaves normally.
     *
     * @return True if we can navigate up from the current directory. False otherwise.
     */
    public boolean canNavigateUp() {
        return currentFileInfo != null &&
                currentFileInfo.getAbsoluteFilePath().contains(File.separator) &&
                !currentFileInfo.getAbsoluteFilePath().equals(root);
    }

    /**
     * Call navigateTo on the parent directory.
     */
    protected void doNavigateUp() {
        final String parentPath = FileUtils.truncatePath(currentFileInfo.getAbsoluteFilePath());
        navigateTo(parentPath);
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
