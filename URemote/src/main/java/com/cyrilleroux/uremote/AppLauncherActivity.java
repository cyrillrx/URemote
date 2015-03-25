package com.cyrilleroux.uremote;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;

import com.cyrilleroux.uremote.objects.AppItem;
import com.cyrilleroux.uremote.request.protobuf.RemoteCommand.Request.Code;
import com.cyrilleroux.uremote.request.protobuf.RemoteCommand.Request.Type;

import java.util.List;

import static com.cyrilleroux.uremote.utils.IntentKeys.EXTRA_APPLICATION_LIST;
import static com.cyrilleroux.uremote.utils.IntentKeys.REQUEST_CODE;
import static com.cyrilleroux.uremote.utils.IntentKeys.REQUEST_TYPE;

// TODO Make AppLauncherActivity layout dynamic => take an app list as data intent

/**
 * This Activity displays a list of applications that you can launch on the remote server.
 *
 * @author Cyril Leroux
 *         Created before first commit (08/04/12).
 */
public class AppLauncherActivity extends ActionBarActivity {

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

        List<AppItem> appItems = getIntent().getParcelableArrayListExtra(EXTRA_APPLICATION_LIST);

        populateAppGridLayout(gridLayout, appItems);

        setContentView(gridLayout, layoutParams);

        getSupportActionBar().hide();
    }

    private void populateAppGridLayout(final GridLayout gridLayout, List<AppItem> apps) {
        if (apps == null) { return; }

        final ViewGroup.LayoutParams btnLayout = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        for (final AppItem app : apps) {
            final ImageButton imageButton = new ImageButton(this);
            imageButton.setLayoutParams(btnLayout);
            imageButton.setBackground(null);
            imageButton.setPadding(15, 15, 15, 15);
            imageButton.setContentDescription(app.getLabel());
            imageButton.setImageResource(app.getImageResource());
            imageButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO Do something => missing Type and code
                    returnAppMessage(Type.APP, Code.valueOf(app.getAction()));
                }
            });
            gridLayout.addView(imageButton);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.launcher_in, R.anim.launcher_out);
    }

    private void returnAppMessage(Type type, Code code) {
        final Intent data = new Intent();
        data.putExtra(REQUEST_TYPE, type.getNumber());
        data.putExtra(REQUEST_CODE, code.getNumber());
        setResult(RESULT_OK, data);
        finish();
        overridePendingTransition(R.anim.launcher_in, R.anim.launcher_out);
    }
}
