package com.cyrillrx.uremote.common.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cyrillrx.uremote.R;
import com.cyrillrx.uremote.common.model.ActionItem;
import com.cyrillrx.uremote.network.Discoverer;
import com.cyrillrx.uremote.ui.AppLauncherActivity;
import com.cyrillrx.uremote.ui.computer.LoadServerActivity;
import com.cyrillrx.uremote.ui.nao.OpenGLActivity;
import com.cyrillrx.uremote.utils.IntentKeys;
import com.cyrillrx.uremote.utils.NavigationUtils;

import static com.cyrillrx.uremote.utils.IntentKeys.ACTION_LOAD;

/** The view holder is the template for the items of the list. */
public class ActionViewHolder extends RecyclerView.ViewHolder {

    ImageView icon;
    TextView tvTitle;

    public ActionViewHolder(View itemView) {
        super(itemView);

        icon = (ImageView) itemView.findViewById(R.id.iv_action_icon);
        tvTitle = (TextView) itemView.findViewById(R.id.tv_action_title);
    }

    public void bind(final ActionItem action) {

        icon.setImageResource(action.getImageResource());
        tvTitle.setText(action.getTitle());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onItemClick(v.getContext(), action.getId()); }
        });
    }

    private void onItemClick(Context context, int actionId) {

        final Activity activity = (Activity) context;

        switch (actionId) {

            case ActionItem.ACTION_COMPUTER:
                NavigationUtils.startServerList(activity);
                break;

            case ActionItem.ACTION_EXPLORER:
                final Intent loadIntent = new Intent(activity, LoadServerActivity.class);
                loadIntent.setAction(ACTION_LOAD);
                loadIntent.putExtra(IntentKeys.DIRECTORY_PATH, Environment.getExternalStorageDirectory().getPath());
                activity.startActivity(loadIntent);
                break;

            case ActionItem.ACTION_LIGHTS:
                Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.msg_light_control_not_available), Toast.LENGTH_SHORT).show();
                Discoverer.discoverDevices();
                break;

            case ActionItem.ACTION_TV:
                NavigationUtils.startTvRemote(activity);
                break;

            case ActionItem.ACTION_ROBOTS:
                NavigationUtils.startRobotControl(activity);
                break;

            case ActionItem.ACTION_HIFI:
                Toast.makeText(activity, activity.getString(R.string.msg_hifi_control_not_available), Toast.LENGTH_SHORT).show();
                NavigationUtils.startHexActivity(activity);
                break;

            case ActionItem.ACTION_APP:
                activity.startActivityForResult(new Intent(activity.getApplicationContext(), AppLauncherActivity.class), NavigationUtils.RC_APP_LAUNCHER);
                break;

            case ActionItem.ACTION_3D_CUBE:
                activity.startActivity(new Intent(activity.getApplicationContext(), OpenGLActivity.class));
                break;

            default:
                break;
        }
    }
}
