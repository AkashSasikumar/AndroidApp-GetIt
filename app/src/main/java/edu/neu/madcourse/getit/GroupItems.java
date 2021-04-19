package edu.neu.madcourse.getit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class GroupItems extends AppCompatActivity {

    private final int GREY_COLOR = Color.parseColor("#BDBDBD");
    private final int GREEN_COLOR = Color.parseColor("#689F38");
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int GET_FROM_GALLERY = 3;

    private ArrayList<Item> mItemList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ItemAdaptor mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mAddItems;
    private FirebaseAuth fAuth;
    private User mLoggedInUser;

    // Display item details dialog
    private AlertDialog mItemDetailsDialog;
    private View mItemDetailsView;
    private ImageView mdImage;
    private TextView mdName, mdInstructions, mdQuantity, mdPreferredStore, mdPreferredBrand, mdPostedBy, mdPostedOn;
    private Button mdButtonGetIt;


    // Add item dialog
    private AlertDialog mItemInputDialog;
    private View mItemInputView;
    private EditText mInputItemName, mInputQuantity, mInputStore, mInputBrand, mInputInstructions;
    private ImageView mInputItemImage;
    private Button mAddImage, mUploadImage, mPostItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_items);

        fAuth = FirebaseAuth.getInstance();
        mLoggedInUser = getLoggedInUser();

        // ToDo: add real logic inside populate items
        mItemList = populateItems();
        setupRecyclerView();
        createItemDetailsDialog();
        createItemInputDialog();
    }

    private User getLoggedInUser(){
        FirebaseUser firebaseUser = fAuth.getCurrentUser();
        return new User("Test", "User", firebaseUser.getEmail());
    }

    private void setupRecyclerView(){
        mAddItems = findViewById(R.id.add_items);
        mAddItems.setOnClickListener(v-> getItemDetailsFromUser());

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
                displayItemDetails(position);
            }
        });
    }

    private void createItemDetailsDialog(){
        AlertDialog.Builder itemDetailsBuilder = new AlertDialog.Builder(this);
        mItemDetailsView = getLayoutInflater().inflate(R.layout.item_details, null);
        itemDetailsBuilder.setView(mItemDetailsView);
        mItemDetailsDialog = itemDetailsBuilder.create();

        // get display dialog views
        mdImage = (ImageView) mItemDetailsView.findViewById(R.id.item_image_display);
        mdName = (TextView) mItemDetailsView.findViewById(R.id.item_name_display);
        mdInstructions = (TextView) mItemDetailsView.findViewById(R.id.item_instructions_display);
        mdQuantity = (TextView) mItemDetailsView.findViewById(R.id.item_quantity_display);
        mdPreferredStore =(TextView)  mItemDetailsView.findViewById(R.id.item_store_display);
        mdPreferredBrand = (TextView)  mItemDetailsView.findViewById(R.id.item_brand_display);
        mdPostedBy =(TextView)  mItemDetailsView.findViewById(R.id.item_posted_by_display);
        mdPostedOn = (TextView) mItemDetailsView.findViewById(R.id.item_posted_on_display);
        mdButtonGetIt = (Button) mItemDetailsView.findViewById(R.id.get_item_display);

        // set listeners
        mdButtonGetIt.setOnClickListener(v->getItem());
    }

    private void getItem() {
        //ToDo: get selected item and add current user
        // update view should show you are getting it
    }

    private void displayItemDetails(int position){
        // set fields
        Item currentItem = mItemList.get(position);

        mdImage.setImageBitmap(currentItem.getImageBitmap());
        mdName.setText(currentItem.getName());
        mdInstructions.setText(currentItem.getInstructions());
        mdQuantity.setText(currentItem.getQuantity());
        mdPreferredStore.setText(currentItem.getPreferredStore());
        mdPreferredBrand.setText(currentItem.getPreferredBrand());
        mdPostedBy.setText(currentItem.getUserPosted().getFirstName() + " (" + currentItem.getUserPosted().getEmail() + ")" );
        mdPostedOn.setText(currentItem.getPostedDateTimeAsString());

        if (currentItem.getUserGettingIt() != null){
            mdButtonGetIt.setText(currentItem.getUserGettingIt().getFirstName() + " is already getting it!");
            mdButtonGetIt.setBackgroundColor(GREY_COLOR);
        }else{
            mdButtonGetIt.setText("I'll get it!");
            mdButtonGetIt.setBackgroundColor(GREEN_COLOR);
        }
        mItemDetailsDialog.show();
    }

    private void createItemInputDialog() {
        // set layout
        AlertDialog.Builder itemDetailsBuilder = new AlertDialog.Builder(this);
        mItemInputView = getLayoutInflater().inflate(R.layout.add_item, null);
        itemDetailsBuilder.setView(mItemInputView);
        mItemInputDialog = itemDetailsBuilder.create();

        // get views
        mAddImage = mItemInputView.findViewById(R.id.add_image);
        mUploadImage = mItemInputView.findViewById(R.id.upload_image);
        mInputItemImage = mItemInputView.findViewById(R.id.item_image);
        mPostItem = mItemInputView.findViewById(R.id.post_item);
        mInputItemName = mItemInputView.findViewById(R.id.item_name_edit);
        mInputQuantity = mItemInputView.findViewById(R.id.item_quantity_edit);
        mInputStore = mItemInputView.findViewById(R.id.item_store_edit);
        mInputBrand = mItemInputView.findViewById(R.id.item_brand_edit);
        mInputInstructions = mItemInputView.findViewById(R.id.item_instructions_edit);

        // set fields
        mInputItemImage.setImageResource(R.drawable.image_placeholder);

        // set listeners
        mAddImage.setOnClickListener(v -> takePictureFromCamera());
        mUploadImage.setOnClickListener(v -> uploadImage());
        mPostItem.setOnClickListener(v -> postItem());
    }

    private void getItemDetailsFromUser() {
        // show the input dialog
        mItemInputDialog.show();
    }

    private void postItem() {

        // get details from the dialog and add to the list
        // ToDo: get details
        String itemName = mInputItemName.getText().toString().trim();
        String itemQuantity = mInputQuantity.getText().toString().trim();
        String preferredStore = mInputStore.getText().toString().trim();
        String preferredBrand = mInputBrand.getText().toString().trim();
        String instructions = mInputInstructions.getText().toString().trim();
        BitmapDrawable drawable = (BitmapDrawable) mInputItemImage.getDrawable();
        Bitmap itemImage = drawable.getBitmap();
        LocalDateTime dateTime = LocalDateTime.now();

        // Validate input fields
        if(TextUtils.isEmpty(itemName)){
            mInputItemName.setError("Item name is required");
            return;
        }

        if(TextUtils.isEmpty(itemQuantity)){
            mInputQuantity.setError("Item quantity is required");
            return;
        }

        Item item = new Item(itemName, itemQuantity, preferredStore, preferredBrand, dateTime,
                mLoggedInUser, null, itemImage , instructions);
        mItemList.add(0, item);
        mItemInputDialog.hide();
        mAdapter.notifyDataSetChanged();
        // ToDo: clear input fields
        Snackbar.make(mRecyclerView, "Your item has been posted!", Snackbar.LENGTH_LONG).show();
        return;
    }


    private boolean hasCamera(){
        PackageManager pm = this.getPackageManager();
        boolean hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        if(hasCamera){
            return true;
        }
        else {
            Snackbar.make(mRecyclerView, "Device does not have camera!", Snackbar.LENGTH_LONG).show();
            return false;
        }
    }

    private void takePictureFromCamera(){
        if (hasCamera()){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } catch (ActivityNotFoundException e) {
                // display error state to the user
                Snackbar.make(mRecyclerView, "Could not capture image. Please try to upload!",
                        Snackbar.LENGTH_LONG).show();
            }
        }
        else{
            Snackbar.make(mRecyclerView, "Device does not have camera", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Image taken from camera
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mInputItemImage.setImageBitmap(imageBitmap);
        }

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                mInputItemImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                // display error state to the user
                Snackbar.make(mRecyclerView, "Could not upload image. Please try to add image!",
                        Snackbar.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public static Bitmap getRotatedImage(Uri path, Bitmap originalImage){
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(path.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;
        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(originalImage, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(originalImage, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(originalImage, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = originalImage;
        }
        return rotatedBitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


    private void uploadImage() {
        startActivityForResult(new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                GET_FROM_GALLERY);
    }

    private ArrayList<Item> populateItems(){
        ArrayList<Item> items = new ArrayList<>();
        BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.image_placeholder);
        Bitmap itemImage = drawable.getBitmap();

        for (int i = 1; i <= 10; ++i){
            User userGettingIt = null;
            if(i % 2 == 0){
                userGettingIt = new User("Ashwin", "Sir", "ashwin@gmail.com");
            }

            items.add(new Item( "Apples" + i, "2 lb", "Walmart", "Great Value", LocalDateTime.now(),
                    mLoggedInUser, userGettingIt, itemImage,
                    "Please get red delicious apples. Price should be around 5$."));
        }
        return items;
    }


}