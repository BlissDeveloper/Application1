package com.example.application1;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask.TaskSnapshot;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ImagePreviewActivity extends AppCompatActivity {
    private final int IMAGE_CAPTURE_REQUEST = 1;
    /* access modifiers changed from: private */
    public String currentUserID;
    private Bitmap image;
    private ImageView imageViewPreview;
    /* access modifiers changed from: private */
    public CollectionReference imagesDataRef;
    private StorageReference imagesRef;
    private FirebaseAuth mAuth;
    /* access modifiers changed from: private */
    public ProgressDialog progressDialog;
    private Toolbar toolbarImagePreview;
    private CollectionReference usersRef;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_image_preview);
        this.imagesRef = FirebaseStorage.getInstance().getReference().child("Images");
        this.imagesDataRef = FirebaseFirestore.getInstance().collection("Uploaded_Images");
        this.usersRef = FirebaseFirestore.getInstance().collection("Users");
        this.mAuth = FirebaseAuth.getInstance();
        this.currentUserID = this.mAuth.getCurrentUser().getUid();
        this.toolbarImagePreview = (Toolbar) findViewById(R.id.toolbarImagePreview);
        this.imageViewPreview = (ImageView) findViewById(R.id.imageViewImagePreview);
        this.progressDialog = new ProgressDialog(this);
        setSupportActionBar(this.toolbarImagePreview);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle((CharSequence) null);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        if (getIntent().hasExtra("image")) {
            this.image = (Bitmap) getIntent().getExtras().get("image");
            loadImage();
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == -1 && data != null) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            Intent intent = new Intent(this, ImagePreviewActivity.class);
            intent.putExtra("image", imageBitmap);
            startActivity(intent);
        }
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId != 16908332) {
            switch (itemId) {
                case R.id.menu_discard /*2131230847*/:
                    showDia(0);
                    return true;
                case R.id.menu_save /*2131230848*/:
                    showDia(1);
                    return true;
                default:
                    return false;
            }
        } else {
            onBackPressed();
            return true;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_preview_menu, menu);
        return true;
    }

    public void saveImage() {
        UUID uuid = UUID.randomUUID();
        this.progressDialog.setTitle("Saving image...");
        this.progressDialog.show();
        StorageReference storageReference = this.imagesRef;
        StringBuilder sb = new StringBuilder();
        sb.append(uuid);
        sb.append(".jpg");
        final StorageReference myRef = storageReference.child(sb.toString());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.image.compress(CompressFormat.JPEG, 75, baos);
        myRef.putBytes(baos.toByteArray()).addOnCompleteListener((OnCompleteListener) new OnCompleteListener<TaskSnapshot>() {
            public void onComplete(@NonNull Task<TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    myRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        public void onSuccess(Uri uri) {
                            ImagePreviewActivity.this.uploadImageDataToFirestore(uri.toString());
                        }
                    });
                    return;
                }
                Log.e("Avery", task.getException().getMessage());
                ImagePreviewActivity.this.progressDialog.dismiss();
            }
        });
    }

    public void uploadImageDataToFirestore(final String uri) {
        this.usersRef.document(this.currentUserID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> imageMap = new HashMap<>();
                    String firstName = documentSnapshot.get("first_name").toString();
                    String lastName = documentSnapshot.get("last_name").toString();
                    StringBuilder sb = new StringBuilder();
                    sb.append(firstName);
                    sb.append(" ");
                    sb.append(lastName);
                    String fullName = sb.toString();
                    String timeStamp = ImagePreviewActivity.this.getCurrentTimestamp();
                    imageMap.put("user_id", ImagePreviewActivity.this.currentUserID);
                    imageMap.put("image_url", uri);
                    imageMap.put("timestamp", timeStamp);
                    imageMap.put("full_name", fullName);
                    ImagePreviewActivity.this.imagesDataRef.add(imageMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                new Builder(ImagePreviewActivity.this).setTitle((CharSequence) "Image saved").setNeutralButton((CharSequence) "OK", (OnClickListener) new OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        ImagePreviewActivity.this.openCamera();
                                    }
                                }).show();
                            } else {
                                Log.d("Avery", task.getException().getMessage());
                            }
                            ImagePreviewActivity.this.progressDialog.dismiss();
                        }
                    });
                    return;
                }
                Log.e("Avery", "No document snapshot exists");
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    public void showDia(int flag) {
        Builder builder = new Builder(this);
        switch (flag) {
            case 0:
                OnClickListener clickListener = new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case -2:
                                ImagePreviewActivity.this.openCamera();
                                return;
                            case -1:
                                dialogInterface.dismiss();
                                return;
                            default:
                                return;
                        }
                    }
                };
                builder.setTitle((CharSequence) "Discard Image?").setPositiveButton((CharSequence) "Cancel", clickListener).setNegativeButton((CharSequence) "Discard", clickListener).show();
                return;
            case 1:
                OnClickListener clickListener1 = new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == -1) {
                            ImagePreviewActivity.this.saveImage();
                        }
                    }
                };
                builder.setTitle((CharSequence) "Save Image?").setPositiveButton((CharSequence) "Save", clickListener1).setNegativeButton((CharSequence) "Cancel", clickListener1).show();
                return;
            default:
                return;
        }
    }

    public String getCurrentTimestamp() {
        return Long.valueOf(System.currentTimeMillis() / 1000).toString();
    }

    public void loadImage() {
        this.imageViewPreview.setImageBitmap(this.image);
    }

    public void openCamera() {
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivityForResult(takePictureIntent, 1);
            finish();
        }
    }
}