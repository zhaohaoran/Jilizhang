package net.haoranzhao.jilizhang;

import android.app.ActionBar;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import net.haoranzhao.jilizhang.util.LocaleHelper;


public class TabMain extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_tab_main);

        ActionBar actionBar = getActionBar();
        //getOverflowMenu();
        actionBar.show();

        setTitle(getResources().getString(R.string.app_name));


        TabHost tabHost = getTabHost();  // The activity TabHost
        TabSpec spec;
        Intent intent;  // Reusable Intent for each tab

        String tabAllRecord = getResources().getString(R.string.tab_indicator_all_records);
        String tabByPerson = getResources().getString(R.string.tab_indicator_by_person);

        //第一个Tab
        intent = new Intent(TabMain.this,AllRecords.class);//新建一个Intent用作Tab1显示的内容
        spec = tabHost.newTabSpec("allRecords")//新建一个 Tab
                .setIndicator(tabAllRecord)//设置名称以及图标
                .setContent(intent);//设置显示的intent，这里的参数也可以是R.id.xxx
        tabHost.addTab(spec);//添加进tabHost
        //第一个Tab
        intent = new Intent(TabMain.this,ByPerson.class);//新建一个Intent用作Tab1显示的内容
        spec = tabHost.newTabSpec("byPerson")//新建一个 Tab
                .setIndicator(tabByPerson)//设置名称以及图标
                .setContent(intent);//设置显示的intent，这里的参数也可以是R.id.xxx
        tabHost.addTab(spec);//添加进tabHost

        tabHost.setCurrentTab(0);//设置当前的选项卡,这里为Tab1


    }
    @Override
    public void onResume(){
        super.onResume();
        String languageSetup = JilizhangApplication.sharedPreferencesHelper.getString(JilizhangApplication.languageKey);
        if(languageSetup!=null){
            LocaleHelper.setLocale(this,languageSetup);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tab_actionbar_manu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_new:
                Intent intent = new Intent(TabMain.this,AddRecord.class);
                startActivity(intent);
                return true;
            case R.id.action_export_by_email:
                Intent intent2 = new Intent(TabMain.this,ExportByEmail.class);
                startActivity(intent2);
                return true;
            case R.id.action_setup:

                Intent intent3 = new Intent(TabMain.this,SetupActivity.class);
                startActivity(intent3);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
