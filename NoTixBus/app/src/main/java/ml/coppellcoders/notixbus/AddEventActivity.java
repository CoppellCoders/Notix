package ml.coppellcoders.notixbus;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class AddEventActivity extends Activity{


    EditText eventName;
    EditText eventDate;
    EditText eventTime;
    EditText eventPrice;
    EditText eventQuantity;
    EditText eventVenue;
    EditText eventVenueAddress;
    Button eventCreate;
    final static int PICK_IMAGE = 1;
    Event event;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        eventName = findViewById(R.id.add_event_name);
        eventDate = findViewById(R.id.add_event_date);
        eventTime = findViewById(R.id.add_event_time);
        eventPrice = findViewById(R.id.add_event_price);
        eventQuantity = findViewById(R.id.add_event_quantity);
        eventVenue = findViewById(R.id.add_event_venue);
        eventVenueAddress = findViewById(R.id.add_event_venue_address);
        eventCreate = findViewById(R.id.add_event_create);

        eventCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String date = eventDate.getText().toString() + " " + eventTime.getText().toString();
                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm");
                try {
                    event = new Event(eventVenueAddress.getText().toString(), "Other", "temp",
                            eventName.getText().toString(), Long.parseLong(eventPrice.getText().toString()),
                            Long.parseLong(eventQuantity.getText().toString()),
                            df.parse(date).getTime(),
                            eventVenue.getText().toString());
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                }catch(ParseException e){
                    e.printStackTrace();
                }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK){
            final Bundle extras = data.getExtras();
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                bitmap = Bitmap.createScaledBitmap(bitmap, 198, 132, true);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                event.setImg(encoded);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Events");
                String key = myRef.push().getKey();
                DatabaseReference tRef = database.getReference("Events/"+key);
                tRef.setValue(event);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
