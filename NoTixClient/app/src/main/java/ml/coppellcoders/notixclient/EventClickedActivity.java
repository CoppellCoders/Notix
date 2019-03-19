package ml.coppellcoders.notixclient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventClickedActivity extends AppCompatActivity {
    QuantityAdapter adapter;
    RecyclerView rv;
    List<Integer> number;
    ImageButton share, getDirection;
    ImageView img;
    TextView addr, price, title,date;
    Button checkout;
    int quan =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_clicked);
        share = findViewById(R.id.sharebtn);
        getDirection = findViewById(R.id.direction_btn);
        addr = findViewById(R.id.adrr_click);
        price = findViewById(R.id.quan_price);
        title = findViewById(R.id.title_clicked);
        checkout = findViewById(R.id.quan_checkout);
        img = findViewById(R.id.quan_img);
        date = findViewById(R.id.quan_date);
        number = new ArrayList<>();
        number.add(1);
        number.add(2);
        number.add(3);
        number.add(4);
        number.add(5);
        number.add(6);
        number.add(7);
        number.add(8);
        number.add(9);
        number.add(10);
        rv = findViewById(R.id.quanrecy);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new QuantityAdapter(number, getApplicationContext());
        rv.setAdapter(adapter);

        Intent i = getIntent();

        final EventModel event = (EventModel)i.getSerializableExtra("event");
        System.out.println(event.toString());

        if(event.getImg().contains("http"))
            Picasso.with(getApplicationContext()).load(event.getImg()).fit().centerCrop().into(img);
        else
            img.setImageBitmap(decodeBase64(event.getImg()));


        title.setText(event.getName());

        addr.setText(event.getVenue()+"\n" + event.getAddress().substring(event.getAddress().indexOf(",")+2).trim());

        final String map = "http://maps.google.com/maps?q=" + event.getAddress().replace( " ","+");
        getDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                startActivity(i);
            }
        });

        price.setText("Select quantity");
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


        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!price.getText().toString().equals("Select quantity")){
                    Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);
                    intent.putExtra("event", event);
                    intent.putExtra("num", quan);
                    startActivity(intent);
                }else{
                    Snackbar.make(findViewById(R.id.clickedcont), "Select quantity", Snackbar.LENGTH_SHORT).show();
                }

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody = "Come with me to " + event.getName()+" at the " + event.getVenue()+" using NoTix";
                myIntent.putExtra(Intent.EXTRA_SUBJECT,shareBody);
                myIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(Intent.createChooser(myIntent,"Share using"));
            }
        });

        adapter.setListener(new RecyclerViewAdapter.Listener<Integer>() {
            @Override
            public void onClick(@NonNull Integer s) {
                quan =s;
                if(event.price!=0)
                price.setText(String.format("$%.2f",s*event.getPrice()));
                else
                    price.setText("Free");

                Snackbar snack = Snackbar.make(findViewById(R.id.clickedcont), "Selected " + s, Snackbar.LENGTH_SHORT);
                View view = snack.getView();
                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
                params.gravity = Gravity.TOP;
                params.topMargin = 60;
                view.setLayoutParams(params);
                snack.show();

            }
        });



    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
