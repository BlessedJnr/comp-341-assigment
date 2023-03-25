package com.example.productivityapp;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class Adapter extends FirebaseRecyclerAdapter<Post, Adapter.PostViewholder>
{

    private RetrieveData context;

    public Adapter(@NonNull FirebaseRecyclerOptions<Post> options, RetrieveData context) {
        super(options);

        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull Adapter.PostViewholder holder, int i, @NonNull Post post) {

        getRef(i).getKey();
        holder.Fname.setText(post.getName());
        holder.Email.setText(post.getEmail());
        holder.Project.setText(post.getProject());
        holder.Team.setText(post.getTeam());

    }

    @NonNull
    @Override
    public Adapter.PostViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.activity2,parent,false);
        return new Adapter.PostViewholder(view);
    }

    public class PostViewholder extends RecyclerView.ViewHolder {

        TextView Fname,Email,Team,Project;

        public PostViewholder(@NonNull View itemView) {
            super(itemView);

            Fname=itemView.findViewById(R.id.fullname);
            Email=itemView.findViewById(R.id.email);
            Team=itemView.findViewById(R.id.team);
            Project=itemView.findViewById(R.id.project);
        }
    }
}
