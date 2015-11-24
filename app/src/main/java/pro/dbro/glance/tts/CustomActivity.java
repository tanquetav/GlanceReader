package pro.dbro.glance.tts;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.astuetz.PagerSlidingTabStrip;
import com.squareup.otto.Bus;
import pro.dbro.glance.AppSpritzer;
import pro.dbro.glance.GlancePrefsManager;
import pro.dbro.glance.R;
import pro.dbro.glance.activities.ImmersiveActivityBase;
import pro.dbro.glance.adapters.BookSectionAdapter;
import pro.dbro.glance.fragments.SpritzFragment;
import pro.dbro.glance.fragments.WpmDialogFragment;
import pro.dbro.glance.lib.Spritzer;
import pro.dbro.glance.lib.SpritzerTextView;

//import pro.dbro.glance.SECRETS;

public class CustomActivity extends ImmersiveActivityBase {
    private AppSpritzer mSpritzer;
    private Bus mBus;

    private Menu mMenu;
    private SpritzerTextView spritzerTextView;

    private Spritzer.SpritzerCallback sSpritzerCallback = new Spritzer.SpritzerCallback() {
        @Override
        public void onSpritzerFinished() {
            doFinish();
        }
    };

    private void doFinish() {
        System.out.println("Terminou");
        Intent sendableIntent = new Intent("finishTTS");
  	    LocalBroadcastManager.getInstance(this).
  	    sendBroadcast(sendableIntent);

       finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.custom, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_speed) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            DialogFragment newFragment = WpmDialogFragment.newInstance();
            newFragment.show(ft, "dialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        setContentView(R.layout.activity_custom);


        spritzerTextView = (SpritzerTextView)findViewById(R.id.spritzText);

        // Retain the SpritzFragment instance so it survives screen rotation
//                  SpritzFragment frag = new SpritzFragment();
//                  frag.setRetainInstance(true);
//                  getSupportFragmentManager().beginTransaction()
//                          .replace(R.id.container, frag, "SPT")
//                          .commit();


    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }


    @Override
    public void onResume() {
        super.onResume();
        String action = getIntent().getAction();

        String data = "Este é um teste. Este é um teste. Este é um teste. Este é um teste. Este é um teste. ";
        if (action.equals(Intent.ACTION_SEND)) {
            data = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        }
//
//        if (mSpritzer == null) {
//            mSpritzer = new AppSpritzer(mBus, spritzerTextView);
//            spritzerTextView.setSpritzer(mSpritzer);
//        }
//
//
//        mSpritzer.setTextAndStart(data,sSpritzerCallback,true);
        System.out.println(data);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(2000);
                }
                catch(Exception e) {

                }
                doFinish();
            }
        }).start();
    }


}

