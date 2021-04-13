package edu.neu.madcourse.getit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class GroupItems extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ItemAdaptor mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private AlertDialog.Builder itemDetailsBuilder;
    private AlertDialog itemDetailsPopup;

    // dialog views
    private ImageView mdImage;
    private TextView mdName, mdNote, mdQuantity, mdPreferredStore, mdPostedBy, mdPostedOn;
    private Button mdButtonGetIt;
    private String mGreyColor = "#BDBDBD";

    ArrayList<Item> mItemList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_items);


        // ToDo: add items
         mItemList = populateItems();

         mRecyclerView = findViewById(R.id.recycler_items);
         mRecyclerView.setHasFixedSize(true);
         mLayoutManager = new LinearLayoutManager(this);
         mAdapter = new ItemAdaptor(mItemList);

         mRecyclerView.setLayoutManager(mLayoutManager);
         mRecyclerView.setAdapter(mAdapter);
         mAdapter.setOnItemClickListener(new ItemAdaptor.OnItemClickListener() {
             @Override
             public void onItemClick(int position) {
                mItemList.get(position);
                //Snackbar.make(mRecyclerView, "Item Clicked", Snackbar.LENGTH_LONG).show();
                popupItemDetails(position);
             }
         });
    }

    ArrayList<Item> populateItems(){
        ArrayList<Item> items = new ArrayList<>();

        for (int i = 1; i <= 10; ++i){
            User userGettingIt = null;
            if(i % 2 == 0){
                userGettingIt = new User("Ashwin", "Sir", "ashwin@gmail.com");
            }

            items.add(new Item( "Apples" + i, "Gala Apples", "Stop & Shop", "2 lb", "10/12/21 at 10:15 p.m",
                    new User("Akash", "Shashikumar", "akash@gmail.com"),
                    userGettingIt,
                    R.drawable.apples));
        }
        return items;
    }

    private void popupItemDetails(int position){
        itemDetailsBuilder = new AlertDialog.Builder(this);
        View itemDetails = getLayoutInflater().inflate(R.layout.item_details_popup, null);

        setPopupFields(position, itemDetails);

        itemDetailsBuilder.setView(itemDetails);
        itemDetailsPopup = itemDetailsBuilder.create();
        itemDetailsPopup.show();
    }

    private void setPopupFields(int position, View itemDetails){
        // set fields
        Item currentItem = mItemList.get(position);
        mdImage = (ImageView) itemDetails.findViewById(R.id.item_image);
        mdName = (TextView) itemDetails.findViewById(R.id.item_name);
        mdNote = (TextView) itemDetails.findViewById(R.id.item_note);
        mdQuantity = (TextView) itemDetails.findViewById(R.id.item_quantity);
        mdPreferredStore =(TextView)  itemDetails.findViewById(R.id.item_store);
        mdPostedBy =(TextView)  itemDetails.findViewById(R.id.item_posted_by);
        mdPostedOn = (TextView) itemDetails.findViewById(R.id.item_posted_on);
        mdButtonGetIt = (Button) itemDetails.findViewById(R.id.button_get_it);

        mdImage.setImageResource(currentItem.getImage());
        mdName.setText(currentItem.getName());
        mdNote.setText(currentItem.getDesc());
        mdQuantity.setText("Quantity: " + currentItem.getQuantity());
        mdPreferredStore.setText("Preferred store: " + currentItem.getPreferredStore());
        mdPostedBy.setText("Posted by: " + currentItem.getUserPosted().getFirstName() + " (" + currentItem.getUserPosted().getEmail() + ")" );
        mdPostedOn.setText("Posted on: " + currentItem.getPostedOn());

        if (currentItem.getUserGettingIt() != null){
            //ToDo: change color of button
            mdButtonGetIt.setText(currentItem.getUserGettingIt().getFirstName() + " is already getting it!");
            mdButtonGetIt.setBackgroundColor(Color.parseColor(mGreyColor));
        }
    }
}