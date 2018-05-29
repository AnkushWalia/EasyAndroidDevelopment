package com.android.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.android.service.model.PostalCodeRepository;

/**
 * Created by WaliaAnkush on 5/15/2018.
 */

public class NameViewModel extends ViewModel {

    // Create a LiveData with a String
    private MutableLiveData<PostalCodeRepository> repositoryMutableLiveData;
    private LiveData<PostalCodeRepository> repository;

    public NameViewModel() {

    }

    public NameViewModel(LiveData<PostalCodeRepository> repository) {
        this.repository = repository;
    }

    public MutableLiveData<PostalCodeRepository> getCurrentPostalData() {
        if (repositoryMutableLiveData == null) {
            repositoryMutableLiveData = new MutableLiveData<PostalCodeRepository>();
        }
        return repositoryMutableLiveData;
    }

    private LiveData<PostalCodeRepository> getPostalData() {
        // DON'T DO THIS
        return repository;
    }


// Rest of the ViewModel...
}
