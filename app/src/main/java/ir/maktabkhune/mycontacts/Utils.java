package ir.maktabkhune.mycontacts;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Calendar;
import java.util.TimeZone;

public class Utils {
    public static String name = "name";

    public static String firstName = "firstName";
    public static String lastName = "lastName";
    public static String imageId = "imageId";
    public static String phoneNumber = "phoneNumber";
    public static String birthDateEntered = "birthDateEntered";
    public static String birthDateMilliSec = "birthDateMilliSec";

    public static String numbersList = "numbersList";
    public static String totalNumbers = "totalNumbers";
    public static String hasBirthDate = "hasBirthDate";
    public static String hasPhoneNo = "hasPhoneNo";
    public static String EmptyText = "";

    public static String getBirthDateString(long dateMilliSec) {
        Calendar calendar1 = Calendar.getInstance(TimeZone.getTimeZone("iran"));
        calendar1.setTimeInMillis(dateMilliSec);
        return calendar1.get(Calendar.YEAR) + "/" + (calendar1.get(Calendar.MONTH) + 1)
                + "/" + calendar1.get(Calendar.DAY_OF_MONTH);
    }

    public static void exitSoftKeyboard(Activity activity) {
        View focusedView = activity.getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

    public static int[] getImageIdByIndex = {
            R.drawable.avatar1,
            R.drawable.avatar2,
            R.drawable.avatar3,
            R.drawable.avatar4,
            R.drawable.avatar5,
            R.drawable.avatar6,
            R.drawable.avatar7,
            R.drawable.avatar8
    };

    public static String[] NAME_OF_DAYS = {
            "",

            "Sundays",

            "Mondays",

            "Tuesdays",

            "Wednesdays",

            "Thursdays",

            "Fridays",

            "Saturdays"
    };
    public static String dontPhoneNumber = "don't have phoneNumber";
}
