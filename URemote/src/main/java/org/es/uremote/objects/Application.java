package org.es.uremote.objects;

/**
* @author Cyril Leroux
* Created before first commit (08/04/12).
*/
public class Application {

	private int mImageResource;
	private String mLabel;
	private String mPath;

	public int getImageResource() { return mImageResource; }
    public void setImageResource(int imageResource) { mImageResource = imageResource; }

	public String getLabel() { return mLabel; }
	public void setLabel(String label) { mLabel = label; }

    public String getPath() { return mPath; }
    public void setPath(final String path) { mPath = path; }
}
