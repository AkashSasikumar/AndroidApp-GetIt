package edu.neu.madcourse.getit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class GroupItems extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ItemAdaptor mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private AlertDialog mItemDetailsDialog;
    private View mItemDetailsView;
    private AlertDialog mItemInputDialog;
    private View mItemInputView;


    // dialog views
    private ImageView mdImage;
    private TextView mdName, mdNote, mdQuantity, mdPreferredStore, mdPostedBy, mdPostedOn;
    private Button mdButtonGetIt;
    private final int GREY_COLOR = Color.parseColor("#BDBDBD");
    private final int GREEN_COLOR = Color.parseColor("#689F38");

    private FloatingActionButton mAddItems;
    ArrayList<Item> mItemList = new ArrayList<>();

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_items);

        // ToDo: add items
        mItemList = populateItems();
        mAddItems = findViewById(R.id.add_items);
        mAddItems.setOnClickListener(v->addItems());

         mRecyclerView = findViewById(R.id.recycler_items);
         mRecyclerView.setHasFixedSize(true);
         mLayoutManager = new LinearLayoutManager(this);
         mAdapter = new ItemAdaptor(mItemList);

         mRecyclerView.setLayoutManager(mLayoutManager);
         mRecyclerView.setAdapter(mAdapter);
         createItemDetailsDialog();
         createItemInputDialog();

         mAdapter.setOnItemClickListener(new ItemAdaptor.OnItemClickListener() {
             @Override
             public void onItemClick(int position) {
                mItemList.get(position);
                //Snackbar.make(mRecyclerView, "Item Clicked", Snackbar.LENGTH_LONG).show();
                displayItemDetails(position);
             }
         });
    }

    private void createItemInputDialog() {
        AlertDialog.Builder itemDetailsBuilder = new AlertDialog.Builder(this);
        mItemInputView = getLayoutInflater().inflate(R.layout.item_details_popup, null);
        itemDetailsBuilder.setView(mItemInputView);
        mItemInputDialog = itemDetailsBuilder.create();
    }

    private boolean hasCamera(){
        PackageManager pm = this.getPackageManager();
        boolean hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        if(hasCamera){
            Snackbar.make(mRecyclerView, "Device has camera", Snackbar.LENGTH_LONG).show();
            return true;
        }
        else {
            Snackbar.make(mRecyclerView, "Device does not have camera", Snackbar.LENGTH_LONG).show();
            return false;
        }
    }

    private void takePicture(){
        if (hasCamera()){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } catch (ActivityNotFoundException e) {
                // display error state to the user
            }
        }
        else{
            Snackbar.make(mRecyclerView, "Device does not have camera", Snackbar.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView imageView = (ImageView) mItemInputView.findViewById(R.id.item_image);
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void addItems() {

        Item addedItem = new Item( "Apples_new" , "Gala Apples", "Stop & Shop", "2 lb", "10/12/21 at 10:15 p.m",
                new User("Akash", "Shashikumar", "akash@gmail.com"),
                null,
                R.drawable.apples);
        takePicture();
        mItemList.add(0, addedItem);
        mAdapter.notifyDataSetChanged();
        mItemInputDialog.show();
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

    private void displayItemDetails(int position){
        // set fields
        Item currentItem = mItemList.get(position);

        mdImage = (ImageView) mItemDetailsView.findViewById(R.id.item_image);
        mdName = (TextView) mItemDetailsView.findViewById(R.id.item_name);
        mdNote = (TextView) mItemDetailsView.findViewById(R.id.item_note);
        mdQuantity = (TextView) mItemDetailsView.findViewById(R.id.item_quantity);
        mdPreferredStore =(TextView)  mItemDetailsView.findViewById(R.id.item_store);
        mdPostedBy =(TextView)  mItemDetailsView.findViewById(R.id.item_posted_by);
        mdPostedOn = (TextView) mItemDetailsView.findViewById(R.id.item_posted_on);
        mdButtonGetIt = (Button) mItemDetailsView.findViewById(R.id.button_get_it);

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
            mdButtonGetIt.setBackgroundColor(GREY_COLOR);
        }else{
            mdButtonGetIt.setText("I'll get it!");
            mdButtonGetIt.setBackgroundColor(GREEN_COLOR);
        }
        mItemDetailsDialog.show();
    }

    private void createItemDetailsDialog(){
        AlertDialog.Builder itemDetailsBuilder = new AlertDialog.Builder(this);
        mItemDetailsView = getLayoutInflater().inflate(R.layout.item_details_popup, null);
        itemDetailsBuilder.setView(mItemDetailsView);
        mItemDetailsDialog = itemDetailsBuilder.create();
    }
}