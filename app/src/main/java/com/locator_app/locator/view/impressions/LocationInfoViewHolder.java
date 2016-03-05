package com.locator_app.locator.view.impressions;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.locator_app.locator.R;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.model.impressions.AbstractImpression;
import com.locator_app.locator.view.map.MapsActivity;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

class LocationInfoViewHolder extends ImpressionViewHolder {

    private ImpressionRecyclerViewAdapter impressionRecyclerViewAdapter;
    TextView locatorName;
    TextView distance;
    TextView city;
    TextView favorites;
    ImageView goToHeatmap;
    Activity activity;

    public LocationInfoViewHolder(ImpressionRecyclerViewAdapter impressionRecyclerViewAdapter,
                                  View itemView, Activity activity) {
        super(itemView);
        this.impressionRecyclerViewAdapter = impressionRecyclerViewAdapter;
        this.activity = activity;
        locatorName = (TextView) itemView.findViewById(R.id.locatorName);
        UserController.getInstance().getUser(impressionRecyclerViewAdapter.location.userId)
                .subscribe(
                        (user) -> {
                            locatorName.setText(user.name);
                        },
                        (err) -> {
                            locatorName.setText("(unknown)");
                        }
                );
        city = (TextView) itemView.findViewById(R.id.city);
        city.setText(impressionRecyclerViewAdapter.location.city.title);
        distance = (TextView) itemView.findViewById(R.id.distance);
        distance.setText("");
        favorites = (TextView) itemView.findViewById(R.id.favorites);
        favorites.setText(Integer.toString(impressionRecyclerViewAdapter.location.favorites.size()));
        goToHeatmap = (ImageView) itemView.findViewById(R.id.heatmap);

        goToHeatmap.setOnClickListener(v -> {
            Intent intent = new Intent(activity, MapsActivity.class);
            intent.putExtra("lon", impressionRecyclerViewAdapter.location.geoTag.getLongitude());
            intent.putExtra("lat", impressionRecyclerViewAdapter.location.geoTag.getLatitude());
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
        });
    }

    @Override
    public void bind(AbstractImpression impression) {
    }

    public void updateFavorCounter() {
        favorites.setText(Integer.toString(impressionRecyclerViewAdapter.location.favorites.size()));
    }
}
