package com.facebook.imagepipeline.core;

import android.content.ContentResolver;
import android.net.Uri;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.common.media.MediaUtils;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.webp.WebpSupportStatus;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.producers.DataFetchProducer;
import com.facebook.imagepipeline.producers.NetworkFetcher;
import com.facebook.imagepipeline.producers.Producer;
import com.facebook.imagepipeline.producers.RemoveImageTransformMetaDataProducer;
import com.facebook.imagepipeline.producers.SwallowResultProducer;
import com.facebook.imagepipeline.producers.ThreadHandoffProducerQueue;
import com.facebook.imagepipeline.producers.ThumbnailProducer;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequest.RequestLevel;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import com.facebook.imagepipeline.transcoder.ImageTranscoderFactory;
import java.util.HashMap;
import java.util.Map;

public class ProducerSequenceFactory
{
  @VisibleForTesting
  Producer<EncodedImage> mBackgroundLocalContentUriFetchToEncodedMemorySequence;
  @VisibleForTesting
  Producer<EncodedImage> mBackgroundLocalFileFetchToEncodedMemorySequence;
  @VisibleForTesting
  Producer<EncodedImage> mBackgroundNetworkFetchToEncodedMemorySequence;
  @VisibleForTesting
  Map<Producer<CloseableReference<CloseableImage>>, Producer<CloseableReference<CloseableImage>>> mBitmapPrepareSequences;
  @VisibleForTesting
  Map<Producer<CloseableReference<CloseableImage>>, Producer<Void>> mCloseableImagePrefetchSequences;
  private Producer<EncodedImage> mCommonNetworkFetchToEncodedMemorySequence;
  private final ContentResolver mContentResolver;
  @VisibleForTesting
  Producer<CloseableReference<CloseableImage>> mDataFetchSequence;
  private final boolean mDiskCacheEnabled;
  private final boolean mDownsampleEnabled;
  private final ImageTranscoderFactory mImageTranscoderFactory;
  @VisibleForTesting
  Producer<CloseableReference<CloseableImage>> mLocalAssetFetchSequence;
  @VisibleForTesting
  Producer<CloseableReference<PooledByteBuffer>> mLocalContentUriEncodedImageProducerSequence;
  @VisibleForTesting
  Producer<CloseableReference<CloseableImage>> mLocalContentUriFetchSequence;
  @VisibleForTesting
  Producer<CloseableReference<PooledByteBuffer>> mLocalFileEncodedImageProducerSequence;
  @VisibleForTesting
  Producer<Void> mLocalFileFetchToEncodedMemoryPrefetchSequence;
  @VisibleForTesting
  Producer<CloseableReference<CloseableImage>> mLocalImageFileFetchSequence;
  @VisibleForTesting
  Producer<CloseableReference<CloseableImage>> mLocalResourceFetchSequence;
  @VisibleForTesting
  Producer<CloseableReference<CloseableImage>> mLocalVideoFileFetchSequence;
  @VisibleForTesting
  Producer<CloseableReference<PooledByteBuffer>> mNetworkEncodedImageProducerSequence;
  @VisibleForTesting
  Producer<CloseableReference<CloseableImage>> mNetworkFetchSequence;
  @VisibleForTesting
  Producer<Void> mNetworkFetchToEncodedMemoryPrefetchSequence;
  private final NetworkFetcher mNetworkFetcher;
  private final boolean mPartialImageCachingEnabled;
  @VisibleForTesting
  Map<Producer<CloseableReference<CloseableImage>>, Producer<CloseableReference<CloseableImage>>> mPostprocessorSequences;
  private final ProducerFactory mProducerFactory;
  @VisibleForTesting
  Producer<CloseableReference<CloseableImage>> mQualifiedResourceFetchSequence;
  private final boolean mResizeAndRotateEnabledForNetwork;
  private final ThreadHandoffProducerQueue mThreadHandoffProducerQueue;
  private final boolean mUseBitmapPrepareToDraw;
  private final boolean mWebpSupportEnabled;
  
  public ProducerSequenceFactory(ContentResolver paramContentResolver, ProducerFactory paramProducerFactory, NetworkFetcher paramNetworkFetcher, boolean paramBoolean1, boolean paramBoolean2, ThreadHandoffProducerQueue paramThreadHandoffProducerQueue, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6, ImageTranscoderFactory paramImageTranscoderFactory)
  {
    mContentResolver = paramContentResolver;
    mProducerFactory = paramProducerFactory;
    mNetworkFetcher = paramNetworkFetcher;
    mResizeAndRotateEnabledForNetwork = paramBoolean1;
    mWebpSupportEnabled = paramBoolean2;
    mPostprocessorSequences = new HashMap();
    mCloseableImagePrefetchSequences = new HashMap();
    mBitmapPrepareSequences = new HashMap();
    mThreadHandoffProducerQueue = paramThreadHandoffProducerQueue;
    mDownsampleEnabled = paramBoolean3;
    mUseBitmapPrepareToDraw = paramBoolean4;
    mPartialImageCachingEnabled = paramBoolean5;
    mDiskCacheEnabled = paramBoolean6;
    mImageTranscoderFactory = paramImageTranscoderFactory;
  }
  
  private Producer getBackgroundLocalContentUriFetchToEncodeMemorySequence()
  {
    try
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.beginSection("ProducerSequenceFactory#getBackgroundLocalContentUriFetchToEncodeMemorySequence");
      }
      if (mBackgroundLocalContentUriFetchToEncodedMemorySequence == null)
      {
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.beginSection("ProducerSequenceFactory#getBackgroundLocalContentUriFetchToEncodeMemorySequence:init");
        }
        localProducer = newEncodedCacheMultiplexToTranscodeSequence(mProducerFactory.newLocalContentUriFetchProducer());
        mBackgroundLocalContentUriFetchToEncodedMemorySequence = mProducerFactory.newBackgroundThreadHandoffProducer(localProducer, mThreadHandoffProducerQueue);
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.endSection();
        }
      }
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      Producer localProducer = mBackgroundLocalContentUriFetchToEncodedMemorySequence;
      return localProducer;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private Producer getBackgroundLocalFileFetchToEncodeMemorySequence()
  {
    try
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.beginSection("ProducerSequenceFactory#getBackgroundLocalFileFetchToEncodeMemorySequence");
      }
      if (mBackgroundLocalFileFetchToEncodedMemorySequence == null)
      {
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.beginSection("ProducerSequenceFactory#getBackgroundLocalFileFetchToEncodeMemorySequence:init");
        }
        localProducer = newEncodedCacheMultiplexToTranscodeSequence(mProducerFactory.newLocalFileFetchProducer());
        mBackgroundLocalFileFetchToEncodedMemorySequence = mProducerFactory.newBackgroundThreadHandoffProducer(localProducer, mThreadHandoffProducerQueue);
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.endSection();
        }
      }
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      Producer localProducer = mBackgroundLocalFileFetchToEncodedMemorySequence;
      return localProducer;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private Producer getBackgroundNetworkFetchToEncodedMemorySequence()
  {
    try
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.beginSection("ProducerSequenceFactory#getBackgroundNetworkFetchToEncodedMemorySequence");
      }
      if (mBackgroundNetworkFetchToEncodedMemorySequence == null)
      {
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.beginSection("ProducerSequenceFactory#getBackgroundNetworkFetchToEncodedMemorySequence:init");
        }
        mBackgroundNetworkFetchToEncodedMemorySequence = mProducerFactory.newBackgroundThreadHandoffProducer(getCommonNetworkFetchToEncodedMemorySequence(), mThreadHandoffProducerQueue);
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.endSection();
        }
      }
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      Producer localProducer = mBackgroundNetworkFetchToEncodedMemorySequence;
      return localProducer;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private Producer getBasicDecodedImageSequence(ImageRequest paramImageRequest)
  {
    try
    {
      boolean bool = FrescoSystrace.isTracing();
      if (bool) {
        FrescoSystrace.beginSection("ProducerSequenceFactory#getBasicDecodedImageSequence");
      }
      Preconditions.checkNotNull(paramImageRequest);
      Object localObject = paramImageRequest.getSourceUri();
      Preconditions.checkNotNull(localObject, "Uri is null.");
      int i = paramImageRequest.getSourceUriType();
      if (i != 0)
      {
        switch (i)
        {
        default: 
          paramImageRequest = new StringBuilder();
          paramImageRequest.append("Unsupported uri scheme! Uri is: ");
          paramImageRequest.append(getShortenedUriString((Uri)localObject));
          throw new IllegalArgumentException(paramImageRequest.toString());
        case 8: 
          localObject = getQualifiedResourceFetchSequence();
          paramImageRequest = (ImageRequest)localObject;
          if (!FrescoSystrace.isTracing()) {
            break;
          }
          FrescoSystrace.endSection();
          return localObject;
        case 7: 
          localObject = getDataFetchSequence();
          paramImageRequest = (ImageRequest)localObject;
          if (!FrescoSystrace.isTracing()) {
            break;
          }
          FrescoSystrace.endSection();
          return localObject;
        case 6: 
          localObject = getLocalResourceFetchSequence();
          paramImageRequest = (ImageRequest)localObject;
          if (!FrescoSystrace.isTracing()) {
            break;
          }
          FrescoSystrace.endSection();
          return localObject;
        case 5: 
          localObject = getLocalAssetFetchSequence();
          paramImageRequest = (ImageRequest)localObject;
          if (!FrescoSystrace.isTracing()) {
            break;
          }
          FrescoSystrace.endSection();
          return localObject;
        case 4: 
          bool = MediaUtils.isVideo(mContentResolver.getType((Uri)localObject));
          if (bool)
          {
            localObject = getLocalVideoFileFetchSequence();
            paramImageRequest = (ImageRequest)localObject;
            if (!FrescoSystrace.isTracing()) {
              break;
            }
            FrescoSystrace.endSection();
            return localObject;
          }
          localObject = getLocalContentUriFetchSequence();
          paramImageRequest = (ImageRequest)localObject;
          if (!FrescoSystrace.isTracing()) {
            break;
          }
          FrescoSystrace.endSection();
          return localObject;
        case 3: 
          localObject = getLocalImageFileFetchSequence();
          paramImageRequest = (ImageRequest)localObject;
          if (!FrescoSystrace.isTracing()) {
            break;
          }
          FrescoSystrace.endSection();
          return localObject;
        case 2: 
          localObject = getLocalVideoFileFetchSequence();
          paramImageRequest = (ImageRequest)localObject;
          if (!FrescoSystrace.isTracing()) {
            break;
          }
          FrescoSystrace.endSection();
          return localObject;
        }
      }
      else
      {
        localObject = getNetworkFetchSequence();
        paramImageRequest = (ImageRequest)localObject;
        if (FrescoSystrace.isTracing())
        {
          FrescoSystrace.endSection();
          return localObject;
        }
      }
    }
    catch (Throwable paramImageRequest)
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      throw paramImageRequest;
    }
    return paramImageRequest;
  }
  
  private Producer getBitmapPrepareSequence(Producer paramProducer)
  {
    try
    {
      Object localObject2 = (Producer)mBitmapPrepareSequences.get(paramProducer);
      Object localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject2 = mProducerFactory.newBitmapPrepareProducer(paramProducer);
        localObject1 = localObject2;
        mBitmapPrepareSequences.put(paramProducer, localObject2);
      }
      return localObject1;
    }
    catch (Throwable paramProducer)
    {
      throw paramProducer;
    }
  }
  
  private Producer getCommonNetworkFetchToEncodedMemorySequence()
  {
    for (;;)
    {
      try
      {
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.beginSection("ProducerSequenceFactory#getCommonNetworkFetchToEncodedMemorySequence");
        }
        if (mCommonNetworkFetchToEncodedMemorySequence == null)
        {
          if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("ProducerSequenceFactory#getCommonNetworkFetchToEncodedMemorySequence:init");
          }
          mCommonNetworkFetchToEncodedMemorySequence = ProducerFactory.newAddImageTransformMetaDataProducer(newEncodedCacheMultiplexToTranscodeSequence(mProducerFactory.newNetworkFetchProducer(mNetworkFetcher)));
          localObject = mProducerFactory;
          Producer localProducer = mCommonNetworkFetchToEncodedMemorySequence;
          if ((!mResizeAndRotateEnabledForNetwork) || (mDownsampleEnabled)) {
            break label128;
          }
          bool = true;
          mCommonNetworkFetchToEncodedMemorySequence = ((ProducerFactory)localObject).newResizeAndRotateProducer(localProducer, bool, mImageTranscoderFactory);
          if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
          }
        }
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.endSection();
        }
        Object localObject = mCommonNetworkFetchToEncodedMemorySequence;
        return localObject;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      label128:
      boolean bool = false;
    }
  }
  
  private Producer getDataFetchSequence()
  {
    try
    {
      if (mDataFetchSequence == null)
      {
        DataFetchProducer localDataFetchProducer = mProducerFactory.newDataFetchProducer();
        localObject1 = localDataFetchProducer;
        Object localObject2 = localObject1;
        if (WebpSupportStatus.sIsWebpSupportRequired) {
          if (mWebpSupportEnabled)
          {
            localObject2 = localObject1;
            if (WebpSupportStatus.sWebpBitmapFactory != null) {}
          }
          else
          {
            localObject2 = mProducerFactory.newWebpTranscodeProducer(localDataFetchProducer);
          }
        }
        localObject1 = ProducerFactory.newAddImageTransformMetaDataProducer((Producer)localObject2);
        mDataFetchSequence = newBitmapCacheGetToDecodeSequence(mProducerFactory.newResizeAndRotateProducer((Producer)localObject1, true, mImageTranscoderFactory));
      }
      Object localObject1 = mDataFetchSequence;
      return localObject1;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private Producer getDecodedImagePrefetchSequence(Producer paramProducer)
  {
    try
    {
      if (!mCloseableImagePrefetchSequences.containsKey(paramProducer))
      {
        SwallowResultProducer localSwallowResultProducer = ProducerFactory.newSwallowResultProducer(paramProducer);
        mCloseableImagePrefetchSequences.put(paramProducer, localSwallowResultProducer);
      }
      paramProducer = (Producer)mCloseableImagePrefetchSequences.get(paramProducer);
      return paramProducer;
    }
    catch (Throwable paramProducer)
    {
      throw paramProducer;
    }
  }
  
  private Producer getLocalAssetFetchSequence()
  {
    try
    {
      if (mLocalAssetFetchSequence == null) {
        mLocalAssetFetchSequence = newBitmapCacheGetToLocalTransformSequence(mProducerFactory.newLocalAssetFetchProducer());
      }
      Producer localProducer = mLocalAssetFetchSequence;
      return localProducer;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private Producer getLocalContentUriFetchSequence()
  {
    try
    {
      if (mLocalContentUriFetchSequence == null) {
        mLocalContentUriFetchSequence = newBitmapCacheGetToLocalTransformSequence(mProducerFactory.newLocalContentUriFetchProducer(), new ThumbnailProducer[] { mProducerFactory.newLocalContentUriThumbnailFetchProducer(), mProducerFactory.newLocalExifThumbnailProducer() });
      }
      Producer localProducer = mLocalContentUriFetchSequence;
      return localProducer;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private Producer getLocalFileFetchToEncodedMemoryPrefetchSequence()
  {
    try
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.beginSection("ProducerSequenceFactory#getLocalFileFetchToEncodedMemoryPrefetchSequence");
      }
      if (mLocalFileFetchToEncodedMemoryPrefetchSequence == null)
      {
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.beginSection("ProducerSequenceFactory#getLocalFileFetchToEncodedMemoryPrefetchSequence:init");
        }
        mLocalFileFetchToEncodedMemoryPrefetchSequence = ProducerFactory.newSwallowResultProducer(getBackgroundLocalFileFetchToEncodeMemorySequence());
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.endSection();
        }
      }
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      Producer localProducer = mLocalFileFetchToEncodedMemoryPrefetchSequence;
      return localProducer;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private Producer getLocalImageFileFetchSequence()
  {
    try
    {
      if (mLocalImageFileFetchSequence == null) {
        mLocalImageFileFetchSequence = newBitmapCacheGetToLocalTransformSequence(mProducerFactory.newLocalFileFetchProducer());
      }
      Producer localProducer = mLocalImageFileFetchSequence;
      return localProducer;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private Producer getLocalResourceFetchSequence()
  {
    try
    {
      if (mLocalResourceFetchSequence == null) {
        mLocalResourceFetchSequence = newBitmapCacheGetToLocalTransformSequence(mProducerFactory.newLocalResourceFetchProducer());
      }
      Producer localProducer = mLocalResourceFetchSequence;
      return localProducer;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private Producer getLocalVideoFileFetchSequence()
  {
    try
    {
      if (mLocalVideoFileFetchSequence == null) {
        mLocalVideoFileFetchSequence = newBitmapCacheGetToBitmapCacheSequence(mProducerFactory.newLocalVideoThumbnailProducer());
      }
      Producer localProducer = mLocalVideoFileFetchSequence;
      return localProducer;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private Producer getNetworkFetchSequence()
  {
    try
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.beginSection("ProducerSequenceFactory#getNetworkFetchSequence");
      }
      if (mNetworkFetchSequence == null)
      {
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.beginSection("ProducerSequenceFactory#getNetworkFetchSequence:init");
        }
        mNetworkFetchSequence = newBitmapCacheGetToDecodeSequence(getCommonNetworkFetchToEncodedMemorySequence());
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.endSection();
        }
      }
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      Producer localProducer = mNetworkFetchSequence;
      return localProducer;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private Producer getNetworkFetchToEncodedMemoryPrefetchSequence()
  {
    try
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.beginSection("ProducerSequenceFactory#getNetworkFetchToEncodedMemoryPrefetchSequence");
      }
      if (mNetworkFetchToEncodedMemoryPrefetchSequence == null)
      {
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.beginSection("ProducerSequenceFactory#getNetworkFetchToEncodedMemoryPrefetchSequence:init");
        }
        mNetworkFetchToEncodedMemoryPrefetchSequence = ProducerFactory.newSwallowResultProducer(getBackgroundNetworkFetchToEncodedMemorySequence());
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.endSection();
        }
      }
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      Producer localProducer = mNetworkFetchToEncodedMemoryPrefetchSequence;
      return localProducer;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private Producer getPostprocessorSequence(Producer paramProducer)
  {
    try
    {
      if (!mPostprocessorSequences.containsKey(paramProducer))
      {
        Object localObject = mProducerFactory.newPostprocessorProducer(paramProducer);
        localObject = mProducerFactory.newPostprocessorBitmapMemoryCacheProducer((Producer)localObject);
        mPostprocessorSequences.put(paramProducer, localObject);
      }
      paramProducer = (Producer)mPostprocessorSequences.get(paramProducer);
      return paramProducer;
    }
    catch (Throwable paramProducer)
    {
      throw paramProducer;
    }
  }
  
  private Producer getQualifiedResourceFetchSequence()
  {
    try
    {
      if (mQualifiedResourceFetchSequence == null) {
        mQualifiedResourceFetchSequence = newBitmapCacheGetToLocalTransformSequence(mProducerFactory.newQualifiedResourceFetchProducer());
      }
      Producer localProducer = mQualifiedResourceFetchSequence;
      return localProducer;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private static String getShortenedUriString(Uri paramUri)
  {
    String str = String.valueOf(paramUri);
    paramUri = str;
    if (str.length() > 30)
    {
      paramUri = new StringBuilder();
      paramUri.append(str.substring(0, 30));
      paramUri.append("...");
      paramUri = paramUri.toString();
    }
    return paramUri;
  }
  
  private Producer newBitmapCacheGetToBitmapCacheSequence(Producer paramProducer)
  {
    paramProducer = mProducerFactory.newBitmapMemoryCacheProducer(paramProducer);
    paramProducer = mProducerFactory.newBitmapMemoryCacheKeyMultiplexProducer(paramProducer);
    paramProducer = mProducerFactory.newBackgroundThreadHandoffProducer(paramProducer, mThreadHandoffProducerQueue);
    return mProducerFactory.newBitmapMemoryCacheGetProducer(paramProducer);
  }
  
  private Producer newBitmapCacheGetToDecodeSequence(Producer paramProducer)
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("ProducerSequenceFactory#newBitmapCacheGetToDecodeSequence");
    }
    paramProducer = newBitmapCacheGetToBitmapCacheSequence(mProducerFactory.newDecodeProducer(paramProducer));
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
    return paramProducer;
  }
  
  private Producer newBitmapCacheGetToLocalTransformSequence(Producer paramProducer)
  {
    return newBitmapCacheGetToLocalTransformSequence(paramProducer, new ThumbnailProducer[] { mProducerFactory.newLocalExifThumbnailProducer() });
  }
  
  private Producer newBitmapCacheGetToLocalTransformSequence(Producer paramProducer, ThumbnailProducer[] paramArrayOfThumbnailProducer)
  {
    return newBitmapCacheGetToDecodeSequence(newLocalTransformationsSequence(newEncodedCacheMultiplexToTranscodeSequence(paramProducer), paramArrayOfThumbnailProducer));
  }
  
  private Producer newDiskCacheSequence(Producer paramProducer)
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("ProducerSequenceFactory#newDiskCacheSequence");
    }
    if (mPartialImageCachingEnabled)
    {
      paramProducer = mProducerFactory.newPartialDiskCacheProducer(paramProducer);
      paramProducer = mProducerFactory.newDiskCacheWriteProducer(paramProducer);
    }
    else
    {
      paramProducer = mProducerFactory.newDiskCacheWriteProducer(paramProducer);
    }
    paramProducer = mProducerFactory.newDiskCacheReadProducer(paramProducer);
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
    return paramProducer;
  }
  
  private Producer newEncodedCacheMultiplexToTranscodeSequence(Producer paramProducer)
  {
    Object localObject = paramProducer;
    if (WebpSupportStatus.sIsWebpSupportRequired) {
      if (mWebpSupportEnabled)
      {
        localObject = paramProducer;
        if (WebpSupportStatus.sWebpBitmapFactory != null) {}
      }
      else
      {
        localObject = mProducerFactory.newWebpTranscodeProducer(paramProducer);
      }
    }
    paramProducer = (Producer)localObject;
    if (mDiskCacheEnabled) {
      paramProducer = newDiskCacheSequence((Producer)localObject);
    }
    paramProducer = mProducerFactory.newEncodedMemoryCacheProducer(paramProducer);
    return mProducerFactory.newEncodedCacheKeyMultiplexProducer(paramProducer);
  }
  
  private Producer newLocalThumbnailProducer(ThumbnailProducer[] paramArrayOfThumbnailProducer)
  {
    paramArrayOfThumbnailProducer = mProducerFactory.newThumbnailBranchProducer(paramArrayOfThumbnailProducer);
    return mProducerFactory.newResizeAndRotateProducer(paramArrayOfThumbnailProducer, true, mImageTranscoderFactory);
  }
  
  private Producer newLocalTransformationsSequence(Producer paramProducer, ThumbnailProducer[] paramArrayOfThumbnailProducer)
  {
    paramProducer = ProducerFactory.newAddImageTransformMetaDataProducer(paramProducer);
    paramProducer = mProducerFactory.newResizeAndRotateProducer(paramProducer, true, mImageTranscoderFactory);
    paramProducer = mProducerFactory.newThrottlingProducer(paramProducer);
    return ProducerFactory.newBranchOnSeparateImagesProducer(newLocalThumbnailProducer(paramArrayOfThumbnailProducer), paramProducer);
  }
  
  private static void validateEncodedImageRequest(ImageRequest paramImageRequest)
  {
    Preconditions.checkNotNull(paramImageRequest);
    boolean bool;
    if (paramImageRequest.getLowestPermittedRequestLevel().getValue() <= ImageRequest.RequestLevel.ENCODED_MEMORY_CACHE.getValue()) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool);
  }
  
  public Producer getDecodedImagePrefetchProducerSequence(ImageRequest paramImageRequest)
  {
    Producer localProducer = getBasicDecodedImageSequence(paramImageRequest);
    paramImageRequest = localProducer;
    if (mUseBitmapPrepareToDraw) {
      paramImageRequest = getBitmapPrepareSequence(localProducer);
    }
    return getDecodedImagePrefetchSequence(paramImageRequest);
  }
  
  public Producer getDecodedImageProducerSequence(ImageRequest paramImageRequest)
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("ProducerSequenceFactory#getDecodedImageProducerSequence");
    }
    Producer localProducer2 = getBasicDecodedImageSequence(paramImageRequest);
    Producer localProducer1 = localProducer2;
    if (paramImageRequest.getPostprocessor() != null) {
      localProducer1 = getPostprocessorSequence(localProducer2);
    }
    paramImageRequest = localProducer1;
    if (mUseBitmapPrepareToDraw) {
      paramImageRequest = getBitmapPrepareSequence(localProducer1);
    }
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
    return paramImageRequest;
  }
  
  public Producer getEncodedImagePrefetchProducerSequence(ImageRequest paramImageRequest)
  {
    validateEncodedImageRequest(paramImageRequest);
    int i = paramImageRequest.getSourceUriType();
    if (i != 0)
    {
      switch (i)
      {
      default: 
        paramImageRequest = paramImageRequest.getSourceUri();
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Unsupported uri scheme for encoded image fetch! Uri is: ");
        localStringBuilder.append(getShortenedUriString(paramImageRequest));
        throw new IllegalArgumentException(localStringBuilder.toString());
      }
      return getLocalFileFetchToEncodedMemoryPrefetchSequence();
    }
    return getNetworkFetchToEncodedMemoryPrefetchSequence();
  }
  
  public Producer getEncodedImageProducerSequence(ImageRequest paramImageRequest)
  {
    try
    {
      boolean bool = FrescoSystrace.isTracing();
      if (bool) {
        FrescoSystrace.beginSection("ProducerSequenceFactory#getEncodedImageProducerSequence");
      }
      validateEncodedImageRequest(paramImageRequest);
      Object localObject = paramImageRequest.getSourceUri();
      int i = paramImageRequest.getSourceUriType();
      if (i != 0)
      {
        switch (i)
        {
        default: 
          paramImageRequest = new StringBuilder();
          paramImageRequest.append("Unsupported uri scheme for encoded image fetch! Uri is: ");
          paramImageRequest.append(getShortenedUriString((Uri)localObject));
          throw new IllegalArgumentException(paramImageRequest.toString());
        case 4: 
          localObject = getLocalContentUriFetchEncodedImageProducerSequence();
          paramImageRequest = (ImageRequest)localObject;
          if (!FrescoSystrace.isTracing()) {
            break;
          }
          FrescoSystrace.endSection();
          return localObject;
        case 2: 
        case 3: 
          localObject = getLocalFileFetchEncodedImageProducerSequence();
          paramImageRequest = (ImageRequest)localObject;
          if (!FrescoSystrace.isTracing()) {
            break;
          }
          FrescoSystrace.endSection();
          return localObject;
        }
      }
      else
      {
        localObject = getNetworkFetchEncodedImageProducerSequence();
        paramImageRequest = (ImageRequest)localObject;
        if (FrescoSystrace.isTracing())
        {
          FrescoSystrace.endSection();
          return localObject;
        }
      }
    }
    catch (Throwable paramImageRequest)
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      throw paramImageRequest;
    }
    return paramImageRequest;
  }
  
  public Producer getLocalContentUriFetchEncodedImageProducerSequence()
  {
    try
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.beginSection("ProducerSequenceFactory#getLocalContentUriFetchEncodedImageProducerSequence");
      }
      if (mLocalContentUriEncodedImageProducerSequence == null)
      {
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.beginSection("ProducerSequenceFactory#getLocalContentUriFetchEncodedImageProducerSequence:init");
        }
        mLocalContentUriEncodedImageProducerSequence = new RemoveImageTransformMetaDataProducer(getBackgroundLocalContentUriFetchToEncodeMemorySequence());
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.endSection();
        }
      }
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      return mLocalContentUriEncodedImageProducerSequence;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public Producer getLocalFileFetchEncodedImageProducerSequence()
  {
    try
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.beginSection("ProducerSequenceFactory#getLocalFileFetchEncodedImageProducerSequence");
      }
      if (mLocalFileEncodedImageProducerSequence == null)
      {
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.beginSection("ProducerSequenceFactory#getLocalFileFetchEncodedImageProducerSequence:init");
        }
        mLocalFileEncodedImageProducerSequence = new RemoveImageTransformMetaDataProducer(getBackgroundLocalFileFetchToEncodeMemorySequence());
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.endSection();
        }
      }
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      return mLocalFileEncodedImageProducerSequence;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public Producer getNetworkFetchEncodedImageProducerSequence()
  {
    try
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.beginSection("ProducerSequenceFactory#getNetworkFetchEncodedImageProducerSequence");
      }
      if (mNetworkEncodedImageProducerSequence == null)
      {
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.beginSection("ProducerSequenceFactory#getNetworkFetchEncodedImageProducerSequence:init");
        }
        mNetworkEncodedImageProducerSequence = new RemoveImageTransformMetaDataProducer(getBackgroundNetworkFetchToEncodedMemorySequence());
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.endSection();
        }
      }
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      return mNetworkEncodedImageProducerSequence;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
}
