package com.ewaytest.di;

import com.ewaytest.MapsActivity;
import com.ewaytest.TramsViewModel;
import com.ewaytest.domain.RoutesInteractorImpl;

import javax.inject.Singleton;
import dagger.Component;

@Component(modules = {AppModule.class, WebModule.class})
@Singleton
public interface AppComponent {

    void inject(MapsActivity activity);
    void inject(RoutesInteractorImpl activity);
    void inject(TramsViewModel activity);



}
