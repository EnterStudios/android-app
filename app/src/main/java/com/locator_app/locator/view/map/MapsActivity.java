package com.locator_app.locator.view.map;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.locator_app.locator.LocatorApplication;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.SchoenHierController;
import com.locator_app.locator.util.CacheImageLoader;
import com.locator_app.locator.util.GpsService;
import com.locator_app.locator.view.bubble.BubbleView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private Bitmap currentPos;
    private Bitmap locationIcon;
    private MapsController mapsController = new MapsController(this);

    @Bind(R.id.schoenHierButton)
    BubbleView schoenHierButton;

    GpsService gpsService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        String urlCurrentPos = "drawable://" + R.drawable.profile;
        String urlLocation   = "drawable://" + R.drawable.location_auf_map;
        CacheImageLoader.getInstance().loadAsync(urlCurrentPos).subscribe(
                (bitmap -> {
                    currentPos = Bitmap.createScaledBitmap(bitmap, 60, 60, false);
                }),
                (error -> {})
        );
        CacheImageLoader.getInstance().loadAsync(urlLocation).subscribe(
                (bitmap -> {
                    locationIcon = Bitmap.createScaledBitmap(bitmap, 70, 70, false);
                }),
                (error -> {})
        );

        schoenHierButton.loadImage("drawable://" + R.drawable.schoenhier);

        gpsService = (GpsService) getSupportFragmentManager()
                .findFragmentById(R.id.gpsService);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @OnClick(R.id.schoenHierButton)
    void onschoenHierButtonClick() {
        SchoenHierController.getInstance().markCurPosAsSchoenHier(gpsService);
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        gpsService.getCurLocationOnGUIThread(this::initiateMap);
    }

    Marker personMarker;

    private void initiateMap(android.location.Location location) {
        googleMap.setOnCameraChangeListener( cameraPosition -> {
            mapsController.drawLocationsAt(cameraPosition.target);
        });

        LatLng locationPos = new LatLng(location.getLatitude(), location.getLongitude());

        BitmapDescriptor currentPosDesc = BitmapDescriptorFactory.fromBitmap(currentPos);
        personMarker = googleMap.addMarker(new MarkerOptions().position(locationPos)
                .icon(currentPosDesc)
                .anchor((float) 0.5, (float) 0.5));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationPos, 15));
        mapsController.addHeatMap(location.getLongitude(), location.getLatitude());
    }

    private void setPersonPosition(android.location.Location location) {
        personMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    Subscription continuousLocation;

    @Override
    public void onResume() {
        super.onResume();
        continuousLocation = gpsService.getContinuousCurLocation()
                                            .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setPersonPosition);
    }

    @Override
    public void onPause() {
        super.onPause();
        continuousLocation.unsubscribe();
    }


    public void drawLocation(double lon, double lat) {
        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(locationIcon))
                .anchor(0.5f, 0.5f)
                .position(new LatLng(lat, lon)));
    }

    private HeatmapTileProvider heatmapTileProvider;
    private TileOverlay tileOverlay;

    static private int[] colors = {
            Color.rgb(0, 255, 0),
            Color.rgb(255, 160, 0)
    };

    static private float[] startPoints = {
            (float) 0.1,
            (float) 0.8
    };

    public void drawHeatMap(List<LatLng> heatPoints) {
        if (heatPoints.isEmpty()) {
            return;
        }

        Gradient gradient = new Gradient(colors, startPoints);

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        heatmapTileProvider = new HeatmapTileProvider.Builder()
                .data(heatPoints)
                .radius(50)
//                .opacity(0.6)
//                .gradient(gradient)
                .build();
        try {
            tileOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(heatmapTileProvider));
        } catch(OutOfMemoryError e) {
        }
    }
}