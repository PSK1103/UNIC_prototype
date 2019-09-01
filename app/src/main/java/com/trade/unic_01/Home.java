package com.trade.unic_01;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ListView lv_languages;
    Toolbar toolbar;

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TabLayout tabLayout;

    private TextView userName,userEmail;
    private ImageView userPhoto;

    ArrayAdapter<String> language_adapter;
    ArrayList<String> languagesarraylist;
    ArrayList<String> search_result_arraylist;
    private String keyword;
    private String imglink;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        viewPager = findViewById(R.id.nav_viewpager);
        pagerAdapter = new FragmentAdapter(this,getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout = findViewById(R.id.nav_tabview);
        tabLayout.setupWithViewPager(viewPager);
         toolbar = findViewById(R.id.toolbar);


        //method for initialisation
        init();

        mAuth = FirebaseAuth.getInstance();

        //setting array adaptet to listview

        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setIcon(R.drawable.logo2);
        actionBar.setTitle("   ");
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        userName = header.findViewById(R.id.user_FullName);
        userPhoto = header.findViewById(R.id.user_photo);
        userEmail = header.findViewById(R.id.user_Email);
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userName.setText(documentSnapshot.getString("first_name")+" "+documentSnapshot.getString("last_name"));
                userEmail.setText(documentSnapshot.getString("email_address"));
                imglink = documentSnapshot.getString("userimage");
            }
        });
        Glide.with(Home.this).load(imglink).into(userPhoto);

    }
    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("CodesFor Languages");
        languagesarraylist = new ArrayList<>();
        search_result_arraylist = new ArrayList<>();

        //adding few data to arraylist
        languagesarraylist.add("SQL");
        languagesarraylist.add("JAVA");
        languagesarraylist.add("JAVA SCRIPT ");
        languagesarraylist.add("C#");
        languagesarraylist.add("PYTHON");
        languagesarraylist.add("C++");
        languagesarraylist.add("IOS");
        languagesarraylist.add("ANDROID");
        languagesarraylist.add("PHP");
        languagesarraylist.add("LARAVEL");

        // initialising array adapter

        language_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,languagesarraylist);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem search_item = menu.findItem(R.id.mi_search);

        SearchView searchView = (SearchView) search_item.getActionView();
        searchView.setFocusable(false);
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String s) {

                //clear the previous data in search arraylist if exist
                search_result_arraylist.clear();

                keyword = s.toUpperCase();

                //checking language arraylist for items containing search keyword

                for(int i =0 ;i < languagesarraylist.size();i++){
                    if(languagesarraylist.get(i).contains(keyword)){
                        search_result_arraylist.add(languagesarraylist.get(i).toString());
                    }
                }

                language_adapter = new ArrayAdapter<String>(Home.this,android.R.layout.simple_list_item_1,search_result_arraylist);
                lv_languages.setAdapter(language_adapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        }else if (id == R.id.nav_tools) {
            Intent intent=new Intent(Home.this,ShopProfile.class);
            //intent.putExtra("pic",shopsSubscribed.get(0).imageid);
            startActivity(intent);

        }
        else if (id == R.id.nav_my_shop) {
            Intent intent =new Intent(Home.this, MyShop.class);
            startActivity(intent);

        }else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {


        } else if (id == R.id.logout){
            String Phone = mAuth.getCurrentUser().getPhoneNumber();
            mAuth.signOut();
            Intent intent = new Intent(Home.this,Login.class);
            intent.putExtra("Phone",Phone);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
