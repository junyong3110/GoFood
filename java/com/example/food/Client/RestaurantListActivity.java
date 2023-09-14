package com.example.food.Client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.Client.Models.Restaurant;
import com.example.food.ItemClickListener;
import com.example.food.R;
import com.example.food.Restaurant.ConstantRes;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class RestaurantListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView txtFullNameClient;
    FirebaseDatabase database;
    DatabaseReference category;
    RecyclerView recyclerViewClient;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder> adapter;
    FirebaseRecyclerOptions<Restaurant> firebaseRecyclerOptions;
    DrawerLayout drawer_client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        Toolbar toolbar = findViewById(R.id.toolbar_client);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle("Restaurants");
        setSupportActionBar(toolbar);

        drawer_client =  findViewById(R.id.drawer_layout_client);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_client, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_client.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view_client);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        txtFullNameClient =  headerView.findViewById(R.id.text_full_name);
        txtFullNameClient.setText(Constant.currentUser.getName());

        database = FirebaseDatabase.getInstance();
        category = database.getReference("Restaurants");
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Restaurant>().setQuery(category, Restaurant.class).build();

        recyclerViewClient = findViewById(R.id.recycler_menu_client);
        recyclerViewClient.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerViewClient.setLayoutManager(layoutManager);

        loadRestaurant();

    }

    private void loadRestaurant() {

        adapter = new FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder>(firebaseRecyclerOptions) {
            @NonNull
            @Override
            public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_item, parent, false);
                return new RestaurantViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position, @NonNull final Restaurant model) {
                holder.restaurantName.setText(model.getName());
                holder.restaurantLocation.setText(model.getLocation());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.placeholder)
                        .into(holder.restaurantImage);

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodList = new Intent(RestaurantListActivity.this, CategoriesActivity.class);
                        Constant.restaurantSelected = adapter.getRef(position).getKey();
                        foodList.putExtra(Constant.RESTAURANT_ID, adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });

            }
        };
        adapter.notifyDataSetChanged();
        recyclerViewClient.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_client);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_action_bar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.refresh)
            loadRestaurant();
        /*if (item.getItemId() == R.id.cart)
            CartActivity();*/
        return super.onOptionsItemSelected(item);
    }

    /*private void CartActivity() {
        Intent cartIntent = new Intent(RestaurantListActivity.this, CartActivity.class);
        startActivity(cartIntent);
    }*/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent NearbyIntent = new Intent(RestaurantListActivity.this, RestaurantListActivity.class);
            startActivity(NearbyIntent);

        } /*else if (id == R.id.nav_menu) {
            Intent NearbyIntent = new Intent(RestaurantListActivity.this, NearbyRestaurantsActivity.class);
            startActivity(NearbyIntent);

        } else if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(RestaurantListActivity.this, CartActivity.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_orders) {
            Intent orderIntent = new Intent(RestaurantListActivity.this, OrderStatusActivity.class);
            startActivity(orderIntent);

        } */
        else if (id == R.id.nav_sign_out) {
            ConfirmSignOutDialog();

        } /*else if (id == R.id.nav_profile) {

            Intent profileIntent = new Intent(RestaurantListActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
        } else if (id == R.id.nav_favorites) {
            startActivity(new Intent(RestaurantListActivity.this, FavoritesActivity.class));
        }*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout_client);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void ConfirmSignOutDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RestaurantListActivity.this);
        alertDialog.setTitle("Confirm Sign Out?");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_signout = inflater.inflate(R.layout.confirm_signout_layout, null);
        alertDialog.setView(layout_signout);
        alertDialog.setIcon(R.drawable.ic_exit_to_app_black_24dp);

        alertDialog.setPositiveButton("SIGN OUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent signOut = new Intent(RestaurantListActivity.this, SignInClientActivity.class);
                startActivity(signOut);

            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

}