package ml.coppellcoders.notixclient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyTixClickedActivity extends AppCompatActivity {

    ImageButton share, getDirection;
    ImageView img, faceimg;
    TextView addr, quantity, title,date, subt, fee, tax, total,facename;

    int quan =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytix_clicked);
        share = findViewById(R.id.sharebtn_mytix);
        getDirection = findViewById(R.id.direction_btnmytix);
        addr = findViewById(R.id.adrr_mytix);

        title = findViewById(R.id.title_mytix);
        quantity = findViewById(R.id.quan_mytix);
        img = findViewById(R.id.quan_img);
        date = findViewById(R.id.time_mytix);
        faceimg = findViewById(R.id.face_imgmytix);
        facename = findViewById(R.id.face_titlemytix);

        subt = findViewById(R.id.check_subtotalmytix);
        fee = findViewById(R.id.check_feesmytix);
        tax = findViewById(R.id.check_taxmytix);
        total = findViewById(R.id.check_totalmytix);

        Intent i = getIntent();

        final BuyInfoModel event = (BuyInfoModel)i.getSerializableExtra("event");
        System.out.println(event.toString());

        if (event.getQuant()>1)
        quantity.setText(event.getQuant() + " entries");
        else
            quantity.setText(event.getQuant() + " Entry");

        subt.setText(String.format("$%.2f",event.getQuant()*event.getPrice()));

        fee.setText(String.format("$%.2f",event.getQuant()*event.getPrice()*.02));
        tax.setText(String.format("$%.2f",event.getQuant()*event.getPrice()*.0825));
        total.setText(String.format("$%.2f",event.getQuant()*event.getPrice()+event.getQuant()*event.getPrice()*.02+event.getQuant()*event.getPrice()*.0825));

        if(event.getFaceimg().contains("http"))
            Picasso.with(getApplicationContext()).load(event.getFaceimg()).fit().centerCrop().into(faceimg);
        else
            faceimg.setImageBitmap(decodeBase64(event.getFaceimg()));


        facename.setText(event.getGuestname());


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


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(event.getTime());

        final long millis = event.getTime() - System.currentTimeMillis();

        int hours = (int) (TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.HOURS.toHours(TimeUnit.MILLISECONDS.toHours(millis)));
        int mins = (int) (TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)));
        date.setText(hours + " hours " + mins +" mins");

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





    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
