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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
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

    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initDagger();
        initDrawer();
        initFragment();
        observeProfile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        initSearchView(menu);
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
            case R.id.home: {
                setTitle(getString(R.string.home));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ProductListParentFragment.newInstance()).commit();
                break;
            }
            case R.id.profile: {
                setTitle(getString(R.string.profile));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ProfileFragment.newInstance()).commit();
                break;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // region private methods

    private void initDagger() {
        MainActivityComponent component = DaggerMainActivityComponent.builder()
                .mainActivity(this)
                .build();
        component.inject(this);
    }

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

    private void initFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ProductListParentFragment.newInstance()).commit();
        navView.setCheckedItem(R.id.home);
    }

    private void initSearchView(Menu menu) {
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                searchView.clearFocus();
                goToSearchProductPage(search);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    private void observeProfile() {
        viewModel.getProfile(viewModel.getUser().getEmail()).observe(this, profile -> {
            if (profile != null) {
                imgProfile.setVisibility(View.VISIBLE);
                txtEmail.setText(profile.getEmail());
                if (profile.getImageUri() != null && !profile.getImageUri().isEmpty())
                    Picasso.get().load(profile.getImageUri()).placeholder(R.drawable.skeleton).into(imgProfile);
            }
        });
    }

    private void goToLoginScreen() {
        showProgressBar();
        new Handler().postDelayed(() -> {
            hideProgressBar();

            finish();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            CustomIntent.customType(MainActivity.this, Constant.ANIMATION_FADEIN_TO_FADEOUT);
        }, Constant.PROGRESS_DELAY);
    }

    private void goToSearchProductPage(String search) {
        Intent intent = new Intent(this, SearchProductActivity.class);
        intent.putExtra(SearchProductActivity.EXTRA_SEARCH_QUERY, search);
        startActivity(intent);
    }

    private void logout() {
        viewModel.logout();
        viewModel.deleteUserData();
        goToLoginScreen();
    }

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

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(toolbar.getWindowToken(), 0);
    }
    // endregion
}
