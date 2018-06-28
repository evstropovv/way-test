package com.ewaytest.di;

import com.ewaytest.domain.RoutesInteractor;
import com.ewaytest.domain.RoutesInteractorImpl;
import com.ewaytest.rest.Webservice;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;

@Module
public class WebModule {

    @Provides
    @Singleton
    RoutesInteractor routesInteractor(Webservice webservice){
        return new RoutesInteractorImpl(webservice);
    }

}
