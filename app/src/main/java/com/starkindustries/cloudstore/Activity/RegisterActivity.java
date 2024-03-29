package com.starkindustries.cloudstore.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.starkindustries.cloudstore.Keys.Keys;
import com.starkindustries.cloudstore.R;
import com.starkindustries.cloudstore.databinding.ActivityRegisterBinding;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    public ActivityRegisterBinding binding;
    public boolean passed;
    public FirebaseAuth auth;
    public FirebaseFirestore store;
    public DocumentReference refrences;
    public String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        binding= DataBindingUtil.setContentView(RegisterActivity.this,R.layout.activity_register);
        auth=FirebaseAuth.getInstance();
        store=FirebaseFirestore.getInstance();
        userid=auth.getCurrentUser().getUid();
        refrences=store.collection(Keys.COLLECTION_NAME).document(userid);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            binding.login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent inext = new Intent(RegisterActivity.this,LoginScreen.class);
                    startActivity(inext);
                }
            });
            binding.signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent inext = new Intent(RegisterActivity.this, DashBoard.class);
                    startActivity(inext);
                }
            });
            binding.profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gallery = new Intent(Intent.ACTION_PICK);
                    gallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(gallery, Keys.GALLERY_REQ_CODE);
                }
            });
            binding.password.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction()==MotionEvent.ACTION_UP)
                    {
                        int selection = binding.password.getSelectionEnd();
                        if(event.getRawX()>=(binding.password.getRight()-binding.password.getCompoundDrawables()[Keys.RIGHT].getBounds().width()))
                        {
                            if(passed)
                            {
                                binding.password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.visibility_off,0);
                                binding.password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                passed=false;
                            }
                            else
                            {
                                binding.password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.visibility_on,0);
                                binding.password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                                passed=true;
                            }
                            binding.password.setSelection(selection);
                            return true;
                        }
                    }
                    return false;
                }
            });
binding.signup.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(TextUtils.isEmpty(binding.name.getText().toString().trim()))
        {
            binding.name.setError("Enter proper name");
            return;
        }
        else if(TextUtils.isEmpty(binding.email.getText().toString().trim()))
        {
            binding.email.setError("Enter proper email");
            return ;
        }
        else if(TextUtils.isEmpty(binding.phoneNo.getText().toString().trim()))
        {
            binding.phoneNo.setError("Enter proper phone no");
            return ;
        }
        else if(TextUtils.isEmpty(binding.password.getText().toString().trim()))
        {
            binding.password.setError("Enter Proper password");
            return ;
        }
        else if(binding.password.getText().toString().trim().length()<8)
        {
            binding.password.setError("Enter password grater than 8 charecters");
            return ;
        }
        auth.createUserWithEmailAndPassword(binding.email.getText().toString().trim(),binding.password.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                binding.progressbar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(task.isSuccessful())
                        {
                            HashMap<String,String> map = new HashMap<String,String>();
                            map.put(Keys.NAME,binding.name.getText().toString().trim());
                            map.put(Keys.EMAIL,binding.email.getText().toString().trim());
                            map.put(Keys.PHONE_NO,binding.phoneNo.getText().toString().trim());
                            map.put(Keys.PASSWORD,binding.password.getText().toString().trim());
                            refrences.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Intent inext = new Intent(RegisterActivity.this,DashBoard.class);

                                    Toast.makeText(RegisterActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(inext);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterActivity.this, "message: "+e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                },2000);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "net on karle bhadwe", Toast.LENGTH_SHORT).show();
            }
        });
    }
});
            return insets;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            if(requestCode==Keys.GALLERY_REQ_CODE)
            {
                binding.profileImage.setImageURI(data.getData());
            }
        }
    }
}