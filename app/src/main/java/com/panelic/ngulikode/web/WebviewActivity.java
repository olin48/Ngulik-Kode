package com.panelic.ngulikode.web;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.panelic.ngulikode.R;

public class WebviewActivity extends AppCompatActivity{

    private Toolbar mToolbar;
    
    public static String DATA = "transaction_data";
    public static String URL = "webview_url";
    
    String mWebUrl = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //opening the webview fragment with passed url
        if (getIntent().hasExtra(URL)){
        	mWebUrl = getIntent().getExtras().getString(URL);
        	openWebFragmentForUrl(mWebUrl);
        }

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
    
    
    public void openWebFragmentForUrl(String url){
		Fragment fragment;
		fragment = new WebviewFragment();
		
		// adding the data
		Bundle bundle = new Bundle();
		bundle.putString(DATA, url);
		fragment.setArguments(bundle);

		FragmentManager fragmentManager = getSupportFragmentManager();

		fragmentManager.beginTransaction().replace(R.id.container, fragment)
				.commit();

		setTitle(getResources().getString(R.string.webview_title));
    }
    
    @Override
    public void onBackPressed() {
    	Fragment webview = getSupportFragmentManager().findFragmentById(R.id.container);
    	
        if (webview instanceof WebviewFragment) {
        	boolean goback = ((WebviewFragment)webview).canGoBack();
        	if (!goback)
        		super.onBackPressed();
        } else {         
        	super.onBackPressed();
        }
    }
}