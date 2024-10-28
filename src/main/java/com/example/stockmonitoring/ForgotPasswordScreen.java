package com.example.stockmonitoring;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordScreen extends BaseActivity {
    Button btnSendLink;

    EditText emailInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_screen);

        btnSendLink = findViewById(R.id.btnSendLink);
        emailInput = findViewById(R.id.emailInput);
    }

    public void sendResetLink(View view) {
        if (view == btnSendLink) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                if ((emailInput.getText().toString().equals("")))
                {
                    Toast.makeText(this, "please type in your email address", Toast.LENGTH_SHORT).show();
                }
                else if (!isEmailOK(emailInput.getText().toString()))
                {
                    Toast.makeText(this, "email address isn't valid", Toast.LENGTH_SHORT).show();
                }

                else {
                    auth = FirebaseAuth.getInstance();

                    auth.sendPasswordResetEmail(emailInput.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgotPasswordScreen.this, "link has been sent", Toast.LENGTH_SHORT).show();
                                        createLoginDialog();
                                    }
                                    else
                                    {
                                        Log.d("sent", task.getException().toString());
                                    }
                                }
                            });
                    Toast.makeText(this, "link has been sent", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}