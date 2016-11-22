package com.panelic.ngulikode.rss.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.panelic.ngulikode.R;
import com.panelic.ngulikode.fav.FavDbAdapter;
import com.panelic.ngulikode.util.DetailActivity;
import com.panelic.ngulikode.util.Helper;
import com.panelic.ngulikode.util.MediaActivity;
import com.panelic.ngulikode.util.WebHelper;
import com.panelic.ngulikode.web.WebviewActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

/**
 *  This activity is used to display details of a rss item
 */

public class RssDetailActivity extends DetailActivity {

	private WebView wb;
	private FavDbAdapter mDbHelper;

	ImageLoader imageLoader;

	String date;
	String link;
	String title;
	String description;
	String favorite;

	@SuppressLint("SetJavaScriptEnabled")@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Use the general detaillayout and set the viewstub for wordpress
		setContentView(R.layout.activity_details);
		ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
		stub.setLayoutResource(R.layout.activity_rss_details);
		View inflated = stub.inflate();

		mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		imageLoader = Helper.initializeImageLoader(this);

		thumb = (ImageView) findViewById(R.id.image);
		coolblue = (RelativeLayout) findViewById(R.id.coolblue);
        
		TextView detailsTitle = (TextView) findViewById(R.id.detailstitle);
		TextView detailsPubdate = (TextView) findViewById(R.id.detailspubdate);

		Bundle bundle = this.getIntent().getExtras();

		detailsTitle.setText(bundle.getString("keyTitle"));
		detailsPubdate.setText(bundle.getString("keyPubdate"));
		date = (bundle.getString("keyPubdate"));
		link = (bundle.getString("keyLink"));
		title = (bundle.getString("keyTitle"));
		description = (bundle.getString("keyDescription"));
		favorite = (bundle.getString("keyFavorites"));

		setUpHeader(null);

		wb = (WebView) findViewById(R.id.descriptionwebview);

		//parse the html and apply some styles
		Document doc = Jsoup.parse(description);
		String html = WebHelper.docToBetterHTML(doc, this);;

		wb.getSettings().setJavaScriptEnabled(true);
		wb.loadDataWithBaseURL(link, html , "text/html", "UTF-8", "");
		Log.v("INFO", "Wordpress HTML: " + html);
		wb.setBackgroundColor(Color.argb(1, 0, 0, 0));
		wb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		wb.getSettings().setDefaultFontSize(WebHelper.getWebViewFontSize(this));
		wb.setWebViewClient(new WebViewClient(){
		    public boolean shouldOverrideUrlLoading(WebView view, String url) {
		    	if (url != null
						&& (url.endsWith(".png") || url
								.endsWith(".jpg")|| url
								.endsWith(".jpeg"))) {
                	Intent commentIntent = new Intent(RssDetailActivity.this, MediaActivity.class);
                	commentIntent.putExtra(MediaActivity.TYPE, MediaActivity.TYPE_IMG);
                	commentIntent.putExtra(MediaActivity.URL, url);
                	startActivity(commentIntent);
                	return true;
		    	} else if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
		        	Intent mIntent = new Intent(RssDetailActivity.this, WebviewActivity.class);
		        	mIntent.putExtra(WebviewActivity.URL, url);
		        	startActivity(mIntent);
		            return true;
		        } else {
		        	Uri uri = Uri.parse(url);
		        	Intent ViewIntent = new Intent(Intent.ACTION_VIEW, uri);

		        	// Verify it resolves
		        	PackageManager packageManager = getPackageManager();
		        	List<ResolveInfo> activities = packageManager.queryIntentActivities(ViewIntent, 0);
		        	boolean isIntentSafe = activities.size() > 0;

		        	// Start an activity if it's safe
		        	if (isIntentSafe) {
		        	    startActivity(ViewIntent);
		        	}
		        	return true;
		        }
		    }
		});

		Helper.admobLoader(this, getResources(), findViewById(R.id.adView));
		
		Button btnMedia = (Button) findViewById(R.id.mediabutton);
		//String imageUrl = (bundle.getString("keyThumbnail"));
		final String videoUrl = (bundle.getString("keyVideo"));
		final String audioUrl = (bundle.getString("keyAudio"));

		if (videoUrl != null)
			btnMedia.setText(getResources().getString(R.string.btn_video));
		else if (audioUrl != null)
			btnMedia.setText(getResources().getString(R.string.btn_audio));
		else
			btnMedia.setVisibility(View.GONE);
		
		//Listening to button event
		btnMedia.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0){
				Intent commentIntent = new Intent(RssDetailActivity.this, MediaActivity.class);
				if (videoUrl != null){
					commentIntent.putExtra(MediaActivity.TYPE, MediaActivity.TYPE_VID);
					commentIntent.putExtra(MediaActivity.URL, videoUrl);
				} else if (audioUrl != null){
					commentIntent.putExtra(MediaActivity.TYPE, MediaActivity.TYPE_AUDIO);
	                commentIntent.putExtra(MediaActivity.URL, audioUrl);
				}
                startActivity(commentIntent);
			}
		});
		
		Button btnOpen = (Button) findViewById(R.id.openbutton);

		//Listening to button event
		btnOpen.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse(link));
				startActivity(intent);

			}
		});

		Button btnFav = (Button) findViewById(R.id.favoritebutton);

		//Listening to button event
		btnFav.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				mDbHelper = new FavDbAdapter(RssDetailActivity.this);
				mDbHelper.open();

				if (mDbHelper.checkEvent(title, description, date, link, "", "", "rss")) {
					// Item is new
					mDbHelper.addFavorite(title, description, date, link, "", "", "rss");
					Toast toast = Toast.makeText(RssDetailActivity.this, getResources().getString(R.string.favorite_success), Toast.LENGTH_LONG);
					toast.show();
				} else {
					Toast toast = Toast.makeText(RssDetailActivity.this, getResources().getString(R.string.favorite_duplicate), Toast.LENGTH_LONG);
					toast.show();
				}
			}
		});


	}
	
	@Override
	public void onPause(){
		super.onPause();
		wb.onPause();
	}

	@Override
	public void onResume(){
		super.onResume();
		wb.onResume();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.menu_share:
				String html = description;
				html = html.replaceAll("<(.*?)\\>", ""); //Removes all items in brackets
				html = html.replaceAll("<(.*?)\\\n", ""); //Must be undeneath
				html = html.replaceFirst("(.*?)\\>", ""); //Removes any connected item to the last bracket
				html = html.replaceAll("&nbsp;", "");
				html = html.replaceAll("&amp;", "");

				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				//this is the text that will be shared
				sendIntent.putExtra(Intent.EXTRA_TEXT, (html + "\n" + link));
				sendIntent.putExtra(Intent.EXTRA_SUBJECT, title); //you can replace title with a string of your choice
				sendIntent.setType("text/plain");
				startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.share_header)));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.rss_detail_menu, menu);
		return true;
	}

}