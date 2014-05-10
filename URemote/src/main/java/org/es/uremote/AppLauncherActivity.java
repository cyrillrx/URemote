package org.es.uremote;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;

import org.es.uremote.exchange.Message.Request.Code;
import org.es.uremote.exchange.Message.Request.Type;
import org.es.uremote.objects.AppItem;

import java.util.ArrayList;

import static org.es.uremote.utils.IntentKeys.EXTRA_APPLICATION_LIST;
import static org.es.uremote.utils.IntentKeys.REQUEST_CODE;
import static org.es.uremote.utils.IntentKeys.REQUEST_TYPE;

// TODO Make AppLauncherActivity layout dynamic => take an app list as data intent
/**
 * This Activity displays a list of applications that you can launch on the remote server.
 *
 * @author Cyril Leroux
 * Created before first commit (08/04/12).
 */
public class AppLauncherActivity extends Activity implements OnClickListener {

    private ArrayList<AppItem> mApplications;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.launcher_in, R.anim.launcher_out);

        GridLayout gridLayout = new GridLayout(this);
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        gridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        gridLayout.setBackgroundColor(Color.TRANSPARENT);
        gridLayout.setColumnCount(3);
        gridLayout.setRowCount(5);
        gridLayout.setOrientation(GridLayout.HORIZONTAL);

        mApplications = getIntent().getParcelableArrayListExtra(EXTRA_APPLICATION_LIST);

        populateAppGridLayout(gridLayout, mApplications);

        final ViewGroup.LayoutParams btnLayout = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageButton ibStart = new ImageButton(this);
        ibStart.setLayoutParams(btnLayout);
        ibStart.setBackground(null);
        ibStart.setPadding(15, 15, 15, 15);
        ibStart.setContentDescription(getString(R.string.cmd_gom_start));
        ibStart.setImageResource(R.drawable.app_gom_player);
        ibStart.setId(R.id.btnAppGomPlayer);
        ibStart.setOnClickListener(this);
        gridLayout.addView(ibStart);

        ImageButton ibStop = new ImageButton(this);
        ibStop.setLayoutParams(btnLayout);
        ibStop.setBackground(null);
        ibStop.setPadding(15, 15, 15, 15);
        ibStop.setContentDescription(getString(R.string.cmd_gom_start));
        ibStop.setImageResource(R.drawable.app_gom_player);
        ibStop.setId(R.id.btnKillGomPlayer);
        ibStop.setOnClickListener(this);
        gridLayout.addView(ibStop);

        setContentView(gridLayout, layoutParams);
	}

    private void populateAppGridLayout(final GridLayout gridLayout, ArrayList<AppItem> apps) {
        if (apps == null) { return; }

        final ViewGroup.LayoutParams btnLayout = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        for (AppItem app : apps) {
            ImageButton imageButton = new ImageButton(this);
            imageButton.setLayoutParams(btnLayout);
            imageButton.setBackground(null);
            imageButton.setPadding(15, 15, 15, 15);
            imageButton.setContentDescription(app.getLabel());
            imageButton.setImageResource(app.getImageResource());
            imageButton.setOnClickListener(this);
            imageButton.setId(((Object) app).hashCode()); // TODO How to define id
            gridLayout.addView(imageButton);
        }
    }

	@Override
	public void onClick(View v) {

        final int id = v.getId();

        // TODO How to catch the right id
//        for (AppItem app : mApplications) {
//            if (id == ((Object)app).hashCode()) {
//                // Do something => missing Type and code
//            }
//        }

		switch (id) {
			case R.id.btnAppGomPlayer:
				returnAppMessage(Type.APP, Code.ON);
				break;

			case R.id.btnKillGomPlayer:
				returnAppMessage(Type.APP, Code.OFF);
				break;

			default:
				break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.launcher_in, R.anim.launcher_out);
	}

	private void returnAppMessage(Type type, Code code) {
		Intent data = new Intent();
		data.putExtra(REQUEST_TYPE, type.getNumber());
		data.putExtra(REQUEST_CODE, code.getNumber());
		setResult(RESULT_OK, data);
		finish();
		overridePendingTransition(R.anim.launcher_in, R.anim.launcher_out);
	}
}
