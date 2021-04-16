package edu.neu.madcourse.getit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YourGroupsActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    EditText mGroupName, mGroupCode;
    Button join_group_btn;
    List<GroupView> groups;
    private TextView locatorTextView;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_groups);
        mGroupName = findViewById(R.id.group_name_field);
        mGroupCode = findViewById(R.id.group_code_field);
        join_group_btn = findViewById(R.id.join_group_btn);
        join_group_btn.setOnClickListener(this);
        final RecyclerView groupsRV = findViewById(R.id.recyclerView);
        groups = new ArrayList<>();

        // hardcoding the groups.
        //TODO: get the groups from the firebase
        for (int i = 0; i < 15; i++) {
            GroupView g = new GroupView("GroupCode" + i, "GroupName" + i);
            groups.add(g);
        }
        final GroupsRVAdapter groupsRVAdapter = new GroupsRVAdapter(groups, this);

        groupsRV.setAdapter(groupsRVAdapter);
        groupsRV.setLayoutManager(new LinearLayoutManager(this));

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locatorTextView = (TextView) findViewById(R.id.textView_locator);

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    getResources().getInteger(R.integer.location_request_code));
        } else {
            initializeLocationUpdates();
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.join_group_btn) {
            String groupCode = mGroupCode.getText().toString().trim();
            String groupName = mGroupName.getText().toString().trim();
            if (TextUtils.isEmpty(groupName) && TextUtils.isEmpty(groupCode)) {
                mGroupName.setError("At least one of Group Name, Group Code fields is required");
            } else {
                Snackbar.make(v, "Joined the " + groupName + " group successfully", Snackbar.LENGTH_LONG).show();
            }

            // add the user to the group and update the recycler view to reflect the new group

            // ToDo: remove test code
            startActivity(new Intent(getApplicationContext(),GroupItems.class));

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeLocationUpdates();
            } else {
                locatorTextView.setText(getString(R.string.NoLocationAccess));
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        locatorTextView.setText(getString(R.string.LatitudeAndLongitude, String.valueOf(location.getLatitude()),
                String.valueOf(location.getLongitude())));
        locationManager.removeUpdates(this);
    }

    private void initializeLocationUpdates() {
        try {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String enabledProvider = locationManager.getBestProvider(criteria, true);
            if (enabledProvider != null) {
                locationManager.requestLocationUpdates(enabledProvider,getResources().getInteger(R.integer.location_update_min_time),
                        getResources().getInteger(R.integer.location_update_min_dist), this);
            }
        } catch (SecurityException ignored) {}
    }
}