package com.cyrillrx.uremote.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cyrillrx.uremote.common.model.ActionItem;
import com.cyrillrx.uremote.component.HexagonGridView;

import java.util.List;

/**
 * The dashboard class that leads everywhere in the application.
 *
 * @author Cyril Leroux
 *         Created on 05/12/13.
 */
public class HexHomeActivity extends AppCompatActivity {

    private static final int ACTION_COMPUTER = 0;
    private static final int ACTION_NAO = 1;
    private static final int ACTION_LIGHTS = 2;
    private static final int ACTION_TV = 3;
    private static final int ACTION_ROBOTS = 4;
    private static final int ACTION_HIFI = 5;

    private List<ActionItem> actionList;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HexagonGridView view = new HexagonGridView(this, null);
        setContentView(view);
    }

//    private void initActionList() {
//        if (actionList != null) {
//            return;
//        }
//        actionList = new ArrayList<>(6);
//        actionList.add(ACTION_COMPUTER, new ActionItem(getString(R.string.title_computer), R.drawable.home_computer));
//        actionList.add(ACTION_NAO, new ActionItem(getString(R.string.title_nao), R.drawable.home_nao));
//        actionList.add(ACTION_LIGHTS, new ActionItem(getString(R.string.title_lights), R.drawable.home_light));
//        actionList.add(ACTION_TV, new ActionItem(getString(R.string.title_tv), R.drawable.home_tv));
//        actionList.add(ACTION_ROBOTS, new ActionItem(getString(R.string.title_robots), R.drawable.home_robot));
//        actionList.add(ACTION_HIFI, new ActionItem(getString(R.string.title_hifi), R.drawable.home_hifi));
//    }
}