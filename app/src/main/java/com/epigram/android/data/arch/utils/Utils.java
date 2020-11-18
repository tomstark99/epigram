package com.epigram.android.data.arch.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class Utils {

    public static void hideKeyboard(Activity activity){

        if (activity.getCurrentFocus() == null) return;
        InputMethodManager systemService = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        systemService.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }

    public static void showKeyboard(View view, Activity activity){
        view.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public static String dateText(DateTime time) {
        DateTime now = new DateTime();
        Period period = new Period(time, now);

        if(time.plusMinutes(2).isAfterNow()) {
            return "just now";
        } else if(time.plusHours(1).isAfterNow()) {
            PeriodFormatter formatter = new PeriodFormatterBuilder()
                    .appendMinutes().appendSuffix(" minutes ago")
                    .printZeroNever()
                    .toFormatter();
            return formatter.print(period);
        } else if(time.plusHours(2).isAfterNow()) {
            PeriodFormatter formatter = new PeriodFormatterBuilder()
                    .appendHours().appendSuffix(" hour ago")
                    .printZeroNever()
                    .toFormatter();
            return formatter.print(period);
        } else if(time.plusDays(1).isAfterNow()) {
            PeriodFormatter formatter = new PeriodFormatterBuilder()
                    .appendHours().appendSuffix(" hours ago")
                    .printZeroNever()
                    .toFormatter();
            return formatter.print(period);
        } else if(time.plusDays(2).isAfterNow() && period.getDays() == 1) {
            PeriodFormatter formatter = new PeriodFormatterBuilder()
                    .appendDays().appendSuffix(" day ago")
                    .printZeroNever()
                    .toFormatter();
            return formatter.print(period);
        } else if(time.plusWeeks(1).isAfterNow()) {
            PeriodFormatter formatter = new PeriodFormatterBuilder()
                    .appendDays().appendSuffix(" days ago")
                    .printZeroNever()
                    .toFormatter();
            return formatter.print(period);
        } else {
            return time.toString("MMM d, yyyy");
        }
    }
}
