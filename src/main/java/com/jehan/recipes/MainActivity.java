package com.jehan.recipes;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private DatabaseReference db;
    private RecyclerView recyclerView, fav_list;
    private ArrayList<Recipe> recipes;
    private MainAdapter mainAdapter;
    private FavAdapter favAdapter;
    private Button  logout_btn;
    private FloatingActionButton add_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        add_btn = (FloatingActionButton) findViewById(R.id.add_btn);
        System.out.println("___________----"+auth.getCurrentUser());
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, loginActivity.class));
            finish();
            return;
        }

        recyclerView = (RecyclerView) findViewById(R.id.main_list);
        fav_list = (RecyclerView) findViewById(R.id.fav_view);
        db = FirebaseDatabase.getInstance().getReference("Recipes");
        recipes = new ArrayList<Recipe>();
        mainAdapter = new MainAdapter(Controller.recipes);
        favAdapter = new FavAdapter(Controller.fav_recipes);


        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Add_Recipe.class));
            }
        });


        Controller.recipes.clear();
        Controller.fav_recipes.clear();
        load_records();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mainAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), 1));

        fav_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        fav_list.setAdapter(favAdapter);
        fav_list.addItemDecoration(new DividerItemDecoration(fav_list.getContext(), 1));
    }

    void load_records() {
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Recipe r = dataSnapshot.getValue(Recipe.class);
                Controller.recipes.add(r);
                if(r.cat.equals(Controller.user.fav)) Controller.fav_recipes.add(r);
                mainAdapter.notifyDataSetChanged();
                favAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh_btn:
                Controller.recipes.clear();
                Controller.fav_recipes.clear();;
                load_records();
                break;
            case R.id.menu_logout_btn:
                auth.signOut();
                startActivity(new Intent(MainActivity.this, SightUpActivity.class));
                finish();
                break;
            default:
                return true;


        }
        return true;

    }
}
