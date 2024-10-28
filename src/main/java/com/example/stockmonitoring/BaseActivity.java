package com.example.stockmonitoring;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public class BaseActivity extends AppCompatActivity {
    Button btnLogin;
    Button btnRegister;
    Button btnForgotPassword;
    Button addPicture;

    Dialog login;
    Dialog register;

    EditText loginEmailInput;
    EditText loginPasswordInput;
    EditText registerEmailInput;
    EditText registerUsernameInput;
    EditText registerPasswordInput;
    EditText registerPasswordConfirmation;

    FirebaseAuth auth;
    FirebaseStorage storage;
    StorageReference storageReference;

    //Intent passInfo;
    Intent forgotPasswordScreen;
    Intent profileScreen;
    Intent homeScreen;
    Intent aboutUsScreen;

    ImageView profilePicture;

    SharedPreferences saveUsername;
    SharedPreferences.Editor editUsername;

    User user;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        homeScreen = new Intent(this, MainActivity.class);

        saveUsername = getSharedPreferences("username", 0);

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();

        if (id == R.id.action_login) {
            createLoginDialog();
        }
        else if (R.id.action_register == id) {
            createRegisterDialog();
        }
        else if (R.id.action_profile == id) {
            profileScreen = new Intent(this, ProfileScreen.class);
            startActivity(profileScreen);
        }

        else if (R.id.action_home == id)
        {
            startActivity(homeScreen);
        }

        else if (R.id.action_about == id)
        {
            aboutUsScreen = new Intent(this, AboutScreen.class);
            startActivity(aboutUsScreen);
        }

        else if (R.id.action_switch == id)
        {
            saveUsername.edit().remove("username").apply();

            Toast.makeText(this, "User disconnected", Toast.LENGTH_SHORT).show();

            createLoginDialog();
         }

        else if (R.id.action_logout == id)
        {
            saveUsername.edit().remove("username").apply();
            FirebaseAuth.getInstance().signOut();

            Toast.makeText(this, "User disconnected", Toast.LENGTH_SHORT).show();

            finishAffinity();
        }
        return true;
    }

    public void createLoginDialog() {
        login = new Dialog(this);
        login.setContentView(R.layout.login_screen);
        login.setTitle("Login");

        loginEmailInput = login.findViewById(R.id.loginEmailInput);
        loginPasswordInput = login.findViewById(R.id.loginPasswordInput);
        btnForgotPassword = login.findViewById(R.id.btnForgotPassword);
        btnLogin = login.findViewById(R.id.btnLogin);
        auth = FirebaseAuth.getInstance();

        login.setCancelable(true);
        login.show();
    }

    public void createRegisterDialog() {
        register = new Dialog(this);
        register.setContentView(R.layout.register_screen);
        register.setTitle("Register Account");

        profilePicture = register.findViewById(R.id.profilePicture);
        addPicture = register.findViewById(R.id.addPicture);
        registerEmailInput = register.findViewById(R.id.registerEmailInput);
        registerUsernameInput = register.findViewById(R.id.registerUsernameInput);
        registerPasswordInput = register.findViewById(R.id.registerPasswordInput);
        registerPasswordConfirmation = register.findViewById(R.id.registerPasswordConfirmation);
        btnRegister = register.findViewById(R.id.btnRegister);
        auth = FirebaseAuth.getInstance();

        register.setCancelable(true);
        register.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void userIdentify(View view)
    {
        if (view == btnLogin)
        {
            String email = loginEmailInput.getText().toString();
            String password = loginPasswordInput.getText().toString();
                if (isEmailOK(email) && isPasswordOK(password))
                {
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        String uid=auth.getUid();
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("users").document(uid).get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        user = documentSnapshot.toObject(User.class);
                                                        String username=user.getUsername();
                                                        editUsername = saveUsername.edit();
                                                        editUsername.putString("username", username);
                                                        editUsername.commit();
                                                        Toast.makeText(BaseActivity.this, "Welcome " + username, Toast.LENGTH_SHORT).show();
                                                        login.cancel();
                                                        finish();
                                                        startActivity(homeScreen);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(BaseActivity.this, "Failed to get the data.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }

                                    else
                                    {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(BaseActivity.this, "Sign in failed, please try again", Toast.LENGTH_SHORT).show();
                                        Log.d("huston", task.getException().toString());
                                    }
                                }
                            });
                }

                else if (TextUtils.isEmpty(loginEmailInput.getText()) || TextUtils.isEmpty(loginPasswordInput.getText()))
                {
                    Toast.makeText(this, "Please fill all the required fields", Toast.LENGTH_LONG).show();
                }
        }
    }

    public void forgotPassword(View view)
    {
        if (view == btnForgotPassword)
        {
            forgotPasswordScreen = new Intent(this, ForgotPasswordScreen.class);
            startActivity(forgotPasswordScreen);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void userRegister(View view)
    {
        if (view == btnRegister)
        {
            String email = registerEmailInput.getText().toString();
            String username = registerUsernameInput.getText().toString();
            String password = registerPasswordInput.getText().toString();
            String passwordConfirmation = registerPasswordConfirmation.getText().toString();

            // check validity
            if (isEmailOK(email) && isUsernameOK(username) && isPasswordOK(password) && isPasswordOK(passwordConfirmation))
            {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseFirestore database =FirebaseFirestore.getInstance();
                            User user = new User(email, username);
                            database.collection("users").document(Objects.requireNonNull(auth.getUid()))
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>()
                                    {
                                        @Override
                                        public void onSuccess(Void unused)
                                        {
                                            storage = FirebaseStorage.getInstance();
                                            storageReference = storage.getReference();
                                            addPictureToFirebase();
                                            Toast.makeText(BaseActivity.this, "Account registered, please log in", Toast.LENGTH_SHORT).show();
                                            register.cancel();
                                            createLoginDialog();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener()
                                    {
                                        @Override
                                        public void onFailure(@NonNull Exception e)
                                        {
                                            Toast.makeText(BaseActivity.this, "Sign up failed, please try again", Toast.LENGTH_LONG).show();
                                        }
                                    });
                            Toast.makeText(BaseActivity.this, "processing...", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(BaseActivity.this, "Sign up failed, please try again", Toast.LENGTH_LONG).show();
                            Log.d("huston", task.getException().toString());
                        }
                    }
                });
            }

            else if (TextUtils.isEmpty(registerEmailInput.getText()) || TextUtils.isEmpty(registerPasswordInput.getText()) || TextUtils.isEmpty(registerPasswordConfirmation.getText()))
            {
                Toast.makeText(this, "Please fill all the required fields", Toast.LENGTH_LONG).show();
            }

            else if (!registerPasswordInput.getText().toString().equals(registerPasswordConfirmation.getText().toString()))
            {
                Toast.makeText(this, "Passwords didn't match", Toast.LENGTH_SHORT).show();
                registerPasswordInput.getText().clear();
                registerPasswordConfirmation.getText().clear();
            }

            else if (!isPasswordOK(password) || !isPasswordOK(passwordConfirmation))
            {
                Toast.makeText(this, "Password is shorter than required (5 characters)", Toast.LENGTH_LONG).show();
                registerPasswordInput.getText().clear();
                registerPasswordConfirmation.getText().clear();
            }
        }

        if (view  == addPicture)
        {
            Intent getPicture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            setPfpActivityResultLauncher.launch(getPicture);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean isEmailOK(String email)
    {
        if (TextUtils.isEmpty(email))
        {
            return false;
        }

        else if (email.length() < 2)
        {
            return false;
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            return false;
        }

        return true;
    }

    public boolean isUsernameOK(String username)
    {
        if (TextUtils.isEmpty(username))
        {
            return false;
        }

        else if (username.length() < 2)
        {
            return false;
        }

        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if(!username.chars().allMatch(Character::isLetter))
            {
                return false;
            }
        }

        return true;
    }

    public boolean isPasswordOK(String password)
    {
        if (TextUtils.isEmpty(password))
        {
            return false;
        }
        else if (password.length() < 5)
        {
            return false;
        }

        return true;
    }

    private void addPictureToFirebase() {
        Bitmap bitmap = ((BitmapDrawable) profilePicture.getDrawable()).getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] data = outputStream.toByteArray();

        UploadTask uploadTask = storageReference.child("images/users").child(auth.getUid()).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(BaseActivity.this, "Could not upload image", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Toast.makeText(BaseActivity.this, "SUCCESS - image uploaded.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private ActivityResultLauncher<Intent> setPfpActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //here we will handle the result of our intent
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null)
                        {
                            Uri selectedImage = data.getData();
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                                profilePicture.setImageBitmap(bitmap);
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else {
                        //cancelled
                        Toast.makeText(BaseActivity.this, "Cancelled...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
}