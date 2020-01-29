package com.edwino.detector;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListOnline extends AppCompatActivity {

    //firebase database

    DatabaseReference onlineRef,currentUserRef,counterRef;
    FirebaseRecyclerAdapter<User,ListOnlineViewHolder>adapter;

    //Location
//    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
//    private static final int PLAY_SERVICES_RES_REQUEST = 7172;
//    private LocationRequest mlocationRequest;
//    private GoogleApiClient mGoogleApiClient;
//    private Location mLocation;


    RecyclerView listonline;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_online);

        listonline = findViewById(R.id.onlinelist);
        listonline.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listonline.setLayoutManager(layoutManager);

        Toolbar toolbar = findViewById(R.id.myToolBar);
        toolbar.setTitle("Detector System");
        setSupportActionBar(toolbar);

        //Firebase
        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        counterRef  = FirebaseDatabase.getInstance().getReference("lastOnline");
        currentUserRef = FirebaseDatabase.getInstance().getReference("lastOnline")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        setUpSystem();

//        updateList();

    }

//    private void updateList() {
//        adapter = new FirebaseRecyclerAdapter<User, ListOnlineViewHolder>(
//                User.class,
//                ListOnlineViewHolder.class,
//                counterRef
//        ) {
//            @Override
//            protected void populateViewHolder(ListOnlineViewHolder viewHolder, User model, int position) {
//                viewHolder.txtEmail.setText(model.getEmail());
//            }
//        };
//    }

    private void setUpSystem() {
        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Boolean.class)){
                    currentUserRef.onDisconnect().removeValue();
                    counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(new User(FirebaseAuth.getInstance().getCurrentUser().getEmail(),"Online"));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        counterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    User user = postSnapshot.getValue(User.class);
                    Log.d("LOG",""+user.getEmail()+"is"+user.getStatus());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu,menu);
        return true;
    }
}
