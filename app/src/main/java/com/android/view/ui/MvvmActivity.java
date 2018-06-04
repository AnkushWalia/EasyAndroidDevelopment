package com.android.view.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.R;
import com.android.service.model.Project;
import com.android.view.base.BaseActivity;

public class MvvmActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvvm);
        // Add project list fragment if this is first creation
        if (savedInstanceState == null) {
            ProjectListFragment fragment = new ProjectListFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, ProjectListFragment.TAG).commit();
        }
    }

    @Override
    protected void initUI() {

    }

    /**
     * Shows the project detail fragment
     */
    public void show(Project project) {
        ProjectFragment projectFragment = ProjectFragment.forProject(project.name);

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("project")
                .replace(R.id.fragment_container,
                        projectFragment, null).commit();
    }


}
