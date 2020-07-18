package com.facebook.imagepipeline.producers;

import android.content.res.AssetManager;
import android.net.Uri;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import java.io.IOException;
import java.util.concurrent.Executor;

public class LocalAssetFetchProducer
  extends LocalFetchProducer
{
  public static final String PRODUCER_NAME = "LocalAssetFetchProducer";
  private final AssetManager mAssetManager;
  
  public LocalAssetFetchProducer(Executor paramExecutor, PooledByteBufferFactory paramPooledByteBufferFactory, AssetManager paramAssetManager)
  {
    super(paramExecutor, paramPooledByteBufferFactory);
    mAssetManager = paramAssetManager;
  }
  
  private static String getAssetName(ImageRequest paramImageRequest)
  {
    return paramImageRequest.getSourceUri().getPath().substring(1);
  }
  
  /* Error */
  private int getLength(ImageRequest paramImageRequest)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aconst_null
    //   4: astore 6
    //   6: aload_0
    //   7: getfield 17	com/facebook/imagepipeline/producers/LocalAssetFetchProducer:mAssetManager	Landroid/content/res/AssetManager;
    //   10: astore 7
    //   12: aload 7
    //   14: aload_1
    //   15: invokestatic 46	com/facebook/imagepipeline/producers/LocalAssetFetchProducer:getAssetName	(Lcom/facebook/imagepipeline/request/ImageRequest;)Ljava/lang/String;
    //   18: invokevirtual 52	android/content/res/AssetManager:openFd	(Ljava/lang/String;)Landroid/content/res/AssetFileDescriptor;
    //   21: astore_1
    //   22: aload_1
    //   23: invokevirtual 57	android/content/res/AssetFileDescriptor:getLength	()J
    //   26: lstore_3
    //   27: lload_3
    //   28: l2i
    //   29: istore_2
    //   30: aload_1
    //   31: ifnull +69 -> 100
    //   34: aload_1
    //   35: invokevirtual 61	android/content/res/AssetFileDescriptor:close	()V
    //   38: iload_2
    //   39: ireturn
    //   40: astore 5
    //   42: aload_1
    //   43: astore 6
    //   45: aload 5
    //   47: astore_1
    //   48: goto +7 -> 55
    //   51: goto +16 -> 67
    //   54: astore_1
    //   55: aload 6
    //   57: ifnull +8 -> 65
    //   60: aload 6
    //   62: invokevirtual 61	android/content/res/AssetFileDescriptor:close	()V
    //   65: aload_1
    //   66: athrow
    //   67: aload_1
    //   68: ifnull +34 -> 102
    //   71: aload_1
    //   72: invokevirtual 61	android/content/res/AssetFileDescriptor:close	()V
    //   75: iconst_m1
    //   76: ireturn
    //   77: astore_1
    //   78: aload 5
    //   80: astore_1
    //   81: goto -14 -> 67
    //   84: astore 5
    //   86: goto -35 -> 51
    //   89: astore_1
    //   90: iload_2
    //   91: ireturn
    //   92: astore 5
    //   94: goto -29 -> 65
    //   97: astore_1
    //   98: iconst_m1
    //   99: ireturn
    //   100: iload_2
    //   101: ireturn
    //   102: iconst_m1
    //   103: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	104	0	this	LocalAssetFetchProducer
    //   0	104	1	paramImageRequest	ImageRequest
    //   29	72	2	i	int
    //   26	2	3	l	long
    //   1	1	5	localObject	Object
    //   40	39	5	localThrowable	Throwable
    //   84	1	5	localIOException1	IOException
    //   92	1	5	localIOException2	IOException
    //   4	57	6	localImageRequest	ImageRequest
    //   10	3	7	localAssetManager	AssetManager
    // Exception table:
    //   from	to	target	type
    //   22	27	40	java/lang/Throwable
    //   12	22	54	java/lang/Throwable
    //   12	22	77	java/io/IOException
    //   22	27	84	java/io/IOException
    //   34	38	89	java/io/IOException
    //   60	65	92	java/io/IOException
    //   71	75	97	java/io/IOException
  }
  
  protected EncodedImage getEncodedImage(ImageRequest paramImageRequest)
    throws IOException
  {
    return getEncodedImage(mAssetManager.open(getAssetName(paramImageRequest), 2), getLength(paramImageRequest));
  }
  
  protected String getProducerName()
  {
    return "LocalAssetFetchProducer";
  }
}
