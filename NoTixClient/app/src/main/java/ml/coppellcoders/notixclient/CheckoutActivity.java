package ml.coppellcoders.notixclient;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.security.MessageDigest;


import java.util.Calendar;

import ml.coppellcoders.notixclient.blockchain.Block;

public class CheckoutActivity extends AppCompatActivity {
    private DatabaseReference mFirebaseRef,mFirebaseRefBus;

    ImageView img;

    TextView title, date, subt, fee, tax, total;

    Button apay, credit, face, buy;

    String previousHash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        img = findViewById(R.id.check_img);
        title = findViewById(R.id.title_check);
        date = findViewById(R.id.date_click);
        subt = findViewById(R.id.check_subtotal);
        fee = findViewById(R.id.check_fees);
        tax = findViewById(R.id.check_tax);
        total = findViewById(R.id.check_total);
        apay = findViewById(R.id.androidpay);
        credit = findViewById(R.id.payment_check);
        face = findViewById(R.id.face_check);
        buy = findViewById(R.id.check_checkout);

        Intent i = getIntent();

        final EventModel event = (EventModel)i.getSerializableExtra("event");
        final int quant = i.getIntExtra("num",1);
        final String name = i.getStringExtra("name");
        final String imgurl = i.getStringExtra("img");

        if(name!=null){
            face.setText(name);
        }

            Picasso.with(getApplicationContext()).load(event.getImg()).fit().centerCrop().into(img);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

        mFirebaseRef = database.getReference("Users").child(currentFirebaseUser.getUid()).child("tickets");
        mFirebaseRefBus = database.getReference("Events").child(event.getKey()).child("tickets");
        title.setText(event.getName());

            subt.setText(String.format("$%.2f",quant*event.getPrice()));

       fee.setText(String.format("$%.2f",quant*event.getPrice()*.02));
       tax.setText(String.format("$%.2f",quant*event.getPrice()*.0825));
       total.setText(String.format("$%.2f",quant*event.getPrice()+quant*event.getPrice()*.02+quant*event.getPrice()*.0825));



        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(event.getDate());


        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        String[] monthNames = new String[12]; // and populate...
        monthNames[0] = "Jan";
        monthNames[1] = "Feb";
        monthNames[2] = "Mar";
        monthNames[3] = "Apr";
        monthNames[4] = "May";
        monthNames[5] = "Jun";
        monthNames[6] = "Jul";
        monthNames[7] = "Aug";
        monthNames[8] = "Sep";
        monthNames[9] = "Oct";
        monthNames[10] = "Nov";
        monthNames[11] = "Dec";
        String hour ="";
        if(mHour+1>12){
            hour = mHour-11+"pm";
        }else{
            hour = mHour+"am";
        }
        date.setText(monthNames[mMonth] + " " + mDay+", "+hour);


        final String[] pushid = new String[1];

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!face.getText().toString().equals("Select a Face")){
                    pushid[0] = mFirebaseRef.push().getKey();


                    mFirebaseRefBus.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            previousHash = dataSnapshot.child("hash").getValue().toString();
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    mFirebaseRef.push().setValue(new BuyInfoModel(event.getDate(),event.getImg(),event.getName(),event.getVenue(),event.getCategory(),event.getAddress(),imgurl,name,event.getPrice(),event.getQuantity(),quant)).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFirebaseRefBus.push().setValue(new Block(event.getDate(),event.getImg(),event.getName(),event.getVenue(),event.getCategory(),event.getAddress(),imgurl,name,event.getPrice(),event.getQuantity(),quant,previousHash)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Intent intent = new Intent(getApplicationContext(), SuccessActivity.class);
                                    intent.putExtra("num", pushid[0]);
                                    startActivity(intent);
                                    finish();

                                }
                            });


                        }
                    });

                }else{
                    Snackbar.make(findViewById(R.id.checkcont), "Please select a face", Snackbar.LENGTH_SHORT).show();

                }

            }
        });

        face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FaceSelectActivity.class);
                intent.putExtra("event", event);
                intent.putExtra("num", quant);
                startActivity(intent);


            }
        });




    }
    public static String applySha256(String input){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            //Applies sha256 to our input,
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
