package com.example.signinactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {
    ImageView postImage;
    EditText postCommentText;
    Button postBtnUpload;
    Intent intent;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    Uri uriSelectedImage;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        postCommentText = findViewById(R.id.post_commentText);
        postImage = findViewById(R.id.post_imageView);
        postBtnUpload = findViewById(R.id.post_btnUpload);
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

    }

    public void upload(View view) {
        postCommentText.clearFocus();
        UUID uuıd = UUID.randomUUID();
        final String imageName = "images/" + uuıd + ".jpg";
        StorageReference storageReference = mStorageRef.child(imageName);
        storageReference.putFile(uriSelectedImage).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //download url
                StorageReference newReference = FirebaseStorage.getInstance().getReference(imageName);
                newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl = uri.toString();
                        FirebaseUser user = mAuth.getCurrentUser();
                        String email = user.getEmail();
                        String name = user.getDisplayName();
                        String userComment = postCommentText.getText().toString();
                        UUID uuıd1 = UUID.randomUUID();
                        String uuidString = uuıd1.toString();

                        HashMap<String, Object> postData = new HashMap<>();
                        postData.put("userEmail",email);
                        postData.put("Comment",userComment);
                        postData.put("downloadUrl",downloadUrl);
                        postData.put("Date :", FieldValue.serverTimestamp());

                        FirebaseFirestore.getInstance().collection("Posts")
                                .add(postData)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(getApplicationContext(),"Post Shared!",Toast.LENGTH_LONG).show();
                                        Intent feedIntent = new Intent(getApplicationContext(),FeedActivity.class);
                                        startActivity(feedIntent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            }
                        });

                        //Firebase Real Time DataBase
                        //myRef.child("Posts").child(uuidString).child("userEmail").setValue(email);
                        //myRef.child("Posts").child(uuidString).child("Comment").setValue(userComment);
                        //myRef.child("Posts").child(uuidString).child("downloadUrl").setValue(downloadUrl);
                        //Toast.makeText(getApplicationContext(),"Post Shared",Toast.LENGTH_LONG).show();
                        //Intent feedIntent = new Intent(getApplicationContext(),FeedActivity.class);
                        //startActivity(feedIntent);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                //userName, comment,


            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    public void selectImage(View view) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            uriSelectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriSelectedImage);
                postImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}
