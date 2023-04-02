package com.example.productivityapp.Teams;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productivityapp.R;

import java.util.ArrayList;
import java.util.List;

public class ViewMembersAdapter extends RecyclerView.Adapter<ViewMembersAdapter.ViewHolder> {

    private List <Post> memberList;
    private Context context;

    public ViewMembersAdapter(List<Post> memberList, GetTeamMembers activity) {
        this.memberList = memberList;
        this.context = activity;
    }

    public void setMemberList(ArrayList<Post> memberList1){
        this.memberList = memberList1;
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
        holder.mTextView.setText(memberList1.getEmail());

        //set constraints for the card item view

        int mMargin = 18;
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.topMargin = mMargin;
        layoutParams.bottomMargin = mMargin;
        holder.itemView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return this.memberList.size();
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder{
        public TextView mTextView;

        public ViewHolder (View itemView){
            super(itemView);
            mTextView = itemView.findViewById(R.id.member_card_title);
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
}
