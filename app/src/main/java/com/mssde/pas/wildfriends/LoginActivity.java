package com.mssde.pas.wildfriends;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Firebase
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    final static String LOG_TAG = "btb";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Button publish;
    private Button show_publish;
    private Button show_map;
    //private Button show_messages;
    private static final int RC_SIGN_IN = 2022;

    private boolean first_time = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        findViewById(R.id.logoutButton).setOnClickListener(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // user is signed in
                CharSequence username = user.getDisplayName();
                if( first_time) {
                    Toast.makeText(LoginActivity.this, getString(R.string.login_user, username), Toast.LENGTH_LONG).show();
                    first_time = false;
                }
                Log.i(LOG_TAG, "onAuthStateChanged() " + getString(R.string.login_user, username));
                ((TextView) findViewById(R.id.textView)).setText(getString(R.string.login_user, username));

                publish = (Button) findViewById(R.id.publiButton);
                show_publish = (Button) findViewById(R.id.verPubliButton);
                show_map = (Button) findViewById(R.id.mapButton);
                //show_messages = (Button) findViewById(R.id.mesButton);

                publish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openPublishActivity();
                    }
                });

                show_publish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openListAdsActivity();
                    }
                });

                show_map.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openMapActivity();
                    }
                });

            } else {
                // user is signed out
                startActivityForResult(
                        // Get an instance of AuthUI based on the default app
                        AuthUI.getInstance().
                                createSignInIntentBuilder().
                                setAvailableProviders(Arrays.asList(
                                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                                        new AuthUI.IdpConfig.EmailBuilder().build()
                                )).
                                setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */).
                                //setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */).
                                        build(),
                        RC_SIGN_IN
                );

                //Remains logged in when unique gmail account
                // setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */).
                //setIsSmartLockEnabled(BuildConfig.DEBUG /* credentials */, true /* hints */).

            }
        };
    }

    public void openPublishActivity(){
        Intent intent = new Intent(this, PublishActivity.class);
        startActivity(intent);
    }

    public void openListAdsActivity(){
        Intent intent = new Intent(this, ListaAnunciosActivity.class);
        startActivity(intent);
    }

    public void openMapActivity(){
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, R.string.signed_in, Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, "onActivityResult " + getString(R.string.signed_in));
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, R.string.signed_cancelled, Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, "onActivityResult " + getString(R.string.signed_cancelled));
                finish();
            }
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        mFirebaseAuth.signOut();
        Log.i(LOG_TAG, getString(R.string.signed_out));
    }
}