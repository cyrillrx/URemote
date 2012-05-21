package org.es.uremote.computer;

import static org.es.uremote.utils.Constants.DEBUG;

import java.util.ArrayList;
import java.util.List;

import org.es.uremote.R;
import org.es.uremote.components.FileManagerAdapter;
import org.es.uremote.network.AsyncMessageMgr;
import org.es.uremote.objects.FileManagerEntity;
import org.es.uremote.utils.ServerMessage;

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

public class FragExplorer extends ListFragment {
	private static final String TAG = "FileManager";
	private static final int MAX_PATH_PORTRAIT = 40;
	private static final int MAX_PATH_LANDSCAPE = 70;
	private static final String DEFAULT_PATH = "L:\\Series";
	private static final String DEFAULT_CONTENT = "..<DIR>|24<DIR>|Breaking Bad<DIR>|Dexter<DIR>|Futurama<DIR>|Game of Thrones<DIR>|Glee<DIR>|Heroes<DIR>|House<DIR>|How I Met Your Mother<DIR>|Legend of the Seeker<DIR>|Merlin<DIR>|Misfits<DIR>|No Ordinary Family<DIR>|Prison Break<DIR>|Scrubs<DIR>|Smallville<DIR>|South Park<DIR>|Terminator The Sarah Connor Chronicles<DIR>|The Vampire Diaries<DIR>|The Walking Dead<DIR>|Thumbs.db<4608 bytes>";

	private TextView mTvPath; 
	private String mDirectoryPath;
	private String mDirectoryContent;
	private ViewPagerDashboard mParent;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mParent = (ViewPagerDashboard) getActivity();
	}
	
	@Override
	public void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		
		if (mDirectoryPath == null || mDirectoryContent == null) {
			mDirectoryPath		= DEFAULT_PATH;
			mDirectoryContent	= DEFAULT_CONTENT;
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.server_frag_explorer, container, false);
		mTvPath = (TextView) view.findViewById(R.id.tvPath);
		return view;
	}

	@Override
	public void onStart() {
		updateView(mDirectoryContent);
		super.onStart();
	}
	
	/**
	 * Transforme le message séréalisé en liste de fichiers pour l'arborescence
	 * @param dirInfo Les informations sur le dossier séréalirées
	 * @return La liste des FileManagerEntity prêts à être affichés
	 */
	private List<FileManagerEntity> directoryInfoToList(String dirInfo) {
		if (dirInfo == null || dirInfo.isEmpty())
			return null;

		final List<FileManagerEntity> fileList = new ArrayList<FileManagerEntity>();
		String[] filesInfo = dirInfo.split("[|]");
		for (String fileInfo : filesInfo) {
			fileList.add(new FileManagerEntity(mDirectoryPath, fileInfo));
		}
		return fileList;
	}

	/**
	 * Fonction permettant de mettre à jour de la vue
	 * @param infos Les éléments constitutifs du dossier à afficher (données séréalisées)
	 */
	private void updateView(String infos){
		final List<FileManagerEntity> fileList = directoryInfoToList(infos);

		if (fileList.size() == 0) {
			if (DEBUG)
				Log.e(TAG, "fileList is null");
			return;
		}

		FileManagerAdapter adpt = new FileManagerAdapter(getActivity().getApplicationContext(), fileList);
		setListAdapter(adpt);

		ListView listView = getListView();
		listView.setOnItemClickListener( new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> _parent, View _view, int _position, long _id) {
				if (fileList.get(_position).isDirectory()) {
					getDirectoryContent(fileList.get(_position).getFullPath());

				} else {
					// Ouvrir le fichier avec le programme par défaut
					openFile(fileList.get(_position).getFullPath());

				}
			}
		});

		int pathLength = mDirectoryPath.length();
		// On n'affiche que les derniers caractères
		//TODO Gérer l'orientation
		int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
		
		int maxPath = (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) ? MAX_PATH_PORTRAIT : MAX_PATH_LANDSCAPE;
		String path = (pathLength > maxPath) ? "..." + mDirectoryPath.substring(pathLength - maxPath + 3, pathLength) : mDirectoryPath;
		mTvPath.setText(path);
	}

	/**
	 * Demande au serveur de lister le contenu du répertoire.
	 * Lance l'activité une fois les données récupérée.
	 * @param _dirPath Le chemin du répertoire à afficher. 
	 */
	private void getDirectoryContent(String _dirPath) {
		sendAsyncMessage(ServerMessage.OPEN_DIR, _dirPath);
	}

	/**
	 * Mise à jour de la vue avec les nouveaux fichiers
	 * @param _dirPath Le chemin du répertoire courant.
	 * @param _dirContent Le contenu du répertoire courant.
	 */
	private void updateDirectory(String _dirPath, String _dirContent) {
		// Afficher le contenu du répertoire
		mDirectoryPath = _dirPath;
		mDirectoryContent = _dirContent;
		updateView(mDirectoryContent);
	}

	private void openFile(String _filename) {
		sendAsyncMessage(ServerMessage.OPEN_FILE, _filename);
	}

	////////////////////////////////////////////////////////////////////
	// *********************** Message Sender *********************** //
	////////////////////////////////////////////////////////////////////
	
	/**
	 * Cette fonction initialise le composant gérant l'envoi des messages 
	 * puis envoie le message passé en paramètre.
	 * @param _code Le code du message. 
	 * @param _param Le paramètre du message.
	 */
	private void sendAsyncMessage(String _code, String _param) {
		if (ExplorerMessageMgr.availablePermits() > 0) {
			new ExplorerMessageMgr(mParent.getHandler()).execute(_code, _param);
		} else {
			Toast.makeText(getActivity().getApplicationContext(), R.string.msg_no_more_permit, Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Classe asynchrone de gestion d'envoi des messages au serveur
	 * @author cyril.leroux
	 */
	private class ExplorerMessageMgr extends AsyncMessageMgr {

		public ExplorerMessageMgr(Handler _handler) {
			super(_handler);
		}

		@Override
		protected void onPostExecute(String _serverReply) {
			super.onPostExecute(_serverReply);

			if (ServerMessage.RC_ERROR.equals(mReturnCode)) {
				showToast(_serverReply);
			} else if (ServerMessage.OPEN_DIR.equals(mCommand)) {
				updateDirectory(mParam, _serverReply);
			} 
		}
	}
}
