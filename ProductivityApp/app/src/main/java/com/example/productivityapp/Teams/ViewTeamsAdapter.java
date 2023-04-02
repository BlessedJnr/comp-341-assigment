package com.example.productivityapp.Teams;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.productivityapp.R;
import java.util.ArrayList;
import java.util.List;

public class ViewTeamsAdapter extends RecyclerView.Adapter<ViewTeamsAdapter.ViewHolder> {
    private List <TeamsItem> mTeamItems;
    Context context;

    public ViewTeamsAdapter (List<TeamsItem> teamItem, TeamsActivity activity){
        this.context = activity;
        this.mTeamItems = teamItem;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teams_card, parent, false);
        return new ViewHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TeamsItem teamsItem = mTeamItems.get(position);
        holder.mTextView.setText(teamsItem.getTeamName());
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();

        int margin = 18;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.topMargin = margin;
        layoutParams.bottomMargin = margin;
        holder.itemView.setLayoutParams(layoutParams);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if(adapterPosition != RecyclerView.NO_POSITION) {
                    String teamName = mTeamItems.get(adapterPosition).getTeamName();
                    Intent intent = new Intent(context,GetTeamMembers.class);
                    intent.putExtra("teamName", teamName);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.mTeamItems.size();
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder{
        public TextView mTextView;

        public ViewHolder (View itemView){
            super(itemView);
            mTextView = itemView.findViewById(R.id.team_card_title);
        }

    }

    public static class TeamsItem {
        private String teamName = "";
        private ArrayList<CreateTeams> teamsList = new ArrayList<CreateTeams>();

        public TeamsItem () {
            this.teamName = "";
            teamsList = new ArrayList<CreateTeams>();
        }
        public TeamsItem(String mTeamName) {
            this.teamName = mTeamName;
        }

        public TeamsItem(String mTeamName, ArrayList<CreateTeams> teamsList) {
            this.teamName = mTeamName;
            this.teamsList = teamsList;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }

        public ArrayList<CreateTeams> getTeamsList() {
            return teamsList;
        }

        public void setTeamsList(ArrayList<CreateTeams> teamsList) {
            this.teamsList = teamsList;
        }
    }
}
