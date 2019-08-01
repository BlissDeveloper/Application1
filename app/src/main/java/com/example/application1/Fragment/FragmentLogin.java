package com.example.application1.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.Fragment;

import com.example.application1.R;
import com.example.application1.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentLogin extends Fragment {
    private Button buttonLogin;
    private Button buttonRegister;
    /* access modifiers changed from: private */
    public TextInputEditText editTextEmail;
    /* access modifiers changed from: private */
    public TextInputEditText editTextPassword;
    /* access modifiers changed from: private */
    public String email;
    /* access modifiers changed from: private */
    public FragmentLoginListener fragmentLoginListener;
    FragmentRegister fragmentRegister;
    /* access modifiers changed from: private */
    public FirebaseAuth mAuth;
    private View mView;
    /* access modifiers changed from: private */
    public String password;
    /* access modifiers changed from: private */
    public ProgressBar progressbarLogin;

    public interface FragmentLoginListener {
        void changeView(Fragment fragment);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_fragment_login2, container, false);
        this.mAuth = FirebaseAuth.getInstance();
        this.editTextEmail = (TextInputEditText) this.mView.findViewById(R.id.editTextLoginEmail);
        this.editTextPassword = (TextInputEditText) this.mView.findViewById(R.id.editTextLoginPassword);
        this.buttonLogin = (Button) this.mView.findViewById(R.id.buttonLogin);
        this.buttonRegister = (Button) this.mView.findViewById(R.id.buttonCreateAccount);
        this.progressbarLogin = (ProgressBar) this.mView.findViewById(R.id.progressbarLogin);
        this.fragmentRegister = new FragmentRegister();
        this.buttonLogin.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                FragmentLogin.this.progressbarLogin.setVisibility(View.VISIBLE);
                if (FragmentLogin.this.isValidAreValidInputs()) {
                    FragmentLogin fragmentLogin = FragmentLogin.this;
                    fragmentLogin.email = fragmentLogin.editTextEmail.getText().toString();
                    FragmentLogin fragmentLogin2 = FragmentLogin.this;
                    fragmentLogin2.password = fragmentLogin2.editTextPassword.getText().toString();
                    FragmentLogin.this.mAuth.signInWithEmailAndPassword(FragmentLogin.this.email, FragmentLogin.this.password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                goToHome();
                            }
                            else {
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            progressbarLogin.setVisibility(View.GONE);

                        }
                    });
                    return;
                }
                FragmentLogin.this.progressbarLogin.setVisibility(View.GONE);
            }
        });
        this.buttonRegister.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                FragmentLogin.this.fragmentLoginListener.changeView(FragmentLogin.this.fragmentRegister);
            }
        });
        return this.mView;
    }

    public void onStart() {
        super.onStart();
        this.progressbarLogin.setVisibility(View.GONE);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentLoginListener) {
            this.fragmentLoginListener = (FragmentLoginListener) context;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(context.toString());
        sb.append(" must implement fragment listener");
        throw new RuntimeException(sb.toString());
    }

    public void onDetach() {
        super.onDetach();
        this.fragmentLoginListener = null;
    }

    public void goToHome() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.progressbarLogin.setVisibility(View.GONE);
    }

    public boolean isValidAreValidInputs() {
        String email2 = this.editTextEmail.getText().toString();
        this.password = this.editTextPassword.getText().toString();
        if (TextUtils.isEmpty(email2) || TextUtils.isEmpty(this.password)) {
            if (TextUtils.isEmpty(email2)) {
                this.editTextEmail.setError("Email cannot be empty");
                this.editTextEmail.requestFocus();
            } else if (TextUtils.isEmpty(this.password)) {
                this.editTextPassword.setError("Password cannot be empty");
                this.editTextPassword.requestFocus();
            }
            return false;
        } else if (Patterns.EMAIL_ADDRESS.matcher(email2).matches()) {
            return true;
        } else {
            this.editTextEmail.setError("Invalid email format");
            this.editTextEmail.requestFocus();
            return false;
        }
    }
}