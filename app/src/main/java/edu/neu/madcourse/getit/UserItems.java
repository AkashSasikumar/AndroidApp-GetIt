package edu.neu.madcourse.getit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.neu.madcourse.getit.callbacks.GroupServiceCallbacks;
import edu.neu.madcourse.getit.callbacks.ItemServiceCallbacks;
import edu.neu.madcourse.getit.callbacks.UserServiceCallbacks;
import edu.neu.madcourse.getit.models.Group;
import edu.neu.madcourse.getit.models.Item;
import edu.neu.madcourse.getit.models.User;
import edu.neu.madcourse.getit.services.GroupService;
import edu.neu.madcourse.getit.services.ItemService;
import edu.neu.madcourse.getit.services.UserService;

public class UserItems extends AppCompatActivity {


    private static final String INTENT_LOGGED_USER_ID = "LOGGED_USER_ID";
    private static final String ITEMS_YOU_POSTED = "Posted by you";
    private static final String ITEMS_OTHERS_GETTING_FOR_YOU = "Getting for others";


    private List<Item> mItemList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ItemAdaptor mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private UserService userService;
    private GroupService groupService;
    private ItemService itemService;
    private String mUserID;

    // Display item details dialog
    private AlertDialog mItemDetailsDialog;
    private View mItemDetailsView;
    private ImageView mdImage;
    private TextView mdName, mdInstructions, mdQuantity, mdPreferredStore, mdPreferredBrand, mdPostedBy, mdPostedOn;
    private Button mdButtonGetIt;

    List<Item> mItemsGetting = new ArrayList<>();
    List<Item> mItemsPosted = new ArrayList<>();
    private boolean displayItemsPosted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_items);

        // get data from intent
        mUserID = getIntent().getStringExtra(INTENT_LOGGED_USER_ID);

        userService = new UserService();
        groupService = new GroupService();
        itemService = new ItemService();

        setToolbarTitle(ITEMS_YOU_POSTED);
        setupRecyclerView();
        populateItemsForUser();
        createItemDetailsDialog();

    }

    private void setToolbarTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    private void setupRecyclerView(){

        mRecyclerView = findViewById(R.id.recycler_user_items);
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
                return;
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_items_menu, menu);
        //MenuItem sortByName = menu.findItem(R.id.sort_by_name);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.display_user_items){
            if(displayItemsPosted){
                // current display is items posted, change it to items getting
                setToolbarTitle(ITEMS_OTHERS_GETTING_FOR_YOU);
                // set the menu title to the other
                item.setTitle(ITEMS_YOU_POSTED);
                mItemList.clear();
                for (Item gettingItem : mItemsGetting){
                    mItemList.add(gettingItem);
                }
                displayItemsPosted = false;
            }else{
                // current display is items getting, change it to items posted
                setToolbarTitle(ITEMS_YOU_POSTED);
                // set the menu title to the other
                item.setTitle(ITEMS_OTHERS_GETTING_FOR_YOU);
                mItemList.clear();
                for (Item postedItem : mItemsPosted){
                    mItemList.add(postedItem);
                }
                displayItemsPosted = true;
            }
        }
        mAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
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
        //mdButtonGetIt = (Button) mItemDetailsView.findViewById(R.id.get_item_display);
//        // set listeners
//        mdButtonGetIt.setOnClickListener(v->getItem());
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

//        if (currentItem.getUserGettingIt() != null){
//            mdButtonGetIt.setText(currentItem.getUserGettingIt().getFullName() + " is already getting it!");
//            mdButtonGetIt.setBackgroundColor(GREY_COLOR);
//        }else{
//            mdButtonGetIt.setText("I'll get it!");
//            mdButtonGetIt.setBackgroundColor(GREEN_COLOR);
//        }
        mItemDetailsDialog.show();
    }

    private void populateItemsForUser(){

//        for (int i = 1; i <=10 ; i++){
//            Item item = new Item("name",  "quantity", "preferredStore","preferredBrand", "postedDateTime",
//                     null, null, null, "instructions");
//            mItemList.add(item);
//            mAdapter.notifyDataSetChanged();
//        }
        userService.getUserByUserId(mUserID, new UserServiceCallbacks.GetUserByUserNameTaskCallback() {
            @Override
            public void onComplete(User user) {
                List<String> itemsGetting = user.getUserItemsGetting();
                List<String> itemsPosted = user.getUserItemsPosted();

                // by default display items that the user needs to get
                for (String itemID: itemsGetting){
                    itemService.getItemByItemId(itemID, new ItemServiceCallbacks.GetItemByItemIdTaskCallback() {
                        @Override
                        public void onComplete(Item item) {
                            mItemsGetting.add(item);
                        }
                    });
                }

                for (String itemID: itemsPosted){
                    itemService.getItemByItemId(itemID, new ItemServiceCallbacks.GetItemByItemIdTaskCallback() {
                        @Override
                        public void onComplete(Item item) {
                            mItemsPosted.add(item);
                            mItemList.add(item);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

}