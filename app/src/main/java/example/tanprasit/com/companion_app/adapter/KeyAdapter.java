package example.tanprasit.com.companion_app.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import example.tanprasit.com.companion_app.Constants;
import example.tanprasit.com.companion_app.R;
import example.tanprasit.com.companion_app.models.Contractor;
import example.tanprasit.com.companion_app.models.Key;
import example.tanprasit.com.companion_app.models.Property;
import example.tanprasit.com.companion_app.networks.RequestTask;
import example.tanprasit.com.companion_app.tools.TimeHelper;
import example.tanprasit.com.companion_app.tools.URLBuilder;

/**
 * Created by luketanprasit on 09/04/2016.
 */
public class KeyAdapter extends RecyclerView.Adapter<KeyAdapter.KeyViewHolder> {
    private List<Key> keyList;
    private Context context;
    private TimeHelper timeHelper;
    private boolean timeTakenUpdated;
    private boolean timeReturnedUpdated;

    // Provide a suitable constructor (depends on the kind of dataset)
    public KeyAdapter(List<Key> keyList) {
        this.keyList = keyList;
        this.timeHelper = new TimeHelper();
    }

    public void swap(List<Key> keyList) {
        this.keyList.clear();
        this.keyList.addAll(keyList);
        notifyDataSetChanged();
    }

    public void clear() {
        this.keyList.clear();
        notifyDataSetChanged();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class KeyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView firstAddressView;
        private TextView secondAddressView;
        private TextView takenDateView;
        private TextView returnedDateView;
        private TextView takenTimeView;
        private TextView returnedTimeView;
        private TextView keyPinView;
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
            keyPinView = (TextView) itemView.findViewById(R.id.key_pin_view);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public KeyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.key_card_view, parent, false);
        this.context = parent.getContext();
        return new KeyViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final KeyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Key key = keyList.get(position);

        holder.firstAddressView.setText(key.getProperty().getAddressLine1());
        holder.secondAddressView.setText(generateSecondAddressLine(key.getProperty()));
        holder.keyPinView.setText(String.valueOf(key.getPin()));

        final boolean keyIsTaken = key.isTaken();
        final boolean keyIsReturned = key.isReturned();

        if (keyIsTaken) {
            this.timeHelper.setDateTime(key.getTakenAt());
            holder.takenDateView.setText(timeHelper.getReadableDateForCard());
            holder.takenTimeView.setText(timeHelper.getReadableTimeForCard());
        } else {
            holder.takenDateView.setText("N/A");
            holder.takenTimeView.setText("");
            holder.keyButton.setEnabled(true);
        }

        if (keyIsReturned) {
            this.timeHelper.setDateTime(key.getReturnedAt());
            holder.returnedDateView.setText(timeHelper.getReadableDateForCard());
            holder.returnedTimeView.setText(timeHelper.getReadableTimeForCard());

            holder.keyButton.setEnabled(false);
        } else {
            holder.returnedDateView.setText("N/A");
            holder.returnedTimeView.setText("");
            holder.keyButton.setEnabled(true);
        }

        holder.keyButton.setText((keyIsTaken)
                ? "Return Key"
                : "Take Key");

        timeHelper.setCurrentTimeFromLong(System.currentTimeMillis());

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String userId = sp.getString(Constants.USER_ID, null);

        final Map<String, String> postParams = new HashMap<>();
        postParams.put("id", String.valueOf(key.getId()));
        postParams.put("contractor_id", userId);

        final String currentDateTime = timeHelper.getApiFormatDateTime();

        // Depending if the key had been taken update either the taken or return time.
        if (!keyIsTaken) {
            postParams.put(Constants.TAKEN_PARAM, currentDateTime);
            this.timeTakenUpdated = true;
        } else {
            postParams.put(Constants.RETURN_PARAM, currentDateTime);
            this.timeReturnedUpdated = true;
        }

        final URLBuilder urlBuilder = new URLBuilder();

        // Set listener for updating key taken and returned times.
        holder.keyButton.setOnClickListener(new View.OnClickListener() {

            // Check if the key had been taken. If so get the return url or get the key taken
            // url.
            String url = (!keyIsTaken)
                    ? urlBuilder.getTimeTakenUrl()
                    : urlBuilder.getTimeReturnedUrl();

            @Override
            public void onClick(View v) {
                new RequestTask(new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Show the success message

                        Contractor contractor = new Gson().fromJson(response, Contractor.class);

                        String message = "No keys to be displayed.";

                        List<Key> keyList = contractor.getKeys();

                        if (keyList.size() > 0) {
                            Collections.reverse(contractor.getKeys());
                            swap(keyList);

                            if (timeTakenUpdated) {
                                message = "Successfully updated taken time.";
                            } else if (timeReturnedUpdated) {
                                message = "Successfully updated return time.";
                            }

                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        } else {
                            // If current user have no keys after refresh clear cards from
                            // recycler view.

                            clear();
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String responseBody = "Failed to retreive key list.";
                        try {
                            responseBody = new String(error.networkResponse.data, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            Log.e("UnsupportedEncoding", e.getMessage());
                        }
                        Toast.makeText(context, responseBody, Toast.LENGTH_SHORT).show();
                    }
                }, context).sendPostRequest(url, postParams);
            }
        });
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