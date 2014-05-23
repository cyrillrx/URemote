package org.es.uremote;

import android.app.Activity;
import android.os.Bundle;

import org.es.components.HexagonGridView;
import org.es.uremote.objects.ActionItem;

import java.util.List;

/**
 * The dashboard class that leads everywhere in the application.
 * <p/>
 * Created by Cyril Leroux on 05/12/13.
 */
public class HexHomeActivity extends Activity {

    private static final int ACTION_COMPUTER = 0;
    private static final int ACTION_NAO = 1;
    private static final int ACTION_LIGHTS = 2;
    private static final int ACTION_TV = 3;
    private static final int ACTION_ROBOTS = 4;
    private static final int ACTION_HIFI = 5;

    private List<ActionItem> mActionList;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HexagonGridView view = new HexagonGridView(this, null);
        setContentView(view);
    }

//    private void initActionList() {
//        if (mActionList != null) {
//            return;
//        }
//        mActionList = new ArrayList<>(6);
//        mActionList.add(ACTION_COMPUTER, new ActionItem(getString(R.string.title_computer), R.drawable.home_computer));
//        mActionList.add(ACTION_NAO, new ActionItem(getString(R.string.title_nao), R.drawable.home_nao));
//        mActionList.add(ACTION_LIGHTS, new ActionItem(getString(R.string.title_lights), R.drawable.home_light));
//        mActionList.add(ACTION_TV, new ActionItem(getString(R.string.title_tv), R.drawable.home_tv));
//        mActionList.add(ACTION_ROBOTS, new ActionItem(getString(R.string.title_robots), R.drawable.home_robot));
//        mActionList.add(ACTION_HIFI, new ActionItem(getString(R.string.title_hifi), R.drawable.home_hifi));
//    }
}