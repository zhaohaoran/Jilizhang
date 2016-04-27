package net.haoranzhao.jilizhang;

import android.app.Application;

import net.haoranzhao.jilizhang.util.LocaleHelper;
import net.haoranzhao.jilizhang.util.SharedPreferencesHelper;

/**
 * Created by zhaohaoran on 4/25/16.
 */
public class JilizhangApplication extends Application{

    public static SharedPreferencesHelper sharedPreferencesHelper;
    //public LocaleHelper localeHelper;
    private String sharedpreferenceName = "SettingPreferences";
    public static String languageKey = "Language";

    @Override
    public void onCreate(){
        sharedPreferencesHelper = new SharedPreferencesHelper(getApplicationContext(),sharedpreferenceName);
        super.onCreate();
        LocaleHelper.onCreate(this,"en");
    }

}
