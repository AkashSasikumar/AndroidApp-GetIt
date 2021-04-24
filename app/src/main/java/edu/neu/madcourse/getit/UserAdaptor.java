package edu.neu.madcourse.getit;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdaptor extends RecyclerView.Adapter<UserAdaptor.UserViewHolder> {

    private static final int LIGHT_GREY = Color.parseColor("#F5F5F5");
    private static final int WHITE = Color.parseColor("#FFFFFF");


    private List<UserCard> mUsers;
    private OnUserClickListener mListener;

    public UserAdaptor(List<UserCard> users){
        mUsers = users;
    }

    public void setOnUserClickListener(OnUserClickListener listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card, parent, false);
       UserAdaptor.UserViewHolder viewHolder = new UserAdaptor.UserViewHolder(v, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserCard currentUser = mUsers.get(position);
        holder.mUserName.setText(currentUser.getUserName());
        holder.mUserEmail.setText(currentUser.getUserEmail());
        holder.mUserScore.setText(Long.toString(currentUser.getUserScore()));
        if (position % 2 == 1){
            holder.itemView.setBackgroundColor(LIGHT_GREY);
        }else{
            holder.itemView.setBackgroundColor(WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public interface OnUserClickListener{
        void onUserClick(int position);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView mUserName, mUserEmail, mUserScore;

        public UserViewHolder(@NonNull View itemView, OnUserClickListener listener) {
            super(itemView);
            mUserName = itemView.findViewById(R.id.user_card_name);
            mUserEmail = itemView.findViewById(R.id.user_card_email);
            mUserScore = itemView.findViewById(R.id.user_card_score_value);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onUserClick(position);
                        }
                    }
                }
            });
        }
    }
}
