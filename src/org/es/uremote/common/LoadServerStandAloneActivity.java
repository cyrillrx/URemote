package org.es.uremote.common;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.es.uremote.R;
import org.es.uremote.utils.IntentKeys;
import org.es.utils.FileUtils;
import org.es.utils.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cyril on 03/09/13.
 */
public class LoadServerStandAloneActivity extends ListActivity {

    private static final String TAG = "LoadServerActivity";

	private static final String PREVIOUS_DIRECTORY_PATH	= "..";

	private TextView mTvPath;

    private String mRoot = null;
    private String mCurrentPath = null;
    private List<File> mCurrentFiles = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server_frag_explorer);

		mTvPath = (TextView) findViewById(R.id.tvPath);

		// Load from caller activity
		mRoot	= getIntent().getStringExtra(IntentKeys.DIRECTORY_PATH);

		File[] fileTab = FileUtils.listFiles(mRoot, new String[]{".xml"}, true);

		List<File> fileList = new ArrayList<>();
		for (File file : fileTab) {
			fileList.add(file);
		}

		updateView(mRoot, fileList);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		final File file = mCurrentFiles.get(position);
		final String filename = file.getName();
		final String fullPath = file.getAbsolutePath();

		if (file.isDirectory()) {

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
	 * @param files The object that represents the directory content.
	 */
	protected void updateView(final String dirPath, final List<File> files) {

		mCurrentPath	= dirPath;
		mCurrentFiles	= files;

		if (getListAdapter() == null) {
			final ExplorerAdapter2 adapter = new ExplorerAdapter2(getApplicationContext(), files);
			setListAdapter(adapter);
		} else {
			((ExplorerAdapter2) getListAdapter()).clear();
			((ExplorerAdapter2) getListAdapter()).addAll(files);
		}

		mTvPath.setText(dirPath);
	}

	/**
	 * Navigates up if possible.<br />
	 * This method is supposed to be called from the parent Activity (most likely through the ActionBar).<br />
	 * Updates the view once the data have been received from the server.
	 */
	public void navigateUp() {

		if (canNavigateUp()) {
			doNavigateUp();
		}
	}

	/**
	 * Call by the activity that holds the fragment if the back button is override.
	 * If the function returns true, the back button is override to go up.
	 * Else, it behaves normally.
	 *
	 * @return True if we can navigate up from the current directory. False otherwise.
	 */
	public boolean canNavigateUp() {
		return mCurrentPath != null && !mCurrentPath.equals(mRoot);
	}

	/**
	 * Call navigateTo on the parent directory.
	 */
	protected void doNavigateUp() {
		final String parentPath = FileUtils.truncatePath(mCurrentPath);
		navigateTo(parentPath);
	}

	/**
	 * Lists the content of the passed directory.<br />
	 * Updates the view once the data have been received.
	 *
	 * @param dirPath The path of the directory to display.
	 */
	protected void navigateTo(String dirPath) {

		File[] fileTab = FileUtils.listFiles(dirPath, new String[]{".xml"}, true);

		List<File> fileList = new ArrayList<>();
		for (File file : fileTab) {
			fileList.add(file);
		}

		updateView(dirPath, fileList);
	}

	/**
	 * Callback triggered when the user clicks on a directory.
	 *
	 * @param dirPath The path of the clicked directory.
	 */
	protected void onDirectoryClick(String dirPath) {
		navigateTo(dirPath);
	}

	/**
	 * Callback triggered when the user clicks on a file.
	 *
	 * @param filename The path of the clicked file.
	 */
	protected void onFileClick(String filename) {
		// Returns the value to the parent
	}

	/**
	 * Handle volume physical buttons.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (canNavigateUp()) {
				navigateUp();
				Log.debug(TAG, "#onKeyDown - Back key overridden.");
				return true;
			}
		}
		Log.debug(TAG, "#onKeyDown - Normal key behavior.");
		return super.onKeyDown(keyCode, event);
	}
}
