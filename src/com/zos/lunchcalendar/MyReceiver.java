package com.zos.lunchcalendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class MyReceiver extends BroadcastReceiver
{
	Bundle extras;      
    @Override
    public void onReceive(Context context, Intent intent)
    {
       Intent service1 = new Intent(context, MyAlarmService.class);
       extras = intent.getExtras();
       String meal = extras.getString("MEAL");
       service1.putExtra("MYMEAL", meal);
       context.startService(service1);
        
    }   
}
