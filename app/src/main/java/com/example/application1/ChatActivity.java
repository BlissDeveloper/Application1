package com.example.application1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.application1.Fragment.ChatFragment;

public class ChatActivity extends AppCompatActivity {
    private Toolbar toolbarChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbarChat = findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbarChat);
        getSupportActionBar().setTitle("Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadFragment();
    }

    public void loadFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutChat, new ChatFragment());
        fragmentTransaction.commit();
    }
}
