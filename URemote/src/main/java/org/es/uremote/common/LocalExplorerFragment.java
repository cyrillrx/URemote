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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Local file explorer fragment.
 * This fragment allow you to browse the content of the device.
 *
 * @author Cyril Leroux
 * Created on 31/08/13.
 */
public class LocalExplorerFragment extends AbstractExplorerFragment2 {

	@Override
	protected void navigateTo(String dirPath) {

		File[] fileTab = FileUtils.listFiles(dirPath, new String[]{".xml"}, true);

		// TODO Implement diamond operator when supported
//		List<File> fileList = new ArrayList<>();
		List<File> fileList = new ArrayList<File>();
		for (File file : fileTab) {
			fileList.add(file);
		}

		updateView(dirPath, fileList);
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
