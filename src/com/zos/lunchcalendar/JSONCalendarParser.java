package com.zos.lunchcalendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

public class JSONCalendarParser {
	final String TAG = "ParsingActivity";

	private ArrayList<String> content = new ArrayList<String>();
	private ArrayList<String> startTime = new ArrayList<String>();
	private ArrayList<String> endTime = new ArrayList<String>();

	private ArrayList<DailyMenu> menuForMonth = new ArrayList<DailyMenu>();
	// private DailyMenu menuForDay = new DailyMenu();

	private String urlString = null;
	private String filename = null;
	private Context myContext;
	public volatile boolean parsingComplete = true;

	public JSONCalendarParser(String url, Context context) {
		this.urlString = url;
		this.myContext = context;
	}

	public JSONCalendarParser(String filename, Context context, boolean fromFile) {
		if (fromFile) {
			this.myContext = context;
			this.filename = filename;
		}
	}

	public ArrayList<String> getContentFromJson() {
		ArrayList<String> result = splitContent(content);
		return result;
	}

	private ArrayList<String> splitContent(ArrayList<String> myContent) {
		// TODO Auto-generated method stub
		ArrayList<String> mealz = new ArrayList<String>();
		DailyMenu menuForDay;

		HashSet<String> hs = new HashSet<String>();
		System.out.println("myContent.size() = " + myContent.size());
		for (int i = 0; i < myContent.size(); i++) {
			String mealNumOne = myContent.get(i);
			String[] mealsAll = mealNumOne.split("[[0-9]]+[.]+[' ']");

			for (int j = 1; j < mealsAll.length; j++) {
				mealsAll[j] = mealsAll[j].replace(" or ", ";");
				mealz.add(mealsAll[j]);
			}
			for (int k = 0; k < mealz.size(); k++) {
				menuForDay = new DailyMenu(mealz.get(k), startTime.get(i),
						endTime.get(i));
				menuForMonth.add(menuForDay);
			}
			hs.addAll(mealz);
			mealz.clear();
			mealz.addAll(hs);
		}
		return mealz;

	}

	protected void buildMenuForMonth() {
		// TODO Auto-generated method stub

		for (int i = 0; i < content.size(); i++) {
			DailyMenu menuForDay = new DailyMenu(content.get(i),
					startTime.get(i), endTime.get(i));
			menuForMonth.add(menuForDay);
		}
	}

	public ArrayList<DailyMenu> getMenuFromJson() {
		// buildMenuForMonth();
		return menuForMonth;
	}

	// public DailyMenu getMenuForOneDay() {
	// return menuForDay;
	// }

	@SuppressLint("NewApi")
	public void readAndParseJSON(String in) {
		try {
			DailyMenu menuForDay = new DailyMenu();
			JSONObject reader = new JSONObject(in);

			JSONObject sys = reader.getJSONObject("feed");

			JSONArray array = sys.getJSONArray("entry");
			
			for (int i = 0; i < array.length(); i++) { // initializing the
														// monthly menu
				System.out.println("Entries " + array.length());
				JSONObject product = new JSONObject(array.getJSONObject(i)
						.getString("title"));
				content.add(product.getString("$t"));

				JSONObject elem = array.getJSONObject(i);
				if (elem != null) {
					JSONArray startEndTime = elem.getJSONArray("gd$when");
					if (startEndTime != null) {
						for (int k = 0; k < startEndTime.length(); k++) {
							JSONObject innerElem = startEndTime
									.getJSONObject(k);
							if (innerElem != null) {
								startTime.add(innerElem.getString("startTime"));
								endTime.add(innerElem.getString("endTime"));

							}
						}
					}
				}
			}

			parsingComplete = false;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void fetchJSON() {
		if (urlString != null) {
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						URL url = new URL(urlString);
						HttpURLConnection conn = (HttpURLConnection) url
								.openConnection();
						conn.setReadTimeout(10000);
						conn.setConnectTimeout(15000);
						conn.setRequestMethod("GET");
						conn.setDoInput(true);

						conn.connect();
						InputStream stream = conn.getInputStream();

						String data = convertStreamToString(stream);

						readAndParseJSON(data);

						stream.close();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			thread.start();
		}
		if (filename != null) {
			String data = loadJSONFromAsset();

			readAndParseJSON(data);
		}
	}

	static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	public String loadJSONFromAsset() {
		String json = "blallblbalablba";
		try {

			AssetManager am = myContext.getAssets();
			StringBuilder buf = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					am.open(this.filename)));
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				// Here we are printing each line at a time from the file we
				// read
				buf.append(sCurrentLine);
			
			}
			json = buf.toString().replaceAll("[\n\r]", "");
		} catch (IOException ex) {
			Log.v(TAG, "NullPointer? called");
			ex.printStackTrace();
			return null;
		}
		return json;

	}
}