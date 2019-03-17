package ml.coppellcoders.notixclient;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

public class EventAdapter extends RecyclerViewAdapter<EventModel, EventAdapter.ItemItemViewHolder>{


    private Context context;
    List<EventModel> events;

    public EventAdapter(List<EventModel> events, Context context) {
        super(events);
        this.events = events;
        this.context = context;
    }



    @Override
    public EventAdapter.ItemItemViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
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
            final EventModel events = get(position);

            Picasso.with(context).load(events.getImg()).fit().centerCrop().into(img);
            System.out.println(events.getImg());
            title.setText(events.getName());

            venue.setText(events.getVenue());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(events.getDate());


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
            date.setText(monthNames[mMonth] + " " + mDay);
            if(events.getPrice()>0){
                price.setText(String.format("$%.2f",events.getPrice()));
            }else{
                price.setText("Free");
            }
        }
    }
}