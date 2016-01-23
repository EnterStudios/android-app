package com.locator_app.locator.view.recyclerviewadapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.locator_app.locator.R;
import com.locator_app.locator.model.User;
import com.locator_app.locator.view.bubble.BubbleView;
import com.locator_app.locator.view.profile.ProfileActivity;

import java.util.LinkedList;
import java.util.List;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder> {

    List<User> users = new LinkedList<>();

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @Override
    public UserRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.default_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserRecyclerViewAdapter.ViewHolder holder, int position) {
        final User user = users.get(position);
        holder.update(user);
        holder.view.setOnClickListener(v -> {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("profile", user);
                    context.startActivity(intent);
                }
        );
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends LocationRecyclerViewAdapter.ViewHolder {

        public final View view;
        public final TextView name;
        public final TextView description;
        public final BubbleView bubbleView;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            name = (TextView) view.findViewById(R.id.text);
            description = (TextView) view.findViewById(R.id.description);
            bubbleView = (BubbleView) view.findViewById(R.id.bubbleView);

            TextView bubbleInfo = (TextView) view.findViewById(R.id.bubble_info);
            bubbleInfo.setVisibility(View.GONE);
        }

        public void update(User user) {
            title.setText(user.name);
            description.setText(user.description);
            bubbleView.loadImage(user.thumbnailUri());
        }
    }
}