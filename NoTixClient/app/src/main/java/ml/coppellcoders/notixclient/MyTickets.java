package ml.coppellcoders.notixclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyTickets extends Fragment {

    RecyclerView  mytix;
    ArrayList<BuyInfoModel> data;
    private DatabaseReference mFirebaseRef;
    private MyTixAdapter  mAdapterEvent;

    ImageButton search;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_mytickets, container, false);

        mytix = view.findViewById(R.id.eventrecy);
        data = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFirebaseRef = database.getReference("Users").child(user).child("tickets");
        mFirebaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    try {

                        System.out.println(dataSnapshot.toString());
                        BuyInfoModel model = dataSnapshot.getValue(BuyInfoModel.class);
                        final String key = dataSnapshot.getKey();
                        data.add(model);

                        System.out.println("nibba " + key);


                        mytix.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

                        mAdapterEvent = new MyTixAdapter(data, getContext());

                        mytix.setAdapter(mAdapterEvent);

                        mAdapterEvent.setListener(new RecyclerViewAdapter.Listener<BuyInfoModel>() {
                            @Override
                            public void onClick(@NonNull BuyInfoModel eventAdapter) {
                                Intent intent = new Intent(getContext(), MyTixClickedActivity.class);
                                intent.putExtra("event", eventAdapter);

                                startActivity(intent);
                            }




                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });









        return view;
    }


}
