package com.android.view.ui;

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
import com.android.databinding.FragmentProjectDetailsBinding;
import com.android.room.DatabaseCreator;
import com.android.room.PersonDAO;
import com.android.service.model.ProjectId;
import com.android.viewmodel.ProjectViewModel;


public class ProjectFragment extends Fragment {
    private static final String KEY_PROJECT_ID = "project_id";
    private FragmentProjectDetailsBinding binding;

    /**
     * Creates project fragment for specific project ID
     */
    public static ProjectFragment forProject(String projectID) {
        ProjectFragment fragment = new ProjectFragment();
        Bundle args = new Bundle();
        args.putString(KEY_PROJECT_ID, projectID);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate this data binding layout
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_project_details, container, false);
        // Create and set the adapter for the RecyclerView.
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ProjectViewModel viewModel = ViewModelProviders.of(this)
                .get(ProjectViewModel.class);
        viewModel.setProjectID(getArguments().getString(KEY_PROJECT_ID));
        binding.setProjectViewModel(viewModel);
        binding.setIsLoading(true);
        observeViewModel(viewModel);
        observeDaoModel(viewModel);
    }

    private void observeDaoModel(ProjectViewModel viewModel) {
        PersonDAO personDAO = DatabaseCreator.getPersonDatabase(getActivity()).PersonDatabase();
        personDAO.findProjectDetail(getArguments().getString(KEY_PROJECT_ID)).observe(getActivity(), projects -> {
            if (projects != null && projects.projectIds.size() > 0) {
                binding.setIsLoading(false);
                viewModel.setProject(projects.projectIds.get(0));
            }
        });
    }

    private void observeViewModel(ProjectViewModel viewModel) {
        // Observe project data
        viewModel.getObservableProject().observe(this, new Observer<ProjectId>() {
            @Override
            public void onChanged(@Nullable ProjectId project) {
                if (project != null) {
//                    binding.setIsLoading(false);
//                    viewModel.setProject(project);
                }
            }
        });
    }
}
