package com.example.productivityapp.Project;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.productivityapp.R;
import java.util.List;

public class ProjectAdapterClass extends RecyclerView.Adapter<ProjectAdapterClass.ViewHolder> {
    private List <ProjectItem> mProjectItems;
    Context context;

    public ProjectAdapterClass (List<ProjectItem> projectItem, ProjectActivity activity) {
        this.context = activity;
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
        ProjectItem projectItem = mProjectItems.get(position);
        holder.mImageView.setImageResource(projectItem.getImageResource());
        holder.mTextView.setText(projectItem.getText());

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();

        int margin = 18;
        int width = holder.itemView.getResources().getDisplayMetrics().widthPixels / 2 - margin *2;
        layoutParams.width = width;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.topMargin = margin;
        layoutParams.bottomMargin = margin;

        if (position % 2 == 0) {
            layoutParams.leftMargin = margin;
            layoutParams.rightMargin = margin / 2;
        } else {
            layoutParams.leftMargin = margin / 2;
            layoutParams.rightMargin = margin;
        }
        holder.itemView.setLayoutParams(layoutParams);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    String projectName = mProjectItems.get(adapterPosition).getText();
                    Intent intent = new Intent(context, TaskActivity.class);
                    intent.putExtra("projectName", projectName);
                    context.startActivity(intent);
                }
            }
        });

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
            mImageView = itemView.findViewById(R.id.project_card_image);
            mTextView = itemView.findViewById(R.id.project_card_title);
        }
    }

    public static class ProjectItem {
        private int mImageResource;
        private String mText;

        public ProjectItem (int imageResource, String text) {
            mImageResource = imageResource;
            mText = text;
        }
        public int getImageResource(){
            return mImageResource;
        }

        public String getText(){
            return mText;
        }
    }


}


