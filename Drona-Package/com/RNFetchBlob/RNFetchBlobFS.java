package com.RNFetchBlob;

import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.StatFs;
import android.util.Base64;
import com.RNFetchBlob.Utils.PathResolver;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

class RNFetchBlobFS
{
  private static HashMap<String, RNFetchBlobFS> fileStreams = new HashMap();
  private DeviceEventManagerModule.RCTDeviceEventEmitter emitter;
  private String encoding = "base64";
  private ReactApplicationContext mCtx;
  private OutputStream writeStreamInstance = null;
  
  RNFetchBlobFS(ReactApplicationContext paramReactApplicationContext)
  {
    mCtx = paramReactApplicationContext;
    emitter = ((DeviceEventManagerModule.RCTDeviceEventEmitter)paramReactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class));
  }
  
  static void closeStream(String paramString, Callback paramCallback)
  {
    Object localObject = fileStreams;
    try
    {
      localObject = ((HashMap)localObject).get(paramString);
      localObject = writeStreamInstance;
      HashMap localHashMap = fileStreams;
      localHashMap.remove(paramString);
      ((OutputStream)localObject).close();
      paramCallback.invoke(new Object[0]);
      return;
    }
    catch (Exception paramString)
    {
      paramCallback.invoke(new Object[] { paramString.getLocalizedMessage() });
    }
  }
  
  static void createFile(String paramString1, String paramString2, String paramString3, Promise paramPromise)
  {
    try
    {
      Object localObject = new File(paramString1);
      boolean bool1 = ((File)localObject).createNewFile();
      boolean bool2 = paramString3.equals("uri");
      if (bool2)
      {
        paramString3 = paramString2.replace("RNFetchBlob-file://", "");
        paramString3 = new File(paramString3);
        bool1 = paramString3.exists();
        if (!bool1)
        {
          paramString1 = new StringBuilder();
          paramString1.append("Source file : ");
          paramString1.append(paramString2);
          paramString1.append(" does not exist");
          paramPromise.reject("ENOENT", paramString1.toString());
          return;
        }
        paramString2 = new FileInputStream(paramString3);
        paramString3 = new FileOutputStream((File)localObject);
        localObject = new byte['?'];
        for (int i = paramString2.read((byte[])localObject); i > 0; i = paramString2.read((byte[])localObject)) {
          paramString3.write((byte[])localObject, 0, i);
        }
        paramString2.close();
        paramString3.close();
      }
      else
      {
        if (!bool1)
        {
          paramString2 = new StringBuilder();
          paramString2.append("File `");
          paramString2.append(paramString1);
          paramString2.append("` already exists");
          paramPromise.reject("EEXIST", paramString2.toString());
          return;
        }
        new FileOutputStream((File)localObject).write(stringToBytes(paramString2, paramString3));
      }
      paramPromise.resolve(paramString1);
      return;
    }
    catch (Exception paramString1)
    {
      paramPromise.reject("EUNSPECIFIED", paramString1.getLocalizedMessage());
    }
  }
  
  static void createFileASCII(String paramString, ReadableArray paramReadableArray, Promise paramPromise)
  {
    try
    {
      Object localObject = new File(paramString);
      boolean bool = ((File)localObject).createNewFile();
      if (!bool)
      {
        paramReadableArray = new StringBuilder();
        paramReadableArray.append("File at path `");
        paramReadableArray.append(paramString);
        paramReadableArray.append("` already exists");
        paramPromise.reject("EEXIST", paramReadableArray.toString());
        return;
      }
      localObject = new FileOutputStream((File)localObject);
      int i = paramReadableArray.size();
      byte[] arrayOfByte = new byte[i];
      i = 0;
      for (;;)
      {
        int j = paramReadableArray.size();
        if (i >= j) {
          break;
        }
        j = paramReadableArray.getInt(i);
        arrayOfByte[i] = ((byte)j);
        i += 1;
      }
      ((OutputStream)localObject).write(arrayOfByte);
      paramPromise.resolve(paramString);
      return;
    }
    catch (Exception paramString)
    {
      paramPromise.reject("EUNSPECIFIED", paramString.getLocalizedMessage());
    }
  }
  
  private static void deleteRecursive(File paramFile)
    throws IOException
  {
    if (paramFile.isDirectory())
    {
      localObject = paramFile.listFiles();
      if (localObject != null)
      {
        int j = localObject.length;
        int i = 0;
        while (i < j)
        {
          deleteRecursive(localObject[i]);
          i += 1;
        }
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Received null trying to list files of directory '");
      ((StringBuilder)localObject).append(paramFile);
      ((StringBuilder)localObject).append("'");
      throw new NullPointerException(((StringBuilder)localObject).toString());
    }
    if (paramFile.delete()) {
      return;
    }
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Failed to delete '");
    ((StringBuilder)localObject).append(paramFile);
    ((StringBuilder)localObject).append("'");
    throw new IOException(((StringBuilder)localObject).toString());
  }
  
  private void emitStreamEvent(String paramString1, String paramString2, WritableArray paramWritableArray)
  {
    WritableMap localWritableMap = Arguments.createMap();
    localWritableMap.putString("event", paramString2);
    localWritableMap.putArray("detail", paramWritableArray);
    emitter.emit(paramString1, localWritableMap);
  }
  
  private void emitStreamEvent(String paramString1, String paramString2, String paramString3)
  {
    WritableMap localWritableMap = Arguments.createMap();
    localWritableMap.putString("event", paramString2);
    localWritableMap.putString("detail", paramString3);
    emitter.emit(paramString1, localWritableMap);
  }
  
  private void emitStreamEvent(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    WritableMap localWritableMap = Arguments.createMap();
    localWritableMap.putString("event", paramString2);
    localWritableMap.putString("code", paramString3);
    localWritableMap.putString("detail", paramString4);
    emitter.emit(paramString1, localWritableMap);
  }
  
  static void execute(String paramString, Promise paramPromise)
  {
    try
    {
      paramString = normalizePath(paramString);
      Object localObject = new File(paramString);
      boolean bool = ((File)localObject).exists();
      if (!bool)
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("No such file '");
        ((StringBuilder)localObject).append(paramString);
        ((StringBuilder)localObject).append("'");
        paramPromise.reject("ENOENT", ((StringBuilder)localObject).toString());
        return;
      }
      bool = ((File)localObject).isDirectory();
      if (!bool)
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Not a directory '");
        ((StringBuilder)localObject).append(paramString);
        ((StringBuilder)localObject).append("'");
        paramPromise.reject("ENOTDIR", ((StringBuilder)localObject).toString());
        return;
      }
      paramString = new File(paramString).list();
      localObject = Arguments.createArray();
      int j = paramString.length;
      int i = 0;
      while (i < j)
      {
        String str = paramString[i];
        ((WritableArray)localObject).pushString(str);
        i += 1;
      }
      paramPromise.resolve(localObject);
      return;
    }
    catch (Exception paramString)
    {
      paramString.printStackTrace();
      paramPromise.reject("EUNSPECIFIED", paramString.getLocalizedMessage());
    }
  }
  
  static void execute(String paramString1, String paramString2, Callback paramCallback)
  {
    Object localObject1 = normalizePath(paramString1);
    String str2 = "";
    Object localObject2 = null;
    paramString1 = null;
    try
    {
      boolean bool = isPathExists((String)localObject1);
      if (!bool)
      {
        paramString2 = new StringBuilder();
        paramString2.append("Source file at path`");
        paramString2.append((String)localObject1);
        paramString2.append("` does not exist");
        paramString2 = paramString2.toString();
        paramCallback.invoke(new Object[] { paramString2 });
        return;
      }
      bool = new File(paramString2).exists();
      if (!bool)
      {
        bool = new File(paramString2).createNewFile();
        if (!bool)
        {
          localObject1 = new StringBuilder();
          ((StringBuilder)localObject1).append("Destination file at '");
          ((StringBuilder)localObject1).append(paramString2);
          ((StringBuilder)localObject1).append("' already exists");
          paramString2 = ((StringBuilder)localObject1).toString();
          paramCallback.invoke(new Object[] { paramString2 });
          return;
        }
      }
      localObject1 = inputStreamFromPath((String)localObject1);
      try
      {
        paramString2 = new FileOutputStream(paramString2);
        paramString1 = new byte['?'];
        try
        {
          for (;;)
          {
            int i = ((InputStream)localObject1).read(paramString1);
            if (i <= 0) {
              break;
            }
            paramString2.write(paramString1, 0, i);
          }
          if (localObject1 != null) {
            try
            {
              ((InputStream)localObject1).close();
            }
            catch (Exception paramString1)
            {
              break label239;
            }
          }
          paramString2.close();
          paramString1 = str2;
        }
        catch (Throwable paramString1)
        {
          break label282;
        }
        catch (Exception paramString1)
        {
          label239:
          break label293;
        }
        paramString2 = new StringBuilder();
        paramString2.append("");
        paramString2.append(paramString1.getLocalizedMessage());
        paramString1 = paramString2.toString();
      }
      catch (Throwable paramString1)
      {
        paramString2 = null;
        paramCallback = paramString1;
        paramString1 = (String)localObject1;
        break label448;
      }
      catch (Exception paramString1)
      {
        label282:
        paramString2 = null;
      }
      label293:
      localObject2 = paramString1;
      paramString1 = (String)localObject1;
      localObject1 = localObject2;
    }
    catch (Throwable paramCallback)
    {
      paramString2 = null;
      paramString1 = (String)localObject2;
      break label448;
    }
    catch (Exception localException)
    {
      paramString2 = null;
    }
    try
    {
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append("");
      ((StringBuilder)localObject2).append(localException.getLocalizedMessage());
      String str1 = ((StringBuilder)localObject2).toString();
      if (paramString1 != null) {
        try
        {
          paramString1.close();
        }
        catch (Exception paramString1)
        {
          break label380;
        }
      }
      if (paramString2 != null)
      {
        paramString2.close();
        break label412;
        label380:
        paramString2 = new StringBuilder();
        paramString2.append(str1);
        paramString2.append(paramString1.getLocalizedMessage());
        paramString1 = paramString2.toString();
        break label415;
      }
      label412:
      paramString1 = str1;
      label415:
      if (paramString1 != "")
      {
        paramCallback.invoke(new Object[] { paramString1 });
        return;
      }
      paramCallback.invoke(new Object[0]);
      return;
    }
    catch (Throwable paramCallback) {}
    label448:
    if (paramString1 != null) {
      try
      {
        paramString1.close();
      }
      catch (Exception paramString1)
      {
        break label474;
      }
    }
    if (paramString2 != null)
    {
      paramString2.close();
      break label503;
      label474:
      paramString2 = new StringBuilder();
      paramString2.append("");
      paramString2.append(paramString1.getLocalizedMessage());
      paramString2.toString();
    }
    label503:
    throw paramCallback;
  }
  
  static void exists(String paramString, Callback paramCallback)
  {
    if (isAsset(paramString)) {}
    try
    {
      paramString = paramString.replace("bundle-assets://", "");
      ReactApplicationContext localReactApplicationContext = RNFetchBlob.RCTContext;
      localReactApplicationContext.getAssets().openFd(paramString);
      paramCallback.invoke(new Object[] { Boolean.valueOf(true), Boolean.valueOf(false) });
      return;
    }
    catch (IOException paramString)
    {
      for (;;) {}
    }
    paramCallback.invoke(new Object[] { Boolean.valueOf(false), Boolean.valueOf(false) });
    return;
    paramString = normalizePath(paramString);
    if (paramString != null)
    {
      paramCallback.invoke(new Object[] { Boolean.valueOf(new File(paramString).exists()), Boolean.valueOf(new File(paramString).isDirectory()) });
      return;
    }
    paramCallback.invoke(new Object[] { Boolean.valueOf(false), Boolean.valueOf(false) });
  }
  
  static void fetch(String paramString1, String paramString2, Callback paramCallback)
  {
    File localFile = new File(paramString1);
    if (!localFile.exists())
    {
      paramString2 = new StringBuilder();
      paramString2.append("Source file at path `");
      paramString2.append(paramString1);
      paramString2.append("` does not exist");
      paramCallback.invoke(new Object[] { paramString2.toString() });
      return;
    }
    try
    {
      paramString1 = new FileInputStream(paramString1);
      paramString2 = new FileOutputStream(paramString2);
      byte[] arrayOfByte = new byte['?'];
      for (;;)
      {
        int i = paramString1.read(arrayOfByte);
        if (i == -1) {
          break;
        }
        paramString2.write(arrayOfByte, 0, i);
      }
      paramString1.close();
      paramString2.flush();
      localFile.delete();
      paramCallback.invoke(new Object[0]);
      return;
    }
    catch (Exception paramString1)
    {
      paramCallback.invoke(new Object[] { paramString1.toString() });
      return;
      paramCallback.invoke(new Object[] { "Source file not found." });
      return;
    }
    catch (FileNotFoundException paramString1)
    {
      for (;;) {}
    }
  }
  
  public static void getSDCardApplicationDir(ReactApplicationContext paramReactApplicationContext, Promise paramPromise)
  {
    if (Environment.getExternalStorageState().equals("mounted")) {
      try
      {
        paramPromise.resolve(paramReactApplicationContext.getExternalFilesDir(null).getParentFile().getAbsolutePath());
        return;
      }
      catch (Exception paramReactApplicationContext)
      {
        paramPromise.reject("RNFetchBlob.getSDCardApplicationDir", paramReactApplicationContext.getLocalizedMessage());
        return;
      }
    }
    paramPromise.reject("RNFetchBlob.getSDCardApplicationDir", "External storage not mounted");
  }
  
  public static void getSDCardDir(Promise paramPromise)
  {
    if (Environment.getExternalStorageState().equals("mounted"))
    {
      paramPromise.resolve(Environment.getExternalStorageDirectory().getAbsolutePath());
      return;
    }
    paramPromise.reject("RNFetchBlob.getSDCardDir", "External storage not mounted");
  }
  
  static Map getSystemfolders(ReactApplicationContext paramReactApplicationContext)
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("DocumentDir", paramReactApplicationContext.getFilesDir().getAbsolutePath());
    localHashMap.put("CacheDir", paramReactApplicationContext.getCacheDir().getAbsolutePath());
    localHashMap.put("DCIMDir", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
    localHashMap.put("PictureDir", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
    localHashMap.put("MusicDir", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath());
    localHashMap.put("DownloadDir", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
    localHashMap.put("MovieDir", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath());
    localHashMap.put("RingtoneDir", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES).getAbsolutePath());
    if (Environment.getExternalStorageState().equals("mounted"))
    {
      localHashMap.put("SDCardDir", Environment.getExternalStorageDirectory().getAbsolutePath());
      File localFile = paramReactApplicationContext.getExternalFilesDir(null);
      if (localFile != null) {
        localHashMap.put("SDCardApplicationDir", localFile.getParentFile().getAbsolutePath());
      } else {
        localHashMap.put("SDCardApplicationDir", "");
      }
    }
    localHashMap.put("MainBundleDir", getApplicationInfodataDir);
    return localHashMap;
  }
  
  static String getTmpPath(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(RNFetchBlob.RCTContext.getFilesDir());
    localStringBuilder.append("/RNFetchBlobTmp_");
    localStringBuilder.append(paramString);
    return localStringBuilder.toString();
  }
  
  static void hash(String paramString1, String paramString2, Promise paramPromise)
  {
    try
    {
      Object localObject = new HashMap();
      ((Map)localObject).put("md5", "MD5");
      ((Map)localObject).put("sha1", "SHA-1");
      ((Map)localObject).put("sha224", "SHA-224");
      ((Map)localObject).put("sha256", "SHA-256");
      ((Map)localObject).put("sha384", "SHA-384");
      ((Map)localObject).put("sha512", "SHA-512");
      boolean bool = ((Map)localObject).containsKey(paramString2);
      if (!bool)
      {
        paramString1 = new StringBuilder();
        paramString1.append("Invalid algorithm '");
        paramString1.append(paramString2);
        paramString1.append("', must be one of md5, sha1, sha224, sha256, sha384, sha512");
        paramPromise.reject("EINVAL", paramString1.toString());
        return;
      }
      File localFile = new File(paramString1);
      bool = localFile.isDirectory();
      if (bool)
      {
        paramString2 = new StringBuilder();
        paramString2.append("Expecting a file but '");
        paramString2.append(paramString1);
        paramString2.append("' is a directory");
        paramPromise.reject("EISDIR", paramString2.toString());
        return;
      }
      bool = localFile.exists();
      if (!bool)
      {
        paramString2 = new StringBuilder();
        paramString2.append("No such file '");
        paramString2.append(paramString1);
        paramString2.append("'");
        paramPromise.reject("ENOENT", paramString2.toString());
        return;
      }
      paramString2 = ((Map)localObject).get(paramString2);
      paramString2 = (String)paramString2;
      paramString2 = MessageDigest.getInstance(paramString2);
      paramString1 = new FileInputStream(paramString1);
      localObject = new byte[1048576];
      long l = localFile.length();
      if (l != 0L) {
        for (;;)
        {
          i = paramString1.read((byte[])localObject);
          if (i == -1) {
            break;
          }
          paramString2.update((byte[])localObject, 0, i);
        }
      }
      paramString1 = new StringBuilder();
      paramString2 = paramString2.digest();
      int j = paramString2.length;
      int i = 0;
      while (i < j)
      {
        byte b = paramString2[i];
        paramString1.append(String.format("%02x", new Object[] { Byte.valueOf(b) }));
        i += 1;
      }
      paramPromise.resolve(paramString1.toString());
      return;
    }
    catch (Exception paramString1)
    {
      paramString1.printStackTrace();
      paramPromise.reject("EUNSPECIFIED", paramString1.getLocalizedMessage());
    }
  }
  
  private static InputStream inputStreamFromPath(String paramString)
    throws IOException
  {
    if (paramString.startsWith("bundle-assets://")) {
      return RNFetchBlob.RCTContext.getAssets().open(paramString.replace("bundle-assets://", ""));
    }
    return new FileInputStream(new File(paramString));
  }
  
  static boolean isAsset(String paramString)
  {
    return (paramString != null) && (paramString.startsWith("bundle-assets://"));
  }
  
  private static boolean isPathExists(String paramString)
  {
    ReactApplicationContext localReactApplicationContext;
    if (paramString.startsWith("bundle-assets://")) {
      localReactApplicationContext = RNFetchBlob.RCTContext;
    }
    try
    {
      localReactApplicationContext.getAssets().open(paramString.replace("bundle-assets://", ""));
      return true;
    }
    catch (IOException paramString)
    {
      for (;;) {}
    }
    return false;
    return new File(paramString).exists();
  }
  
  static void lstat(String paramString, Callback paramCallback)
  {
    paramString = normalizePath(paramString);
    new AsyncTask()
    {
      protected Integer doInBackground(String... paramAnonymousVarArgs)
      {
        Object localObject1 = Arguments.createArray();
        if (paramAnonymousVarArgs[0] == null)
        {
          val$callback.invoke(new Object[] { "the path specified for lstat is either `null` or `undefined`." });
          return Integer.valueOf(0);
        }
        Object localObject2 = new File(paramAnonymousVarArgs[0]);
        if (!((File)localObject2).exists())
        {
          localObject1 = val$callback;
          localObject2 = new StringBuilder();
          ((StringBuilder)localObject2).append("failed to lstat path `");
          ((StringBuilder)localObject2).append(paramAnonymousVarArgs[0]);
          ((StringBuilder)localObject2).append("` because it does not exist or it is not a folder");
          ((Callback)localObject1).invoke(new Object[] { ((StringBuilder)localObject2).toString() });
          return Integer.valueOf(0);
        }
        if (((File)localObject2).isDirectory())
        {
          paramAnonymousVarArgs = ((File)localObject2).list();
          int j = paramAnonymousVarArgs.length;
          int i = 0;
          while (i < j)
          {
            String str = paramAnonymousVarArgs[i];
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append(((File)localObject2).getPath());
            localStringBuilder.append("/");
            localStringBuilder.append(str);
            ((WritableArray)localObject1).pushMap(RNFetchBlobFS.statFile(localStringBuilder.toString()));
            i += 1;
          }
        }
        ((WritableArray)localObject1).pushMap(RNFetchBlobFS.statFile(((File)localObject2).getAbsolutePath()));
        val$callback.invoke(new Object[] { null, localObject1 });
        return Integer.valueOf(0);
      }
    }.execute(new String[] { paramString });
  }
  
  static void mkdir(String paramString, Promise paramPromise)
  {
    Object localObject = new File(paramString);
    if (((File)localObject).exists())
    {
      StringBuilder localStringBuilder = new StringBuilder();
      if (((File)localObject).isDirectory()) {
        localObject = "Folder";
      } else {
        localObject = "File";
      }
      localStringBuilder.append((String)localObject);
      localStringBuilder.append(" '");
      localStringBuilder.append(paramString);
      localStringBuilder.append("' already exists");
      paramPromise.reject("EEXIST", localStringBuilder.toString());
      return;
    }
    try
    {
      boolean bool = ((File)localObject).mkdirs();
      if (!bool)
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("mkdir failed to create some or all directories in '");
        ((StringBuilder)localObject).append(paramString);
        ((StringBuilder)localObject).append("'");
        paramPromise.reject("EUNSPECIFIED", ((StringBuilder)localObject).toString());
        return;
      }
      paramPromise.resolve(Boolean.valueOf(true));
      return;
    }
    catch (Exception paramString)
    {
      paramPromise.reject("EUNSPECIFIED", paramString.getLocalizedMessage());
    }
  }
  
  static String normalizePath(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    if (!paramString.matches("\\w+\\:.*")) {
      return paramString;
    }
    if (paramString.startsWith("file://")) {
      return paramString.replace("file://", "");
    }
    Uri localUri = Uri.parse(paramString);
    if (paramString.startsWith("bundle-assets://")) {
      return paramString;
    }
    return PathResolver.getRealPathFromURI(RNFetchBlob.RCTContext, localUri);
  }
  
  static void readFile(String paramString1, String paramString2, Promise paramPromise)
  {
    Object localObject2 = normalizePath(paramString1);
    Object localObject1 = paramString1;
    if (localObject2 != null) {
      localObject1 = localObject2;
    }
    int k = 0;
    boolean bool;
    long l;
    int j;
    if (localObject2 != null) {
      try
      {
        bool = ((String)localObject2).startsWith("bundle-assets://");
        if (bool)
        {
          localObject2 = ((String)localObject1).replace("bundle-assets://", "");
          paramString1 = RNFetchBlob.RCTContext;
          l = paramString1.getAssets().openFd((String)localObject2).getLength();
          i = (int)l;
          paramString1 = new byte[i];
          ReactApplicationContext localReactApplicationContext = RNFetchBlob.RCTContext;
          localObject2 = localReactApplicationContext.getAssets().open((String)localObject2);
          j = ((InputStream)localObject2).read(paramString1, 0, i);
          ((InputStream)localObject2).close();
        }
      }
      catch (Exception paramString1)
      {
        break label501;
      }
      catch (FileNotFoundException paramString1)
      {
        break label514;
      }
    }
    if (localObject2 == null)
    {
      paramString1 = RNFetchBlob.RCTContext;
      localObject2 = paramString1.getContentResolver().openInputStream(Uri.parse((String)localObject1));
      j = ((InputStream)localObject2).available();
      i = j;
      paramString1 = new byte[j];
      j = ((InputStream)localObject2).read(paramString1);
      ((InputStream)localObject2).close();
    }
    else
    {
      localObject2 = new File((String)localObject1);
      l = ((File)localObject2).length();
      i = (int)l;
      paramString1 = new byte[i];
      localObject2 = new FileInputStream((File)localObject2);
      j = ((FileInputStream)localObject2).read(paramString1);
      ((FileInputStream)localObject2).close();
    }
    if (j < i)
    {
      paramString1 = new StringBuilder();
      paramString1.append("Read only ");
      paramString1.append(j);
      paramString1.append(" bytes of ");
      paramString1.append(i);
      paramPromise.reject("EUNSPECIFIED", paramString1.toString());
      return;
    }
    paramString2 = paramString2.toLowerCase();
    int i = paramString2.hashCode();
    if (i != -1396204209)
    {
      if (i != 3600241)
      {
        if (i == 93106001)
        {
          bool = paramString2.equals("ascii");
          if (bool)
          {
            i = 1;
            break label374;
          }
        }
      }
      else
      {
        bool = paramString2.equals("utf8");
        if (bool)
        {
          i = 2;
          break label374;
        }
      }
    }
    else
    {
      bool = paramString2.equals("base64");
      if (bool)
      {
        i = 0;
        break label374;
      }
    }
    i = -1;
    switch (i)
    {
    default: 
      break;
    case 2: 
      paramPromise.resolve(new String(paramString1));
      return;
    case 1: 
      paramString2 = Arguments.createArray();
      j = paramString1.length;
      i = k;
      while (i < j)
      {
        k = paramString1[i];
        paramString2.pushInt(k);
        i += 1;
      }
      paramPromise.resolve(paramString2);
      return;
    case 0: 
      label374:
      paramPromise.resolve(Base64.encodeToString(paramString1, 2));
      return;
    }
    paramPromise.resolve(new String(paramString1));
    return;
    label501:
    paramPromise.reject("EUNSPECIFIED", paramString1.getLocalizedMessage());
    return;
    label514:
    paramString1 = paramString1.getLocalizedMessage();
    if (paramString1.contains("EISDIR"))
    {
      paramString2 = new StringBuilder();
      paramString2.append("Expecting a file but '");
      paramString2.append((String)localObject1);
      paramString2.append("' is a directory; ");
      paramString2.append(paramString1);
      paramPromise.reject("EISDIR", paramString2.toString());
      return;
    }
    paramString2 = new StringBuilder();
    paramString2.append("No such file '");
    paramString2.append((String)localObject1);
    paramString2.append("'; ");
    paramString2.append(paramString1);
    paramPromise.reject("ENOENT", paramString2.toString());
  }
  
  static void removeSession(ReadableArray paramReadableArray, Callback paramCallback)
  {
    new AsyncTask()
    {
      protected Integer doInBackground(ReadableArray... paramAnonymousVarArgs)
      {
        try
        {
          Object localObject1 = new ArrayList();
          int i = 0;
          Object localObject2;
          Object localObject3;
          for (;;)
          {
            localObject2 = paramAnonymousVarArgs[0];
            int j = ((ReadableArray)localObject2).size();
            if (i >= j) {
              break;
            }
            localObject2 = paramAnonymousVarArgs[0];
            localObject2 = ((ReadableArray)localObject2).getString(i);
            localObject3 = new File((String)localObject2);
            bool = ((File)localObject3).exists();
            if (bool)
            {
              bool = ((File)localObject3).delete();
              if (!bool) {
                ((ArrayList)localObject1).add(localObject2);
              }
            }
            i += 1;
          }
          boolean bool = ((ArrayList)localObject1).isEmpty();
          if (bool)
          {
            localObject1 = val$callback;
            ((Callback)localObject1).invoke(new Object[] { null, Boolean.valueOf(true) });
          }
          else
          {
            localObject2 = new StringBuilder();
            ((StringBuilder)localObject2).append("Failed to delete: ");
            localObject1 = ((ArrayList)localObject1).iterator();
            for (;;)
            {
              bool = ((Iterator)localObject1).hasNext();
              if (!bool) {
                break;
              }
              localObject3 = ((Iterator)localObject1).next();
              localObject3 = (String)localObject3;
              ((StringBuilder)localObject2).append((String)localObject3);
              ((StringBuilder)localObject2).append(", ");
            }
            localObject1 = val$callback;
            localObject2 = ((StringBuilder)localObject2).toString();
            ((Callback)localObject1).invoke(new Object[] { localObject2 });
          }
        }
        catch (Exception localException)
        {
          val$callback.invoke(new Object[] { localException.getLocalizedMessage() });
        }
        return Integer.valueOf(paramAnonymousVarArgs[0].size());
      }
    }.execute(new ReadableArray[] { paramReadableArray });
  }
  
  static void slice(String paramString1, String paramString2, int paramInt1, int paramInt2, String paramString3, Promise paramPromise)
  {
    try
    {
      paramString1 = normalizePath(paramString1);
      paramString3 = new File(paramString1);
      boolean bool = paramString3.isDirectory();
      if (bool)
      {
        paramString2 = new StringBuilder();
        paramString2.append("Expecting a file but '");
        paramString2.append(paramString1);
        paramString2.append("' is a directory");
        paramPromise.reject("EISDIR", paramString2.toString());
        return;
      }
      bool = paramString3.exists();
      if (!bool)
      {
        paramString2 = new StringBuilder();
        paramString2.append("No such file '");
        paramString2.append(paramString1);
        paramString2.append("'");
        paramPromise.reject("ENOENT", paramString2.toString());
        return;
      }
      long l = paramString3.length();
      int i = (int)l;
      paramInt2 = Math.min(i, paramInt2);
      paramInt2 -= paramInt1;
      paramString1 = new FileInputStream(new File(paramString1));
      paramString3 = new FileOutputStream(new File(paramString2));
      l = paramInt1;
      l = paramString1.skip(l);
      int j = (int)l;
      if (j != paramInt1)
      {
        paramString1 = new StringBuilder();
        paramString1.append("Skipped ");
        paramString1.append(j);
        paramString1.append(" instead of the specified ");
        paramString1.append(paramInt1);
        paramString1.append(" bytes, size is ");
        paramString1.append(i);
        paramPromise.reject("EUNSPECIFIED", paramString1.toString());
        return;
      }
      byte[] arrayOfByte = new byte['?'];
      paramInt1 = 0;
      while (paramInt1 < paramInt2)
      {
        i = paramString1.read(arrayOfByte, 0, 10240);
        if (i <= 0) {
          break;
        }
        paramString3.write(arrayOfByte, 0, Math.min(paramInt2 - paramInt1, i));
        paramInt1 += i;
      }
      paramString1.close();
      paramString3.flush();
      paramString3.close();
      paramPromise.resolve(paramString2);
      return;
    }
    catch (Exception paramString1)
    {
      paramString1.printStackTrace();
      paramPromise.reject("EUNSPECIFIED", paramString1.getLocalizedMessage());
    }
  }
  
  static void stat(String paramString, Callback paramCallback)
  {
    try
    {
      paramString = normalizePath(paramString);
      Object localObject = statFile(paramString);
      if (localObject == null)
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("failed to stat path `");
        ((StringBuilder)localObject).append(paramString);
        ((StringBuilder)localObject).append("` because it does not exist or it is not a folder");
        paramString = ((StringBuilder)localObject).toString();
        paramCallback.invoke(new Object[] { paramString, null });
        return;
      }
      paramCallback.invoke(new Object[] { null, localObject });
      return;
    }
    catch (Exception paramString)
    {
      paramCallback.invoke(new Object[] { paramString.getLocalizedMessage() });
    }
  }
  
  static WritableMap statFile(String paramString)
  {
    try
    {
      paramString = normalizePath(paramString);
      WritableMap localWritableMap = Arguments.createMap();
      boolean bool = isAsset(paramString);
      if (bool)
      {
        localObject1 = paramString.replace("bundle-assets://", "");
        Object localObject2 = RNFetchBlob.RCTContext;
        localObject2 = ((ContextWrapper)localObject2).getAssets().openFd((String)localObject1);
        localWritableMap.putString("filename", (String)localObject1);
        localWritableMap.putString("path", paramString);
        localWritableMap.putString("type", "asset");
        localWritableMap.putString("size", String.valueOf(((AssetFileDescriptor)localObject2).getLength()));
        localWritableMap.putInt("lastModified", 0);
        return localWritableMap;
      }
      Object localObject1 = new File(paramString);
      bool = ((File)localObject1).exists();
      if (!bool) {
        return null;
      }
      localWritableMap.putString("filename", ((File)localObject1).getName());
      localWritableMap.putString("path", ((File)localObject1).getPath());
      bool = ((File)localObject1).isDirectory();
      if (bool) {
        paramString = "directory";
      } else {
        paramString = "file";
      }
      localWritableMap.putString("type", paramString);
      localWritableMap.putString("size", String.valueOf(((File)localObject1).length()));
      localWritableMap.putString("lastModified", String.valueOf(((File)localObject1).lastModified()));
      return localWritableMap;
    }
    catch (Exception paramString) {}
    return null;
  }
  
  private static byte[] stringToBytes(String paramString1, String paramString2)
  {
    if (paramString2.equalsIgnoreCase("ascii")) {
      return paramString1.getBytes(Charset.forName("US-ASCII"));
    }
    if (paramString2.toLowerCase().contains("base64")) {
      return Base64.decode(paramString1, 2);
    }
    if (paramString2.equalsIgnoreCase("utf8")) {
      return paramString1.getBytes(Charset.forName("UTF-8"));
    }
    return paramString1.getBytes(Charset.forName("US-ASCII"));
  }
  
  static void unlink(String paramString, Callback paramCallback)
  {
    try
    {
      paramString = normalizePath(paramString);
      deleteRecursive(new File(paramString));
      paramCallback.invoke(new Object[] { null, Boolean.valueOf(true) });
      return;
    }
    catch (Exception paramString)
    {
      paramCallback.invoke(new Object[] { paramString.getLocalizedMessage(), Boolean.valueOf(false) });
    }
  }
  
  static void update(Callback paramCallback)
  {
    StatFs localStatFs = new StatFs(Environment.getDataDirectory().getPath());
    WritableMap localWritableMap = Arguments.createMap();
    if (Build.VERSION.SDK_INT >= 18)
    {
      localWritableMap.putString("internal_free", String.valueOf(localStatFs.getFreeBytes()));
      localWritableMap.putString("internal_total", String.valueOf(localStatFs.getTotalBytes()));
      localStatFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
      localWritableMap.putString("external_free", String.valueOf(localStatFs.getFreeBytes()));
      localWritableMap.putString("external_total", String.valueOf(localStatFs.getTotalBytes()));
    }
    paramCallback.invoke(new Object[] { null, localWritableMap });
  }
  
  static void writeArrayChunk(String paramString, ReadableArray paramReadableArray, Callback paramCallback)
  {
    Object localObject = fileStreams;
    try
    {
      paramString = ((HashMap)localObject).get(paramString);
      paramString = writeStreamInstance;
      int i = paramReadableArray.size();
      localObject = new byte[i];
      i = 0;
      for (;;)
      {
        int j = paramReadableArray.size();
        if (i >= j) {
          break;
        }
        j = paramReadableArray.getInt(i);
        localObject[i] = ((byte)j);
        i += 1;
      }
      paramString.write((byte[])localObject);
      paramCallback.invoke(new Object[0]);
      return;
    }
    catch (Exception paramString)
    {
      paramCallback.invoke(new Object[] { paramString.getLocalizedMessage() });
    }
  }
  
  static void writeChunk(String paramString1, String paramString2, Callback paramCallback)
  {
    RNFetchBlobFS localRNFetchBlobFS = (RNFetchBlobFS)fileStreams.get(paramString1);
    paramString1 = writeStreamInstance;
    paramString2 = stringToBytes(paramString2, encoding);
    try
    {
      paramString1.write(paramString2);
      paramCallback.invoke(new Object[0]);
      return;
    }
    catch (Exception paramString1)
    {
      paramCallback.invoke(new Object[] { paramString1.getLocalizedMessage() });
    }
  }
  
  /* Error */
  static void writeFile(String paramString, ReadableArray paramReadableArray, boolean paramBoolean, Promise paramPromise)
  {
    // Byte code:
    //   0: new 81	java/io/File
    //   3: dup
    //   4: aload_0
    //   5: invokespecial 84	java/io/File:<init>	(Ljava/lang/String;)V
    //   8: astore 7
    //   10: aload 7
    //   12: invokevirtual 346	java/io/File:getParentFile	()Ljava/io/File;
    //   15: astore 8
    //   17: aload 7
    //   19: invokevirtual 107	java/io/File:exists	()Z
    //   22: istore 6
    //   24: iload 6
    //   26: ifne +129 -> 155
    //   29: aload 8
    //   31: ifnull +69 -> 100
    //   34: aload 8
    //   36: invokevirtual 107	java/io/File:exists	()Z
    //   39: istore 6
    //   41: iload 6
    //   43: ifne +57 -> 100
    //   46: aload 8
    //   48: invokevirtual 525	java/io/File:mkdirs	()Z
    //   51: istore 6
    //   53: iload 6
    //   55: ifne +45 -> 100
    //   58: new 109	java/lang/StringBuilder
    //   61: dup
    //   62: invokespecial 110	java/lang/StringBuilder:<init>	()V
    //   65: astore_1
    //   66: aload_1
    //   67: ldc_w 728
    //   70: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   73: pop
    //   74: aload_1
    //   75: aload_0
    //   76: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   79: pop
    //   80: aload_1
    //   81: ldc -58
    //   83: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   86: pop
    //   87: aload_3
    //   88: ldc -7
    //   90: aload_1
    //   91: invokevirtual 123	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   94: invokeinterface 129 3 0
    //   99: return
    //   100: aload 7
    //   102: invokevirtual 88	java/io/File:createNewFile	()Z
    //   105: istore 6
    //   107: iload 6
    //   109: ifne +46 -> 155
    //   112: new 109	java/lang/StringBuilder
    //   115: dup
    //   116: invokespecial 110	java/lang/StringBuilder:<init>	()V
    //   119: astore_1
    //   120: aload_1
    //   121: ldc_w 730
    //   124: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   127: pop
    //   128: aload_1
    //   129: aload_0
    //   130: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   133: pop
    //   134: aload_1
    //   135: ldc_w 732
    //   138: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   141: pop
    //   142: aload_3
    //   143: ldc 120
    //   145: aload_1
    //   146: invokevirtual 123	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   149: invokeinterface 129 3 0
    //   154: return
    //   155: new 136	java/io/FileOutputStream
    //   158: dup
    //   159: aload 7
    //   161: iload_2
    //   162: invokespecial 735	java/io/FileOutputStream:<init>	(Ljava/io/File;Z)V
    //   165: astore 7
    //   167: aload_1
    //   168: invokeinterface 175 1 0
    //   173: newarray byte
    //   175: astore 8
    //   177: iconst_0
    //   178: istore 4
    //   180: aload_1
    //   181: invokeinterface 175 1 0
    //   186: istore 5
    //   188: iload 4
    //   190: iload 5
    //   192: if_icmpge +26 -> 218
    //   195: aload 8
    //   197: iload 4
    //   199: aload_1
    //   200: iload 4
    //   202: invokeinterface 179 2 0
    //   207: i2b
    //   208: bastore
    //   209: iload 4
    //   211: iconst_1
    //   212: iadd
    //   213: istore 4
    //   215: goto -35 -> 180
    //   218: aload 7
    //   220: aload 8
    //   222: invokevirtual 736	java/io/FileOutputStream:write	([B)V
    //   225: aload 7
    //   227: invokevirtual 634	java/io/FileOutputStream:close	()V
    //   230: aload_3
    //   231: aload_1
    //   232: invokeinterface 175 1 0
    //   237: invokestatic 741	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   240: invokeinterface 163 2 0
    //   245: return
    //   246: astore_1
    //   247: aload 7
    //   249: invokevirtual 634	java/io/FileOutputStream:close	()V
    //   252: aload_1
    //   253: athrow
    //   254: astore_0
    //   255: aload_3
    //   256: ldc -91
    //   258: aload_0
    //   259: checkcast 55	java/lang/Exception
    //   262: invokevirtual 77	java/lang/Exception:getLocalizedMessage	()Ljava/lang/String;
    //   265: invokeinterface 129 3 0
    //   270: return
    //   271: new 109	java/lang/StringBuilder
    //   274: dup
    //   275: invokespecial 110	java/lang/StringBuilder:<init>	()V
    //   278: astore_1
    //   279: aload_1
    //   280: ldc_w 730
    //   283: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   286: pop
    //   287: aload_1
    //   288: aload_0
    //   289: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   292: pop
    //   293: aload_1
    //   294: ldc_w 732
    //   297: invokevirtual 116	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   300: pop
    //   301: aload_3
    //   302: ldc 120
    //   304: aload_1
    //   305: invokevirtual 123	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   308: invokeinterface 129 3 0
    //   313: return
    //   314: astore_1
    //   315: goto -44 -> 271
    //   318: astore_1
    //   319: goto -48 -> 271
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	322	0	paramString	String
    //   0	322	1	paramReadableArray	ReadableArray
    //   0	322	2	paramBoolean	boolean
    //   0	322	3	paramPromise	Promise
    //   178	36	4	i	int
    //   186	7	5	j	int
    //   22	86	6	bool	boolean
    //   8	240	7	localObject1	Object
    //   15	206	8	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   167	177	246	java/lang/Throwable
    //   180	188	246	java/lang/Throwable
    //   195	209	246	java/lang/Throwable
    //   218	225	246	java/lang/Throwable
    //   0	24	254	java/lang/Exception
    //   34	41	254	java/lang/Exception
    //   46	53	254	java/lang/Exception
    //   58	99	254	java/lang/Exception
    //   100	107	254	java/lang/Exception
    //   112	154	254	java/lang/Exception
    //   155	167	254	java/lang/Exception
    //   225	245	254	java/lang/Exception
    //   247	254	254	java/lang/Exception
    //   0	24	314	java/io/FileNotFoundException
    //   34	41	314	java/io/FileNotFoundException
    //   46	53	314	java/io/FileNotFoundException
    //   58	99	314	java/io/FileNotFoundException
    //   100	107	314	java/io/FileNotFoundException
    //   112	154	314	java/io/FileNotFoundException
    //   155	167	314	java/io/FileNotFoundException
    //   225	245	318	java/io/FileNotFoundException
    //   247	254	318	java/io/FileNotFoundException
  }
  
  static void writeFile(String paramString1, String paramString2, String paramString3, boolean paramBoolean, Promise paramPromise)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a21 = a20\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:552)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:1)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:166)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:331)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:387)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:90)\n\t... 17 more\n");
  }
  
  void readStream(String paramString1, String paramString2, int paramInt1, int paramInt2, String paramString3)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a23 = a22\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:552)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:1)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:166)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:331)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:387)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:90)\n\t... 17 more\n");
  }
  
  void scanFile(String[] paramArrayOfString1, String[] paramArrayOfString2, final Callback paramCallback)
  {
    ReactApplicationContext localReactApplicationContext = mCtx;
    try
    {
      MediaScannerConnection.scanFile(localReactApplicationContext, paramArrayOfString1, paramArrayOfString2, new MediaScannerConnection.OnScanCompletedListener()
      {
        public void onScanCompleted(String paramAnonymousString, Uri paramAnonymousUri)
        {
          paramCallback.invoke(new Object[] { null, Boolean.valueOf(true) });
        }
      });
      return;
    }
    catch (Exception paramArrayOfString1)
    {
      paramCallback.invoke(new Object[] { paramArrayOfString1.getLocalizedMessage(), null });
    }
  }
  
  void writeStream(String paramString1, String paramString2, boolean paramBoolean, Callback paramCallback)
  {
    try
    {
      localObject1 = new File(paramString1);
      Object localObject2 = ((File)localObject1).getParentFile();
      boolean bool = ((File)localObject1).exists();
      if (!bool)
      {
        if (localObject2 != null)
        {
          bool = ((File)localObject2).exists();
          if (!bool)
          {
            bool = ((File)localObject2).mkdirs();
            if (!bool)
            {
              paramString2 = new StringBuilder();
              paramString2.append("Failed to create parent directory of '");
              paramString2.append(paramString1);
              paramString2.append("'");
              paramString2 = paramString2.toString();
              paramCallback.invoke(new Object[] { "ENOTDIR", paramString2 });
              return;
            }
          }
        }
        bool = ((File)localObject1).createNewFile();
        if (!bool)
        {
          paramString2 = new StringBuilder();
          paramString2.append("File '");
          paramString2.append(paramString1);
          paramString2.append("' does not exist and could not be created");
          paramString2 = paramString2.toString();
          paramCallback.invoke(new Object[] { "ENOENT", paramString2 });
        }
      }
      else
      {
        bool = ((File)localObject1).isDirectory();
        if (bool)
        {
          paramString2 = new StringBuilder();
          paramString2.append("Expecting a file but '");
          paramString2.append(paramString1);
          paramString2.append("' is a directory");
          paramString2 = paramString2.toString();
          paramCallback.invoke(new Object[] { "EISDIR", paramString2 });
          return;
        }
      }
      localObject1 = new FileOutputStream(paramString1, paramBoolean);
      encoding = paramString2;
      paramString2 = UUID.randomUUID().toString();
      localObject2 = fileStreams;
      ((HashMap)localObject2).put(paramString2, this);
      writeStreamInstance = ((OutputStream)localObject1);
      paramCallback.invoke(new Object[] { null, null, paramString2 });
      return;
    }
    catch (Exception paramString2)
    {
      Object localObject1 = new StringBuilder();
      ((StringBuilder)localObject1).append("Failed to create write stream at path `");
      ((StringBuilder)localObject1).append(paramString1);
      ((StringBuilder)localObject1).append("`; ");
      ((StringBuilder)localObject1).append(paramString2.getLocalizedMessage());
      paramCallback.invoke(new Object[] { "EUNSPECIFIED", ((StringBuilder)localObject1).toString() });
    }
  }
}
