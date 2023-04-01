package com.example.productivityapp;


import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

public class Adapter extends FirebaseRecyclerAdapter<Post, Adapter.PostViewholder> {
    //get the current user
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    //get the user email
    String email = user.getEmail();
    String encodedEmail = email.replace(".", ",");
    private RetrieveData context;

    public Adapter(@NonNull FirebaseRecyclerOptions<Post> options, RetrieveData context) {
        super(options);

        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull PostViewholder holder, int position, @NonNull Post post) {

        String key = getRef(holder.getAdapterPosition()).getKey();
        holder.Fname.setText(post.getName());
        holder.Email.setText(post.getEmail());
        holder.Project.setText(post.getProject());
        holder.Team.setText(post.getTeam());

        holder.Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPlus dialogPlus = DialogPlus.newDialog(context)
                        .setGravity(Gravity.CENTER)
                        .setMargin(50, 0, 50, 0)
                        .setContentHolder(new ViewHolder(R.layout.edit))
                        .setExpanded(false)
                        .create();

                View holderView = (LinearLayout) dialogPlus.getHolderView();

                EditText Fname = holderView.findViewById(R.id.name);
                EditText Email = holderView.findViewById(R.id.email);
                EditText Team = holderView.findViewById(R.id.team);
                EditText Project = holderView.findViewById(R.id.project);

                if (Fname != null) {
                    Fname.setText(post.getName());

                }
                Email.setText(post.getEmail());
                Team.setText(post.getTeam());
                Project.setText(post.getProject());


                Button Update = holderView.findViewById(R.id.updatebtn);

                Update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> map = new HashMap<>();

                        map.put("name", Fname.getText().toString());
                        map.put("email", Email.getText().toString());
                        map.put("team", Team.getText().toString());
                        map.put("project", Project.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("TeamMembers").child(encodedEmail)
                                .child(key)
                                .updateChildren(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dialogPlus.dismiss();
                                    }
                                });

                    }

                });
                dialogPlus.show();
            }
        });
        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance().getReference().child("TeamMembers").child(encodedEmail)
                        .child(getRef(holder.getAdapterPosition()).getKey())
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


    }

    @NonNull
    @Override
    public Adapter.PostViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity2, parent, false);
        return new Adapter.PostViewholder(view);
    }

    public class PostViewholder extends RecyclerView.ViewHolder {

        TextView Fname, Email, Team, Project;
        ImageView Edit, Delete;

        public PostViewholder(@NonNull View itemView) {
            super(itemView);

            Fname = itemView.findViewById(R.id.fullname);
            Email = itemView.findViewById(R.id.email);
            Team = itemView.findViewById(R.id.team);
            Project = itemView.findViewById(R.id.project);

            Edit = itemView.findViewById(R.id.edit);
            Delete = itemView.findViewById(R.id.delete);
        }
    }
}
