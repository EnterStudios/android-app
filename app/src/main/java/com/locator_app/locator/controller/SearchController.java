package com.locator_app.locator.controller;

import com.locator_app.locator.apiservice.search.SearchApiService;
import com.locator_app.locator.model.LocatorLocation;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchController {
    SearchApiService searchService;

    public Observable<List<LocatorLocation>> search(double lon, double lat) {
        return searchService.search(lon, lat)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<LocatorLocation>> searchString(String searchString,
                                                          double lon, double lat) {
        return searchService.searchString(searchString, lon, lat)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static SearchController instance;
    public static SearchController getInstance() {
        if (instance == null) {
            instance = new SearchController();
        }
        return instance;
    }
    private SearchController() { searchService = new SearchApiService(); }
}
