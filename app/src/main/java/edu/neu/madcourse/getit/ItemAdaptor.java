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

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.getit.models.Item;

public class ItemAdaptor extends RecyclerView.Adapter<ItemAdaptor.ItemViewHolder> {

    public interface OnItemClickListener{

        void onItemClick(int position);

        void onGetButtonClick(int position);
    }

    private List<Item> mItemList;
    private OnItemClickListener mListener;
    private String mGreyColor = "#BDBDBD";
    private String mGreenColor = "#689F38";

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView;
        public TextView mName, mQuantity, mStore, mPostedBy;
        public Button mGetIt;


        public ItemViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.item_image_card);
            mName = itemView.findViewById(R.id.item_name_card);
            mQuantity = itemView.findViewById(R.id.item_quantity_card);
            mStore = itemView.findViewById(R.id.item_store_card);
            mPostedBy = itemView.findViewById(R.id.item_posted_by_card);
            mGetIt = itemView.findViewById(R.id.get_item_card);

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

            mGetIt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onGetButtonClick(position);
                        }
                    }
                }
            });
        }
    }

    public  ItemAdaptor(List<Item> itemList){
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
        if(currentItem.getUserPosted() != null) {
            holder.mPostedBy.setText("Posted by: " + currentItem.getUserPosted().getFullName());
        }
        holder.mImageView.setImageBitmap(currentItem.getImageBitmap());

        if (currentItem.getUserGettingIt() != null){
            holder.mGetIt.setBackgroundColor(Color.parseColor(mGreyColor));
            holder.mGetIt.setText(currentItem.getUserGettingIt().getFullName() + " is already getting it!");
        }else{
            holder.mGetIt.setBackgroundColor(Color.parseColor(mGreenColor));
            holder.mGetIt.setText("I'll get' it!");
        }
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }


}
