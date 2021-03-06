package com.example.chatappfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatappfirebase.Fragments.ChatsFragment;
import com.example.chatappfirebase.Fragments.ProfileFragment;
import com.example.chatappfirebase.Fragments.UsersFragment;
import com.example.chatappfirebase.Model.Users;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mauth;
    Toolbar toolbar;

    CircleImageView imageView;
    TextView username;

    DatabaseReference reference;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mauth = FirebaseAuth.getInstance();
        imageView = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);

        //Toolbar
        toolbar = findViewById(R.id.toolbarmain);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager2 pager2 = findViewById(R.id.viewPager);
        ViewStateAdapter fragmentAdapter = new ViewStateAdapter(getSupportFragmentManager(), getLifecycle());

        fragmentAdapter.addFragment(new ChatsFragment(), "Chats");
        fragmentAdapter.addFragment(new UsersFragment(), "Users");
        fragmentAdapter.addFragment(new ProfileFragment(), "Profile");

        pager2.setAdapter(fragmentAdapter);

        // Tab Layout
        TabLayout tablayout = findViewById(R.id.tablayout);
        tablayout.addTab(tablayout.newTab().setText("Chats"));
        tablayout.addTab(tablayout.newTab().setText("Users"));
        tablayout.addTab(tablayout.newTab().setText("Profile"));

        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // change tab when swiping
        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tablayout.selectTab(tablayout.getTabAt(position));
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                username.setText(users.getUsername());

                if(users.getImageURL().equals("default")){
                    imageView.setImageResource(R.drawable.user);
                }
                else{
                    Glide.with(getApplicationContext()).load(users.getImageURL()).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private class ViewStateAdapter extends FragmentStateAdapter {
        ArrayList<Fragment> fragments;
        ArrayList<String> titles;

        public ViewStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
            fragments = new ArrayList<>();
            titles = new ArrayList<>();
        }


        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }

        public CharSequence getPageTitle(int position){
            return titles.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logout){
            mauth.signOut();
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}