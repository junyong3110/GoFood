package com.example.food.Restaurant;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.ItemClickListener;
import com.example.food.MainActivity;
import com.example.food.R;
import com.example.food.Restaurant.Models.CategoryRes;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class MainAdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView txtFullNameAdmin;
    //Firebase
    FirebaseDatabase database;
    DatabaseReference categories;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseRecyclerAdapter<CategoryRes, CategoryViewHolderRes> adapter;
    RecyclerView recycler_menu_admin;
    RecyclerView.LayoutManager layoutManager;
    TextInputEditText edtName;
    MaterialButton btnUpload, btnSelect;
    CategoryRes newCategory;
    Uri saveUri;
    DrawerLayout drawer_admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        Toolbar toolbar = findViewById(R.id.toolbar_admin);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle("Category Management");
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance();
        categories = database.getReference("Restaurants").child(ConstantRes.currentUser.getRestaurantId()).child("detail").child("Category");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        FloatingActionButton fab = findViewById(R.id.fab_admin);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        drawer_admin =  findViewById(R.id.drawer_layout_admin);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer_admin, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_admin.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view_admin);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        txtFullNameAdmin =  headerView.findViewById(R.id.text_full_name);
        txtFullNameAdmin.setText(ConstantRes.currentUser.getName());

        recycler_menu_admin =  findViewById(R.id.recycler_menu_admin);
        recycler_menu_admin.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu_admin.setLayoutManager(layoutManager);

        loadMenu();
    }

    private void showDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainAdminActivity.this);
        alertDialog.setTitle("Add New Category");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_category, null);

        edtName = add_menu_layout.findViewById(R.id.et_name_category);
        btnSelect = add_menu_layout.findViewById(R.id.btn_category_select);
        btnUpload = add_menu_layout.findViewById(R.id.btn_category_upload);

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

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);

        //set Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                //create new category
                if (newCategory != null) {
                    categories.push().setValue(newCategory);
                    Snackbar.make(drawer_admin, " New Category " + newCategory.getName() + " was added ",
                            Snackbar.LENGTH_SHORT).show();
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstantRes.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null
                && data.getData() != null) {

            saveUri = data.getData();
            btnSelect.setText("Image Selected!");
        }
    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), ConstantRes.PICK_IMAGE_REQUEST);

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
                            Toast.makeText(MainAdminActivity.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    //set value for newCategory if image upload and we can get download link
                                    newCategory = new CategoryRes(edtName.getText().toString(), uri.toString());
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            mDialog.dismiss();
                            Toast.makeText(MainAdminActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NotNull UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Uploading " + progress + " % ");

                        }


                    });
        }
    }

    private void loadMenu() {

        FirebaseRecyclerOptions<CategoryRes> options = new FirebaseRecyclerOptions.Builder<CategoryRes>()
                .setQuery(categories, CategoryRes.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<CategoryRes, CategoryViewHolderRes>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolderRes viewHolder, int position, @NonNull CategoryRes model) {

                viewHolder.txtMenuName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(viewHolder.imageView);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent foodList = new Intent(MainAdminActivity.this, FoodListActivityRes.class);
                        foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(foodList);

                    }
                });
            }

            @NonNull
            @Override
            public CategoryViewHolderRes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);
                return new CategoryViewHolderRes(itemView);
            }
        };

        //refresh data if have data changed
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recycler_menu_admin.setAdapter(adapter);

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout_admin);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_orders) {
            //Intent orders = new Intent(MainAdminActivity.this, OrderStatusActivityRes.class);
            //startActivity(orders);
        }  else if (id == R.id.nav_sign_out_admin) {
            ConfirmSignOutDialog();
        }else if (id == R.id.nav_view_comment) {
            //Intent comment = new Intent(MainAdminActivity.this, ViewCommentActivityRes.class);
            //startActivity(comment);
        } else if (id == R.id.nav_shipper) {
            //Intent shippers = new Intent(MainAdminActivity.this, RiderManagementActivityRes.class);
            //startActivity(shippers);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_admin);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void ConfirmSignOutDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainAdminActivity.this);
        alertDialog.setTitle("Confirm Sign Out?");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_signout = inflater.inflate(R.layout.confirm_signout_layout, null);
        alertDialog.setView(layout_signout);
        alertDialog.setIcon(R.drawable.ic_exit_to_app_black_24dp);

        alertDialog.setPositiveButton("SIGN OUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent signout = new Intent(MainAdminActivity.this, MainActivity.class);
                startActivity(signout);
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