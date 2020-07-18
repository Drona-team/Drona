package com.google.android.exoplayer2.upstream;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class RawResourceDataSource
  extends BaseDataSource
{
  public static final String RAW_RESOURCE_SCHEME = "rawresource";
  @Nullable
  private AssetFileDescriptor assetFileDescriptor;
  private long bytesRemaining;
  @Nullable
  private InputStream inputStream;
  private boolean opened;
  private final Resources resources;
  @Nullable
  private Uri uri;
  
  public RawResourceDataSource(Context paramContext)
  {
    super(false);
    resources = paramContext.getResources();
  }
  
  public RawResourceDataSource(Context paramContext, TransferListener paramTransferListener)
  {
    this(paramContext);
    if (paramTransferListener != null) {
      addTransferListener(paramTransferListener);
    }
  }
  
  public static Uri buildRawResourceUri(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("rawresource:///");
    localStringBuilder.append(paramInt);
    return Uri.parse(localStringBuilder.toString());
  }
  
  /* Error */
  public void close()
    throws RawResourceDataSource.RawResourceDataSourceException
  {
    // Byte code:
    //   0: aload_0
    //   1: aconst_null
    //   2: putfield 78	com/google/android/exoplayer2/upstream/RawResourceDataSource:uri	Landroid/net/Uri;
    //   5: aload_0
    //   6: getfield 80	com/google/android/exoplayer2/upstream/RawResourceDataSource:inputStream	Ljava/io/InputStream;
    //   9: astore_1
    //   10: aload_1
    //   11: ifnull +12 -> 23
    //   14: aload_0
    //   15: getfield 80	com/google/android/exoplayer2/upstream/RawResourceDataSource:inputStream	Ljava/io/InputStream;
    //   18: astore_1
    //   19: aload_1
    //   20: invokevirtual 84	java/io/InputStream:close	()V
    //   23: aload_0
    //   24: aconst_null
    //   25: putfield 80	com/google/android/exoplayer2/upstream/RawResourceDataSource:inputStream	Ljava/io/InputStream;
    //   28: aload_0
    //   29: getfield 86	com/google/android/exoplayer2/upstream/RawResourceDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   32: astore_1
    //   33: aload_1
    //   34: ifnull +12 -> 46
    //   37: aload_0
    //   38: getfield 86	com/google/android/exoplayer2/upstream/RawResourceDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   41: astore_1
    //   42: aload_1
    //   43: invokevirtual 89	android/content/res/AssetFileDescriptor:close	()V
    //   46: aload_0
    //   47: aconst_null
    //   48: putfield 86	com/google/android/exoplayer2/upstream/RawResourceDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   51: aload_0
    //   52: getfield 91	com/google/android/exoplayer2/upstream/RawResourceDataSource:opened	Z
    //   55: ifeq +147 -> 202
    //   58: aload_0
    //   59: iconst_0
    //   60: putfield 91	com/google/android/exoplayer2/upstream/RawResourceDataSource:opened	Z
    //   63: aload_0
    //   64: invokevirtual 94	com/google/android/exoplayer2/upstream/BaseDataSource:transferEnded	()V
    //   67: return
    //   68: astore_1
    //   69: goto +13 -> 82
    //   72: astore_1
    //   73: new 6	com/google/android/exoplayer2/upstream/RawResourceDataSource$RawResourceDataSourceException
    //   76: dup
    //   77: aload_1
    //   78: invokespecial 97	com/google/android/exoplayer2/upstream/RawResourceDataSource$RawResourceDataSourceException:<init>	(Ljava/io/IOException;)V
    //   81: athrow
    //   82: aload_0
    //   83: aconst_null
    //   84: putfield 86	com/google/android/exoplayer2/upstream/RawResourceDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   87: aload_0
    //   88: getfield 91	com/google/android/exoplayer2/upstream/RawResourceDataSource:opened	Z
    //   91: ifeq +12 -> 103
    //   94: aload_0
    //   95: iconst_0
    //   96: putfield 91	com/google/android/exoplayer2/upstream/RawResourceDataSource:opened	Z
    //   99: aload_0
    //   100: invokevirtual 94	com/google/android/exoplayer2/upstream/BaseDataSource:transferEnded	()V
    //   103: aload_1
    //   104: athrow
    //   105: astore_1
    //   106: goto +13 -> 119
    //   109: astore_1
    //   110: new 6	com/google/android/exoplayer2/upstream/RawResourceDataSource$RawResourceDataSourceException
    //   113: dup
    //   114: aload_1
    //   115: invokespecial 97	com/google/android/exoplayer2/upstream/RawResourceDataSource$RawResourceDataSourceException:<init>	(Ljava/io/IOException;)V
    //   118: athrow
    //   119: aload_0
    //   120: aconst_null
    //   121: putfield 80	com/google/android/exoplayer2/upstream/RawResourceDataSource:inputStream	Ljava/io/InputStream;
    //   124: aload_0
    //   125: getfield 86	com/google/android/exoplayer2/upstream/RawResourceDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   128: astore_2
    //   129: aload_2
    //   130: ifnull +12 -> 142
    //   133: aload_0
    //   134: getfield 86	com/google/android/exoplayer2/upstream/RawResourceDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   137: astore_2
    //   138: aload_2
    //   139: invokevirtual 89	android/content/res/AssetFileDescriptor:close	()V
    //   142: aload_0
    //   143: aconst_null
    //   144: putfield 86	com/google/android/exoplayer2/upstream/RawResourceDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   147: aload_0
    //   148: getfield 91	com/google/android/exoplayer2/upstream/RawResourceDataSource:opened	Z
    //   151: ifeq +12 -> 163
    //   154: aload_0
    //   155: iconst_0
    //   156: putfield 91	com/google/android/exoplayer2/upstream/RawResourceDataSource:opened	Z
    //   159: aload_0
    //   160: invokevirtual 94	com/google/android/exoplayer2/upstream/BaseDataSource:transferEnded	()V
    //   163: aload_1
    //   164: athrow
    //   165: astore_1
    //   166: goto +13 -> 179
    //   169: astore_1
    //   170: new 6	com/google/android/exoplayer2/upstream/RawResourceDataSource$RawResourceDataSourceException
    //   173: dup
    //   174: aload_1
    //   175: invokespecial 97	com/google/android/exoplayer2/upstream/RawResourceDataSource$RawResourceDataSourceException:<init>	(Ljava/io/IOException;)V
    //   178: athrow
    //   179: aload_0
    //   180: aconst_null
    //   181: putfield 86	com/google/android/exoplayer2/upstream/RawResourceDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   184: aload_0
    //   185: getfield 91	com/google/android/exoplayer2/upstream/RawResourceDataSource:opened	Z
    //   188: ifeq +12 -> 200
    //   191: aload_0
    //   192: iconst_0
    //   193: putfield 91	com/google/android/exoplayer2/upstream/RawResourceDataSource:opened	Z
    //   196: aload_0
    //   197: invokevirtual 94	com/google/android/exoplayer2/upstream/BaseDataSource:transferEnded	()V
    //   200: aload_1
    //   201: athrow
    //   202: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	203	0	this	RawResourceDataSource
    //   9	34	1	localObject	Object
    //   68	1	1	localThrowable1	Throwable
    //   72	32	1	localIOException1	IOException
    //   105	1	1	localThrowable2	Throwable
    //   109	55	1	localIOException2	IOException
    //   165	1	1	localThrowable3	Throwable
    //   169	32	1	localIOException3	IOException
    //   128	11	2	localAssetFileDescriptor	AssetFileDescriptor
    // Exception table:
    //   from	to	target	type
    //   28	33	68	java/lang/Throwable
    //   42	46	68	java/lang/Throwable
    //   73	82	68	java/lang/Throwable
    //   42	46	72	java/io/IOException
    //   5	10	105	java/lang/Throwable
    //   19	23	105	java/lang/Throwable
    //   110	119	105	java/lang/Throwable
    //   19	23	109	java/io/IOException
    //   124	129	165	java/lang/Throwable
    //   138	142	165	java/lang/Throwable
    //   170	179	165	java/lang/Throwable
    //   138	142	169	java/io/IOException
  }
  
  public Uri getUri()
  {
    return uri;
  }
  
  public long open(DataSpec paramDataSpec)
    throws RawResourceDataSource.RawResourceDataSourceException
  {
    uri = uri;
    Object localObject = uri;
    for (;;)
    {
      try
      {
        boolean bool = TextUtils.equals("rawresource", ((Uri)localObject).getScheme());
        if (bool) {
          localObject = uri;
        }
      }
      catch (IOException paramDataSpec)
      {
        int i;
        AssetFileDescriptor localAssetFileDescriptor;
        long l1;
        throw new RawResourceDataSourceException((IOException)paramDataSpec);
      }
      try
      {
        i = Integer.parseInt(((Uri)localObject).getLastPathSegment());
        transferInitializing(paramDataSpec);
        localObject = resources;
        localObject = ((Resources)localObject).openRawResourceFd(i);
        assetFileDescriptor = ((AssetFileDescriptor)localObject);
        localObject = assetFileDescriptor;
        localObject = new FileInputStream(((AssetFileDescriptor)localObject).getFileDescriptor());
        inputStream = ((InputStream)localObject);
        localObject = inputStream;
        localAssetFileDescriptor = assetFileDescriptor;
        ((InputStream)localObject).skip(localAssetFileDescriptor.getStartOffset());
        localObject = inputStream;
        l1 = position;
        l1 = ((InputStream)localObject).skip(l1);
        if (l1 >= position)
        {
          long l2 = length;
          l1 = -1L;
          if (l2 != -1L)
          {
            bytesRemaining = length;
          }
          else
          {
            localObject = assetFileDescriptor;
            l2 = ((AssetFileDescriptor)localObject).getLength();
            if (l2 != -1L) {
              l1 = l2 - position;
            }
            bytesRemaining = l1;
          }
          opened = true;
          transferStarted(paramDataSpec);
          return bytesRemaining;
        }
        paramDataSpec = new EOFException();
        throw paramDataSpec;
      }
      catch (NumberFormatException paramDataSpec) {}
    }
    paramDataSpec = new RawResourceDataSourceException("Resource identifier must be an integer.");
    throw paramDataSpec;
    paramDataSpec = new RawResourceDataSourceException("URI must use scheme rawresource");
    throw paramDataSpec;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws RawResourceDataSource.RawResourceDataSourceException
  {
    if (paramInt2 == 0) {
      return 0;
    }
    if (bytesRemaining == 0L) {
      return -1;
    }
    long l1;
    long l2;
    if (bytesRemaining != -1L)
    {
      l1 = bytesRemaining;
      l2 = paramInt2;
    }
    try
    {
      l1 = Math.min(l1, l2);
      paramInt2 = (int)l1;
      InputStream localInputStream = inputStream;
      paramInt1 = localInputStream.read(paramArrayOfByte, paramInt1, paramInt2);
      if (paramInt1 == -1)
      {
        if (bytesRemaining == -1L) {
          return -1;
        }
        throw new RawResourceDataSourceException(new EOFException());
      }
      if (bytesRemaining != -1L) {
        bytesRemaining -= paramInt1;
      }
      bytesTransferred(paramInt1);
      return paramInt1;
    }
    catch (IOException paramArrayOfByte)
    {
      throw new RawResourceDataSourceException(paramArrayOfByte);
    }
  }
  
  public static class RawResourceDataSourceException
    extends IOException
  {
    public RawResourceDataSourceException(IOException paramIOException)
    {
      super();
    }
    
    public RawResourceDataSourceException(String paramString)
    {
      super();
    }
  }
}
