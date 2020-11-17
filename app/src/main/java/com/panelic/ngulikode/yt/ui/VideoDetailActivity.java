package com.panelic.ngulikode.yt.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.panelic.ngulikode.R;
import com.panelic.ngulikode.comments.CommentsActivity;
import com.panelic.ngulikode.fav.FavDbAdapter;
import com.panelic.ngulikode.util.DetailActivity;
import com.panelic.ngulikode.util.Helper;
import com.panelic.ngulikode.util.WebHelper;
import com.panelic.ngulikode.yt.player.YouTubePlayerActivity;

/**
 * This activity is used to display the details of a video
 */

public class VideoDetailActivity extends DetailActivity {

	private FavDbAdapter mDbHelper;

	String date;
	String id;
	String title;
	String description;
	String favorite;
	String image;

	ImageLoader imageLoader;
	private TextView mPresentation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Use the general detaillayout and set the viewstub for youtube
		setContentView(R.layout.activity_details);
		ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
		stub.setLayoutResource(R.layout.activity_youtube_detail);
		View inflated = stub.inflate();

		mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		// getSupportActionBar().setBackgroundDrawable(new
		// ColorDrawable(Color.argb(128, 0, 0, 0)));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		mPresentation = (TextView) findViewById(R.id.youtubetitle);
		TextView detailsDescription = (TextView) findViewById(R.id.youtubedescription);
		TextView detailsSubTitle = (TextView) findViewById(R.id.youtubesubtitle);

		imageLoader = Helper.initializeImageLoader(VideoDetailActivity.this);
		detailsDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP,
				WebHelper.getWebViewFontSize(this));
		Bundle bundle = this.getIntent().getExtras();

		mPresentation.setText(bundle.getString("keyTitle"));
		detailsDescription.setText(bundle.getString("keyDescription"));
		
		String subText = getResources().getString(R.string.video_subtitle_start) + 
				bundle.getString("keyDate") + 
				getResources().getString(R.string.video_subtitle_end) + 
				bundle.getString("keyChannel");
		detailsSubTitle.setText(subText);

		title = (bundle.getString("keyTitle"));
		id = (bundle.getString("keyId"));
		date = (bundle.getString("keyDate"));
		description = (bundle.getString("keyDescription"));
		favorite = (bundle.getString("keyFavorites"));
		image = (bundle.getString("keyImage"));

		Helper.admobLoader(this, getResources(), findViewById(R.id.adView));

		thumb = (ImageView) findViewById(R.id.image);
		coolblue = (RelativeLayout) findViewById(R.id.coolblue);

		imageLoader.displayImage(image, thumb);

		setUpHeader(image);

		ImageButton btnPlay = (ImageButton) findViewById(R.id.playbutton);
		btnPlay.bringToFront();
		// Listening to button event
		btnPlay.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent(VideoDetailActivity.this,
						YouTubePlayerActivity.class);
				intent.putExtra(YouTubePlayerActivity.EXTRA_VIDEO_ID, id);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
			}
		});

		Button btnFav = (Button) findViewById(R.id.favorite);

		// Listening to button event
		btnFav.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				mDbHelper = new FavDbAdapter(VideoDetailActivity.this);
				mDbHelper.open();

				if (mDbHelper.checkEvent(title, description, date, id, "", "",
						"youtube")) {
					// Item is new
					mDbHelper.addFavorite(title, description, date, id, "", "",
							"youtube");
					Toast toast = Toast
							.makeText(VideoDetailActivity.this, getResources()
									.getString(R.string.favorite_success),
									Toast.LENGTH_LONG);
					toast.show();
				} else {
					Toast toast = Toast.makeText(
							VideoDetailActivity.this,
							getResources().getString(
									R.string.favorite_duplicate),
							Toast.LENGTH_LONG);
					toast.show();
				}
			}
		});

		Button btnComment = (Button) findViewById(R.id.comments);

		// Listening to button event
		btnComment.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				// Start NewActivity.class
				Intent commentIntent = new Intent(VideoDetailActivity.this,
						CommentsActivity.class);
				commentIntent.putExtra(CommentsActivity.DATA_TYPE,
						CommentsActivity.YOUTUBE);
				commentIntent.putExtra(CommentsActivity.DATA_ID,
						id);
				startActivity(commentIntent);
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.menu_share:
			String applicationName = getResources()
					.getString(R.string.app_name);
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);

			String urlvalue = getResources().getString(
					R.string.video_share_begin);
			String seenvalue = getResources().getString(
					R.string.video_share_middle);
			String appvalue = getResources()
					.getString(R.string.video_share_end);
			// this is the text that will be shared
			sendIntent.putExtra(Intent.EXTRA_TEXT, (urlvalue
					+ "http://youtube.com/watch?v=" + id + seenvalue
					+ applicationName + appvalue));
			sendIntent.putExtra(Intent.EXTRA_SUBJECT, title); // you can replace
																// title with a
																// string of
																// your choice
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent, getResources()
					.getString(R.string.share_header)));
			return true;
		case R.id.menu_view:
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("vnd.youtube:" + id));
				startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://www.youtube.com/watch?v=" + id));
				startActivity(intent);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.youtube_detail_menu, menu);
		return true;
	}

}
