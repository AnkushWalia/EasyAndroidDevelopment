package com.yalantis.ucrop.imagepicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yalantis.ucrop.R;
import com.yalantis.ucrop.imagepicker.listener.OnFolderClickListener;
import com.yalantis.ucrop.imagepicker.model.Folder;
import com.yalantis.ucrop.imagepicker.ui.common.BaseRecyclerViewAdapter;
import com.yalantis.ucrop.imagepicker.ui.imagepicker.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by boss1088 on 8/22/16.
 */
public class FolderPickerAdapter extends BaseRecyclerViewAdapter<FolderPickerAdapter.FolderViewHolder> {

    private List<Folder> folders = new ArrayList<>();
    private OnFolderClickListener itemClickListener;

    public FolderPickerAdapter(Context context, ImageLoader imageLoader, OnFolderClickListener itemClickListener) {
        super(context, imageLoader);
        this.itemClickListener = itemClickListener;
    }

    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = getInflater().inflate(R.layout.imagepicker_item_folder, parent, false);
        return new FolderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FolderViewHolder holder, int position) {

        final Folder folder = folders.get(position);

        getImageLoader().loadImage(folder.getImages().get(0).getPath(), holder.image);

        holder.name.setText(folder.getFolderName());

        final int count = folder.getImages().size();
        holder.count.setText(String.format(count > 1
                        ? getContext().getString(R.string.imagepicker_photo_count_multiple)
                        : getContext().getString(R.string.imagepicker_photo_count_single)
                , count));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onFolderClick(folder);
            }
        });

    }

    public void setData(List<Folder> folders) {
        if (folders != null) {
            this.folders.clear();
            this.folders.addAll(folders);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    static class FolderViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView name;
        private TextView count;

        public FolderViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_folder_thumbnail);
            name = itemView.findViewById(R.id.text_folder_name);
            count = itemView.findViewById(R.id.text_photo_count);
        }
    }

}
