package net.haoranzhao.jilizhang.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by zhaohaoran on 4/22/16.
 */
public class SharedPreferencesHelper {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    //public static final String MY_PREFERENCES_NAME = "MyPrefs" ;
    private String preferenceName;
    private Context mContext;



    public SharedPreferencesHelper(Context mContext,String preferenceName){
        this.mContext = mContext;
        this.preferenceName = preferenceName;
        sharedPreferences = mContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }

    public void init(Context mContext,String preferenceName){
        this.mContext = mContext;
        this.preferenceName = preferenceName;
        sharedPreferences = mContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }

    public boolean getBoolean(String k){
        return sharedPreferences.getBoolean(k,false);
    }
    public float getFloat(String k){
        return sharedPreferences.getFloat(k,-1);
    }
    public int getInt(String k){
        return sharedPreferences.getInt(k,-1);
    }
    public long getLong(String k){
        return sharedPreferences.getLong(k,-1);
    }
    public String getString(String k){
        return sharedPreferences.getString(k,null);
    }
    public Set<String> getStringSet(String k){
        return sharedPreferences.getStringSet(k,null);
    }

    /*
     * functions below are about editor and put
     */
    public void initEditor(){
         editor = sharedPreferences.edit();
    }
    public boolean putBoolean(String k, boolean v){
        if(editor!=null){
            editor.putBoolean(k,v);
        }else return false;
        return true;
    }
    public boolean putFloat(String k, float v){
        if(editor!=null){
            editor.putFloat(k,v);
        }else return false;
        return true;
    }
    public boolean putInt(String k, int v){
        if(editor!=null){
            editor.putInt(k,v);
        }else return false;
        return true;
    }
    public boolean putLong(String k, long v){
        if(editor!=null){
            editor.putLong(k,v);
        }else return false;
        return true;
    }
    public boolean putString(String k, String v){
        if(editor!=null){
            editor.putString(k,v);
        }else return false;
        return true;
    }
    public boolean putStringSet(String k, Set<String> v){
        if(editor!=null){
            editor.putStringSet(k,v);
        }else return false;
        return true;
    }
    public void apply(){
        editor.apply();
    }
    public boolean commit(){
        return editor.commit();
    }
    public void clearEditor(){
        editor.clear();
    }

    public boolean putBooleanOnce(String k, boolean v){
        editor = sharedPreferences.edit();
        if(editor!=null){
            editor.putBoolean(k,v);
            editor.apply();
            editor.clear();
        }else return false;
        return true;
    }
    public boolean putFloatOnce(String k, float v){
        editor = sharedPreferences.edit();
        if(editor!=null){
            editor.putFloat(k,v);
            editor.apply();
            editor.clear();
        }else return false;
        return true;
    }
    public boolean putIntOnce(String k, int v){
        editor = sharedPreferences.edit();
        if(editor!=null){
            editor.putInt(k,v);
            editor.clear();
        }else return false;
        return true;
    }
    public boolean putLongOnce(String k, long v){
        editor = sharedPreferences.edit();
        if(editor!=null){
            editor.putLong(k,v);
            editor.apply();
            editor.clear();
        }else return false;
        return true;
    }
    public boolean putStringOnce(String k, String v){
        editor = sharedPreferences.edit();
        if(editor!=null){
            editor.putString(k,v);
            editor.apply();
            editor.clear();
        }else return false;
        return true;
    }
    public boolean putStringSetOnce(String k, Set<String> v){
        editor = sharedPreferences.edit();
        if(editor!=null){
            editor.putStringSet(k,v);
            editor.apply();
            editor.clear();
        }else return false;
        return true;
    }





}
