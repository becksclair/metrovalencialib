package com.heliasar.tools;

import com.heliasar.metrovalencialib.Config;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class Utils {

	static final String TAG = "AC - LOG";

	private Context context;
	private int duration;
	private Toast toast;
	
	public static boolean isAboveHoneycomb() {
		if (Build.VERSION.SDK_INT < 11)
			return false;
		else
			return true;
	}

	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	public Utils() {
		this.duration = Toast.LENGTH_LONG;
	}

	public Utils(Context cont) {
		this.context = cont;
		this.duration = Toast.LENGTH_LONG;
	}

	public void showToast(CharSequence text) {
		this.toast = Toast.makeText(this.context, text, this.duration);
		toast.show();
	}

	public void logt(CharSequence text) {
		this.toast = Toast.makeText(this.context, text, this.duration);
		toast.show();
	}

	public static void l(String text) {
		if (Config.LOGGING || Config.DEBUG) {
			Log.d(TAG, text);
		}
	}
	
	public static void loadAds(AdView adView) {
		class AdThread implements Runnable {
			private AdView v;
			
			public AdThread(AdView view) {
				v = view;
			}

			public void run() {
				AdRequest adRequest = new AdRequest();
				adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
				adRequest.addTestDevice("F2F0DF1E4835F0434F127A51EC9F93EE");
				v.loadAd(adRequest);
			}
		}
		
		new Handler().postDelayed(new AdThread(adView), 1000);
	}
}
