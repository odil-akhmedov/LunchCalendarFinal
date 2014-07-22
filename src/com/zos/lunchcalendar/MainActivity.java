package com.zos.lunchcalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.R.string;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends Activity {
	final String TAG = "MainActivity";
	// Changed view type to global value to fix changing problem. -ZR
	
	private Bundle settings;

	CustomAdapter adapter;
	ArrayList<String> result = new ArrayList<String>();

	private ArrayList<DailyMenu> menuForMonth = new ArrayList<DailyMenu>();
	public DailyMenu menuForOneDay = new DailyMenu();

	private String url = "http://www.google.com/calendar/feeds/gqccak2junkb7eup9ls76k919c@group.calendar.google.com/public/full?alt=json&orderby=starttime&max-results=15&singleevents=true&sortorder=ascending&futureevents=true";
	private JSONCalendarParser obj;
	SharedPreferences sharedPreferences;

	Set<String> preferredMealsFromArray = new HashSet<String>();
	ArrayList<String> preferredMeals = new ArrayList<String>();
	String preferredDay; // Day before, same day...

	Date notificationTime;
	private PendingIntent pendingIntent;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// if (settings != null){
		// viewType = settings.getInt("VIEW_TYPE");
		// } else
		// viewType = 0;

		if (savedInstanceState != null) {
			Log.v(TAG, "onRestoreInstanceState called");
			Log.v(TAG, "Values restored in onRestoreInstanceState = "
					+ savedInstanceState.getInt("VIEWTYPE"));
		}

		switch (Global.viewType) {
		case 0:
			setContentView(R.layout.activity_main_list);
			break;
		case 1:
			setContentView(R.layout.activity_main_grid);
			break;
		}

		set_update_date(); // SET UPDATE DATE -ZR
		startParsing();
		// adapter = new CalendarAdapters(getApplicationContext());
		// Notifications

		String startTime = "2014-07-20 21:00:00";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long notifyTime;
		Date date = null;
		try {
			date = sdf.parse(startTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Calendar calendar = Calendar.getInstance();
		// calendar.setTimeInMillis(date.getTime());
		notifyTime = date.getTime();

		System.out.println("Notifytime = " + notifyTime);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		preferredMealsFromArray = sharedPreferences.getStringSet(
				"PreferredMeals", null);

		/*
		 * Calendar calendar = Calendar.getInstance();
		 * 
		 * calendar.set(2014, 6, 20, 20, 48, 4); //omg months start from 0 o_O
		 * 
		 * Intent myIntent = new Intent(MainActivity.this, MyReceiver.class);
		 * pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0,
		 * myIntent,0);
		 * 
		 * AlarmManager alarmManager =
		 * (AlarmManager)getSystemService(ALARM_SERVICE);
		 * alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(),
		 * pendingIntent); alarmManager.set(AlarmManager.RTC,
		 * System.currentTimeMillis(), pendingIntent);
		 * 
		 * 
		 * NotificationManager notificationManager = (NotificationManager)
		 * getSystemService(NOTIFICATION_SERVICE);
		 * 
		 * String longText = "Meals you saved is going to be served soon";
		 * 
		 * PendingIntent pIntent = PendingIntent.getActivity(
		 * getApplicationContext(), 0, new Intent(), // add this
		 * PendingIntent.FLAG_UPDATE_CURRENT);
		 * 
		 * Notification n = new Notification.Builder(this)
		 * .setContentTitle("Your favorite meal spotted!")
		 * .setContentText("Subject").setSmallIcon(R.drawable.ic_launcher)
		 * .setContentIntent(pIntent).setAutoCancel(true)
		 * .addAction(R.drawable.ic_launcher, "More", pIntent) .setStyle(new
		 * Notification.BigTextStyle().bigText(longText)).build();
		 * 
		 * notificationManager.notify(0, n);
		 */

	}

	
	// Additions by Zack: ============================================================//
	
	// Added global variables to easily set and store update date. -ZR
	public static class Global {
		public static String update_date = "";
		
		public static int viewType = 0; // 0 - for list view, 1 for grid view
	}
	// loads the last update date from shared preferences
	public void load_update_date(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		Global.update_date = preferences.getString("update_date", Global.update_date);
	}
	// saves the last update date to shared preferences
	public void save_update_date(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("update_date", Global.update_date);
		editor.commit();
	}
	// sets and saves new update date when process is complete. long name, I KNOW.
	public void set_save_new_update_date(){
		TextView txt_up_date = (TextView) findViewById(R.id.txt_date);
		
		SimpleDateFormat date = new SimpleDateFormat("MM/dd/yy");
		String currentDate = date.format(new Date());
		Global.update_date = currentDate;
		txt_up_date.setText(currentDate);
		save_update_date();
	}
	// sets the date on any inflation
	public void set_update_date(){
		load_update_date();
		TextView txt_up_date = (TextView) findViewById(R.id.txt_date);
		
		//txt_up_date.setText(Global.update_date);
		Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = df.format(c.getTime());
		
		txt_up_date.setText(formattedDate);
	}
	
	
	
	//=========================END======================================================//
	
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		Global.viewType = savedInstanceState.getInt("VIEWTYPE");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt("VIEWTYPE", Global.viewType);
	}

	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.v(TAG, "onRestart method called");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		set_update_date(); // SET UPDATE DATE -ZR
		Log.v(TAG, "onResume method called");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.v(TAG, "onPause method called");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		Log.v(TAG, "onDestroy method called");
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	// Options menu
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		Intent i;
		switch (menuItem.getItemId()) {
		case R.id.listViewMode: // do something
			Global.viewType = 0; // List
			i = new Intent(this, MainActivity.class);
			i.putExtra("VIEW_TYPE", Global.viewType);
			startActivity(i);
			return true;

		case R.id.gridViewMode:
			Global.viewType = 1; // Grid
			i = new Intent(this, MainActivity.class);
			i.putExtra("VIEW_TYPE", Global.viewType);
			startActivity(i);
			return true;
		case R.id.settings:
			i = new Intent(this, SettingsActivity.class);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(menuItem);
		}
	}

	public void open(View view) {
		Toast.makeText(MainActivity.this, "Updating...", Toast.LENGTH_SHORT).show(); // Notify user of process taking place
		obj = new JSONCalendarParser(url, getApplicationContext());
		obj.fetchJSON();
		while (obj.parsingComplete)
			;

		result = obj.getContentFromJson();

		menuForMonth = obj.getMenuFromJson();
		switch (Global.viewType) {
		case 0:
			CustomAdapter adapter = new CustomAdapter(this,
					R.layout.custom_row_textview, menuForMonth, Global.viewType);
			ListView listView = (ListView) findViewById(R.id.listViewMain);

			listView.setAdapter(adapter);
			break;
		case 1:
			CustomAdapter adapter2 = new CustomAdapter(this,
					R.layout.custom_grid_textview, menuForMonth, Global.viewType);
			GridView g = (GridView) findViewById(R.id.gridViewMain);
			g.setAdapter(adapter2);
			break;
		}
		set_save_new_update_date(); // sets the new update date -ZR
	}

	public void startParsing() {
		obj = new JSONCalendarParser(url, getApplicationContext());
		obj.fetchJSON();
		while (obj.parsingComplete);

		result = obj.getContentFromJson();

		menuForMonth = obj.getMenuFromJson();
		switch (Global.viewType) {
		case 0:
			CustomAdapter adapter = new CustomAdapter(this,
					R.layout.custom_row_textview, menuForMonth, Global.viewType);
			ListView listView = (ListView) findViewById(R.id.listViewMain);

			listView.setAdapter(adapter);
			break;
		case 1:
			CustomAdapter adapter2 = new CustomAdapter(this,
					R.layout.custom_grid_textview, menuForMonth, Global.viewType);
			GridView g = (GridView) findViewById(R.id.gridViewMain);
			g.setAdapter(adapter2);
			break;
		}

	}
}