package net.haoranzhao.jilizhang;

import android.app.Activity;
import android.os.Bundle;

import net.haoranzhao.jilizhang.util.LocaleHelper;

public class CloudBackup extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_backup);


    }

    @Override
    public void onResume(){
        super.onResume();
        String languageSetup = JilizhangApplication.sharedPreferencesHelper.getString(JilizhangApplication.languageKey);
        if(languageSetup!=null){
            LocaleHelper.setLocale(this,languageSetup);
        }
    }

}
