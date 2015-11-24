package pro.dbro.glance.tts;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import pro.dbro.glance.AppSpritzer;
import pro.dbro.glance.GlancePrefsManager;
import pro.dbro.glance.R;
import pro.dbro.glance.lib.SpritzerTextView;

/**
 * Created by george on 11/24/15.
 */
public class Util {

    public static View generateView(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        SpritzerTextView  spritzerTextView = (SpritzerTextView) inflater.inflate(R.layout.comp,null);

//        SpritzerTextView spritzerTextView = new SpritzerTextView(context);
        AppSpritzer mSpritzer = new AppSpritzer(null, spritzerTextView);
        spritzerTextView.setSpritzer(mSpritzer);

        int mWpm = GlancePrefsManager.getWpm(context);
        System.out.println(mWpm );

        mSpritzer.setWpm(mWpm );

//				spritzerTextView = new TextView(GlanceTtsService.this);
//        spritzerTextView.setBackgroundColor(0xFF000000);
//        spritzerTextView.setTextColor(0xFFFFFFFF);
//        spritzerTextView.setTextSize(30, TypedValue.COMPLEX_UNIT_SP);
//        spritzerTextView.setTypeface(Typeface.MONOSPACE);
        spritzerTextView.setText("Texto");
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;
  //      params.x = 0;
  //      params.y = 100;

        windowManager.addView(spritzerTextView, params);
        return spritzerTextView;
    }

}
