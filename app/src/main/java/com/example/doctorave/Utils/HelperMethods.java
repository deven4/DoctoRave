package com.example.doctorave.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.doctorave.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelperMethods {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static void HideKeyboard(Activity activity) {

        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static void showToastInCenter(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    public static void showToastInCenter(Context context) {
        Toast toast = Toast.makeText(context, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    public static void setUpViewPagerIndicator(Context context, ViewPager viewPager,
                                               LinearLayout indicatorLayout, int imagesCount) {

        ImageView[] dots = new ImageView[imagesCount];
        indicatorLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(context);
            dots[i].setImageResource(R.drawable.circle_indicator);
            dots[i].setPadding(0, 0, 6, 0);
            indicatorLayout.addView(dots[i]);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                for (ImageView imageView : dots) {
                    imageView.setImageResource(R.drawable.circle_indicator);
                    imageView.clearColorFilter();
                }
                if (position < dots.length)
                    dots[position].setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    public static boolean validateEmailId(String email){

        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }
}

