package com.mwenda.carfix.ui.gas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GasStationsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GasStationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gas station fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}