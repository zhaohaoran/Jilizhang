package net.haoranzhao.jilizhang;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import net.haoranzhao.jilizhang.util.DBHelper;
import net.haoranzhao.jilizhang.util.EmailHelper;

public class ExportByEmail extends Activity {
    private static String TAG="EXPORT_BY_EMAIL";
    private LinearLayout exportLayoutLL;
    private CheckBox deleteAllCB;
    private EditText emailET;
    private ProgressBar exportingPB;
    private Button exportBTN;
    private boolean ifDeleteAll;

    private String EmailAddress;
    private EmailHelper emailHelper;

    private DBHelper dbHelper;
    public static final String DBNAME = "jilizhang.db";
    SQLiteDatabase db;

    private static int SUCCESS=4,
            EMAIL_FAILED=3,
            DB_EXPORT_FAILED=2,
            EXPORT_FAILED=1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_export_by_email);

        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.show();

        emailHelper = new EmailHelper(ExportByEmail.this);
        dbHelper = new DBHelper(ExportByEmail.this,DBNAME,null,1);


        exportLayoutLL = (LinearLayout)findViewById(R.id.export_layout_LL);
        deleteAllCB = (CheckBox)findViewById(R.id.deleteAllRecord_CB);
        emailET = (EditText)findViewById(R.id.email_ET);
        //TODO: replace this with progress dialog. ref:http://blog.csdn.net/ameyume/article/details/6138488
        // do this in next version
        exportingPB = (ProgressBar)findViewById(R.id.exporting_PB);
        exportingPB.setVisibility(View.INVISIBLE);
        exportBTN = (Button)findViewById(R.id.export_btn);


        exportBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmailAddress = emailET.getText().toString();

                if(deleteAllCB.isChecked()){
                    ShowDeletAllDialog("Delete all", "Really want to delete all?","Delete","Cancel",ExportByEmail.this);
                }else{

                }


                if(CheckEmailFormat(EmailAddress)){
                    AsyncTaskExport mAsyncTaskExport = new AsyncTaskExport();
                    mAsyncTaskExport.execute();
                }else{
                    ShowOneBtnDialog("Email Format Error","Email Format Error.","OK",ExportByEmail.this);
                }
            }
        });


    }

    //check the email format
    public boolean CheckEmailFormat(String emailAddress){

        emailAddress.trim();

        if (emailAddress == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches();
        }
    }



    public void showProcessBar(LinearLayout layout, ProgressBar bar, boolean ifSwitchOn){

        runOnUiThread(new actionbarRunnable( layout,  bar, ifSwitchOn));


    }

    private class actionbarRunnable implements Runnable {
        private boolean ifSwitchOn=false;
        private ProgressBar bar;
        private LinearLayout layout;

        public actionbarRunnable(LinearLayout layout, ProgressBar bar, boolean ifSwitchOn){
            this.ifSwitchOn=ifSwitchOn;
            this.bar = bar;
            this.layout = layout;
        }
        @Override
        public void run() {
            if(ifSwitchOn){
                bar.setVisibility(View.VISIBLE);
                layout.setVisibility(View.INVISIBLE);
            }else{
                layout.setVisibility(View.VISIBLE);
                bar.setVisibility(View.INVISIBLE);
            }
        }
    }

    private class AsyncTaskExport extends AsyncTask<Void, Integer, Integer>{

        @Override
        protected Integer doInBackground(Void... params) {
            int status;
            status = ExportByEmail.this.EXPORT_FAILED;
            showProcessBar(exportLayoutLL,exportingPB,true);

            db = openOrCreateDatabase(DBNAME, Context.MODE_PRIVATE, null);

            String exportRes = dbHelper.exportCSV(db,ExportByEmail.this);
            //export successfully
            if(exportRes.substring(0,DBHelper.EXPORT_SUCCESS.length()).equals(DBHelper.EXPORT_SUCCESS)){
                String filename = exportRes.substring(DBHelper.EXPORT_SUCCESS.length()+1);
                Log.d(TAG,filename);
                if(!EmailAddress.equals("") || !EmailAddress.equals(null)){
                    if(emailHelper.sendEmail(EmailAddress,filename)){
                        status = ExportByEmail.this.SUCCESS;
                    }else{//There is no email client installed.
                        status = ExportByEmail.this.EMAIL_FAILED;
                    }
                }
            }else{//export failed
                status = ExportByEmail.this.DB_EXPORT_FAILED;
            }

            Log.d(TAG,status+"");
            return status;

        }

        @Override
        protected void onPostExecute(Integer result) {
            showProcessBar(exportLayoutLL,exportingPB,false);
            if(result == (ExportByEmail.this.SUCCESS)) {
                ShowOneBtnDialog("Exported successfully","Exported successfully.","OK",ExportByEmail.this);
                //ExportByEmail.this.finish();
            }else if(result==(ExportByEmail.this.EMAIL_FAILED)){
                ShowOneBtnDialog("Email failed","There is no email client installed." +
                        "You can access to the exported file under the directory of the app.","OK",ExportByEmail.this);
            }else if(result==(ExportByEmail.this.DB_EXPORT_FAILED)){
                ShowOneBtnDialog("Exported failed","Exported failed,you can try again.","OK",ExportByEmail.this);
            }else if(result==(ExportByEmail.this.EXPORT_FAILED)){
                ShowOneBtnDialog("Exported failed","Exported failed,you can try again.","OK",ExportByEmail.this);
            }

        }



    }

    public void ShowOneBtnDialog(String title, String Message,String BtnTxt, Context mContext){
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(Message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, BtnTxt,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void ShowDeletAllDialog(String title, String Message,String BtnPosTxt,String BtnNegTxt, Context mContext){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle(title);
        alertDialog.setMessage(Message);
        //build two button dialog for delete all
        alertDialog.setPositiveButton(BtnPosTxt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.delAllRecords();
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton(BtnNegTxt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.create().show();
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
