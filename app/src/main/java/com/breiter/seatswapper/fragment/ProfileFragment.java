package com.breiter.seatswapper.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.breiter.seatswapper.R;
import com.breiter.seatswapper.model.User;
import com.breiter.seatswapper.tool.LogoutManager;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final int IMAGE_GALLERY_REQUEST = 1;

    private TextView usernameTextView;
    private TextView uploadImgTxtView;
    private TextView logoutTxtView;
    private CircleImageView profileImageView;

    private LogoutManager logoutManager;

    private FirebaseUser currentUser;
    private DatabaseReference userRef;
    private StorageReference storageReference;
    private StorageTask uploadTask;
    private Uri imageUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        storageReference = FirebaseStorage.getInstance().getReference("Uploads");

        bindViews(view);  //1

        setUserProfile(); //2


        uploadImgTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                verifyReadStoragePermission();  //3

            }
        });


        // Logout and redirect to Login Activity
        logoutTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logoutManager = new LogoutManager(getContext());
                logoutManager.logout();

            }
        });


        return view;

    }


    //1.
    private void bindViews(View view) {

        usernameTextView = view.findViewById(R.id.usernameTextView);

        profileImageView = view.findViewById(R.id.profileImageView);

        uploadImgTxtView = view.findViewById(R.id.uploadImgTxtView);

        logoutTxtView = view.findViewById(R.id.logoutTxtView);

    }


    //2. Retrieve current user's photo and username from Firebase and set to the views
    private void setUserProfile() {

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {

                    User user = dataSnapshot.getValue(User.class);

                    usernameTextView.setText(user.getUsername());

                    if (user.getImageURL().equals("default"))
                        profileImageView.setImageResource(R.drawable.user);
                    else
                        Glide.with(getContext()).load(user.getImageURL()).into(profileImageView);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    //3. Checks if the app has permission to read from device storage and proceed
    private void verifyReadStoragePermission() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_GALLERY_REQUEST);

        else
            photoGalleryIntent(); //3a
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == IMAGE_GALLERY_REQUEST) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                photoGalleryIntent(); //3a

            }

        }
    }


    //3a If there's a permission for read the library, start photo intent
    private void photoGalleryIntent() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_GALLERY_REQUEST);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_GALLERY_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {

            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress())
                Toast.makeText(getContext(), "Uploading in progress", Toast.LENGTH_SHORT).show();

            else
                uploadFileFirebase(); //3b

        }
    }


    //3b. Uploading image file to Firebase storage
    private void uploadFileFirebase() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());

        progressDialog.setMessage("Uploading...");

        progressDialog.show();

        if (imageUri != null) {

            String imageName = UUID.randomUUID().toString() + System.currentTimeMillis() + ".jpg";

            final StorageReference filePath = storageReference.child(imageName);

            uploadTask = filePath.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful())
                        throw task.getException();

                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();

                        String imgUri = downloadUri.toString();

                        Map<String, Object> hashMap = new HashMap<>();

                        hashMap.put("imageURL", imgUri);

                        userRef.updateChildren(hashMap);

                        progressDialog.dismiss();

                    } else {
                        Toast.makeText(getContext(), "There was a problem with loading your" +
                                " image...", Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });

        } else {
            //imgUri is null

            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }

    }


}
