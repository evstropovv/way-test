package com.ewaytest.rest;

import com.ewaytest.models.routelist.RouteList;
import com.ewaytest.models.todisplay.RouteToDisplay;
import com.ewaytest.models.vehicle.RoutesGPS;

import io.reactivex.Flowable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Webservice {

    @GET("/")
    Flowable<RouteList> getRoutesList(@Query("function") String sort);

    @GET("/")
    Single<RoutesGPS> getVehicleGps(@Query("function") String function, @Query("id") String transportId);

    @GET("/")
    Single<RouteToDisplay> getRouteToDisplay(@Query("function") String function, @Query("id") String transportId,
                                             @Query("start_position") String startPosition, @Query("stop_position") String stopPosition);
}
