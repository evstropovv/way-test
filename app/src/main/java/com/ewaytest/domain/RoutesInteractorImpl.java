package com.ewaytest.domain;

import com.ewaytest.models.routelist.RouteList;
import com.ewaytest.models.todisplay.RouteToDisplay;
import com.ewaytest.models.vehicle.RoutesGPS;
import com.ewaytest.rest.Webservice;

import io.reactivex.Flowable;
import io.reactivex.Single;
import retrofit2.Call;

public class RoutesInteractorImpl implements RoutesInteractor {

    Webservice webservice;

    public RoutesInteractorImpl(Webservice webservice) {
        this.webservice = webservice;
    }

    @Override
    public Flowable<RouteList> getRoutes() {
        return webservice.getRoutesList("cities.GetRoutesList");
    }

    @Override
    public Single<RoutesGPS> getRoutesGps(String id) {
        return webservice.getVehicleGps("routes.GetRouteGPS", id);
    }

   @Override
    public Single<RouteToDisplay> getRouteToDisplay(String id, String startPosition, String stopPosition) {
        return webservice.getRouteToDisplay("routes.GetRouteToDisplay", id, startPosition, stopPosition);
    }
}
