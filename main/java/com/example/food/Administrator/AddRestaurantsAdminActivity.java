package com.example.food.Administrator;

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
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddRestaurantsAdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    FirebaseDatabase database;
    DatabaseReference adminsRestaurants;
    FirebaseRecyclerAdapter<AddRestaurantsAdmin, AddRestaurantsAdminViewHolder> adapter;

    RecyclerView recyclerViewAdmin;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton fabAddRestaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurants_admin);

        database = FirebaseDatabase.getInstance();
        adminsRestaurants = database.getReference("UserRestaurantAdmin");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Restaurants Admin");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fabAddRestaurants = findViewById(R.id.fab_add_restaurants);
        fabAddRestaurants.setOnClickListener(v -> showCreateAdminLayout());

        recyclerViewAdmin = findViewById(R.id.recycler_restaurants_admins);
        recyclerViewAdmin.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewAdmin.setLayoutManager(layoutManager);

        loadAllAdmins();
    }

    private void loadAllAdmins() {
        FirebaseRecyclerOptions<AddRestaurantsAdmin> allAdmins = new FirebaseRecyclerOptions.Builder<AddRestaurantsAdmin>()
                .setQuery(adminsRestaurants, AddRestaurantsAdmin.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<AddRestaurantsAdmin, AddRestaurantsAdminViewHolder>(allAdmins) {
            @Override
            protected void onBindViewHolder(@NonNull AddRestaurantsAdminViewHolder holder, final int position, @NonNull final AddRestaurantsAdmin model) {
                holder.admin_phone.setText(model.getPhone());
                holder.admin_name.setText(model.getName());
                holder.admin_id.setText(model.getRestaurantId());
                holder.admin_password.setText(model.getPassword());

                holder.btn_edit.setOnClickListener(v -> showEditDialog(adapter.getRef(position).getKey(), model));

                holder.btn_remove.setOnClickListener(v -> showDeleteAccountDialog(adapter.getRef(position).getKey()));
            }

            @NonNull
            @Override
            public AddRestaurantsAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_layout, parent, false);
                return new AddRestaurantsAdminViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerViewAdmin.setAdapter(adapter);
    }

    private void showCreateAdminLayout() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddRestaurantsAdminActivity.this);
        alertDialog.setTitle("Create Admin Account");
        alertDialog.setMessage("Please fill in all information");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_admin = inflater.inflate(R.layout.create_admin_layout, null);

        final TextInputEditText admin_phone = layout_admin.findViewById(R.id.et_create_admin_phone);
        final TextInputEditText admin_name = layout_admin.findViewById(R.id.et_create_admin_name);
        final TextInputEditText admin_id = layout_admin.findViewById(R.id.et_create_admin_id);
        final TextInputEditText admin_password = layout_admin.findViewById(R.id.et_create_admin_password);
        admin_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        admin_password.setTransformationMethod(new PasswordTransformationMethod());

        alertDialog.setView(layout_admin);
        alertDialog.setIcon(R.drawable.ic_create_black_24dp);


        alertDialog.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                //create account

                if (TextUtils.isEmpty(admin_phone.getText())) {
                    Toast.makeText(AddRestaurantsAdminActivity.this, "Phone Number is Empty!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(admin_name.getText())) {
                    Toast.makeText(AddRestaurantsAdminActivity.this, "Username is Empty!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(admin_id.getText())) {
                    Toast.makeText(AddRestaurantsAdminActivity.this, "Restaurant Id is Empty!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(admin_password.getText())) {
                    Toast.makeText(AddRestaurantsAdminActivity.this, "Password is Empty!", Toast.LENGTH_SHORT).show();
                } else if (admin_phone.getText().length() < 9) {
                    Toast.makeText(AddRestaurantsAdminActivity.this, "Phone Number cannot less than 10 digits!", Toast.LENGTH_SHORT).show();
                } else if (admin_phone.getText().length() > 13) {
                    Toast.makeText(AddRestaurantsAdminActivity.this, "Phone Number cannot exceed 13 digits!", Toast.LENGTH_SHORT).show();
                } else {
                    AddRestaurantsAdmin admin = new AddRestaurantsAdmin();
                    admin.setName(admin_name.getText().toString());
                    admin.setPassword(admin_password.getText().toString());
                    admin.setPhone(admin_phone.getText().toString());
                    admin.setRestaurantId(admin_id.getText().toString());
                    admin.setIsadmin("true");
                    admin.setIsstaff("false");

                    adminsRestaurants.child(admin_phone.getText().toString())
                            .setValue(admin)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AddRestaurantsAdminActivity.this, "Restaurant Admin Created Successfully!", Toast.LENGTH_SHORT).show();
                                }
                            })

                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddRestaurantsAdminActivity.this, "Failed to Create Account!", Toast.LENGTH_SHORT).show();
                                }
                            });

                }

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

    private void showDeleteAccountDialog(final String key) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddRestaurantsAdminActivity.this);
        alertDialog.setTitle("Confirm Delete?");

        LayoutInflater inflater = this.getLayoutInflater();
        View confirm_delete_layout = inflater.inflate(R.layout.confirm_delete_layout, null);
        alertDialog.setView(confirm_delete_layout);
        alertDialog.setIcon(R.drawable.ic_delete);

        alertDialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                adminsRestaurants.child(key).removeValue();
                Toast.makeText(AddRestaurantsAdminActivity.this, "Account Delete Successfully!", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
        adapter.notifyDataSetChanged();
    }

    private void showEditDialog(final String key, AddRestaurantsAdmin model) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddRestaurantsAdminActivity.this);
        alertDialog.setTitle("Update Admin Account");
        alertDialog.setMessage("Please fill in all information");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_admin = inflater.inflate(R.layout.create_admin_layout, null);

        final TextInputEditText admin_phone = layout_admin.findViewById(R.id.et_create_admin_phone);
        final TextInputEditText admin_name = layout_admin.findViewById(R.id.et_create_admin_name);
        final TextInputEditText admin_id = layout_admin.findViewById(R.id.et_create_admin_id);
        final TextInputEditText admin_password = layout_admin.findViewById(R.id.et_create_admin_password);
        admin_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        admin_password.setTransformationMethod(new PasswordTransformationMethod());

        //set data
        admin_name.setText(model.getName());
        admin_phone.setText(model.getPhone());
        admin_phone.setEnabled(false);
        admin_id.setText(model.getRestaurantId());
        admin_id.setEnabled(false);
        admin_password.setText(model.getPassword());

        alertDialog.setView(layout_admin);
        alertDialog.setIcon(R.drawable.ic_create_black_24dp);


        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                //create account

                if (TextUtils.isEmpty(admin_phone.getText())) {
                    Toast.makeText(AddRestaurantsAdminActivity.this, "Phone Number is Empty!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(admin_name.getText())) {
                    Toast.makeText(AddRestaurantsAdminActivity.this, "Username is Empty!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(admin_id.getText())) {
                    Toast.makeText(AddRestaurantsAdminActivity.this, "Restaurant Id is Empty!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(admin_password.getText())) {
                    Toast.makeText(AddRestaurantsAdminActivity.this, "Password is Empty!", Toast.LENGTH_SHORT).show();
                } else if (admin_phone.getText().length() < 9) {
                    Toast.makeText(AddRestaurantsAdminActivity.this, "Phone Number cannot less than 10 digits!", Toast.LENGTH_SHORT).show();
                } else if (admin_phone.getText().length() > 13) {
                    Toast.makeText(AddRestaurantsAdminActivity.this, "Phone Number cannot exceed 13 digits!", Toast.LENGTH_SHORT).show();
                } else {

                    Map<String, Object> update = new HashMap<>();
                    update.put("name", admin_name.getText().toString());
                    update.put("phone", admin_phone.getText().toString());
                    update.put("restaurantId", admin_id.getText().toString());
                    update.put("password", admin_password.getText().toString());


                    adminsRestaurants.child(key).updateChildren(update)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AddRestaurantsAdminActivity.this, "Admin Updated Successfully!", Toast.LENGTH_SHORT).show();
                                }
                            })

                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddRestaurantsAdminActivity.this, "Failed to Update Account!", Toast.LENGTH_SHORT).show();
                                }
                            });

                }

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_restaurants) {
            Intent cartIntent = new Intent(AddRestaurantsAdminActivity.this, AddRestaurantsActivity.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_add_admins) {

        } else if (id == R.id.nav_sign_out) {
            ConfirmSignOutDialog();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void ConfirmSignOutDialog() {

        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(AddRestaurantsAdminActivity.this);
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
                Intent logout = new Intent(AddRestaurantsAdminActivity.this, SignInAdministratorActivity.class);
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