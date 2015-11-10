package net.kornan.gallery;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;

public class ImagesAdapter extends BaseAdapter {
	private List<ImageItem> dataList;
	private BitmapCache cache;
	private Handler mHandler;
	private Context context;
	public GridView mGridView;

	public ImagesAdapter(Context context, List<ImageItem> list, Handler mHandler) {
		this.context = context;
		this.dataList = list;
		this.mHandler = mHandler;
		// cache = new NativeImageLoader();
		cache = BitmapCache.getInstance();
	}

	@Override
	public int getCount() {
		int count = 0;
		if (dataList != null) {
			count = dataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	class Holder {
		ImageView imageView;
		CheckBox checkBox;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;

		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(context, R.layout.item_image, null);
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.imageView1);
			holder.checkBox = (CheckBox) convertView
					.findViewById(R.id.checkBox1);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		ImageItem item = dataList.get(position);
		holder.imageView.setTag(item.imagePath);
		setImageViewForCache(item.imagePath, holder.imageView);
		// BitmapCache.getInstance().displayBmp(holder.imageView,
		// item.thumbnailPath, item.imagePath, new ImageCallback() {
		//
		// @Override
		// public void imageLoad(ImageView imageView, Bitmap bitmap, Object...
		// params) {
		// // TODO Auto-generated method stub
		// imageView.setImageBitmap(bitmap);
		// }
		// });
		holder.checkBox.setOnCheckedChangeListener(null);
		holder.checkBox.setChecked(item.isSelected);
		holder.checkBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						dataList.get(position).isSelected = isChecked;
					}
				});
		return convertView;
	}

	private void setImageViewForCache(String imageUrl, ImageView imageView) {
		Bitmap bitmap = cache.getBitmapFromLruCache(imageUrl);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			imageView.setImageResource(R.drawable.img_bg);
		}
	}
}
