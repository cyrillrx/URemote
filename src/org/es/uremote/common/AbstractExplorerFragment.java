package org.es.uremote.common;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import org.es.uremote.R;
import org.es.uremote.components.FileManagerAdapter;
import org.es.uremote.exchange.ExchangeMessageUtils;
import org.es.uremote.exchange.ExchangeMessages.DirContent;
import org.es.utils.Log;

import java.io.File;

import static org.es.uremote.exchange.ExchangeMessages.DirContent.File.FileType.DIRECTORY;

/**
 * File explorer fragment.<br />
 * This fragment allow you to browse a list of files.
 *
 * @author Cyril Leroux
 * Created on 29/08/13.
 */
public abstract class AbstractExplorerFragment extends ListFragment {

	private static final String TAG = "AbstractExplorerFragment";

	private static final String DEFAULT_PATH			= "";
	private static final String PREVIOUS_DIRECTORY_PATH	= "..";
	private static final String KEY_DIRECTORY_CONTENT	= "DIRECTORY_CONTENT";

	private TextView mTvPath;
	protected DirContent mCurrentDirContent = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.server_frag_explorer, container, false);
		mTvPath = (TextView) view.findViewById(R.id.tvPath);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		DirContent dirContent = null;

		// Restoring current directory content
		if (savedInstanceState != null) {
			byte[] dirContentAsByteArray = savedInstanceState.getByteArray(KEY_DIRECTORY_CONTENT);
			dirContent = ExchangeMessageUtils.createDirectoryContent(dirContentAsByteArray);
		}

		// Get the directory content or update the one that already exist.
		if (dirContent == null) {
			navigateTo(DEFAULT_PATH);
		} else {
			// TODO uncomment after test
			//updateView(dirContent);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (mCurrentDirContent != null) {
			outState.putByteArray(KEY_DIRECTORY_CONTENT, mCurrentDirContent.toByteArray());
		}
		super.onSaveInstanceState(outState);
	}

	/**
	 * Updates the view with the content passed directory.
	 *
	 * @param dirContent The object that represents the directory content.
	 */
	protected void updateView(final DirContent dirContent) {

		mCurrentDirContent = dirContent;

		if (dirContent.getFileCount() == 0) {
			Log.warning(TAG, "#updateView - No file in the directory.");
			return;
		}

		final FileManagerAdapter adapter = new FileManagerAdapter(getActivity().getApplicationContext(), dirContent);
		setListAdapter(adapter);

		ListView listView = getListView();
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final DirContent.File file = dirContent.getFile(position);
				final String filename = file.getName();
				final String fullPath = dirContent.getPath()+ File.separator + filename;

				if (DIRECTORY.equals(file.getType())) {

					if (PREVIOUS_DIRECTORY_PATH.equals(filename)) {
						navigateUp();

					} else {
						onDirectoryClick(fullPath);
					}

				} else {
					onFileClick(fullPath);
				}
			}
		});

		mTvPath.setText(dirContent.getPath());
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
	 */
	public void navigateUp() {

		if (canNavigateUp()) {
			doNavigateUp();
		}
	}

	/**
	 * @return True if we can navigate up from the current directory. False otherwise.
	 */
	protected abstract boolean canNavigateUp();

	/**
	 * Lists the content of parent directory.<br />
	 * Updates the view once the data have been received.
	 */
	protected abstract void doNavigateUp();

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
