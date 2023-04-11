package com.example.productivityapp.Teams;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productivityapp.Project.CreateProject;
import com.example.productivityapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewMembersAdapter extends RecyclerView.Adapter<ViewMembersAdapter.ViewHolder> {

    private List<Post> memberList;
    private Context context;
    private String projectName;
    boolean isOwner = false;

    public ViewMembersAdapter(List<Post> memberList, GetTeamMembers activity, String project) {
        this.memberList = memberList;
        this.context = activity;
        this.projectName = project;
    }

    public void setMemberList(ArrayList<Post> memberList1) {
        this.memberList = memberList1;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post memberList1 = memberList.get(position);
        holder.mTextView.setText(memberList1.getName());

        //set constraints for the card item view

        int mMargin = 18;
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.topMargin = mMargin;
        layoutParams.bottomMargin = mMargin;
        holder.itemView.setLayoutParams(layoutParams);

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProjectFromMember(memberList1.getEmail().replace(".", ","), projectName);
                if (isOwner){
                    deleteProject(memberList1.getEmail().replace(".", ","), projectName);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.memberList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.member_card_title);
            mImageView = itemView.findViewById(R.id.delete_member);
        }
    }

    public static class MemberItems {
        private String memberName = "";


        public MemberItems() {
        }

        public MemberItems(String memberName) {
            this.memberName = memberName;
        }


        public String getMemberName() {
            return memberName;
        }

        public void setMemberName(String memberName) {
            this.memberName = memberName;
        }
    }

    private void deleteProjectFromMember(String memberEncodedEmail, String projectName) {

        //query for existing projects with the same name

        String encodedEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");
        DatabaseReference currentProjectRef = FirebaseDatabase.getInstance().getReference("Users").child(encodedEmail).child("Projects");
        currentProjectRef.orderByChild("projectName").equalTo(projectName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //project with the same name exists
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        CreateProject createProject = dataSnapshot.getValue(CreateProject.class);

                        if (createProject.getMainOwner().equals(encodedEmail)) {
                            setOwner(true);
                            break;
                        } else {
                            Toast.makeText(context, "You don't own this team", Toast.LENGTH_SHORT).show();
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteProject(String memberEncodedEmail, String projectName){
        DatabaseReference memberRef = FirebaseDatabase.getInstance().getReference("Users").child(memberEncodedEmail).child("Projects");
        memberRef.orderByChild("projectName").equalTo(projectName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();
                }
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

