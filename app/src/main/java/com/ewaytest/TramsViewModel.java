package com.ewaytest;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.ewaytest.domain.RoutesInteractor;
import com.ewaytest.models.routelist.Route;
import com.ewaytest.models.vehicle.Vehicle;
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

    private VisibleRegion visibleRegion;

    private Timer timer;

    //key - id of route , value - GPS coordinates

    private MutableLiveData<HashMap<String, List<Vehicle>>> tramsWithGps;
    private HashMap<String, List<Vehicle>> mapTramsWithGps;

    private MutableLiveData<HashMap<String, List<Vehicle>>> tramsWithGpsVisible;


    public TramsViewModel() {
        App.getComponent().inject(this);
        tramsWithGps = new MutableLiveData<>();
        mapTramsWithGps = new HashMap<>();
        timer = new Timer();
        tramsWithGpsVisible = new MutableLiveData<>();
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
                mapTramsWithGps.clear();
                for (int i = 0; i < routeList.size(); i++) {
                    int finalI = i;
                    Disposable disposable = routesInteractor
                            .getRoutesGps(routeList.get(i).getId())
                            .subscribeOn(Schedulers.io())
                            .subscribe(routesGPS -> {
                                mapTramsWithGps.put(routeList.get(finalI).getId(), routesGPS.getVehicle());
                                tramsWithGps.postValue(mapTramsWithGps);
                                // Log.d("Log.d", "update" + tramsWithGps.getValue().size()+"");
                            });
                    disposables.add(disposable);
                }
                updateTramsGpsInVisible();
            }
        }, 0, PERIOD_REQUEST);
    }


    public void setNewVisibleRegion(VisibleRegion visibleRegion) {
        this.visibleRegion = visibleRegion;
        updateTramsGpsInVisible();
    }

    public LiveData<HashMap<String, List<Vehicle>>> getTramsWithGpsInVisible() {
        if (tramsRoutes == null) getRoutesList();
        return tramsWithGpsVisible;
    }

    private void updateTramsGpsInVisible() {
        //отбор всех видимых трамвайчиков
        HashMap<String, List<Vehicle>> mapTramsWithGpsVisible = new HashMap<>();
        for (Map.Entry<String, List<Vehicle>> entry : mapTramsWithGps.entrySet()) {
            List<Vehicle> vehicles = new ArrayList<>();
            for (int i = 0; i < entry.getValue().size(); i++) {
                if (isVisibleOnMap(new LatLng(entry.getValue().get(i).getLat(), entry.getValue().get(i).getLng()))) {
                    vehicles.add(entry.getValue().get(i));
                }
            }
            mapTramsWithGpsVisible.put(entry.getKey(), vehicles);
        }
        tramsWithGpsVisible.postValue(mapTramsWithGpsVisible);
    }

    private boolean isVisibleOnMap(LatLng latLng) {
        try {
            return visibleRegion.latLngBounds.contains(latLng);
        } catch (NullPointerException e) {
            return false;
        }
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
