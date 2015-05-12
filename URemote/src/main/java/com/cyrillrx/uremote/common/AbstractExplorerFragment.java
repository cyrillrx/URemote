package com.cyrillrx.uremote.common;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.cyrillrx.android.toolbox.Logger;
import com.cyrillrx.android.utils.FileUtils;
import com.cyrillrx.uremote.R;
import com.cyrillrx.uremote.common.adapter.ExplorerArrayAdapter;
import com.cyrillrx.uremote.request.FileInfoFactory;
import com.cyrillrx.uremote.request.protobuf.RemoteCommand.FileInfo;
import com.cyrillrx.uremote.utils.IntentKeys;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * File explorer fragment.<br />
 * This fragment allow you to browse a list of files.
 *
 * @author Cyril Leroux
 *         Created on 29/08/13.
 */
public abstract class AbstractExplorerFragment extends ListFragment {

    private static final String TAG = AbstractExplorerFragment.class.getSimpleName();

    private static final String PREVIOUS_DIRECTORY_PATH = "..";
    private static final String KEY_DIRECTORY_CONTENT = "DIRECTORY_CONTENT";
    private static final String DEFAULT_DIRECTORY_PATH = "default_path";

    private TextView mTvPath;
    private String mRoot = null;

    protected String mPath = null;
    protected FileInfo mCurrentFileInfo = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.server_frag_explorer, container, false);
        mTvPath = (TextView) view.findViewById(R.id.tvPath);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FileInfo dirContent = null;

        // Restoring current directory content
        if (savedInstanceState != null) {
            byte[] dirContentAsByteArray = savedInstanceState.getByteArray(KEY_DIRECTORY_CONTENT);
            dirContent = FileInfoFactory.createFromByteArray(dirContentAsByteArray);
        }

        // Get the directory content or update the one that already exist.
        if (dirContent == null) {
            final String path = getActivity().getIntent().getStringExtra(IntentKeys.DIRECTORY_PATH);
            if (mRoot == null) {
                mRoot = path;
            }

            mPath = (path == null) ? DEFAULT_DIRECTORY_PATH : path;
            navigateTo(mPath);

        } else {
            updateView(dirContent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mCurrentFileInfo != null) {
            outState.putByteArray(KEY_DIRECTORY_CONTENT, mCurrentFileInfo.toByteArray());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final FileInfo file = mCurrentFileInfo.getChild(position);
        final String filename = file.getFilename();
        final String fullPath = mCurrentFileInfo.getAbsoluteFilePath() + File.separator + filename;

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

    /**
     * Updates the view with the content passed directory.
     *
     * @param dirContent The object that represents the directory content.
     */
    protected void updateView(final FileInfo dirContent) {

        mCurrentFileInfo = dirContent;

        if (dirContent.getChildCount() == 0) {
            Logger.warning(TAG, "#updateView - No file in the directory.");
            return;
        }

        List<FileInfo> files = new ArrayList<>();
        files.addAll(dirContent.getChildList());

        if (getListAdapter() == null) {
            final ExplorerArrayAdapter adapter = newExplorerAdapter(files);
            setListAdapter(adapter);
        } else {
            getListAdapter().clear();
            getListAdapter().addAll(files);
        }

        mTvPath.setText(dirContent.getAbsoluteFilePath());
    }

    protected ExplorerArrayAdapter newExplorerAdapter(List<FileInfo> files) {
        return new ExplorerArrayAdapter(getActivity().getApplicationContext(), files);
    }

    @Override
    public ExplorerArrayAdapter getListAdapter() {
        return (ExplorerArrayAdapter) super.getListAdapter();
    }

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
        return mCurrentFileInfo != null &&
                mCurrentFileInfo.getAbsoluteFilePath().contains(File.separator) &&
                !mCurrentFileInfo.getAbsoluteFilePath().equals(mRoot);
    }

    /**
     * Call navigateTo on the parent directory.
     */
    protected void doNavigateUp() {
        final String parentPath = FileUtils.truncatePath(mCurrentFileInfo.getAbsoluteFilePath());
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
