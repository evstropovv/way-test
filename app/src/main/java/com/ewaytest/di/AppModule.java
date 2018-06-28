package com.ewaytest.di;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ewaytest.BuildConfig;
import com.ewaytest.db.AppDatabase;
import com.ewaytest.db.TramDao;
import com.ewaytest.rest.Webservice;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {
    private Context context;

    public AppModule(@NonNull Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    TramDao provideDao() {
        return AppDatabase.getAppDatabase(context).userDao();
    }

    @Provides
    @Singleton
    Webservice getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        HttpUrl url = request.url().newBuilder()
                                .addQueryParameter("login", BuildConfig.LOGIN)
                                .addQueryParameter("password", BuildConfig.PASSWORD)
                                .addQueryParameter("city", BuildConfig.CITY)
                                .build();
                        request = request.newBuilder().url(url).build();

                        return chain.proceed(request);
                    }
                })
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BuildConfig.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(Webservice.class);

    }

}
