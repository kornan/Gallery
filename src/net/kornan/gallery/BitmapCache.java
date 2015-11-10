package net.kornan.gallery;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

public class BitmapCache {
	public Handler h = new Handler();
	private LruCache<String, Bitmap> mLruCache;
	public final String TAG = getClass().getSimpleName();
	private static BitmapCache mInstance;
	private ExecutorService mImageThreadPool;

	private BitmapCache() {
		mImageThreadPool = Executors.newFixedThreadPool(1);
		// 获取应用程序的最大内存
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		// 用最大内存的1/8来存储图片
		final int cacheSize = maxMemory / 4;
		mLruCache = new LruCache<String, Bitmap>(cacheSize) {
			// 获取每张图片的大小
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
			}
		};
	}

	public static BitmapCache getInstance() {
		if (mInstance == null) {
			mInstance = new BitmapCache();
		}
		return mInstance;
	}

	public void cancelAllTasks() {
		if (mImageThreadPool != null) {
			mImageThreadPool.shutdownNow();
		}
	}

	public void displayBmp(final ImageView iv, final String thumbPath,
			final String sourcePath, final ImageCallback callback) {
		if (TextUtils.isEmpty(thumbPath) && TextUtils.isEmpty(sourcePath)) {
			Log.e(TAG, "no paths pass in");
			return;
		}

		final String path;
		final boolean isThumbPath;
		if (!TextUtils.isEmpty(thumbPath)) {
			path = thumbPath;
			isThumbPath = true;
		} else if (!TextUtils.isEmpty(sourcePath)) {
			path = sourcePath;
			isThumbPath = false;
		} else {
			return;
		}

		Bitmap bitmap = getBitmapFromLruCache(path);
		if (bitmap != null) {
			if (callback != null) {
				callback.imageLoad(iv, bitmap, sourcePath);
			}
			iv.setImageBitmap(bitmap);
			Log.d(TAG, "hit cache");
			return;
		}
		iv.setImageBitmap(null);

		if (bitmap == null) {
			mImageThreadPool.execute(new Runnable() {
				Bitmap thumb;

				@Override
				public void run() {
					try {
						if (isThumbPath) {
							thumb = BitmapFactory.decodeFile(thumbPath);
							if (thumb == null) {
								thumb = revitionImageSize(sourcePath);
							}
						} else {
							thumb = revitionImageSize(sourcePath);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (thumb == null)
						return;
					addBitmapToLruCache(path, thumb);
					if (callback != null) {
						h.post(new Runnable() {
							@Override
							public void run() {
								callback.imageLoad(iv, thumb, sourcePath);
							}
						});
					}
				}
			});
		}
	}

	public Bitmap revitionImageSize(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 256)
					&& (options.outHeight >> i <= 256)) {
				in = new BufferedInputStream(
						new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i ++;
		}
		return bitmap;
	}

	/**
	 * 将图片存储到LruCache
	 */
	public void addBitmapToLruCache(String key, Bitmap bitmap) {
		if (getBitmapFromLruCache(key) == null) {
			mLruCache.put(key, bitmap);
		}
	}

	/**
	 * 从LruCache缓存获取图片
	 */
	public Bitmap getBitmapFromLruCache(String key) {
		return mLruCache.get(key);
	}

	public interface ImageCallback {
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params);
	}
}
