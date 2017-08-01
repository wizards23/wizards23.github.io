package com.main.pda;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;

public class Scan {
	Timer t = new Timer();
	public Handler handler;

	public void scan() {
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				Message m = handler.obtainMessage();
				m.what = 1;
				handler.sendMessage(m);
			}
		}, 600);
	}
}
