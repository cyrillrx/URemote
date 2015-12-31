package com.cyrillrx.uremote.common.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cyrillrx.uremote.R;
import com.cyrillrx.uremote.common.model.ActionItem;
import com.cyrillrx.uremote.network.Discoverer;
import com.cyrillrx.uremote.ui.AppLauncherActivity;
import com.cyrillrx.uremote.ui.HomeActivity;
import com.cyrillrx.uremote.ui.nao.OpenGLActivity;
import com.cyrillrx.uremote.utils.NavigationUtils;

import java.util.List;

import static android.view.HapticFeedbackConstants.VIRTUAL_KEY;

/**
 * Adapter used to display an action list.
 * Used for {@link HomeActivity} activity.
 *
 * @author Cyril Leroux
 *         Created on 05/11/12.
 */
public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ViewHolder> {

    private Activity         mActivity;
    private List<ActionItem> mActionList;

    /** The view holder is the template for the items of the list. */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView  tvTitle;
        TextView  tvSummary;

        public ViewHolder(View itemView) {
            super(itemView);

            icon = (ImageView) itemView.findViewById(R.id.ivActionIcon);
            tvTitle = (TextView) itemView.findViewById(R.id.tvActionTitle);
            tvSummary = (TextView) itemView.findViewById(R.id.tvActionSummary);
        }
    }

    public ActionAdapter(Activity activity, List<ActionItem> actionList) {
        mActivity = activity;
        mActionList = actionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.action_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final ActionItem action = mActionList.get(position);

        holder.icon.setImageResource(action.getImageResource());
        holder.tvTitle.setText(action.getTitle());
        holder.tvSummary.setText(action.getSummary());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemView.performHapticFeedback(VIRTUAL_KEY);
                onItemClick(action.getId());
            }
        });
    }

    @Override
    public int getItemCount() { return mActionList.size(); }

    private void onItemClick(int actionId) {

        switch (actionId) {

            case ActionItem.ACTION_COMPUTER:
                NavigationUtils.startServerList(mActivity);
                break;

            case ActionItem.ACTION_LIGHTS:
                Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.msg_light_control_not_available), Toast.LENGTH_SHORT).show();
                Discoverer.discoverDevices();
                break;

            case ActionItem.ACTION_TV:
                NavigationUtils.startTvRemote(mActivity);
                break;

            case ActionItem.ACTION_ROBOTS:
                NavigationUtils.startRobotControl(mActivity);
                break;

            case ActionItem.ACTION_HIFI:
                Toast.makeText(mActivity, mActivity.getString(R.string.msg_hifi_control_not_available), Toast.LENGTH_SHORT).show();
                NavigationUtils.startHexActivity(mActivity);
                break;

            case ActionItem.ACTION_APP:
                mActivity.startActivityForResult(new Intent(mActivity.getApplicationContext(), AppLauncherActivity.class), NavigationUtils.RC_APP_LAUNCHER);
                break;

            case ActionItem.ACTION_STORE:
                break;

            case ActionItem.ACTION_SHOW_NAO:
                mActivity.startActivity(new Intent(mActivity.getApplicationContext(), OpenGLActivity.class));
                break;

            default:
                break;
        }
    }

}
