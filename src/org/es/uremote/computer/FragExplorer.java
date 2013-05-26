package org.es.uremote.computer;

import static org.es.network.ExchangeProtos.DirContent.File.FileType.DIRECTORY;
import static org.es.network.ExchangeProtos.Request.Code.NONE;
import static org.es.network.ExchangeProtos.Response.ReturnCode.RC_ERROR;

import java.io.File;

import org.es.network.AsyncMessageMgr;
import org.es.network.MessageHelper;
import org.es.network.ExchangeProtos.DirContent;
import org.es.network.ExchangeProtos.Request;
import org.es.network.ExchangeProtos.Request.Code;
import org.es.network.ExchangeProtos.Request.Type;
import org.es.network.ExchangeProtos.Response;
import org.es.network.IRequestSender;
import org.es.uremote.R;
import org.es.uremote.Computer;
import org.es.uremote.components.FileManagerAdapter;
import org.es.uremote.objects.ServerInfo;
import org.es.uremote.utils.FileUtils;
import org.es.utils.Log;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * File explorer fragment.
 * This fragment allow you to browse your PC content through the application.
 *
 * @author Cyril Leroux
 */
public class FragExplorer extends ListFragment implements IRequestSender  {
	private static final String TAG = "FileManager";
	private static final int MAX_PATH_PORTRAIT = 40;
	private static final int MAX_PATH_LANDSCAPE = 70;
	private static final String DEFAULT_PATH = "L:";
	private static final String DIRECTORY_CONTENT = "DIRECTORY_CONTENT";

	private TextView mTvPath;
	private DirContent mDirectoryContent = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.debug(TAG, "onCreateView");

		View view = inflater.inflate(R.layout.server_frag_explorer, container, false);
		mTvPath = (TextView) view.findViewById(R.id.tvPath);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.debug(TAG, "onActivityCreated");

		// Restoring current directory content
		if (savedInstanceState != null) {
			try {
				byte[] dirContent = savedInstanceState.getByteArray(DIRECTORY_CONTENT);
				if (dirContent != null) {
					mDirectoryContent = DirContent.parseFrom(savedInstanceState.getByteArray(DIRECTORY_CONTENT));
				}
			} catch (InvalidProtocolBufferException e) {
				Log.error(TAG, "onCreate InvalidProtocolBufferException : " + e);
			}
		}
		// Get the directory content from the server or update the one that already exist.
		if (mDirectoryContent == null) {
			openDirectory(DEFAULT_PATH);
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

	/**
	 * Update the view with the content of the new directory
	 * @param _dirContent The object that hosts the directory content.
	 */
	private void updateView(final DirContent _dirContent){
		if (_dirContent == null) {
			return;
		}
		if (_dirContent.getFileCount() == 0) {
			Log.error(TAG, "file count == 0");
			return;
		}

		final FileManagerAdapter adpt = new FileManagerAdapter(getActivity().getApplicationContext(), _dirContent);
		setListAdapter(adpt);

		ListView listView = getListView();
		listView.setOnItemClickListener( new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> _parent, View _view, int _position, long _id) {
				final DirContent.File file = _dirContent.getFile(_position);
				final String filename = file.getName();
				final String currentDirPath = _dirContent.getPath();

				if (DIRECTORY.equals(file.getType())) {

					if ("..".equals(filename)) {
						openParentDirectory(currentDirPath);
					} else {
						final String dirPath = currentDirPath + File.separator + filename;
						openDirectory(dirPath);
					}

				} else {
					// Open the file with the default program.
					final String fullPath = currentDirPath + File.separator + filename;
					openFile(fullPath);

				}
			}
		});

		// If path is to big, just show the last characters.
		int pathLength = mDirectoryContent.getPath().length();
		int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
		int maxPath = (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) ? MAX_PATH_PORTRAIT : MAX_PATH_LANDSCAPE;
		String path = (pathLength > maxPath) ? "..." + mDirectoryContent.getPath().substring(pathLength - maxPath + 3, pathLength) : mDirectoryContent.getPath();
		mTvPath.setText(path);
	}

	/**
	 * Ask the server to list the content of the passed directory.
	 * Updates the view once the data have been received from the server.
	 * @param _dirPath The path of the directory to display.
	 */
	private void openDirectory(String _dirPath) {
		sendAsyncRequest(MessageHelper.buildRequest(AsyncMessageMgr.getSecurityToken(), Type.EXPLORER, Code.GET_FILE_LIST, NONE, _dirPath));
	}

	/**
	 * Ask the server to list the content of the passed directory's parent.
	 * Updates the view once the data have been received from the server.
	 * @param _dirPath The path of the child directory.
	 */
	private void openParentDirectory(String _dirPath) {
		final String parentPath = FileUtils.truncatePath(_dirPath);
		sendAsyncRequest(MessageHelper.buildRequest(AsyncMessageMgr.getSecurityToken(), Type.EXPLORER, Code.GET_FILE_LIST, NONE, parentPath));
	}

	/**
	 * Ask the server to list the content of the current directory's parent.
	 * This method is supposed to be called from the {@link Computer} class.
	 * Updates the view once the data have been received from the server.
	 * @param _dirPath The path of the child directory.
	 * @return true if it is possible to navigate up.
	 */
	public boolean navigateUpIfPossible() {
		if (mDirectoryContent == null) {
			return false;
		}
		final String dirPath = mDirectoryContent.getPath();
		if (dirPath.contains(File.separator)) {
			openParentDirectory(dirPath);
			return true;
		}
		return false;
	}

	private void openFile(String _filename) {
		sendAsyncRequest(MessageHelper.buildRequest(AsyncMessageMgr.getSecurityToken(), Type.EXPLORER, Code.OPEN_FILE, NONE, _filename));
	}

	////////////////////////////////////////////////////////////////////
	// *********************** Message Sender *********************** //
	////////////////////////////////////////////////////////////////////

	/**
	 * Initializes the message handler then send the request.
	 * @param _request The request to send.
	 */
	@Override
	public void sendAsyncRequest(Request _request) {
		if (ExplorerMessageMgr.availablePermits() > 0) {
			new ExplorerMessageMgr(Computer.getHandler()).execute(_request);
		} else {
			Toast.makeText(getActivity().getApplicationContext(), R.string.msg_no_more_permit, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Class that handle asynchronous requests sent to a remote server.
	 * @author Cyril Leroux
	 */
	private class ExplorerMessageMgr extends AsyncMessageMgr {

		public ExplorerMessageMgr(Handler _handler) {
			super(_handler, ServerInfo.loadFromPreferences(getActivity().getApplicationContext()));
		}

		@Override
		protected void onPostExecute(Response _response) {
			super.onPostExecute(_response);

			Log.debug(TAG, _response.getMessage());
			if (RC_ERROR.equals(_response.getReturnCode())) {
				if (!_response.getMessage().isEmpty()) {
					sendToastToUI(_response.getMessage());
				}
				return;
			}
			//TODO supprimer
			//} else if (_response.hasRequest() && GET_FILE_LIST.equals(_response.getRequest().getCode())) {
			if (!_response.getMessage().isEmpty()) {
				sendToastToUI(_response.getMessage());
			}
			mDirectoryContent = _response.getDirContent();
			updateView(_response.getDirContent());

		}
	}
}
