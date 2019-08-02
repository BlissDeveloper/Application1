package com.example.application1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.application1.Fragment.FragmentGallery;

public class GalleryActivity extends AppCompatActivity {
    private Toolbar toolbarGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().
                replace(R.id.frameLayoutGallery, new FragmentGallery());
        fragmentTransaction.commit();

        toolbarGallery = findViewById(R.id.toolbarGallery);
        setSupportActionBar(toolbarGallery);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Gallery");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return false;
        }
    }
}
