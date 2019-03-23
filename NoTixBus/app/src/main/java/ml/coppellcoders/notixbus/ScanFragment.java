package ml.coppellcoders.notixbus;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Calendar;

public class ScanFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        IntentIntegrator ii = IntentIntegrator.forSupportFragment(ScanFragment.this);
        ii.setOrientationLocked(false);
        ii.setPrompt("Scan Ticket");
        ii.initiateScan();
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            String raw = scanningResult.getContents();
            String parts[] = raw.split("\\[");
            String event = parts[0];
            String ticket = parts[1];
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Events/"+event+"/tickets/"+ticket);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String guestName = dataSnapshot.child("guestname").getValue().toString();
                    String image = dataSnapshot.child("faceimg").getValue().toString();
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
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Ticket Info")
                            .setMessage(String.format("Info for %s%nEvent Name: %s%nDate: %s%n numTickets: %s%n", guestName, event, date, numTickets))
                            .setPositiveButton(android.R.string.no, null)
                            .show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Toast.makeText(getActivity(),  scanningResult.getContents() + "   type:" + scanningResult.getFormatName(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Nothing scanned", Toast.LENGTH_SHORT).show();
        }
    }
}
