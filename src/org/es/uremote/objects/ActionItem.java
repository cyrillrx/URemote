package org.es.uremote.objects;


/**
 * Simple object that hold an action.
 * @author Cyril Leroux
 *
 */
public class ActionItem {

	private final String mTitle;
	private final String mSummary;
	private final int mImageResource;

	/**
	 * Constructor
	 * @param title
	 * @param summary
	 * @param imageResource
	 */
	public ActionItem(final String title, final String summary, final int imageResource) {
		mTitle			= title;
		mSummary		= summary;
		mImageResource	= imageResource;
	}

	/**
	 * @return the mTitle
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * @return the mSummary
	 */
	public String getSummary() {
		return mSummary;
	}

	/**
	 * @return the mImageResource
	 */
	public int getImageResource() {
		return mImageResource;
	}
}
