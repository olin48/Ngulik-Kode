package com.panelic.ngulikode.facebook;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.panelic.ngulikode.R;
import com.panelic.ngulikode.comments.CommentsActivity;
import com.panelic.ngulikode.util.Helper;
import com.panelic.ngulikode.util.MediaActivity;
import com.panelic.ngulikode.util.WebHelper;

import java.util.List;
import java.util.Locale;

public class FacebookAdapter  extends ArrayAdapter<FacebookItem>{
	
	private Context context;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

    public FacebookAdapter(Context context, List<FacebookItem> objects) {
        super(context, 0, objects);
    	this.context = context;
		imageLoader = Helper.initializeImageLoader(context);
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.placeholder)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.build();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final FacebookItem post = getItem(position);
        final FacebookItemViewHolder viewHolder;
        
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_facebook_row, parent, false);

            viewHolder = new FacebookItemViewHolder();
            viewHolder.profilePicImg = (ImageView) convertView.findViewById(R.id.profile_image);
            viewHolder.userNameView = (TextView)convertView.findViewById(R.id.name);
            viewHolder.dateView = (TextView)convertView.findViewById(R.id.date);
            viewHolder.inlineImg = (ImageView)convertView.findViewById(R.id.photo);
            viewHolder.inlineImgBtn = (ImageButton)convertView.findViewById(R.id.playbutton);
            viewHolder.likesCountView = (TextView)convertView.findViewById(R.id.like_count);
            viewHolder.contentView = (TextView)convertView.findViewById(R.id.message);
            viewHolder.shareBtn = (Button) convertView.findViewById(R.id.share);
            viewHolder.openBtn = (Button) convertView.findViewById(R.id.open);
            viewHolder.commentsBtn = (Button) convertView.findViewById(R.id.comments);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (FacebookItemViewHolder)convertView.getTag();
        }

        viewHolder.profilePicImg.setImageDrawable(null);
        imageLoader.displayImage(post.profilePhotoUrl, viewHolder.profilePicImg);

        String userNameView  = post.username.substring(0,1).toUpperCase(Locale.getDefault()) + post.username.substring(1).toLowerCase(Locale.getDefault());
        viewHolder.userNameView.setText(userNameView);

        viewHolder.dateView.setText(DateUtils.getRelativeDateTimeString(context,post.createdTime.getTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL));

        viewHolder.inlineImg.setImageDrawable(null);
        imageLoader.displayImage(post.imageUrl,viewHolder.inlineImg, options);
        viewHolder.inlineImg.setTag(position);
        Log.v("POST TYPE",post.type);
        if (post.type.equals("video")){
        	viewHolder.inlineImgBtn.setVisibility(View.VISIBLE);
        } else {
        	viewHolder.inlineImgBtn.setVisibility(View.GONE);
        }
        
        Log.v("INFO", "Position: " + position);

        if (post.type.equals("photo")){
            viewHolder.inlineImg.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {

	                Intent commentIntent = new Intent(context, MediaActivity.class);
	                commentIntent.putExtra(MediaActivity.TYPE, MediaActivity.TYPE_IMG);
	                commentIntent.putExtra(MediaActivity.URL, getItem((int) viewHolder.inlineImg.getTag()).imageUrl);
	                context.startActivity(commentIntent);
                }
            });
        }
        else if (post.type.equals("video")) {	
        	View.OnClickListener videoListener = new View.OnClickListener() {
        		public void onClick(View arg0) {

        			Intent commentIntent = new Intent(context, MediaActivity.class);
        			commentIntent.putExtra(MediaActivity.TYPE, MediaActivity.TYPE_VID);
        			commentIntent.putExtra(MediaActivity.URL, getItem((int) viewHolder.inlineImg.getTag()).videoUrl);
                	context.startActivity(commentIntent);
        		}
            };
            
            viewHolder.inlineImgBtn.setOnClickListener(videoListener);
            viewHolder.inlineImg.setOnClickListener(videoListener);
        }

        viewHolder.likesCountView.setText(Helper.formatValue(post.likesCount));

        viewHolder.contentView.setText(Html.fromHtml(post.caption.replace("\n", "<br>")));
        viewHolder.contentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, WebHelper.getTextViewFontSize(context));
        
        viewHolder.shareBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

    			Intent sendIntent = new Intent();
    			sendIntent.setAction(Intent.ACTION_SEND);
    			
    			// this is the text that will be shared
    			sendIntent.putExtra(Intent.EXTRA_TEXT,post.link);
    			
    			sendIntent.setType("text/plain");
    			context.startActivity(Intent.createChooser(sendIntent, context.getResources()
    					.getString(R.string.share_header)));
            }
        });
		
        viewHolder.openBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
            	final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(post.link));
            	context.startActivity(intent);
            }
        });

        viewHolder.commentsBtn.setText(Helper.formatValue(post.commentsCount) + " " + context.getResources().getString(R.string.comments));
        viewHolder.commentsBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent commentIntent = new Intent(getContext(), CommentsActivity.class);
                commentIntent.putExtra(CommentsActivity.DATA_ARRAY, post.commentsArray.toString());
                commentIntent.putExtra(CommentsActivity.DATA_TYPE, CommentsActivity.FACEBOOK);
                getContext().startActivity(commentIntent);
            }
        });

        return convertView;
    }

    
    class FacebookItemViewHolder {
        ImageView profilePicImg;

        TextView userNameView;
        TextView dateView;
        ImageView inlineImg;
        ImageButton inlineImgBtn;
        TextView likesCountView;
        TextView contentView;

        Button shareBtn;
        Button openBtn;
        Button commentsBtn;
    }
}
