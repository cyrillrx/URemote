package org.es.uremote.computer;

import static org.es.uremote.utils.Constants.DEBUG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.es.uremote.R;
import org.es.uremote.components.FileManagerAdapter;
import org.es.uremote.objects.FileManagerEntity;
import org.es.uremote.utils.IntentKeys;
import org.es.uremote.utils.Message;

import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class FragFileManager extends ListFragment {
	private static final String TAG = "FileManager";

	private static Semaphore sSemaphore = new Semaphore(1);
	private AsyncMessageMgr mMessageManager; 

	private String mDirectoryPath;
	private String mDirectoryContent;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO recupérer le repertoire courant ici (getExtra)
		mDirectoryPath		= getActivity().getIntent().getStringExtra(IntentKeys.DIR_PATH);
		mDirectoryContent	= getActivity().getIntent().getStringExtra(IntentKeys.DIR_CONTENT);

		if (mDirectoryPath == null || mDirectoryContent == null) {
			mDirectoryPath		= "L:\\Series";
			mDirectoryContent = "..<DIR>|24<DIR>|Breaking Bad<DIR>|Dexter<DIR>|Futurama<DIR>|Game of Thrones<DIR>|Glee<DIR>|Heroes<DIR>|House<DIR>|How I Met Your Mother<DIR>|Legend of the Seeker<DIR>|Merlin<DIR>|Misfits<DIR>|No Ordinary Family<DIR>|Prison Break<DIR>|Scrubs<DIR>|Smallville<DIR>|South Park<DIR>|Terminator The Sarah Connor Chronicles<DIR>|The Vampire Diaries<DIR>|The Walking Dead<DIR>|Thumbs.db<4608 bytes>";
		}

		//int pathLength = mDirectoryPath.length();
		// On n'affiche que les derniers caractères
		// TODO Gérer l'orientation
		//		String actionBarTitle = (pathLength > 33) ? "..." + mDirectoryPath.substring(pathLength - 30, pathLength) : mDirectoryPath;
		//		getActionBar().setTitle(actionBarTitle);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.filemanager, container, false);
		return view;
	}

	@Override
	public void onStart() {
		updateView(mDirectoryContent);
		super.onStart();
	}

	/**
	 * Cette fonction initialise le composant gérant l'envoi des messages 
	 * puis envoie le message passé en paramètre.
	 * @param _command La commande à envoyer
	 * @param _param Le paramètre de la commande.
	 */
	private void sendAsyncCommand(String _command, String _param) {

		if (mMessageManager == null) {
			mMessageManager = new AsyncMessageMgr();
			mMessageManager.execute(_command, _param);
		} else {
			Toast.makeText(getActivity().getApplicationContext(), "Already initialized", Toast.LENGTH_SHORT).show();
		}
	}

	//	private void stopMessageManager() {
	//		if (mMessageManager != null) {
	//			mMessageManager = null;
	//		}
	//	}

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
//		if (listView != null) {
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
//		}
	}

	/**
	 * Demande au serveur de lister le contenu du répertoire.
	 * Lance l'activité une fois les données récupérée.
	 * @param _dirPath Le chemin du répertoire à afficher. 
	 */
	private void getDirectoryContent(String _dirPath) {
		sendAsyncCommand(Message.OPEN_DIR, _dirPath);
	}

	private void openDirectory(String _dirPath, String _dirContent) {
		// Afficher le contenu du répertoire
		Intent intent = new Intent(getActivity().getApplicationContext(), FragFileManager.class);
		intent.putExtra(IntentKeys.DIR_PATH,	_dirPath);
		intent.putExtra(IntentKeys.DIR_CONTENT,	_dirContent);
		startActivity(intent);	
	}

	private void openFile(String _filename) {
		sendAsyncCommand(Message.OPEN_FILE, _filename);
	}

	/**
	 * Classe asynchrone de gestion d'envoi de command avec paramètres au serveur.
	 * @author cyril.leroux
	 */
	public class AsyncMessageMgr extends AsyncTask<String, byte[], String> {
		private static final String TAG = "AsyncMessageMgr";
		private static final int PORT = 8082;
		private static final String HOST = "192.168.0.1";
		private static final int CONNECTION_TIMEOUT = 1000;

		private String mCommand;
		private String mParam;

		/**
		 * Cette fonction est exécutée avant l'appel à {@link #doInBackground(String...)} 
		 * Exécutée dans le thread principal.
		 */
		@Override
		protected void onPreExecute() {
			try {
				sSemaphore.acquire();
			} catch (InterruptedException e) {
				if (DEBUG)
					Log.e(TAG, "Semaphore acquire error.");
			}
			if (DEBUG)
				Log.i(TAG, "Semaphore acquire. " + sSemaphore.availablePermits() + " left");
		}

		/**
		 * Cette fonction est exécutée sur un thread différent du thread principal
		 */
		@Override
		protected String doInBackground(String... _params) {
			String serverReply = "";

			mCommand	= _params[0];
			mParam		= (_params.length > 1) ? _params[1] : "";
			final String message	= mCommand + "|" + mParam;

			Socket socket = null;
			try {

				// Création du socket
				socket = connectToRemoteSocket(HOST, PORT, message);
				if (socket != null && socket.isConnected())
					serverReply = sendAsyncMessage(socket, message);

			} catch (IOException e) {
				mCommand = Message.ERROR;
				serverReply = "IOException" + e.getMessage();
				if (DEBUG)
					Log.e(TAG, serverReply);

			}  catch (Exception e) {
				mCommand = Message.ERROR;
				serverReply = "IOException" + e.getMessage();
				if (DEBUG) 
					Log.e(TAG, serverReply);

			} finally {
				closeSocketIO(socket);
			}

			return serverReply;
		}

		/**
		 * Cette fonction est exécutée après l'appel à {@link #doInBackground(String...)} 
		 * Exécutée dans le thread principal.
		 * @param _serverReply La réponse du serveur renvoyée par la fonction {@link #doInBackground(String...)}.
		 */
		@Override
		protected void onPostExecute(String _serverReply) {

			if (_serverReply != null && !_serverReply.isEmpty()) {

				if (DEBUG)
					Log.i(TAG, "Get a reply : " + _serverReply);

				if (Message.OPEN_DIR.equals(mCommand)) {
					openDirectory(mParam, _serverReply);

				} else if (Message.ERROR.equals(mCommand)) {
					Toast.makeText(getActivity().getApplicationContext(), _serverReply, Toast.LENGTH_SHORT).show();
				}

			} 

			sSemaphore.release();
			mMessageManager = null;

			if (DEBUG)
				Log.i(TAG, "Semaphore release");
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}


		/**
		 * Fonction de connexion à un socket disant.
		 * @param _host L'adresse ip de l'hôte auquel est lié le socket.
		 * @param _port Le numéro de port de l'hôte auquel est lié le socket.
		 * @param _message Le message à envoyer.
		 * @return true si la connexion s'est effectuée correctement, false dans les autres cas.
		 * @throws IOException excteption
		 */
		private Socket connectToRemoteSocket(String _host, int _port, String _message) throws IOException {

			final SocketAddress socketAddress = new InetSocketAddress(_host, _port);
			Socket socket = new Socket();
			socket.connect(socketAddress, CONNECTION_TIMEOUT);

			return socket;
		}

		/**
		 * Cette fonction est appelée depuis le thread principal
		 * Elle permet l'envoi d'une commande et d'un paramètre 
		 * @param _socket Le socket sur lequel on envoie le message.
		 * @param _message Le message à transmettre
		 * @return La réponse du serveur.
		 * @throws IOException exception.
		 */
		private String sendAsyncMessage(Socket _socket, String _message) throws IOException {
			if (DEBUG)
				Log.i(TAG, "sendMessage: " + _message);
			String serverReply = "";

			if (_socket.isConnected()) {
				_socket.getOutputStream().write(_message.getBytes());
				_socket.getOutputStream().flush();
				_socket.shutdownOutput();
				serverReply = getServerReply(_socket);
			}
			return serverReply;
		}

		/**
		 * @param _socket Le socket auquel le message a été envoyé.
		 * @return La réponse du serveur.
		 * @throws IOException exeption
		 */
		private String getServerReply(Socket _socket) throws IOException {
			final int BUFSIZ = 512; 

			final BufferedReader bufferReader = new BufferedReader(new InputStreamReader(_socket.getInputStream()), BUFSIZ);
			String line = "", reply = "";
			while ((line = bufferReader.readLine()) != null)
				reply += line;

			if (DEBUG)
				Log.i(TAG, "Got a reply : " + reply);

			return reply;
		} 

		/**
		 * Ferme les entrées/sortie du socket puis ferme le socket.
		 * @param _socket Le socket à fermer.
		 */
		private void closeSocketIO(Socket _socket) {
			if (_socket == null)
				return;

			try { if (_socket.getInputStream() != null)	_socket.getInputStream().close();	} catch(IOException e) {}
			try { if (_socket.getOutputStream() != null)	_socket.getOutputStream().close();	} catch(IOException e) {}
			try { _socket.close(); } catch(IOException e) {}
		}

	}
}
