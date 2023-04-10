package com.example.productivityapp.Notifications;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productivityapp.R;

import java.util.List;

public class InboxNotificationsAdapter extends RecyclerView.Adapter<InboxNotificationsAdapter.ViewHolder>{

    private List<CreateInboxNotifications> mMessages;
    private int mMargin;

    public InboxNotificationsAdapter(List<CreateInboxNotifications> mMessages, int mMargin) {
        this.mMessages = mMessages;
        this.mMargin = mMargin;
    }

    public void setmMessages(List<CreateInboxNotifications> mMessages) {
        this.mMessages = mMessages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CreateInboxNotifications messages = mMessages.get(position);
        holder.messageSubject.setText(messages.getMessageSubject());
        holder.messageBody.setText(messages.getMessageBody());
        holder.timestamp.setText(messages.getTimeStamp());

        // Set constraints for the card item view
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.topMargin = mMargin;
        layoutParams.bottomMargin = mMargin;

        holder.itemView.setLayoutParams(layoutParams);

    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView messageSubject;
        private TextView messageBody;
        private TextView timestamp;

        public ViewHolder (View itemView){
            super(itemView);
            messageSubject = itemView.findViewById(R.id.message_subject);
            messageBody = itemView.findViewById(R.id.message_body);
            messageBody = itemView.findViewById(R.id.message_timestamp);
        }
    }
}
