package com.zos.lunchcalendar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("NewApi") public class CustomAdapter extends ArrayAdapter<DailyMenu> {

	private Context context;
	private int viewType;
	// private DailyMenu[] data;
	private ArrayList<DailyMenu> data = new ArrayList<DailyMenu>();
	private ArrayList<String> mealsList = new ArrayList<String>();
	private Set<String> preferredMealsFromArray = new HashSet<String>();
	private ArrayList<String> preferredMeals = new ArrayList<String>();
	private LayoutInflater layoutInflater;

	public CustomAdapter(Context context, int textViewResourceId,
			ArrayList<DailyMenu> lunch_data, int viewType) {
		super(context, textViewResourceId, lunch_data);
		this.viewType = viewType;
		// store constructor parameters
		this.context = context;
		this.data = lunch_data;
		// store objects that will be used multiple times
		this.layoutInflater = LayoutInflater.from(this.context);
	}

	// special for Checked List
	public CustomAdapter(String flag, Context context, int textViewResourceId,
			ArrayList<String> lunchList, int viewType) {
		super(context, textViewResourceId);
		this.viewType = viewType;
		this.context = context;
		this.mealsList = lunchList;
		this.layoutInflater = LayoutInflater.from(this.context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (this.viewType == 0) {
			// if convertView isn't populated, use LayoutInflater to
			// create a new row layout
			View row = convertView;
			if (row == null) {
				row = this.layoutInflater.inflate(R.layout.custom_row_textview,
						null);
			}
			// extract data
			DailyMenu item = this.data.get(position);
			// assign the caption

			TextView lunchDate = (TextView) row.findViewById(R.id.showDate);
			lunchDate.setText(item.getStartTime());

			TextView lunchTitle = (TextView) row.findViewById(R.id.showTitle);
			lunchTitle.setText(item.getTitle());
			if (isPreferredMeal(item.getTitle())) {
				lunchDate.setBackgroundColor(Color.GRAY);
				lunchTitle.setBackgroundColor(Color.GRAY);
			}
			// assign the icon
			// ImageView lunchIcon = (ImageView)
			// row.findViewById(R.id.imageview);

			// lunchIcon.setImageResource(item.getIcon());
			return row;
		} else if (this.viewType == 1) {
			View row = convertView;
			if (row == null) {
				row = this.layoutInflater.inflate(
						R.layout.custom_grid_textview, null);
			}
			DailyMenu item = this.data.get(position);

			TextView lunchData = (TextView) row.findViewById(R.id.showData);
			lunchData.setText(item.getStartTime() + "\n" + item.getTitle());
			
			if (isPreferredMeal(item.getTitle())) {
				lunchData.setBackgroundColor(Color.GRAY);
				//lunchTitle.setBackgroundColor(Color.GRAY);
			}
			
			return row;
		} else { // checked list
			View row = convertView;
			if (row == null) {
				row = this.layoutInflater.inflate(
						R.layout.custom_checked_textview, null);
			}
			String item = this.mealsList.get(position);
			TextView lunchData = (TextView) row.findViewById(R.id.mealsList);
			lunchData.setText(item);
			return row;
		}

	}

	private boolean isPreferredMeal(String meal) {
		// TODO Auto-generated method stub
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		preferredMealsFromArray = sharedPreferences.getStringSet(
				"PreferredMeals", null);
		if (preferredMealsFromArray != null) {
			for (String str : preferredMealsFromArray)
				preferredMeals.add(str);

		}
		for (int i = 0; i < preferredMeals.size(); i++) {
			if (preferredMeals.get(i).contains(meal)) 
				return true;
		}
		return false;
	}
}
