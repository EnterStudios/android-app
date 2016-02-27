package com.locator_app.locator.view.locationcreation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.locator_app.locator.R;
import com.locator_app.locator.model.GoogleLocation;
import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.util.GpsService;
import com.locator_app.locator.view.HomeActivity;
import com.locator_app.locator.view.LoadingSpinner;
import com.locator_app.locator.view.fragments.SearchResultsFragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class ChooseCategories extends Activity {
    Bundle extras;

    @Bind(R.id.nature)
    LinearLayout nature;
    @Bind(R.id.nightlife)
    LinearLayout nightlife;
    @Bind(R.id.culture)
    LinearLayout culture;
    @Bind(R.id.secret)
    LinearLayout secret;
    @Bind(R.id.gastro)
    LinearLayout gastro;
    @Bind(R.id.holiday)
    LinearLayout holiday;

    @Bind(R.id.cancelButton)
    ImageView cancelButton;

    private static final String NATURE_ID    = "nature";
    private static final String NIGHTLIFE_ID = "party";
    private static final String CULTURE_ID   = "culture";
    private static final String SECRET_ID    = "secret";
    private static final String GASTRO_ID    = "gastro";
    private static final String HOLIDAY_ID   = "holiday";

    @Bind(R.id.next)
    ImageView next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_categories);
        ButterKnife.bind(this);

        extras = getIntent().getExtras();
    }

    @OnClick(R.id.nature)
    void onNatureClicked()    { toggleCategory(nature, NATURE_ID); }
    @OnClick(R.id.nightlife)
    void onNightlifeClicked() { toggleCategory(nightlife, NIGHTLIFE_ID); }
    @OnClick(R.id.culture)
    void onCultureClicked()   { toggleCategory(culture, CULTURE_ID); }
    @OnClick(R.id.secret)
    void onSecretClicked()    { toggleCategory(secret, SECRET_ID); }
    @OnClick(R.id.gastro)
    void onGastroClicked()    { toggleCategory(gastro, GASTRO_ID); }
    @OnClick(R.id.holiday)
    void onHolidayClicked()   { toggleCategory(holiday, HOLIDAY_ID); }

    private ArrayList<CharSequence> selectedCategories = new ArrayList<>();
    private static final int MAX_CATEGORIES = 3;

    synchronized
    private void toggleCategory(View v, String category) {
        if (selectedCategories.contains(category)) {
            selectedCategories.remove(category);
            toggleAlpha(v);
        } else if (selectedCategories.size() < MAX_CATEGORIES){
            selectedCategories.add(category);
            toggleAlpha(v);
        }

        if (selectedCategories.size() == 1) {
            next.setAlpha((float) 1);
        } else if (selectedCategories.size() == 0) {
            next.setAlpha((float) 0.3);
        }
    }

    private void toggleAlpha(View v) {
        if (v.getAlpha() == 1) {
            v.setAlpha((float) 0.3);
        } else {
            v.setAlpha((float) 1);
        }
    }

    @OnClick(R.id.next)
    void onNextClicked() {
        if (selectedCategories.size() > 0) {
//            Intent intent = new Intent(this, NameLocation.class);
//            intent.putExtras(extras);
//            intent.putCharSequenceArrayListExtra("categories", selectedCategories);
//            startActivity(intent);
            LoadingSpinner.showSpinner(this);
            cancelButton.setVisibility(View.GONE);
            //upload location
        }
    }

    @OnClick(R.id.cancelButton)
    void onCancelButtonClicked() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}