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
import com.google.android.material.textfield.TextInputEditText;
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

        holder.Edit.setOnClickListener(v -> {
            DialogPlus dialogPlus = DialogPlus.newDialog(context)
                    .setGravity(Gravity.CENTER)
                    .setMargin(50, 0, 50, 0)
                    .setContentHolder(new ViewHolder(R.layout.updated_edit))
                    .setExpanded(false)
                    .create();

            View holderView = (LinearLayout) dialogPlus.getHolderView();

            TextInputEditText Fname = holderView.findViewById(R.id.updatenameinput);
            TextInputEditText Email = holderView.findViewById(R.id.updateemailinput);
            TextInputEditText Team = holderView.findViewById(R.id.teamnameinput);
            TextInputEditText Project = holderView.findViewById(R.id.updateprojectnameinput);

            if (Fname != null) {
                Fname.setText(post.getName());

            }
            Email.setText(post.getEmail());
            Team.setText(post.getTeam());
            Project.setText(post.getProject());
            Button Update = holderView.findViewById(R.id.updatebtn);

            Update.setOnClickListener(v1 -> {
                Map<String, Object> map = new HashMap<>();
                map.put("name", Fname.getText().toString());
                map.put("email", Email.getText().toString());
                map.put("team", Team.getText().toString());
                map.put("project", Project.getText().toString());

                FirebaseDatabase.getInstance().getReference().child("TeamMembers").child(encodedEmail)
                        .child(key)
                        .updateChildren(map)
                        .addOnCompleteListener(task -> dialogPlus.dismiss());
            });
            dialogPlus.show();
        });
        holder.Delete.setOnClickListener(v -> FirebaseDatabase.getInstance().getReference().child("TeamMembers").child(encodedEmail)
                .child(getRef(holder.getAdapterPosition()).getKey())
                .removeValue()
                .addOnCompleteListener(task -> Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()));
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
