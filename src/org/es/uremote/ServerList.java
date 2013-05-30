package org.es.uremote;

import java.util.ArrayList;
import java.util.List;

import org.es.uremote.objects.ServerInfo;

import android.app.ListActivity;
import android.os.Bundle;

/**
 * @author Cyril Leroux
 */
public class ServerList extends ListActivity {

	List<ServerInfo> mServers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server_list);
		mServers = new ArrayList<ServerInfo>();
	}
}