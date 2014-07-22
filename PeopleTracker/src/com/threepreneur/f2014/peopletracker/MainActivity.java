package com.threepreneur.f2014.peopletracker;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.threepreneur.lib.JSONParser;
import com.threepreneur.lib.UserFunctions;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MainActivity extends FragmentActivity {

	/* ========================================= */
	final int RQS_GooglePlayServices = 1;
	private GoogleMap myMap;
	private JSONObject jObject;
	private String jsonResult = "";
	private Hashtable<String, String> markers;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private Marker marker;
	/* ========================================= */
	
	private UserFunctions userFunctions;
	private Button btnLogout;
	InputStream is = null;
	String result = null;
	String line = null;

	// String[] roll_no, name;
	String[] nama_pemonitor, hp_pemonitor, imei_pemonitor, nama_pengguna,
			hp_pengguna, imei_pengguna;
	Spinner spinner1;
//	String URL = "http://10.0.2.2/android_login_api/spinner/spinner1.php";
//	String URL = "http://192.168.43.69/android_login_api/spinner/spinner.php";
//	String URL2 = "http://192.168.43.69/android_login_api/spinner/lokasi.php";
	String URL = "http://people.geotracker.cu.cc/android_login_api/spinner/spinner.php";
	String URL2 = "http://people.geotracker.cu.cc/android_login_api/spinner/lokasi.php";
	
	private String no_hp;
	private double lat;
	private double lon;
	private String id;
	private String nama;
	private String hp;
	private String imei;
	private String foto_pengguna;
	private String update_on;
	public JSONObject jsonobject;
	public ArrayList<SpinnerPopulation> pengguna;
	public ArrayList<String> penggunalist;
	public JSONArray jsonarray;
	public String GetImei_Spinner;
	private String link;
	private String foto;
	public Bitmap bm;
	private Menu menu;
	private int mapType = GoogleMap.MAP_TYPE_NORMAL;
	private String pesan_Ubah_Tipe_Map;
	private String pesan_Informasi;
	private String pesan_Logout;
	private String pesan_Exit;
	private String pesan_snippet_no_hp;
	private String pesan_snippet_tersimpan;
	private String pesan_gagal_json;
	protected String pesan_menu_Dialog_lokasi;
	protected String pesan_menu_Tittle_lokasi;
	protected String pesan_gagal_gps;
	protected String pesan_klik_infowindow;
	private String pesan_Progress_Dialog;
	private String pesan_Progress_Tunggu;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
		.permitAll().build();
		
		StrictMode.setThreadPolicy(policy);
		*/
		no_hp = getIntent().getStringExtra("no_hp");
//		Log.wtf("Sukses Login", no_hp);
		/**
		 * Dashboard Screen for the application
		 **/
		// Check login status in database
		
//		link= URL2+"?no_hp="+no_hp;
//		RefreshList(link);
		
		userFunctions = new UserFunctions();
		if (userFunctions.isUserLoggedIn(getApplicationContext())) {
			setContentView(R.layout.activity_main);
			/*btnLogout = (Button) findViewById(R.id.btnLogout);
			btnLogout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					userFunctions.logoutUser(getApplicationContext());
					Intent login = new Intent(getApplicationContext(),
							LoginActivity.class);
					login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(login);
					// Closing dashboard screen
					finish();
				}
			});*/
			new DownloadJSON().execute();
		} else {
			// user is not logged in show login screen
			Intent login = new Intent(getApplicationContext(),
					LoginActivity.class);
			login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(login);
			// Closing dashboard screen
			finish();
		}
	}
	
	
	// Download JSON file AsyncTask
		private class DownloadJSON extends AsyncTask<Void, Void, Void> {

			@Override
			protected Void doInBackground(Void... params) {
				// Locate the WorldPopulation Class	
				pengguna = new ArrayList<SpinnerPopulation>();
				// Create an array to populate the spinner 
				penggunalist = new ArrayList<String>();
				// JSON file URL address
//				jsonobject = JSONParser.getJSONfromURL("http://192.168.43.69/android_login_api/spinner/lokasi.php"+ "?no_hp=" + no_hp);
				jsonobject = JSONParser.getJSONfromURL(URL+ "?no_hp=" + no_hp);
				
				try {
					// Locate the NodeList name
					jsonarray = jsonobject.getJSONArray("lokasi");
					for (int i = 0; i < jsonarray.length(); i++) {
						jsonobject = jsonarray.getJSONObject(i);
						
						SpinnerPopulation worldpop = new SpinnerPopulation();

						worldpop.setnama_pengguna(jsonobject.optString("nama_pengguna"));
						worldpop.setimei_pengguna(jsonobject.optString("imei_pengguna"));
						pengguna.add(worldpop);
						
						// Populate spinner with country names
						penggunalist.add(jsonobject.optString("nama_pengguna"));

					}
				} catch (Exception e) {
//					Log.e("Error", e.getMessage());
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void args) {
				// Locate the spinner in activity_main.xml
				Spinner mySpinner = (Spinner) findViewById(R.id.spinner1);
				// Spinner adapter
				mySpinner.setAdapter(new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_dropdown_item,penggunalist));
				// Spinner on item click listener
				mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int position, long arg3) {
								// TODO Auto-generated method stub
								// Set the text followed by the position 
//								Log.e("Get Nama Pengguna > ", pengguna.get(position).getnama_pengguna());
//								Log.e("Get Imei Pengguna > ", pengguna.get(position).getimei_pengguna());
								GetImei_Spinner=pengguna.get(position).getimei_pengguna();
								link= URL2 + "?id=" + GetImei_Spinner;
//								Log.e("Link Untuk Lokasi > ", link.toString());
								RefreshList(link);
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								// TODO Auto-generated method stub
							}
						});
			}
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.activity_main, menu);
		this.menu = menu;
		pesan_Ubah_Tipe_Map=getResources().getString(R.string.menu_Ubah_Map);
		pesan_Informasi=getResources().getString(R.string.menu_Informasi);
		pesan_Logout=getResources().getString(R.string.txt_Logout);
		pesan_Exit=getResources().getString(R.string.menu_Exit);
		menu.add(0, 1, 0,pesan_Ubah_Tipe_Map).setIcon(
				android.R.drawable.ic_menu_mapmode);
		menu.add(0, 2, 0,pesan_Informasi).setIcon(
				R.drawable.ic_ppltracker);
		menu.add(0, 3, 0,pesan_Logout).setIcon(android.R.drawable.ic_lock_power_off);
		menu.add(0, 4, 0,pesan_Exit).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:

			MenuOpsiUbahTipeMap();
			return true;
		case 2:
			Intent j = new Intent(this, AboutActivity.class);
			startActivity(j);
			// LoadingBiasa();
			return true;
		case 3:
			userFunctions.logoutUser(getApplicationContext());
			Intent login = new Intent(getApplicationContext(),
					LoginActivity.class);
			login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(login);
			// Closing dashboard screen
			finish();
			// LoadingBiasa();
			return true;
		case 4:
			finish();
			// LoadingBiasa();
			return true;
		}
		return false;
	}

	/*
	 * ========================================================================
	 */
	public void RefreshList(final String url2) { 
		try {
			FragmentManager myFragmentManager = getSupportFragmentManager(); 
			SupportMapFragment mySupportMapFragment = (SupportMapFragment) myFragmentManager.findFragmentById(R.id.lokasi);
			myMap = mySupportMapFragment.getMap();
			myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			myMap.getUiSettings().setCompassEnabled(true);
			myMap.getUiSettings().setZoomControlsEnabled(true);
			myMap.getUiSettings().setMyLocationButtonEnabled(true);
//			myMap.getUiSettings().setAllGesturesEnabled(true);
			myMap.setMyLocationEnabled(true);
			initImageLoader();
	        markers = new Hashtable<String, String>();
	        imageLoader = ImageLoader.getInstance();
			options = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.ajaxloader)
					// Display Stub Image
					.showImageForEmptyUri(R.drawable.no_image)
					// If Empty image found
					.cacheInMemory().cacheOnDisc()
					.bitmapConfig(Bitmap.Config.RGB_565).build();
			if ( myMap != null ) {
				myMap.clear();
			// getRequest(url2+getIntent().getStringExtra("id_obat"));
//			String url = "http://vnot.pusku.com/mymed/daftarapotik.php?id=";
//			url += getIntent().getStringExtra("id_obat");
			myMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
			JSONParser connection = new JSONParser();
			jsonResult = connection.getRequest(url2);
			jObject = new JSONObject(jsonResult);
			JSONArray menuitemArray = jObject.getJSONArray("lokasi");

			
			// untuk nampilin marker yang banyak
			for (int i = 0; i < menuitemArray.length(); i++) {
				lat=Double.parseDouble(menuitemArray.getJSONObject(i).getString("lat").toString());
				lon=Double.parseDouble(menuitemArray.getJSONObject(i).getString("lon").toString()); 
				LatLng titik = new LatLng(lat,lon);
				MarkerOptions markerTitik = new MarkerOptions();
				markerTitik.position(titik);
				id = menuitemArray.getJSONObject(i).getString("id")
						.toString();
				nama = menuitemArray.getJSONObject(i)
						.getString("nama_pengguna").toString();
//				Log.e("koordinat '"+nama.toString()+"' >", titik.toString());
				hp = menuitemArray.getJSONObject(i).getString("hp_pengguna")
						.toString();
				imei = menuitemArray.getJSONObject(i)
						.getString("imei_pengguna").toString();
				foto_pengguna = menuitemArray.getJSONObject(i)
						.getString("foto_pengguna").toString();
				update_on = menuitemArray.getJSONObject(i)
						.getString("update_on").toString();
				
				foto = "http://192.168.43.69/android_login_api/spinner/foto/";
//				Log.e("Alamat Foto Default!! >", foto.toString());
				foto += foto_pengguna;
//				Log.e("Alamat Foto dari '"+nama.toString()+"' >", foto.toString());
				
				// markerJogja.icon(BitmapDescriptorFactory.fromResource(R.drawable.lain_lain));
				/*BitmapFactory.Options bmOptions;
				bmOptions = new BitmapFactory.Options();
				bmOptions.inSampleSize = 1;
				Bitmap bm = LoadImage(foto, bmOptions);
				// markerTitik.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//				markerTitik.icon(BitmapDescriptorFactory.fromBitmap(bm));
				Log.e("Foto >", bm.toString());*/
				
				markerTitik.position(titik);
				markerTitik.title(nama);
				pesan_snippet_no_hp=getResources().getString(R.string.txt_snippet1);
				pesan_snippet_tersimpan=getResources().getString(R.string.txt_snippet2);
				markerTitik.snippet(pesan_snippet_no_hp+ hp +pesan_snippet_tersimpan+ update_on);
				markerTitik.icon(BitmapDescriptorFactory.fromResource(R.drawable.target));
//				markerTitik.icon(BitmapDescriptorFactory
//						.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				
				// markerJogja.icon(BitmapDescriptorFactory.fromResource(R.drawable.lain_lain));
//				BitmapFactory.Options bmOptions;
//				bmOptions = new BitmapFactory.Options();
//				bmOptions.inSampleSize = 1;
//				Bitmap bm = LoadImage(foto, bmOptions);
//				Log.e("bmOptions dari '"+nama.toString()+"' >", bmOptions.toString());
//				Log.e("Foto dari '"+nama.toString()+"' >", bm.toString());
				/*String angka_stok=menuitemArray.getJSONObject(i).getString("stok").toString();
				
				markerTitik.snippet("Keterangan : "+ menuitemArray.getJSONObject(i)
						.getString("alamat_apotik").toString()
						+" Stok Saat ini : "+angka_stok);*/
				// markerTitik.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//				markerTitik.icon(BitmapDescriptorFactory.fromBitmap(bm));
				
				
//				Toast.makeText(getApplicationContext(),"Sedang memuat lokasi keberadaan apotik...",Toast.LENGTH_LONG).show();
				myMap.addMarker(markerTitik);
				Marker titikku = myMap.addMarker(markerTitik);
				markers.put(titikku.getId(), foto);
				// final titik a=arg0;
				myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(titik, 15));
			}
			}
			myMap.setMapType(mapType);	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			pesan_gagal_json=getResources().getString(R.string.txt_gagal_lokasi_json);
			Toast.makeText(getApplicationContext(),pesan_gagal_json,
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}


		myMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker arg0) {
				// TODO Auto-generated method stub
				pesan_menu_Tittle_lokasi=getResources().getString(R.string.menu_Tittle_lokasi);
				pesan_menu_Dialog_lokasi=getResources().getString(R.string.menu_dialog_lokasi);
				final Marker a = arg0;
				final CharSequence[] dialogitem = {pesan_menu_Dialog_lokasi};
				try {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							MainActivity.this);
					builder.setTitle(pesan_menu_Tittle_lokasi);
					builder.setItems(dialogitem,
							new DialogInterface.OnClickListener() {
								private String pesan_refresh;

								@Override
								public void onClick(DialogInterface dialog,
										int item) {
									switch (item) {
									/*case 0:
										Intent i = new Intent(
												getApplicationContext(),
												DetailApotikActivity.class);
										Toast.makeText(getApplicationContext(),
												"Mohon tunggu...",
												Toast.LENGTH_SHORT).show();
										i.putExtra("nama_apotik", nama_apotik1);
										i.putExtra("alamat_apotik",
												alamat_apotik1);
										i.putExtra("telp", telp1);
										i.putExtra("ket", ket1);
										i.putExtra("stok", stok1);
										startActivity(i);
										break;*/
									case 0:
										StringBuilder urlString = new StringBuilder();
										String daddr = (String.valueOf(a
												.getPosition().latitude) + "," + String
												.valueOf(a.getPosition().longitude));
										urlString
												.append("http://maps.google.com/maps?f=d&hl=en");
										urlString.append("&saddr="
												+ String.valueOf(myMap
														.getMyLocation()
														.getLatitude())
												+ ","
												+ String.valueOf(myMap
														.getMyLocation()
														.getLongitude()));
										urlString.append("&daddr=" + daddr);
										Intent i2 = new Intent(
												Intent.ACTION_VIEW, Uri
														.parse(urlString
																.toString()));
										pesan_refresh=getResources().getString(R.string.refresh);
										Toast.makeText(getApplicationContext(),pesan_refresh,
												Toast.LENGTH_SHORT).show();
										startActivity(i2);
										break;
									/*
									 * case 2 : Intent i3 = new
									 * Intent(LokasiActivity.this,
									 * RouteActivity.class);
									 * i3.putExtra("latend",
									 * String.valueOf(a.getPosition
									 * ().latitude)); i3.putExtra("lngend",
									 * String
									 * .valueOf(a.getPosition().longitude));
									 * i3.putExtra("latstart",
									 * String.valueOf(myMap
									 * .getMyLocation().getLatitude()));
									 * i3.putExtra("lngstart",
									 * String.valueOf(myMap
									 * .getMyLocation().getLongitude()));
									 * startActivity(i3);
									 * //Toast.makeText(getApplicationContext(),
									 * "Muat Ulang Daftar...",
									 * Toast.LENGTH_SHORT).show(); break;*/
									 
									}
								}
							});
					builder.create().show();
					 
				} catch (Exception ee) {
					pesan_gagal_gps=getResources().getString(R.string.txt_gagal_gps);
					Toast.makeText(
							getApplicationContext(),pesan_gagal_gps,
							Toast.LENGTH_LONG).show();
				}
				// return false;
			}
		});

		myMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker arg0) {
				// TODO Auto-generated method stub
				pesan_klik_infowindow=getResources().getString(R.string.txt_klik_infowindow);
				Toast.makeText(getApplicationContext(),pesan_klik_infowindow, Toast.LENGTH_LONG)
						.show();
				return false;
			}
		});
	}
	
	/*private Bitmap LoadImage(String URL, BitmapFactory.Options options) {
		Bitmap bitmap = null;
		InputStream in = null;
		try {
			JSONParser connection = new JSONParser();
			in = connection.OpenHttpConnection(URL, this);
			bitmap = BitmapFactory.decodeStream(in, null, options);
			in.close();
		} catch (IOException e1) {
			Log.e("EROR! IMAGE >", e1.toString());
		}
		return bitmap;
	}*/
	
	private class CustomInfoWindowAdapter implements InfoWindowAdapter {

		private View view;
		private String url;

		public CustomInfoWindowAdapter() {
			view = getLayoutInflater().inflate(R.layout.custome_infowindow1,
					null);
		}

		@Override
		public View getInfoContents(Marker marker) {

			if (MainActivity.this.marker != null && MainActivity.this.marker.isInfoWindowShown()) {
				MainActivity.this.marker.hideInfoWindow();
				MainActivity.this.marker.showInfoWindow();
			}
			return null;
		}

		@Override
		public View getInfoWindow(final Marker marker) {
			MainActivity.this.marker = marker;

			/*String url = null;

			if (marker.getId() != null && markers != null && markers.size() > 0) {
				if ( markers.get(marker.getId()) != null &&	markers.get(marker.getId()) != null) {
					url = markers.get(marker.getId());
					Log.e("ASUUUUU ", url);
				}
			}
			url = markers.get(marker.getId());
			Log.e("ASUUUUU ", url);
			final ImageView image = ((ImageView) view.findViewById(R.id.badge));

			if (url != null && !url.equalsIgnoreCase("null") && !url.equalsIgnoreCase("")) {
				imageLoader.displayImage(url, image, options, new SimpleImageLoadingListener() {
							@Override
							public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
								super.onLoadingComplete(imageUri, view, loadedImage);
								getInfoContents(marker);
							}
						});
			} else {
//				image.setImageURI(BitmapDescriptorFactory.fromBitmap(bm));
				image.setImageResource(R.drawable.no_image);
			}*/
			
			/*ImageView iViewer = (ImageView) findViewById(R.id.badge);
			iViewer.setImageResource(R.drawable.no_image);
			// Menggunakan Library Picasso
			 Picasso.with(getBaseContext()).load(foto).into(iViewer);
			try {
//				LoadingDataObat();
				Picasso.with(getBaseContext()).load(foto).into(iViewer);
//				LoadingDataObat();
//				Picasso.with(getBaseContext()).setDebugging(true);
			} catch (Exception e) {
				
				Picasso.with(getBaseContext()).load(R.drawable.no_image).into(iViewer);
				// TODO: handle exception
			}*/

			final String title = marker.getTitle();
			final TextView titleUi = ((TextView) view.findViewById(R.id.title));
			if (title != null) {
				titleUi.setText(title);
			} else {
				titleUi.setText(R.string.app_name);
			}

			final String snippet = marker.getSnippet();
			final TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
			if (snippet != null) {
				snippetUi.setText(snippet);
			} else {
				snippetUi.setText(R.string.hello_world);
			}

			return view;
		}
	}

	private void initImageLoader() {
		int memoryCacheSize;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
			int memClass = ((ActivityManager)getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
			memoryCacheSize = (memClass / 8) * 1024 * 1024;
		} else {
			memoryCacheSize = 2 * 1024 * 1024;
		}

		final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).threadPoolSize(5)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCacheSize(memoryCacheSize)
				.memoryCache(new FIFOLimitedMemoryCache(memoryCacheSize-1000000))
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).enableLogging() 
				.build();

		ImageLoader.getInstance().init(config);
	}
	
	public void MenuOpsiUbahTipeMap(){
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				MainActivity.this);
		builder.setTitle(R.string.tittle_opsi);
		builder.setItems(R.array.opsi,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int item) {
						switch (item) {
						case 0:
							
//							String url = "http://vnot.pusku.com/mymed/daftarobat.php?gol=B";
							try {
								mapType = GoogleMap.MAP_TYPE_NORMAL;
								Thread.sleep(2000);
//								RefreshList(url);
								LoadingCepat();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							break;
						case 1:

//							String url1 = "http://vnot.pusku.com/mymed/daftarobat.php?gol=B/K";
							try {
								mapType = GoogleMap.MAP_TYPE_TERRAIN;
								Thread.sleep(2000);
//								RefreshList(url1);
								LoadingCepat();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							break;
						case 2:

//							String url2 = "http://vnot.pusku.com/mymed/daftarobat.php?gol=K";
							try {
								mapType = GoogleMap.MAP_TYPE_SATELLITE;
								Thread.sleep(2000);
//								RefreshList(url2);
								LoadingCepat();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							break;
						}/**/
					}
				});
		builder.create().show();
		myMap.setMapType(mapType);
	}
	
	public void LoadingCepat() {
		pesan_Progress_Dialog=getResources().getString(R.string.txt_PD1);
		pesan_Progress_Tunggu=getResources().getString(R.string.please_wait);
		final ProgressDialog myProgressDialog2 = ProgressDialog.show(
				MainActivity.this, pesan_Progress_Dialog, pesan_Progress_Tunggu,
				true);
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(6000);
				} catch (Exception e) {
				}
				myProgressDialog2.dismiss();
			}
		}.start();
	}
}
