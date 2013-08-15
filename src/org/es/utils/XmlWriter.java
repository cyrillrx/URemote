package org.es.utils;

import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Class that handle XML file creation.<br />
 * Default encoding : UTF-8
 *
 * @author Cyril Leroux
 */
public class XmlWriter {
	private static final String TAG				= "XmlWriter";
	private static final String ENCODING		= "UTF-8";
	private static final String NAMESPACE		= null;
	private static final String FEATURE_INDENT	= "http://xmlpull.org/v1/doc/features.html#indent-output";

	private final String mRootTag;
	private final FileOutputStream mFileOutputStream;
	private final XmlSerializer mSerializer;

	/**
	 * Constructor.
	 * Create and initiate the XML file.
	 *
	 * @param fos The file output stream to write in.
	 * @param rootTag Root tag of the file.
	 * @throws IOException
	 */
	public XmlWriter(FileOutputStream fos, String rootTag) throws IOException {

		mRootTag = rootTag;

		mFileOutputStream = fos;

		mSerializer = Xml.newSerializer();
		mSerializer.setOutput(mFileOutputStream, ENCODING);
		mSerializer.startDocument(ENCODING, true);
		mSerializer.setFeature(FEATURE_INDENT, true);
		mSerializer.startTag(null, rootTag);
	}

	/** Close and save the file. */
	public void closeAndSave() {
		try {
			mSerializer.endTag(NAMESPACE, mRootTag);
			mSerializer.endDocument();
			mSerializer.flush();
			mFileOutputStream.close();
		} catch (Exception e) {
			Log.error(TAG, "Error in closeAndSave method");
		}
	}

	/**
	 * Add simple tag (text + tag) in the XML tree.
	 *
	 * @param tag Tag name.
	 * @param text Text to write in the tag.
	 * @param attribute Attribute to write in the tag (String[] = {"attribute", "value"}.
	 * Set attribute to null if no attribute.
	 */
	public void addChild(String tag, String text, String[] attribute) {
		try {
			mSerializer.startTag(null, tag); // Open tag
			if (attribute != null) {
				mSerializer.attribute(null, attribute[0], attribute[1]);
			}

			if (text != null) {
				mSerializer.text(text);
			}

			mSerializer.endTag(null, tag); // Close tag
		} catch (Exception e) {
			Log.error(TAG, "Error in addChild method");
		}
	}

	/**
	 * Add simple tag (integer + attribute) in the XML tree.
	 *
	 * @param tag Tag name.
	 * @param intValue Integer value to convert and write in the tag.
	 * @param attribute Attribute to write in the tag (String[] = {"attribute", "value"}.
	 * Set attribute to null if no attribute.
	 */
	public void addChild(String tag, int intValue, String[] attribute) {
		String floatStr = String.valueOf(intValue);
		addChild(tag, floatStr, attribute);
	}

	/**
	 * This method allow to open a tag from the outside of the class.
	 *
	 * @param namespace The namespace to use.
	 * @param tag Tag name.
	 */
	public void startTag(String namespace, String tag) {
		try {
			mSerializer.startTag(null, tag); // Open tag
		} catch (Exception e) {
			Log.error(TAG, "Error in addChild method");
		}
	}

	/**
	 * This method allow to close a tag from the outside of the class.
	 *
	 * @param namespace The namespace to use.
	 * @param tag Tag name.
	 */
	public void endTag(String namespace, String tag) {
		try {
			mSerializer.endTag(null, tag); // Close the tag
		} catch (Exception e) {
			Log.error(TAG, "Error in addChild method");
		}
	}
}