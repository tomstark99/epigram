package com.example.epigram;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class Utils {

    public static void hideKeyboard(Activity activity){

        if (activity.getCurrentFocus() == null) return;
        InputMethodManager systemService = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        systemService.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }
}
