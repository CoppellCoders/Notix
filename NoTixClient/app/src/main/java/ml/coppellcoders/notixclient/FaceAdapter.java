package ml.coppellcoders.notixclient;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

public class FaceAdapter extends RecyclerViewAdapter<FaceModel, FaceAdapter.ItemItemViewHolder> implements Serializable {


    private Context context;
    List<FaceModel> events;

    public FaceAdapter(List<FaceModel> events, Context context) {
        super(events);
        this.events = events;
        this.context = context;
    }



    @Override
    public FaceAdapter.ItemItemViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                                 final int viewType) {
        return new ItemItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_face, parent, false));
    }


    public class ItemItemViewHolder extends RecyclerViewAdapter.ViewHolder {


        TextView title;
        ImageView img;
        FrameLayout btn;



        public ItemItemViewHolder (@NonNull final View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.face_title);
            img = itemView.findViewById(R.id.face_img);
            btn = itemView.findViewById(R.id.removeface_btn);

        }

        @Override
        public void bind(final int position) {
            super.bind(position);
            final FaceModel events = get(position);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context,"Clicked " + events.getName(),Toast.LENGTH_SHORT);

                }
            });
            if(events.getUrl().contains("http"))
                Picasso.with(context).load(events.getUrl()).fit().centerCrop().into(img);
            else
                img.setImageBitmap(decodeBase64(events.getUrl()));


            title.setText(events.getName());


        }
    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}