package com.cyrillrx.uremote.common.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyrillrx.uremote.R;
import com.cyrillrx.uremote.common.model.ActionItem;
import com.cyrillrx.uremote.ui.HomeActivity;

import java.util.List;

/**
 * Adapter used to display an action list.
 * Used for {@link HomeActivity} activity.
 *
 * @author Cyril Leroux
 *         Created on 05/11/12.
 */
public class ActionAdapter extends RecyclerView.Adapter<ActionViewHolder> {

    private List<ActionItem> actions;

    public ActionAdapter(List<ActionItem> actions) { this.actions = actions; }

    @NonNull
    @Override
    public ActionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_action, parent, false);
        return new ActionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ActionViewHolder holder, int position) {
        holder.bind(actions.get(position));
    }

    @Override
    public int getItemCount() { return actions.size(); }

}
