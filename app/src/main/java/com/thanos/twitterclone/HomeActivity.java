package com.thanos.twitterclone;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    List <Tweet> tweetList = new ArrayList<Tweet>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FirebaseApp.initializeApp(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://twitter-clone-2906-default-rtdb.asia-southeast1.firebasedatabase.app");
        TweetAdapter tweetAdapter = new TweetAdapter(HomeActivity.this, tweetList);
        RecyclerView tweetRv  = findViewById(R.id.tweet_Rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        tweetRv.setLayoutManager(layoutManager);
        tweetRv.setAdapter(tweetAdapter);

        // navigation menu
        NavigationView navigationView = findViewById(R.id.navigation_view);
        Menu menu = navigationView.getMenu();
        ImageButton btnMenu = findViewById(R.id.btn_menu);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);


        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawer.isDrawerOpen(GravityCompat.START)){
                    drawer.closeDrawer(GravityCompat.START);
                }else{
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        View headerView = navigationView.getHeaderView(0);
        TextView txtHeaderName = headerView.findViewById(R.id.txt_display_name);
        TextView txtHeaderUsername = headerView.findViewById(R.id.txt_username);
        txtHeaderName.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("display_name", ""));
        txtHeaderUsername.setText("@" + PreferenceManager.getDefaultSharedPreferences(this).getString("uname", ""));



        DatabaseReference tweetRef = database.getReference("tweets");
        tweetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {
                    tweetList.clear();
                    for (DataSnapshot tweetDataSnapshot : snapshot.getChildren()) {
                        tweetList.add(new Tweet(tweetDataSnapshot.child("username").getValue().toString(), tweetDataSnapshot.child("display_name").getValue().toString(),
                                tweetDataSnapshot.child("message").getValue().toString(), tweetDataSnapshot.child("time_published").getValue().toString()));
                    }
                    tweetAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Something went wrong with database",Toast.LENGTH_LONG).show();

            }
        });
        FloatingActionButton fab = findViewById(R.id.btn_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, NewTweetActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            DrawerLayout drawerLayout  = findViewById(R.id.drawer_layout);
            int id  = item.getItemId();
            if(id == R.id.item_10){
                FirebaseAuth.getInstance().signOut();
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("is_user_logged", false).commit();
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString("display_name","").clear().commit();
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString("uname","").clear().commit();
                finish();
                Toast.makeText(this, "You clicked to logout ", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(HomeActivity.this, LauncherActivity.class);
                startActivity(intent);

            }

        return true;
    }
}