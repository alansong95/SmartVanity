package com.example.alan.smartvanity;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {



    private static String KEY_SOME_INTEGER = "KEY_SOME_INTEGER";

    private FirebaseAuth mAuth;
    private String uiState;

    ImageView mBackgroundImageView;
    EditText mEmailEditText;
    EditText mPasswordEditText;
    EditText mConfirmPasswordEditText;
    TextView mSignUp;
    TextView mSignInTitle;
    Button mSignInButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        uiState = "signup";
        setContentView(R.layout.activity_login);

        if ( savedInstanceState != null ) {
            int a = savedInstanceState.getInt(KEY_SOME_INTEGER);
        }

        findViews();
        setupListeners();
        populateUI();
//        FragmentManager
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            System.out.println("No user is currently signed in.");
            // No user is signed in.
        } else {
            // Go to home page.
        }
    }

    private void findViews() {
        mBackgroundImageView = findViewById(R.id.activity_login_background_image_view);
        mEmailEditText = findViewById(R.id.activity_login_email_edit_text);
        mPasswordEditText = findViewById(R.id.activity_login_password_edit_text);
        mConfirmPasswordEditText = findViewById(R.id.activity_login_confirm_password_edit_text);
        mSignInButton = findViewById(R.id.activity_login_signin_button);
        mSignUp = findViewById(R.id.activity_login_signup_text);
        mSignInTitle = findViewById(R.id.activity_login_signin_title_text_view);
    }

    private void setupListeners() {

//        mBackgroundImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // click callback
//                Intent someActivityIntent = new Intent(getApplicationContext(), SelectWidget.class);
//                Bundle stuff = new Bundle();
//                startActivity( someActivityIntent );
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        // ui thread
//                    }
//                });
//            }
//        });

        mBackgroundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyBoard();
            }
        });

        mEmailEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Tapped the email edit text.");
            }
        });

        mPasswordEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Tapped the password edit text.");
            }
        });

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Attempted sign in.");
            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Smart Vanity", "Switching UI State");
                if (uiState == "signin") {
                    uiState = "signup";
                } else {
                    uiState = "signin";
                }
                populateUI();
            }
        });

    }

    private void populateUI() {
        Log.d("SmartVanity", "Populating UI with state: " + uiState);
        if (uiState == "signin") {
            mConfirmPasswordEditText.setVisibility(View.GONE);
            mSignInButton.setText("Sign In");
            mSignUp.setText("Don't have an account yet?\n\nSIGN UP");
            mSignInTitle.setText("Sign In");
        } else if (uiState == "signup") {
            mConfirmPasswordEditText.setVisibility(View.VISIBLE);
            mSignInButton.setText("Sign Up");
            mSignUp.setText("Already have an account?\n\nSIGN IN");
            mSignInTitle.setText("Sign Up");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int a = 1;
        outState.putInt( KEY_SOME_INTEGER, a );

        super.onSaveInstanceState(outState);
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
