package ml.coppellcoders.notixbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import ml.coppellcoders.notixbus.R;

import static android.support.constraint.Constraints.TAG;

public class EventsFragment extends Fragment {
    RecyclerView rv;
    DatabaseReference mDatabase;
    ArrayList<Event> events;
    EventAdapter ea;
    ImageView addEvent;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_events, container, false);
        rv = view.findViewById(R.id.events_recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        events = new ArrayList<>();
        ea = new EventAdapter(events, getContext());
        rv.setAdapter(ea);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Events");
        addEvent  = view.findViewById(R.id.add_event);
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddEventActivity.class));
            }
        });
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    events.add(child.getValue(Event.class));
                }
                ea = new EventAdapter(events, getContext());
                rv.setAdapter(ea);
                System.out.println(events);
                System.out.println("Data Changed");
                ea.setListener(new RecyclerViewAdapter.Listener<Event>() {
                    @Override
                    public void onClick(@NonNull Event event) {
                        //startActivity(new Intent(getContext(), AddEventActivity.class));
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        System.out.println(events);
        return view;
    }
}
