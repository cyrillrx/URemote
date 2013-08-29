package org.es.uremote.common;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;

import org.es.uremote.Computer;
import org.es.uremote.R;
import org.es.uremote.components.FileManagerAdapter;
import org.es.uremote.computer.dao.ServerSettingDao;
import org.es.uremote.exchange.ExchangeMessageUtils;
import org.es.uremote.exchange.ExchangeMessages.DirContent;
import org.es.uremote.exchange.ExchangeMessages.Request;
import org.es.uremote.exchange.ExchangeMessages.Request.Code;
import org.es.uremote.exchange.ExchangeMessages.Request.Type;
import org.es.uremote.exchange.ExchangeMessages.Response;
import org.es.uremote.exchange.RequestSender;
import org.es.uremote.network.AsyncMessageMgr;
import org.es.uremote.network.MessageHelper;
import org.es.uremote.utils.TaskCallbacks;
import org.es.utils.FileUtils;
import org.es.utils.Log;

import java.io.File;

import static org.es.uremote.exchange.ExchangeMessages.DirContent.File.FileType.DIRECTORY;
import static org.es.uremote.exchange.ExchangeMessages.Request.Code.NONE;
import static org.es.uremote.exchange.ExchangeMessages.Response.ReturnCode.RC_ERROR;

/**
 * File explorer fragment.
 * This fragment allow you to browse a list of files.
 *
 * @author Cyril Leroux
 * Created on 29/08/13.
 */
public abstract class AbstractExplorerFragment extends ListFragment {

	private static final String TAG = "AbstractExplorerFragment";

	private static final String DEFAULT_PATH		= "";
	private static final String DIRECTORY_CONTENT	= "DIRECTORY_CONTENT";

	private TextView mTvPath;
	private DirContent mDirectoryContent = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.server_frag_explorer, container, false);
		mTvPath = (TextView) view.findViewById(R.id.tvPath);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Restoring current directory content
		if (savedInstanceState != null) {
			byte[] dirContent = savedInstanceState.getByteArray(DIRECTORY_CONTENT);
			mDirectoryContent = ExchangeMessageUtils.createDirectoryContent(dirContent);
		}

		// Get the directory content from the server or update the one that already exist.
		if (mDirectoryContent == null) {
			// TODO see if function make sense here
			onDirectoryClick(DEFAULT_PATH);
		} else {
			updateView(mDirectoryContent);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (mDirectoryContent != null) {
			outState.putByteArray(DIRECTORY_CONTENT, mDirectoryContent.toByteArray());
		}
		super.onSaveInstanceState(outState);
	}

	// TODO change comment
	/**
	 * Update the view with the content of the new directory
	 *
	 * @param dirContent The object that hosts the directory content.
	 */
	private void updateView(final DirContent dirContent) {
		if (dirContent == null) {
			return;
		}
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
				final String currentDirPath = dirContent.getPath();

				if (DIRECTORY.equals(file.getType())) {

					if ("..".equals(filename)) {
						navigateUp();

					} else {
						final String dirPath = currentDirPath + File.separator + filename;
						onDirectoryClick(dirPath);
					}

				} else {
					// Open the file with the default program.
					final String fullPath = currentDirPath + File.separator + filename;
					onFileClick(fullPath);

				}
			}
		});

		mTvPath.setText(mDirectoryContent.getPath());
	}

	// TODO change comment
	/**
	 * Ask the server to list the content of the current directory's parent.
	 * This method is supposed to be called from the {@link org.es.uremote.Computer} class.
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
	 * List the content of parent directory.
	 * Updates the view once the data have been received.
	 */
	protected abstract void doNavigateUp();

	// TODO change comment
	/**
	 * Ask the server to list the content of the passed directory.
	 * Updates the view once the data have been received from the server.
	 *
	 * @param dirPath The path of the directory to display.
	 */
	protected abstract void onDirectoryClick(String dirPath);

	// TODO add comment
	protected abstract void onFileClick(String filename);
}
