package dev.pattabiraman.utils.adapter;

import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import dev.pattabiraman.utils.AppHelperMethods;

public class SearchableAdapter extends ArrayAdapter implements Filterable {

    private final String LOG_TAG = "Google Places Autocomplete";
    private String API_KEY = "";
    private ArrayList<String> resultList;
    AppCompatActivity activity;

    public SearchableAdapter(final AppCompatActivity context, final int resId, final String API_KEY) {
        super(context, resId);
        this.activity = context;
        this.API_KEY = API_KEY;
    }

    @Override
    public int getCount() {
        if (resultList != null) {
            return resultList.size();
        } else {
            return 0;
        }
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    private ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;
        ArrayList<String> descriptionList = null;
        if (input.trim().length() > 2) {
            HttpURLConnection conn = null;
            StringBuilder jsonResults = new StringBuilder();
            String OUT_JSON = "/json";
            String TYPE_AUTOCOMPLETE = "/autocomplete";
            String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE
                    + OUT_JSON);
            sb.append("?key=").append(API_KEY);
            sb.append("&components=country:in");
            try {
                sb.append("&input=").append(URLEncoder.encode(input, "utf8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                URL url = new URL(sb.toString());
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
            } catch (IOException e) {
                return resultList;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            try {
                JSONObject jsonObj = new JSONObject(jsonResults.toString());
                JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
                resultList = new ArrayList(predsJsonArray.length());
                descriptionList = new ArrayList(predsJsonArray.length());
                if (jsonObj.has("error_message")) {
                    if (jsonObj.has("status")) {
                        if (jsonObj.opt("status").toString().equalsIgnoreCase("OVER_QUERY_LIMIT")) {
             /* AppUtils.getInstance().showToast(getContext(), AppConstant.TOAST_TYPE_INFO,
                  jsonObj.opt("status").toString());*/
                        }
                    }
                }
                for (int i = 0; i < predsJsonArray.length(); i++) {
                    resultList.add(predsJsonArray.optJSONObject(i).toString());
                    descriptionList.add(predsJsonArray.optJSONObject(i).getString(
                            "description"));
                }
            } catch (JSONException e) {
                AppHelperMethods.getInstance(activity).traceLog(LOG_TAG + "\n" + sb.toString(), e.getMessage());
            }
        }
        return descriptionList;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    resultList = autocomplete(constraint.toString());
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }
}