package edu.neu.madcourse.getit;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ItemAdaptor extends RecyclerView.Adapter<ItemAdaptor.ItemViewHolder> {

    public interface OnItemClickListener{

        void onItemClick(int position);
    }

    private ArrayList<Item> mItemList;
    private OnItemClickListener mListener;
    private String mGreyColor = "#BDBDBD";

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView;
        public TextView mName, mQuantity, mStore, mPostedBy;
        public Button mGetIt;


        public ItemViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.item_image);
            mName = itemView.findViewById(R.id.item_name);
            mQuantity = itemView.findViewById(R.id.item_quantity);
            mStore = itemView.findViewById(R.id.item_store);
            mPostedBy = itemView.findViewById(R.id.item_posted_by);
            mGetIt = itemView.findViewById(R.id.button_get_it);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public  ItemAdaptor(ArrayList<Item> itemList){
        mItemList = itemList;
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(v, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item currentItem = mItemList.get(position);

        holder.mName.setText(currentItem.getName());
        holder.mStore.setText("Preferred store: " + currentItem.getPreferredStore());
        holder.mQuantity.setText("Quantity: " + currentItem.getQuantity());
        holder.mPostedBy.setText("Posted by: " + currentItem.getUserPosted().getFirstName());
        holder.mImageView.setImageResource(R.drawable.apples);

        if (currentItem.getUserGettingIt() != null){
            holder.mGetIt.setBackgroundColor(Color.parseColor(mGreyColor));
            holder.mGetIt.setText(currentItem.getUserGettingIt().getFirstName() + " is already getting it!");
        }
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }


}
