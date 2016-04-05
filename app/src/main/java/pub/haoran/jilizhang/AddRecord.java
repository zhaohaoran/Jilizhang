package pub.haoran.jilizhang;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import pub.haoran.jilizhang.util.DBHelper;


public class AddRecord extends Activity {

    public static final String DBNAME = "jilizhang.db";

    Button saveBtn;
    EditText personNameEditTxt;
    EditText amountEditTxt;
    Spinner selectPersonSpinner;
    RadioGroup fromWhomRG;
    RadioButton noFromMeRB;
    RadioButton fromMeRB;
    CheckBox addNewCheckBox;
    TextView addNewPersonTxt;
    TextView chooseAPersonTxt;

    String str;

    String PersonName;
    String amount;
    String[] allPersons;
    int ifFromMe = 0;

    DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_add_record);

        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.show();

        dbHelper = new DBHelper(AddRecord.this,DBNAME,null,1);

        addNewPersonTxt=(TextView)findViewById(R.id.addNewPersonTxt);
        addNewPersonTxt.setTextColor(Color.LTGRAY);
        chooseAPersonTxt=(TextView)findViewById(R.id.chooseAPersonTxt);

        selectPersonSpinner = (Spinner)findViewById(R.id.selectPersonSpinner);

        personNameEditTxt = (EditText)findViewById(R.id.personNameEdittxt);
        personNameEditTxt.setEnabled(false);

        addNewCheckBox = (CheckBox)findViewById(R.id.addNewCheckBox);

        addNewCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    selectPersonSpinner.setEnabled(false);
                    personNameEditTxt.setEnabled(true);
                    chooseAPersonTxt.setTextColor(Color.LTGRAY);
                    addNewPersonTxt.setTextColor(Color.BLACK);
                }else {
                    selectPersonSpinner.setEnabled(true);
                    personNameEditTxt.setEnabled(false);
                    addNewPersonTxt.setTextColor(Color.LTGRAY);
                    chooseAPersonTxt.setTextColor(Color.BLACK);
                }
            }
        });

        saveBtn = (Button)findViewById(R.id.savebtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = amountEditTxt.getText().toString();

                try{
                    BigDecimal amountBig = new BigDecimal(amount.trim());
                    if(addNewCheckBox.isChecked()){
                        PersonName = personNameEditTxt.getText().toString();
                    }
                    else{
                        PersonName =str;
                    }

                    Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
                    t.setToNow(); // 取得系统时间。
                    String time=t.year+"-"+(t.month+1)+"-"+t.monthDay;
                    //ifFromMe;
                    //need a sqlite insert operation
                    dbHelper.addRecord(PersonName,time,amount,ifFromMe);



                    AddRecord.this.finish();

                }catch (NumberFormatException e){
                    Toast.makeText(getApplicationContext(), "Amount format error, please have check.",
                            Toast.LENGTH_SHORT).show();
                }


            }
        });


        personNameEditTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_UNSPECIFIED) {
//                    Toast.makeText(AddRecord.this, "你点了软键盘回车按钮",
//                            Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });


         //Theme.Holo.Light
        selectPersonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                str=parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), str+" selected",
                        Toast.LENGTH_SHORT).show();            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        List<String> persons=new ArrayList<String>();

        SQLiteDatabase db = openOrCreateDatabase("jilizhang.db", Context.MODE_PRIVATE, null);
        //String args[] = {dbHelper.PERSON};
        dbHelper.onCreate(db);
        Cursor c = db.rawQuery("SELECT DISTINCT "+dbHelper.PERSON+" FROM "+dbHelper.TBNAME+" ORDER BY "+ dbHelper.PERSON, null); //执行本地SQL语句查询
        c.moveToFirst();
        while(!c.isAfterLast()){
            int index = c.getColumnIndex(dbHelper.PERSON);

            //Log.d("SQLite", c.getString(index));

            persons.add(c.getString(index));

            c.moveToNext();
        }


        //  建立Adapter绑定数据源
        MyAdapterSpinner _MyAdapter=new MyAdapterSpinner(this, persons);
        //绑定Adapter
        selectPersonSpinner.setAdapter(_MyAdapter);




        fromWhomRG = (RadioGroup)findViewById(R.id.fromWhomRG);
        fromWhomRG.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener(){
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        //获取变更后的选中项的ID
                        int radioButtonId = group.getCheckedRadioButtonId();
                        //根据ID获取RadioButton的实例
                        RadioButton rb = (RadioButton)AddRecord.this.findViewById(radioButtonId);
                        //更新文本内容，以符合选中项
                        //tv.setText("您的性别是：" + rb.getText());

                        if(radioButtonId == R.id.noFromMeRB){
                            ifFromMe = 0;
                            Toast.makeText(getApplicationContext(), "'he/she gives me' selected",
                                    Toast.LENGTH_SHORT).show();

                        }
                        else if(radioButtonId == R.id.fromMeRB){
                            ifFromMe = 1;
                            Toast.makeText(getApplicationContext(), "'I give him/her' selected",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

//        noFromMeRB = (RadioButton)findViewById(R.id.noFromMeRB);
//        fromMeRB = (RadioButton)findViewById(R.id.fromMeRB);
        amountEditTxt = (EditText)findViewById(R.id.amountEditTxt);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tab_actionbar_manu, menu);

        MenuItem item = menu.findItem(R.id.action_add_new);
        item.setVisible(false);
        this.invalidateOptionsMenu();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
