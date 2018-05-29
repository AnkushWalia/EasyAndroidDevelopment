package com.android.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.util.Log;

import com.android.retrofit.repository.ProjectRepository;
import com.android.service.model.ProjectId;


public class ProjectViewModel extends ViewModel {
    private static final String TAG = ProjectViewModel.class.getName();
    private static final MutableLiveData ABSENT = new MutableLiveData();
    private final LiveData projectObservable;
    private final MutableLiveData<String> projectID;
    public ObservableField<ProjectId> project = new ObservableField<>();

    {
        //noinspection unchecked
        ABSENT.setValue(null);
    }

    public ProjectViewModel() {
        this.projectID = new MutableLiveData<>();

        projectObservable = Transformations.switchMap(projectID, input -> {
            if (input.isEmpty()) {
                Log.i(TAG, "ProjectViewModel projectID is absent!!!");
                return ABSENT;
            }

            Log.i(TAG, "ProjectViewModel projectID is " + projectID.getValue());

            return ProjectRepository.getInstance().getProjectDetails("Google", projectID.getValue());
        });
    }


    public LiveData<ProjectId> getObservableProject() {
        return projectObservable;
    }

    public void setProject(ProjectId project) {
        this.project.set(project);
    }

    public void setProjectID(String projectID) {
        this.projectID.setValue(projectID);

    }
}
