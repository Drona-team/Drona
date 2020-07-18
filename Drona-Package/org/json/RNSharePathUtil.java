package org.json;

import android.app.Application;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video.Media;
import android.text.TextUtils;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;
import com.facebook.react.bridge.ReactContext;
import java.io.File;
import java.util.ArrayList;

public class RNSharePathUtil
{
  private static final ArrayList<String> authorities = new ArrayList();
  
  public RNSharePathUtil() {}
  
  public static Uri compatUriFromFile(ReactContext paramReactContext, File paramFile)
  {
    compileAuthorities(paramReactContext);
    Object localObject1 = Uri.fromFile(paramFile).getAuthority();
    if ((!TextUtils.isEmpty((CharSequence)localObject1)) && (authorities.contains(localObject1))) {
      return Uri.fromFile(paramFile);
    }
    if (paramFile.getAbsolutePath().startsWith("content://")) {
      return Uri.fromFile(paramFile);
    }
    localObject1 = null;
    int i = 0;
    while (i < authorities.size())
    {
      Object localObject2 = authorities;
      try
      {
        localObject2 = ((ArrayList)localObject2).get(i);
        localObject2 = (String)localObject2;
        localObject2 = FileProvider.getUriForFile(paramReactContext, (String)localObject2, paramFile);
        localObject1 = localObject2;
        if (localObject2 != null) {
          return localObject2;
        }
      }
      catch (Exception localException)
      {
        for (;;) {}
      }
      i += 1;
    }
    return localObject1;
  }
  
  public static void compileAuthorities(ReactContext paramReactContext)
  {
    if (authorities.size() == 0)
    {
      Object localObject = (Application)paramReactContext.getApplicationContext();
      if ((localObject instanceof ShareApplication)) {
        authorities.add(((ShareApplication)localObject).getFileProviderAuthority());
      }
      localObject = authorities;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramReactContext.getPackageName());
      localStringBuilder.append(".rnshare.fileprovider");
      ((ArrayList)localObject).add(localStringBuilder.toString());
    }
  }
  
  public static String getDataColumn(Context paramContext, Uri paramUri, String paramString, String[] paramArrayOfString)
  {
    try
    {
      paramUri = new CursorLoader(paramContext, paramUri, new String[] { "_data" }, paramString, paramArrayOfString, null).loadInBackground();
      paramContext = paramUri;
      if (paramUri != null) {
        try
        {
          boolean bool = paramUri.moveToFirst();
          if (bool)
          {
            paramString = paramUri.getString(paramUri.getColumnIndexOrThrow("_data"));
            if (paramUri == null) {
              break label102;
            }
            paramUri.close();
            return paramString;
          }
        }
        catch (Throwable paramUri) {}
      }
      if (paramUri == null) {
        break label104;
      }
      paramUri.close();
      return null;
    }
    catch (Throwable paramUri)
    {
      paramContext = null;
      if (paramContext != null) {
        paramContext.close();
      }
      throw paramUri;
    }
    label102:
    return paramString;
    label104:
    return null;
  }
  
  public static String getRealPathFromURI(Context paramContext, Uri paramUri)
  {
    int i = Build.VERSION.SDK_INT;
    StringBuilder localStringBuilder = null;
    if ((i >= 19) && (DocumentsContract.isDocumentUri(paramContext, paramUri)))
    {
      if (isExternalStorageDocument(paramUri))
      {
        paramContext = DocumentsContract.getDocumentId(paramUri).split(":");
        paramUri = paramContext[0];
        if ((!"primary".equalsIgnoreCase(paramUri)) && (!"0".equalsIgnoreCase(paramUri)))
        {
          if ("raw".equalsIgnoreCase(paramUri))
          {
            paramUri = new StringBuilder();
            paramUri.append("");
            paramUri.append(paramContext[1]);
            return paramUri.toString();
          }
          if (!TextUtils.isEmpty(paramUri))
          {
            localStringBuilder = new StringBuilder();
            localStringBuilder.append("");
            localStringBuilder.append("/storage/");
            localStringBuilder.append(paramUri);
            localStringBuilder.append("/");
            localStringBuilder.append(paramContext[1]);
            return localStringBuilder.toString();
          }
        }
        else
        {
          paramUri = new StringBuilder();
          paramUri.append("");
          paramUri.append(Environment.getExternalStorageDirectory());
          paramUri.append("/");
          paramUri.append(paramContext[1]);
          return paramUri.toString();
        }
      }
      else
      {
        if (isDownloadsDocument(paramUri))
        {
          paramUri = DocumentsContract.getDocumentId(paramUri);
          if (paramUri.startsWith("raw:"))
          {
            paramContext = new StringBuilder();
            paramContext.append("");
            paramContext.append(paramUri.replaceFirst("raw:", ""));
            return paramContext.toString();
          }
          paramUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(paramUri).longValue());
          localStringBuilder = new StringBuilder();
          localStringBuilder.append("");
          localStringBuilder.append(getDataColumn(paramContext, paramUri, null, null));
          return localStringBuilder.toString();
        }
        if (isMediaDocument(paramUri))
        {
          Object localObject1 = DocumentsContract.getDocumentId(paramUri).split(":");
          Object localObject2 = localObject1[0];
          if ("image".equals(localObject2))
          {
            paramUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
          }
          else if ("video".equals(localObject2))
          {
            paramUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
          }
          else if ("audio".equals(localObject2))
          {
            paramUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
          }
          else
          {
            paramUri = localStringBuilder;
            if ("raw".equalsIgnoreCase(localObject2))
            {
              paramContext = new StringBuilder();
              paramContext.append("");
              paramContext.append(localObject1[1]);
              return paramContext.toString();
            }
          }
          localStringBuilder = localObject1[1];
          localObject1 = new StringBuilder();
          ((StringBuilder)localObject1).append("");
          ((StringBuilder)localObject1).append(getDataColumn(paramContext, paramUri, "_id=?", new String[] { localStringBuilder }));
          return ((StringBuilder)localObject1).toString();
        }
      }
    }
    else
    {
      if ("content".equalsIgnoreCase(paramUri.getScheme()))
      {
        if (isGooglePhotosUri(paramUri)) {
          return paramUri.getLastPathSegment();
        }
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("");
        localStringBuilder.append(getDataColumn(paramContext, paramUri, null, null));
        return localStringBuilder.toString();
      }
      if ("file".equalsIgnoreCase(paramUri.getScheme())) {
        return paramUri.getPath();
      }
    }
    return null;
  }
  
  public static boolean isDownloadsDocument(Uri paramUri)
  {
    return "com.android.providers.downloads.documents".equals(paramUri.getAuthority());
  }
  
  public static boolean isExternalStorageDocument(Uri paramUri)
  {
    return "com.android.externalstorage.documents".equals(paramUri.getAuthority());
  }
  
  public static boolean isGooglePhotosUri(Uri paramUri)
  {
    return "com.google.android.apps.photos.content".equals(paramUri.getAuthority());
  }
  
  public static boolean isMediaDocument(Uri paramUri)
  {
    return "com.android.providers.media.documents".equals(paramUri.getAuthority());
  }
}
