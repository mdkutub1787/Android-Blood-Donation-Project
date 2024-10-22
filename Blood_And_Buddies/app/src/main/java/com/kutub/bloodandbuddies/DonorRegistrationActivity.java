package com.kutub.bloodandbuddies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DonorRegistrationActivity extends AppCompatActivity {

    private TextView donorBackBtn;
    private CircleImageView profile_image;
    private TextInputEditText donorFullName, donorPhnNum, donorEmail, donorPassword;
    private Spinner bgSpinner;
    private Button donorRegBtn;

    private Uri resultUri;
    private ProgressDialog loader;
    private FirebaseAuth userAuth;
    private DatabaseReference userDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_registration);

        donorBackBtn = findViewById(R.id.donorBackBtn);
        profile_image = findViewById(R.id.profile_image);
        donorFullName = findViewById(R.id.donorFullName);
        donorPhnNum = findViewById(R.id.donorPhnNum);
        donorEmail = findViewById(R.id.donorEmail);
        bgSpinner = findViewById(R.id.bgSpinner);
        donorPassword = findViewById(R.id.donorPassword);
        donorRegBtn = findViewById(R.id.donorRegBtn);

        loader = new ProgressDialog(this);
        userAuth = FirebaseAuth.getInstance();

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        donorRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fullName = donorFullName.getText().toString().trim();
                final String number = donorPhnNum.getText().toString().trim();
                final String bloodGroup = bgSpinner.getSelectedItem().toString();
                final String email = donorEmail.getText().toString().trim();
                final String password = donorPassword.getText().toString().trim();

                // Validate inputs
                if (TextUtils.isEmpty(fullName)) {
                    donorFullName.setError("Name is Required");
                    return;
                }
                if (TextUtils.isEmpty(number)) {
                    donorPhnNum.setError("Phone Number is Required");
                    return;
                }
                if (bloodGroup.equals("Select Your Blood Group>")) {
                    Toast.makeText(DonorRegistrationActivity.this, "Select Blood Group", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    donorEmail.setError("Email is Required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    donorPassword.setError("Password is Required");
                    return;
                }
                if (password.length() < 6) {
                    donorPassword.setError("Password must be at least 6 characters");
                    return;
                }
                if (resultUri == null) {
                    Toast.makeText(DonorRegistrationActivity.this, "Profile image is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Proceed with registration
                loader.setMessage("Just a moment...");
                loader.setCanceledOnTouchOutside(false);
                loader.show();

                userAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            String error = task.getException().toString();
                            Toast.makeText(DonorRegistrationActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                            loader.dismiss();
                        } else {
                            String currentUserId = userAuth.getCurrentUser().getUid();
                            userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

                            HashMap<String, Object> userInfo = new HashMap<>();
                            userInfo.put("id", currentUserId);
                            userInfo.put("name", fullName);
                            userInfo.put("email", email);
                            userInfo.put("phonenumber", number);
                            userInfo.put("bloodgroup", bloodGroup);
                            userInfo.put("type", "donor");
                            userInfo.put("search", "donor" + bloodGroup);

                            userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        uploadProfileImage(currentUserId);
                                    } else {
                                        Toast.makeText(DonorRegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        loader.dismiss();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        donorBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DonorRegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void uploadProfileImage(String currentUserId) {
        final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile images").child(currentUserId);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        UploadTask uploadTask = filePath.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DonorRegistrationActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                loader.dismiss();
            }
        });
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null) {
                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            Map<String, Object> newImageMap = new HashMap<>();
                            newImageMap.put("profilepictureurl", imageUrl);
                            userDatabaseRef.updateChildren(newImageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(DonorRegistrationActivity.this, "Image URL added to database successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(DonorRegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                    loader.dismiss();
                                    Intent intent = new Intent(DonorRegistrationActivity.this, DashboardActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            resultUri = data.getData();
            profile_image.setImageURI(resultUri);
        }
    }
}
