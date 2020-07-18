package com.facebook.imagepipeline.producers;

import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.cache.MemoryCache;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequest.RequestLevel;
import com.facebook.imagepipeline.systrace.FrescoSystrace;

public class EncodedMemoryCacheProducer
  implements Producer<EncodedImage>
{
  public static final String EXTRA_CACHED_VALUE_FOUND = "cached_value_found";
  public static final String PRODUCER_NAME = "EncodedMemoryCacheProducer";
  private final CacheKeyFactory mCacheKeyFactory;
  private final Producer<EncodedImage> mInputProducer;
  private final MemoryCache<CacheKey, PooledByteBuffer> mMemoryCache;
  
  public EncodedMemoryCacheProducer(MemoryCache paramMemoryCache, CacheKeyFactory paramCacheKeyFactory, Producer paramProducer)
  {
    mMemoryCache = paramMemoryCache;
    mCacheKeyFactory = paramCacheKeyFactory;
    mInputProducer = paramProducer;
  }
  
  public void produceResults(Consumer paramConsumer, ProducerContext paramProducerContext)
  {
    try
    {
      boolean bool = FrescoSystrace.isTracing();
      if (bool) {
        FrescoSystrace.beginSection("EncodedMemoryCacheProducer#produceResults");
      }
      String str = paramProducerContext.getId();
      ProducerListener localProducerListener = paramProducerContext.getListener();
      localProducerListener.onProducerStart(str, "EncodedMemoryCacheProducer");
      Object localObject = paramProducerContext.getImageRequest();
      CacheKey localCacheKey = mCacheKeyFactory.getEncodedCacheKey((ImageRequest)localObject, paramProducerContext.getCallerContext());
      localCloseableReference = mMemoryCache.cache(localCacheKey);
      EncodedImage localEncodedImage = null;
      localObject = null;
      if (localCloseableReference != null) {
        try
        {
          localEncodedImage = new EncodedImage(localCloseableReference);
          try
          {
            bool = localProducerListener.requiresExtraMap(str);
            paramProducerContext = (ProducerContext)localObject;
            if (bool) {
              paramProducerContext = ImmutableMap.of("cached_value_found", "true");
            }
            localProducerListener.onProducerFinishWithSuccess(str, "EncodedMemoryCacheProducer", paramProducerContext);
            localProducerListener.onUltimateProducerReached(str, "EncodedMemoryCacheProducer", true);
            paramConsumer.onProgressUpdate(1.0F);
            paramConsumer.onNewResult(localEncodedImage, 1);
            EncodedImage.closeSafely(localEncodedImage);
            CloseableReference.closeSafely(localCloseableReference);
            if (!FrescoSystrace.isTracing()) {
              return;
            }
            FrescoSystrace.endSection();
            return;
          }
          catch (Throwable paramConsumer)
          {
            EncodedImage.closeSafely(localEncodedImage);
            throw paramConsumer;
          }
          i = paramProducerContext.getLowestPermittedRequestLevel().getValue();
        }
        catch (Throwable paramConsumer) {}
      }
      int i;
      int j = ImageRequest.RequestLevel.ENCODED_MEMORY_CACHE.getValue();
      if (i >= j)
      {
        bool = localProducerListener.requiresExtraMap(str);
        if (bool) {
          paramProducerContext = ImmutableMap.of("cached_value_found", "false");
        } else {
          paramProducerContext = null;
        }
        localProducerListener.onProducerFinishWithSuccess(str, "EncodedMemoryCacheProducer", paramProducerContext);
        localProducerListener.onUltimateProducerReached(str, "EncodedMemoryCacheProducer", false);
        paramConsumer.onNewResult(null, 1);
        CloseableReference.closeSafely(localCloseableReference);
        if (!FrescoSystrace.isTracing()) {
          return;
        }
        FrescoSystrace.endSection();
        return;
      }
      bool = paramProducerContext.getImageRequest().isMemoryCacheEnabled();
      localObject = new EncodedMemoryCacheConsumer(paramConsumer, mMemoryCache, localCacheKey, bool);
      bool = localProducerListener.requiresExtraMap(str);
      paramConsumer = localEncodedImage;
      if (bool) {
        paramConsumer = ImmutableMap.of("cached_value_found", "false");
      }
      localProducerListener.onProducerFinishWithSuccess(str, "EncodedMemoryCacheProducer", paramConsumer);
      mInputProducer.produceResults((Consumer)localObject, paramProducerContext);
      CloseableReference.closeSafely(localCloseableReference);
      if (!FrescoSystrace.isTracing()) {
        return;
      }
      FrescoSystrace.endSection();
      return;
    }
    catch (Throwable paramConsumer)
    {
      CloseableReference localCloseableReference;
      if (!FrescoSystrace.isTracing()) {
        break label415;
      }
      FrescoSystrace.endSection();
      label415:
      throw paramConsumer;
    }
    CloseableReference.closeSafely(localCloseableReference);
    throw paramConsumer;
  }
  
  private static class EncodedMemoryCacheConsumer
    extends DelegatingConsumer<EncodedImage, EncodedImage>
  {
    private final boolean mIsMemoryCacheEnabled;
    private final MemoryCache<CacheKey, PooledByteBuffer> mMemoryCache;
    private final CacheKey mRequestedCacheKey;
    
    public EncodedMemoryCacheConsumer(Consumer paramConsumer, MemoryCache paramMemoryCache, CacheKey paramCacheKey, boolean paramBoolean)
    {
      super();
      mMemoryCache = paramMemoryCache;
      mRequestedCacheKey = paramCacheKey;
      mIsMemoryCacheEnabled = paramBoolean;
    }
    
    /* Error */
    public void onNewResultImpl(EncodedImage paramEncodedImage, int paramInt)
    {
      // Byte code:
      //   0: invokestatic 37	com/facebook/imagepipeline/systrace/FrescoSystrace:isTracing	()Z
      //   3: istore_3
      //   4: iload_3
      //   5: ifeq +8 -> 13
      //   8: ldc 39
      //   10: invokestatic 43	com/facebook/imagepipeline/systrace/FrescoSystrace:beginSection	(Ljava/lang/String;)V
      //   13: iload_2
      //   14: invokestatic 49	com/facebook/imagepipeline/producers/BaseConsumer:isNotLast	(I)Z
      //   17: istore_3
      //   18: iload_3
      //   19: ifne +193 -> 212
      //   22: aload_1
      //   23: ifnull +189 -> 212
      //   26: iload_2
      //   27: bipush 10
      //   29: invokestatic 53	com/facebook/imagepipeline/producers/BaseConsumer:statusHasAnyFlag	(II)Z
      //   32: istore_3
      //   33: iload_3
      //   34: ifne +178 -> 212
      //   37: aload_1
      //   38: invokevirtual 59	com/facebook/imagepipeline/image/EncodedImage:getImageFormat	()Lcom/facebook/imageformat/ImageFormat;
      //   41: astore 4
      //   43: getstatic 65	com/facebook/imageformat/ImageFormat:UNKNOWN	Lcom/facebook/imageformat/ImageFormat;
      //   46: astore 5
      //   48: aload 4
      //   50: aload 5
      //   52: if_acmpne +6 -> 58
      //   55: goto +157 -> 212
      //   58: aload_1
      //   59: invokevirtual 69	com/facebook/imagepipeline/image/EncodedImage:getByteBufferRef	()Lcom/facebook/common/references/CloseableReference;
      //   62: astore 5
      //   64: aload 5
      //   66: ifnull +125 -> 191
      //   69: aconst_null
      //   70: astore 4
      //   72: aload_0
      //   73: getfield 26	com/facebook/imagepipeline/producers/EncodedMemoryCacheProducer$EncodedMemoryCacheConsumer:mIsMemoryCacheEnabled	Z
      //   76: istore_3
      //   77: iload_3
      //   78: ifeq +20 -> 98
      //   81: aload_0
      //   82: getfield 22	com/facebook/imagepipeline/producers/EncodedMemoryCacheProducer$EncodedMemoryCacheConsumer:mMemoryCache	Lcom/facebook/imagepipeline/cache/MemoryCache;
      //   85: aload_0
      //   86: getfield 24	com/facebook/imagepipeline/producers/EncodedMemoryCacheProducer$EncodedMemoryCacheConsumer:mRequestedCacheKey	Lcom/facebook/cache/common/CacheKey;
      //   89: aload 5
      //   91: invokeinterface 75 3 0
      //   96: astore 4
      //   98: aload 5
      //   100: invokestatic 81	com/facebook/common/references/CloseableReference:closeSafely	(Lcom/facebook/common/references/CloseableReference;)V
      //   103: aload 4
      //   105: ifnull +86 -> 191
      //   108: new 55	com/facebook/imagepipeline/image/EncodedImage
      //   111: dup
      //   112: aload 4
      //   114: invokespecial 83	com/facebook/imagepipeline/image/EncodedImage:<init>	(Lcom/facebook/common/references/CloseableReference;)V
      //   117: astore 5
      //   119: aload 5
      //   121: aload_1
      //   122: invokevirtual 87	com/facebook/imagepipeline/image/EncodedImage:copyMetaDataFrom	(Lcom/facebook/imagepipeline/image/EncodedImage;)V
      //   125: aload 4
      //   127: invokestatic 81	com/facebook/common/references/CloseableReference:closeSafely	(Lcom/facebook/common/references/CloseableReference;)V
      //   130: aload_0
      //   131: invokevirtual 91	com/facebook/imagepipeline/producers/DelegatingConsumer:getConsumer	()Lcom/facebook/imagepipeline/producers/Consumer;
      //   134: fconst_1
      //   135: invokeinterface 97 2 0
      //   140: aload_0
      //   141: invokevirtual 91	com/facebook/imagepipeline/producers/DelegatingConsumer:getConsumer	()Lcom/facebook/imagepipeline/producers/Consumer;
      //   144: aload 5
      //   146: iload_2
      //   147: invokeinterface 101 3 0
      //   152: aload 5
      //   154: invokestatic 103	com/facebook/imagepipeline/image/EncodedImage:closeSafely	(Lcom/facebook/imagepipeline/image/EncodedImage;)V
      //   157: invokestatic 37	com/facebook/imagepipeline/systrace/FrescoSystrace:isTracing	()Z
      //   160: ifeq +85 -> 245
      //   163: invokestatic 107	com/facebook/imagepipeline/systrace/FrescoSystrace:endSection	()V
      //   166: return
      //   167: astore_1
      //   168: aload 5
      //   170: invokestatic 103	com/facebook/imagepipeline/image/EncodedImage:closeSafely	(Lcom/facebook/imagepipeline/image/EncodedImage;)V
      //   173: aload_1
      //   174: athrow
      //   175: astore_1
      //   176: aload 4
      //   178: invokestatic 81	com/facebook/common/references/CloseableReference:closeSafely	(Lcom/facebook/common/references/CloseableReference;)V
      //   181: aload_1
      //   182: athrow
      //   183: astore_1
      //   184: aload 5
      //   186: invokestatic 81	com/facebook/common/references/CloseableReference:closeSafely	(Lcom/facebook/common/references/CloseableReference;)V
      //   189: aload_1
      //   190: athrow
      //   191: aload_0
      //   192: invokevirtual 91	com/facebook/imagepipeline/producers/DelegatingConsumer:getConsumer	()Lcom/facebook/imagepipeline/producers/Consumer;
      //   195: aload_1
      //   196: iload_2
      //   197: invokeinterface 101 3 0
      //   202: invokestatic 37	com/facebook/imagepipeline/systrace/FrescoSystrace:isTracing	()Z
      //   205: ifeq +40 -> 245
      //   208: invokestatic 107	com/facebook/imagepipeline/systrace/FrescoSystrace:endSection	()V
      //   211: return
      //   212: aload_0
      //   213: invokevirtual 91	com/facebook/imagepipeline/producers/DelegatingConsumer:getConsumer	()Lcom/facebook/imagepipeline/producers/Consumer;
      //   216: aload_1
      //   217: iload_2
      //   218: invokeinterface 101 3 0
      //   223: invokestatic 37	com/facebook/imagepipeline/systrace/FrescoSystrace:isTracing	()Z
      //   226: ifeq +19 -> 245
      //   229: invokestatic 107	com/facebook/imagepipeline/systrace/FrescoSystrace:endSection	()V
      //   232: return
      //   233: astore_1
      //   234: invokestatic 37	com/facebook/imagepipeline/systrace/FrescoSystrace:isTracing	()Z
      //   237: ifeq +6 -> 243
      //   240: invokestatic 107	com/facebook/imagepipeline/systrace/FrescoSystrace:endSection	()V
      //   243: aload_1
      //   244: athrow
      //   245: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	246	0	this	EncodedMemoryCacheConsumer
      //   0	246	1	paramEncodedImage	EncodedImage
      //   0	246	2	paramInt	int
      //   3	75	3	bool	boolean
      //   41	136	4	localObject1	Object
      //   46	139	5	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   130	152	167	java/lang/Throwable
      //   108	125	175	java/lang/Throwable
      //   72	77	183	java/lang/Throwable
      //   81	98	183	java/lang/Throwable
      //   0	4	233	java/lang/Throwable
      //   8	13	233	java/lang/Throwable
      //   13	18	233	java/lang/Throwable
      //   26	33	233	java/lang/Throwable
      //   37	48	233	java/lang/Throwable
      //   58	64	233	java/lang/Throwable
      //   98	103	233	java/lang/Throwable
      //   125	130	233	java/lang/Throwable
      //   152	157	233	java/lang/Throwable
      //   168	175	233	java/lang/Throwable
      //   176	183	233	java/lang/Throwable
      //   184	191	233	java/lang/Throwable
      //   191	202	233	java/lang/Throwable
      //   212	223	233	java/lang/Throwable
    }
  }
}
