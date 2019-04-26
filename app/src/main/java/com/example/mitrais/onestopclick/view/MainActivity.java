package com.example.mitrais.onestopclick.view;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mitrais.onestopclick.Constant;
import com.example.mitrais.onestopclick.R;
import com.example.mitrais.onestopclick.dagger.component.DaggerMainActivityComponent;
import com.example.mitrais.onestopclick.dagger.component.MainActivityComponent;
import com.example.mitrais.onestopclick.viewmodel.MainViewModel;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

/**
 * MainActivity handle main page logic
 */
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
        initDagger();
        initDrawer();
        if (savedInstanceState == null)
            initFragment();
        observeProfile();
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
                logout();
                return true;
            case R.id.sync:
                if (isProductSyncInProgress())
                    Toasty.info(this, getString(R.string.product_sync_in_progress), Toast.LENGTH_SHORT).show();
                else
                    syncUserData();
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
            case R.id.all: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ProductListFragment.newInstance(Constant.PRODUCT_TYPE_ALL)).commit();
                break;
            }
            case R.id.movie: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ProductListFragment.newInstance(Constant.PRODUCT_TYPE_MOVIE)).commit();
                break;
            }
            case R.id.music: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ProductListFragment.newInstance(Constant.PRODUCT_TYPE_MUSIC)).commit();
                break;
            }
            case R.id.book: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ProductListFragment.newInstance(Constant.PRODUCT_TYPE_BOOK)).commit();
                break;
            }
            case R.id.profile: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ProfileFragment.newInstance()).commit();
                break;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // region private methods

    /**
     * initialize dagger injection
     */
    private void initDagger() {
        MainActivityComponent component = DaggerMainActivityComponent.builder()
                .mainActivity(this)
                .build();
        component.inject(this);
    }

    /**
     * initialize drawer menu
     */
    private void initDrawer() {
        View navHeaderView = navView.getHeaderView(0);
        imgProfile = navHeaderView.findViewById(R.id.img_profile);
        txtEmail = navHeaderView.findViewById(R.id.txt_email);

        setSupportActionBar(toolbar);
        setTitle(getString(R.string.main_menu));


        /* initialize drawer toggle */
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_bar_open, R.string.navigation_bar_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideSoftKeyboard();
            }
        };

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);
    }

    /**
     * initialize first fragment
     */
    private void initFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ProductListFragment.newInstance(Constant.PRODUCT_TYPE_ALL)).commit();
        navView.setCheckedItem(R.id.all);
    }

    /**
     * observe user profile
     */
    private void observeProfile() {
        viewModel.getProfileByEmail(viewModel.getCurrentUser().getEmail()).observe(this, profile -> {
            if (profile != null) {
                txtEmail.setText(profile.getEmail());
                if (profile.getImageUri() != null && !profile.getImageUri().isEmpty())
                    Picasso.get().load(profile.getImageUri()).placeholder(R.drawable.skeleton).into(imgProfile);
            }
        });
    }

    /**
     * start LoginActivity
     */
    public void goToLoginPage() {
        showProgressBar();
        new Handler().postDelayed(() -> {
            hideProgressBar();

            finish();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            CustomIntent.customType(MainActivity.this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
        }, Constant.PROGRESS_DELAY);
    }

    /**
     * log user out
     */
    private void logout() {
        viewModel.logout();
        goToLoginPage();
    }

    /**
     * synchronized user data
     */
    private void syncUserData() {
        showProgressBar();
        productSyncTask = viewModel.syncUserData()
                .addOnCompleteListener(task -> hideProgressBar())
                .addOnSuccessListener(queryDocumentSnapshots -> Toasty.success(this, getString(R.string.product_sync_success), Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toasty.error(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    /**
     * @return true if sync in progress
     */
    private boolean isProductSyncInProgress() {
        return productSyncTask != null && !productSyncTask.isComplete();
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(toolbar.getWindowToken(), 0);
    }
    // endregion
}
