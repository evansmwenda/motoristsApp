package com.mwenda.carfix.ui.police;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PoliceStationsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PoliceStationsViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is police stations fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}