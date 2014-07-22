package com.threepreneur.f2014.peopletracker;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.threepreneur.lib.DatabaseHandler;
import com.threepreneur.lib.UserFunctions;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class LoginActivity extends Activity {

	// JSON Response node names
	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	private static String KEY_ERROR_MSG = "error_msg";
	private static String KEY_UID = "uid";
	private static String KEY_NAME = "name";
	private static String KEY_PHONE = "no_telp";
	private static String KEY_CREATED_AT = "created_at";
	private EditText loginPhone;
	private EditText inputPassword;
	private Button btnLogin;
//	private Button btnLinkToRegister;
	private TextView loginErrorMsg;
	private Menu menu;
	public ProgressDialog pDialog;
	JSONObject json;
	UserFunctions userFunction;
	JSONObject json_user;
	private String pesan_Eror_cek_koneksi;
	private String pesan_sukses_cek_koneksi;
	private String pesan_galau_cek_koneksi;
	private String pesan_Ya;
	private String pesan_Tidak;
	private String pesan_no_koneksi;
	private String pesan_GPS_Disabled;
	private String pesan_GPS_Enabled;
	private String pesan_Informasi;
	private String pesan_Exit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
//		CekStatusGPS();
//		CekStatusJaringan();
		// Importing all assets like buttons, text fields
		loginPhone = (EditText) findViewById(R.id.loginPhone);
		inputPassword = (EditText) findViewById(R.id.loginPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
//		btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
		loginErrorMsg = (TextView) findViewById(R.id.login_error);

		// Login button Click Event
		btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				/*String phone = loginPhone.getText().toString();
				String password = inputPassword.getText().toString();
				userFunction = new UserFunctions();
				Log.d("Button", "Login");
				Log.wtf("Button", "Login");
				json = userFunction.loginUser(phone, password);*/

				new PostLogin().execute();
				/*// check for login response
				try {
					if (json.getString(KEY_SUCCESS) != null) {
						loginErrorMsg.setText("");
						String res = json.getString(KEY_SUCCESS);
						if (Integer.parseInt(res) == 1) {
							// user successfully logged in
							// Store user details in SQLite Database
							DatabaseHandler db = new DatabaseHandler(
									getApplicationContext());
							JSONObject json_user = json.getJSONObject("user");

							// Clear all previous data in database
							userFunction.logoutUser(getApplicationContext());
							db.addUser(json_user.getString(KEY_NAME),
									json_user.getString(KEY_PHONE),
									json.getString(KEY_UID),
									json_user.getString(KEY_CREATED_AT));

							// Launch Dashboard Screen
							Intent dashboard = new Intent(
									getApplicationContext(), MainActivity.class);

							// Close all views before launching Dashboard
							dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							dashboard.putExtra("no_hp",
									json_user.getString(KEY_PHONE));
							startActivity(dashboard);
							Log.wtf("NO HP PEMONITOR",
									json_user.getString(KEY_PHONE));
							// Close Login Screen
							finish();
						} else {
							// Error in login
							loginErrorMsg
									.setText("Incorrect username/password");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}*/
			}
		});

		/*// Link to Register Screen
		btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(),
						RegisterActivity.class);
				startActivity(i);
				finish();
			}
		});*/
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
			pesan_Eror_cek_koneksi=getResources().getString(R.string.txt_eror_cek_koneksi);
			Toast.makeText(getApplicationContext(),pesan_Eror_cek_koneksi, Toast.LENGTH_LONG).show();
		}
		return false;
	}
	
	public void CekStatusJaringan() {
		if (isConnectingToNetwork()) {
			if (hasActiveInternetConnection()) {
				pesan_sukses_cek_koneksi=getResources().getString(R.string.txt_sukses_cek_koneksi);
				Toast.makeText(getApplicationContext(),pesan_sukses_cek_koneksi, Toast.LENGTH_LONG)
						.show();
				
				/*btnLogin.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						String phone = loginPhone.getText().toString();
						String password = inputPassword.getText().toString();
						UserFunctions userFunction = new UserFunctions();
						Log.d("Button", "Login");
						Log.wtf("Button", "Login");
						JSONObject json = userFunction.loginUser(phone, password);

						// check for login response
						try {
							if (json.getString(KEY_SUCCESS) != null) {
								loginErrorMsg.setText("");
								String res = json.getString(KEY_SUCCESS);
								if (Integer.parseInt(res) == 1) {
									// user successfully logged in
									// Store user details in SQLite Database
									DatabaseHandler db = new DatabaseHandler(
											getApplicationContext());
									JSONObject json_user = json.getJSONObject("user");

									// Clear all previous data in database
									userFunction.logoutUser(getApplicationContext());
									db.addUser(json_user.getString(KEY_NAME),
											json_user.getString(KEY_PHONE),
											json.getString(KEY_UID),
											json_user.getString(KEY_CREATED_AT));

									// Launch Dashboard Screen
									Intent dashboard = new Intent(
											getApplicationContext(), MainActivity.class);

									// Close all views before launching Dashboard
									dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									dashboard.putExtra("no_hp",
											json_user.getString(KEY_PHONE));
									startActivity(dashboard);
									Log.wtf("NO HP PEMONITOR",
											json_user.getString(KEY_PHONE));
									// Close Login Screen
									finish();
								} else {
									// Error in login
									loginErrorMsg
											.setText("Incorrect username/password");
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});*/
				
			} else {
				pesan_galau_cek_koneksi=getResources().getString(R.string.txt_galau_cek_koneksi);
				pesan_Ya=getResources().getString(R.string.menu_Ya);
				pesan_Tidak=getResources().getString(R.string.menu_Tidak);
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						this);
				builder.setMessage(pesan_galau_cek_koneksi)
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
			pesan_no_koneksi=getResources().getString(R.string.txt_no_koneksi);
			pesan_Ya=getResources().getString(R.string.menu_Ya);
			pesan_Tidak=getResources().getString(R.string.menu_Tidak);
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

	public void CekStatusGPS() {
		final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			pesan_GPS_Disabled=getResources().getString(R.string.menu_GPS_Disabled);
			pesan_Ya=getResources().getString(R.string.menu_Ya);
			pesan_Tidak=getResources().getString(R.string.menu_Tidak);
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
			Toast.makeText(getApplicationContext(),pesan_GPS_Enabled,
					Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * Async task class to get json by making HTTP call
	 * */
	private class PostLogin extends AsyncTask<Void, Void, Void> {

		private String pesan;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(LoginActivity.this);
			pesan=getResources().getString(R.string.please_wait);
			pDialog.setMessage(pesan);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// check for login response
			try {
				String phone = loginPhone.getText().toString();
				String password = inputPassword.getText().toString();
				userFunction = new UserFunctions();
//				Log.d("Button", "Login");
//				Log.wtf("Button", "Login");
				json = userFunction.loginUser(phone, password);
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			try {
				if (json.getString(KEY_SUCCESS) != null) {
					loginErrorMsg.setText("");
					String res = json.getString(KEY_SUCCESS);
					if (Integer.parseInt(res) == 1) {
						// user successfully logged in
						// Store user details in SQLite Database
						DatabaseHandler db = new DatabaseHandler(
								getApplicationContext());
						json_user = json.getJSONObject("user");

						// Clear all previous data in database
						userFunction.logoutUser(getApplicationContext());
						db.addUser(json_user.getString(KEY_NAME),
								json_user.getString(KEY_PHONE),
								json.getString(KEY_UID),
								json_user.getString(KEY_CREATED_AT));

						// Launch Dashboard Screen
						Intent dashboard = new Intent(
								getApplicationContext(), MainActivity.class);

						// Close all views before launching Dashboard
						dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						dashboard.putExtra("no_hp",
								json_user.getString(KEY_PHONE));
						startActivity(dashboard);
//						Log.wtf("NO HP PEMONITOR",
//								json_user.getString(KEY_PHONE));
						// Close Login Screen
						finish();
					} else {
						// Error in login
//						Intent login = new Intent(
//								getApplicationContext(), LoginActivity.class);
						pesan=getResources().getString(R.string.password_incorrect);

						loginErrorMsg.setText(pesan);
						pDialog.dismiss();
//						startActivity(login);
//						finish();
					}
				}
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}			

		}

	}
	
	
	void showToast(CharSequence msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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
}
