package com.locator_app.locator.service.locations;

import android.util.Log;

import com.locator_app.locator.model.LocatorLocation;
import com.locator_app.locator.service.Api;
import com.locator_app.locator.service.ServiceFactory;

import java.net.UnknownHostException;

import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class LocationsApiService {

    public interface LocationsApi {

        @POST(Api.version + "/locations")
        Observable<Response<PostLocationResponse>> postLocation(@Body PostLocationRequest request);

        @GET(Api.version + "/locations/{locationId}")
        Observable<Response<LocatorLocation>> locationById(@Path("locationId") String locationId);

        @GET(Api.version + "/locations/nearby")
        Observable<Response<LocationsNearbyResponse>> locationsNearby(@Query("long") double lon,
                                                                      @Query("lat") double lat,
                                                                      @Query("maxDistance") double maxDistance,
                                                                      @Query("limit") int limit);
    }

    LocationsApi service = ServiceFactory.createService(LocationsApi.class);

    public Observable<LocatorLocation> getLocationById(String locationId) {
        return service.locationById(locationId)
                .doOnError(this::handleGetLocationByIdError)
                .onErrorResumeNext(throwable -> {
                    return Observable.error(new Exception("uuuuups, sorry :-/"));
                })
                .flatMap(this::parseLocatorLocationResponse);

    }

    private Observable<LocatorLocation> parseLocatorLocationResponse(Response response) {
        Log.d("LocationsApiService", "na dann mal los ...");
        if (response.isSuccess()) {
            if (response.body() != null) {
                return Observable.just((LocatorLocation)response.body());
            }
            return Observable.error(new Error("no such location... :-("));
        }
        return Observable.error(new Error("http-error: " + response.code()));
    }

    private void handleGetLocationByIdError(Throwable throwable) {
        if (throwable instanceof UnknownHostException) {
            Log.d("LocationApiService", "Internet aus oder schläft der Locator-Server?");
        }
    }


    public Observable<LocationsNearbyResponse> getLocationsNearby(double lon, double lat,
                                                                  double maxRadius, int limit) {
        return service.locationsNearby(lon, lat, maxRadius, limit)
                .flatMap(this::parseLocationsNearbyResponse);
    }

    private Observable<LocationsNearbyResponse> parseLocationsNearbyResponse(Response response) {
        if (response.isSuccess()) {
            return Observable.just((LocationsNearbyResponse)response.body());
        }
        return Observable.error(new Exception("http-code: " + Integer.toString(response.code())));
    }
}
