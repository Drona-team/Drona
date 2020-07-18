package com.RNFetchBlob.Utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video.Media;
import com.RNFetchBlob.RNFetchBlobUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class PathResolver
{
  public PathResolver() {}
  
  private static String getContentName(ContentResolver paramContentResolver, Uri paramUri)
  {
    paramContentResolver = paramContentResolver.query(paramUri, null, null, null, null);
    paramContentResolver.moveToFirst();
    int i = paramContentResolver.getColumnIndex("_display_name");
    if (i >= 0)
    {
      paramUri = paramContentResolver.getString(i);
      paramContentResolver.close();
      return paramUri;
    }
    return null;
  }
  
  public static String getDataColumn(Context paramContext, Uri paramUri, String paramString, String[] paramArrayOfString)
  {
    try
    {
      paramString = paramContext.getContentResolver().query(paramUri, new String[] { "_data" }, paramString, paramArrayOfString, null);
      paramUri = paramString;
      if (paramString != null) {
        paramContext = paramUri;
      }
      boolean bool;
      label91:
      paramUri.close();
    }
    catch (Throwable paramUri)
    {
      try
      {
        try
        {
          bool = paramString.moveToFirst();
          if (bool)
          {
            paramContext = paramUri;
            paramArrayOfString = paramString.getString(paramString.getColumnIndexOrThrow("_data"));
            paramContext = paramArrayOfString;
          }
        }
        catch (Exception paramString)
        {
          break label91;
        }
        paramContext = null;
        if (paramString == null) {
          break label122;
        }
        paramString.close();
        return paramContext;
      }
      catch (Throwable paramUri)
      {
        if (paramContext == null) {
          break label120;
        }
        paramContext.close();
        throw paramUri;
      }
      paramUri = paramUri;
      paramContext = null;
    }
    catch (Exception paramString)
    {
      paramUri = null;
      paramContext = paramUri;
      paramString.printStackTrace();
      if (paramUri == null) {
        break label124;
      }
    }
    return null;
    label120:
    label122:
    return paramContext;
    label124:
    return null;
  }
  
  public static String getRealPathFromURI(Context paramContext, Uri paramUri)
  {
    int i;
    if (Build.VERSION.SDK_INT >= 19) {
      i = 1;
    } else {
      i = 0;
    }
    InputStream localInputStream = null;
    if ((i != 0) && (DocumentsContract.isDocumentUri(paramContext, paramUri)))
    {
      if (isExternalStorageDocument(paramUri))
      {
        paramContext = DocumentsContract.getDocumentId(paramUri).split(":");
        if (!"primary".equalsIgnoreCase(paramContext[0])) {
          break label434;
        }
        paramUri = new StringBuilder();
        paramUri.append(Environment.getExternalStorageDirectory());
        paramUri.append("/");
        paramUri.append(paramContext[1]);
        return paramUri.toString();
      }
      if (!isDownloadsDocument(paramUri)) {}
    }
    Object localObject1;
    label434:
    try
    {
      paramUri = DocumentsContract.getDocumentId(paramUri);
      if (paramUri != null)
      {
        boolean bool = paramUri.startsWith("raw:/");
        if (bool)
        {
          paramContext = Uri.parse(paramUri).getPath();
          return paramContext;
        }
      }
      paramContext = getDataColumn(paramContext, ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(paramUri).longValue()), null, null);
      return paramContext;
    }
    catch (Exception paramContext) {}
    if (isMediaDocument(paramUri))
    {
      localObject1 = DocumentsContract.getDocumentId(paramUri).split(":");
      Object localObject2 = localObject1[0];
      if ("image".equals(localObject2))
      {
        paramUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
      }
      else if ("video".equals(localObject2))
      {
        paramUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
      }
      else
      {
        paramUri = localInputStream;
        if ("audio".equals(localObject2)) {
          paramUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
      }
      return getDataColumn(paramContext, paramUri, "_id=?", new String[] { localObject1[1] });
    }
    if ("content".equalsIgnoreCase(paramUri.getScheme()))
    {
      if (isGooglePhotosUri(paramUri)) {
        return paramUri.getLastPathSegment();
      }
      return getDataColumn(paramContext, paramUri, null, null);
    }
    try
    {
      localInputStream = paramContext.getContentResolver().openInputStream(paramUri);
      if (localInputStream == null) {
        break label437;
      }
      paramUri = getContentName(paramContext.getContentResolver(), paramUri);
      if (paramUri == null) {
        break label437;
      }
      paramContext = new File(paramContext.getCacheDir(), paramUri);
      paramUri = new FileOutputStream(paramContext);
      localObject1 = new byte['?'];
      for (;;)
      {
        i = localInputStream.read((byte[])localObject1);
        if (i <= 0) {
          break;
        }
        paramUri.write((byte[])localObject1);
      }
      paramUri.close();
      localInputStream.close();
      paramContext = paramContext.getAbsolutePath();
      return paramContext;
    }
    catch (Exception paramContext)
    {
      RNFetchBlobUtils.emitWarningEvent(paramContext.toString());
      return null;
    }
    if ("content".equalsIgnoreCase(paramUri.getScheme()))
    {
      if (isGooglePhotosUri(paramUri)) {
        return paramUri.getLastPathSegment();
      }
      return getDataColumn(paramContext, paramUri, null, null);
    }
    if ("file".equalsIgnoreCase(paramUri.getScheme()))
    {
      return paramUri.getPath();
      return null;
    }
    label437:
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
