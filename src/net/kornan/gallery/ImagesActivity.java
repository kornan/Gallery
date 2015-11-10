package net.kornan.gallery;

import java.util.List;

import net.kornan.gallery.BitmapCache.ImageCallback;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class ImagesActivity extends Activity {
	private List<ImageItem> dataList;
	private GridView gridView;
	private ImagesAdapter adapter;
	private AlbumHelper helper;
	public static final String EXTRA_IMAGE_LIST = "imagelist";
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(ImagesActivity.this, "最多选择9张图片",
						Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_images);
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		initData();
		initView();
	}

	private void initData() {
		dataList = helper.getImagesItemList();
	}

	private void initView() {
		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setOnItemClickListener(null);
		gridView.setOnScrollListener(onScrollListener);
		adapter = new ImagesAdapter(this, dataList, mHandler);
		gridView.setAdapter(adapter);

		adapter.mGridView = gridView;

	}

	private int mFirstVisibleItem;
	private int mVisibleItemCount;
	// 记录是否是第一次进入该界面
	private boolean isFirstEnterThisActivity = true;
	private OnScrollListener onScrollListener = new OnScrollListener() {
		// 滚动停止加载
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			if (scrollState == SCROLL_STATE_IDLE) {
				loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
			} else {
				// BitmapCache.getInstance().cancelAllTasks();
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			mFirstVisibleItem = firstVisibleItem;
			mVisibleItemCount = visibleItemCount;
			if (isFirstEnterThisActivity && visibleItemCount > 0) {
				loadBitmaps(firstVisibleItem, visibleItemCount);
				isFirstEnterThisActivity = false;
			}
		}
	};

	private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
		try {
			for (int i = firstVisibleItem; i < firstVisibleItem
					+ visibleItemCount; i++) {
				String thumbnailPath = dataList.get(i).thumbnailPath;
				String imagePath = dataList.get(i).imagePath;
				Bitmap bitmap = BitmapCache.getInstance()
						.getBitmapFromLruCache(imagePath);
				if (bitmap == null) {
					ImageView imageView = (ImageView) gridView
							.findViewWithTag(imagePath);
					BitmapCache.getInstance().displayBmp(imageView,
							thumbnailPath, imagePath, new ImageCallback() {
								@Override
								public void imageLoad(ImageView imageView,
										Bitmap bitmap, Object... params) {
									// TODO Auto-generated method stub
									imageView.setImageBitmap(bitmap);
								}
							});
				} else {
					ImageView imageView = (ImageView) gridView
							.findViewWithTag(imagePath);
					if (imageView != null && bitmap != null) {
						imageView.setImageBitmap(bitmap);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
