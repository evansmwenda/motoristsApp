package com.mwenda.carfix.ui.hospitals;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HospitalsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HospitalsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is hospitals fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}