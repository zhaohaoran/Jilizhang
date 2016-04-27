package net.haoranzhao.jilizhang.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zhaohaoran on 6/11/15.
 */
public class OtherUtils {

    public void copyFileToRoot(Context mContext,String filename) throws IOException {
        boolean res = false;
        File root = Environment.getDataDirectory();
        File outputDir = new File(mContext.getFilesDir(), "exported");

        File src = new File(outputDir, filename);
        File dst = new File(root,filename);
        if(!dst.exists()){
            dst.createNewFile();
        }

        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }


}