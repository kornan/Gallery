package net.kornan.gallery;

import java.util.ArrayList;
import java.util.List;

import net.kornan.gallery.BitmapCache.ImageCallback;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class ImagesActivity extends Activity implements OnClickListener {
	private List<ImageItem> dataList;
	private GridView gridView;
	private ImagesAdapter adapter;
	private AlbumHelper helper;
	private Button btn_preview;
	/**
	 * 选择图片的最大值,0为无限制,默认为9
	 */
	private int select_max = 9;
	private ArrayList<String> select_images=new ArrayList<String>();
	// public static final String EXTRA_IMAGE_LIST = "imagelist";

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(ImagesActivity.this,
						"最多选择" + select_max + "张图片", Toast.LENGTH_LONG).show();
				break;
			case 1:
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
		select_max = getIntent().getIntExtra("select_max", 9);
		dataList = helper.getImagesItemList();
	}

	private void initView() {
		btn_preview = (Button) findViewById(R.id.btn_preview);
		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setOnItemClickListener(null);
		gridView.setOnScrollListener(onScrollListener);
		adapter = new ImagesAdapter(this, dataList, mHandler);
		gridView.setAdapter(adapter);

		btn_preview.setOnClickListener(this);
	}

	// 记录是否是第一次进入该界面
	private boolean isFirstEnterThisActivity = true;
	private OnScrollListener onScrollListener = new OnScrollListener() {
		private int mFirstVisibleItem;
		private int mVisibleItemCount;

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
				ImageView imageView = (ImageView) gridView
						.findViewWithTag(imagePath);
				if (bitmap == null) {
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
					if (imageView != null && bitmap != null) {
						imageView.setImageBitmap(bitmap);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_preview:
			Intent intent = new Intent(this, PreviewActivity.class);
			intent.putStringArrayListExtra(PreviewActivity.PREVIEW_TAG, select_images);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
}
