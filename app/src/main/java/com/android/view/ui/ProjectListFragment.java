package com.android.view.ui;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.R;
import com.android.databinding.FragmentProjectListBinding;
import com.android.room.DatabaseCreator;
import com.android.room.PersonDAO;
import com.android.service.model.Project;
import com.android.view.adapter.ProjectAdapter;
import com.android.view.callback.ProjectClickCallback;
import com.android.viewmodel.ProjectListViewModel;

import java.util.List;

public class ProjectListFragment extends Fragment {
    public static final String TAG = "ProjectListFragment";
    private ProjectAdapter projectAdapter;
    private FragmentProjectListBinding binding;
    private ProjectClickCallback projectClickCallback = new ProjectClickCallback() {
        @Override
        public void onClick(Project project) {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                ((MvvmActivity) getActivity()).show(project);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_project_list, container, false);
        projectAdapter = new ProjectAdapter(projectClickCallback);
        binding.projectList.setAdapter(projectAdapter);
        binding.setIsLoading(true);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ProjectListViewModel viewModel = ViewModelProviders.of(this).get(ProjectListViewModel.class);
        //   observeViewModel(viewModel);
        observeDaoModel();
    }

    private void observeDaoModel() {
        PersonDAO personDAO = DatabaseCreator.getPersonDatabase(getActivity()).PersonDatabase();
        personDAO.getAllPersons().observe(getActivity(), projects -> {
            if (projects != null && projects.size() > 0) {
                binding.setIsLoading(false);
                projectAdapter.setProjectList(projects);
            }
        });
    }

    private void observeViewModel(ProjectListViewModel viewModel) {
        // Update the list when the data changes
        viewModel.getProjectListObservable().observe(this, new Observer<List<Project>>() {
            @Override
            public void onChanged(@Nullable List<Project> projects) {
                if (projects != null) {
                    binding.setIsLoading(false);
                    if (projects.size() > 0)
                        projectAdapter.setProjectList(projects);
                }
            }
        });
    }
}
