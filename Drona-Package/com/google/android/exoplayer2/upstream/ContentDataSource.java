package com.google.android.exoplayer2.upstream;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import androidx.annotation.Nullable;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;

public final class ContentDataSource
  extends BaseDataSource
{
  @Nullable
  private AssetFileDescriptor assetFileDescriptor;
  private long bytesRemaining;
  @Nullable
  private FileInputStream inputStream;
  private boolean opened;
  private final ContentResolver resolver;
  @Nullable
  private Uri uri;
  
  public ContentDataSource(Context paramContext)
  {
    super(false);
    resolver = paramContext.getContentResolver();
  }
  
  public ContentDataSource(Context paramContext, TransferListener paramTransferListener)
  {
    this(paramContext);
    if (paramTransferListener != null) {
      addTransferListener(paramTransferListener);
    }
  }
  
  /* Error */
  public void close()
    throws ContentDataSource.ContentDataSourceException
  {
    // Byte code:
    //   0: aload_0
    //   1: aconst_null
    //   2: putfield 49	com/google/android/exoplayer2/upstream/ContentDataSource:uri	Landroid/net/Uri;
    //   5: aload_0
    //   6: getfield 51	com/google/android/exoplayer2/upstream/ContentDataSource:inputStream	Ljava/io/FileInputStream;
    //   9: astore_1
    //   10: aload_1
    //   11: ifnull +12 -> 23
    //   14: aload_0
    //   15: getfield 51	com/google/android/exoplayer2/upstream/ContentDataSource:inputStream	Ljava/io/FileInputStream;
    //   18: astore_1
    //   19: aload_1
    //   20: invokevirtual 55	java/io/FileInputStream:close	()V
    //   23: aload_0
    //   24: aconst_null
    //   25: putfield 51	com/google/android/exoplayer2/upstream/ContentDataSource:inputStream	Ljava/io/FileInputStream;
    //   28: aload_0
    //   29: getfield 57	com/google/android/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   32: astore_1
    //   33: aload_1
    //   34: ifnull +12 -> 46
    //   37: aload_0
    //   38: getfield 57	com/google/android/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   41: astore_1
    //   42: aload_1
    //   43: invokevirtual 60	android/content/res/AssetFileDescriptor:close	()V
    //   46: aload_0
    //   47: aconst_null
    //   48: putfield 57	com/google/android/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   51: aload_0
    //   52: getfield 62	com/google/android/exoplayer2/upstream/ContentDataSource:opened	Z
    //   55: ifeq +147 -> 202
    //   58: aload_0
    //   59: iconst_0
    //   60: putfield 62	com/google/android/exoplayer2/upstream/ContentDataSource:opened	Z
    //   63: aload_0
    //   64: invokevirtual 65	com/google/android/exoplayer2/upstream/BaseDataSource:transferEnded	()V
    //   67: return
    //   68: astore_1
    //   69: goto +13 -> 82
    //   72: astore_1
    //   73: new 6	com/google/android/exoplayer2/upstream/ContentDataSource$ContentDataSourceException
    //   76: dup
    //   77: aload_1
    //   78: invokespecial 68	com/google/android/exoplayer2/upstream/ContentDataSource$ContentDataSourceException:<init>	(Ljava/io/IOException;)V
    //   81: athrow
    //   82: aload_0
    //   83: aconst_null
    //   84: putfield 57	com/google/android/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   87: aload_0
    //   88: getfield 62	com/google/android/exoplayer2/upstream/ContentDataSource:opened	Z
    //   91: ifeq +12 -> 103
    //   94: aload_0
    //   95: iconst_0
    //   96: putfield 62	com/google/android/exoplayer2/upstream/ContentDataSource:opened	Z
    //   99: aload_0
    //   100: invokevirtual 65	com/google/android/exoplayer2/upstream/BaseDataSource:transferEnded	()V
    //   103: aload_1
    //   104: athrow
    //   105: astore_1
    //   106: goto +13 -> 119
    //   109: astore_1
    //   110: new 6	com/google/android/exoplayer2/upstream/ContentDataSource$ContentDataSourceException
    //   113: dup
    //   114: aload_1
    //   115: invokespecial 68	com/google/android/exoplayer2/upstream/ContentDataSource$ContentDataSourceException:<init>	(Ljava/io/IOException;)V
    //   118: athrow
    //   119: aload_0
    //   120: aconst_null
    //   121: putfield 51	com/google/android/exoplayer2/upstream/ContentDataSource:inputStream	Ljava/io/FileInputStream;
    //   124: aload_0
    //   125: getfield 57	com/google/android/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   128: astore_2
    //   129: aload_2
    //   130: ifnull +12 -> 142
    //   133: aload_0
    //   134: getfield 57	com/google/android/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   137: astore_2
    //   138: aload_2
    //   139: invokevirtual 60	android/content/res/AssetFileDescriptor:close	()V
    //   142: aload_0
    //   143: aconst_null
    //   144: putfield 57	com/google/android/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   147: aload_0
    //   148: getfield 62	com/google/android/exoplayer2/upstream/ContentDataSource:opened	Z
    //   151: ifeq +12 -> 163
    //   154: aload_0
    //   155: iconst_0
    //   156: putfield 62	com/google/android/exoplayer2/upstream/ContentDataSource:opened	Z
    //   159: aload_0
    //   160: invokevirtual 65	com/google/android/exoplayer2/upstream/BaseDataSource:transferEnded	()V
    //   163: aload_1
    //   164: athrow
    //   165: astore_1
    //   166: goto +13 -> 179
    //   169: astore_1
    //   170: new 6	com/google/android/exoplayer2/upstream/ContentDataSource$ContentDataSourceException
    //   173: dup
    //   174: aload_1
    //   175: invokespecial 68	com/google/android/exoplayer2/upstream/ContentDataSource$ContentDataSourceException:<init>	(Ljava/io/IOException;)V
    //   178: athrow
    //   179: aload_0
    //   180: aconst_null
    //   181: putfield 57	com/google/android/exoplayer2/upstream/ContentDataSource:assetFileDescriptor	Landroid/content/res/AssetFileDescriptor;
    //   184: aload_0
    //   185: getfield 62	com/google/android/exoplayer2/upstream/ContentDataSource:opened	Z
    //   188: ifeq +12 -> 200
    //   191: aload_0
    //   192: iconst_0
    //   193: putfield 62	com/google/android/exoplayer2/upstream/ContentDataSource:opened	Z
    //   196: aload_0
    //   197: invokevirtual 65	com/google/android/exoplayer2/upstream/BaseDataSource:transferEnded	()V
    //   200: aload_1
    //   201: athrow
    //   202: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	203	0	this	ContentDataSource
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
    throws ContentDataSource.ContentDataSourceException
  {
    uri = uri;
    try
    {
      transferInitializing(paramDataSpec);
      Object localObject = resolver;
      Uri localUri = uri;
      localObject = ((ContentResolver)localObject).openAssetFileDescriptor(localUri, "r");
      assetFileDescriptor = ((AssetFileDescriptor)localObject);
      if (assetFileDescriptor != null)
      {
        localObject = assetFileDescriptor;
        localObject = new FileInputStream(((AssetFileDescriptor)localObject).getFileDescriptor());
        inputStream = ((FileInputStream)localObject);
        localObject = assetFileDescriptor;
        long l1 = ((AssetFileDescriptor)localObject).getStartOffset();
        localObject = inputStream;
        long l2 = position;
        l2 = ((FileInputStream)localObject).skip(l2 + l1);
        l2 -= l1;
        if (l2 == position)
        {
          long l3 = length;
          l1 = -1L;
          if (l3 != -1L)
          {
            bytesRemaining = length;
          }
          else
          {
            localObject = assetFileDescriptor;
            l3 = ((AssetFileDescriptor)localObject).getLength();
            if (l3 == -1L)
            {
              localObject = inputStream;
              localObject = ((FileInputStream)localObject).getChannel();
              l2 = ((FileChannel)localObject).size();
              if (l2 != 0L)
              {
                l1 = ((FileChannel)localObject).position();
                l1 = l2 - l1;
              }
              bytesRemaining = l1;
            }
            else
            {
              bytesRemaining = (l3 - l2);
            }
          }
          opened = true;
          transferStarted(paramDataSpec);
          return bytesRemaining;
        }
        paramDataSpec = new EOFException();
        throw paramDataSpec;
      }
      paramDataSpec = new StringBuilder();
      paramDataSpec.append("Could not open file descriptor for: ");
      localObject = uri;
      paramDataSpec.append(localObject);
      paramDataSpec = new FileNotFoundException(paramDataSpec.toString());
      throw paramDataSpec;
    }
    catch (IOException paramDataSpec)
    {
      throw new ContentDataSourceException((IOException)paramDataSpec);
    }
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws ContentDataSource.ContentDataSourceException
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
      FileInputStream localFileInputStream = inputStream;
      paramInt1 = localFileInputStream.read(paramArrayOfByte, paramInt1, paramInt2);
      if (paramInt1 == -1)
      {
        if (bytesRemaining == -1L) {
          return -1;
        }
        throw new ContentDataSourceException(new EOFException());
      }
      if (bytesRemaining != -1L) {
        bytesRemaining -= paramInt1;
      }
      bytesTransferred(paramInt1);
      return paramInt1;
    }
    catch (IOException paramArrayOfByte)
    {
      throw new ContentDataSourceException(paramArrayOfByte);
    }
  }
  
  public static class ContentDataSourceException
    extends IOException
  {
    public ContentDataSourceException(IOException paramIOException)
    {
      super();
    }
  }
}
