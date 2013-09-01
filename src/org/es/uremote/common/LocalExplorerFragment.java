package org.es.uremote.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.es.uremote.R;
import org.es.uremote.exchange.DirContentFactory;
import org.es.uremote.exchange.ExchangeMessages;
import org.es.uremote.utils.IntentKeys;
import org.es.utils.FileUtils;

import java.io.File;

/**
 * Local file explorer fragment.
 * This fragment allow you to browse the content of the device.
 *
 * @author Cyril Leroux
 * Created on 31/08/13.
 */
public class LocalExplorerFragment extends AbstractExplorerFragment {

	@Override
	protected void navigateTo(String dirPath) {

		// TODO change static extension for a variable
		final ExchangeMessages.DirContent content
				= DirContentFactory.createFromLocalPath(dirPath, new String[]{".xml"}, true);
		updateView(content);
	}

	@Override
	protected void onDirectoryClick(String dirPath) {
		navigateTo(dirPath);
	}

	@Override
	protected void onFileClick(String filename) {
		// Returns the value to the parent
	}
}
