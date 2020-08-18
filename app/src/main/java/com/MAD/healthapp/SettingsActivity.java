package com.MAD.healthapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private DatabaseReference userDatabase;
    private FirebaseUser firebaseUser;
    private CircleImageView circleImageView;
    private TextView name, status;
    private static final int GALLERY_PICK = 1;
    private StorageReference imageReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        circleImageView = findViewById(R.id.img_dp);
        name = findViewById(R.id.settings_displayName);
        status = findViewById(R.id.txt_status);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        imageReference = FirebaseStorage.getInstance().getReference();
        String current_uid = firebaseUser.getUid();

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        userDatabase.keepSynced(true);
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("Name").getValue().toString());
                String image = dataSnapshot.child("Image").getValue().toString();
                status.setText(dataSnapshot.child("Status").getValue().toString());
                //String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                Picasso.with(SettingsActivity.this).load(image).into(circleImageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //listener for calling status changing activity
        findViewById(R.id.btn_changeStatus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent statusIn = new Intent(SettingsActivity.this, StatusActivity.class);
                statusIn.putExtra("oldStat", status.getText().toString());
                startActivity(statusIn);
            }
        });
        //listener for calling image changing dialog
        findViewById(R.id.btn_changeImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "SELECT IMAGE"), GALLERY_PICK);

            }
        });

    }

    String downUrl = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imgURL = data.getData();
            //set aspect ratio of image
            CropImage.activity(imgURL).setAspectRatio(1, 1).start(this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog = new ProgressDialog(SettingsActivity.this);
                progressDialog.setTitle("Uploading Image");
                progressDialog.setMessage("Please wait while we uploading the image");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                Uri resultUri = result.getUri();
                StorageReference filePath = imageReference.child("profile_images").child(firebaseUser.getUid() + ".jpg");
                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downUrl = uri.toString();
                                Log.e("Down url", downUrl);
                                if (!TextUtils.isEmpty(downUrl))
                                    userDatabase.child("Image").setValue(downUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            progressDialog.dismiss();
                                            Toast.makeText(SettingsActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                                        }

                                    });

                            }
                        });


                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}
