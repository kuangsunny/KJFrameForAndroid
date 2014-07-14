package org.kymjs.aframe.image;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import org.kymjs.aframe.utils.StringUtils;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

/**
 * 图片本地缓存和内存缓存机制
 * 
 * @author cwj
 * 
 */
public class ImageCacheManager {
	private final String TAG = this.getClass().getSimpleName();

	private static CompressFormat mCompressFormat = CompressFormat.JPEG;
	// 本地缓存池大小
	private static int DISK_IMAGECACHE_SIZE = 1024 * 1024 * 20;
	// 内存缓存池大小
	private static int MEMORY_IMAGECACHE_SIZE = 1024 * 1024 * 5;
	private static int DISK_IMAGECACHE_QUALITY = 80;

	private static ImageCacheManager mInstance;

	private MemoryLruImageCache memoryCache;
	private DiskLruImageCache diskCache;

	// 用户储存大图片的临时缓存
	private Map<String, SoftReference<Bitmap>> softImageCache;

	public static ImageCacheManager getInstance(Context context) {
		if (mInstance == null)
			mInstance = new ImageCacheManager(context);

		return mInstance;
	}

	// 取应用内存的6分之一作为图片内存缓存大小
	private ImageCacheManager(Context context) {
		initDiskCache(context);
		initCacheManager(context);
	}

	public MemoryLruImageCache getMemoryCache() {
		return memoryCache;
	}

	public DiskLruImageCache getDiskCache() {
		return diskCache;
	}

	public void initDiskCache(Context context) {
		diskCache = new DiskLruImageCache(context, context.getPackageCodePath(), DISK_IMAGECACHE_SIZE, mCompressFormat,
				DISK_IMAGECACHE_QUALITY);
	}

	public void initCacheManager(Context context) {
		int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		softImageCache = new HashMap<String, SoftReference<Bitmap>>();
		MEMORY_IMAGECACHE_SIZE = 1024 * 1024 * memClass / 6;
		memoryCache = new MemoryLruImageCache(MEMORY_IMAGECACHE_SIZE);
	}

	public Bitmap getBitmap(Context context , String url, boolean cacheInMemory, boolean isLocal) {
		String key = createKey(url, isLocal);
		Bitmap bitmap = null;
		if (!cacheInMemory) {
			bitmap = diskCache.getBitmap(context ,key);
			if (bitmap != null)
				softImageCache.put(key, new SoftReference<Bitmap>(bitmap));
		} else {
			bitmap = memoryCache.getBitmap(key);
			if (bitmap == null && !isLocal) {
				bitmap = diskCache.getBitmap(context ,key);
				if (bitmap != null) {
					putBitmap(context , url, bitmap, cacheInMemory, isLocal);
				}
			}
		}
		if (bitmap != null && !bitmap.isRecycled()) {
			return bitmap;
		}
		return null;
	}

	public void putBitmap(Context context , String url, Bitmap bitmap, boolean cacheInMemory, boolean isLocal) {
		String key = createKey(url, isLocal);
		if (cacheInMemory && !memoryCache.containsKey(key)) {
			memoryCache.putBitmap(key, bitmap);
		}
		if (!isLocal && !diskCache.containsKey(context , key)) {
			diskCache.putBitmap(context , key, bitmap);
		}
	} 

	private String createKey(String url, boolean isLocal) {
		return isLocal == true ? url : String.valueOf(StringUtils.md5(url));
	}

	public void cleanCache(Context context) {
		diskCache.clearCache(context);
	}

	public void reclySoftImageCache(String url) {/*
		String realUrl = Config.TEST_BASE_URL + "/" + url;
		String md5Url = createKey(realUrl, false);
		LogUtil.v(TAG, "recly bitmap from SoftImageCache " + md5Url);
		SoftReference<Bitmap> bitmap = softImageCache.get(md5Url);
		if (bitmap != null && bitmap.get() != null)
			BitmapUtil.recyledBitmap(bitmap.get());
	*/}

}
