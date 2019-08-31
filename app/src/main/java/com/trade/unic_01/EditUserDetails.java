package com.trade.unic_01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class EditUserDetails extends AppCompatActivity {
    private static final int GALLERY_INTENT = 2;
    String mFName,mLName,mPhone,mEmail,URL;
    EditText Firstname,Lastname;
    TextView Phone,Email;
    ImageView profile;
    Button confirm;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    StorageReference ref;
    Bitmap userImage;
    Uri imageurl;
    private boolean imageSelectSuccessful = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_details);
        Firstname=findViewById(R.id.FirstName);
        Lastname=findViewById(R.id.LastName);
        Email=findViewById(R.id.email);
        Phone=findViewById(R.id.phone);
        profile=findViewById(R.id.profilephoto);
        confirm = findViewById(R.id.buttonConfirm);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);
            }
        });
        mFName=getIntent().getStringExtra("first name");
        mLName=getIntent().getStringExtra("last name");
        mPhone=getIntent().getStringExtra("phone");
        mEmail=getIntent().getStringExtra("email");
        Firstname.setText(mFName);
        Lastname.setText(mLName);
        Phone.setText(mPhone);
        Email.setText(mEmail);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFName = Firstname.getText().toString();
                mLName = Lastname.getText().toString();
                ref = FirebaseStorage.getInstance().getReference().child(mAuth.getUid()+"/userimage.jpg");
                if(imageSelectSuccessful){
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    userImage.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = ref.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            Toast.makeText(EditUserDetails.this, "Image upload successful", Toast.LENGTH_SHORT).show();
                        }
                    });
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageurl = uri;
                            Toast.makeText(EditUserDetails.this, "Image link successful", Toast.LENGTH_SHORT).show();
                            db.collection("users").document(mAuth.getUid()).update("first_name",mFName,"last_name",mLName,"userimage",imageurl.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(EditUserDetails.this, "Details updated successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(EditUserDetails.this,Home.class));
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditUserDetails.this, "Image link failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else Toast.makeText(EditUserDetails.this, "Image selection failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                userImage = bitmap;
                imageSelectSuccessful = true;
                // Log.d(TAG, String.valueOf(bitmap));


                profile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
