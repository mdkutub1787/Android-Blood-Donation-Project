package com.kutub.bloodandbuddies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kutub.bloodandbuddies.Adapter.UserAdapter;
import com.kutub.bloodandbuddies.Model.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout appDrawerLayout;
    private Toolbar appToolbar;
    private NavigationView appNavigationView;

    private CircleImageView nav_user_image;
    private TextView nav_user_name, nav_user_email, nav_user_pnumber, nav_user_bloodgroup, nav_user_type;

    private DatabaseReference userRef;

    private RecyclerView appRecyclerView;
    private ProgressBar appProgressbar;
    private List<User> userList;
    private UserAdapter userAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        appToolbar = findViewById(R.id.toobar);
        setSupportActionBar(appToolbar);
        getSupportActionBar().setTitle("Blood and Buddies App");

        appDrawerLayout = findViewById(R.id.drawerLayout);
        appNavigationView = findViewById(R.id.appNavigationView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(DashboardActivity.this, appDrawerLayout, appToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        appDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        appNavigationView.setNavigationItemSelectedListener(this);

        appProgressbar = findViewById(R.id.progressbar);
        appRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        appRecyclerView.setLayoutManager(layoutManager);

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(DashboardActivity.this, userList);
        appRecyclerView.setAdapter(userAdapter);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child("type").getValue(String.class);
                if(type != null) {
                    if(type.equals("donor")){
                        readRecipients();
                    } else {
                        readDonor();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });

        nav_user_image = appNavigationView.getHeaderView(0).findViewById(R.id.nav_user_image);
        nav_user_name = appNavigationView.getHeaderView(0).findViewById(R.id.nav_user_name);
        nav_user_pnumber = appNavigationView.getHeaderView(0).findViewById(R.id.nav_user_pnumber);
        nav_user_email = appNavigationView.getHeaderView(0).findViewById(R.id.nav_user_email);
        nav_user_bloodgroup = appNavigationView.getHeaderView(0).findViewById(R.id.nav_user_bloodgroup);
        nav_user_type = appNavigationView.getHeaderView(0).findViewById(R.id.nav_user_type);

        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String name = snapshot.child("name").getValue(String.class);
                    nav_user_name.setText(name != null ? name : "");

                    String email = snapshot.child("email").getValue(String.class);
                    nav_user_email.setText(email != null ? email : "");

                    String phonenumber = snapshot.child("phonenumber").getValue(String.class);
                    nav_user_pnumber.setText(phonenumber != null ? phonenumber : "");

                    String bloodgroup = snapshot.child("bloodgroup").getValue(String.class);
                    nav_user_bloodgroup.setText(bloodgroup != null ? bloodgroup : "");

                    String type = snapshot.child("type").getValue(String.class);
                    nav_user_type.setText(type != null ? type : "");

                    if(snapshot.hasChild("profilepictureurl")){
                        String imageUrl = snapshot.child("profilepictureurl").getValue(String.class);
                        Glide.with(getApplicationContext()).load(imageUrl).into(nav_user_image);
                    } else {
                        nav_user_image.setImageResource(R.drawable.profile);
                    }

                    Menu nav_menu = appNavigationView.getMenu();
                    if (type.equals("donor")){
                        nav_menu.findItem(R.id.notifications).setVisible(true);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readDonor() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = reference.orderByChild("type").equalTo("donor");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
                appProgressbar.setVisibility(View.GONE);

                if(userList.isEmpty()){
                    Toast.makeText(DashboardActivity.this, "No Donors", Toast.LENGTH_SHORT).show();
                    appProgressbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "Error fetching donors", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readRecipients() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = reference.orderByChild("type").equalTo("recipient");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
                appProgressbar.setVisibility(View.GONE);

                if(userList.isEmpty()){
                    Toast.makeText(DashboardActivity.this, "No Recipient", Toast.LENGTH_SHORT).show();
                    appProgressbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "Error fetching recipients", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Get the selected item's ID
        int id = item.getItemId();

        // Using if-else instead of switch-case for navigation
        if (id == R.id.sent_mail) {
            // Navigate to SentEmailActivity
            Intent intent2 = new Intent(DashboardActivity.this, SentEmailActivity.class);
            startActivity(intent2);
        } else if (id == R.id.compatible) {
            // Navigate to CatogarySelectedActivity with extra data
            Intent intent3 = new Intent(DashboardActivity.this, CatogarySelectedActivity.class);
            intent3.putExtra("group", "Compatible with me");
            startActivity(intent3);
        } else if (id == R.id.aplus) {
            Intent intent4 = new Intent(DashboardActivity.this, CatogarySelectedActivity.class);
            intent4.putExtra("group", "A+");
            startActivity(intent4);
        } else if (id == R.id.aminus) {
            Intent intent5 = new Intent(DashboardActivity.this, CatogarySelectedActivity.class);
            intent5.putExtra("group", "A-");
            startActivity(intent5);
        } else if (id == R.id.abplus) {
            Intent intent6 = new Intent(DashboardActivity.this, CatogarySelectedActivity.class);
            intent6.putExtra("group", "AB+");
            startActivity(intent6);
        } else if (id == R.id.abminus) {
            Intent intent7 = new Intent(DashboardActivity.this, CatogarySelectedActivity.class);
            intent7.putExtra("group", "AB-");
            startActivity(intent7);
        } else if (id == R.id.bplus) {
            Intent intent8 = new Intent(DashboardActivity.this, CatogarySelectedActivity.class);
            intent8.putExtra("group", "B+");
            startActivity(intent8);
        } else if (id == R.id.bminus) {
            Intent intent9 = new Intent(DashboardActivity.this, CatogarySelectedActivity.class);
            intent9.putExtra("group", "B-");
            startActivity(intent9);
        } else if (id == R.id.oplus) {
            Intent intent10 = new Intent(DashboardActivity.this, CatogarySelectedActivity.class);
            intent10.putExtra("group", "O+");
            startActivity(intent10);
        } else if (id == R.id.ominus) {
            Intent intent11 = new Intent(DashboardActivity.this, CatogarySelectedActivity.class);
            intent11.putExtra("group", "O-");
            startActivity(intent11);
        } else if (id == R.id.profile) {
            // Navigate to ProfileActivity
            Intent intent12 = new Intent(DashboardActivity.this, ProfileActivity.class);
            startActivity(intent12);
        } else if (id == R.id.notifications) {
            // Navigate to NotificationActivity
            Intent intent13 = new Intent(DashboardActivity.this, NotificationActivity.class);
            startActivity(intent13);
        } else if (id == R.id.logout) {
            // Sign out and navigate to LoginActivity
            FirebaseAuth.getInstance().signOut();
            Intent intent14 = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(intent14);
            finish(); // End the current activity
        } else if (id == R.id.about) {
            // Navigate to AboutActivity
            Intent intent15 = new Intent(DashboardActivity.this, AboutActivity.class);
            startActivity(intent15);
        }

        // Close the navigation drawer
        appDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (appDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            appDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
