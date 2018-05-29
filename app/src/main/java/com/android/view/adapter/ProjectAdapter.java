package com.android.view.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.android.R;
import com.android.databinding.ProjectListItemBinding;
import com.android.service.model.Project;
import com.android.view.callback.ProjectClickCallback;

import java.util.List;
import java.util.Objects;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    @Nullable
    private final ProjectClickCallback projectClickCallback;
    List<? extends Project> projectList;

    public ProjectAdapter(@Nullable ProjectClickCallback projectClickCallback) {
        this.projectClickCallback = projectClickCallback;
    }

    public void setProjectList(final List<? extends Project> projectList) {
        if (this.projectList == null) {
            this.projectList = projectList;
            notifyItemRangeInserted(0, projectList.size());
        } else {
//            this.projectList.clear();
//            this.projectList = projectList;
//            notifyDataSetChanged();
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return ProjectAdapter.this.projectList.size();
                }

                @Override
                public int getNewListSize() {
                    return projectList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return ProjectAdapter.this.projectList.get(oldItemPosition).id ==
                            projectList.get(newItemPosition).id;
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Project project = projectList.get(newItemPosition);
                    Project old = ProjectAdapter.this.projectList.get(oldItemPosition);
                    return project.id == old.id
                            && Objects.equals(project.git_url, old.git_url);
                }
            });
            this.projectList = projectList;
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ProjectListItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.project_list_item,
                        parent, false);

        binding.setCallback(projectClickCallback);

        return new ProjectViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ProjectViewHolder holder, int position) {
        holder.binding.setProject(projectList.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return projectList == null ? 0 : projectList.size();
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {

        final ProjectListItemBinding binding;

        public ProjectViewHolder(ProjectListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
