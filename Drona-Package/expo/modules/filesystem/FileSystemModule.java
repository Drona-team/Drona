package expo.modules.filesystem;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BaseBundle;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Base64;
import android.util.Log;
import androidx.core.content.FileProvider;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.Response.Builder;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.unimodules.core.ExportedModule;
import org.unimodules.core.ModuleRegistry;
import org.unimodules.core.Promise;
import org.unimodules.core.interfaces.ActivityProvider;
import org.unimodules.core.interfaces.services.EventEmitter;
import org.unimodules.interfaces.filesystem.FilePermissionModuleInterface;
import org.unimodules.interfaces.filesystem.Permission;

public class FileSystemModule
  extends ExportedModule
{
  private static final String EXDownloadProgressEventName = "Exponent.downloadProgress";
  private static final String HEADER_KEY = "headers";
  private static final long MIN_EVENT_DT_MS = 100L;
  private static final String NAME = "ExponentFileSystem";
  private static final String WRITE = "FileSystemModule";
  private OkHttpClient mClient;
  private final Map<String, DownloadResumable> mDownloadResumableMap = new HashMap();
  private ModuleRegistry mModuleRegistry;
  
  public FileSystemModule(Context paramContext)
  {
    super(paramContext);
    try
    {
      ensureDirExists(getContext().getFilesDir());
      ensureDirExists(getContext().getCacheDir());
      return;
    }
    catch (IOException paramContext)
    {
      paramContext.printStackTrace();
    }
  }
  
  private void checkIfFileDirExists(Uri paramUri)
    throws IOException
  {
    paramUri = uriToFile(paramUri);
    if (paramUri.getParentFile().exists()) {
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Directory for ");
    localStringBuilder.append(paramUri.getPath());
    localStringBuilder.append(" doesn't exist.");
    throw new IOException(localStringBuilder.toString());
  }
  
  private Uri contentUriFromFile(File paramFile)
  {
    Object localObject = mModuleRegistry;
    try
    {
      localObject = ((ModuleRegistry)localObject).getModule(ActivityProvider.class);
      localObject = (ActivityProvider)localObject;
      localObject = ((ActivityProvider)localObject).getCurrentActivity().getApplication();
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(((ContextWrapper)localObject).getPackageName());
      localStringBuilder.append(".FileSystemFileProvider");
      paramFile = FileProvider.getUriForFile((Context)localObject, localStringBuilder.toString(), paramFile);
      return paramFile;
    }
    catch (Exception paramFile)
    {
      throw paramFile;
    }
  }
  
  private void ensureDirExists(File paramFile)
    throws IOException
  {
    if (!paramFile.isDirectory())
    {
      if (paramFile.mkdirs()) {
        return;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Couldn't create directory '");
      localStringBuilder.append(paramFile);
      localStringBuilder.append("'");
      throw new IOException(localStringBuilder.toString());
    }
  }
  
  private void ensurePermission(Uri paramUri, Permission paramPermission)
    throws IOException
  {
    if (paramPermission.equals(Permission.READ))
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("Location '");
      localStringBuilder.append(paramUri);
      localStringBuilder.append("' isn't readable.");
      ensurePermission(paramUri, paramPermission, localStringBuilder.toString());
    }
    if (paramPermission.equals(Permission.WRITE))
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("Location '");
      localStringBuilder.append(paramUri);
      localStringBuilder.append("' isn't writable.");
      ensurePermission(paramUri, paramPermission, localStringBuilder.toString());
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Location '");
    localStringBuilder.append(paramUri);
    localStringBuilder.append("' doesn't have permission '");
    localStringBuilder.append(paramPermission.name());
    localStringBuilder.append("'.");
    ensurePermission(paramUri, paramPermission, localStringBuilder.toString());
  }
  
  private void ensurePermission(Uri paramUri, Permission paramPermission, String paramString)
    throws IOException
  {
    if (permissionsForUri(paramUri).contains(paramPermission)) {
      return;
    }
    throw new IOException(paramString);
  }
  
  private void forceDelete(File paramFile)
    throws IOException
  {
    if (paramFile.isDirectory())
    {
      File[] arrayOfFile = paramFile.listFiles();
      if (arrayOfFile != null)
      {
        Object localObject = null;
        int j = arrayOfFile.length;
        int i = 0;
        while (i < j)
        {
          File localFile = arrayOfFile[i];
          try
          {
            forceDelete(localFile);
          }
          catch (IOException localIOException) {}
          i += 1;
        }
        if (localIOException == null)
        {
          if (paramFile.delete()) {
            return;
          }
          localStringBuilder = new StringBuilder();
          localStringBuilder.append("Unable to delete directory ");
          localStringBuilder.append(paramFile);
          localStringBuilder.append(".");
          throw new IOException(localStringBuilder.toString());
        }
        throw localStringBuilder;
      }
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("Failed to list contents of ");
      localStringBuilder.append(paramFile);
      throw new IOException(localStringBuilder.toString());
    }
    if (paramFile.delete()) {
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Unable to delete file: ");
    localStringBuilder.append(paramFile);
    throw new IOException(localStringBuilder.toString());
  }
  
  /* Error */
  private static byte[] getInputStreamBytes(InputStream paramInputStream)
    throws IOException
  {
    // Byte code:
    //   0: new 253	java/io/ByteArrayOutputStream
    //   3: dup
    //   4: invokespecial 254	java/io/ByteArrayOutputStream:<init>	()V
    //   7: astore_2
    //   8: sipush 1024
    //   11: newarray byte
    //   13: astore_3
    //   14: aload_0
    //   15: aload_3
    //   16: invokevirtual 260	java/io/InputStream:read	([B)I
    //   19: istore_1
    //   20: iload_1
    //   21: iconst_m1
    //   22: if_icmpeq +13 -> 35
    //   25: aload_2
    //   26: aload_3
    //   27: iconst_0
    //   28: iload_1
    //   29: invokevirtual 264	java/io/ByteArrayOutputStream:write	([BII)V
    //   32: goto -18 -> 14
    //   35: aload_2
    //   36: invokevirtual 268	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   39: astore_0
    //   40: aload_2
    //   41: invokevirtual 271	java/io/ByteArrayOutputStream:close	()V
    //   44: aload_0
    //   45: areturn
    //   46: astore_0
    //   47: aload_2
    //   48: invokevirtual 271	java/io/ByteArrayOutputStream:close	()V
    //   51: aload_0
    //   52: athrow
    //   53: astore_2
    //   54: aload_0
    //   55: areturn
    //   56: astore_2
    //   57: goto -6 -> 51
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	60	0	paramInputStream	InputStream
    //   19	10	1	i	int
    //   7	41	2	localByteArrayOutputStream	java.io.ByteArrayOutputStream
    //   53	1	2	localIOException1	IOException
    //   56	1	2	localIOException2	IOException
    //   13	14	3	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   14	20	46	java/lang/Throwable
    //   25	32	46	java/lang/Throwable
    //   35	40	46	java/lang/Throwable
    //   40	44	53	java/io/IOException
    //   47	51	56	java/io/IOException
  }
  
  private OkHttpClient getOkHttpClient()
  {
    try
    {
      if (mClient == null)
      {
        localObject = new OkHttpClient.Builder().connectTimeout(60L, TimeUnit.SECONDS).readTimeout(60L, TimeUnit.SECONDS).writeTimeout(60L, TimeUnit.SECONDS);
        CookieHandler localCookieHandler = (CookieHandler)mModuleRegistry.getModule(CookieHandler.class);
        if (localCookieHandler != null) {
          ((OkHttpClient.Builder)localObject).cookieJar((CookieJar)new JavaNetCookieJar(localCookieHandler));
        }
        mClient = ((OkHttpClient.Builder)localObject).build();
      }
      Object localObject = mClient;
      return localObject;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private InputStream openAssetInputStream(Uri paramUri)
    throws IOException
  {
    paramUri = paramUri.getPath().substring(1);
    return getContext().getAssets().open(paramUri);
  }
  
  private EnumSet permissionsForPath(String paramString)
  {
    return ((FilePermissionModuleInterface)mModuleRegistry.getModule(FilePermissionModuleInterface.class)).getPathPermissions(getContext(), paramString);
  }
  
  private EnumSet permissionsForUri(Uri paramUri)
  {
    if ("content".equals(paramUri.getScheme())) {
      return EnumSet.of((Enum)Permission.READ);
    }
    if ("asset".equals(paramUri.getScheme())) {
      return EnumSet.of((Enum)Permission.READ);
    }
    if ("file".equals(paramUri.getScheme())) {
      return permissionsForPath(paramUri.getPath());
    }
    return EnumSet.noneOf(Permission.class);
  }
  
  private String toString(File paramFile)
    throws IOException
  {
    paramFile = new FileInputStream(paramFile);
    try
    {
      String str = String.valueOf(Hex.encodeHex(DigestUtils.md5(paramFile)));
      paramFile.close();
      return str;
    }
    catch (Throwable localThrowable)
    {
      paramFile.close();
      throw localThrowable;
    }
  }
  
  private static Bundle translateHeaders(Headers paramHeaders)
  {
    Bundle localBundle = new Bundle();
    int i = 0;
    while (i < paramHeaders.size())
    {
      String str = paramHeaders.name(i);
      if (localBundle.get(str) != null)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(localBundle.getString(str));
        localStringBuilder.append(", ");
        localStringBuilder.append(paramHeaders.value(i));
        localBundle.putString(str, localStringBuilder.toString());
      }
      else
      {
        localBundle.putString(str, paramHeaders.value(i));
      }
      i += 1;
    }
    return localBundle;
  }
  
  private File uriToFile(Uri paramUri)
  {
    return new File(paramUri.getPath());
  }
  
  public void copyAsync(Map paramMap, Promise paramPromise)
  {
    try
    {
      boolean bool = paramMap.containsKey("from");
      if (!bool)
      {
        paramPromise.reject("E_MISSING_PARAMETER", "`FileSystem.moveAsync` needs a `from` path.");
        return;
      }
      Object localObject = paramMap.get("from");
      localObject = (String)localObject;
      localObject = Uri.parse((String)localObject);
      Permission localPermission = Permission.READ;
      ensurePermission((Uri)localObject, localPermission);
      bool = paramMap.containsKey("to");
      if (!bool)
      {
        paramPromise.reject("E_MISSING_PARAMETER", "`FileSystem.moveAsync` needs a `to` path.");
        return;
      }
      paramMap = paramMap.get("to");
      paramMap = (String)paramMap;
      paramMap = Uri.parse(paramMap);
      localPermission = Permission.WRITE;
      ensurePermission(paramMap, localPermission);
      bool = "file".equals(((Uri)localObject).getScheme());
      if (bool)
      {
        localObject = uriToFile((Uri)localObject);
        paramMap = uriToFile(paramMap);
        bool = ((File)localObject).isDirectory();
        if (bool)
        {
          FileUtils.copyDirectory((File)localObject, paramMap);
          paramPromise.resolve(null);
          return;
        }
        FileUtils.copyFile((File)localObject, paramMap);
        paramPromise.resolve(null);
        return;
      }
      bool = "content".equals(((Uri)localObject).getScheme());
      if (bool)
      {
        localObject = getContext().getContentResolver().openInputStream((Uri)localObject);
        IOUtils.copy((InputStream)localObject, new FileOutputStream(uriToFile(paramMap)));
        paramPromise.resolve(null);
        return;
      }
      bool = "asset".equals(((Uri)localObject).getScheme());
      if (bool)
      {
        localObject = openAssetInputStream((Uri)localObject);
        IOUtils.copy((InputStream)localObject, new FileOutputStream(uriToFile(paramMap)));
        paramPromise.resolve(null);
        return;
      }
      paramMap = new StringBuilder();
      paramMap.append("Unsupported scheme for location '");
      paramMap.append(localObject);
      paramMap.append("'.");
      paramMap = new IOException(paramMap.toString());
      throw paramMap;
    }
    catch (Exception paramMap)
    {
      Log.e(WRITE, paramMap.getMessage());
      paramPromise.reject(paramMap);
    }
  }
  
  public void deleteAsync(String paramString, Map paramMap, Promise paramPromise)
  {
    try
    {
      paramString = Uri.parse(paramString);
      Object localObject = Uri.withAppendedPath(paramString, "..");
      Permission localPermission = Permission.WRITE;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Location '");
      localStringBuilder.append(paramString);
      localStringBuilder.append("' isn't deletable.");
      ensurePermission((Uri)localObject, localPermission, localStringBuilder.toString());
      boolean bool = "file".equals(paramString.getScheme());
      if (bool)
      {
        localObject = uriToFile(paramString);
        bool = ((File)localObject).exists();
        if (bool)
        {
          if (Build.VERSION.SDK_INT >= 26) {
            FileUtils.forceDelete((File)localObject);
          } else {
            forceDelete((File)localObject);
          }
          paramPromise.resolve(null);
          return;
        }
        bool = paramMap.containsKey("idempotent");
        if (bool)
        {
          paramMap = paramMap.get("idempotent");
          paramMap = (Boolean)paramMap;
          bool = paramMap.booleanValue();
          if (bool)
          {
            paramPromise.resolve(null);
            return;
          }
        }
        paramMap = new StringBuilder();
        paramMap.append("File '");
        paramMap.append(paramString);
        paramMap.append("' could not be deleted because it could not be found");
        paramPromise.reject("E_FILE_NOT_FOUND", paramMap.toString());
        return;
      }
      paramMap = new StringBuilder();
      paramMap.append("Unsupported scheme for location '");
      paramMap.append(paramString);
      paramMap.append("'.");
      paramString = new IOException(paramMap.toString());
      throw paramString;
    }
    catch (Exception paramString)
    {
      Log.e(WRITE, paramString.getMessage());
      paramPromise.reject(paramString);
    }
  }
  
  public void downloadAsync(String paramString1, final String paramString2, final Map paramMap, final Promise paramPromise)
  {
    try
    {
      paramString2 = Uri.parse(paramString2);
      Object localObject1 = Permission.WRITE;
      ensurePermission(paramString2, (Permission)localObject1);
      checkIfFileDirExists(paramString2);
      boolean bool = paramString1.contains(":");
      if (!bool)
      {
        localObject1 = getContext();
        int i = ((Context)localObject1).getResources().getIdentifier(paramString1, "raw", ((Context)localObject1).getPackageName());
        localObject1 = Okio.buffer(Okio.source(((Context)localObject1).getResources().openRawResource(i)));
        paramString1 = uriToFile(paramString2);
        paramString1.delete();
        paramString2 = Okio.buffer(Okio.sink(paramString1));
        localObject1 = (Source)localObject1;
        paramString2.writeAll((Source)localObject1);
        paramString2.close();
        paramString2 = new Bundle();
        paramString2.putString("uri", Uri.fromFile(paramString1).toString());
        if (paramMap != null)
        {
          bool = paramMap.containsKey("md5");
          if (bool)
          {
            paramMap = paramMap.get("md5");
            paramMap = (Boolean)paramMap;
            bool = paramMap.booleanValue();
            if (bool) {
              paramString2.putString("md5", toString(paramString1));
            }
          }
        }
        paramPromise.resolve(paramString2);
        return;
      }
      bool = "file".equals(paramString2.getScheme());
      if (bool)
      {
        paramString1 = new Request.Builder().url(paramString1);
        if (paramMap != null)
        {
          bool = paramMap.containsKey("headers");
          if (bool)
          {
            localObject1 = paramMap.get("headers");
            localObject1 = (Map)localObject1;
            Iterator localIterator = ((Map)localObject1).keySet().iterator();
            for (;;)
            {
              bool = localIterator.hasNext();
              if (!bool) {
                break;
              }
              Object localObject2 = localIterator.next();
              localObject2 = (String)localObject2;
              paramString1.addHeader((String)localObject2, ((Map)localObject1).get(localObject2).toString());
            }
          }
        }
        paramString1 = getOkHttpClient().newCall(paramString1.build());
        paramString1.enqueue(new Callback()
        {
          public void onFailure(Call paramAnonymousCall, IOException paramAnonymousIOException)
          {
            Log.e(FileSystemModule.WRITE, String.valueOf(paramAnonymousIOException.getMessage()));
            paramPromise.reject(paramAnonymousIOException);
          }
          
          public void onResponse(Call paramAnonymousCall, Response paramAnonymousResponse)
            throws IOException
          {
            Object localObject1 = FileSystemModule.this;
            paramAnonymousCall = this;
            localObject1 = ((FileSystemModule)localObject1).uriToFile(paramString2);
            ((File)localObject1).delete();
            Object localObject2 = Okio.buffer(Okio.sink((File)localObject1));
            ((BufferedSink)localObject2).writeAll((Source)paramAnonymousResponse.body().source());
            ((BufferedSink)localObject2).close();
            localObject2 = new Bundle();
            ((BaseBundle)localObject2).putString("uri", Uri.fromFile((File)localObject1).toString());
            if ((paramMap != null) && (paramMap.containsKey("md5")) && (((Boolean)paramMap.get("md5")).booleanValue())) {
              ((BaseBundle)localObject2).putString("md5", this$0.toString((File)localObject1));
            }
            ((BaseBundle)localObject2).putInt("status", paramAnonymousResponse.code());
            ((Bundle)localObject2).putBundle("headers", FileSystemModule.translateHeaders(paramAnonymousResponse.headers()));
            paramAnonymousResponse.close();
            paramPromise.resolve(localObject2);
          }
        });
        return;
      }
      paramString1 = new StringBuilder();
      paramString1.append("Unsupported scheme for location '");
      paramString1.append(paramString2);
      paramString1.append("'.");
      paramString1 = new IOException(paramString1.toString());
      throw paramString1;
    }
    catch (Exception paramString1)
    {
      Log.e(WRITE, paramString1.getMessage());
      paramPromise.reject(paramString1);
    }
  }
  
  public void downloadResumablePauseAsync(String paramString, Promise paramPromise)
  {
    paramString = (DownloadResumable)mDownloadResumableMap.get(paramString);
    if (paramString != null)
    {
      call.cancel();
      mDownloadResumableMap.remove(uuid);
      paramString = fileUri;
      try
      {
        paramString = uriToFile(paramString);
        Bundle localBundle = new Bundle();
        localBundle.putString("resumeData", String.valueOf(paramString.length()));
        paramPromise.resolve(localBundle);
        return;
      }
      catch (Exception paramString)
      {
        Log.e(WRITE, paramString.getMessage());
        paramPromise.reject(paramString);
        return;
      }
    }
    paramString = new IOException("No download object available");
    Log.e(WRITE, paramString.getMessage());
    paramPromise.reject(paramString);
  }
  
  public void downloadResumableStartAsync(String paramString1, String paramString2, final String paramString3, Map paramMap, final String paramString4, Promise paramPromise)
  {
    try
    {
      paramString2 = Uri.parse(paramString2);
      checkIfFileDirExists(paramString2);
      final boolean bool1 = "file".equals(paramString2.getScheme());
      if (bool1)
      {
        if (paramString4 != null) {
          bool1 = true;
        } else {
          bool1 = false;
        }
        Object localObject1 = new ProgressListener()
        {
          long mLastUpdate = -1L;
          
          public void update(long paramAnonymousLong1, long paramAnonymousLong2, boolean paramAnonymousBoolean)
          {
            EventEmitter localEventEmitter = (EventEmitter)mModuleRegistry.getModule(EventEmitter.class);
            if (localEventEmitter != null)
            {
              Bundle localBundle1 = new Bundle();
              Bundle localBundle2 = new Bundle();
              long l = paramAnonymousLong1;
              if (bool1) {
                l = paramAnonymousLong1 + Long.parseLong(paramString4);
              }
              paramAnonymousLong1 = paramAnonymousLong2;
              if (bool1) {
                paramAnonymousLong1 = paramAnonymousLong2 + Long.parseLong(paramString4);
              }
              paramAnonymousLong2 = System.currentTimeMillis();
              if ((paramAnonymousLong2 > mLastUpdate + 100L) || (l == paramAnonymousLong1))
              {
                mLastUpdate = paramAnonymousLong2;
                localBundle2.putDouble("totalBytesWritten", l);
                localBundle2.putDouble("totalBytesExpectedToWrite", paramAnonymousLong1);
                localBundle1.putString("uuid", paramString3);
                localBundle1.putBundle("data", localBundle2);
                localEventEmitter.emit("Exponent.downloadProgress", localBundle1);
              }
            }
          }
        };
        Object localObject2 = getOkHttpClient().newBuilder();
        localObject1 = ((OkHttpClient.Builder)localObject2).addNetworkInterceptor(new Interceptor()
        {
          public Response intercept(Interceptor.Chain paramAnonymousChain)
            throws IOException
          {
            paramAnonymousChain = paramAnonymousChain.proceed(paramAnonymousChain.request());
            return paramAnonymousChain.newBuilder().body(new FileSystemModule.ProgressResponseBody(paramAnonymousChain.body(), val$progressListener)).build();
          }
        }).build();
        localObject2 = new Request.Builder();
        Object localObject3;
        if (bool1)
        {
          localObject3 = new StringBuilder();
          ((StringBuilder)localObject3).append("bytes=");
          ((StringBuilder)localObject3).append(paramString4);
          ((StringBuilder)localObject3).append("-");
          ((Request.Builder)localObject2).addHeader("Range", ((StringBuilder)localObject3).toString());
        }
        if (paramMap != null)
        {
          boolean bool2 = paramMap.containsKey("headers");
          if (bool2)
          {
            paramString4 = paramMap.get("headers");
            paramString4 = (Map)paramString4;
            localObject3 = paramString4.keySet().iterator();
            for (;;)
            {
              bool2 = ((Iterator)localObject3).hasNext();
              if (!bool2) {
                break;
              }
              Object localObject4 = ((Iterator)localObject3).next();
              localObject4 = (String)localObject4;
              ((Request.Builder)localObject2).addHeader((String)localObject4, paramString4.get(localObject4).toString());
            }
          }
        }
        paramString4 = ((OkHttpClient)localObject1).newCall(((Request.Builder)localObject2).url(paramString1).build());
        paramString1 = new DownloadResumable(paramString3, paramString1, paramString2, paramString4);
        localObject1 = mDownloadResumableMap;
        ((Map)localObject1).put(paramString3, paramString1);
        paramString1 = uriToFile(paramString2);
        paramString1 = new DownloadResumableTaskParams(paramMap, paramString4, paramString1, bool1, paramPromise);
        paramString2 = new DownloadResumableTask(null);
        paramString2.execute(new DownloadResumableTaskParams[] { paramString1 });
        return;
      }
      paramString1 = new StringBuilder();
      paramString1.append("Unsupported scheme for location '");
      paramString1.append(paramString2);
      paramString1.append("'.");
      paramString1 = new IOException(paramString1.toString());
      throw paramString1;
    }
    catch (Exception paramString1)
    {
      Log.e(WRITE, paramString1.getMessage());
      paramPromise.reject(paramString1);
    }
  }
  
  public Map getConstants()
  {
    HashMap localHashMap = new HashMap();
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(Uri.fromFile(getContext().getFilesDir()).toString());
    localStringBuilder.append("/");
    localHashMap.put("documentDirectory", localStringBuilder.toString());
    localStringBuilder = new StringBuilder();
    localStringBuilder.append(Uri.fromFile(getContext().getCacheDir()).toString());
    localStringBuilder.append("/");
    localHashMap.put("cacheDirectory", localStringBuilder.toString());
    localHashMap.put("bundleDirectory", "asset:///");
    return localHashMap;
  }
  
  public void getContentUriAsync(String paramString, Promise paramPromise)
  {
    try
    {
      Object localObject = Uri.parse(paramString);
      Permission localPermission = Permission.WRITE;
      ensurePermission((Uri)localObject, localPermission);
      localPermission = Permission.READ;
      ensurePermission((Uri)localObject, localPermission);
      checkIfFileDirExists((Uri)localObject);
      boolean bool = "file".equals(((Uri)localObject).getScheme());
      if (bool)
      {
        paramString = uriToFile((Uri)localObject);
        localObject = new Bundle();
        ((BaseBundle)localObject).putString("uri", contentUriFromFile(paramString).toString());
        paramPromise.resolve(localObject);
        return;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("No readable files with the uri: ");
      ((StringBuilder)localObject).append(paramString);
      ((StringBuilder)localObject).append(". Please use other uri.");
      paramPromise.reject("E_DIRECTORY_NOT_READ", ((StringBuilder)localObject).toString());
      return;
    }
    catch (Exception paramString)
    {
      Log.e(WRITE, paramString.getMessage());
      paramPromise.reject(paramString);
    }
  }
  
  public void getFreeDiskStorageAsync(Promise paramPromise)
  {
    try
    {
      StatFs localStatFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
      long l1 = localStatFs.getAvailableBlocksLong();
      long l2 = localStatFs.getBlockSizeLong();
      double d1 = BigInteger.valueOf(l1).multiply(BigInteger.valueOf(l2)).doubleValue();
      double d2 = Math.pow(2.0D, 53.0D);
      paramPromise.resolve(Double.valueOf(Math.min(d1, d2 - 1.0D)));
      return;
    }
    catch (Exception localException)
    {
      Log.e(WRITE, localException.getMessage());
      paramPromise.reject("ERR_FILESYSTEM", "Unable to determine free disk storage capacity", localException);
    }
  }
  
  public void getInfoAsync(String paramString, Map paramMap, Promise paramPromise)
  {
    for (;;)
    {
      try
      {
        localUri = Uri.parse(paramString);
        paramString = Permission.READ;
        ensurePermission(localUri, paramString);
        bool = "file".equals(localUri.getScheme());
        if (bool)
        {
          paramString = uriToFile(localUri);
          localBundle = new Bundle();
          bool = paramString.exists();
          if (bool)
          {
            localBundle.putBoolean("exists", true);
            localBundle.putBoolean("isDirectory", paramString.isDirectory());
            localBundle.putString("uri", Uri.fromFile(paramString).toString());
            bool = paramMap.containsKey("md5");
            if (bool)
            {
              paramMap = paramMap.get("md5");
              paramMap = (Boolean)paramMap;
              bool = paramMap.booleanValue();
              if (bool) {
                localBundle.putString("md5", toString(paramString));
              }
            }
            long l = paramString.length();
            d = l;
            localBundle.putDouble("size", d);
            l = paramString.lastModified();
            d = l;
            localBundle.putDouble("modificationTime", d * 0.001D);
            paramPromise.resolve(localBundle);
            return;
          }
          localBundle.putBoolean("exists", false);
          localBundle.putBoolean("isDirectory", false);
          paramPromise.resolve(localBundle);
          return;
        }
        bool = "content".equals(localUri.getScheme());
        if (!bool)
        {
          bool = "asset".equals(localUri.getScheme());
          if (!bool)
          {
            paramString = new StringBuilder();
            paramString.append("Unsupported scheme for location '");
            paramString.append(localUri);
            paramString.append("'.");
            paramString = new IOException(paramString.toString());
            throw paramString;
          }
        }
        localBundle = new Bundle();
      }
      catch (Exception paramString)
      {
        Uri localUri;
        boolean bool;
        Bundle localBundle;
        double d;
        Log.e(WRITE, ((Exception)paramString).getMessage());
        paramPromise.reject(paramString);
        return;
      }
      try
      {
        bool = "content".equals(localUri.getScheme());
        if (bool) {
          paramString = getContext().getContentResolver().openInputStream(localUri);
        } else {
          paramString = openAssetInputStream(localUri);
        }
        if (paramString != null)
        {
          localBundle.putBoolean("exists", true);
          localBundle.putBoolean("isDirectory", false);
          localBundle.putString("uri", localUri.toString());
          int i = paramString.available();
          d = i;
          localBundle.putDouble("size", d);
          bool = paramMap.containsKey("md5");
          if (bool)
          {
            paramMap = paramMap.get("md5");
            paramMap = (Boolean)paramMap;
            bool = paramMap.booleanValue();
            if (bool) {
              localBundle.putString("md5", String.valueOf(Hex.encodeHex(DigestUtils.md5(paramString))));
            }
          }
          paramPromise.resolve(localBundle);
          return;
        }
        paramString = new FileNotFoundException();
        throw paramString;
      }
      catch (FileNotFoundException paramString) {}
    }
    localBundle.putBoolean("exists", false);
    localBundle.putBoolean("isDirectory", false);
    paramPromise.resolve(localBundle);
  }
  
  public String getName()
  {
    return "ExponentFileSystem";
  }
  
  public void getTotalDiskCapacityAsync(Promise paramPromise)
  {
    try
    {
      StatFs localStatFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
      long l1 = localStatFs.getBlockCountLong();
      long l2 = localStatFs.getBlockSizeLong();
      double d1 = BigInteger.valueOf(l1).multiply(BigInteger.valueOf(l2)).doubleValue();
      double d2 = Math.pow(2.0D, 53.0D);
      paramPromise.resolve(Double.valueOf(Math.min(d1, d2 - 1.0D)));
      return;
    }
    catch (Exception localException)
    {
      Log.e(WRITE, localException.getMessage());
      paramPromise.reject("ERR_FILESYSTEM", "Unable to access total disk capacity", localException);
    }
  }
  
  public void makeDirectoryAsync(String paramString, Map paramMap, Promise paramPromise)
  {
    try
    {
      paramString = Uri.parse(paramString);
      Object localObject = Permission.WRITE;
      ensurePermission(paramString, (Permission)localObject);
      boolean bool1 = "file".equals(paramString.getScheme());
      if (bool1)
      {
        localObject = uriToFile(paramString);
        boolean bool2 = ((File)localObject).isDirectory();
        bool1 = paramMap.containsKey("intermediates");
        if (bool1)
        {
          paramMap = paramMap.get("intermediates");
          paramMap = (Boolean)paramMap;
          bool1 = paramMap.booleanValue();
          if (bool1)
          {
            i = 1;
            break label99;
          }
        }
        int i = 0;
        label99:
        if (i != 0) {
          bool1 = ((File)localObject).mkdirs();
        } else {
          bool1 = ((File)localObject).mkdir();
        }
        if ((!bool1) && ((i == 0) || (!bool2)))
        {
          paramMap = new StringBuilder();
          paramMap.append("Directory '");
          paramMap.append(paramString);
          paramMap.append("' could not be created or already exists.");
          paramPromise.reject("E_DIRECTORY_NOT_CREATED", paramMap.toString());
          return;
        }
        paramPromise.resolve(null);
        return;
      }
      paramMap = new StringBuilder();
      paramMap.append("Unsupported scheme for location '");
      paramMap.append(paramString);
      paramMap.append("'.");
      paramString = new IOException(paramMap.toString());
      throw paramString;
    }
    catch (Exception paramString)
    {
      Log.e(WRITE, paramString.getMessage());
      paramPromise.reject(paramString);
    }
  }
  
  public void moveAsync(Map paramMap, Promise paramPromise)
  {
    try
    {
      boolean bool = paramMap.containsKey("from");
      if (!bool)
      {
        paramPromise.reject("E_MISSING_PARAMETER", "`FileSystem.moveAsync` needs a `from` path.");
        return;
      }
      Object localObject1 = paramMap.get("from");
      localObject1 = (String)localObject1;
      localObject1 = Uri.parse((String)localObject1);
      Object localObject2 = Uri.withAppendedPath((Uri)localObject1, "..");
      Permission localPermission = Permission.WRITE;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Location '");
      localStringBuilder.append(localObject1);
      localStringBuilder.append("' isn't movable.");
      ensurePermission((Uri)localObject2, localPermission, localStringBuilder.toString());
      bool = paramMap.containsKey("to");
      if (!bool)
      {
        paramPromise.reject("E_MISSING_PARAMETER", "`FileSystem.moveAsync` needs a `to` path.");
        return;
      }
      paramMap = paramMap.get("to");
      paramMap = (String)paramMap;
      paramMap = Uri.parse(paramMap);
      localObject2 = Permission.WRITE;
      ensurePermission(paramMap, (Permission)localObject2);
      bool = "file".equals(((Uri)localObject1).getScheme());
      if (bool)
      {
        bool = uriToFile((Uri)localObject1).renameTo(uriToFile(paramMap));
        if (bool)
        {
          paramPromise.resolve(null);
          return;
        }
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("File '");
        ((StringBuilder)localObject2).append(localObject1);
        ((StringBuilder)localObject2).append("' could not be moved to '");
        ((StringBuilder)localObject2).append(paramMap);
        ((StringBuilder)localObject2).append("'");
        paramPromise.reject("E_FILE_NOT_MOVED", ((StringBuilder)localObject2).toString());
        return;
      }
      paramMap = new StringBuilder();
      paramMap.append("Unsupported scheme for location '");
      paramMap.append(localObject1);
      paramMap.append("'.");
      paramMap = new IOException(paramMap.toString());
      throw paramMap;
    }
    catch (Exception paramMap)
    {
      Log.e(WRITE, paramMap.getMessage());
      paramPromise.reject(paramMap);
    }
  }
  
  public void onCreate(ModuleRegistry paramModuleRegistry)
  {
    mModuleRegistry = paramModuleRegistry;
  }
  
  public void readAsStringAsync(String paramString, Map paramMap, Promise paramPromise)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a16 = a15\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  public void readDirectoryAsync(String paramString, Map paramMap, Promise paramPromise)
  {
    try
    {
      paramString = Uri.parse(paramString);
      paramMap = Permission.READ;
      ensurePermission(paramString, paramMap);
      boolean bool = "file".equals(paramString.getScheme());
      if (bool)
      {
        paramMap = uriToFile(paramString).listFiles();
        if (paramMap != null)
        {
          paramString = new ArrayList();
          int j = paramMap.length;
          int i = 0;
          while (i < j)
          {
            Object localObject = paramMap[i];
            paramString.add(localObject.getName());
            i += 1;
          }
          paramPromise.resolve(paramString);
          return;
        }
        paramMap = new StringBuilder();
        paramMap.append("Directory '");
        paramMap.append(paramString);
        paramMap.append("' could not be read.");
        paramPromise.reject("E_DIRECTORY_NOT_READ", paramMap.toString());
        return;
      }
      paramMap = new StringBuilder();
      paramMap.append("Unsupported scheme for location '");
      paramMap.append(paramString);
      paramMap.append("'.");
      paramString = new IOException(paramMap.toString());
      throw paramString;
    }
    catch (Exception paramString)
    {
      Log.e(WRITE, paramString.getMessage());
      paramPromise.reject(paramString);
    }
  }
  
  public void writeAsStringAsync(String paramString1, String paramString2, Map paramMap, Promise paramPromise)
  {
    try
    {
      Uri localUri = Uri.parse(paramString1);
      paramString1 = Permission.WRITE;
      ensurePermission(localUri, paramString1);
      boolean bool = "file".equals(localUri.getScheme());
      if (bool)
      {
        String str = "utf8";
        bool = paramMap.containsKey("encoding");
        paramString1 = str;
        if (bool)
        {
          Object localObject = paramMap.get("encoding");
          paramString1 = str;
          if ((localObject instanceof String))
          {
            paramString1 = paramMap.get("encoding");
            paramString1 = (String)paramString1;
            paramString1 = paramString1.toLowerCase();
          }
        }
        paramMap = new FileOutputStream(uriToFile(localUri));
        bool = paramString1.equals("base64");
        if (bool)
        {
          paramMap.write(Base64.decode(paramString2, 0));
        }
        else
        {
          paramString1 = new OutputStreamWriter(paramMap);
          paramString1.write(paramString2);
          paramString1.close();
        }
        paramMap.close();
        paramPromise.resolve(null);
        return;
      }
      paramString1 = new StringBuilder();
      paramString1.append("Unsupported scheme for location '");
      paramString1.append(localUri);
      paramString1.append("'.");
      paramString1 = new IOException(paramString1.toString());
      throw paramString1;
    }
    catch (Exception paramString1)
    {
      Log.e(WRITE, paramString1.getMessage());
      paramPromise.reject(paramString1);
    }
  }
  
  private static class DownloadResumable
  {
    public final Call call;
    public final Uri fileUri;
    public final String proto;
    public final String uuid;
    
    public DownloadResumable(String paramString1, String paramString2, Uri paramUri, Call paramCall)
    {
      uuid = paramString1;
      proto = paramString2;
      fileUri = paramUri;
      call = paramCall;
    }
  }
  
  private class DownloadResumableTask
    extends AsyncTask<FileSystemModule.DownloadResumableTaskParams, Void, Void>
  {
    private DownloadResumableTask() {}
    
    protected Void doInBackground(FileSystemModule.DownloadResumableTaskParams... paramVarArgs)
    {
      Object localObject1 = 0call;
      Promise localPromise = 0promise;
      File localFile = 0file;
      boolean bool = 0isResume;
      Object localObject2 = 0options;
      try
      {
        localObject1 = ((Call)localObject1).execute();
        paramVarArgs = ((Response)localObject1).body();
        BufferedInputStream localBufferedInputStream = new BufferedInputStream(paramVarArgs.byteStream());
        if (bool) {
          paramVarArgs = new FileOutputStream(localFile, true);
        } else {
          paramVarArgs = new FileOutputStream(localFile, false);
        }
        byte[] arrayOfByte = new byte['?'];
        for (;;)
        {
          int i = localBufferedInputStream.read(arrayOfByte);
          if (i == -1) {
            break;
          }
          paramVarArgs.write(arrayOfByte, 0, i);
        }
        paramVarArgs = new Bundle();
        paramVarArgs.putString("uri", Uri.fromFile(localFile).toString());
        if (localObject2 != null)
        {
          bool = ((Map)localObject2).containsKey("md5");
          if (bool)
          {
            localObject2 = ((Map)localObject2).get("md5");
            localObject2 = (Boolean)localObject2;
            bool = ((Boolean)localObject2).booleanValue();
            if (bool)
            {
              localObject2 = FileSystemModule.this;
              paramVarArgs.putString("md5", ((FileSystemModule)localObject2).toString(localFile));
            }
          }
        }
        paramVarArgs.putInt("status", ((Response)localObject1).code());
        paramVarArgs.putBundle("headers", FileSystemModule.translateHeaders(((Response)localObject1).headers()));
        ((Response)localObject1).close();
        localPromise.resolve(paramVarArgs);
        return null;
      }
      catch (Exception paramVarArgs)
      {
        Log.e(FileSystemModule.WRITE, paramVarArgs.getMessage());
        localPromise.reject(paramVarArgs);
      }
      return null;
    }
  }
  
  private static class DownloadResumableTaskParams
  {
    Call call;
    File file;
    boolean isResume;
    Map<String, Object> options;
    Promise promise;
    
    DownloadResumableTaskParams(Map paramMap, Call paramCall, File paramFile, boolean paramBoolean, Promise paramPromise)
    {
      options = paramMap;
      call = paramCall;
      file = paramFile;
      isResume = paramBoolean;
      promise = paramPromise;
    }
  }
  
  static abstract interface ProgressListener
  {
    public abstract void update(long paramLong1, long paramLong2, boolean paramBoolean);
  }
  
  private static class ProgressResponseBody
    extends ResponseBody
  {
    private BufferedSource bufferedSource;
    private final FileSystemModule.ProgressListener progressListener;
    private final ResponseBody responseBody;
    
    ProgressResponseBody(ResponseBody paramResponseBody, FileSystemModule.ProgressListener paramProgressListener)
    {
      responseBody = paramResponseBody;
      progressListener = paramProgressListener;
    }
    
    private Source source(Source paramSource)
    {
      (Source)new ForwardingSource(paramSource)
      {
        long totalBytesRead = 0L;
        
        public long read(Buffer paramAnonymousBuffer, long paramAnonymousLong)
          throws IOException
        {
          long l1 = super.read(paramAnonymousBuffer, paramAnonymousLong);
          long l2 = totalBytesRead;
          boolean bool1 = l1 < -1L;
          if (bool1) {
            paramAnonymousLong = l1;
          } else {
            paramAnonymousLong = 0L;
          }
          totalBytesRead = (l2 + paramAnonymousLong);
          paramAnonymousBuffer = progressListener;
          paramAnonymousLong = totalBytesRead;
          l2 = responseBody.contentLength();
          boolean bool2;
          if (!bool1) {
            bool2 = true;
          } else {
            bool2 = false;
          }
          paramAnonymousBuffer.update(paramAnonymousLong, l2, bool2);
          return l1;
        }
      };
    }
    
    public long contentLength()
    {
      return responseBody.contentLength();
    }
    
    public MediaType contentType()
    {
      return responseBody.contentType();
    }
    
    public BufferedSource source()
    {
      if (bufferedSource == null) {
        bufferedSource = Okio.buffer(source((Source)responseBody.source()));
      }
      return bufferedSource;
    }
  }
}
