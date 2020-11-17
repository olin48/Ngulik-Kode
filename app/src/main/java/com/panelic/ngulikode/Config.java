package com.panelic.ngulikode;

import com.panelic.ngulikode.facebook.FacebookFragment;
import com.panelic.ngulikode.fav.ui.FavFragment;
import com.panelic.ngulikode.rss.ui.RssFragment;
import com.panelic.ngulikode.web.WebviewFragment;
import com.panelic.ngulikode.yt.ui.VideosFragment;

import java.util.ArrayList;
import java.util.List;

public class Config {

	public static List<NavItem> configuration() {

		List<NavItem> i = new ArrayList<NavItem>();

		//DONT MODIFY ABOVE THIS LINE

        //Some sample content is added below, please refer to your documentation for more information about configuring this file properly
        i.add(new NavItem("Home", R.drawable.ic_home, NavItem.ITEM, RssFragment.class, "http://www.ngulikode.com/feeds/posts/default?alt=rss"));
        i.add(new NavItem("Blog", NavItem.SECTION));
        i.add(new NavItem("Android Tutorial", R.drawable.ic_android, NavItem.ITEM, RssFragment.class, "http://www.ngulikode.com/feeds/posts/default/-/Android?alt=rss"));
        i.add(new NavItem("Information", R.drawable.ic_reader, NavItem.ITEM, RssFragment.class, "http://www.ngulikode.com/feeds/posts/default/-/Info?alt=rss"));
        i.add(new NavItem("Review Apps", R.drawable.ic_eye, NavItem.ITEM, RssFragment.class, "http://www.ngulikode.com/feeds/posts/default/-/Review%20Apps?alt=rss"));
        i.add(new NavItem("Tips Blogger", R.drawable.ic_tips_blog, NavItem.ITEM, RssFragment.class, "http://www.ngulikode.com/feeds/posts/default/-/Tips%20Blogger?alt=rss"));
        i.add(new NavItem("Tips & Trik", R.drawable.ic_tips_trik, NavItem.ITEM, RssFragment.class, "http://www.ngulikode.com/feeds/posts/default/-/Tips%20%26%20Trik?alt=rss"));

        i.add(new NavItem("Web Tools", NavItem.SECTION));
        i.add(new NavItem("Ad Converter", R.drawable.ic_details, NavItem.ITEM, WebviewFragment.class, "file:///android_asset/ad_converter.html"));
        i.add(new NavItem("Flat UI Design", R.drawable.ic_details, NavItem.ITEM, WebviewFragment.class, "file:///android_asset/flat.html"));
        i.add(new NavItem("Kamus HTML", R.drawable.ic_details, NavItem.ITEM, WebviewFragment.class, "file:///android_asset/kamus.html"));

        i.add(new NavItem("Social Network", NavItem.SECTION));
        i.add(new NavItem("Facebook", R.drawable.ic_facebook, NavItem.ITEM, FacebookFragment.class, "1612712408963926"));
        i.add(new NavItem("YouTube", R.drawable.ic_youtube, NavItem.ITEM, VideosFragment.class, "UCDcZF3d1cTRKCQjO9DOmIIg"));

        //It's suggested to not change the content below this line
        i.add(new NavItem("Tools", NavItem.SECTION));
//        i.add(new NavItem("Contact", R.drawable.ic_email, NavItem.EXTRA, SendFragment.class, null));
        i.add(new NavItem("Favorites", R.drawable.ic_favorite, NavItem.EXTRA, FavFragment.class, null));
        i.add(new NavItem("Settings", R.drawable.ic_settings, NavItem.EXTRA, SettingsFragment.class, null));
        
        //DONT MODIFY BELOW THIS LINE
        
        return i;
			
	}
	
}