package edu.neu.madcourse.getit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.neu.madcourse.getit.callbacks.GroupServiceCallbacks;
import edu.neu.madcourse.getit.callbacks.ItemServiceCallbacks;
import edu.neu.madcourse.getit.callbacks.UserServiceCallbacks;
import edu.neu.madcourse.getit.models.Group;
import edu.neu.madcourse.getit.models.Item;
import edu.neu.madcourse.getit.models.User;
import edu.neu.madcourse.getit.services.GroupService;
import edu.neu.madcourse.getit.services.ItemService;
import edu.neu.madcourse.getit.services.UserService;

public class GroupItems extends AppCompatActivity {

    private final int GREY_COLOR = Color.parseColor("#BDBDBD");
    private final int GREEN_COLOR = Color.parseColor("#689F38");
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int GET_FROM_GALLERY = 3;

    private static final String INTENT_GROUP_NAME = "GROUP_NAME";
    private static final String INTENT_GROUP_ID = "GROUP_ID";
    private static final String INTENT_GROUP_CODE = "GROUP_CODE";

    private ArrayList<Item> mItemList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ItemAdaptor mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    //private Toolbar mToolbar;
    private FloatingActionButton mAddItems;
    private FirebaseAuth fAuth;
    private User mLoggedInUser;
    private String groupName;
    private String groupCode;
    private String groupID;
    private GroupService groupService;
    private ItemService itemService;
    private UserService userService;

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

        // get data from intent
        groupName = getIntent().getStringExtra(INTENT_GROUP_NAME);
        groupCode = getIntent().getStringExtra(INTENT_GROUP_CODE);
        groupID = getIntent().getStringExtra(INTENT_GROUP_ID);

        setToolbarTitle("Items (" + groupName + ")");
        fAuth = FirebaseAuth.getInstance();
        groupService = new GroupService();
        itemService = new ItemService();
        userService = new UserService();
        mItemList = new ArrayList<>();
        setLoggedInUser();

        setupRecyclerView();
        populateItems();
        createItemDetailsDialog();
        createItemInputDialog();
    }

    private void setToolbarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        //MenuItem sortByName = menu.findItem(R.id.sort_by_name);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.sort_by_item_name:
                Collections.sort(mItemList, Item.itemNameComparator);
                break;
            case R.id.sort_by_date_earliest:
                Collections.sort(mItemList, Item.itemDateOldestComparator);
                break;
            case R.id.sort_by_date_latest:
                Collections.sort(mItemList, Item.itemDateRecentComparator);
                break;
            case R.id.sort_by_user_posted:
                Collections.sort(mItemList, Item.itemUserPostedComparator);
                break;
            case R.id.sort_by_user_getting:
                Collections.sort(mItemList, Item.itemUserGettingComparator);
                break;
            case R.id.group_settings:
                onSettingsClick();
                break;

//            case R.id.add_member:
//                addMemberToGroup();
//                break;
        }
        mAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    private void onSettingsClick(){
        Snackbar.make(mRecyclerView, "Settings clicked!", Snackbar.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), GroupSettings.class);
        intent.putExtra(INTENT_GROUP_NAME, groupName);
        intent.putExtra(INTENT_GROUP_ID, groupID);
        intent.putExtra(INTENT_GROUP_CODE, groupCode);
        startActivity(intent);
    }

    private void setLoggedInUser(){
        FirebaseUser firebaseUser = fAuth.getCurrentUser();
        userService.getUserFromEmail(firebaseUser.getEmail(), new UserServiceCallbacks.GetUserFromEmailCallback() {
            @Override
            public void onComplete(User user) {
                mLoggedInUser = user;
            }
        });
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
                // mItemList.get(position);
                displayItemDetails(position);
            }

            @Override
            public void onGetButtonClick(int position) {
                getItem(position);
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
//        // set listeners
//        mdButtonGetIt.setOnClickListener(v->getItem());
    }

    private void getItem(int position) {
        //ToDo: get selected item and add current user
        // update view should show you are getting it
        Item currentItem = mItemList.get(position);

        // update database
        itemService.addUserGettingTheItem(currentItem.getItemID(), mLoggedInUser, new ItemServiceCallbacks.addUserGettingTheItemTaskCallback() {
            @Override
            public void onComplete(boolean isSuccess) {
                if(isSuccess){
                    currentItem.setUserGettingIt(mLoggedInUser);
                    mItemList.remove(position);
                    mItemList.add(position, currentItem);
                    mdButtonGetIt.setText(currentItem.getUserGettingIt().getFullName() + " is already getting it!");
                    mdButtonGetIt.setBackgroundColor(GREY_COLOR);
                    mdButtonGetIt.setClickable(false);
                    mAdapter.notifyDataSetChanged();
                    Snackbar.make(mRecyclerView, "Item has been added to list of items you need to get!", Snackbar.LENGTH_LONG).show();
                }else{
                    Snackbar.make(mRecyclerView, "Oops! Something went wrong. Please try again!", Snackbar.LENGTH_LONG).show();
                }
            }
        });

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
        mdPostedBy.setText(currentItem.getUserPosted().getFullName() + " (" + currentItem.getUserPosted().getUserEmail() + ")" );
        mdPostedOn.setText(currentItem.getPostedDateTime());

        if (currentItem.getUserGettingIt() != null){
            mdButtonGetIt.setText(currentItem.getUserGettingIt().getFullName() + " is already getting it!");
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
        String currentDateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // Validate input fields
        if(TextUtils.isEmpty(itemName)){
            mInputItemName.setError("Item name is required");
            return;
        }

        if(TextUtils.isEmpty(itemQuantity)){
            mInputQuantity.setError("Item quantity is required");
            return;
        }

        Item item = new Item(itemName, itemQuantity, preferredStore, preferredBrand, currentDateAndTime,
                mLoggedInUser, null, itemImage , instructions);
//        mItemList.add(0, item);
//        mItemInputDialog.hide();
//        mAdapter.notifyDataSetChanged();

        // Add to database first and then show success
        itemService.createItem(item, mLoggedInUser, new ItemServiceCallbacks.CreateItemTaskCallback() {
            @Override
            public void onComplete(boolean isSuccess, String itemId) {
                if(isSuccess) {
                    // Add this item to group: TODO: send groupId instead of groupName
                    groupService.addItemToGroup(itemId, groupName, new GroupServiceCallbacks.AddItemToGroupTaskCallback() {
                        @Override
                        public void onComplete(boolean isSuccess) {
                            if(isSuccess) {
                                // Now show on screen
                                mItemList.add(0, item);
                                mItemInputDialog.hide();
                                mAdapter.notifyDataSetChanged();
                                // ToDo: clear input fields
                                Snackbar.make(mRecyclerView, "Your item has been posted!", Snackbar.LENGTH_LONG).show();
                            } else {
                                // ToDo: clear input fields
                                Snackbar.make(mRecyclerView, "Error in adding item. Please try again!", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    // ToDo: clear input fields
                    Snackbar.make(mRecyclerView, "Error in adding item. Please try again!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
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

    private void populateItems(){
        groupService.getGroupByGroupName(groupName, new GroupServiceCallbacks.GetGroupByGroupNameTaskCallback() {
            @Override
            public void onComplete(Group group) {
                List<String> itemIds = group.getItems();
                for(String itemId: itemIds) {
                    itemService.getItemByItemId(itemId, new ItemServiceCallbacks.GetItemByItemIdTaskCallback() {
                        @Override
                        public void onComplete(Item item) {
                            mItemList.add(item);
                            Collections.sort(mItemList, Item.itemDateRecentComparator);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
}