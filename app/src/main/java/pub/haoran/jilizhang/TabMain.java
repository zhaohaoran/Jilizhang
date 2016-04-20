package pub.haoran.jilizhang;

import android.app.ActionBar;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;


public class TabMain extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_tab_main);

        ActionBar actionBar = getActionBar();
        //getOverflowMenu();
        actionBar.show();

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabSpec spec;
        Intent intent;  // Reusable Intent for each tab


        //第一个Tab
        intent = new Intent(TabMain.this,AllRecords.class);//新建一个Intent用作Tab1显示的内容
        spec = tabHost.newTabSpec("allRecords")//新建一个 Tab
                .setIndicator("All records")//设置名称以及图标
                .setContent(intent);//设置显示的intent，这里的参数也可以是R.id.xxx
        tabHost.addTab(spec);//添加进tabHost
        //第一个Tab
        intent = new Intent(TabMain.this,ByPerson.class);//新建一个Intent用作Tab1显示的内容
        spec = tabHost.newTabSpec("byPerson")//新建一个 Tab
                .setIndicator("Check by Person")//设置名称以及图标
                .setContent(intent);//设置显示的intent，这里的参数也可以是R.id.xxx
        tabHost.addTab(spec);//添加进tabHost

        tabHost.setCurrentTab(0);//设置当前的选项卡,这里为Tab1


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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
