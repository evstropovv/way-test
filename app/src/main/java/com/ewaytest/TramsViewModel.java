package com.ewaytest;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.ewaytest.domain.RoutesInteractor;
import com.ewaytest.models.routelist.Route;
import com.ewaytest.models.vehicle.RoutesGPS;
import com.ewaytest.models.vehicle.Vehicle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.gson.Gson;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Timer;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class TramsViewModel extends ViewModel {

    @Inject
    RoutesInteractor routesInteractor;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private List<Route> tramsRoutes;

    private VisibleRegion visibleRegion;

    private Timer timer;

    //key - id of route , value - GPS coordinates

    private MutableLiveData<HashMap<String, List<Vehicle>>> tramsWithGps;
    private HashMap<String, List<Vehicle>> mapTramsWithGps;

    public TramsViewModel() {
        App.getComponent().inject(this);
        tramsWithGps = new MutableLiveData<>();
        mapTramsWithGps = new HashMap<>();
    }

    public void getRoutesList() {
        //get routes list and filtering trams with gps
        Disposable disposable = routesInteractor.getRoutes()
                .map(data -> data.getRoutesList().getRoute())
                .flatMap((Function<List<Route>, Publisher<Route>>) routes -> Flowable.fromIterable(routes))
                .filter(route -> (route.getHasGps() == 1) && (route.getTransport().equals("tram")))
                .toList()
                .subscribeOn(Schedulers.io())
                .subscribe(routeList -> {

                    tramsRoutes = routeList;
                    loadTramsWithGps(routeList);

                }, throwable -> {

                });
        disposables.add(disposable);
    }

    private void loadTramsWithGps(List<Route> routeList) {
        for (int i = 0; i < routeList.size(); i++) {
            int finalI = i;
            Disposable disposable = routesInteractor
                    .getRoutesGps(routeList.get(i).getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(routesGPS -> {
                        mapTramsWithGps.put(routeList.get(finalI).getId(), routesGPS.getVehicle());
                        tramsWithGps.postValue(mapTramsWithGps);
//                        if (finalI == routeList.size() - 1)
//                            Log.d("Log.d", new Gson().toJson(tramsWithGps));

                    });
            disposables.add(disposable);
        }
    }


    public LiveData<HashMap<String, List<Vehicle>>> getTramsWithGps(VisibleRegion visibleRegion) {

        return tramsWithGps;
    }


//    //загружаем только трамвайчики из видимого на экране квадрата
//    public LiveData<List<Coordinates>> getTramsInVisible(VisibleRegion visibleRegion) {
//        if (tramsInVisible != null) tramsInVisible = new MutableLiveData<>();
//
//        //пробежаться перебором по getAllLvivTramsCoordiantes и достать только те которые в квадрате
//
//
//        return tramsInVisible;
//    }
//
//    public LiveData<List<Coordinates>> getAllLvivTramsCoordinates(){
//        if (allLvivTrams ==null) allLvivTrams = new MutableLiveData<>();
//
//        //load all lviv trams coordinates
//
//        //можно подключится к Room и от туда брать всю инфу
//
//        return allLvivTrams;
//    }

    public boolean isVisibleOnMap(LatLng latLng) {
        return visibleRegion.latLngBounds.contains(latLng);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.dispose();
    }
}
