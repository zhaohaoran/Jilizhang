package net.haoranzhao.jilizhang;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.haoranzhao.jilizhang.util.LocaleHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetupActivity extends Activity {
    //private static String[] languageArray={"English","Simplified Chinese"};
    private List<String> languages;
    private List<String> languagesSystem;

    private Spinner selectLanguageSpinner;
    private Button saveBtn;

    private String selectedLang;
    private String[] languagesArray;
    private String[] languagesArraySystem;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_setup_activity);

        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.show();

//        String languageSetup = JilizhangApplication.sharedPreferencesHelper.getString(JilizhangApplication.languageKey);
//        if(languageSetup!=null){
//            LocaleHelper.setLocale(this,languageSetup);
//        }

        selectedLang = "en";

        languagesArray = getResources().getStringArray(R.array.language_array_readable);
        languagesArraySystem = getResources().getStringArray(R.array.language_array_system);

        //list here is used for spinner
        languages = new ArrayList<String>(Arrays.asList(languagesArray));
        languagesSystem = new ArrayList<String>(Arrays.asList(languagesArraySystem));

        selectLanguageSpinner = (Spinner)findViewById(R.id.selectLanguageSpinner);

        selectLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String language=parent.getItemAtPosition(position).toString();

                String selected = getResources().getString(R.string.toast_name_select);

                //may cause hard of maintenance because of the position
                selectedLang = languagesArraySystem[position];

                Toast.makeText(getApplicationContext(), language+" "+selected,
                        Toast.LENGTH_SHORT).show();            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        //  建立Adapter绑定数据源
        MyAdapterSpinner _MyAdapter=new MyAdapterSpinner(this, languages);
        //绑定Adapter
        selectLanguageSpinner.setAdapter(_MyAdapter);


        String languageSetup = JilizhangApplication.sharedPreferencesHelper.getString(JilizhangApplication.languageKey);
        if(languageSetup!=null){
            selectLanguageSpinner.setSelection(languagesSystem.indexOf(languageSetup));
            LocaleHelper.setLocale(this,languageSetup);
            setTitle(getResources().getString(R.string.title_activity_language_setup));
        }


        saveBtn = (Button)findViewById(R.id.savebtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JilizhangApplication.sharedPreferencesHelper.putStringOnce(JilizhangApplication.languageKey, selectedLang);
                LocaleHelper.setLocale(getApplicationContext(),selectedLang);

                SetupActivity.this.finish();

                Intent intent3 = new Intent(SetupActivity.this,TabMain.class);
                startActivity(intent3);

            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();

    }



    public class MyAdapterSpinner extends BaseAdapter {
        private List<String> mList;
        private Context mContext;

        public MyAdapterSpinner(Context pContext, List<String> nList) {
            this.mContext = pContext;
            this.mList = nList;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * 下面是重要代码
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater _LayoutInflater=LayoutInflater.from(mContext);
            convertView=_LayoutInflater.inflate(R.layout.spinner_item, null);
            if(convertView!=null)
            {
                TextView _TextView1=(TextView)convertView.findViewById(R.id.personNameSpinnerItem);
                _TextView1.setText(mList.get(position));
                //_TextView1.setSelected(true);
            }
            return convertView;
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.tab_actionbar_manu, menu);
//
//        MenuItem item = menu.findItem(R.id.action_add_new);
//        item.setVisible(false);
//        this.invalidateOptionsMenu();
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                Intent intent3 = new Intent(SetupActivity.this,TabMain.class);
                startActivity(intent3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
