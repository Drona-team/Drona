package com.bumptech.glide.load.data.mediastore;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

class ThumbnailStreamOpener
{
  private static final FileService DEFAULT_SERVICE = new FileService();
  private static final String PAGE_KEY = "ThumbStreamOpener";
  private final ArrayPool byteArrayPool;
  private final ContentResolver contentResolver;
  private final List<ImageHeaderParser> parsers;
  private final ThumbnailQuery query;
  private final FileService service;
  
  ThumbnailStreamOpener(List paramList, FileService paramFileService, ThumbnailQuery paramThumbnailQuery, ArrayPool paramArrayPool, ContentResolver paramContentResolver)
  {
    service = paramFileService;
    query = paramThumbnailQuery;
    byteArrayPool = paramArrayPool;
    contentResolver = paramContentResolver;
    parsers = paramList;
  }
  
  ThumbnailStreamOpener(List paramList, ThumbnailQuery paramThumbnailQuery, ArrayPool paramArrayPool, ContentResolver paramContentResolver)
  {
    this(paramList, DEFAULT_SERVICE, paramThumbnailQuery, paramArrayPool, paramContentResolver);
  }
  
  private String getPath(Uri paramUri)
  {
    paramUri = query.query(paramUri);
    if (paramUri != null) {
      try
      {
        boolean bool = paramUri.moveToFirst();
        if (bool)
        {
          String str = paramUri.getString(0);
          if (paramUri == null) {
            break label71;
          }
          paramUri.close();
          return str;
        }
      }
      catch (Throwable localThrowable)
      {
        if (paramUri != null) {
          paramUri.close();
        }
        throw localThrowable;
      }
    }
    if (paramUri != null)
    {
      paramUri.close();
      return null;
      label71:
      return localThrowable;
    }
    return null;
  }
  
  private boolean isValid(File paramFile)
  {
    return (service.exists(paramFile)) && (0L < service.length(paramFile));
  }
  
  /* Error */
  int getOrientation(Uri paramUri)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 40	com/bumptech/glide/load/data/mediastore/ThumbnailStreamOpener:contentResolver	Landroid/content/ContentResolver;
    //   4: astore 4
    //   6: aload 4
    //   8: aload_1
    //   9: invokevirtual 88	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   12: astore 6
    //   14: aload 6
    //   16: astore 5
    //   18: aload_0
    //   19: getfield 42	com/bumptech/glide/load/data/mediastore/ThumbnailStreamOpener:parsers	Ljava/util/List;
    //   22: astore 7
    //   24: aload_0
    //   25: getfield 38	com/bumptech/glide/load/data/mediastore/ThumbnailStreamOpener:byteArrayPool	Lcom/bumptech/glide/load/engine/bitmap_recycle/ArrayPool;
    //   28: astore 8
    //   30: aload 5
    //   32: astore 4
    //   34: aload 7
    //   36: aload 6
    //   38: aload 8
    //   40: invokestatic 93	com/bumptech/glide/load/ImageHeaderParserUtils:getOrientation	(Ljava/util/List;Ljava/io/InputStream;Lcom/bumptech/glide/load/engine/bitmap_recycle/ArrayPool;)I
    //   43: istore_2
    //   44: aload 6
    //   46: ifnull +132 -> 178
    //   49: aload 6
    //   51: invokevirtual 96	java/io/InputStream:close	()V
    //   54: iload_2
    //   55: ireturn
    //   56: astore 6
    //   58: goto +15 -> 73
    //   61: astore_1
    //   62: aconst_null
    //   63: astore 4
    //   65: goto +89 -> 154
    //   68: astore 6
    //   70: aconst_null
    //   71: astore 5
    //   73: aload 5
    //   75: astore 4
    //   77: ldc 10
    //   79: iconst_3
    //   80: invokestatic 102	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   83: istore_3
    //   84: iload_3
    //   85: ifeq +56 -> 141
    //   88: aload 5
    //   90: astore 4
    //   92: new 104	java/lang/StringBuilder
    //   95: dup
    //   96: invokespecial 105	java/lang/StringBuilder:<init>	()V
    //   99: astore 7
    //   101: aload 5
    //   103: astore 4
    //   105: aload 7
    //   107: ldc 107
    //   109: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   112: pop
    //   113: aload 5
    //   115: astore 4
    //   117: aload 7
    //   119: aload_1
    //   120: invokevirtual 114	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   123: pop
    //   124: aload 5
    //   126: astore 4
    //   128: ldc 10
    //   130: aload 7
    //   132: invokevirtual 118	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   135: aload 6
    //   137: invokestatic 122	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   140: pop
    //   141: aload 5
    //   143: ifnull +8 -> 151
    //   146: aload 5
    //   148: invokevirtual 96	java/io/InputStream:close	()V
    //   151: iconst_m1
    //   152: ireturn
    //   153: astore_1
    //   154: aload 4
    //   156: ifnull +8 -> 164
    //   159: aload 4
    //   161: invokevirtual 96	java/io/InputStream:close	()V
    //   164: aload_1
    //   165: athrow
    //   166: astore_1
    //   167: iload_2
    //   168: ireturn
    //   169: astore_1
    //   170: goto -19 -> 151
    //   173: astore 4
    //   175: goto -11 -> 164
    //   178: iload_2
    //   179: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	180	0	this	ThumbnailStreamOpener
    //   0	180	1	paramUri	Uri
    //   43	136	2	i	int
    //   83	2	3	bool	boolean
    //   4	156	4	localObject1	Object
    //   173	1	4	localIOException1	java.io.IOException
    //   16	131	5	localInputStream1	InputStream
    //   12	38	6	localInputStream2	InputStream
    //   56	1	6	localIOException2	java.io.IOException
    //   68	68	6	localIOException3	java.io.IOException
    //   22	109	7	localObject2	Object
    //   28	11	8	localArrayPool	ArrayPool
    // Exception table:
    //   from	to	target	type
    //   34	44	56	java/io/IOException
    //   34	44	56	java/lang/NullPointerException
    //   6	14	61	java/lang/Throwable
    //   6	14	68	java/io/IOException
    //   6	14	68	java/lang/NullPointerException
    //   34	44	153	java/lang/Throwable
    //   77	84	153	java/lang/Throwable
    //   92	101	153	java/lang/Throwable
    //   105	113	153	java/lang/Throwable
    //   117	124	153	java/lang/Throwable
    //   128	141	153	java/lang/Throwable
    //   49	54	166	java/io/IOException
    //   146	151	169	java/io/IOException
    //   159	164	173	java/io/IOException
  }
  
  public InputStream open(Uri paramUri)
    throws FileNotFoundException
  {
    Object localObject = getPath(paramUri);
    if (TextUtils.isEmpty((CharSequence)localObject)) {
      return null;
    }
    localObject = service.get((String)localObject);
    if (!isValid((File)localObject)) {
      return null;
    }
    localObject = Uri.fromFile((File)localObject);
    try
    {
      InputStream localInputStream = contentResolver.openInputStream((Uri)localObject);
      return localInputStream;
    }
    catch (NullPointerException localNullPointerException)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("NPE opening uri: ");
      localStringBuilder.append(paramUri);
      localStringBuilder.append(" -> ");
      localStringBuilder.append(localObject);
      throw ((FileNotFoundException)new FileNotFoundException(localStringBuilder.toString()).initCause(localNullPointerException));
    }
  }
}
