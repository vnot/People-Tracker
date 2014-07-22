/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * */
package com.threepreneur.lib;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class UserFunctions {

	private JSONParser jsonParser;

//	private static String loginURL = "http://10.0.2.2/android_login_api/";
//	private static String registerURL = "http://10.0.2.2/android_login_api/";
//	private static String loginURL = "http://192.168.43.69/android_login_api/";
//	private static String registerURL = "http://192.168.43.69/android_login_api/";
	private static String loginURL = "http://people.geotracker.cu.cc/android_login_api/";
	private static String registerURL = "http://people.geotracker.cu.cc/android_login_api/";

	private static String login_tag = "login";
	private static String register_tag = "register";

	// constructor
	public UserFunctions() {
		jsonParser = new JSONParser();
	}

	/**
	 * function make Login Request
	 * 
	 * @param no_telp
	 * @param password
	 * */
	public JSONObject loginUser(String no_telp, String password) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", login_tag));
		params.add(new BasicNameValuePair("no_telp", no_telp));
		params.add(new BasicNameValuePair("password", password));
		JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
		// return json
//		Log.e("JSON", json.toString());
		return json;
	}

	/**
	 * function make Login Request
	 * 
	 * @param name
	 * @param no_telp
	 * @param password
	 * */
	public JSONObject registerUser(String name, String no_telp, String password) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", register_tag));
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("no_telp", no_telp));
		params.add(new BasicNameValuePair("password", password));

		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
		// return json
		return json;
	}

	/**
	 * Function get Login status
	 * */
	public boolean isUserLoggedIn(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getRowCount();
		if (count > 0) {
			// user logged in
			return true;
		}
		return false;
	}

	/**
	 * Function to logout user Reset Database
	 * */
	public boolean logoutUser(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();
		return true;
	}


}
