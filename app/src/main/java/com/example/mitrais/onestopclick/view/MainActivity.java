package com.example.mitrais.onestopclick.view;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.dagger.component.DaggerMainActivityComponent;
import com.example.mitrais.onestopclick.dagger.component.MainActivityComponent;
import com.example.mitrais.onestopclick.model.Profile;
import com.example.mitrais.onestopclick.viewmodel.MainViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ImageView imgProfile;
    private TextView txtEmail;
    private Task productSyncTask;

    @Inject
    MainViewModel viewModel;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        View navHeaderView = navView.getHeaderView(0);
        imgProfile = navHeaderView.findViewById(R.id.img_profile);
        txtEmail = navHeaderView.findViewById(R.id.txt_email);

        setSupportActionBar(toolbar);
        setTitle(getString(R.string.main_menu));

        // Initialize drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_bar_open, R.string.navigation_bar_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);

        // Initialize first shown fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProductFragment()).commit();
            navView.setCheckedItem(R.id.movie);
        }

        // Initialize dagger injection
        MainActivityComponent component = DaggerMainActivityComponent.builder()
                .mainActivity(this)
                .build();
        component.inject(this);

        // Initialize user profile
        FirebaseUser user = viewModel.getCurrentUser();
        viewModel.getProfileByEmail(user.getEmail()).observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(@Nullable Profile profile) {
                if(profile != null){
                    txtEmail.setText(profile.getEmail());
                    Picasso.get().load(profile.getProfileImageUri()).placeholder(R.drawable.ic_launcher_background).into(imgProfile);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                doLogout();
                return true;
            case R.id.sync:
                if (isProductSyncInProgress())
                    Toasty.info(this, getString(R.string.product_sync_in_progress), Toast.LENGTH_SHORT).show();
                else
                    syncProductData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.movie: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProductFragment()).commit();
                break;
            }
            case R.id.music: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProductFragment()).commit();
                break;
            }
            case R.id.book: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProductFragment()).commit();
                break;
            }
            case R.id.profile: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Go to login page
    public void goToLoginPage() {
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                finish();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                CustomIntent.customType(MainActivity.this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
            }
        }, Constant.PROGRESS_DELAY);
    }

    // logout from current user
    private void doLogout() {
        viewModel.logout();
        goToLoginPage();
    }

    // synchronize product data
    private void syncProductData() {
        progressBar.setVisibility(View.VISIBLE);
        productSyncTask = viewModel.syncProductData()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.INVISIBLE);
                })
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Toasty.success(this, getString(R.string.product_sync_success), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // return true if product sync in progress
    private boolean isProductSyncInProgress() {
        return productSyncTask != null && !productSyncTask.isComplete();
    }
}
