package com.threepreneur.f2014.peopletracker;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class AboutActivity extends Activity {

	private Menu menu;
	private String pesan_Informasi;
	private String pesan_Exit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.menu = menu;
		pesan_Informasi=getResources().getString(R.string.menu_Informasi);
		pesan_Exit=getResources().getString(R.string.menu_Exit);
		menu.add(0, 1, 0,pesan_Informasi).setIcon(
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
