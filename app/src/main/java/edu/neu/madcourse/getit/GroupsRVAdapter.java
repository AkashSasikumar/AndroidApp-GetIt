package edu.neu.madcourse.getit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class GroupsRVAdapter extends RecyclerView.Adapter<GroupsRVAdapter.GroupViewHolder> {

    public interface OnGroupClickListener{

        void onGroupClick(int position);
    }

    private GroupsRVAdapter.OnGroupClickListener mListener;
    private List<GroupView> groups;
    private Context context;


    public void setOnGroupClickListener(GroupsRVAdapter.OnGroupClickListener listener){
        mListener = listener;
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {

        private TextView mGroupName, mGroupCode;

        public GroupViewHolder(@NonNull View groupView, OnGroupClickListener listener) {
            super(groupView);
            mGroupName = (TextView) groupView.findViewById(R.id.text_view_group_name);
            mGroupCode = (TextView) groupView.findViewById(R.id.text_view_group_code);
            groupView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onGroupClick(position);
                        }
                    }
                }
            });


        }


        public void setGroupCode(String groupCode) {
            mGroupCode.setText(groupCode);
        }

        public void setGroupName(String groupName) {
            mGroupName.setText(groupName);
        }

    }


    public GroupsRVAdapter(List<GroupView> groups) {
        this.groups = groups;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
//        Context context = parent.getContext();
//        LayoutInflater inflater = LayoutInflater.from(context);
//
//        View contactView = inflater.inflate(R.layout.groups_card, parent, false);

        //return new GroupViewHolder(contactView, listener);

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_card, parent, false);
        GroupViewHolder viewHolder = new GroupViewHolder(v, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {
        GroupView group = groups.get(position);

        holder.setGroupCode(group.getGroupCode());
        holder.setGroupName(group.getGroupName());
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }
}
