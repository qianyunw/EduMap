package com.example.edumap;

import java.util.ArrayList;

import android.app.Activity;

public class ActivityCollector {

	public static ArrayList<Activity> activities = new ArrayList<Activity>();
	
	public static void addActivity(Activity activity) {
		activities.add(activity);
	}
	public static void removeActivity() {
		if(!activities.isEmpty()){
			activities.get(activities.size()-1).finish();
		}
	}

	public static void removeActivity(Activity activity) {
		activities.remove(activity);
	}
}
