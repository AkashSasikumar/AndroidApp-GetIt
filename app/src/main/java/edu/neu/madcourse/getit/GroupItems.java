package edu.neu.madcourse.getit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class GroupItems extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ItemAdaptor mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private AlertDialog.Builder itemDetailsBuilder;
    private AlertDialog itemDetailsPopup;

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
                Snackbar.make(mRecyclerView, "Item Clicked", Snackbar.LENGTH_LONG).show();
                popupItemDetails(position);
             }
         });
    }

    ArrayList<Item> populateItems(){
        ArrayList<Item> items = new ArrayList<>();

        for (int i = 0; i < 10; ++i){
            items.add(new Item( "Apples", "Gala Apples", "Stop & Shop", "2 lb",
                    new User("Akash", "Shashikumar", "akash@gmail.com"),
                    new User("Ashwin", "Sir", "ashwin@gmail.com"),
                    R.drawable.apples));
        }
        return items;
    }

    private void popupItemDetails(int position){
        itemDetailsBuilder = new AlertDialog.Builder(this);
        View itemDetails = getLayoutInflater().inflate(R.layout.item_details_popup, null);
        itemDetails.setClipToOutline(true);
        itemDetailsBuilder.setView(itemDetails);
        itemDetailsPopup = itemDetailsBuilder.create();
        itemDetailsPopup.show();
    }

}