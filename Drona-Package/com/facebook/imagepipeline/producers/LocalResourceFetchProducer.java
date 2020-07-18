package com.facebook.imagepipeline.producers;

import android.content.res.Resources;
import android.net.Uri;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import java.io.IOException;
import java.util.concurrent.Executor;

public class LocalResourceFetchProducer
  extends LocalFetchProducer
{
  public static final String PRODUCER_NAME = "LocalResourceFetchProducer";
  private final Resources mResources;
  
  public LocalResourceFetchProducer(Executor paramExecutor, PooledByteBufferFactory paramPooledByteBufferFactory, Resources paramResources)
  {
    super(paramExecutor, paramPooledByteBufferFactory);
    mResources = paramResources;
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
    //   7: getfield 17	com/facebook/imagepipeline/producers/LocalResourceFetchProducer:mResources	Landroid/content/res/Resources;
    //   10: astore 7
    //   12: aload 7
    //   14: aload_1
    //   15: invokestatic 29	com/facebook/imagepipeline/producers/LocalResourceFetchProducer:getResourceId	(Lcom/facebook/imagepipeline/request/ImageRequest;)I
    //   18: invokevirtual 35	android/content/res/Resources:openRawResourceFd	(I)Landroid/content/res/AssetFileDescriptor;
    //   21: astore_1
    //   22: aload_1
    //   23: invokevirtual 40	android/content/res/AssetFileDescriptor:getLength	()J
    //   26: lstore_3
    //   27: lload_3
    //   28: l2i
    //   29: istore_2
    //   30: aload_1
    //   31: ifnull +69 -> 100
    //   34: aload_1
    //   35: invokevirtual 44	android/content/res/AssetFileDescriptor:close	()V
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
    //   62: invokevirtual 44	android/content/res/AssetFileDescriptor:close	()V
    //   65: aload_1
    //   66: athrow
    //   67: aload_1
    //   68: ifnull +34 -> 102
    //   71: aload_1
    //   72: invokevirtual 44	android/content/res/AssetFileDescriptor:close	()V
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
    //   0	104	0	this	LocalResourceFetchProducer
    //   0	104	1	paramImageRequest	ImageRequest
    //   29	72	2	i	int
    //   26	2	3	l	long
    //   1	1	5	localObject	Object
    //   40	39	5	localThrowable	Throwable
    //   84	1	5	localNotFoundException	android.content.res.Resources.NotFoundException
    //   92	1	5	localIOException	IOException
    //   4	57	6	localImageRequest	ImageRequest
    //   10	3	7	localResources	Resources
    // Exception table:
    //   from	to	target	type
    //   22	27	40	java/lang/Throwable
    //   12	22	54	java/lang/Throwable
    //   12	22	77	android/content/res/Resources$NotFoundException
    //   22	27	84	android/content/res/Resources$NotFoundException
    //   34	38	89	java/io/IOException
    //   60	65	92	java/io/IOException
    //   71	75	97	java/io/IOException
  }
  
  private static int getResourceId(ImageRequest paramImageRequest)
  {
    return Integer.parseInt(paramImageRequest.getSourceUri().getPath().substring(1));
  }
  
  protected EncodedImage getEncodedImage(ImageRequest paramImageRequest)
    throws IOException
  {
    return getEncodedImage(mResources.openRawResource(getResourceId(paramImageRequest)), getLength(paramImageRequest));
  }
  
  protected String getProducerName()
  {
    return "LocalResourceFetchProducer";
  }
}
