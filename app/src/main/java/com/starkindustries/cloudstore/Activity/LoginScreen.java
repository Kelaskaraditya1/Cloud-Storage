package com.starkindustries.cloudstore.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.starkindustries.cloudstore.Keys.Keys;
import com.starkindustries.cloudstore.R;
import com.starkindustries.cloudstore.databinding.ActivityLoginScreenBinding;

import java.security.Key;

public class LoginScreen extends AppCompatActivity {
    public ActivityLoginScreenBinding binding;
    public FirebaseAuth auth;
    public FirebaseFirestore store;
    public DocumentReference reference;
    public String userid;
    public static boolean pass_ed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen);
        binding= DataBindingUtil.setContentView(LoginScreen.this,R.layout.activity_login_screen);
        auth=FirebaseAuth.getInstance();
        store=FirebaseFirestore.getInstance();
        userid=auth.getCurrentUser().getUid();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            binding.password.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction()==MotionEvent.ACTION_UP)
                    {
                        int selection=binding.password.getSelectionEnd();
                        if(event.getRawX()>=(binding.password.getRight()-binding.password.getCompoundDrawables()[Keys.RIGHT].getBounds().width()))
                        {

                            if(pass_ed)
                            {
                               binding.password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.visibility_off,0);
                               binding.password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                               pass_ed=false;
                            }
                            else
                            {
                                binding.password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.visibility_on,0);
                                binding.password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                                pass_ed=true;
                            }
                            binding.password.setSelection(selection);
                            return true;
                        }
                    }
                    return false;
                }
            });
            binding.login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reference=store.collection(Keys.COLLECTION_NAME).document(userid);
                    if(TextUtils.isEmpty(binding.username.getText().toString().trim()))
                    {
                        binding.username.setError("Enter proper username");
                        return ;
                    }
                    else if(TextUtils.isEmpty(binding.password.getText().toString().trim()))
                    {
                        binding.password.setError("Enter proper password");
                        return ;
                    }
                    else if (binding.password.getText().toString().trim().length()<8)
                    {
                        binding.password.setError("Password length should be greater than 8 charecters");
                        return ;
                    }
                    auth.signInWithEmailAndPassword(binding.username.getText().toString().trim(),binding.password.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {

//                                reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                                    @Override
//                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                                        if(value.exists())
//                                        {
//                                            Log.d("username","name: "+value.get(Keys.NAME));
//                                        }
//                                    }
//                                });
                                reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if(value.exists())
                                        {
                                            Log.d("username","name: "+value.get(Keys.PHONE_NO));
                                        }
                                        else Log.d("nameerror","message : "+error);
                                    }
                                });
                                Intent inext = new Intent(LoginScreen.this,DashBoard.class);
//                                            String name=value.getString(Keys.NAME);
//                                            Log.d("username","The name is "+name);
                                startActivity(inext);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginScreen.this, "Either email or password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            binding.signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent inext = new Intent(LoginScreen.this,RegisterActivity.class);
                    startActivity(inext);
                }
            });
            return insets;
        });
    }
}