package com.ewaytest.domain;

import com.ewaytest.models.routelist.RouteList;
import com.ewaytest.models.todisplay.RouteToDisplay;
import com.ewaytest.models.vehicle.RoutesGPS;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;

public interface RoutesInteractor {

    Flowable<RouteList> getRoutes();

    Single<RoutesGPS> getRoutesGps(String id);

    Single<RouteToDisplay> getRouteToDisplay(String id, String startPosition, String stopPosition);
}