package org.es.uremote.computer;

import static org.es.network.ExchangeProtos.DirContent.File.FileType.DIRECTORY;
import static org.es.network.ExchangeProtos.Request.Code.GET_FILE_LIST;
import static org.es.network.ExchangeProtos.Response.ReturnCode.RC_ERROR;
import static org.es.uremote.BuildConfig.DEBUG;

import org.es.network.AsyncMessageMgr;
import org.es.network.ExchangeProtos.DirContent;
import org.es.network.ExchangeProtos.Request;
import org.es.network.ExchangeProtos.Request.Code;
import org.es.network.ExchangeProtos.Request.Type;
import org.es.network.ExchangeProtos.Response;
import org.es.network.IRequestSender;
import org.es.uremote.R;
import org.es.uremote.ServerControl;
import org.es.uremote.components.FileManagerAdapter;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
	private static final String DEFAULT_PATH = "L:\\";
	//	private static final String DEFAULT_CONTENT = "..<DIR>|24<DIR>|Breaking Bad<DIR>|Dexter<DIR>|Futurama<DIR>|Game of Thrones<DIR>|Glee<DIR>|Heroes<DIR>|House<DIR>|How I Met Your Mother<DIR>|Legend of the Seeker<DIR>|Merlin<DIR>|Misfits<DIR>|No Ordinary Family<DIR>|Prison Break<DIR>|Scrubs<DIR>|Smallville<DIR>|South Park<DIR>|Terminator The Sarah Connor Chronicles<DIR>|The Vampire Diaries<DIR>|The Walking Dead<DIR>|Thumbs.db<4608 bytes>";

	private TextView mTvPath;
	private final String mDirectoryPath = DEFAULT_PATH;
	private DirContent mDirectoryContent = null;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.server_frag_explorer, container, false);
		mTvPath = (TextView) view.findViewById(R.id.tvPath);
		return view;
	}

	@Override
	public void onStart() {
		if (mDirectoryContent == null) {
			sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.EXPLORER, Code.GET_FILE_LIST, mDirectoryPath));
		} else {
			updateView(mDirectoryContent);
		}
		super.onStart();
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
			if (DEBUG) {
				Log.e(TAG, "file count == 0");
			}
			return;
		}

		FileManagerAdapter adpt = new FileManagerAdapter(getActivity().getApplicationContext(), _dirContent);
		setListAdapter(adpt);

		ListView listView = getListView();
		listView.setOnItemClickListener( new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> _parent, View _view, int _position, long _id) {
				DirContent.File file = _dirContent.getFile(_position);
				if (DIRECTORY.equals(_dirContent.getFile(_position).getType())) {
					getDirectoryContent(_dirContent.getPath());

				} else {
					// Open the file with the default program.
					final String fullPath = _dirContent.getPath() + file.getName();
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
	 * Ask the server to list the content of a directory.
	 * Launches the activity once the data have been received.
	 * @param _dirPath The path of the directory to display.
	 */
	private void getDirectoryContent(String _dirPath) {
		sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.EXPLORER, Code.GET_FILE_LIST));
	}

	private void openFile(String _filename) {
		sendAsyncRequest(AsyncMessageMgr.buildRequest(Type.EXPLORER, Code.OPEN_FILE));
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
			new ExplorerMessageMgr(ServerControl.getHandler()).execute(_request);
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
			super(_handler);
		}

		@Override
		protected void onPostExecute(Response _response) {
			super.onPostExecute(_response);

			if (DEBUG) {
				Log.e(TAG, "ExplorerMessageMgr onPostExecute");
			}

			if (RC_ERROR.equals(_response.getReturnCode())) {
				showToast(_response.getMessage());

			} else if (_response.hasRequest() && GET_FILE_LIST.equals(_response.getRequest().getCode())) {
				mDirectoryContent = _response.getDirContent();
				updateView(_response.getDirContent());
			}
		}
	}
}
