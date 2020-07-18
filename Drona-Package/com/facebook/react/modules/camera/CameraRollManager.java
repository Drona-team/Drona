package com.facebook.react.modules.camera;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore.Files;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.GuardedAsyncTask;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.module.annotations.ReactModule;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ReactModule(name="CameraRollManager")
public class CameraRollManager
  extends ReactContextBaseJavaModule
{
  private static final String ASSET_TYPE_ALL = "All";
  private static final String ASSET_TYPE_PHOTOS = "Photos";
  private static final String ASSET_TYPE_VIDEOS = "Videos";
  private static final String ERROR_UNABLE_TO_FILTER = "E_UNABLE_TO_FILTER";
  private static final String ERROR_UNABLE_TO_LOAD = "E_UNABLE_TO_LOAD";
  private static final String ERROR_UNABLE_TO_LOAD_PERMISSION = "E_UNABLE_TO_LOAD_PERMISSION";
  private static final String ERROR_UNABLE_TO_SAVE = "E_UNABLE_TO_SAVE";
  public static final String NAME = "CameraRollManager";
  private static final String[] PROJECTION = { "_id", "mime_type", "bucket_display_name", "datetaken", "width", "height", "longitude", "latitude", "_data" };
  private static final String SELECTION_BUCKET = "bucket_display_name = ?";
  private static final String SELECTION_DATE_TAKEN = "datetaken < ?";
  
  public CameraRollManager(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  private static void putBasicNodeInfo(Cursor paramCursor, WritableMap paramWritableMap, int paramInt1, int paramInt2, int paramInt3)
  {
    paramWritableMap.putString("type", paramCursor.getString(paramInt1));
    paramWritableMap.putString("group_name", paramCursor.getString(paramInt2));
    paramWritableMap.putDouble("timestamp", paramCursor.getLong(paramInt3) / 1000.0D);
  }
  
  private static void putEdges(ContentResolver paramContentResolver, Cursor paramCursor, WritableMap paramWritableMap, int paramInt)
  {
    WritableNativeArray localWritableNativeArray = new WritableNativeArray();
    paramCursor.moveToFirst();
    int k = paramCursor.getColumnIndex("_id");
    int i2 = paramCursor.getColumnIndex("mime_type");
    int i3 = paramCursor.getColumnIndex("bucket_display_name");
    int i4 = paramCursor.getColumnIndex("datetaken");
    int j = paramCursor.getColumnIndex("width");
    int i5 = paramCursor.getColumnIndex("height");
    int m = paramCursor.getColumnIndex("longitude");
    int n = paramCursor.getColumnIndex("latitude");
    int i6 = paramCursor.getColumnIndex("_data");
    int i1;
    for (int i = 0; (i < paramInt) && (!paramCursor.isAfterLast()); i = i1 + 1)
    {
      WritableNativeMap localWritableNativeMap1 = new WritableNativeMap();
      WritableNativeMap localWritableNativeMap2 = new WritableNativeMap();
      i1 = i;
      if (putImageInfo(paramContentResolver, paramCursor, localWritableNativeMap2, k, j, i5, i6, i2))
      {
        putBasicNodeInfo(paramCursor, localWritableNativeMap2, i2, i3, i4);
        putLocationInfo(paramCursor, localWritableNativeMap2, m, n);
        localWritableNativeMap1.putMap("node", localWritableNativeMap2);
        localWritableNativeArray.pushMap(localWritableNativeMap1);
      }
      else
      {
        i1 = i - 1;
      }
      paramCursor.moveToNext();
    }
    paramWritableMap.putArray("edges", localWritableNativeArray);
  }
  
  private static boolean putImageInfo(ContentResolver paramContentResolver, Cursor paramCursor, WritableMap paramWritableMap, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    WritableNativeMap localWritableNativeMap = new WritableNativeMap();
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("file://");
    ((StringBuilder)localObject).append(paramCursor.getString(paramInt4));
    localObject = Uri.parse(((StringBuilder)localObject).toString());
    localWritableNativeMap.putString("uri", ((Uri)localObject).toString());
    float f4 = paramCursor.getInt(paramInt2);
    float f3 = paramCursor.getInt(paramInt3);
    paramCursor = paramCursor.getString(paramInt5);
    float f2 = f4;
    float f1 = f3;
    if (paramCursor != null)
    {
      f2 = f4;
      f1 = f3;
      if (paramCursor.startsWith("video")) {
        try
        {
          paramCursor = paramContentResolver.openAssetFileDescriptor((Uri)localObject, "r");
          MediaMetadataRetriever localMediaMetadataRetriever = new MediaMetadataRetriever();
          localMediaMetadataRetriever.setDataSource(paramCursor.getFileDescriptor());
          if (f4 > 0.0F)
          {
            f2 = f4;
            f1 = f3;
            if (f3 > 0.0F) {
              break label203;
            }
          }
          try
          {
            paramInt1 = Integer.parseInt(localMediaMetadataRetriever.extractMetadata(18));
            f2 = paramInt1;
            paramInt1 = Integer.parseInt(localMediaMetadataRetriever.extractMetadata(19));
            f1 = paramInt1;
            label203:
            paramInt1 = Integer.parseInt(localMediaMetadataRetriever.extractMetadata(9));
            paramInt1 /= 1000;
            localWritableNativeMap.putInt("playableDuration", paramInt1);
            localMediaMetadataRetriever.release();
            paramCursor.close();
          }
          catch (Throwable paramContentResolver) {}catch (NumberFormatException paramContentResolver)
          {
            paramWritableMap = new StringBuilder();
            paramWritableMap.append("Number format exception occurred while trying to fetch video metadata for ");
            paramWritableMap.append(((Uri)localObject).toString());
            FLog.e("ReactNative", paramWritableMap.toString(), paramContentResolver);
            localMediaMetadataRetriever.release();
            paramCursor.close();
            return false;
          }
          localMediaMetadataRetriever.release();
          paramCursor.close();
          throw paramContentResolver;
        }
        catch (Exception paramContentResolver)
        {
          paramCursor = new StringBuilder();
          paramCursor.append("Could not get video metadata for ");
          paramCursor.append(((Uri)localObject).toString());
          FLog.e("ReactNative", paramCursor.toString(), paramContentResolver);
          return false;
        }
      }
    }
    if (f2 > 0.0F)
    {
      f3 = f2;
      f2 = f1;
      if (f1 > 0.0F) {
        break label417;
      }
    }
    try
    {
      paramContentResolver = paramContentResolver.openAssetFileDescriptor((Uri)localObject, "r");
      paramCursor = new BitmapFactory.Options();
      inJustDecodeBounds = true;
      BitmapFactory.decodeFileDescriptor(paramContentResolver.getFileDescriptor(), null, paramCursor);
      f3 = outWidth;
      paramInt1 = outHeight;
      paramContentResolver.close();
      f2 = paramInt1;
      label417:
      localWritableNativeMap.putDouble("width", f3);
      localWritableNativeMap.putDouble("height", f2);
      paramWritableMap.putMap("image", localWritableNativeMap);
      return true;
    }
    catch (IOException paramContentResolver)
    {
      paramCursor = new StringBuilder();
      paramCursor.append("Could not get width/height for ");
      paramCursor.append(((Uri)localObject).toString());
      FLog.e("ReactNative", paramCursor.toString(), paramContentResolver);
    }
    return false;
  }
  
  private static void putLocationInfo(Cursor paramCursor, WritableMap paramWritableMap, int paramInt1, int paramInt2)
  {
    double d1 = paramCursor.getDouble(paramInt1);
    double d2 = paramCursor.getDouble(paramInt2);
    if ((d1 > 0.0D) || (d2 > 0.0D))
    {
      paramCursor = new WritableNativeMap();
      paramCursor.putDouble("longitude", d1);
      paramCursor.putDouble("latitude", d2);
      paramWritableMap.putMap("location", paramCursor);
    }
  }
  
  private static void putPageInfo(Cursor paramCursor, WritableMap paramWritableMap, int paramInt)
  {
    WritableNativeMap localWritableNativeMap = new WritableNativeMap();
    boolean bool;
    if (paramInt < paramCursor.getCount()) {
      bool = true;
    } else {
      bool = false;
    }
    localWritableNativeMap.putBoolean("has_next_page", bool);
    if (paramInt < paramCursor.getCount())
    {
      paramCursor.moveToPosition(paramInt - 1);
      localWritableNativeMap.putString("end_cursor", paramCursor.getString(paramCursor.getColumnIndex("datetaken")));
    }
    paramWritableMap.putMap("page_info", localWritableNativeMap);
  }
  
  public String getName()
  {
    return "CameraRollManager";
  }
  
  public void getPhotos(ReadableMap paramReadableMap, Promise paramPromise)
  {
    int i = paramReadableMap.getInt("first");
    String str1;
    if (paramReadableMap.hasKey("after")) {
      str1 = paramReadableMap.getString("after");
    } else {
      str1 = null;
    }
    String str2;
    if (paramReadableMap.hasKey("groupName")) {
      str2 = paramReadableMap.getString("groupName");
    } else {
      str2 = null;
    }
    if (paramReadableMap.hasKey("assetType")) {}
    for (String str3 = paramReadableMap.getString("assetType");; str3 = "Photos") {
      break;
    }
    ReadableArray localReadableArray;
    if (paramReadableMap.hasKey("mimeTypes")) {
      localReadableArray = paramReadableMap.getArray("mimeTypes");
    } else {
      localReadableArray = null;
    }
    if (!paramReadableMap.hasKey("groupTypes"))
    {
      new GetMediaTask(getReactApplicationContext(), i, str1, str2, localReadableArray, str3, paramPromise, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
      return;
    }
    throw new JSApplicationIllegalArgumentException("groupTypes is not supported on Android");
  }
  
  public void saveToCameraRoll(String paramString1, String paramString2, Promise paramPromise)
  {
    new SaveToCameraRoll(getReactApplicationContext(), Uri.parse(paramString1), paramPromise).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
  }
  
  private static class GetMediaTask
    extends GuardedAsyncTask<Void, Void>
  {
    @Nullable
    private final String mAfter;
    private final String mAssetType;
    private final Context mContext;
    private final int mFirst;
    @Nullable
    private final String mGroupName;
    @Nullable
    private final ReadableArray mMimeTypes;
    private final Promise mPromise;
    
    private GetMediaTask(ReactContext paramReactContext, int paramInt, String paramString1, String paramString2, ReadableArray paramReadableArray, String paramString3, Promise paramPromise)
    {
      super();
      mContext = paramReactContext;
      mFirst = paramInt;
      mAfter = paramString1;
      mGroupName = paramString2;
      mMimeTypes = paramReadableArray;
      mPromise = paramPromise;
      mAssetType = paramString3;
    }
    
    protected void doInBackgroundGuarded(Void... paramVarArgs)
    {
      Object localObject3 = new StringBuilder("1");
      Object localObject4 = new ArrayList();
      if (!TextUtils.isEmpty(mAfter))
      {
        ((StringBuilder)localObject3).append(" AND datetaken < ?");
        ((List)localObject4).add(mAfter);
      }
      if (!TextUtils.isEmpty(mGroupName))
      {
        ((StringBuilder)localObject3).append(" AND bucket_display_name = ?");
        ((List)localObject4).add(mGroupName);
      }
      paramVarArgs = mAssetType;
      int i = -1;
      int k = paramVarArgs.hashCode();
      int j = 0;
      if (k != -1905167199)
      {
        if (k != -1732810888)
        {
          if ((k == 65921) && (paramVarArgs.equals("All"))) {
            i = 2;
          }
        }
        else if (paramVarArgs.equals("Videos")) {
          i = 1;
        }
      }
      else if (paramVarArgs.equals("Photos")) {
        i = 0;
      }
      switch (i)
      {
      default: 
        paramVarArgs = mPromise;
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("Invalid filter option: '");
        ((StringBuilder)localObject1).append(mAssetType);
        ((StringBuilder)localObject1).append("'. Expected one of '");
        ((StringBuilder)localObject1).append("Photos");
        ((StringBuilder)localObject1).append("', '");
        ((StringBuilder)localObject1).append("Videos");
        ((StringBuilder)localObject1).append("' or '");
        ((StringBuilder)localObject1).append("All");
        ((StringBuilder)localObject1).append("'.");
        paramVarArgs.reject("E_UNABLE_TO_FILTER", ((StringBuilder)localObject1).toString());
        return;
      case 2: 
        ((StringBuilder)localObject3).append(" AND media_type IN (3,1)");
        break;
      case 1: 
        ((StringBuilder)localObject3).append(" AND media_type = 3");
        break;
      case 0: 
        ((StringBuilder)localObject3).append(" AND media_type = 1");
      }
      if ((mMimeTypes != null) && (mMimeTypes.size() > 0))
      {
        ((StringBuilder)localObject3).append(" AND mime_type IN (");
        i = j;
        while (i < mMimeTypes.size())
        {
          ((StringBuilder)localObject3).append("?,");
          ((List)localObject4).add(mMimeTypes.getString(i));
          i += 1;
        }
        ((StringBuilder)localObject3).replace(((StringBuilder)localObject3).length() - 1, ((StringBuilder)localObject3).length(), ")");
      }
      paramVarArgs = new WritableNativeMap();
      Object localObject1 = mContext.getContentResolver();
      try
      {
        Object localObject2 = MediaStore.Files.getContentUri("external");
        String[] arrayOfString = CameraRollManager.PROJECTION;
        localObject3 = ((StringBuilder)localObject3).toString();
        i = ((List)localObject4).size();
        Object localObject5 = new String[i];
        localObject4 = ((List)localObject4).toArray((Object[])localObject5);
        localObject4 = (String[])localObject4;
        localObject5 = new StringBuilder();
        ((StringBuilder)localObject5).append("datetaken DESC, date_modified DESC LIMIT ");
        i = mFirst;
        ((StringBuilder)localObject5).append(i + 1);
        localObject2 = ((ContentResolver)localObject1).query((Uri)localObject2, arrayOfString, (String)localObject3, (String[])localObject4, ((StringBuilder)localObject5).toString());
        if (localObject2 == null)
        {
          paramVarArgs = mPromise;
          paramVarArgs.reject("E_UNABLE_TO_LOAD", "Could not get media");
          return;
        }
        try
        {
          CameraRollManager.putEdges((ContentResolver)localObject1, (Cursor)localObject2, paramVarArgs, mFirst);
          CameraRollManager.putPageInfo((Cursor)localObject2, paramVarArgs, mFirst);
          ((Cursor)localObject2).close();
          localObject1 = mPromise;
          ((Promise)localObject1).resolve(paramVarArgs);
          return;
        }
        catch (Throwable localThrowable)
        {
          ((Cursor)localObject2).close();
          localObject2 = mPromise;
          ((Promise)localObject2).resolve(paramVarArgs);
          throw localThrowable;
        }
        return;
      }
      catch (SecurityException paramVarArgs)
      {
        mPromise.reject("E_UNABLE_TO_LOAD_PERMISSION", "Could not get media: need READ_EXTERNAL_STORAGE permission", paramVarArgs);
      }
    }
  }
  
  private static class SaveToCameraRoll
    extends GuardedAsyncTask<Void, Void>
  {
    private static final int SAVE_BUFFER_SIZE = 1048576;
    private final Context mContext;
    private final Promise mPromise;
    private final Uri mUri;
    
    public SaveToCameraRoll(ReactContext paramReactContext, Uri paramUri, Promise paramPromise)
    {
      super();
      mContext = paramReactContext;
      mUri = paramUri;
      mPromise = paramPromise;
    }
    
    protected void doInBackgroundGuarded(Void... paramVarArgs)
    {
      throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a21 = a20\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
    }
  }
}
