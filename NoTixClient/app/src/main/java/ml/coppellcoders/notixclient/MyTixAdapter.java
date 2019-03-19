package ml.coppellcoders.notixclient;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyTixAdapter extends RecyclerViewAdapter<BuyInfoModel, MyTixAdapter.ItemItemViewHolder>{


    private Context context;
    List<BuyInfoModel> events;

    public MyTixAdapter(List<BuyInfoModel> events, Context context) {
        super(events);
        this.events = events;
        this.context = context;
    }



    @Override
    public MyTixAdapter.ItemItemViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                              final int viewType) {
        return new ItemItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_event, parent, false));
    }


    public class ItemItemViewHolder extends RecyclerViewAdapter.ViewHolder {

        ImageView img;
        TextView title,date, venue,price;



        public ItemItemViewHolder (@NonNull final View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.event_img);
            title = itemView.findViewById(R.id.event_title);
            date = itemView.findViewById(R.id.event_date);
            venue = itemView.findViewById(R.id.event_venue);
            price = itemView.findViewById(R.id.event_price);
        }

        @Override
        public void bind(int position) {
            super.bind(position);
            final BuyInfoModel events = get(position);

            if(events.getImg().contains("http"))
                Picasso.with(context).load(events.getImg()).fit().centerCrop().into(img);
            else
                img.setImageBitmap(decodeBase64(events.getImg()));
            System.out.println(events.getImg());
            title.setText(events.getName());

            venue.setText(events.getVenue());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(events.getTime());


            int mMonth = calendar.get(Calendar.MONTH);
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            String[] monthNames = new String[12]; // and populate...
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
            final long millis = events.getTime() - System.currentTimeMillis();
            int hours = (int) (TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.HOURS.toHours(TimeUnit.MILLISECONDS.toHours(millis)));
            int mins = (int) (TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)));

            date.setText(monthNames[mMonth] + " " + mDay);

            price.setText(hours + " hours " + mins +" mins");

        }
    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}