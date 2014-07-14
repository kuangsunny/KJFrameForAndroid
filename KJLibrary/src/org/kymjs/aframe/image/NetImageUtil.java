package org.kymjs.aframe.image;

import org.kymjs.aframe.utils.StringUtils;
import org.kymjs.kjlibrary.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

public class NetImageUtil {
	private static final String TAG = NetImageUtil.class.getSimpleName();
	public static RequestQueue requesQueue = null;
	public static ImageCacheManager imageCache;

	public enum ImageType {
		HEAD, PHOTO, PHOTO_BIG;
	}

	// 初始化 图片加载器
	public static void init(Context context) {
		requesQueue = RequestManager.getInstance(context).getmRequestQueue();
		imageCache = ImageCacheManager.getInstance(context);
	}

	/**
	 * 更具类型加载图像
	 * 
	 * @param tag
	 * @param imageView
	 * @param url
	 * @param imageType
	 */
	public static void loadImage(Context context , String tag, ImageView imageView, String url, ImageType imageType) {
		loadImage(context , tag, imageView, url, R.drawable.ic_launcher, 0, false);
	}

	/**
	 * 加载显示图片
	 * 
	 * @param imageView
	 *            显示图片的组件
	 * @param url
	 *            图片加载的URL
	 * @param placeHolderImg
	 *            默认的占位显示图片
	 */
	public static void loadImage(Context context , String tag, ImageView imageView, String url, int defaultImageResId) {
		loadImage(context , tag, imageView, url, defaultImageResId, 0);
	}

	/**
	 * 加载显示图片
	 * 
	 * @param imageView
	 *            显示图片的组件
	 * @param url
	 *            图片加载的URL
	 * @param placeHolderImg
	 *            默认的占位显示图片
	 * @param placeHolderImg
	 *            错误显示的图片
	 */
	public static void loadImage(Context context , String tag, ImageView imageView, String url, int defaultImageResId, int errorImageResId) {
		loadImage(context , tag, imageView, url, defaultImageResId, errorImageResId, true, null);
	}

	/**
	 * 加载显示图片
	 * 
	 * @param imageView
	 *            显示图片的组件
	 * @param url
	 *            图片加载的URL
	 * @param placeHolderImg
	 *            默认的占位显示图片
	 * @param cacheInMemory
	 *            是否将图片缓存到内存中(大图不建议缓存到内存中)
	 */
	public static void loadImage(Context context , String tag, final ImageView imageView, final String url, int defaultImageResId,
			final int errorImageResId, final boolean cacheInMemory) {
		loadImage(context , tag, imageView, url, defaultImageResId, errorImageResId, cacheInMemory, null);
	}

	/**
	 * 加载显示图片
	 * 
	 * @param imageView
	 *            显示图片的组件
	 * @param url
	 *            图片加载的URL
	 * @param placeHolderImg
	 *            默认的占位显示图片
	 * @param cacheInMemory
	 *            是否将图片缓存到内存中(大图不建议缓存到内存中)
	 * @param cacheInDisk
	 *            是否将图片缓存到本地(网络加载图片建议都缓到本地)
	 */
	public static void loadImage(final Context context , String tag, final ImageView imageView, final String url, int defaultImageResId,
			final int errorImageResId, final boolean cacheInMemory, final BitmapDisplayer displayer) {
		// 防止HTC图片复用，先取出资源文件的drawable
		Drawable defaultDrawable = context.getResources().getDrawable(defaultImageResId);
		imageView.setImageDrawable(defaultDrawable);
		if (StringUtils.isEmpty(url))
			return;
		final String imgaeUrl = url;
		// 给view设置tag防止图片错位
		imageView.setTag(imgaeUrl);
		Bitmap bitmap = imageCache.getBitmap(context , imgaeUrl, cacheInMemory, false);

		if (bitmap != null) {
			if (displayer != null) {
				displayer.display(context , imgaeUrl, bitmap, imageView, false);
			} else {
				imageView.setImageBitmap(bitmap);
			}
			return;
		}
		ImageRequest imageRquest = new ImageRequest(imgaeUrl, new Listener<Bitmap>() {

			@Override
			public void onResponse(Bitmap response) {
				// 获取当前view的Tag判断是否是当前的url如果是就加载
				if (imageView.getTag().toString() == imgaeUrl) {
					if (displayer != null)
						displayer.display(context , imgaeUrl, response, imageView, false);
					else
						imageView.setImageBitmap(response);
					imageCache.putBitmap(context , imgaeUrl, response, cacheInMemory, false);
				}
			}

		}, 0, 0, Config.RGB_565, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// imageView.setImageResource(R.drawable.ic_launcher);
//				LogUtil.e(TAG, error.getMessage() == null ? StringUtils.BLANK_SPACE : error.getMessage());
			}
		});

		imageRquest.setTag(tag);
		requesQueue.add(imageRquest);
	}

}
