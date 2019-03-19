package ml.coppellcoders.notixclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

/**
 * Created by Darshan on 1/20/2018.
 */

public class ShowSplash extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        startActivity(new Intent(this, Login.class));
        finish();
    }
}
