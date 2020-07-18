package androidx.documentfile.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.RequiresApi;

@RequiresApi(19)
class DocumentsContractApi19
{
  private static final int FLAG_VIRTUAL_DOCUMENT = 512;
  private static final String TAG = "DocumentFile";
  
  private DocumentsContractApi19() {}
  
  public static boolean canRead(Context paramContext, Uri paramUri)
  {
    if (paramContext.checkCallingOrSelfUriPermission(paramUri, 1) != 0) {
      return false;
    }
    return !TextUtils.isEmpty(getRawType(paramContext, paramUri));
  }
  
  public static boolean canWrite(Context paramContext, Uri paramUri)
  {
    if (paramContext.checkCallingOrSelfUriPermission(paramUri, 2) != 0) {
      return false;
    }
    String str = getRawType(paramContext, paramUri);
    int i = queryForInt(paramContext, paramUri, "flags", 0);
    if (TextUtils.isEmpty(str)) {
      return false;
    }
    if ((i & 0x4) != 0) {
      return true;
    }
    if (("vnd.android.document/directory".equals(str)) && ((i & 0x8) != 0)) {
      return true;
    }
    return (!TextUtils.isEmpty(str)) && ((i & 0x2) != 0);
  }
  
  private static void closeQuietly(AutoCloseable paramAutoCloseable)
  {
    if (paramAutoCloseable != null) {
      try
      {
        paramAutoCloseable.close();
        return;
      }
      catch (RuntimeException paramAutoCloseable)
      {
        throw paramAutoCloseable;
      }
      catch (Exception paramAutoCloseable) {}
    }
  }
  
  public static boolean exists(Context paramContext, Uri paramUri)
  {
    ContentResolver localContentResolver = paramContext.getContentResolver();
    boolean bool = false;
    StringBuilder localStringBuilder = null;
    paramContext = null;
    try
    {
      paramUri = localContentResolver.query(paramUri, new String[] { "document_id" }, null, null, null);
      try
      {
        int i = paramUri.getCount();
        if (i > 0) {
          bool = true;
        }
        closeQuietly(paramUri);
        return bool;
      }
      catch (Throwable localThrowable)
      {
        paramContext = paramUri;
        paramUri = localThrowable;
        break label125;
      }
      catch (Exception localException1) {}
      paramContext = paramUri;
    }
    catch (Throwable paramUri) {}catch (Exception localException2)
    {
      paramUri = localStringBuilder;
    }
    localStringBuilder = new StringBuilder();
    paramContext = paramUri;
    localStringBuilder.append("Failed query: ");
    paramContext = paramUri;
    localStringBuilder.append(localException2);
    paramContext = paramUri;
    Log.w("DocumentFile", localStringBuilder.toString());
    closeQuietly(paramUri);
    return false;
    label125:
    closeQuietly(paramContext);
    throw paramUri;
  }
  
  public static long getFlags(Context paramContext, Uri paramUri)
  {
    return queryForLong(paramContext, paramUri, "flags", 0L);
  }
  
  public static String getName(Context paramContext, Uri paramUri)
  {
    return queryForString(paramContext, paramUri, "_display_name", null);
  }
  
  private static String getRawType(Context paramContext, Uri paramUri)
  {
    return queryForString(paramContext, paramUri, "mime_type", null);
  }
  
  public static String getType(Context paramContext, Uri paramUri)
  {
    paramContext = getRawType(paramContext, paramUri);
    if ("vnd.android.document/directory".equals(paramContext)) {
      return null;
    }
    return paramContext;
  }
  
  public static boolean isDirectory(Context paramContext, Uri paramUri)
  {
    return "vnd.android.document/directory".equals(getRawType(paramContext, paramUri));
  }
  
  public static boolean isFile(Context paramContext, Uri paramUri)
  {
    paramContext = getRawType(paramContext, paramUri);
    return (!"vnd.android.document/directory".equals(paramContext)) && (!TextUtils.isEmpty(paramContext));
  }
  
  public static boolean isVirtual(Context paramContext, Uri paramUri)
  {
    if (!DocumentsContract.isDocumentUri(paramContext, paramUri)) {
      return false;
    }
    return (getFlags(paramContext, paramUri) & 0x200) != 0L;
  }
  
  public static long lastModified(Context paramContext, Uri paramUri)
  {
    return queryForLong(paramContext, paramUri, "last_modified", 0L);
  }
  
  public static long length(Context paramContext, Uri paramUri)
  {
    return queryForLong(paramContext, paramUri, "_size", 0L);
  }
  
  private static int queryForInt(Context paramContext, Uri paramUri, String paramString, int paramInt)
  {
    return (int)queryForLong(paramContext, paramUri, paramString, paramInt);
  }
  
  private static long queryForLong(Context paramContext, Uri paramUri, String paramString, long paramLong)
  {
    ContentResolver localContentResolver = paramContext.getContentResolver();
    StringBuilder localStringBuilder = null;
    paramContext = null;
    try
    {
      paramUri = localContentResolver.query(paramUri, new String[] { paramString }, null, null, null);
      try
      {
        boolean bool = paramUri.moveToFirst();
        if (bool)
        {
          bool = paramUri.isNull(0);
          if (!bool)
          {
            long l = paramUri.getLong(0);
            closeQuietly(paramUri);
            return l;
          }
        }
        closeQuietly(paramUri);
        return paramLong;
      }
      catch (Throwable paramString)
      {
        paramContext = paramUri;
        paramUri = paramString;
        break label147;
      }
      catch (Exception paramString) {}
      paramContext = paramUri;
    }
    catch (Throwable paramUri) {}catch (Exception paramString)
    {
      paramUri = localStringBuilder;
    }
    localStringBuilder = new StringBuilder();
    paramContext = paramUri;
    localStringBuilder.append("Failed query: ");
    paramContext = paramUri;
    localStringBuilder.append(paramString);
    paramContext = paramUri;
    Log.w("DocumentFile", localStringBuilder.toString());
    closeQuietly(paramUri);
    return paramLong;
    label147:
    closeQuietly(paramContext);
    throw paramUri;
  }
  
  private static String queryForString(Context paramContext, Uri paramUri, String paramString1, String paramString2)
  {
    ContentResolver localContentResolver = paramContext.getContentResolver();
    StringBuilder localStringBuilder = null;
    paramContext = null;
    try
    {
      paramUri = localContentResolver.query(paramUri, new String[] { paramString1 }, null, null, null);
      try
      {
        boolean bool = paramUri.moveToFirst();
        if (bool)
        {
          bool = paramUri.isNull(0);
          if (!bool)
          {
            paramContext = paramUri.getString(0);
            closeQuietly(paramUri);
            return paramContext;
          }
        }
        closeQuietly(paramUri);
        return paramString2;
      }
      catch (Throwable paramString1)
      {
        paramContext = paramUri;
        paramUri = paramString1;
        break label145;
      }
      catch (Exception paramString1) {}
      paramContext = paramUri;
    }
    catch (Throwable paramUri) {}catch (Exception paramString1)
    {
      paramUri = localStringBuilder;
    }
    localStringBuilder = new StringBuilder();
    paramContext = paramUri;
    localStringBuilder.append("Failed query: ");
    paramContext = paramUri;
    localStringBuilder.append(paramString1);
    paramContext = paramUri;
    Log.w("DocumentFile", localStringBuilder.toString());
    closeQuietly(paramUri);
    return paramString2;
    label145:
    closeQuietly(paramContext);
    throw paramUri;
  }
}
