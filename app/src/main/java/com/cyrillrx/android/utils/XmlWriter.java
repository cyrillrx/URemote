package com.cyrillrx.android.utils;

import android.util.Xml;

import com.cyrillrx.logger.Logger;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Class that handle XML file creation.<br />
 * Default encoding : UTF-8
 *
 * @author Cyril Leroux
 *         Created on 04/06/13.
 */
public class XmlWriter {

    private static final String TAG = XmlWriter.class.getSimpleName();

    private static final String ENCODING = "UTF-8";
    private static final String NAMESPACE = null;
    private static final String FEATURE_INDENT = "http://xmlpull.org/v1/doc/features.html#indent-output";

    private final String rootTag;
    private final FileOutputStream fileOutputStream;
    private final XmlSerializer serializer;

    /**
     * Constructor.
     * Create and initiate the XML file.
     *
     * @param fos     The file output stream to write in.
     * @param rootTag Root tag of the file.
     * @throws IOException
     */
    public XmlWriter(FileOutputStream fos, String rootTag) throws IOException {

        this.rootTag = rootTag;

        fileOutputStream = fos;

        serializer = Xml.newSerializer();
        serializer.setOutput(fileOutputStream, ENCODING);
        serializer.startDocument(ENCODING, true);
        serializer.setFeature(FEATURE_INDENT, true);
        serializer.startTag(null, rootTag);
    }

    /** Close and save the file. */
    public void closeAndSave() {
        try {
            serializer.endTag(NAMESPACE, rootTag);
            serializer.endDocument();
            serializer.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            Logger.error(TAG, "Error in closeAndSave method");
        }
    }

    /**
     * Add simple tag (text + tag) in the XML tree.
     *
     * @param tag       Tag name.
     * @param text      Text to write in the tag.
     * @param attribute Attribute to write in the tag (String[] = {"attribute", "value"}.
     *                  Set attribute to null if no attribute.
     */
    public void addChild(String tag, String text, String[] attribute) {
        try {
            serializer.startTag(null, tag); // Open tag
            if (attribute != null) {
                serializer.attribute(null, attribute[0], attribute[1]);
            }

            if (text != null) {
                serializer.text(text);
            }

            serializer.endTag(null, tag); // Close tag
        } catch (Exception e) {
            Logger.error(TAG, "Error in addChild method");
        }
    }

    /**
     * Add simple tag (integer + attribute) in the XML tree.
     *
     * @param tag       Tag name.
     * @param intValue  Integer value to convert and write in the tag.
     * @param attribute Attribute to write in the tag (String[] = {"attribute", "value"}.
     *                  Set attribute to null if no attribute.
     */
    public void addChild(String tag, int intValue, String[] attribute) {
        String floatStr = String.valueOf(intValue);
        addChild(tag, floatStr, attribute);
    }

    /**
     * This method allow to open a tag from the outside of the class.
     *
     * @param namespace The namespace to use.
     * @param tag       Tag name.
     */
    public void startTag(String namespace, String tag) {
        try {
            serializer.startTag(null, tag); // Open tag
        } catch (Exception e) {
            Logger.error(TAG, "Error in addChild method");
        }
    }

    /**
     * This method allow to close a tag from the outside of the class.
     *
     * @param namespace The namespace to use.
     * @param tag       Tag name.
     */
    public void endTag(String namespace, String tag) {
        try {
            serializer.endTag(null, tag); // Close the tag
        } catch (Exception e) {
            Logger.error(TAG, "Error in addChild method");
        }
    }
}