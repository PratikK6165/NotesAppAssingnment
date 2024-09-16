package com.notesappassingnment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.notesappassingnment.Fragments.LoginFragment;
import com.notesappassingnment.Fragments.NotesHomepageFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getSharedPreferences("UserLoginDetails", Context.MODE_PRIVATE);
        String userId = sharedPref.getString("userId", null);

        if (userId != null) {
            // User is logged in, navigate to HomeFragment
            navigateToHomeFragment();

        } else {
            // User is not logged in, navigate to LoginFragment
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, new LoginFragment())
                        .commit();
            }
        }


    }

    public void navigateToHomeFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main, new NotesHomepageFragment())
                .commit();
    }
}