package ml.coppellcoders.notixclient;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Search extends Fragment {

    RecyclerView rv, searchrecy;
    CategoryAdapter adapter;
    List<String> cate;
    ArrayList<EventModel> data;
    private DatabaseReference mFirebaseRef;
    private EventAdapter  mAdapterEvent;
    private ImageButton search;
    private EditText editText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        rv = view.findViewById(R.id.caterecy);
        searchrecy = view.findViewById(R.id.search_recy);
        cate = new ArrayList<>();
        cate.add("All");
        cate.add("Music");
        cate.add("Sports");
        cate.add("Business");
        cate.add("Parties");
        cate.add("Arts");
        cate.add("Charity");
        cate.add("Education");
        cate.add("Community");
        cate.add("Spirituality");
        cate.add("Other");
        data = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new CategoryAdapter(cate, getContext());
        rv.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        mFirebaseRef = database.getReference("Events");
        editText = view.findViewById(R.id.editTextSearch);
        query("");

        search = view.findViewById(R.id.search_btn);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText.getText().toString().equals(""))
                    query(editText.getText().toString());

            }
        });


        adapter.setListener(new RecyclerViewAdapter.Listener<String>() {
            @Override
            public void onClick(@NonNull String s) {
                query(s);
            }
        });

return view;
    }




    private void query(final String params){
        data.clear();
        if(mAdapterEvent!=null)
        mAdapterEvent.clear();

        mFirebaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    try {


                        System.out.println(dataSnapshot.toString());
                        EventModel model = dataSnapshot.getValue(EventModel.class);

                        if(model.getName().toLowerCase().contains(params.toLowerCase())||model.getCategory().toLowerCase().contains(params.toLowerCase())||params.equals("")){
                            data.add(model);
                        }





                        searchrecy.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

                        mAdapterEvent = new EventAdapter(data, getContext());
                        searchrecy.setAdapter(mAdapterEvent);
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

    }

}
