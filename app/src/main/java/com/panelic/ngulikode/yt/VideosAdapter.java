package com.panelic.ngulikode.yt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.panelic.ngulikode.R;
import com.panelic.ngulikode.util.Helper;

import java.util.List;

/**
 * Setting our custom listview rows with the retrieved videos
 */
public class VideosAdapter extends  ArrayAdapter<Video> {

	List<Video> videos;

	private LayoutInflater mInflater;	
	@SuppressWarnings("unused")
	private ListView mListView;
	private Context mContext;
	private String TAG_TOP = "TOP";
	private DisplayImageOptions options;

	public VideosAdapter(Context context, Integer something, List<Video> videos, ListView listView) {
		super(context, something, videos);
		this.mContext = context;
		this.videos = videos;
		this.mInflater = LayoutInflater.from(context);
		this.mListView = listView;
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.placeholder)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.build();
	}

	@Override
	public int getCount() {
		return videos.size();
	}

	@Override
	public Video getItem(int position) {
		return videos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final Video video = videos.get(position);
		ImageLoader imageLoader = Helper.initializeImageLoader(mContext);
		
		//if it is the first item, give a special treatment.
		if (position == 0 && null != video){
			convertView = mInflater.inflate(R.layout.listview_highlight, null);
			imageLoader.displayImage(video.getImage(), (ImageView) convertView.findViewById(R.id.imageViewHighlight), options);
			((TextView) convertView.findViewById(R.id.textViewHighlight)).setText(video.getTitle());
			convertView.setTag(TAG_TOP);
			return convertView;
		}
		
		ViewHolder holder;

		if(convertView == null || convertView.getTag().equals(TAG_TOP)){
		    convertView = mInflater.inflate(R.layout.fragment_youtube_row, null);
		
		    holder = new ViewHolder();
		    holder.title= (TextView) convertView.findViewById(R.id.userVideoTitleTextView);
		    holder.date = (TextView) convertView.findViewById(R.id.userVideoDateTextView);
		    holder.thumb =(ImageView) convertView.findViewById(R.id.userVideoThumbImageView);
		    convertView.setTag(holder);
		
		} else {
            holder = (ViewHolder) convertView.getTag();
        }
		
		holder.thumb.setImageDrawable(null);

		if(video != null){

			// Set the title for the video
			holder.title.setText(video.getTitle());
			// Set the date for the video
			holder.date.setText(video.getUpdated());
			
		    //setting the image
			imageLoader.displayImage(video.getThumbUrl(), holder.thumb, options);

		}
		return convertView;
	
	}

	static class ViewHolder {
		  TextView title;
		  TextView date;
		  ImageView thumb;
		  int position;
    }

}