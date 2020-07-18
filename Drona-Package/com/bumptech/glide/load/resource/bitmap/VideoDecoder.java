package com.bumptech.glide.load.resource.bitmap;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build.VERSION;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import androidx.annotation.VisibleForTesting;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Option.CacheKeyUpdater;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class VideoDecoder<T>
  implements ResourceDecoder<T, Bitmap>
{
  private static final MediaMetadataRetrieverFactory DEFAULT_FACTORY = new MediaMetadataRetrieverFactory();
  public static final long DEFAULT_FRAME = -1L;
  @VisibleForTesting
  static final int DEFAULT_FRAME_OPTION = 2;
  public static final Option<Integer> FRAME_OPTION;
  private static final String PAGE_KEY = "VideoDecoder";
  public static final Option<Long> TARGET_FRAME = Option.disk("com.bumptech.glide.load.resource.bitmap.VideoBitmapDecode.TargetFrame", Long.valueOf(-1L), new Option.CacheKeyUpdater()
  {
    private final ByteBuffer buffer = ByteBuffer.allocate(8);
    
    public void update(byte[] paramAnonymousArrayOfByte, Long paramAnonymousLong, MessageDigest paramAnonymousMessageDigest)
    {
      paramAnonymousMessageDigest.update(paramAnonymousArrayOfByte);
      paramAnonymousArrayOfByte = buffer;
      try
      {
        buffer.position(0);
        paramAnonymousMessageDigest.update(buffer.putLong(paramAnonymousLong.longValue()).array());
        return;
      }
      catch (Throwable paramAnonymousLong)
      {
        throw paramAnonymousLong;
      }
    }
  });
  private final BitmapPool bitmapPool;
  private final MediaMetadataRetrieverFactory factory;
  private final MediaMetadataRetrieverInitializer<T> initializer;
  
  static
  {
    FRAME_OPTION = Option.disk("com.bumptech.glide.load.resource.bitmap.VideoBitmapDecode.FrameOption", Integer.valueOf(2), new Option.CacheKeyUpdater()
    {
      private final ByteBuffer buffer = ByteBuffer.allocate(4);
      
      public void update(byte[] paramAnonymousArrayOfByte, Integer paramAnonymousInteger, MessageDigest paramAnonymousMessageDigest)
      {
        if (paramAnonymousInteger == null) {
          return;
        }
        paramAnonymousMessageDigest.update(paramAnonymousArrayOfByte);
        paramAnonymousArrayOfByte = buffer;
        try
        {
          buffer.position(0);
          paramAnonymousMessageDigest.update(buffer.putInt(paramAnonymousInteger.intValue()).array());
          return;
        }
        catch (Throwable paramAnonymousInteger)
        {
          throw paramAnonymousInteger;
        }
      }
    });
  }
  
  VideoDecoder(BitmapPool paramBitmapPool, MediaMetadataRetrieverInitializer paramMediaMetadataRetrieverInitializer)
  {
    this(paramBitmapPool, paramMediaMetadataRetrieverInitializer, DEFAULT_FACTORY);
  }
  
  VideoDecoder(BitmapPool paramBitmapPool, MediaMetadataRetrieverInitializer paramMediaMetadataRetrieverInitializer, MediaMetadataRetrieverFactory paramMediaMetadataRetrieverFactory)
  {
    bitmapPool = paramBitmapPool;
    initializer = paramMediaMetadataRetrieverInitializer;
    factory = paramMediaMetadataRetrieverFactory;
  }
  
  public static ResourceDecoder asset(BitmapPool paramBitmapPool)
  {
    return new VideoDecoder(paramBitmapPool, new AssetFileDescriptorInitializer(null));
  }
  
  private static Bitmap decodeFrame(MediaMetadataRetriever paramMediaMetadataRetriever, long paramLong, int paramInt1, int paramInt2, int paramInt3, DownsampleStrategy paramDownsampleStrategy)
  {
    if ((Build.VERSION.SDK_INT >= 27) && (paramInt2 != Integer.MIN_VALUE) && (paramInt3 != Integer.MIN_VALUE) && (paramDownsampleStrategy != DownsampleStrategy.NONE)) {
      paramDownsampleStrategy = decodeScaledFrame(paramMediaMetadataRetriever, paramLong, paramInt1, paramInt2, paramInt3, paramDownsampleStrategy);
    } else {
      paramDownsampleStrategy = null;
    }
    Object localObject = paramDownsampleStrategy;
    if (paramDownsampleStrategy == null) {
      localObject = decodeOriginalFrame(paramMediaMetadataRetriever, paramLong, paramInt1);
    }
    return localObject;
  }
  
  private static Bitmap decodeOriginalFrame(MediaMetadataRetriever paramMediaMetadataRetriever, long paramLong, int paramInt)
  {
    return paramMediaMetadataRetriever.getFrameAtTime(paramLong, paramInt);
  }
  
  private static Bitmap decodeScaledFrame(MediaMetadataRetriever paramMediaMetadataRetriever, long paramLong, int paramInt1, int paramInt2, int paramInt3, DownsampleStrategy paramDownsampleStrategy)
  {
    try
    {
      int j = Integer.parseInt(paramMediaMetadataRetriever.extractMetadata(18));
      int m = j;
      int i = Integer.parseInt(paramMediaMetadataRetriever.extractMetadata(19));
      int k = i;
      int n = Integer.parseInt(paramMediaMetadataRetriever.extractMetadata(24));
      if ((n == 90) || (n == 270))
      {
        k = j;
        m = i;
      }
      float f1 = paramDownsampleStrategy.getScaleFactor(m, k, paramInt2, paramInt3);
      float f2 = m;
      paramInt2 = Math.round(f2 * f1);
      f2 = k;
      paramMediaMetadataRetriever = paramMediaMetadataRetriever.getScaledFrameAtTime(paramLong, paramInt1, paramInt2, Math.round(f1 * f2));
      return paramMediaMetadataRetriever;
    }
    catch (Throwable paramMediaMetadataRetriever)
    {
      if (Log.isLoggable("VideoDecoder", 3)) {
        Log.d("VideoDecoder", "Exception trying to decode frame on oreo+", paramMediaMetadataRetriever);
      }
    }
    return null;
  }
  
  public static ResourceDecoder parcel(BitmapPool paramBitmapPool)
  {
    return new VideoDecoder(paramBitmapPool, new ParcelFileDescriptorInitializer());
  }
  
  public Resource decode(Object paramObject, int paramInt1, int paramInt2, Options paramOptions)
    throws IOException
  {
    long l = ((Long)paramOptions.getOption(TARGET_FRAME)).longValue();
    if ((l < 0L) && (l != -1L))
    {
      paramObject = new StringBuilder();
      paramObject.append("Requested frame must be non-negative, or DEFAULT_FRAME, given: ");
      paramObject.append(l);
      throw new IllegalArgumentException(paramObject.toString());
    }
    Object localObject2 = (Integer)paramOptions.getOption(FRAME_OPTION);
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = Integer.valueOf(2);
    }
    localObject2 = (DownsampleStrategy)paramOptions.getOption(DownsampleStrategy.OPTION);
    paramOptions = (Options)localObject2;
    if (localObject2 == null) {
      paramOptions = DownsampleStrategy.DEFAULT;
    }
    localObject2 = factory.build();
    try
    {
      initializer.initialize((MediaMetadataRetriever)localObject2, paramObject);
      paramObject = decodeFrame((MediaMetadataRetriever)localObject2, l, ((Integer)localObject1).intValue(), paramInt1, paramInt2, paramOptions);
      ((MediaMetadataRetriever)localObject2).release();
      return BitmapResource.obtain(paramObject, bitmapPool);
    }
    catch (Throwable paramObject) {}catch (RuntimeException paramObject)
    {
      throw new IOException(paramObject);
    }
    ((MediaMetadataRetriever)localObject2).release();
    throw paramObject;
  }
  
  public boolean handles(Object paramObject, Options paramOptions)
  {
    return true;
  }
  
  private static final class AssetFileDescriptorInitializer
    implements VideoDecoder.MediaMetadataRetrieverInitializer<AssetFileDescriptor>
  {
    private AssetFileDescriptorInitializer() {}
    
    public void initialize(MediaMetadataRetriever paramMediaMetadataRetriever, AssetFileDescriptor paramAssetFileDescriptor)
    {
      paramMediaMetadataRetriever.setDataSource(paramAssetFileDescriptor.getFileDescriptor(), paramAssetFileDescriptor.getStartOffset(), paramAssetFileDescriptor.getLength());
    }
  }
  
  @VisibleForTesting
  static class MediaMetadataRetrieverFactory
  {
    MediaMetadataRetrieverFactory() {}
    
    public MediaMetadataRetriever build()
    {
      return new MediaMetadataRetriever();
    }
  }
  
  @VisibleForTesting
  static abstract interface MediaMetadataRetrieverInitializer<T>
  {
    public abstract void initialize(MediaMetadataRetriever paramMediaMetadataRetriever, Object paramObject);
  }
  
  static final class ParcelFileDescriptorInitializer
    implements VideoDecoder.MediaMetadataRetrieverInitializer<ParcelFileDescriptor>
  {
    ParcelFileDescriptorInitializer() {}
    
    public void initialize(MediaMetadataRetriever paramMediaMetadataRetriever, ParcelFileDescriptor paramParcelFileDescriptor)
    {
      paramMediaMetadataRetriever.setDataSource(paramParcelFileDescriptor.getFileDescriptor());
    }
  }
}
