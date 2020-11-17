package com.panelic.ngulikode.comments;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.panelic.ngulikode.R;
import com.panelic.ngulikode.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CommentsActivity extends AppCompatActivity {

	ArrayList<Comment> comments;
	ArrayAdapter<Comment> commentsAdapter;
	int type;
	String id;

	private Toolbar mToolbar;

	public static String DATA_ARRAY = "array";
	public static String DATA_TYPE = "type";
	public static String DATA_ID = "id";
	public static int INSTAGRAM = 1;
	public static int FACEBOOK = 2;
	public static int YOUTUBE = 3;
	public static int WORDPRESS = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comments);
		mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(getResources().getString(R.string.comments));

		Helper.admobLoader(this,getResources(), findViewById(R.id.adView));

		Bundle extras = getIntent().getExtras();
		String jsonArray = extras.getString(DATA_ARRAY);
		type = extras.getInt(DATA_TYPE);
		id = extras.getString(DATA_ID);

		comments = new ArrayList<>();
		commentsAdapter = new CommentsAdapter(this, comments, type);

		ListView lvComments = (ListView) findViewById(R.id.listView);
		lvComments.setAdapter(commentsAdapter);
		lvComments.setEmptyView(findViewById(R.id.empty));

		commentsAdapter.notifyDataSetChanged();

		// Fetch other comments
		fetchComments(jsonArray);
	}

	private void fetchComments(final String jsonArray) {
		 if (type == FACEBOOK) {
			try {
				JSONArray dataJsonArray = new JSONArray(jsonArray);
				for (int i = 0; i < dataJsonArray.length(); i++) {
					JSONObject commentJson = dataJsonArray.getJSONObject(i);
					Comment comment = new Comment();
					comment.text = commentJson.getString("message");
					comment.username = commentJson.getJSONObject("from")
							.getString("name");
					comment.profileUrl = "https://graph.facebook.com/"
							+ commentJson.getJSONObject("from").getString("id")
							+ "/picture?type=large";
					comments.add(comment);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (type == YOUTUBE) {
			final String url = "https://www.googleapis.com/youtube/v3/commentThreads?part=snippet&maxResults=100&videoId="
					+ id
					+ "&key="
					+ getResources().getString(R.string.google_server_key);
			((TextView) findViewById(R.id.empty)).setText(getResources()
					.getString(R.string.loading));
			
			Log.v("INFO", url);
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						JSONObject response = getJSONFromUrl(url);
						JSONArray dataJsonArray = response
								.getJSONArray("items");
						for (int i = 0; i < dataJsonArray.length(); i++) {
							JSONObject commentJson = dataJsonArray
									.getJSONObject(i);
							if (commentJson.getJSONObject("snippet").has(
									"topLevelComment")) {
								JSONObject innerSnippet = commentJson
										.getJSONObject("snippet")
										.getJSONObject("topLevelComment")
										.getJSONObject("snippet");
								Comment comment = new Comment();
								comment.text = innerSnippet
										.getString("textDisplay");
								comment.username = innerSnippet
										.getString("authorDisplayName");
								comment.profileUrl = innerSnippet
										.getString("authorProfileImageUrl");
								comments.add(comment);
							}

						}

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								commentsAdapter.notifyDataSetChanged();
								((TextView) findViewById(R.id.empty))
										.setText(getResources().getString(
												R.string.no_results));
							}
						});

					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}).start();

		}
		commentsAdapter.notifyDataSetChanged();
	}

	private int checkIfContains(int parentId, int linesCount) {
		for (int a = 0; a < comments.size(); a++) {
			Log.v("INFO", "AddedCommentId: " + comments.get(a).id + " ParentId: " + parentId);
			if (comments.get(a).id == parentId){
				Log.v("INFO", "So, this is the result");
				return a;
			}
		}
		return -1;
	}

	public JSONObject getJSONFromUrl(String url) {
		JSONObject jObj = null;
		String json = null;

		// Making HTTP request
		StringBuffer chaine = new StringBuffer("");
		try {
			URL urlCon = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) urlCon
					.openConnection();
			connection.setRequestProperty("User-Agent", "");
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			connection.connect();

			InputStream inputStream = connection.getInputStream();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					inputStream));
			String line = "";
			while ((line = rd.readLine()) != null) {
				chaine.append(line);
			}

			json = chaine.toString();

			jObj = new JSONObject(json);

		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
		} catch (JSONException e) {
			// writing exception to log
			e.printStackTrace();
		}

		return jObj;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
