package ml.coppellcoders.notixbus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



import java.util.Calendar;


public class QRScanner extends Activity implements QRCodeReaderView.OnQRCodeReadListener {


    ImageView cancel;
    TextView name;
    ImageView image;
    TextView eventName;
    TextView eventTime;
    TextView eventTickets;
    AlertDialog dialog;
    private QRCodeReaderView qrCodeReaderView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        qrCodeReaderView = findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(false);

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        String parts[] = text.split("\\[");
        String event = parts[0];
        String ticket = parts[1];
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Events/"+event+"/tickets/"+ticket);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String guestName = dataSnapshot.child("guestname").getValue().toString();
                String imageSrc = dataSnapshot.child("faceimg").getValue().toString();
                String event = dataSnapshot.child("name").getValue().toString();
                String time = dataSnapshot.child("time").getValue().toString();
                String numTickets = dataSnapshot.child("quant").getValue().toString();
                String[] monthNames = new String[12];
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
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(time));
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                String date = monthNames[mMonth] + " " + mDay;
                View view = getLayoutInflater().inflate(R.layout.attendee_info, null);
                cancel = view.findViewById(R.id.attendee_cancel);
                name = view.findViewById(R.id.attendee_name);
                image = view.findViewById(R.id.attendee_image);
                eventName = view.findViewById(R.id.attendee_event_name);
                eventTime = view.findViewById(R.id.attendee_event_time);
                eventTickets = view.findViewById(R.id.attendee_event_tickets);
                if(dialog==null || !dialog.isShowing()) {
                    dialog = new AlertDialog.Builder(QRScanner.this)
                            .setView(view)
                            .create();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    name.setText(guestName);
                    image.setImageBitmap(decodeBase64(imageSrc));
                    eventName.setText(event);
                    eventTime.setText(date);
                    eventTickets.setText(numTickets + " ticket(s)");
                    dialog.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }
}
