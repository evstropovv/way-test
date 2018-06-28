package com.ewaytest.workers;

import android.support.annotation.NonNull;

import androidx.work.Worker;

public class LoadCurrentTrams extends Worker {

    @NonNull
    @Override
    public Result doWork() {

        //download and save trams coordinations...

//        return Result.SUCCESS;
//        return Result.FAILURE;

        return null;
    }
}
