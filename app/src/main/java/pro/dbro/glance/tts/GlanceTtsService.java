/*************************************************************************/
/*                                                                       */
/*                  Language Technologies Institute                      */
/*                     Carnegie Mellon University                        */
/*                         Copyright (c) 2010                            */
/*                        All Rights Reserved.                           */
/*                                                                       */
/*  Permission is hereby granted, free of charge, to use and distribute  */
/*  this software and its documentation without restriction, including   */
/*  without limitation the rights to use, copy, modify, merge, publish,  */
/*  distribute, sublicense, and/or sell copies of this work, and to      */
/*  permit persons to whom this work is furnished to do so, subject to   */
/*  the following conditions:                                            */
/*   1. The code must retain the above copyright notice, this list of    */
/*      conditions and the following disclaimer.                         */
/*   2. Any modifications must be clearly marked as such.                */
/*   3. Original authors' names are not deleted.                         */
/*   4. The authors' names are not used to endorse or promote products   */
/*      derived from this software without specific prior written        */
/*      permission.                                                      */
/*                                                                       */
/*  CARNEGIE MELLON UNIVERSITY AND THE CONTRIBUTORS TO THIS WORK         */
/*  DISCLAIM ALL WARRANTIES WITH REGARD TO THIS SOFTWARE, INCLUDING      */
/*  ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS, IN NO EVENT   */
/*  SHALL CARNEGIE MELLON UNIVERSITY NOR THE CONTRIBUTORS BE LIABLE      */
/*  FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES    */
/*  WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN   */
/*  AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION,          */
/*  ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF       */
/*  THIS SOFTWARE.                                                       */
/*                                                                       */
/*************************************************************************/
/*             Author:  Alok Parlikar (aup@cs.cmu.edu)                   */
/*               Date:  June 2012                                        */
/*************************************************************************/

package pro.dbro.glance.tts;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.speech.tts.SynthesisCallback;
import android.speech.tts.SynthesisRequest;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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



//		callback.start(16000,
//		                AudioFormat.ENCODING_PCM_16BIT, 1);

		Handler hd = new Handler(ht.getLooper());

		hd.post(new Runnable() {
			@Override
			public void run() {
				Intent shareIntent = new Intent(Intent.ACTION_SEND, null, getApplicationContext(), CustomActivity.class);
				shareIntent.putExtra(Intent.EXTRA_TEXT, text);
				shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);


				startActivity(shareIntent);

				System.out.println("PLaying: " + text);
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
		System.out.println("Staring"+Thread.currentThread().getId());

		try {
			sem.acquire();
		} catch (InterruptedException e) {
		}

		System.out.println("Done"+Thread.currentThread().getId());
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
			sem.release();
//			mCallback.done();
//			System.out.println("done");
//			mCallback=null;
		}
	}
}
