package com.example.food.Administrator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.food.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class AddRestaurantsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    FirebaseDatabase database;
    DatabaseReference restaurants;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseRecyclerAdapter<AddRestaurants, AddRestaurantsViewHolder> adapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton add_restaurants;
    TextInputEditText edtName, edtId, edtLocation, latitude, longitude;
    MaterialButton btnUpload, btnSelect;
    AddRestaurants newRestaurants;
    Uri saveUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurants);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Restaurants");
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance();
        restaurants = database.getReference("Restaurants");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        add_restaurants = findViewById(R.id.fab_add_restaurants);
        add_restaurants.setOnClickListener(v -> addRestaurants());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = findViewById(R.id.recycler_restaurants);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadRestaurants();
    }

    private void loadRestaurants() {
        FirebaseRecyclerOptions<AddRestaurants> options = new FirebaseRecyclerOptions.Builder<AddRestaurants>()
                .setQuery(restaurants, AddRestaurants.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<AddRestaurants, AddRestaurantsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AddRestaurantsViewHolder holder, final int position, @NonNull final AddRestaurants model) {
                holder.txtMenuName.setText(model.getName());
                holder.txtLocation.setText(model.getLocation());
                Picasso.get().load(model.getImage()).into(holder.imageView);
            }

            @NonNull
            @Override
            public AddRestaurantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.restaurant_item, parent, false);
                return new AddRestaurantsViewHolder(itemView);
            }
        };

        //refresh data if have data changed
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    private void addRestaurants() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddRestaurantsActivity.this);
        alertDialog.setTitle("Add New Restaurant");
        alertDialog.setMessage("Please fill information");


        LayoutInflater inflater = LayoutInflater.from(this);
        View add_restaurant_layout = inflater.inflate(R.layout.add_new_restaurants, null);

        edtName = add_restaurant_layout.findViewById(R.id.et_name_restaurants);
        edtId = add_restaurant_layout.findViewById(R.id.et_name_restaurants_id);
        edtLocation = add_restaurant_layout.findViewById(R.id.et_name_restaurants_location);
        latitude = add_restaurant_layout.findViewById(R.id.et_name_restaurants_latitude);
        longitude = add_restaurant_layout.findViewById(R.id.et_name_restaurants_longitude);
        btnSelect = add_restaurant_layout.findViewById(R.id.btn_restaurant_select);
        btnUpload = add_restaurant_layout.findViewById(R.id.btn_restaurant_upload);

        //Event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //let users select image from gallery and save URL of this image
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //upload image
                uploadImage();
            }
        });

        alertDialog.setView(add_restaurant_layout);
        alertDialog.setIcon(R.drawable.ic_baseline_restaurant_24);

        //set Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                AddRestaurants restaurant = new AddRestaurants();
                restaurant.setName(edtName.getText().toString());
                restaurant.setRestaurantId(edtId.getText().toString());
                restaurant.setLocation(edtLocation.getText().toString());
                restaurant.setLatitude(latitude.getText().toString());
                restaurant.setLongitude(longitude.getText().toString());
                restaurant.setImage(saveUri.toString());

                restaurants.child(edtId.getText().toString())
                        .setValue(restaurant)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AddRestaurantsActivity.this, "Restaurant Created Successfully!", Toast.LENGTH_SHORT).show();
                            }
                        })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddRestaurantsActivity.this, "Failed to Create Restaurant!", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });


        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alertDialog.show();


    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), ConstantAdmin.PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstantAdmin.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null
                && data.getData() != null) {

            saveUri = data.getData();
            btnSelect.setText("Image Selected!");
        }
    }

    private void uploadImage() {

        if (saveUri != null) {

            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);

            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            mDialog.dismiss();
                            Toast.makeText(AddRestaurantsActivity.this, "Uploaded!!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    //set value for newRestaurants if image upload and we can get download link
                                    newRestaurants = new AddRestaurants();
                                    newRestaurants.setName(edtName.getText().toString());
                                    newRestaurants.setLocation(edtLocation.getText().toString());
                                    newRestaurants.setRestaurantId(edtId.getText().toString());
                                    newRestaurants.setLatitude(latitude.getText().toString());
                                    newRestaurants.setLongitude(longitude.getText().toString());
                                    newRestaurants.setImage(uri.toString());
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            mDialog.dismiss();
                            Toast.makeText(AddRestaurantsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploading" + progress + " % ");
                        }
                    });
        }
    }

    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(ConstantAdmin.UPDATE)) {

            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));

        } else if (item.getTitle().equals(ConstantAdmin.DELETE)) {

            ConfirmDeleteDialog(item);
        }

        return super.onContextItemSelected(item);
    }

    private void showUpdateDialog(final String key, final AddRestaurants item) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddRestaurantsActivity.this);
        alertDialog.setTitle("Update Restaurant");
        alertDialog.setMessage("Please fill information");

        LayoutInflater inflater = this.getLayoutInflater();
        View update_restaurant_layout = inflater.inflate(R.layout.add_new_restaurants, null);

        edtName = update_restaurant_layout.findViewById(R.id.et_name_restaurants);
        edtId = update_restaurant_layout.findViewById(R.id.et_name_restaurants_id);
        edtLocation = update_restaurant_layout.findViewById(R.id.et_name_restaurants_location);
        latitude = update_restaurant_layout.findViewById(R.id.et_name_restaurants_latitude);
        longitude = update_restaurant_layout.findViewById(R.id.et_name_restaurants_longitude);
        btnSelect = update_restaurant_layout.findViewById(R.id.btn_restaurant_select);
        btnUpload = update_restaurant_layout.findViewById(R.id.btn_restaurant_upload);

        //set default name
        edtName.setText(item.getName());
        edtId.setText(item.getRestaurantId());
        edtId.setEnabled(false);
        edtLocation.setText(item.getLocation());
        latitude.setText(item.getLatitude());
        longitude.setText(item.getLongitude());

        //Event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //let users select image from gallery and save URL of this image
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //upload image
                changeImage(item);
            }
        });

        alertDialog.setView(update_restaurant_layout);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);

        //set Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                //update information
                item.setName(edtName.getText().toString());
                item.setRestaurantId(edtId.getText().toString());
                item.setLocation(edtLocation.getText().toString());
                item.setLatitude(latitude.getText().toString());
                item.setLongitude(longitude.getText().toString());
                restaurants.child(key).setValue(item);
                Toast.makeText(AddRestaurantsActivity.this, "Restaurant information Updated Successfully!", Toast.LENGTH_SHORT).show();

            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alertDialog.show();

    }

    private void changeImage(final AddRestaurants item) {

        if (saveUri != null) {

            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            mDialog.dismiss();
                            Toast.makeText(AddRestaurantsActivity.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    //set value for newCategory if image upload and we can get download link
                                    item.setImage(uri.toString());
                                    Toast.makeText(AddRestaurantsActivity.this, "Image Changed Successfully!", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            mDialog.dismiss();
                            Toast.makeText(AddRestaurantsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploading " + progress + " % ");
                        }
                    });
        }
    }

    private void ConfirmDeleteDialog(final MenuItem item) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddRestaurantsActivity.this);
        alertDialog.setTitle("Confirm Delete?");

        LayoutInflater inflater = this.getLayoutInflater();
        View confirm_delete_layout = inflater.inflate(R.layout.confirm_delete_layout, null);
        alertDialog.setView(confirm_delete_layout);
        alertDialog.setIcon(R.drawable.ic_delete);

        alertDialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteCategory(adapter.getRef(item.getOrder()).getKey());
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

    private void deleteCategory(String key) {

        //get all food in category
        DatabaseReference foods = database.getReference("Restaurants");
        final Query foodInCategory = foods.orderByChild("menuId").equalTo(key);
        foodInCategory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    postSnapShot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        restaurants.child(key).removeValue();
        Toast.makeText(AddRestaurantsActivity.this, "Restaurant Deleted Successfully!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_restaurants) {

        } else if (id == R.id.nav_add_admins) {
            Intent cartIntent = new Intent(AddRestaurantsActivity.this, AddRestaurantsAdminActivity.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_sign_out) {
            ConfirmSignOutDialog();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void ConfirmSignOutDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddRestaurantsActivity.this);
        alertDialog.setTitle("Confirm Sign Out?");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_signout = inflater.inflate(R.layout.confirm_signout_layout, null);
        alertDialog.setView(layout_signout);
        alertDialog.setIcon(R.drawable.ic_exit_to_app_black_24dp);

        alertDialog.setPositiveButton("SIGN OUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //log out
                Intent logout = new Intent(AddRestaurantsActivity.this, SignInAdministratorActivity.class);
                logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logout);

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