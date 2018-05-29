package com.android.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.android.retrofit.repository.ProjectRepository;
import com.android.service.model.Project;

import java.util.List;


public class ProjectListViewModel extends ViewModel {
    private LiveData<List<Project>> projectListObservable;

    public ProjectListViewModel() {
        // If any transformation is needed, this can be simply done by Transformations class ...
        projectListObservable = ProjectRepository.getInstance().getProjectList("Google");
    }

    /**
     * Expose the LiveData Projects query so the UI can observe it.
     */
    public LiveData<List<Project>> getProjectListObservable() {
        return projectListObservable;
    }
}
