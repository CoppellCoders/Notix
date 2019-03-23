package ml.coppellcoders.notixbus;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ml.coppellcoders.notixbus.*;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    changeFragment(new EventsFragment());
                    return true;
                case R.id.navigation_dashboard:
                    changeFragment(new ScanFragment());
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextMessage = (TextView) findViewById(R.id.message);
        FrameLayout fl = findViewById(R.id.content);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        ViewTreeObserver vto = navigation.getViewTreeObserver();

        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                fl.setLayoutParams(new ConstraintLayout.LayoutParams(width, height - navigation.getHeight()));
            }
        });

        System.out.println("Navigation Height: " + navigation.getHeight());
        fl.setLayoutParams(new ConstraintLayout.LayoutParams(width, height-200));

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        changeFragment(new EventsFragment());
    }
    public boolean changeFragment(Fragment target){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, target);
        ft.commit();
        return true;
    }

}
