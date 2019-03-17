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
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    RecyclerView trending, nearby;
    ArrayList<EventModel> data;
    private DatabaseReference mFirebaseRef;
    private EventAdapter  mAdapterEvent;
    private TrendingEventAdapter mAdapter;

    ImageButton search;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        trending = view.findViewById(R.id.trendingrecy);
        nearby = view.findViewById(R.id.nearbyrecy);
        data = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        mFirebaseRef = database.getReference("Events");
        mFirebaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    try {

                        System.out.println(dataSnapshot.toString());
                        EventModel model = dataSnapshot.getValue(EventModel.class);


                        data.add(model);


                        trending.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                        nearby.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                        mAdapter = new TrendingEventAdapter(data, getContext());
                        mAdapterEvent = new EventAdapter(data, getContext());
                        trending.setAdapter(mAdapter);
                        nearby.setAdapter(mAdapterEvent);
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

        search = view.findViewById(R.id.searchbtn);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(new Search());
            }
        });

        return view;
    }

    public boolean changeFragment(Fragment target){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, target);
        ft.commit();
        return true;
    }
}
