package net.haoranzhao.jilizhang.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import net.haoranzhao.jilizhang.R;

import java.io.File;

import static android.support.v4.content.FileProvider.getUriForFile;

/**
 * Created by zhaohaoran on 4/5/16.
 */
public class EmailHelper {
    private String TAG = "EMAIL_HELPER";
    private Context mContext;

    public EmailHelper(Context mContext){
        this.mContext = mContext;
    }





    public boolean sendEmail(String Address, String filename) {
        boolean info=false;

        String[] TO = {Address};

        File outputDir = mContext.getFilesDir();
        File fileLocation = new File(outputDir, filename);

        File exportedPath = new File(mContext.getFilesDir(), "exported");
        File newFile = new File(exportedPath, filename);
        Uri path = getUriForFile(mContext, "net.haoranzhao.jilizhang.fileprovider", newFile);

        Log.d(TAG,path.toString());

        Intent emailIntent = new Intent(Intent.ACTION_SEND);

//        Intent resultIntent = new Intent();
        if (path != null) {

            emailIntent.setFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        //File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
        //Uri path = Uri.fromFile(fileLocation);
        //String[] CC = {""};
        //Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setType("vnd.android.cursor.dir/email");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        // the attachment
        emailIntent .putExtra(Intent.EXTRA_STREAM, path);
        //emailIntent.putExtra(Intent.EXTRA_CC, CC);
        String mailSubject = mContext.getResources().getString(R.string.mail_subject);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, mailSubject);
        String mailText = mContext.getResources().getString(R.string.mail_text);
        emailIntent.putExtra(Intent.EXTRA_TEXT, mailText);

        try {
            // Get a string resource from your app's Resources
            String sendingEmail = mContext.getResources().getString(R.string.Sending_e_mail);

            mContext.startActivity(Intent.createChooser(emailIntent,sendingEmail));
            Log.i(TAG, "Finished sending email...");
            info=true;
        }
        catch (android.content.ActivityNotFoundException ex) {
            info=false;
        }
        return info;

    }

}
