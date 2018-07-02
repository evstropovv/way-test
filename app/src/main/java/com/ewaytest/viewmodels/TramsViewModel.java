package com.ewaytest.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Looper;
import android.util.Log;
import com.ewaytest.App;
import com.ewaytest.domain.RoutesInteractor;
import com.ewaytest.models.routelist.Route;
import com.ewaytest.models.todisplay.RouteToDisplay;
import com.ewaytest.models.vehicle.Vehicle;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.gson.Gson;
import org.reactivestreams.Publisher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class TramsViewModel extends ViewModel {

    @Inject
    RoutesInteractor routesInteractor;

    private final int PERIOD_REQUEST = 20 * 1000; //20 sec //TODO

    private final CompositeDisposable disposables = new CompositeDisposable();

    Disposable disposable;

    private boolean isRouteShowing = false;

    private List<Route> tramsRoutes;

    private Timer timer;

    private CameraPosition cameraPosition;

    //key - id of route , value - GPS coordinates

    private MutableLiveData<HashMap<String, HashSet<Vehicle>>> tramsWithGps;

    private MutableLiveData<RouteToDisplay> route;

    private HashMap<String, HashSet<Vehicle>> mapTramsWithGps;

    private List<String> visibleRouteId;

    public TramsViewModel() {
        App.getComponent().inject(this);
        tramsWithGps = new MutableLiveData<>();
        route = new MutableLiveData<>();
        mapTramsWithGps = new HashMap<>();
        visibleRouteId = new ArrayList<>();
        timer = new Timer();
    }

    private void getRoutesList() {
        Log.d("Log.d", "isMainThread - " + (Looper.myLooper() == Looper.getMainLooper()));

        //get routes list and filtering trams with gps
        Disposable routesDisposable = routesInteractor.getRoutes()
                .subscribeOn(Schedulers.io())
                .map(data -> data.getRoutesList().getRoute())
                .flatMap((Function<List<Route>, Publisher<Route>>) Flowable::fromIterable)
                .filter(route -> (route.getHasGps() == 1) && (route.getTransport().equals("tram")))
                .toList()
                .subscribe(routeList -> {
                    Log.d("Log.d", "routeList" + new Gson().toJson(routeList));
                    tramsRoutes = routeList;
                    loadTramsWithGps(routeList);
                }, throwable -> {
                });
        disposables.add(routesDisposable);
    }


    private void loadTramsWithGps(List<Route> routeList) {
        //циклично раз в 20 сек загружаем GPS трамваев
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
//                mapTramsWithGps = new HashMap<>();
                if (visibleRouteId.size() > 0) {
                    for (int i = 0; i < visibleRouteId.size(); i++) {
                        int finalI = i;
                        disposable = routesInteractor
                                .getRoutesGps(visibleRouteId.get(i))
                                .subscribeOn(Schedulers.io())
                                .subscribe(routesGPS -> {
                                    try {  //во время поворота экрана может измениться количество видимых авто
                                        HashSet<Vehicle> vehicleSet = new HashSet<>(routesGPS.getVehicle());
                                        mapTramsWithGps.put(visibleRouteId.get(finalI), vehicleSet);
                                        if (finalI == (visibleRouteId.size() - 1))
                                            tramsWithGps.postValue(mapTramsWithGps);
                                    } catch (Exception e) {
                                    }
                                });
                        disposables.add(disposable);
                    }
                } else {
                    for (int i = 0; i < routeList.size(); i++) {
                        int finalI = i;
                        disposable = routesInteractor
                                .getRoutesGps(routeList.get(i).getId())
                                .subscribeOn(Schedulers.io())
                                .subscribe(routesGPS -> {
                                    try {  //во время поворота экрана может измениться количество видимых авто
                                        HashSet<Vehicle> vehicleSet = new HashSet<>(routesGPS.getVehicle());
                                        String key = routeList.get(finalI).getId();
                                        mapTramsWithGps.put(key, vehicleSet);
                                        if (finalI == (routeList.size() - 1))
                                            tramsWithGps.postValue(mapTramsWithGps);
                                    } catch (Exception e) {
                                        Log.e("Log.e", e.getMessage()+ "");
                                    }
                                });
                        disposables.add(disposable);
                    }
                }
            }
        }, 0, PERIOD_REQUEST);
    }

    public LiveData<RouteToDisplay> getRouteToDisplay() {
        return route;
    }

    public void clearRouteToDisplay() {
        route.postValue(null);
        isRouteShowing = false;
    }

    public boolean isRouteShowing() {
        return isRouteShowing;
    }


    public void loadRouteToDisplay(String routeID) {
        int i = 0;
        int size = tramsRoutes.size();
        while (true) {
            i++;
            if (routeID.equals(tramsRoutes.get(i).getId())) {
                break;
            }
            if (i == size - 1) break;
        }

        String startPosition = tramsRoutes.get(i).getStartPosition();
        String stopPosition = tramsRoutes.get(i).getStopPosition();

        Disposable disposable = routesInteractor.getRouteToDisplay(routeID, startPosition, stopPosition)
                .subscribeOn(Schedulers.io())
                .subscribe(routeToDisplay -> {
                    isRouteShowing = true;
                    route.postValue(routeToDisplay);
                }, throwable -> {
                });
        disposables.add(disposable);
    }

    public LiveData<HashMap<String, HashSet<Vehicle>>> getTramsWithGps() {
        if (tramsRoutes == null) getRoutesList();
        return tramsWithGps;
    }

    public void setVisibleRouteId(String id) {
        if (visibleRouteId.size() > 0) {
            boolean isInList = false;
            for (int i = 0; i < visibleRouteId.size(); i++) {
                if (id.equals(visibleRouteId.get(i))) isInList = true;
            }
            if (!isInList) visibleRouteId.add(id);
        } else {
            visibleRouteId.add(id);
        }
    }

    public void removeVisibleRouteId(String id) {
        if (visibleRouteId.size() > 0) {
            for (int i = 0; i < visibleRouteId.size(); i++) {
                if (visibleRouteId.get(i).equals(id)) visibleRouteId.remove(i);
            }
        }
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
