package rocks.athrow.android_stock_rotation.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import rocks.athrow.android_stock_rotation.R;

/**
 * Utilities
 * Created by jose on 1/10/17.
 */

public final class Utilities {

    private Utilities() {
        throw new AssertionError("No Utilities instances for you!");
    } // suppress constructor


    @SuppressWarnings("SameParameterValue")
    public static void showToast(Context context, String message, int duration) {
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    /**
     * getStringAsDate
     *
     * @param dateString a string in date format
     * @param format     the resulting date format
     * @return a new date in the specified format
     */
    public static Date getStringAsDate(String dateString, @SuppressWarnings("SameParameterValue") String format, @SuppressWarnings("SameParameterValue") String timezone) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        if (timezone == null) {
            formatter.setTimeZone(TimeZone.getDefault());
        } else {
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        Date date = new Date();
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    /**
     * getDateAsString
     * Convert a date into a string
     *
     * @param date   the date
     * @param format the format in which to return the string
     * @return the new formatted date string
     */
    @SuppressWarnings("SameParameterValue")
    public static String getDateAsString(Date date, String format, String timezone) {
        DateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        if (timezone == null) {
            formatter.setTimeZone(TimeZone.getDefault());
        } else {
            formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        }
        return formatter.format(date);
    }

    public static void badgeFormat(TextView view, String status, Context context) {
        switch (status) {
            case "in":
                view.setBackground(ContextCompat.getDrawable(context, R.drawable.badge_in));
                break;
            case "out":
                view.setBackground(ContextCompat.getDrawable(context, R.drawable.badge_out));
                break;
            case "Receive":
                view.setBackground(ContextCompat.getDrawable(context, R.drawable.badge_receive));
                break;
            case "Move":
                view.setBackground(ContextCompat.getDrawable(context, R.drawable.badge_move));
                break;
            case "Adjust":
                view.setBackground(ContextCompat.getDrawable(context, R.drawable.badge_adjust));
                break;
            case "Init":
                view.setBackground(ContextCompat.getDrawable(context, R.drawable.badge_init));
                break;
            case "match":
                view.setBackground(ContextCompat.getDrawable(context, R.drawable.badge_validate_match));
                break;
            case "mismatch":
                view.setBackground(ContextCompat.getDrawable(context, R.drawable.badge_validate_mismatch));
                break;
        }
    }

    /**
     * isConnected
     * This method is used to check for network connectivity before attempting a network call
     *
     * @param context the activity from where the method is called
     * @return true for is connected and false for is not connected
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
