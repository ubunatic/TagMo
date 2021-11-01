package com.hiddenramblings.tagmo;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.hiddenramblings.tagmo.nfctech.TagWriter;
import com.hiddenramblings.tagmo.settings.Preferences_;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.File;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@EApplication
public class TagMo extends Application {

    public static final String PROVIDER = BuildConfig.APPLICATION_ID + ".provider";

    public static final String ACTION_EDIT_COMPLETE = BuildConfig.APPLICATION_ID + ".EDIT_COMPLETE";
    public static final String ACTION_SCAN_TAG = BuildConfig.APPLICATION_ID + ".SCAN_TAG";
    public static final String ACTION_WRITE_TAG_FULL = BuildConfig.APPLICATION_ID + ".WRITE_TAG_FULL";
    public static final String ACTION_WRITE_TAG_RAW = BuildConfig.APPLICATION_ID + ".WRITE_TAG_RAW";
    public static final String ACTION_WRITE_TAG_DATA = BuildConfig.APPLICATION_ID + ".WRITE_TAG_DATA";
    public static final String ACTION_WRITE_ALL_TAGS = BuildConfig.APPLICATION_ID + ".WRITE_ALL_TAGS";
    public static final String ACTION_ACTIVATE_BANK = BuildConfig.APPLICATION_ID + ".ACTIVATE_BANK";
    public static final String ACTION_SET_BANK_COUNT = BuildConfig.APPLICATION_ID + ".SET_BANK_COUNT";
    public static final String ACTION_REPLACE_AMIIBO = BuildConfig.APPLICATION_ID + ".REPLACE_AMIIBO";
    public static final String ACTION_FORMAT_BANK = BuildConfig.APPLICATION_ID + ".FORMAT_BANK";
    public static final String ACTION_LOCK_AMIIBO = BuildConfig.APPLICATION_ID + ".LOCK_AMIIBO";
    public static final String ACTION_UNLOCK_UNIT = BuildConfig.APPLICATION_ID + ".UNLOCK_UNIT";
    public static final String ACTION_BACKUP_AMIIBO = BuildConfig.APPLICATION_ID + ".BACKUP_AMIIBO";
    public static final String ACTION_NFC_SCANNED = BuildConfig.APPLICATION_ID + ".NFC_SCANNED";

    public static final String EXTRA_TAG_DATA = BuildConfig.APPLICATION_ID + ".EXTRA_TAG_DATA";
    public static final String EXTRA_AMIIBO_DATA = BuildConfig.APPLICATION_ID + ".EXTRA_AMIIBO_DATA";
    public static final String EXTRA_IGNORE_TAG_ID = BuildConfig.APPLICATION_ID + ".EXTRA_IGNORE_TAG_ID";
    public static final String EXTRA_AMIIBO_ID = BuildConfig.APPLICATION_ID + ".AMIIBO_ID";
    public static final String EXTRA_AMIIBO_FILES = BuildConfig.APPLICATION_ID + ".EXTRA_AMIIBO_FILES";
    public static final String EXTRA_SIGNATURE = BuildConfig.APPLICATION_ID + ".EXTRA_SIGNATURE";
    public static final String EXTRA_ACTIVE_BANK = BuildConfig.APPLICATION_ID + ".EXTRA_ACTIVE_BANK";
    public static final String EXTRA_BANK_COUNT = BuildConfig.APPLICATION_ID + ".EXTRA_BANK_COUNT";
    public static final String EXTRA_CURRENT_BANK = BuildConfig.APPLICATION_ID + ".EXTRA_CURRENT_BANK";

    @Pref
    Preferences_ prefs;

    public static Charset UTF_8;
    public static Charset UTF_16BE;
    public static Charset UTF_16LE;

    private static WeakReference<Context> mContext;
    private static WeakReference<Preferences_> mPrefs;

    @Override
    public void onCreate() {
        super.onCreate();

        setTheme(R.style.AppTheme);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            UTF_8 = StandardCharsets.UTF_8;
            UTF_16BE = StandardCharsets.UTF_16BE;
            UTF_16LE = StandardCharsets.UTF_16LE;
        } else {
            UTF_8 = Charset.forName("UTF-8");
            UTF_16BE = Charset.forName("UTF-16BE");
            UTF_16LE = Charset.forName("UTF-16LE");
        }
        mContext = new WeakReference<>(this);
        mPrefs = new WeakReference<>(prefs);
    }

    public static Context getContext() {
        return mContext.get();
    }

    public static Preferences_ getPrefs() {
        return mPrefs.get();
    }

    public static String getStringRes(int resource) {
        return mContext.get().getString(resource);
    }

    public static String getStringRes(int resource, String params) {
        return mContext.get().getString(resource, params);
    }

    public static String getStringRes(int resource, String params, int digits) {
        return mContext.get().getString(resource, params, digits);
    }

    public static String getStringRes(int resource, int params) {
        try {
            Resources res = mContext.get().getResources();
            res.getIdentifier(res.getResourceName(params),
                    "string", BuildConfig.APPLICATION_ID);
            return mContext.get().getString(resource, mContext.get().getString(params));
        } catch (Resources.NotFoundException ignore) {
            return mContext.get().getString(resource, params);
        }
    }

    public static String TAG(Class<?> src) {
        return src.getSimpleName();
    }

    public static void Debug(Class<?> src, String params) {
        if (!mPrefs.get().disableDebug().get())
            Log.d(TAG(src), params);
    }

    public static void Debug(Class<?> src, int resource) {
        if (!mPrefs.get().disableDebug().get())
            Log.d(TAG(src), getStringRes(resource));
    }

    public static void Debug(Class<?> src, int resource, String params) {
        if (!mPrefs.get().disableDebug().get())
            Log.d(TAG(src), getStringRes(resource, params));
    }

    public static void Error(Class<?> src, String params) {
        Log.e(TAG(src), params);
    }

    public static void Error(Class<?> src, int resource) {
        Log.e(TAG(src), getStringRes(resource));
    }

    public static void Error(Class<?> src, int resource, String params) {
        Log.e(TAG(src), getStringRes(resource, params));
    }

    public static void Error(Class<?> src, int resource, Exception e) {
        Log.e(TAG(src), getStringRes(resource), e);
    }

    public static File getExternalFiles() {
        return mContext.get().getExternalFilesDir(null);
    }

    public static void scanFile(File file) {
        try {
            MediaScannerConnection.scanFile(TagMo.getContext(),
                    new String[]{file.getAbsolutePath()}, null, null);
        } catch (Exception e) {
            Error(TagWriter.class, R.string.fail_media_scan, e);
        }
    }

    public static String getMimeType(File file) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase());
    }
}