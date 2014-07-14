/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.kymjs.aframe.image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

/**
 * Displays {@link Bitmap} in {@link ImageView}. Implementations can apply some
 * changes to Bitmap or any animation for displaying Bitmap.<br />
 * Implementations have to be thread-safe.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.5.6
 */
@SuppressLint("HandlerLeak")
public class BitmapDisplayer implements BitmapDisplayerCalback {
	/**
	 * Display bitmap in {@link ImageView}. Displayed bitmap should be returned.<br />
	 * <b>NOTE:</b> This method is called on UI thread so it's strongly
	 * recommended not to do any heavy work in it.
	 * 
	 * @param bitmap
	 *            Source bitmap
	 * @param imageView
	 *            {@linkplain ImageView Image view} to display Bitmap
	 * @param loadedFrom
	 *            Source of loaded image
	 * @return
	 * @return Bitmap which was displayed in {@link ImageView}
	 * 
	 */
	protected Context context;
	protected ImageView imageView;
	protected boolean isLocal;
	protected String url;

	protected Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Bitmap bitmap = (Bitmap) msg.obj;
			if (bitmap != null && imageView.getTag().toString() == url)
				imageView.setImageBitmap(bitmap);
		};
	};

	public void display(Context context , String url, Bitmap bitmap, ImageView imageView, boolean isLocal) {
		this.context = context;
		this.imageView = imageView;
		this.isLocal = isLocal;
		this.url = url;
	}

	@Override
	public void displayCallBack(Bitmap bitmap) {
		if (isLocal)
			ImageCacheManager.getInstance(context).putBitmap(context ,url, bitmap, false, isLocal);
		else
			ImageCacheManager.getInstance(context).putBitmap(context , url, bitmap, true, isLocal);
		Message msg = new Message();
		msg.obj = bitmap;
		mHandler.sendMessage(msg);
	};

}
