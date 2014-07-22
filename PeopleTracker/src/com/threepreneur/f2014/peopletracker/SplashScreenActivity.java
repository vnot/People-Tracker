package com.threepreneur.f2014.peopletracker;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.threepreneur.lib.JSONParser;

public class SplashScreenActivity extends Activity {
	
	long Delay = 8000;
	public Object jsonResult;
	private Menu menu;
	private String pesan_Informasi;
	private String pesan_Exit;
	private String pesan_GPS_Disabled;
	private String pesan_Ya;
	private String pesan_Tidak;
	private String pesan_GPS_Enabled;
	private String pesan_EROR_Koneksi;
	private String pesan_SUKSES_Koneksi;
	private String pesan_galau_Koneksi;
	private String pesan_no_koneksi;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Remove the Title Bar
				requestWindowFeature(Window.FEATURE_NO_TITLE);
				setContentView(R.layout.activity_splash_screen);
//				CekStatusGPS();
//				CekStatusJaringan();
				// Create a Timer
				Timer RunSplash = new Timer();
				// Task to do when the timer ends
				TimerTask ShowSplash = new TimerTask() {

					@Override
					public void run() {
						// Close SplashScreenActivity.class

						finish();
					}
				};
				// Start the timer
				RunSplash.schedule(ShowSplash, Delay);
				new PrefetchData().execute();
	}

	private class PrefetchData extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// before making http calls
			Log.e("JSON", "Pre execute");


		}

		@Override
		protected Void doInBackground(Void... arg0) {
			/*
			 * Will make http call here This call will download required data
			 * before launching the app example: 1. Downloading and storing
			 * SQLite 2. Downloading images 3. Parsing the xml / json 4. Sending
			 * device information to server 5. etc.,
			 */
			JSONParser request=new JSONParser();
			jsonResult = request.getRequest("http://people.geotracker.cu.cc/android_login_api/");
//			jsonResult = getRequest("http://vnot.pusku.com/mymed/daftarobat.php");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// After completing http call
			// will close this activity and lauch main activity
//			CekStatusGPS();
//			CekStatusJaringan();
			Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
			startActivity(i);

			// close this activity
			finish();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.menu = menu;
		pesan_Informasi=getResources().getString(R.string.menu_Informasi);
		pesan_Exit=getResources().getString(R.string.menu_Exit);
		menu.add(0, 1, 0, pesan_Informasi).setIcon(
				R.drawable.ic_ppltracker);
		menu.add(0, 2, 0, pesan_Exit).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			Intent j = new Intent(this, AboutActivity.class);
			startActivity(j);
			return true;
		case 2:
			finish();
			// LoadingBiasa();
			return true;
		}
		return false;
	}
	
	public void CekStatusGPS() {
		final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			pesan_GPS_Disabled=getResources().getString(R.string.menu_GPS_Disabled);
			pesan_Ya=getResources().getString(R.string.menu_Ya);
			pesan_Tidak=getResources().getString(R.string.menu_Tidak);
			builder.setMessage(pesan_GPS_Disabled)
					.setCancelable(false)
					.setPositiveButton(pesan_Ya,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										final DialogInterface dialog,
										final int id) {
									startActivity(new Intent(
											Settings.ACTION_LOCATION_SOURCE_SETTINGS));
								}
							})
					.setNegativeButton(pesan_Tidak,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										final DialogInterface dialog,
										final int id) {
									dialog.cancel();
								}
							});
			final AlertDialog alert = builder.create();
			alert.show();
		} else {
			pesan_GPS_Enabled=getResources().getString(R.string.menu_GPS_Enabled);
			Toast.makeText(getApplicationContext(), pesan_GPS_Enabled,
					Toast.LENGTH_LONG).show();
		}
	}
	
	public boolean isConnectingToNetwork() {
		ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
		}
		return false;
	}

	public boolean hasActiveInternetConnection() {
		try {
			HttpURLConnection urlc = (HttpURLConnection) (new URL(
					"http://www.google.com").openConnection());
			urlc.setRequestProperty("User-Agent", "Test");
			urlc.setRequestProperty("Connection", "close");
			urlc.setConnectTimeout(1500);
			urlc.connect();
			return (urlc.getResponseCode() == 200);
		} catch (IOException e) {
			pesan_EROR_Koneksi=getResources().getString(R.string.txt_eror_cek_koneksi);
			Toast.makeText(getApplicationContext(),pesan_EROR_Koneksi, Toast.LENGTH_LONG).show();
		}
		return false;
	}
	
	public void CekStatusJaringan() {
		if (isConnectingToNetwork()) {
			if (hasActiveInternetConnection()) {
				pesan_SUKSES_Koneksi=getResources().getString(R.string.txt_sukses_cek_koneksi);
				Toast.makeText(getApplicationContext(),pesan_SUKSES_Koneksi, Toast.LENGTH_LONG)
						.show();
//				JSONParser request=new JSONParser();
//				jsonResult = request.getRequest("http://people.geotracker.cu.cc/android_login_api/");
			} else {
//				Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG)
//						.show();
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						this);
				pesan_galau_Koneksi=getResources().getString(R.string.txt_galau_cek_koneksi);
				pesan_Ya=getResources().getString(R.string.menu_Ya);
				pesan_Tidak=getResources().getString(R.string.menu_Tidak);
				builder.setMessage(pesan_galau_Koneksi)
						.setCancelable(false)
						.setPositiveButton(pesan_Ya,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(
											final DialogInterface dialog,
											final int id) {
										startActivity(new Intent(
												Settings.ACTION_WIRELESS_SETTINGS));
										finish();
									}
								})
						.setNegativeButton(pesan_Tidak,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(
											final DialogInterface dialog,
											final int id) {
										dialog.cancel();
										finish();
									}
								});
				final AlertDialog alert = builder.create();
				alert.show();
			}
		} else {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			pesan_no_koneksi=getResources().getString(R.string.txt_no_koneksi);
			pesan_Ya=getResources().getString(R.string.menu_Ya);
			pesan_Tidak=getResources().getString(R.string.menu_Tidak);
			builder.setMessage(pesan_no_koneksi)
					.setCancelable(false)
					.setPositiveButton(pesan_Ya,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										final DialogInterface dialog,
										final int id) {
									startActivity(new Intent(
											Settings.ACTION_WIRELESS_SETTINGS));
									finish();
								}
							})
					.setNegativeButton(pesan_Tidak,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										final DialogInterface dialog,
										final int id) {
									dialog.cancel();
									finish();
								}
							});
			final AlertDialog alert = builder.create();
			alert.show();
		}
	}
}
