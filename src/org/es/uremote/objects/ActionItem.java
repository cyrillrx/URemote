package org.es.uremote.objects;


/**
 * Simple object that hold an action.
 * Used in Home Activity
 *
 * @author Cyril Leroux
 */
public class ActionItem {

	private final String mTitle;
	private final String mSummary;
	private final int mImageResource;

	/**
	 * Constructor
	 *
	 * @param title The action title.
	 * @param summary The action summary.
	 * @param imageResource The resource id of the thumbnail image.
	 */
	public ActionItem(final String title, final String summary, final int imageResource) {
		mTitle			= title;
		mSummary		= summary;
		mImageResource	= imageResource;
	}

	/** @return the mTitle */
	public String getTitle() {
		return mTitle;
	}

	/** @return the mSummary */
	public String getSummary() {
		return mSummary;
	}

	/** @return the mImageResource */
	public int getImageResource() {
		return mImageResource;
	}
}
