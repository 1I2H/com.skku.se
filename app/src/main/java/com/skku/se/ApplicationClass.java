package com.skku.se;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.navercorp.volleyextensions.volleyer.Volleyer;
import com.navercorp.volleyextensions.volleyer.factory.DefaultRequestQueueFactory;

/**
 * Created by XEiN on 11/26/15.
 */
public class ApplicationClass extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		RequestQueue requestQueue = DefaultRequestQueueFactory.create(this);
		requestQueue.start();
		Volleyer.volleyer(requestQueue).settings().setAsDefault().done();
	}
}
