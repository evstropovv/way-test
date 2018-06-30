package com.ewaytest;

import android.app.Application;

import com.ewaytest.di.AppComponent;
import com.ewaytest.di.AppModule;
import com.ewaytest.di.DaggerAppComponent;

public class App extends Application {

    private static AppComponent appComponent;

    public static AppComponent getComponent() {
        return appComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = buildComponent();
    }

    protected AppComponent buildComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }
}
