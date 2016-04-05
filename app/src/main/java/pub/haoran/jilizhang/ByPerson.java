package pub.haoran.jilizhang;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.haoran.jilizhang.util.DBHelper;


public class ByPerson extends Activity {
    private List<Map<String, Object>> mData;
    private ListView lv;
    private TextView Txtv;
    DBHelper dbHelper;
    String str;
    Spinner selectPersonSpinner;
    public static final String DBNAME = "jilizhang.db";
    SQLiteDatabase db;
    List<Map<String, Object>> list;
    TextView who_owe_you;
    MyAdapter adapter;
    MyAdapterSpinner _MyAdapter;
    List<String> persons;

    BigDecimal sumBD;
    BigDecimal next = BigDecimal.ZERO;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_by_person);

        who_owe_you = (TextView) findViewById(R.id.who_owe_you);

        dbHelper = new DBHelper(ByPerson.this, DBNAME, null, 1);
        db = openOrCreateDatabase("jilizhang.db", Context.MODE_PRIVATE, null);
        dbHelper.onCreate(db);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onResume() {
        super.onResume();
        List<String> fileList = new ArrayList<String>();
        list = new ArrayList<Map<String, Object>>();

        lv = (ListView) findViewById(R.id.MyListView);
        Txtv = (TextView) findViewById(R.id.NoRecordTxt);

//        lv.setVisibility(View.INVISIBLE);
//        Txtv.setVisibility(View.VISIBLE);

        selectPersonSpinner = (Spinner) findViewById(R.id.SpinnerByperson);
        //Theme.Holo.Light
        selectPersonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                str = parent.getItemAtPosition(position).toString();
                //System.out.println(time+"");
                String amountStr;
                int ifFromMe;

                list.clear();
                sumBD = BigDecimal.ZERO;
                //next = BigDecimal.ZERO;


                Cursor c = db.rawQuery("SELECT * FROM " + dbHelper.TBNAME + " WHERE " + dbHelper.PERSON + " = '" + str + "';", null); //执行本地SQL语句查询
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    Map<String, Object> map = new HashMap<String, Object>();

                    int index = c.getColumnIndex(dbHelper.PERSON);
                    map.put("personName", c.getString(index));
                    index = c.getColumnIndex(dbHelper.DATE);
                    map.put("date", c.getString(index));
                    index = c.getColumnIndex(dbHelper.ID);
                    map.put("id", c.getInt(index) + "");
                    index = c.getColumnIndex(dbHelper.IFFROMME);
                    ifFromMe = c.getInt(index);

                    index = c.getColumnIndex(dbHelper.AMOUNT);
                    amountStr = c.getString(index);

//                    if(-1 == amountStr.indexOf(".")){
//                        amountStr+=".0";
//                    }

                    try{
                        next = new BigDecimal(amountStr);

                    }catch (NumberFormatException e){

                    }

                    if (0 == ifFromMe) {
                        sumBD = sumBD.add(next);
                        amountStr = "In " + amountStr;
                        //recordAmount.setTextColor(Color.GREEN);
                    } else if (1 == ifFromMe) {
                        sumBD = sumBD.subtract(next);
                        amountStr = "Out " + amountStr;
                        //recordAmount.setTextColor(Color.RED);
                    }

                    map.put("recordAmount", amountStr);
                    map.put("ifFromMe", ifFromMe);
                    map.put("deleteButton", "delete");
                    list.add(map);

                    c.moveToNext();
                }


                if (0 < sumBD.signum()) {
                    who_owe_you.setText("You owe " + str + " $" + sumBD.abs());
                } else if (0 > sumBD.signum()) {
                    who_owe_you.setText(str + " owe you" + " $" + sumBD.abs());
                } else {
                    who_owe_you.setText(str + " and you mutually do not owe.");
                }


                if (list.size() != 0) {
                    Txtv.setVisibility(View.INVISIBLE);
                    lv.setVisibility(View.VISIBLE);
                    mData = list;
                    adapter = new MyAdapter(ByPerson.this);
                    lv.setAdapter(adapter);

                    lv.setOnItemClickListener(itemListener);
                } else {
                    lv.setVisibility(View.INVISIBLE);
                    Txtv.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        persons = new ArrayList<String>();

        //SQLiteDatabase dbByPerson = openOrCreateDatabase("jilizhang.db", Context.MODE_PRIVATE, null);
        //String args[] = {dbHelper.PERSON};
        // dbHelper.onCreate(db);
        //get all the distinct involved person
        Cursor c1 = db.rawQuery("SELECT DISTINCT " + dbHelper.PERSON + " FROM " + dbHelper.TBNAME +" ORDER BY "+ dbHelper.PERSON, null); //执行本地SQL语句查询
        c1.moveToFirst();
        while (!c1.isAfterLast()) {
            int index = c1.getColumnIndex(dbHelper.PERSON);

            //Log.d("SQLite", c.getString(index));

            persons.add(c1.getString(index));

            c1.moveToNext();
        }


        //  建立Adapter绑定数据源
        _MyAdapter = new MyAdapterSpinner(this, persons);
        //绑定Adapter
        selectPersonSpinner.setAdapter(_MyAdapter);
    }

    private AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position,
                                long id) {
            //call activity add/edit with current name
        }

        // mImg.setImageResource(pics[position]);
    };

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ByPerson Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://pub.haoran.jilizhang/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ByPerson Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://pub.haoran.jilizhang/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public final class ViewHolder {
        public TextView recordAmount;
        public TextView personName;
        public TextView date;
        public Button deleteBtn;
    }

    //this is a override of BaseAdapter to generate a listview with a label and a button which clickable in to each item
    public class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;


        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mData.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.itemoflist, null);
                holder.recordAmount = (TextView) convertView.findViewById(R.id.recordAmount);
                holder.date = (TextView) convertView.findViewById(R.id.date);
                holder.personName = (TextView) convertView.findViewById(R.id.personName);
                holder.deleteBtn = (Button) convertView.findViewById(R.id.deleteBtn);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.recordAmount.setText((String) mData.get(position).get("recordAmount"));
            holder.date.setText((String) mData.get(position).get("date"));
            holder.personName.setText((String) mData.get(position).get("personName"));
            holder.personName.setSelected(true);

            if (1 == (int) mData.get(position).get("ifFromMe")) {
                holder.recordAmount.setTextColor(Color.rgb(0xEE, 0x00, 0x00));
            } else if (0 == (int) mData.get(position).get("ifFromMe")) {
                holder.recordAmount.setTextColor(Color.rgb(0x71, 0xC6, 0x71));//#EE0000
            }

            //holder.deleteBtn.setTag(position);
            holder.deleteBtn.setOnClickListener(new deleteBtnListener(position));
            Log.v("process:", "in42");


            return convertView;
        }

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
            LayoutInflater _LayoutInflater = LayoutInflater.from(mContext);
            convertView = _LayoutInflater.inflate(R.layout.spinner_item_no_image, null);
            if (convertView != null) {
                TextView _TextView1 = (TextView) convertView.findViewById(R.id.noImageSpinnerItem);
                _TextView1.setText(mList.get(position));
               // _TextView1.setSelected(true);
            }
            return convertView;
        }
    }

    public class deleteBtnListener implements View.OnClickListener {
        int position;
        int id;
        String idStr;

        public deleteBtnListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            //a function will delete the record
            // deleteRecord();
            idStr = (String) mData.get(position).get("id");
            id = Integer.parseInt(idStr);

            new AlertDialog.Builder(ByPerson.this)
        /* 弹出窗口的最上头文字 */
                    .setTitle("")
        /* 设置弹出窗口的图式 */
                    .setIcon(R.drawable.trash_bin)
        /* 设置弹出窗口的信息 */
                    .setMessage("Really want to delete？")
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface,
                                        int i) {
                                    //应该删除数据库里的数据同时更新列表

                                    dbHelper.delRecord(id);

                                    mData.clear();
                                    String amountStr;
                                    int ifFromMe;

                                    list.clear();
                                    sumBD = BigDecimal.ZERO;


                                    //List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

                                    Cursor c = db.rawQuery("SELECT * FROM " + dbHelper.TBNAME + " WHERE " + dbHelper.PERSON + " = '" + str + "';", null); //执行本地SQL语句查询
                                    c.moveToFirst();
                                    while (!c.isAfterLast()) {
                                        Map<String, Object> map = new HashMap<String, Object>();

                                        int index = c.getColumnIndex(dbHelper.PERSON);
                                        map.put("personName", c.getString(index));
                                        index = c.getColumnIndex(dbHelper.DATE);
                                        map.put("date", c.getString(index));
                                        index = c.getColumnIndex(dbHelper.ID);
                                        map.put("id", c.getInt(index) + "");
                                        index = c.getColumnIndex(dbHelper.IFFROMME);
                                        ifFromMe = c.getInt(index);

                                        index = c.getColumnIndex(dbHelper.AMOUNT);
                                        amountStr = c.getString(index);

                                        next = new BigDecimal(amountStr);

                                        if (0 == ifFromMe) {
                                            sumBD = sumBD.add(next);
                                            amountStr = "In " + amountStr;
                                            //recordAmount.setTextColor(Color.GREEN);
                                        } else if (1 == ifFromMe) {
                                            sumBD = sumBD.subtract(next);
                                            amountStr = "Out " + amountStr;
                                            //recordAmount.setTextColor(Color.RED);
                                        }

                                        map.put("recordAmount", amountStr);
                                        map.put("ifFromMe", ifFromMe);
                                        map.put("deleteButton", "delete");
                                        list.add(map);

                                        c.moveToNext();
                                    }

                                    if (0 < sumBD.signum()) {
                                        who_owe_you.setText("You owe " + str + " $" + sumBD.abs());
                                    } else if (0 > sumBD.signum()) {
                                        who_owe_you.setText(str + " owe you" + " $" + sumBD.abs());
                                    } else {
                                        who_owe_you.setText(str + " and you mutually do not owe.");
                                    }

                                    mData = list;

                                    adapter.notifyDataSetChanged();
                                    //finish();/* 关闭窗口 */


                                    persons.clear();

                                    Cursor c1 = db.rawQuery("SELECT DISTINCT " + dbHelper.PERSON + " FROM " + dbHelper.TBNAME, null); //执行本地SQL语句查询
                                    c1.moveToFirst();
                                    while (!c1.isAfterLast()) {
                                        int index = c1.getColumnIndex(dbHelper.PERSON);


                                        persons.add(c1.getString(index));

                                        c1.moveToNext();
                                    }

                                    _MyAdapter.notifyDataSetChanged();

                                }
                            })
        /* 设置弹出窗口的返回事件 */
                    .setNegativeButton("cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface,
                                        int i) {
                                    //什么都不干


                                }
                            }).show();

            Log.v("process", "in delete");

        }
    }
}
