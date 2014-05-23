package org.es.uremote;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import org.es.uremote.components.ActionArrayAdapter;
import org.es.uremote.nao.OpenGLActivity;
import org.es.uremote.objects.ActionItem;

import java.util.ArrayList;
import java.util.List;

import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;

/**
 * Created by Cyril Leroux on 08/11/13.
 */
public class NaoActivity extends ListActivity implements OnItemClickListener {

    private static final int ACTION_APP      = 0;
    private static final int ACTION_STORE    = 1;
    private static final int ACTION_SHOW_NAO = 2;

    /** ActivityForResults request code */
    private static final int RC_APP_LAUNCHER = 0;

    private List<ActionItem> mActionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nao);

        initActionList();

        final int colorRes1 = getResources().getColor(android.R.color.secondary_text_light);
        final int colorRes2 = getResources().getColor(android.R.color.secondary_text_light);
        final ActionArrayAdapter adapter = new ActionArrayAdapter(getApplicationContext(), mActionList, colorRes1, colorRes2, null);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    private void initActionList() {
        if (mActionList != null) {
            return;
        }
        mActionList = new ArrayList<>(2);
        mActionList.add(ACTION_APP,      new ActionItem(getString(R.string.title_app_list),  R.drawable.home_nao));
        mActionList.add(ACTION_STORE,    new ActionItem(getString(R.string.title_app_store), R.drawable.nao_app_store));
        mActionList.add(ACTION_SHOW_NAO, new ActionItem(getString(R.string.title_show_nao), R.drawable.home_nao));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        view.performHapticFeedback(VIRTUAL_KEY);
        switch (position) {

            case ACTION_APP:
                startActivityForResult(new Intent(getApplicationContext(), AppLauncherActivity.class), RC_APP_LAUNCHER);
                break;

            case ACTION_STORE:
                break;

            case ACTION_SHOW_NAO:
                startActivity(new Intent(getApplicationContext(), OpenGLActivity.class));
                break;

            default:
                break;
        }
    }
}
