package org.es.uremote.objects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Structure permettant de stocké les éléments constitutifs d'un explorateur de fichier
 * @author Cyril Leroux
 */
public class FileManagerEntity {
	public static final int TYPE_DIRECTORY	= 0;
	public static final int TYPE_FILE		= 1;
	public static final int TYPE_VIDEO		= 2;

	private String mName;
	private String mPath;
	private int mType;
	private String mSize;

	/** 
	 * Constructeur de contenu de type Catégorie.
	 * 
	 * @param _path Le chemin du fichier (dans le nom)
	 * @param _serializedData Les informations du fichiers séréalisées.
	 */
	public FileManagerEntity(String _path, String _serializedData) {
		mPath = _path;
		if (_serializedData.contains("<DIR>"))
			mType = TYPE_DIRECTORY;
		else if (_serializedData.contains(".avi<"))
			mType = TYPE_VIDEO;
		else 
			mType = TYPE_FILE;

		//mName = _serializedData.split("[<]")[0];
		//mSize = 0.0f;

		final Pattern filePattern = Pattern.compile("^([0-9A-Za-z' ._-]*)<([0-9A-Za-z ]*)>$");
		Matcher matcher = filePattern.matcher(_serializedData);
		while(matcher.find()) {	
			mName = matcher.group(1);
			mSize = matcher.group(2);
		}
	}

	public boolean isDirectory() { return mType == TYPE_DIRECTORY; }
	public boolean isVideo() { return mType == TYPE_VIDEO; }

	public String getFullPath() {
		if (mName.equals("..")) {
			int pos = mPath.lastIndexOf("\\");
			return mPath.substring(0, pos);

		}
		return mPath + "\\" + mName;
	}

	public String getName() { return mName; }
	public void setName(String _name) { mName = _name; }

	public String getPath() { return mPath; }
	public void setPath(String _path) { mPath = _path; }

	public String getSize() { return mSize; }
	public void setSize(String _size) { mSize = _size; }

}
