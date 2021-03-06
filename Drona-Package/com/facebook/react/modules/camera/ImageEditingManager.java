package com.facebook.react.modules.camera;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.GuardedAsyncTask;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@ReactModule(name="ImageEditingManager")
public class ImageEditingManager
  extends ReactContextBaseJavaModule
{
  private static final int COMPRESS_QUALITY = 90;
  @SuppressLint({"InlinedApi"})
  private static final String[] EXIF_ATTRIBUTES = { "FNumber", "DateTime", "DateTimeDigitized", "ExposureTime", "Flash", "FocalLength", "GPSAltitude", "GPSAltitudeRef", "GPSDateStamp", "GPSLatitude", "GPSLatitudeRef", "GPSLongitude", "GPSLongitudeRef", "GPSProcessingMethod", "GPSTimeStamp", "ImageLength", "ImageWidth", "ISOSpeedRatings", "Make", "Model", "Orientation", "SubSecTime", "SubSecTimeDigitized", "SubSecTimeOriginal", "WhiteBalance" };
  private static final List<String> LOCAL_URI_PREFIXES = Arrays.asList(new String[] { "file://", "content://" });
  public static final String NAME = "ImageEditingManager";
  private static final String TEMP_FILE_PREFIX = "ReactNative_cropped_image_";
  
  public ImageEditingManager(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
    new CleanTask(getReactApplicationContext(), null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
  }
  
  private static void copyExif(Context paramContext, Uri paramUri, File paramFile)
    throws IOException
  {
    paramContext = getFileFromUri(paramContext, paramUri);
    if (paramContext == null)
    {
      paramContext = new StringBuilder();
      paramContext.append("Couldn't get real path for uri: ");
      paramContext.append(paramUri);
      FLog.warn("ReactNative", paramContext.toString());
      return;
    }
    paramContext = new ExifInterface(paramContext.getAbsolutePath());
    paramUri = new ExifInterface(paramFile.getAbsolutePath());
    paramFile = EXIF_ATTRIBUTES;
    int j = paramFile.length;
    int i = 0;
    while (i < j)
    {
      String str1 = paramFile[i];
      String str2 = paramContext.getAttribute(str1);
      if (str2 != null) {
        paramUri.setAttribute(str1, str2);
      }
      i += 1;
    }
    paramUri.saveAttributes();
  }
  
  private static File createTempFile(Context paramContext, String paramString)
    throws IOException
  {
    File localFile2 = paramContext.getExternalCacheDir();
    File localFile1 = localFile2;
    paramContext = paramContext.getCacheDir();
    if ((localFile2 == null) && (paramContext == null)) {
      throw new IOException("No cache directory available");
    }
    if (localFile2 != null)
    {
      do
      {
        if (paramContext == null)
        {
          paramContext = localFile1;
          break;
        }
      } while (localFile2.getFreeSpace() <= paramContext.getFreeSpace());
      paramContext = localFile1;
    }
    return File.createTempFile("ReactNative_cropped_image_", getFileExtensionForType(paramString), paramContext);
  }
  
  private static Bitmap.CompressFormat getCompressFormatForType(String paramString)
  {
    if ("image/png".equals(paramString)) {
      return Bitmap.CompressFormat.PNG;
    }
    if ("image/webp".equals(paramString)) {
      return Bitmap.CompressFormat.WEBP;
    }
    return Bitmap.CompressFormat.JPEG;
  }
  
  private static int getDecodeSampleSize(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = 1;
    int j;
    if ((paramInt2 > paramInt3) || (paramInt1 > paramInt4))
    {
      paramInt2 /= 2;
      j = paramInt1 / 2;
      paramInt1 = i;
    }
    while ((j / paramInt1 >= paramInt3) && (paramInt2 / paramInt1 >= paramInt4))
    {
      paramInt1 *= 2;
      continue;
      return 1;
    }
    return paramInt1;
  }
  
  private static String getFileExtensionForType(String paramString)
  {
    if ("image/png".equals(paramString)) {
      return ".png";
    }
    if ("image/webp".equals(paramString)) {
      return ".webp";
    }
    return ".jpg";
  }
  
  private static File getFileFromUri(Context paramContext, Uri paramUri)
  {
    if (paramUri.getScheme().equals("file")) {
      return new File(paramUri.getPath());
    }
    if (paramUri.getScheme().equals("content"))
    {
      paramContext = paramContext.getContentResolver().query(paramUri, new String[] { "_data" }, null, null, null);
      if (paramContext != null) {
        try
        {
          boolean bool = paramContext.moveToFirst();
          if (bool)
          {
            paramUri = paramContext.getString(0);
            bool = TextUtils.isEmpty(paramUri);
            if (!bool)
            {
              paramUri = new File(paramUri);
              paramContext.close();
              return paramUri;
            }
          }
          paramContext.close();
        }
        catch (Throwable paramUri)
        {
          paramContext.close();
          throw paramUri;
        }
      }
    }
    return null;
  }
  
  private static boolean isLocalUri(String paramString)
  {
    Iterator localIterator = LOCAL_URI_PREFIXES.iterator();
    while (localIterator.hasNext()) {
      if (paramString.startsWith((String)localIterator.next())) {
        return true;
      }
    }
    return false;
  }
  
  private static void writeCompressedBitmapToFile(Bitmap paramBitmap, String paramString, File paramFile)
    throws IOException
  {
    paramFile = new FileOutputStream(paramFile);
    try
    {
      paramBitmap.compress(getCompressFormatForType(paramString), 90, paramFile);
      paramFile.close();
      return;
    }
    catch (Throwable paramBitmap)
    {
      paramFile.close();
      throw paramBitmap;
    }
  }
  
  public void cropImage(String paramString, ReadableMap paramReadableMap, Callback paramCallback1, Callback paramCallback2)
  {
    boolean bool = paramReadableMap.hasKey("offset");
    ReadableMap localReadableMap2 = null;
    ReadableMap localReadableMap1;
    if (bool) {
      localReadableMap1 = paramReadableMap.getMap("offset");
    } else {
      localReadableMap1 = null;
    }
    if (paramReadableMap.hasKey("size")) {
      localReadableMap2 = paramReadableMap.getMap("size");
    }
    if ((localReadableMap1 != null) && (localReadableMap2 != null) && (localReadableMap1.hasKey("x")) && (localReadableMap1.hasKey("y")) && (localReadableMap2.hasKey("width")) && (localReadableMap2.hasKey("height")))
    {
      if ((paramString != null) && (!paramString.isEmpty()))
      {
        paramString = new CropTask(getReactApplicationContext(), paramString, (int)localReadableMap1.getDouble("x"), (int)localReadableMap1.getDouble("y"), (int)localReadableMap2.getDouble("width"), (int)localReadableMap2.getDouble("height"), paramCallback1, paramCallback2, null);
        if (paramReadableMap.hasKey("displaySize"))
        {
          paramReadableMap = paramReadableMap.getMap("displaySize");
          paramString.setTargetSize((int)paramReadableMap.getDouble("width"), (int)paramReadableMap.getDouble("height"));
        }
        paramString.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        return;
      }
      throw new JSApplicationIllegalArgumentException("Please specify a URI");
    }
    throw new JSApplicationIllegalArgumentException("Please specify offset and size");
  }
  
  public Map getConstants()
  {
    return Collections.emptyMap();
  }
  
  public String getName()
  {
    return "ImageEditingManager";
  }
  
  public void onCatalystInstanceDestroy()
  {
    new CleanTask(getReactApplicationContext(), null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
  }
  
  private static class CleanTask
    extends GuardedAsyncTask<Void, Void>
  {
    private final Context mContext;
    
    private CleanTask(ReactContext paramReactContext)
    {
      super();
      mContext = paramReactContext;
    }
    
    private void cleanDirectory(File paramFile)
    {
      paramFile = paramFile.listFiles(new FilenameFilter()
      {
        public boolean accept(File paramAnonymousFile, String paramAnonymousString)
        {
          return paramAnonymousString.startsWith("ReactNative_cropped_image_");
        }
      });
      if (paramFile != null)
      {
        int j = paramFile.length;
        int i = 0;
        while (i < j)
        {
          paramFile[i].delete();
          i += 1;
        }
      }
    }
    
    protected void doInBackgroundGuarded(Void... paramVarArgs)
    {
      cleanDirectory(mContext.getCacheDir());
      paramVarArgs = mContext.getExternalCacheDir();
      if (paramVarArgs != null) {
        cleanDirectory(paramVarArgs);
      }
    }
  }
  
  private static class CropTask
    extends GuardedAsyncTask<Void, Void>
  {
    final int left;
    final Context mContext;
    final Callback mError;
    final int mHeight;
    final Callback mSuccess;
    int mTargetHeight = 0;
    int mTargetWidth = 0;
    final String mUri;
    final int mWidth;
    final int top;
    
    private CropTask(ReactContext paramReactContext, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Callback paramCallback1, Callback paramCallback2)
    {
      super();
      if ((paramInt1 >= 0) && (paramInt2 >= 0) && (paramInt3 > 0) && (paramInt4 > 0))
      {
        mContext = paramReactContext;
        mUri = paramString;
        left = paramInt1;
        top = paramInt2;
        mWidth = paramInt3;
        mHeight = paramInt4;
        mSuccess = paramCallback1;
        mError = paramCallback2;
        return;
      }
      throw new JSApplicationIllegalArgumentException(String.format("Invalid crop rectangle: [%d, %d, %d, %d]", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt4) }));
    }
    
    private Bitmap crop(BitmapFactory.Options paramOptions)
      throws IOException
    {
      InputStream localInputStream = openBitmapInputStream();
      BitmapRegionDecoder localBitmapRegionDecoder = BitmapRegionDecoder.newInstance(localInputStream, false);
      try
      {
        int i = left;
        int j = top;
        int k = left;
        int m = mWidth;
        int n = top;
        int i1 = mHeight;
        paramOptions = localBitmapRegionDecoder.decodeRegion(new Rect(i, j, k + m, n + i1), paramOptions);
        if (localInputStream != null) {
          localInputStream.close();
        }
        localBitmapRegionDecoder.recycle();
        return paramOptions;
      }
      catch (Throwable paramOptions)
      {
        if (localInputStream != null) {
          localInputStream.close();
        }
        localBitmapRegionDecoder.recycle();
        throw paramOptions;
      }
    }
    
    /* Error */
    private Bitmap cropAndResize(int paramInt1, int paramInt2, BitmapFactory.Options paramOptions)
      throws IOException
    {
      // Byte code:
      //   0: aload_3
      //   1: invokestatic 115	com/facebook/infer/annotation/Assertions:assertNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
      //   4: pop
      //   5: new 117	android/graphics/BitmapFactory$Options
      //   8: dup
      //   9: invokespecial 119	android/graphics/BitmapFactory$Options:<init>	()V
      //   12: astore 13
      //   14: aload 13
      //   16: iconst_1
      //   17: putfield 123	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
      //   20: aload_0
      //   21: invokespecial 82	com/facebook/react/modules/camera/ImageEditingManager$CropTask:openBitmapInputStream	()Ljava/io/InputStream;
      //   24: astore 12
      //   26: aload 12
      //   28: aconst_null
      //   29: aload 13
      //   31: invokestatic 129	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   34: pop
      //   35: aload 12
      //   37: ifnull +8 -> 45
      //   40: aload 12
      //   42: invokevirtual 103	java/io/InputStream:close	()V
      //   45: aload_0
      //   46: getfield 41	com/facebook/react/modules/camera/ImageEditingManager$CropTask:mWidth	I
      //   49: i2f
      //   50: aload_0
      //   51: getfield 43	com/facebook/react/modules/camera/ImageEditingManager$CropTask:mHeight	I
      //   54: i2f
      //   55: fdiv
      //   56: fstore 4
      //   58: iload_1
      //   59: i2f
      //   60: fstore 6
      //   62: iload_2
      //   63: i2f
      //   64: fstore 9
      //   66: fload 6
      //   68: fload 9
      //   70: fdiv
      //   71: fstore 5
      //   73: fload 4
      //   75: fload 5
      //   77: fcmpl
      //   78: ifle +58 -> 136
      //   81: aload_0
      //   82: getfield 43	com/facebook/react/modules/camera/ImageEditingManager$CropTask:mHeight	I
      //   85: i2f
      //   86: fload 5
      //   88: fmul
      //   89: fstore 4
      //   91: aload_0
      //   92: getfield 43	com/facebook/react/modules/camera/ImageEditingManager$CropTask:mHeight	I
      //   95: i2f
      //   96: fstore 5
      //   98: aload_0
      //   99: getfield 37	com/facebook/react/modules/camera/ImageEditingManager$CropTask:left	I
      //   102: i2f
      //   103: aload_0
      //   104: getfield 41	com/facebook/react/modules/camera/ImageEditingManager$CropTask:mWidth	I
      //   107: i2f
      //   108: fload 4
      //   110: fsub
      //   111: fconst_2
      //   112: fdiv
      //   113: fadd
      //   114: fstore 7
      //   116: aload_0
      //   117: getfield 39	com/facebook/react/modules/camera/ImageEditingManager$CropTask:top	I
      //   120: i2f
      //   121: fstore 8
      //   123: fload 9
      //   125: aload_0
      //   126: getfield 43	com/facebook/react/modules/camera/ImageEditingManager$CropTask:mHeight	I
      //   129: i2f
      //   130: fdiv
      //   131: fstore 6
      //   133: goto +55 -> 188
      //   136: aload_0
      //   137: getfield 41	com/facebook/react/modules/camera/ImageEditingManager$CropTask:mWidth	I
      //   140: i2f
      //   141: fstore 4
      //   143: aload_0
      //   144: getfield 41	com/facebook/react/modules/camera/ImageEditingManager$CropTask:mWidth	I
      //   147: i2f
      //   148: fload 5
      //   150: fdiv
      //   151: fstore 5
      //   153: aload_0
      //   154: getfield 37	com/facebook/react/modules/camera/ImageEditingManager$CropTask:left	I
      //   157: i2f
      //   158: fstore 7
      //   160: aload_0
      //   161: getfield 39	com/facebook/react/modules/camera/ImageEditingManager$CropTask:top	I
      //   164: i2f
      //   165: aload_0
      //   166: getfield 43	com/facebook/react/modules/camera/ImageEditingManager$CropTask:mHeight	I
      //   169: i2f
      //   170: fload 5
      //   172: fsub
      //   173: fconst_2
      //   174: fdiv
      //   175: fadd
      //   176: fstore 8
      //   178: fload 6
      //   180: aload_0
      //   181: getfield 41	com/facebook/react/modules/camera/ImageEditingManager$CropTask:mWidth	I
      //   184: i2f
      //   185: fdiv
      //   186: fstore 6
      //   188: aload_3
      //   189: aload_0
      //   190: getfield 41	com/facebook/react/modules/camera/ImageEditingManager$CropTask:mWidth	I
      //   193: aload_0
      //   194: getfield 43	com/facebook/react/modules/camera/ImageEditingManager$CropTask:mHeight	I
      //   197: iload_1
      //   198: iload_2
      //   199: invokestatic 133	com/facebook/react/modules/camera/ImageEditingManager:access$600	(IIII)I
      //   202: putfield 136	android/graphics/BitmapFactory$Options:inSampleSize	I
      //   205: aload 13
      //   207: iconst_0
      //   208: putfield 123	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
      //   211: aload_0
      //   212: invokespecial 82	com/facebook/react/modules/camera/ImageEditingManager$CropTask:openBitmapInputStream	()Ljava/io/InputStream;
      //   215: astore 12
      //   217: aload 12
      //   219: aconst_null
      //   220: aload_3
      //   221: invokestatic 129	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   224: astore 13
      //   226: aload 13
      //   228: ifnull +111 -> 339
      //   231: aload 12
      //   233: ifnull +8 -> 241
      //   236: aload 12
      //   238: invokevirtual 103	java/io/InputStream:close	()V
      //   241: fload 7
      //   243: aload_3
      //   244: getfield 136	android/graphics/BitmapFactory$Options:inSampleSize	I
      //   247: i2f
      //   248: fdiv
      //   249: f2d
      //   250: invokestatic 142	java/lang/Math:floor	(D)D
      //   253: d2i
      //   254: istore_1
      //   255: fload 8
      //   257: aload_3
      //   258: getfield 136	android/graphics/BitmapFactory$Options:inSampleSize	I
      //   261: i2f
      //   262: fdiv
      //   263: f2d
      //   264: invokestatic 142	java/lang/Math:floor	(D)D
      //   267: d2i
      //   268: istore_2
      //   269: fload 4
      //   271: aload_3
      //   272: getfield 136	android/graphics/BitmapFactory$Options:inSampleSize	I
      //   275: i2f
      //   276: fdiv
      //   277: f2d
      //   278: invokestatic 142	java/lang/Math:floor	(D)D
      //   281: d2i
      //   282: istore 10
      //   284: fload 5
      //   286: aload_3
      //   287: getfield 136	android/graphics/BitmapFactory$Options:inSampleSize	I
      //   290: i2f
      //   291: fdiv
      //   292: f2d
      //   293: invokestatic 142	java/lang/Math:floor	(D)D
      //   296: d2i
      //   297: istore 11
      //   299: fload 6
      //   301: aload_3
      //   302: getfield 136	android/graphics/BitmapFactory$Options:inSampleSize	I
      //   305: i2f
      //   306: fmul
      //   307: fstore 4
      //   309: new 144	android/graphics/Matrix
      //   312: dup
      //   313: invokespecial 145	android/graphics/Matrix:<init>	()V
      //   316: astore_3
      //   317: aload_3
      //   318: fload 4
      //   320: fload 4
      //   322: invokevirtual 149	android/graphics/Matrix:setScale	(FF)V
      //   325: aload 13
      //   327: iload_1
      //   328: iload_2
      //   329: iload 10
      //   331: iload 11
      //   333: aload_3
      //   334: iconst_1
      //   335: invokestatic 155	android/graphics/Bitmap:createBitmap	(Landroid/graphics/Bitmap;IIIILandroid/graphics/Matrix;Z)Landroid/graphics/Bitmap;
      //   338: areturn
      //   339: new 157	java/lang/StringBuilder
      //   342: dup
      //   343: invokespecial 158	java/lang/StringBuilder:<init>	()V
      //   346: astore_3
      //   347: aload_3
      //   348: ldc -96
      //   350: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   353: pop
      //   354: aload_3
      //   355: aload_0
      //   356: getfield 35	com/facebook/react/modules/camera/ImageEditingManager$CropTask:mUri	Ljava/lang/String;
      //   359: invokevirtual 164	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   362: pop
      //   363: new 76	java/io/IOException
      //   366: dup
      //   367: aload_3
      //   368: invokevirtual 168	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   371: invokespecial 169	java/io/IOException:<init>	(Ljava/lang/String;)V
      //   374: athrow
      //   375: astore_3
      //   376: aload 12
      //   378: ifnull +8 -> 386
      //   381: aload 12
      //   383: invokevirtual 103	java/io/InputStream:close	()V
      //   386: aload_3
      //   387: athrow
      //   388: astore_3
      //   389: aload 12
      //   391: ifnull +8 -> 399
      //   394: aload 12
      //   396: invokevirtual 103	java/io/InputStream:close	()V
      //   399: aload_3
      //   400: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	401	0	this	CropTask
      //   0	401	1	paramInt1	int
      //   0	401	2	paramInt2	int
      //   0	401	3	paramOptions	BitmapFactory.Options
      //   56	265	4	f1	float
      //   71	214	5	f2	float
      //   60	240	6	f3	float
      //   114	128	7	f4	float
      //   121	135	8	f5	float
      //   64	60	9	f6	float
      //   282	48	10	i	int
      //   297	35	11	j	int
      //   24	371	12	localInputStream	InputStream
      //   12	314	13	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   217	226	375	java/lang/Throwable
      //   339	375	375	java/lang/Throwable
      //   26	35	388	java/lang/Throwable
    }
    
    private InputStream openBitmapInputStream()
      throws IOException
    {
      if (ImageEditingManager.isLocalUri(mUri)) {
        localObject = mContext.getContentResolver().openInputStream(Uri.parse(mUri));
      } else {
        localObject = new URL(mUri).openConnection().getInputStream();
      }
      if (localObject != null) {
        return localObject;
      }
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Cannot open bitmap: ");
      ((StringBuilder)localObject).append(mUri);
      throw new IOException(((StringBuilder)localObject).toString());
    }
    
    protected void doInBackgroundGuarded(Void... paramVarArgs)
    {
      try
      {
        Object localObject = new BitmapFactory.Options();
        int i;
        if ((mTargetWidth > 0) && (mTargetHeight > 0)) {
          i = 1;
        } else {
          i = 0;
        }
        if (i != 0)
        {
          i = mTargetWidth;
          int j = mTargetHeight;
          paramVarArgs = cropAndResize(i, j, (BitmapFactory.Options)localObject);
        }
        else
        {
          paramVarArgs = crop((BitmapFactory.Options)localObject);
        }
        String str = outMimeType;
        if (str != null)
        {
          boolean bool = str.isEmpty();
          if (!bool)
          {
            localObject = mContext;
            localObject = ImageEditingManager.createTempFile((Context)localObject, str);
            ImageEditingManager.writeCompressedBitmapToFile(paramVarArgs, str, (File)localObject);
            bool = str.equals("image/jpeg");
            if (bool)
            {
              paramVarArgs = mContext;
              str = mUri;
              ImageEditingManager.copyExif(paramVarArgs, Uri.parse(str), (File)localObject);
            }
            paramVarArgs = mSuccess;
            localObject = Uri.fromFile((File)localObject).toString();
            paramVarArgs.invoke(new Object[] { localObject });
            return;
          }
        }
        paramVarArgs = new IOException("Could not determine MIME type");
        throw paramVarArgs;
      }
      catch (Exception paramVarArgs)
      {
        mError.invoke(new Object[] { paramVarArgs.getMessage() });
      }
    }
    
    public void setTargetSize(int paramInt1, int paramInt2)
    {
      if ((paramInt1 > 0) && (paramInt2 > 0))
      {
        mTargetWidth = paramInt1;
        mTargetHeight = paramInt2;
        return;
      }
      throw new JSApplicationIllegalArgumentException(String.format("Invalid target size: [%d, %d]", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) }));
    }
  }
}
