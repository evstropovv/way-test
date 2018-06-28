package com.ewaytest;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.ewaytest.domain.RoutesInteractor;
import com.ewaytest.models.routelist.Route;
import com.ewaytest.models.vehicle.Vehicle;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class TramsViewModel extends ViewModel {

    @Inject
    RoutesInteractor routesInteractor;

    private final int PERIOD_REQUEST = 20 * 1000; //20 sec //TODO

    private final CompositeDisposable disposables = new CompositeDisposable();

    private List<Route> tramsRoutes;

    private Timer timer;

    private CameraPosition cameraPosition;

    //key - id of route , value - GPS coordinates

    private MutableLiveData<HashMap<String, List<Vehicle>>> tramsWithGps;
    private HashMap<String, List<Vehicle>> mapTramsWithGps;


    public TramsViewModel() {
        App.getComponent().inject(this);
        tramsWithGps = new MutableLiveData<>();
        mapTramsWithGps = new HashMap<>();
        timer = new Timer();
    }

    private void getRoutesList() {
        //get routes list and filtering trams with gps
        Disposable disposable = routesInteractor.getRoutes()
                .map(data -> data.getRoutesList().getRoute())
                .flatMap((Function<List<Route>, Publisher<Route>>) Flowable::fromIterable)
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
        //циклично раз в 20 сек загружаем GPS данные ВСЕХ травмаев
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mapTramsWithGps = new HashMap<>();

                for (int i = 0; i < routeList.size(); i++) {
                    int finalI = i;
                    Disposable disposable = routesInteractor
                            .getRoutesGps(routeList.get(i).getId())
                            .subscribeOn(Schedulers.io())
                            .subscribe(routesGPS -> {
                                mapTramsWithGps.put(routeList.get(finalI).getId(), routesGPS.getVehicle());
                                if (finalI == (routeList.size() - 1))
                                    tramsWithGps.postValue(mapTramsWithGps);
                            });
                    disposables.add(disposable);
                }

            }
        }, 0, PERIOD_REQUEST);
    }

//
//    public void setNewVisibleRegion(VisibleRegion visibleRegion) {
//        this.visibleRegion = visibleRegion;
//    //    updateTramsGpsInVisible();
//    }

    public LiveData<HashMap<String, List<Vehicle>>> getTramsWithGpsInVisible() {
        if (tramsRoutes == null) getRoutesList();
        return tramsWithGps;
    }

    public void setCameraPosition(CameraPosition cameraPosition) {
        this.cameraPosition = cameraPosition;
    }

    public CameraPosition getCameraPosition() {
        return cameraPosition;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.dispose();
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }
}
