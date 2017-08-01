package com.broadcastreceiver;


import com.main.pda.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastreceiverStart extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			
            Intent intent2 = new Intent(context, MainActivity.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
            context.startActivity(intent2); 
            Log.i("123", "开机启动");
            
           
		}
		
	}

}
