
package pro.dbro.glance.tts;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.HandlerThread;
import android.speech.tts.SynthesisCallback;
import android.speech.tts.SynthesisRequest;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import pro.dbro.glance.AppSpritzer;
import pro.dbro.glance.lib.Spritzer;
import pro.dbro.glance.lib.SpritzerTextView;

import java.util.concurrent.Semaphore;

/**
 * Implements the Flite Engine as a TextToSpeechService
 *
 */

@TargetApi(14)
public class GlanceTtsService extends TextToSpeechService {
	private final static String LOG_TAG = "Flite_Java_" + GlanceTtsService.class.getSimpleName();

	private static final String DEFAULT_LANGUAGE = "eng";
	private static final String DEFAULT_COUNTRY = "";
	private static final String DEFAULT_VARIANT = "";

	private String mCountry = DEFAULT_COUNTRY;
	private String mLanguage = DEFAULT_LANGUAGE;
	private String mVariant = DEFAULT_VARIANT;
	private Semaphore sem = new Semaphore(0);
	private Object mAvailableVoices;
	private SynthesisCallback mCallback;

	private HandlerThread ht = new HandlerThread("HELPER");

	private Spritzer.SpritzerCallback mSplitzerCallback = new Spritzer.SpritzerCallback() {
     @Override
     public void onSpritzerFinished() {
         finish();
     }
 };
	private SpritzerTextView  spritzerTextView;
	private WindowManager windowManager;

	@Override
	public void onCreate() {
		initializeFliteEngine();

		ht.start();

		// This calls onIsLanguageAvailable() and must run after Initialization
		super.onCreate();

		IntentFilter receiveFilter = new IntentFilter("finishTTS");

		LocalBroadcastManager.getInstance(this).
				registerReceiver(mBroadcastReceiver, receiveFilter);

	}

	private void initializeFliteEngine() {
	}

	@Override
	protected String[] onGetLanguage() {
		Log.v(LOG_TAG, "onGetLanguage");
		return new String[] {
				mLanguage, mCountry, mVariant
		};
	}

	@Override
	protected int onIsLanguageAvailable(String language, String country, String variant) {
		Log.v(LOG_TAG, "onIsLanguageAvailable"+language+country+variant);
		return TextToSpeech.LANG_AVAILABLE;//mEngine.isLanguageAvailable(language, country, variant);
	}

	@Override
	protected int onLoadLanguage(String language, String country, String variant) {
		Log.v(LOG_TAG, "onLoadLanguage"+language+country+variant);
		return TextToSpeech.LANG_AVAILABLE;//mEngine.isLanguageAvailable(language, country, variant);
	}

	@Override
	protected void onStop() {

		Log.v(LOG_TAG, "onStop");
	}

	@Override
	protected synchronized void onSynthesizeText(
			SynthesisRequest request, SynthesisCallback callback) {
		Log.v(LOG_TAG, "onSynthesize");

		String language = request.getLanguage();
		String country = request.getCountry();
		String variant = request.getVariant();
		final String text = request.getText();
		Integer speechrate = request.getSpeechRate();

		Log.v(LOG_TAG, text);

		boolean result = true;

		if (! ((mLanguage == language) &&
				(mCountry == country) &&
				(mVariant == variant ))) {
			//result = mEngine.setLanguage(language, country, variant);
			mLanguage = language;
			mCountry = country;
			mVariant = variant;
		}

		if (!result) {
			Log.e(LOG_TAG, "Could not set language for synthesis");
			return;
		}

//		mEngine.setSpeechRate(speechrate);

		mCallback = callback;



//		callback.start(16000,                   				new ImageView(this);

//		                AudioFormat.ENCODING_PCM_16BIT, 1);

		Handler hd = new Handler(ht.getLooper());

		hd.post(new Runnable() {
			@Override
			public void run() {
//				Intent shareIntent = new Intent(Intent.ACTION_SEND, null, getApplicationContext(), CustomActivity.class);
//				shareIntent.putExtra(Intent.EXTRA_TEXT, text);
//				shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);


				//		startActivity(shareIntent);


				windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

				spritzerTextView = (SpritzerTextView) Util.generateView(GlanceTtsService.this);
				spritzerTextView.getSpritzer().setTextAndStart(text, mSplitzerCallback, true);


/*
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(2400);

						} catch (Exception e) {

						}
						finish();
					}
				}).start();
*/
//				try {
//					Thread.sleep(1400);
//				} catch (InterruptedException e) {
//				}
//
//				finish();

			}
		});

//        Integer rate = new Integer(mEngine.getSampleRate());
//        Log.e(LOG_TAG, rate.toString());
//		mCallback.start(16000, AudioFormat.ENCODING_PCM_16BIT, 1);
//		mEngine.synthesize(text);
		sem = new Semaphore(0);

		try {
			sem.acquire();
		} catch (InterruptedException e) {
		}

		mCallback.done();
	}

	     /*
	     +        Intent sendableIntent = new Intent("hehe");
	     +        LocalBroadcastManager.getInstance(this).
	     +                sendBroadcast(sendableIntent);
	     +
+        <item name="android:windowBackground">@android:color/transparent</item>

	      */

	/**
	 * Listens for language update broadcasts and initializes the flite engine.
	 */
	private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Handler hd = new Handler(ht.getLooper());
			hd.post(new Runnable() {
				@Override
				public void run() {
					finish();
				}
			});
		}
	};

	private void finish() {
		if ( mCallback != null) {
			windowManager.removeView(spritzerTextView);
			sem.release();
//			mCallback.done();
//			System.out.println("done");
//			mCallback=null;
		}
	}
}
