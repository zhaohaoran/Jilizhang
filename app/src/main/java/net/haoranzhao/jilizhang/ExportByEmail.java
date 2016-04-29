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
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import net.haoranzhao.jilizhang.util.DBHelper;
import net.haoranzhao.jilizhang.util.EmailHelper;
import net.haoranzhao.jilizhang.util.LocaleHelper;
import net.haoranzhao.jilizhang.util.OtherUtils;

import java.io.IOException;

public class ExportByEmail extends Activity {
    private static String TAG="EXPORT_BY_EMAIL";
    private LinearLayout exportLayoutLL;
    private CheckBox deleteAllCB;
    private EditText emailET;
    private ProgressBar exportingPB;
    private ImageButton exportBTN;

    private String EmailAddress;
    private EmailHelper emailHelper;
    private OtherUtils otherUtils;

    private DBHelper dbHelper;
    public static final String DBNAME = "jilizhang.db";
    SQLiteDatabase db;
    private String filename;

    private String delete_all, really_delete_all, error, email_format_error,ok,export_success,export_failed,no_email_client,yes,cancel,copy_to_root_failed;

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
        otherUtils = new OtherUtils();


        exportLayoutLL = (LinearLayout)findViewById(R.id.export_layout_LL);
        deleteAllCB = (CheckBox)findViewById(R.id.deleteAllRecord_CB);
        emailET = (EditText)findViewById(R.id.email_ET);
        //TODO: replace this with progress dialog. ref:http://blog.csdn.net/ameyume/article/details/6138488
        // do this in next version
        exportingPB = (ProgressBar)findViewById(R.id.exporting_PB);
        exportingPB.setVisibility(View.INVISIBLE);
        exportBTN = (ImageButton)findViewById(R.id.export_btn);


        //private String delete_all, really_delete_all, error, email_format_error,ok,export_success,export_failed,no_email_client;

        delete_all = getResources().getString(R.string.delete_all);
        really_delete_all = getResources().getString(R.string.really_delete_all);
        error = getResources().getString(R.string.error);
        email_format_error = getResources().getString(R.string.email_format_error);
        ok = getResources().getString(R.string.ok);
        export_success = getResources().getString(R.string.export_success);
        export_failed = getResources().getString(R.string.export_failed);
        no_email_client = getResources().getString(R.string.no_email_client);
        yes = getResources().getString(R.string.yes);
        cancel = getResources().getString(R.string.cancel);
        copy_to_root_failed = getResources().getString(R.string.copy_to_root_failed);

        exportBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmailAddress = emailET.getText().toString();

                if(deleteAllCB.isChecked()){
                    ShowDeletAllDialog(delete_all, really_delete_all,yes,cancel,ExportByEmail.this);
                }else{

                }


                if(CheckEmailFormat(EmailAddress)){
                    AsyncTaskExport mAsyncTaskExport = new AsyncTaskExport();
                    mAsyncTaskExport.execute();
                }else{
                    ShowOneBtnDialog(error,email_format_error,ok,ExportByEmail.this);
                }
            }
        });


    }

    @Override
    public void onResume(){
        super.onResume();
        String languageSetup = JilizhangApplication.sharedPreferencesHelper.getString(JilizhangApplication.languageKey);
        if(languageSetup!=null){
            LocaleHelper.setLocale(this,languageSetup);
        }
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
                filename = exportRes.substring(DBHelper.EXPORT_SUCCESS.length()+1);
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
                ShowOneBtnDialog(export_success,export_success,ok,ExportByEmail.this);
                //ExportByEmail.this.finish();
            }else if(result==(ExportByEmail.this.EMAIL_FAILED)){
                //ShowOneBtnDialog(export_failed,no_email_client,ok,ExportByEmail.this);
                ShowCopyToRootDialog(export_failed,no_email_client,ok,cancel,ExportByEmail.this,filename);


            }else if(result==(ExportByEmail.this.DB_EXPORT_FAILED)){
                ShowOneBtnDialog(export_failed,export_failed,ok,ExportByEmail.this);
            }else if(result==(ExportByEmail.this.EXPORT_FAILED)){
                ShowOneBtnDialog(export_failed,export_failed,ok,ExportByEmail.this);
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

    public void ShowCopyToRootDialog(String title, String Message, String BtnPosTxt, String BtnNegTxt, final Context mContext,final String filename){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle(title);
        alertDialog.setMessage(Message);
        //build two button dialog for delete all
        alertDialog.setPositiveButton(BtnPosTxt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    otherUtils.copyFileToRoot(mContext,filename);
                } catch (IOException e) {
                    dialog.dismiss();
                    ShowOneBtnDialog(export_failed,copy_to_root_failed,ok,ExportByEmail.this);
                    e.printStackTrace();
                }
            }
        });
        alertDialog.setNegativeButton(BtnNegTxt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });alertDialog.create().show();
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }





}
