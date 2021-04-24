package edu.neu.madcourse.getit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.neu.madcourse.getit.callbacks.FCMServiceCallBacks;
import edu.neu.madcourse.getit.callbacks.GroupServiceCallbacks;
import edu.neu.madcourse.getit.callbacks.UserServiceCallbacks;
import edu.neu.madcourse.getit.models.Group;
import edu.neu.madcourse.getit.models.User;
import edu.neu.madcourse.getit.services.FCMService;
import edu.neu.madcourse.getit.services.GroupService;
import edu.neu.madcourse.getit.services.UserService;

public class YourGroupsActivity extends AppCompatActivity implements View.OnClickListener, OnCompleteListener<Void> {
    private static final String FETCHING_FCM_REGISTRATION_TOKEN_STATUS = "FETCHING_FCM_REGISTRATION_TOKEN_STATUS:  ";
    //EditText mGroupName, mGroupCode;
    Button join_group_btn;
    Button create_group_btn;
    Button view_my_items;
    List<GroupView> groups;
    UserService userService;
    GroupService groupService;
    FCMService fcmService;
    GroupsRVAdapter mGroupAdapter;
    RecyclerView groupsRV;
    ConstraintLayout mLayout;

    private FirebaseAuth fAuth;
    private String userID;
    private String userName;

    private static final String INTENT_GROUP_NAME = "GROUP_NAME";
    private static final String INTENT_GROUP_ID = "GROUP_ID";
    private static final String INTENT_GROUP_CODE = "GROUP_CODE";
    private static final String INTENT_LOGGED_USER_ID = "LOGGED_USER_ID";


    // Create group dialog
    private AlertDialog mCreateGroupDialog;
    private View mCreateGroupView;
    private EditText mCreateGroupName;
    private Button mCreateGroupDialogBtn;

    // Join group dialog
    private AlertDialog mJoinGroupDialog;
    private View mJoinGroupView;
    private EditText mJoinGroupCode;
    private Button mJoinGroupDialogBtn;


    // location service
    private static final String TAG = YourGroupsActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    /**
     * Tracks whether the user requested to add or remove geofences, or to do neither.
     */
    private enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

    /**
     * Provides access to the Geofencing API.
     */
    private GeofencingClient mGeofencingClient;

    /**
     * The list of geofences used in this sample.
     */
    private ArrayList<Geofence> mGeofenceList;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;


    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_groups);
        //getSupportActionBar().setTitle("Your Groups");
//        mGroupName = findViewById(R.id.group_name_field);
//        mGroupCode = findViewById(R.id.group_code_field);
        join_group_btn = findViewById(R.id.join_group_btn);
        create_group_btn = findViewById(R.id.create_group_btn);
        view_my_items = findViewById(R.id.view_my_items);
        mLayout = findViewById(R.id.your_groups_view);
        join_group_btn.setOnClickListener(this);
        create_group_btn.setOnClickListener(this);
        view_my_items.setOnClickListener(this);

        // recycler view
        groupsRV = findViewById(R.id.recyclerView);
        groupsRV.setHasFixedSize(true);
        groups = new ArrayList<>();
        mGroupAdapter = new GroupsRVAdapter(groups);
        groupsRV.setAdapter(mGroupAdapter);
        groupsRV.setLayoutManager(new LinearLayoutManager(YourGroupsActivity.this));
        mGroupAdapter.setOnGroupClickListener(new GroupsRVAdapter.OnGroupClickListener() {
            @Override
            public void onGroupClick(int position) {
                GroupView clickedGroup = groups.get(position);
                String groupName = clickedGroup.getGroupName();
                String groupID = clickedGroup.getGroupID();
                String groupCode = clickedGroup.getGroupCode();
                // Snackbar.make(groupsRV, "Clicked on group: "+ groupName, Snackbar.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), GroupItems.class);
                intent.putExtra(INTENT_GROUP_NAME, groupName);
                intent.putExtra(INTENT_GROUP_ID, groupID);
                intent.putExtra(INTENT_GROUP_CODE, groupCode);
                startActivity(intent);
            }
        });

        createCreateGroupDialog();
        createJoinGroupDialog();
        startUpLocationService();

        userService = new UserService();
        groupService = new GroupService();
        fcmService = new FCMService();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        fAuth = FirebaseAuth.getInstance();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d(FETCHING_FCM_REGISTRATION_TOKEN_STATUS, "failed ", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        Log.d(FETCHING_FCM_REGISTRATION_TOKEN_STATUS, "success for user:  " + fAuth.getCurrentUser().getUid() + " token: " + token);

                        userService.updateUserDeviceToken(fAuth.getCurrentUser().getUid(), token);
                    }
                });

        // get the user info from firebase
        userService.getUserByUserId(userID, new UserServiceCallbacks.GetUserByUserNameTaskCallback() {
            @Override
            public void onComplete(User user) {
                userName = user.getFullName();
                List<String> groupNames = user.getGroups();
                for (int i = 0; i < groupNames.size(); i++) {

                    groupService.getGroupNameByGroupID(groupNames.get(i), new GroupServiceCallbacks.GetGroupNameFromGroupIDCallback() {
                        @Override
                        public void onComplete(Group group) {
                            GroupView g = new GroupView(Long.toString(group.getGroup_code()),
                                    group.getGroup_name(), group.getGroupId());
                            groups.add(g);
                            mGroupAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    private void startUpLocationService() {

        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<>();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

//        setButtonsEnabledState();

        // Get the geofences used. Geofence data is hard coded in this sample.
        populateGeofenceList();

        mGeofencingClient = LocationServices.getGeofencingClient(this);
    }

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofencesOnComplete(View view) {
        if (!checkPermissions()) {
            mPendingGeofenceTask = YourGroupsActivity.PendingGeofenceTask.ADD;
            requestPermissions();
            return;
        }
        addGeofences();
    }


    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            addGeofencesOnComplete(mLayout);
            performPendingGeofenceTask();
        }
    }

    /**
     * Performs the geofencing task that was pending until location permission was granted.
     */
    private void performPendingGeofenceTask() {
        if (mPendingGeofenceTask == YourGroupsActivity.PendingGeofenceTask.ADD) {
            addGeofences();
        } else if (mPendingGeofenceTask == YourGroupsActivity.PendingGeofenceTask.REMOVE) {
            removeGeofences();
        }
    }

    /**
     * Adds geofences. This method should be called after the user has granted the location
     * permission.
     */
    @SuppressWarnings("MissingPermission")
    private void addGeofences() {
        if (!checkPermissions()) {
            showSnackbar(getString(R.string.insufficient_permissions));
            return;
        }

        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(this);
    }

    /**
     * Removes geofences. This method should be called after the user has granted the location
     * permission.
     */
    @SuppressWarnings("MissingPermission")
    private void removeGeofences() {
        if (!checkPermissions()) {
            showSnackbar(getString(R.string.insufficient_permissions));
            return;
        }

        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale, R.string.settings,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            // Request permission
                            ActivityCompat.requestPermissions(YourGroupsActivity.this,
                                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);

                            // Build intent that displays the App settings screen.
//                            Intent intent = new Intent();
//                            intent.setAction(
//                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                            Uri uri = Uri.fromParts("package",
//                                    BuildConfig.APPLICATION_ID, null);
//                            intent.setData(uri);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
                        }
                    });


//            showSnackbar(R.string.permission_rationale, android.R.string.ok,
//                    new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            // Request permission
//                            ActivityCompat.requestPermissions(YourGroupsActivity.this,
//                                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
//                                    REQUEST_PERMISSIONS_REQUEST_CODE);
//                        }
//                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(YourGroupsActivity.this,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }


    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    private void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : Constants.STOP_N_SHOP_LANDMARKS.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build());
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted.");
                performPendingGeofenceTask();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.

                showSnackbar(R.string.permission_denied_explanation, android.R.string.ok,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Request permission
//                                ActivityCompat.requestPermissions(YourGroupsActivity.this,
//                                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
//                                        REQUEST_PERMISSIONS_REQUEST_CODE);
                            }
                        });

//                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
//                        new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                // Build intent that displays the App settings screen.
//                                Intent intent = new Intent();
//                                intent.setAction(
//                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                Uri uri = Uri.fromParts("package",
//                                        BuildConfig.APPLICATION_ID, null);
//                                intent.setData(uri);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                            }
//                        });
                mPendingGeofenceTask = YourGroupsActivity.PendingGeofenceTask.NONE;
            }
        }
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }


    /**
     * Runs when the result of calling {@link #addGeofences()} and/or {@link #removeGeofences()}
     * is available.
     *
     * @param task the resulting Task, containing either a result or error.
     */
    @Override
    public void onComplete(@NonNull Task<Void> task) {
        mPendingGeofenceTask = YourGroupsActivity.PendingGeofenceTask.NONE;
        if (task.isSuccessful()) {
            updateGeofencesAdded(!getGeofencesAdded());
//            setButtonsEnabledState();

            int messageId = getGeofencesAdded() ? R.string.geofences_added :
                    R.string.geofences_removed;
            Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
            Log.w(TAG, errorMessage);
        }
    }

    /**
     * Returns true if geofences were added, otherwise false.
     */
    private boolean getGeofencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                Constants.GEOFENCES_ADDED_KEY, false);
    }

    /**
     * Stores whether geofences were added ore removed in {@link SharedPreferences};
     *
     * @param added Whether geofences were added or removed.
     */
    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(Constants.GEOFENCES_ADDED_KEY, added)
                .apply();
    }

    @Override
    public void onClick(View v) {

//        String groupCode = mGroupCode.getText().toString().trim();
//        String groupName = mGroupName.getText().toString().trim();
//        mGroupCode.setText("");
//        mGroupName.setText("");

        if (v.getId() == R.id.join_group_btn) {
            showJoinGroupDialog();

        } else if (v.getId() == R.id.create_group_btn) {
            showCreateGroupDialog();

        } else if (v.getId() == R.id.view_my_items) {
            Intent intent = new Intent(getApplicationContext(), UserItems.class);
            intent.putExtra(INTENT_LOGGED_USER_ID, fAuth.getCurrentUser().getUid());
            startActivity(intent);
        }
    }

    private void createCreateGroupDialog() {
        // set layout
        AlertDialog.Builder createGroupBuilder = new AlertDialog.Builder(this);
        mCreateGroupView = getLayoutInflater().inflate(R.layout.create_group_dialog, null);
        createGroupBuilder.setView(mCreateGroupView);
        mCreateGroupDialog = createGroupBuilder.create();

        // get views
        mCreateGroupName = mCreateGroupView.findViewById(R.id.group_name_dialog);
        mCreateGroupDialogBtn = mCreateGroupView.findViewById(R.id.create_group_btn);

        mCreateGroupDialogBtn.setOnClickListener(v -> createGroupBtnOnClick());
    }

    private void createGroupBtnOnClick() {
        String groupName = mCreateGroupName.getText().toString().trim();
        // Validate input fields
        if (TextUtils.isEmpty(groupName)) {
            mCreateGroupName.setError("Group Name is required.");
            return;
        }

        mCreateGroupDialog.hide();

        groupService.createGroup(groupName, new GroupServiceCallbacks.CreateGroupTaskCallback() {
            @Override
            public void onComplete(Group group) {
                if (group != null) {
                    // add current user to the created group
                    groupService.addUserToGroupByGroupIdAndUserId(userID, group.getGroupId(), new GroupServiceCallbacks.AddUserToGroupTaskCallback() {
                        @Override
                        public void onComplete(boolean isSuccess) {
                            if (isSuccess) {
                                groups.add(new GroupView(Long.toString(group.getGroup_code()),
                                        groupName, group.getGroupId()));
                                mGroupAdapter.notifyDataSetChanged();
                                Snackbar.make(mLayout, "Group " + groupName + " created successfully!", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Snackbar.make(mCreateGroupView, "Sorry, could not create a group!", Snackbar.LENGTH_LONG).show();
                }
            }
        });


    }

    private void showCreateGroupDialog() {
        // show the input dialog
        mCreateGroupName.setText("");
        mCreateGroupDialog.show();
    }


    private void createJoinGroupDialog() {
        // set layout
        AlertDialog.Builder joinGroupBuilder = new AlertDialog.Builder(this);
        mJoinGroupView = getLayoutInflater().inflate(R.layout.join_group_dialog, null);
        joinGroupBuilder.setView(mJoinGroupView);
        mJoinGroupDialog = joinGroupBuilder.create();

        // get views
        mJoinGroupCode = mJoinGroupView.findViewById(R.id.join_group_code);
        mJoinGroupDialogBtn = mJoinGroupView.findViewById(R.id.join_group_dialog_btn);

        mJoinGroupDialogBtn.setOnClickListener(v -> joinGroupBtnOnClick());
    }

    private void joinGroupBtnOnClick() {

        String groupCode = mJoinGroupCode.getText().toString().trim();
        // Validate input fields
        if (TextUtils.isEmpty(groupCode)) {
            mJoinGroupCode.setError("Group Code is required.");
            return;
        }

        mJoinGroupDialog.hide();


        for (GroupView group : groups) {
            if (group.getGroupCode().equals(groupCode)) {
                Snackbar.make(mLayout, "You are already a member of " + group.getGroupName(), Snackbar.LENGTH_LONG).show();
                return;
            }
        }

        fcmService.sendNewGroupMemberNotification(groupCode, userName, new FCMServiceCallBacks.sendNewGroupMemberNotificationCallback() {
            @Override
            public void onComplete() {
                groupService.addUserToGroupByGroupCode(userID, groupCode, new GroupServiceCallbacks.AddUserByGroupCodeTaskCallback() {
                    public void onComplete(Group group) {
                        if (group != null) {
                            groups.add(new GroupView(Long.toString(group.getGroup_code()),
                                    group.getGroup_name(), group.getGroupId()));
                            mGroupAdapter.notifyDataSetChanged();
                            Snackbar.make(mLayout, "Joined group " + group.getGroup_name() + " successfully", Snackbar.LENGTH_LONG).show();

                        } else {
                            Snackbar.make(mLayout, "Sorry, group with the given code does not exist!", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });


    }

    private void showJoinGroupDialog() {
        // show the input dialog
        mJoinGroupCode.setText("");
        mJoinGroupDialog.show();
    }

}