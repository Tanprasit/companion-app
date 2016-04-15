package example.tanprasit.com.companion_app.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import example.tanprasit.com.companion_app.R;
import example.tanprasit.com.companion_app.models.Key;
import example.tanprasit.com.companion_app.models.Property;
import example.tanprasit.com.companion_app.tools.TimeHelper;

/**
 * Created by luketanprasit on 09/04/2016.
 */
public class KeyAdapter extends RecyclerView.Adapter<KeyAdapter.KeyViewHolder> {
    private List<Key> keyList;
    private TimeHelper timeHelper;

    // Provide a suitable constructor (depends on the kind of dataset)
    public KeyAdapter(List<Key> keyList) {
        this.keyList = keyList;
        this.timeHelper = new TimeHelper();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class KeyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private CardView cardView;
        private TextView firstAddressView;
        private TextView secondAddressView;
        private TextView takenDateView;
        private TextView returnedDateView;
        private TextView takenTimeView;
        private TextView returnedTimeView;
        private Button keyButton;

        public KeyViewHolder(View itemView) {
            super(itemView);
            firstAddressView = (TextView) itemView.findViewById(R.id.key_address_line_1_view);
            secondAddressView = (TextView) itemView.findViewById(R.id.key_address_line_2_view);
            takenDateView = (TextView) itemView.findViewById(R.id.key_taken_date);
            returnedDateView = (TextView) itemView.findViewById(R.id.key_returned_date);
            takenTimeView = (TextView) itemView.findViewById(R.id.key_taken_time);
            returnedTimeView = (TextView) itemView.findViewById(R.id.key_returned_time);
            keyButton = (Button) itemView.findViewById(R.id.key_button);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public KeyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.key_card_view, parent, false);
        return new KeyViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(KeyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.firstAddressView.setText(keyList.get(position).getProperty().getAddressLine1());
        holder.secondAddressView.setText(generateSecondAddressLine(keyList.get(position).getProperty()));

        Key key = keyList.get(position);

        this.timeHelper.setDateTime(key.getTakenAt());
        holder.takenDateView.setText(timeHelper.getReadableDateForCard());
        holder.takenTimeView.setText(timeHelper.getReadableTimeForCard());

        this.timeHelper.setDateTime(key.getReturnedAt());
        holder.returnedDateView.setText(timeHelper.getReadableDateForCard());
        holder.returnedTimeView.setText(timeHelper.getReadableTimeForCard());

        holder.keyButton.setText((key.isTaken())
                ? key.getPin() + " / Return Key"
                : "Take Key");
    }

    private String generateSecondAddressLine(Property property) {
        return property.getAddressLine2() + ", "
                + property.getCity() + ", "
                + property.getCounty() + ", "
                + property.getPostcode();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return keyList.size();
    }

}