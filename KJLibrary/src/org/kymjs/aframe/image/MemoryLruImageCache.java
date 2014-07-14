package org.kymjs.aframe.image;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Basic LRU Memory cache.
 * 
 * @author Trey Robinson
 * 
 */
public class MemoryLruImageCache extends LruCache<String, Bitmap> {

	private final String TAG = this.getClass().getSimpleName();

	public MemoryLruImageCache(int maxSize) {
		super(maxSize);
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	public Bitmap getBitmap(String url) {
//		LogUtil.v(TAG, "getBitmap item from Mem Cache " + url);
		return get(url);
	}

	public void putBitmap(String url, Bitmap bitmap) {
//		LogUtil.v(TAG, "putBitmap item to Mem Cache" + url);
		put(url, bitmap);
	}

	public boolean containsKey(String url) {
		return getBitmap(url) != null;
	}
	

}
