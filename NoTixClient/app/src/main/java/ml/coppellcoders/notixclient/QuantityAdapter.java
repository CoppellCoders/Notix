package ml.coppellcoders.notixclient;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

public class QuantityAdapter extends RecyclerViewAdapter<Integer, QuantityAdapter.ItemItemViewHolder> implements Serializable {


    private Context context;
    List<Integer> events;

    public QuantityAdapter(List<Integer> events, Context context) {
        super(events);
        this.events = events;
        this.context = context;
    }



    @Override
    public QuantityAdapter.ItemItemViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                                 final int viewType) {
        return new ItemItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_quan, parent, false));
    }


    public class ItemItemViewHolder extends RecyclerViewAdapter.ViewHolder {


        TextView title;



        public ItemItemViewHolder (@NonNull final View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.quan_title);

        }

        @Override
        public void bind(int position) {
            super.bind(position);



            title.setText(String.valueOf(position+1));


        }
    }
}