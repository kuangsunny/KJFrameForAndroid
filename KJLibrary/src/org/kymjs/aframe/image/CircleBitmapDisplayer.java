//package org.kymjs.aframe.image;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Bitmap.Config;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.PorterDuff;
//import android.graphics.PorterDuffXfermode;
//import android.graphics.Rect;
//import android.graphics.RectF;
//import android.widget.ImageView;
//
///**
// * 圆形头像的制作
// * 
// * @author cwj
// * 
// */
//@SuppressLint("HandlerLeak")
//public class CircleBitmapDisplayer extends BitmapDisplayer {
//
//	private static final int BOUND = 2;
//	private Paint mPaint;
//	private Paint mPaint2;
//	private Paint framePaint;
//
//	private RectF rect;
//	private int width;
//	private int height;
//	private int frameColor;
//
//	public CircleBitmapDisplayer(int frameColor) {
//		this.frameColor = frameColor;
//		init();
//	}
//
//	private void init() {
//		mPaint = new Paint();
//		mPaint.setColor(Color.WHITE);
//		mPaint.setAntiAlias(true);
//		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
//
//		mPaint2 = new Paint();
//		mPaint2.setXfermode(null);
//
//		framePaint = new Paint();
//		framePaint.setAntiAlias(true);
//		framePaint.setColor(frameColor);
//		framePaint.setStyle(Paint.Style.STROKE);
//		framePaint.setStrokeWidth((float) BOUND);
//
//	}
//
//	@Override
//	public void display(Context context ,String url, final Bitmap bitmap, final ImageView imageView, boolean islocal) {
//		super.display(context , url, bitmap, imageView, islocal);
//		TaskExecutor.getInstance().executeTask(new Runnable() {
//			@Override
//			public void run() {
//				Bitmap roundedBitmap = cricleBitmap(bitmap, imageView);
//				displayCallBack(roundedBitmap);
//			}
//		});
//	}
//
//	public Bitmap cricleBitmap(Bitmap bitmap, ImageView imageView) {
//		Bitmap circleBitmap;
//
//		int bw = bitmap.getWidth();
//		int bh = bitmap.getHeight();
//		int vw = UIHelper.getMeasureWidth(imageView);
//		int vh = UIHelper.getMeasureHeight(imageView);
//		if (vw <= 0)
//			vw = bw;
//		if (vh <= 0)
//			vh = bh;
//
//		Rect srcRect;
//		Rect destRect;
//
//		switch (imageView.getScaleType()) {
//		case CENTER_INSIDE:
//			float vRation = (float) vw / vh;
//			float bRation = (float) bw / bh;
//			int destWidth;
//			int destHeight;
//			if (vRation > bRation) {
//				destHeight = Math.min(vh, bh);
//				destWidth = (int) (bw / ((float) bh / destHeight));
//			} else {
//				destWidth = Math.min(vw, bw);
//				destHeight = (int) (bh / ((float) bw / destWidth));
//			}
//			int x = (vw - destWidth) / 2;
//			int y = (vh - destHeight) / 2;
//			srcRect = new Rect(0, 0, bw, bh);
//			destRect = new Rect(x, y, x + destWidth, y + destHeight);
//			width = vw;
//			height = vh;
//			break;
//		case FIT_CENTER:
//		case FIT_START:
//		case FIT_END:
//		default:
//			vRation = (float) vw / vh;
//			bRation = (float) bw / bh;
//			if (vRation > bRation) {
//				width = (int) (bw / ((float) bh / vh));
//				height = vh;
//			} else {
//				width = vw;
//				height = (int) (bh / ((float) bw / vw));
//			}
//			srcRect = new Rect(0, 0, bw, bh);
//			destRect = new Rect(0, 0, width, height);
//			break;
//		case CENTER_CROP:
//			vRation = (float) vw / vh;
//			bRation = (float) bw / bh;
//			int srcWidth;
//			int srcHeight;
//			if (vRation > bRation) {
//				srcWidth = bw;
//				srcHeight = (int) (vh * ((float) bw / vw));
//				x = 0;
//				y = (bh - srcHeight) / 2;
//			} else {
//				srcWidth = (int) (vw * ((float) bh / vh));
//				srcHeight = bh;
//				x = (bw - srcWidth) / 2;
//				y = 0;
//			}
//			width = srcWidth;// Math.min(vw, bw);
//			height = srcHeight;// Math.min(vh, bh);
//			srcRect = new Rect(x, y, x + srcWidth, y + srcHeight);
//			destRect = new Rect(0, 0, width, height);
//			break;
//		case FIT_XY:
//			width = vw;
//			height = vh;
//			srcRect = new Rect(0, 0, bw, bh);
//			destRect = new Rect(0, 0, width, height);
//			break;
//		case CENTER:
//		case MATRIX:
//			width = Math.min(vw, bw);
//			height = Math.min(vh, bh);
//			x = (bw - width) / 2;
//			y = (bh - height) / 2;
//			srcRect = new Rect(x, y, x + width, y + height);
//			destRect = new Rect(0, 0, width, height);
//			break;
//		}
//
//		try {
//			circleBitmap = getCricleBitmap(bitmap, srcRect, destRect, width, height);
//		} catch (OutOfMemoryError e) {
////			LogUtil.e(CircleBitmapDisplayer.class.getSimpleName(),
////					"Can't create bitmap with rounded corners. Not enough memory.");
//			circleBitmap = bitmap;
//		}
//		return circleBitmap;
//	}
//
//	public Bitmap getCricleBitmap(Bitmap bitmap, Rect srcRect, Rect destRect, int width, int height) {
//		Bitmap outPut = Bitmap.createBitmap(width, height, Config.ARGB_8888);
//		rect = new RectF(BOUND - 1, BOUND - 1, width - BOUND, height - BOUND);
//		Canvas canvas2 = new Canvas(outPut);
//		canvas2.drawBitmap(bitmap, srcRect, destRect, mPaint2);
//		drawLeftUp(canvas2);
//		drawRightUp(canvas2);
//		drawLeftDown(canvas2);
//		drawRightDown(canvas2);
//		if (frameColor != 0) {
//			drawFrame(canvas2);
//		}
//
//		return outPut;
//	}
//
//	private void drawLeftUp(Canvas canvas) {
//		Path path = new Path();
//		path.moveTo(0, width / 2);
//		path.lineTo(0, 0);
//		path.lineTo(width / 2, 0);
//		path.arcTo(rect, -90, -90);
//		path.close();
//		canvas.drawPath(path, mPaint);
//	}
//
//	private void drawLeftDown(Canvas canvas) {
//		Path path = new Path();
//		path.moveTo(0, height / 2 + 0);
//		path.lineTo(0, height + 0);
//		path.lineTo(width / 2 + 0, height + 0);
//		path.arcTo(rect, 90, 90);
//		path.close();
//		canvas.drawPath(path, mPaint);
//	}
//
//	private void drawRightDown(Canvas canvas) {
//		Path path = new Path();
//		path.moveTo(width / 2, height + 0);
//		path.lineTo(width + 0, height + 0);
//		path.lineTo(width + 0, height / 2);
//		path.arcTo(rect, 0, 90);
//		path.close();
//		canvas.drawPath(path, mPaint);
//	}
//
//	private void drawRightUp(Canvas canvas) {
//		Path path = new Path();
//		path.moveTo(width + 0, height / 2 + 0);
//		path.lineTo(width + 0, 0);
//		path.lineTo(width / 2 + 0, 0);
//		path.arcTo(rect, -90, 90);
//		path.close();
//		canvas.drawPath(path, mPaint);
//	}
//
//	private void drawFrame(Canvas canvas) {
//		canvas.drawCircle(width / 2, width / 2, (width - BOUND) / 2, framePaint);
//	}
//}
