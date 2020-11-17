package com.panelic.ngulikode;

import androidx.fragment.app.Fragment;

public class NavItem {
	public static int ITEM = 1;
	public static int SECTION = 2;
	public static int EXTRA = 3;
	public static int TOP = 4;
	
    private String mText;
    private int mDrawable;
    private String mData;
    private int mType;
    private boolean mRequirePurchase;
    private Class<? extends Fragment> mFragment;

    public NavItem(String text, int drawable, int type, Class<? extends Fragment> fragment, String data) {
        mText = text;
        mDrawable = drawable;
        mFragment = fragment;
        mData = data;
        mType = type;
        mRequirePurchase = false;
    }
    
    public NavItem(String text, int drawable, int type, Class<? extends Fragment> fragment, String data, boolean requiresPurchase) {
        mText = text;
        mDrawable = drawable;
        mFragment = fragment;
        mData = data;
        mType = type;
        mRequirePurchase = requiresPurchase;
    }
    
    public NavItem(String text, int type) {
    	mText = text;
        mType = type;
        mRequirePurchase = false;
    }

    public String getText() {
        return mText;
    }

    public int getDrawable() {
        return mDrawable;
    }
    
    public Class<? extends Fragment> getFragment() {
    	return mFragment;
    }
    
    public String getData() {
    	return mData;
    }
    
    public int getType(){
    	return mType;
    }
    
    public boolean requiresPurchase(){
    	return mRequirePurchase;
    }
}
