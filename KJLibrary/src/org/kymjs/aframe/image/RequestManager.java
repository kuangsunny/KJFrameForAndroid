package org.kymjs.aframe.image;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestManager {
	private static RequestManager mInstance = null;
	private RequestQueue mRequestQueue;

	private RequestManager(Context context) {
		mRequestQueue = Volley.newRequestQueue(context);
	}

	public static RequestManager getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new RequestManager(context);
		}
		return mInstance;
	}

	public RequestQueue getmRequestQueue() {
		return mRequestQueue;
	}

	public void release() {
		this.mRequestQueue = null;
		mInstance = null;
	}

}
