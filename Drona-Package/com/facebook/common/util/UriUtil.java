package com.facebook.common.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.provider.ContactsContract;
import android.provider.MediaStore.Images.Media;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class UriUtil
{
  public static final String DATA_SCHEME = "data";
  public static final String HTTPS_SCHEME = "https";
  public static final String HTTP_SCHEME = "http";
  public static final String LOCAL_ASSET_SCHEME = "asset";
  private static final Uri LOCAL_CONTACT_IMAGE_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "display_photo");
  public static final String LOCAL_CONTENT_SCHEME = "content";
  public static final String LOCAL_FILE_SCHEME = "file";
  public static final String LOCAL_RESOURCE_SCHEME = "res";
  public static final String QUALIFIED_RESOURCE_SCHEME = "android.resource";
  
  public UriUtil() {}
  
  public static String getRealPathFromUri(ContentResolver paramContentResolver, Uri paramUri)
  {
    boolean bool = isLocalContentUri(paramUri);
    Object localObject = null;
    if (bool) {
      try
      {
        Cursor localCursor = paramContentResolver.query(paramUri, null, null, null, null);
        paramUri = localCursor;
        paramContentResolver = localObject;
        if (localCursor != null) {
          try
          {
            bool = localCursor.moveToFirst();
            paramContentResolver = localObject;
            if (bool)
            {
              int i = localCursor.getColumnIndex("_data");
              paramContentResolver = localObject;
              if (i != -1) {
                paramContentResolver = localCursor.getString(i);
              }
            }
          }
          catch (Throwable paramContentResolver)
          {
            break label102;
          }
        }
        paramUri = paramContentResolver;
        if (localCursor == null) {
          break label126;
        }
        localCursor.close();
        return paramContentResolver;
      }
      catch (Throwable paramContentResolver)
      {
        paramUri = null;
        label102:
        if (paramUri != null) {
          paramUri.close();
        }
        throw paramContentResolver;
      }
    }
    if (isLocalFileUri(paramUri))
    {
      paramUri = paramUri.getPath();
      label126:
      return paramUri;
    }
    return null;
  }
  
  public static String getSchemeOrNull(Uri paramUri)
  {
    if (paramUri == null) {
      return null;
    }
    return paramUri.getScheme();
  }
  
  public static Uri getUriForFile(File paramFile)
  {
    return Uri.fromFile(paramFile);
  }
  
  public static Uri getUriForQualifiedResource(String paramString, int paramInt)
  {
    return new Uri.Builder().scheme("android.resource").authority(paramString).path(String.valueOf(paramInt)).build();
  }
  
  public static Uri getUriForResourceId(int paramInt)
  {
    return new Uri.Builder().scheme("res").path(String.valueOf(paramInt)).build();
  }
  
  public static boolean isDataUri(Uri paramUri)
  {
    return "data".equals(getSchemeOrNull(paramUri));
  }
  
  public static boolean isLocalAssetUri(Uri paramUri)
  {
    return "asset".equals(getSchemeOrNull(paramUri));
  }
  
  public static boolean isLocalCameraUri(Uri paramUri)
  {
    paramUri = paramUri.toString();
    return (paramUri.startsWith(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())) || (paramUri.startsWith(MediaStore.Images.Media.INTERNAL_CONTENT_URI.toString()));
  }
  
  public static boolean isLocalContactUri(Uri paramUri)
  {
    return (isLocalContentUri(paramUri)) && ("com.android.contacts".equals(paramUri.getAuthority())) && (!paramUri.getPath().startsWith(LOCAL_CONTACT_IMAGE_URI.getPath()));
  }
  
  public static boolean isLocalContentUri(Uri paramUri)
  {
    return "content".equals(getSchemeOrNull(paramUri));
  }
  
  public static boolean isLocalFileUri(Uri paramUri)
  {
    return "file".equals(getSchemeOrNull(paramUri));
  }
  
  public static boolean isLocalResourceUri(Uri paramUri)
  {
    return "res".equals(getSchemeOrNull(paramUri));
  }
  
  public static boolean isNetworkUri(Uri paramUri)
  {
    paramUri = getSchemeOrNull(paramUri);
    return ("https".equals(paramUri)) || ("http".equals(paramUri));
  }
  
  public static boolean isQualifiedResourceUri(Uri paramUri)
  {
    return "android.resource".equals(getSchemeOrNull(paramUri));
  }
  
  public static Uri parseUriOrNull(String paramString)
  {
    if (paramString != null) {
      return Uri.parse(paramString);
    }
    return null;
  }
  
  public static URL uriToUrl(Uri paramUri)
  {
    if (paramUri == null) {
      return null;
    }
    try
    {
      paramUri = new URL(paramUri.toString());
      return paramUri;
    }
    catch (MalformedURLException paramUri)
    {
      throw new RuntimeException(paramUri);
    }
  }
}
