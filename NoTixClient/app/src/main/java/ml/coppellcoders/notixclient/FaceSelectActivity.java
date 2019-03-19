package ml.coppellcoders.notixclient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class FaceSelectActivity extends AppCompatActivity {

    RecyclerView faces;
    private DatabaseReference mFirebaseRef;
    private FaceAdapter  mAdapterEvent;
    ArrayList<FaceModel> data;
    boolean imgSelct = false;
    EditText name;
    Button btn;
    ImageView add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_select);
        faces = findViewById(R.id.facerecy);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        data= new ArrayList<>();
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

        mFirebaseRef = database.getReference("Users").child(currentFirebaseUser.getUid()).child("Faces");
        Intent i = getIntent();

        final EventModel event = (EventModel)i.getSerializableExtra("event");
        final int quant = i.getIntExtra("num",1);
        name = findViewById(R.id.faceditext);
        btn = findViewById(R.id.addface);
        add = findViewById(R.id.faceimg);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(116,156)
                        .start(FaceSelectActivity.this);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!name.getText().toString().isEmpty()&&imgSelct){
                    BitmapDrawable drawable = (BitmapDrawable) add.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    mFirebaseRef.push().setValue(new FaceModel(name.getText().toString(),encoded));
                    name.getText().clear();
                    add.setImageDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.default_face) );

                }else{
                    Snackbar.make(findViewById(R.id.facesnack), "Image or name not inputted", Snackbar.LENGTH_SHORT).show();

                }
            }
        });





        mFirebaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    try {

                        System.out.println(dataSnapshot.toString());
                        FaceModel model = dataSnapshot.getValue(FaceModel.class);


                        data.add(model);


                        faces.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));


                        mAdapterEvent = new FaceAdapter(data, getApplicationContext());

                        faces.setAdapter(mAdapterEvent);
                        mAdapterEvent.setListener(new RecyclerViewAdapter.Listener<FaceModel>() {
                            @Override
                            public void onClick(@NonNull FaceModel faceModel) {
                                Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);
                                intent.putExtra("event", event);
                                intent.putExtra("num", quant);
                                intent.putExtra("name", faceModel.getName());
                                intent.putExtra("img",faceModel.getUrl());
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
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                Picasso.with(getApplicationContext()).load(resultUri).fit().centerCrop().into(add);
                imgSelct = true;



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}
