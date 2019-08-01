package com.example.application1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.Fragment;

import com.example.application1.Fragment.FragmentLogin;
import com.example.application1.Fragment.FragmentLogin.FragmentLoginListener;
import com.example.application1.Fragment.FragmentRegister.FragmentRegisterListener;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements FragmentLoginListener, FragmentRegisterListener {
    private String currentUserID;
    private FragmentLogin fragmentLogin;
    private FrameLayout frameLayoutMain;
    private FirebaseAuth mAuth;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main);
        this.mAuth = FirebaseAuth.getInstance();
        if (this.mAuth.getCurrentUser() != null) {
            this.currentUserID = this.mAuth.getCurrentUser().getUid();
        }
        this.frameLayoutMain = (FrameLayout) findViewById(R.id.frameLayoutMain);
        this.fragmentLogin = new FragmentLogin();
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutMain, this.fragmentLogin).commit();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        if (this.mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    public void changeView(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutMain, fragment).commit();
    }

    public void changeV(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutMain, fragment).commit();
    }
}