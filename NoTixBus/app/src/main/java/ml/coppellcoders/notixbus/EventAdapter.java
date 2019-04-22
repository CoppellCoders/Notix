package ml.coppellcoders.notixbus;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventAdapter extends RecyclerViewAdapter<Event, EventAdapter.ItemItemViewHolder>{


    private Context context;
    List<Event> events;

    public EventAdapter(List<Event> outfit, Context context) {
        super(outfit);
        this.events = outfit;
        this.context = context;
    }



    @Override
    public EventAdapter.ItemItemViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                                  final int viewType) {
        return new ItemItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false));
    }


    public class ItemItemViewHolder extends RecyclerViewAdapter.ViewHolder {
        @NonNull
        private final ImageView imgImageView;
        @NonNull
        private final TextView title;

        @NonNull
        private final TextView date;

        @NonNull
        private final TextView location;

        @NonNull
        private final TextView time;


        public ItemItemViewHolder (@NonNull final View itemView) {
            super(itemView);
            imgImageView = (ImageView) itemView.findViewById(R.id.itemPic);
            title = (TextView) itemView.findViewById(R.id.event_title);
            date = (TextView) itemView.findViewById(R.id.event_date);
            location = (TextView) itemView.findViewById(R.id.event_location);
            time = (TextView) itemView.findViewById(R.id.event_time);
        }

        @Override
        public void bind(int position) {
            super.bind(position);


            final Event Item = get(position);
            if(Item.getImg().startsWith("http")) {
                Picasso.with(context).load(Item.getImg()).fit().centerCrop().into(imgImageView);
            }else{
                imgImageView.setImageBitmap(decodeBase64(Item.getImg()));
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Item.getTime());
            int mMonth = calendar.get(Calendar.MONTH);
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
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
            title.setText(Item.getName());
            date.setText(monthNames[mMonth] + " " + mDay);
            location.setText(Item.getVenue());
            long timeLeft = Item.getTime() - System.currentTimeMillis();
            int minutes = (int) ((timeLeft / (1000 * 60)) % 60);
            int hours = (int) ((timeLeft / (1000 * 60 * 60)));

            time.setText(hours+" hours " + minutes +" minutes");
        }

    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}