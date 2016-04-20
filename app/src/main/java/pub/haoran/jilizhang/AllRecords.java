package pub.haoran.jilizhang;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.haoran.jilizhang.util.DBHelper;


public class AllRecords extends Activity {
    private List<Map<String, Object>> mData;
    private ListView lv;
    private TextView Txtv,TotalBalanceTv;
    DBHelper dbHelper;
    TextView recordAmount;
    MyAdapter adapter;
    SQLiteDatabase db;
    public static final String DBNAME = "jilizhang.db";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_records);

        recordAmount = (TextView)findViewById(R.id.recordAmount);

        dbHelper = new DBHelper(AllRecords.this,DBNAME,null,1);


        //打开或创建test.db数据库
        db = openOrCreateDatabase(DBNAME, Context.MODE_PRIVATE, null);
        //db.execSQL("DROP TABLE IF EXISTS records");
        //创建records表
        //db.execSQL("CREATE TABLE records (_id INTEGER PRIMARY KEY AUTOINCREMENT, person TEXT not null, date TEXT not null, amount INTEGER, iffromme BLOB)");
        dbHelper.onCreate(db);



        // Date date = new Date();

        Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
        t.setToNow(); // get system time。
        String time=t.year+"-"+(t.month+1)+"-"+t.monthDay;

        //dbHelper.addRecord("李四",time,250.0,true);

        Log.e("msg", time);
       // long time = date.getTime();


    }

    @Override
    public void onResume(){
        super.onResume();

        List<String> fileList = new ArrayList<String>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();


        //System.out.println(time+"");
        String amountStr;
        int ifFromMe;

        BigDecimal sumBD = BigDecimal.ZERO;
        BigDecimal next = BigDecimal.ZERO;


        Cursor c = db.rawQuery("SELECT * FROM "+dbHelper.TBNAME, null); //执行本地SQL语句查询
        c.moveToFirst();
        while(!c.isAfterLast()){
            Map<String, Object> map = new HashMap<String, Object>();

            int index = c.getColumnIndex(dbHelper.PERSON);
            map.put("personName", c.getString(index));
            index = c.getColumnIndex(dbHelper.DATE);
            map.put("date",c.getString(index));

            index = c.getColumnIndex(dbHelper.ID);
            map.put("id",c.getInt(index)+"");

            index = c.getColumnIndex(dbHelper.IFFROMME);
            ifFromMe = c.getInt(index);

            index = c.getColumnIndex(dbHelper.AMOUNT);
            amountStr = c.getString(index);

            try{
                next = new BigDecimal(amountStr);

            }catch (NumberFormatException e){

            }

            if(0==ifFromMe){
                sumBD = sumBD.add(next);
                amountStr = "In "+amountStr;
                //recordAmount.setTextColor(Color.GREEN);
            }
            else if(1==ifFromMe){
                sumBD = sumBD.subtract(next);
                amountStr = "Out "+amountStr;
                //recordAmount.setTextColor(Color.RED);
            }

            map.put("recordAmount", amountStr);
            map.put("ifFromMe",ifFromMe);
            map.put("deleteButton", "delete");
            list.add(map);

            c.moveToNext();
        }

        TotalBalanceTv = (TextView)findViewById(R.id.totalBalance_TV);

        if (0 < sumBD.signum()) {
            TotalBalanceTv.setText("Your total balance is $" + sumBD.abs());
        } else if (0 > sumBD.signum()) {
            TotalBalanceTv.setText("Your total balance is -$" + sumBD.abs());
        } else {
            TotalBalanceTv.setText("Your total balance is $" + sumBD.abs());
        }




        lv = (ListView)findViewById(R.id.MyListView);
        Txtv = (TextView)findViewById(R.id.NoRecordTxt);

        if(list.size()!=0){
            Txtv.setVisibility(View.INVISIBLE);
            lv.setVisibility(View.VISIBLE);
            mData = list;
            adapter = new MyAdapter(this);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(itemListener);
        }else{
            lv.setVisibility(View.INVISIBLE);
            Txtv.setVisibility(View.VISIBLE);
        }



    }




    private AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener()
    {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position,
                                long id) {
            //call activity add/edit with current name
        }

        // mImg.setImageResource(pics[position]);
    };

    public final class ViewHolder{
        public TextView recordAmount;
        public TextView personName;
        public TextView date;
        public Button deleteBtn;
    }

    //this is a override of BaseAdapter to generate a listview with a label and a button which clickable in to each item
    public class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;


        public MyAdapter(Context context){
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
                holder=new ViewHolder();
                convertView = mInflater.inflate(R.layout.itemoflist, null);
                holder.recordAmount = (TextView)convertView.findViewById(R.id.recordAmount);
                holder.date = (TextView)convertView.findViewById(R.id.date);
                holder.personName = (TextView)convertView.findViewById(R.id.personName);
                holder.deleteBtn = (Button)convertView.findViewById(R.id.deleteBtn);
                convertView.setTag(holder);

            }else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.recordAmount.setText((String)mData.get(position).get("recordAmount"));
            holder.date.setText((String)mData.get(position).get("date"));
            holder.personName.setText((String)mData.get(position).get("personName"));
            holder.personName.setSelected(true);

            if(1 == (int)mData.get(position).get("ifFromMe")){
                holder.recordAmount.setTextColor(Color.rgb(0xEE,0x00,0x00));
            }
            else if(0 == (int)mData.get(position).get("ifFromMe")){
                holder.recordAmount.setTextColor(Color.rgb(0x71,0xC6,0x71));//#EE0000
            }

            //holder.deleteBtn.setTag(position);
            holder.deleteBtn.setOnClickListener(new deleteBtnListener(position));
            //Log.v("process:", "in42");


            return convertView;
        }

    }

    public class deleteBtnListener implements View.OnClickListener{
        int position;
        int id;
        String idStr;

        public deleteBtnListener(int position){
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            //a function will delete the record
            // deleteRecord();
            idStr = (String)mData.get(position).get("id");
            id = Integer.parseInt(idStr);

            new AlertDialog.Builder(AllRecords.this)
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
                                    //System.out.println(time+"");
                                    String amountStr;
                                    int ifFromMe;

                                    BigDecimal sumBD = BigDecimal.ZERO;
                                    BigDecimal next = BigDecimal.ZERO;

                                    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

                                    Cursor c = db.rawQuery("SELECT * FROM "+dbHelper.TBNAME, null); //执行本地SQL语句查询
                                    c.moveToFirst();
                                    while(!c.isAfterLast()){
                                        Map<String, Object> map = new HashMap<String, Object>();

                                        int index = c.getColumnIndex(dbHelper.PERSON);
                                        map.put("personName", c.getString(index));
                                        index = c.getColumnIndex(dbHelper.DATE);
                                        map.put("date",c.getString(index));

                                        index = c.getColumnIndex(dbHelper.ID);
                                        map.put("id",c.getInt(index)+"");

                                        index = c.getColumnIndex(dbHelper.IFFROMME);
                                        ifFromMe = c.getInt(index);

                                        index = c.getColumnIndex(dbHelper.AMOUNT);
                                        amountStr = c.getString(index);

                                        try{
                                            next = new BigDecimal(amountStr);

                                        }catch (NumberFormatException e){

                                        }


                                        if(0==ifFromMe){
                                            sumBD = sumBD.add(next);
                                            amountStr = "In "+amountStr;
                                            //recordAmount.setTextColor(Color.GREEN);
                                        }
                                        else if(1==ifFromMe){
                                            sumBD = sumBD.subtract(next);
                                            amountStr = "Out "+amountStr;
                                            //recordAmount.setTextColor(Color.RED);
                                        }

                                        map.put("recordAmount", amountStr);
                                        map.put("ifFromMe",ifFromMe);
                                        map.put("deleteButton", "delete");
                                        list.add(map);

                                        c.moveToNext();
                                    }


                                    TotalBalanceTv = (TextView)findViewById(R.id.totalBalance_TV);

                                    if (0 < sumBD.signum()) {
                                        TotalBalanceTv.setText("Your total balance is $" + sumBD.abs());
                                    } else if (0 > sumBD.signum()) {
                                        TotalBalanceTv.setText("Your total balance is -$" + sumBD.abs());
                                    } else {
                                        TotalBalanceTv.setText("Your total balance is $" + sumBD.abs());
                                    }


                                    mData = list;

                                    adapter.notifyDataSetChanged();
                                    //finish();/* 关闭窗口 */
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
