package com.example.productivityapp.Project;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.productivityapp.R;
import java.util.List;

public class ProjectAdapterClass extends RecyclerView.Adapter<ProjectAdapterClass.ViewHolder> {
    private List <ProjectItem> mProjectItems;

    public ProjectAdapterClass (List<ProjectItem> projectItem) {
        mProjectItems = projectItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){

    }

    @Override
    public int getItemCount(){
        return mProjectItems.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView;
        public TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class ProjectItem {
        private int mImageResource;
        private String mText;

        public int getImageResource(){
            return mImageResource;
        }

        public String getText(){
            return mText;
        }
    }


}


