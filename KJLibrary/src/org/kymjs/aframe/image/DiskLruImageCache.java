package org.kymjs.aframe.image;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import com.jakewharton.disklrucache.DiskLruCache;

/**
 * Implementation of DiskLruCache by Jake Wharton modified from
 * http://stackoverflow
 * .com/questions/10185898/using-disklrucache-in-android-4-0-
 * does-not-provide-for-opencache-method
 */
public class DiskLruImageCache {

	private DiskLruCache mDiskCache;
	private static CompressFormat mCompressFormat = CompressFormat.JPEG;
	private static int IO_BUFFER_SIZE = 16 * 1024;
	private static int mCompressQuality = 70;
	private static final int APP_VERSION = 1;
	private static final int VALUE_COUNT = 1;
	private int diskCacheSize;
	private String uniqueName;

	// 这里本地缓存地址保存在/data/data/com.chen.mullistdemo/cache/data/app/com.chen.mullistdemo-2.apk
	public DiskLruImageCache(Context context, String uniqueName, int diskCacheSize, CompressFormat compressFormat,
			int quality) {
		try {
			this.uniqueName = uniqueName;
			this.diskCacheSize = diskCacheSize;
			final File diskCacheDir = getDiskCacheDir(context, uniqueName);
			mDiskCache = DiskLruCache.open(diskCacheDir, APP_VERSION, VALUE_COUNT, diskCacheSize);
			mCompressFormat = compressFormat;
			mCompressQuality = quality;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor) throws IOException,
			FileNotFoundException {
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(editor.newOutputStream(0), IO_BUFFER_SIZE);
			return bitmap.compress(mCompressFormat, mCompressQuality, out);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	private File getDiskCacheDir(Context context, String uniqueName) {

		final String cachePath = context.getCacheDir().getPath();
		return new File(cachePath + File.separator + uniqueName);
	}

	public void putBitmap(Context context , String key, Bitmap data) {
		if (mDiskCache.isClosed()) {
			try {
				final File diskCacheDir = getDiskCacheDir(context, uniqueName);
				mDiskCache = DiskLruCache.open(diskCacheDir, APP_VERSION, VALUE_COUNT, diskCacheSize);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		DiskLruCache.Editor editor = null;
		try {
			editor = mDiskCache.edit(key);
			if (editor == null) {
				return;
			}

			if (writeBitmapToFile(data, editor)) {
				mDiskCache.flush();
				editor.commit();
//				if (Config.DEBUG) {
//					LogUtil.d("cache_test_DISK_", "image put on disk cache " + key);
//				}
			} else {
				editor.abort();
//				if (Config.DEBUG) {
//					LogUtil.d("cache_test_DISK_", "ERROR on: image put on disk cache " + key);
//				}
			}
		} catch (IOException e) {
//			if (Config.DEBUG) {
//				LogUtil.d("cache_test_DISK_", "ERROR on: image put on disk cache " + key);
//			}
			try {
				if (editor != null) {
					editor.abort();
				}
			} catch (IOException ignored) {
			}
		}

	}

	public Bitmap getBitmap(Context context , String key) {
		if (mDiskCache.isClosed()) {
			try {
				final File diskCacheDir = getDiskCacheDir(context, uniqueName);
				mDiskCache = DiskLruCache.open(diskCacheDir, APP_VERSION, VALUE_COUNT, diskCacheSize);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Bitmap bitmap = null;
		DiskLruCache.Snapshot snapshot = null;
		try {
			snapshot = mDiskCache.get(key);
			if (snapshot == null) {
				return null;
			}
			final InputStream in = snapshot.getInputStream(0);
			if (in != null) {
				final BufferedInputStream buffIn = new BufferedInputStream(in, IO_BUFFER_SIZE);
				bitmap = BitmapFactory.decodeStream(buffIn);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (snapshot != null) {
				snapshot.close();
			}
		}

//		if (Config.DEBUG) {
//			LogUtil.d("cache_test_DISK_", bitmap == null ? "" : "image read from disk " + key);
//		}

		return bitmap;

	}

	public boolean containsKey(Context context , String key) {
		if (mDiskCache.isClosed()) {
			try {
				final File diskCacheDir = getDiskCacheDir(context, uniqueName);
				mDiskCache = DiskLruCache.open(diskCacheDir, APP_VERSION, VALUE_COUNT, diskCacheSize);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		boolean contained = false;
		DiskLruCache.Snapshot snapshot = null;
		try {
			snapshot = mDiskCache.get(key);
			contained = snapshot != null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (snapshot != null) {
				snapshot.close();
			}
		}

		return contained;

	}

	public void clearCache(Context context) {
		if (mDiskCache.isClosed()) {
			try {
				final File diskCacheDir = getDiskCacheDir(context, uniqueName);
				mDiskCache = DiskLruCache.open(diskCacheDir, APP_VERSION, VALUE_COUNT, diskCacheSize);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (mDiskCache.isClosed()) {
			ImageCacheManager.getInstance(context).initDiskCache(
					context);
		}
		try {
			mDiskCache.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public File getCacheFolder() {
		return mDiskCache.getDirectory();
	}

}
