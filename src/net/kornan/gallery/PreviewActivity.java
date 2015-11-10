package net.kornan.gallery;

import java.util.ArrayList;
import java.util.List;

import net.kornan.gallery.BitmapCache.ImageCallback;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

public class PreviewActivity extends Activity {
	public static final String PREVIEW_TAG = "paths";
	private ArrayList<View> listViews = null;
	private ViewPager pager;
	private PreViewPageAdapter adapter;
	private int count = 0;
	public List<String> drr = new ArrayList<String>();
	public int max;

	private void initView() {
		Button photo_bt_exit = (Button) findViewById(R.id.photo_bt_exit);
		photo_bt_exit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		Button photo_bt_enter = (Button) findViewById(R.id.photo_bt_enter);
		photo_bt_enter.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});
		pager = (ViewPager) findViewById(R.id.viewpager);
		pager.addOnPageChangeListener(pageChangeListener);
		listViews = new ArrayList<View>();
	}

	private void initData() {
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		try {
			ArrayList<String> paths = getIntent().getStringArrayListExtra(
					PREVIEW_TAG);
			if (paths != null) {
				drr.addAll(paths);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < drr.size(); i++) {
			ImageView img = new ImageView(this);
//			img.setBackgroundColor(0xff000000);
			BitmapCache.getInstance().displayBmp(img, metric.widthPixels,
					metric.heightPixels, drr.get(i), new ImageCallback() {

						@Override
						public void imageLoad(ImageView imageView,
								Bitmap bitmap, Object... params) {
							// TODO Auto-generated method stub
							imageView.setImageBitmap(bitmap);
						}
					});
			// String path = Bimp.drr.get(i);
			// if ("http:".equals(path.substring(0, 5))) {
			// ImageLoader.getInstance().displayImage(path.toString(), img);
			// } else {
			// Uri uri = Uri.fromFile(new File(path));
			// ImageLoader.getInstance().displayImage(uri.toString(), img);
			// }
			img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
			listViews.add(img);// 添加view
		}
		adapter = new PreViewPageAdapter(listViews);
		pager.setAdapter(adapter);
		pager.setCurrentItem(0);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview);
		initView();
		initData();
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			count = arg0;
		}
	};

	class PreViewPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;

		private int size;

		public PreViewPageAdapter(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public int getCount() {
			return size;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {// �?��view对象
			((ViewPager) arg0).removeView(listViews.get(arg1 % size));
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int arg1) {// 返回view对象
			try {
				((ViewPager) arg0).addView(listViews.get(arg1 % size), 0);

			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
}
