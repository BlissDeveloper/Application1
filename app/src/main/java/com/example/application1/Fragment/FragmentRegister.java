package com.example.application1.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.application1.MainActivity;
import com.example.application1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FragmentRegister extends Fragment {
    private Button buttonLogin;
    private Button buttonRegister;
    private String currentUserID;
    private TextInputEditText editTextConfirmPassword;
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextFirstName;
    private TextInputEditText editTextLastName;
    private TextInputEditText editTextPassword;
    /* access modifiers changed from: private */
    public String email;
    /* access modifiers changed from: private */
    public FragmentLogin fragmentLogin;
    /* access modifiers changed from: private */
    public FragmentRegisterListener fragmentRegisterListener;
    /* access modifiers changed from: private */
    public FirebaseAuth mAuth;
    private View mView;
    /* access modifiers changed from: private */
    public String password;
    private String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{8,}";
    /* access modifiers changed from: private */
    public ProgressBar progressBarRegister;
    private CollectionReference userCollection;

    public interface FragmentRegisterListener {
        void changeV(Fragment fragment);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_fragment_register, container, false);
        this.mAuth = FirebaseAuth.getInstance();
        if (this.mAuth.getCurrentUser() != null) {
            this.currentUserID = this.mAuth.getCurrentUser().getUid();
        }
        this.userCollection = FirebaseFirestore.getInstance().collection("Users");
        this.fragmentLogin = new FragmentLogin();
        editTextEmail = mView.findViewById(R.id.editTextEmail);
        this.editTextPassword = (TextInputEditText) this.mView.findViewById(R.id.editTextPassword);
        this.editTextFirstName = (TextInputEditText) this.mView.findViewById(R.id.editTextFirstName);
        this.editTextLastName = (TextInputEditText) this.mView.findViewById(R.id.editTextLastName);
        this.editTextConfirmPassword = (TextInputEditText) this.mView.findViewById(R.id.editTextConfirmPassword);
        this.progressBarRegister = (ProgressBar) this.mView.findViewById(R.id.progressBarRegister);
        this.buttonLogin = (Button) this.mView.findViewById(R.id.buttonHaveAccount);
        this.buttonRegister = (Button) this.mView.findViewById(R.id.buttonRegister);
        this.buttonLogin.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                FragmentRegister.this.fragmentRegisterListener.changeV(FragmentRegister.this.fragmentLogin);
            }
        });
        this.buttonRegister.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (FragmentRegister.this.hasValidInputs()) {
                    Toast.makeText(FragmentRegister.this.getActivity(), "Inputs are valid!", Toast.LENGTH_LONG).show();
                    FragmentRegister.this.progressBarRegister.setVisibility(View.VISIBLE);
                    FragmentRegister.this.registerUser();
                }
            }
        });
        return this.mView;
    }

    public void onStart() {
        super.onStart();
        this.progressBarRegister.setVisibility(View.GONE);
    }

    public boolean hasValidInputs() {
        String fName = this.editTextFirstName.getText().toString();
        String lName = this.editTextLastName.getText().toString();
        String email2 = this.editTextEmail.getText().toString();
        String password2 = this.editTextPassword.getText().toString();
        String password_confirm = this.editTextConfirmPassword.getText().toString();
        if (TextUtils.isEmpty(fName) || TextUtils.isEmpty(lName) || TextUtils.isEmpty(email2) || TextUtils.isEmpty(password2) || TextUtils.isEmpty(password_confirm)) {
            if (TextUtils.isEmpty(fName)) {
                this.editTextFirstName.setError("First name is required.");
            }
            if (TextUtils.isEmpty(lName)) {
                this.editTextLastName.setError("Last name is required.");
            }
            if (TextUtils.isEmpty(email2)) {
                this.editTextEmail.setError("Email is required");
            }
            if (TextUtils.isEmpty(password2)) {
                this.editTextPassword.setError("Password must not be empty");
            }
            if (TextUtils.isEmpty(password_confirm)) {
                this.editTextPassword.setError("Password confirmation is required");
            }
            return false;
        }
        Matcher matcher = Pattern.compile(this.passwordRegex).matcher(password2);
        if (!Patterns.EMAIL_ADDRESS.matcher(email2).matches() || !matcher.matches()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email2).matches()) {
                this.editTextEmail.setError("Email format is invalid.");
                this.editTextEmail.requestFocus();
            }
            if (!matcher.matches()) {
                this.editTextPassword.setError("");
                Toast.makeText(getActivity(), "Password must have at least a number, a lowercase letter, 8 characters and have no spaces.", Toast.LENGTH_SHORT).show();
                this.editTextEmail.requestFocus();
            }
            return false;
        } else if (password2.equals(password_confirm)) {
            this.progressBarRegister.setVisibility(View.GONE);
            registerUser();
            return true;
        } else {
            Toast.makeText(getActivity(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void registerUser() {
        this.email = this.editTextEmail.getText().toString();
        this.password = this.editTextPassword.getText().toString();
        this.mAuth.createUserWithEmailAndPassword(this.email, this.password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FragmentRegister.this.mAuth.signInWithEmailAndPassword(FragmentRegister.this.email, FragmentRegister.this.password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FragmentRegister.this.insertUserDataIntoFirestore();
                                return;
                            }
                            Log.e("Avery", task.getException().getMessage());
                            FragmentRegister.this.progressBarRegister.setVisibility(View.GONE);
                        }
                    });
                    return;
                }
                Log.d("Avery", task.getException().getMessage());
                FragmentRegister.this.progressBarRegister.setVisibility(View.GONE);
            }
        });
    }

    public void insertUserDataIntoFirestore() {
        String firstName = this.editTextFirstName.getText().toString();
        String lastName = this.editTextLastName.getText().toString();
        this.currentUserID = this.mAuth.getCurrentUser().getUid();
        char firstNameInitial = firstName.charAt(0);
        char lastNameInitial = lastName.charAt(0);
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toString(firstNameInitial));
        sb.append(Character.toString(lastNameInitial));
        String combinedInitials = sb.toString().toUpperCase();
        Map<String, Object> map = new HashMap<>();
        map.put("first_name", firstName);
        map.put("last_name", lastName);
        map.put("user_id", this.currentUserID);
        map.put("name_initials", combinedInitials);
        this.userCollection.document(this.currentUserID).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(FragmentRegister.this.getActivity(), "Account created successfully!", Toast.LENGTH_SHORT).show();
                    FragmentRegister.this.clearAll();
                    return;
                }
                Log.e("Avery", task.getException().getMessage());
                FragmentRegister.this.progressBarRegister.setVisibility(View.GONE);
            }
        });
    }

    public void clearAll() {
        this.editTextFirstName.setText(null);
        this.editTextLastName.setText(null);
        this.editTextEmail.setText(null);
        this.editTextPassword.setText(null);
        this.editTextConfirmPassword.setText(null);
        this.progressBarRegister.setVisibility(View.GONE);
        goToHome();
    }

    public void goToHome() {
        startActivity(new Intent(getActivity(), MainActivity.class));
    }
}