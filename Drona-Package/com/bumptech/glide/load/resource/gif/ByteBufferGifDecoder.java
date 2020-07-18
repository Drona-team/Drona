package com.bumptech.glide.load.resource.gif;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.util.Log;
import androidx.annotation.VisibleForTesting;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.gifdecoder.GifDecoder.BitmapProvider;
import com.bumptech.glide.gifdecoder.GifHeader;
import com.bumptech.glide.gifdecoder.GifHeaderParser;
import com.bumptech.glide.gifdecoder.StandardGifDecoder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.ImageHeaderParser.ImageType;
import com.bumptech.glide.load.ImageHeaderParserUtils;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.UnitTransformation;
import com.bumptech.glide.util.LogTime;
import com.bumptech.glide.util.Util;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Queue;

public class ByteBufferGifDecoder
  implements ResourceDecoder<ByteBuffer, GifDrawable>
{
  private static final GifDecoderFactory GIF_DECODER_FACTORY = new GifDecoderFactory();
  private static final String PAGE_KEY = "BufferGifDecoder";
  private static final GifHeaderParserPool PARSER_POOL = new GifHeaderParserPool();
  private final Context context;
  private final GifDecoderFactory gifDecoderFactory;
  private final GifHeaderParserPool parserPool;
  private final List<ImageHeaderParser> parsers;
  private final GifBitmapProvider provider;
  
  public ByteBufferGifDecoder(Context paramContext)
  {
    this(paramContext, Glide.get(paramContext).getRegistry().getImageHeaderParsers(), Glide.get(paramContext).getBitmapPool(), Glide.get(paramContext).getArrayPool());
  }
  
  public ByteBufferGifDecoder(Context paramContext, List paramList, BitmapPool paramBitmapPool, ArrayPool paramArrayPool)
  {
    this(paramContext, paramList, paramBitmapPool, paramArrayPool, PARSER_POOL, GIF_DECODER_FACTORY);
  }
  
  ByteBufferGifDecoder(Context paramContext, List paramList, BitmapPool paramBitmapPool, ArrayPool paramArrayPool, GifHeaderParserPool paramGifHeaderParserPool, GifDecoderFactory paramGifDecoderFactory)
  {
    context = paramContext.getApplicationContext();
    parsers = paramList;
    gifDecoderFactory = paramGifDecoderFactory;
    provider = new GifBitmapProvider(paramBitmapPool, paramArrayPool);
    parserPool = paramGifHeaderParserPool;
  }
  
  private GifDrawableResource decode(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, GifHeaderParser paramGifHeaderParser, Options paramOptions)
  {
    long l = LogTime.getLogTime();
    try
    {
      GifHeader localGifHeader = paramGifHeaderParser.parseHeader();
      int i = localGifHeader.getNumFrames();
      if (i > 0)
      {
        i = localGifHeader.getStatus();
        if (i == 0)
        {
          paramGifHeaderParser = paramOptions.getOption(GifOptions.DECODE_FORMAT);
          paramOptions = DecodeFormat.PREFER_RGB_565;
          if (paramGifHeaderParser == paramOptions) {
            paramGifHeaderParser = Bitmap.Config.RGB_565;
          } else {
            paramGifHeaderParser = Bitmap.Config.ARGB_8888;
          }
          i = getSampleSize(localGifHeader, paramInt1, paramInt2);
          paramByteBuffer = gifDecoderFactory.build(provider, localGifHeader, paramByteBuffer, i);
          paramByteBuffer.setDefaultBitmapConfig(paramGifHeaderParser);
          paramByteBuffer.advance();
          paramGifHeaderParser = paramByteBuffer.getNextFrame();
          if (paramGifHeaderParser == null)
          {
            if (!Log.isLoggable("BufferGifDecoder", 2)) {
              break label351;
            }
            paramByteBuffer = new StringBuilder();
            paramByteBuffer.append("Decoded GIF from stream in ");
            paramByteBuffer.append(LogTime.getElapsedMillis(l));
            Log.v("BufferGifDecoder", paramByteBuffer.toString());
            return null;
          }
          paramOptions = UnitTransformation.get();
          paramByteBuffer = new GifDrawableResource(new GifDrawable(context, paramByteBuffer, paramOptions, paramInt1, paramInt2, paramGifHeaderParser));
          if (!Log.isLoggable("BufferGifDecoder", 2)) {
            break label353;
          }
          paramGifHeaderParser = new StringBuilder();
          paramGifHeaderParser.append("Decoded GIF from stream in ");
          paramGifHeaderParser.append(LogTime.getElapsedMillis(l));
          Log.v("BufferGifDecoder", paramGifHeaderParser.toString());
          return paramByteBuffer;
        }
      }
      if (!Log.isLoggable("BufferGifDecoder", 2)) {
        break label355;
      }
      paramByteBuffer = new StringBuilder();
      paramByteBuffer.append("Decoded GIF from stream in ");
      paramByteBuffer.append(LogTime.getElapsedMillis(l));
      Log.v("BufferGifDecoder", paramByteBuffer.toString());
      return null;
    }
    catch (Throwable paramByteBuffer)
    {
      if (Log.isLoggable("BufferGifDecoder", 2))
      {
        paramGifHeaderParser = new StringBuilder();
        paramGifHeaderParser.append("Decoded GIF from stream in ");
        paramGifHeaderParser.append(LogTime.getElapsedMillis(l));
        Log.v("BufferGifDecoder", paramGifHeaderParser.toString());
      }
      throw paramByteBuffer;
    }
    label351:
    return null;
    label353:
    return paramByteBuffer;
    label355:
    return null;
  }
  
  private static int getSampleSize(GifHeader paramGifHeader, int paramInt1, int paramInt2)
  {
    int i = Math.min(paramGifHeader.getHeight() / paramInt2, paramGifHeader.getWidth() / paramInt1);
    if (i == 0) {
      i = 0;
    } else {
      i = Integer.highestOneBit(i);
    }
    i = Math.max(1, i);
    if ((Log.isLoggable("BufferGifDecoder", 2)) && (i > 1))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Downsampling GIF, sampleSize: ");
      localStringBuilder.append(i);
      localStringBuilder.append(", target dimens: [");
      localStringBuilder.append(paramInt1);
      localStringBuilder.append("x");
      localStringBuilder.append(paramInt2);
      localStringBuilder.append("], actual dimens: [");
      localStringBuilder.append(paramGifHeader.getWidth());
      localStringBuilder.append("x");
      localStringBuilder.append(paramGifHeader.getHeight());
      localStringBuilder.append("]");
      Log.v("BufferGifDecoder", localStringBuilder.toString());
    }
    return i;
  }
  
  public GifDrawableResource decode(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, Options paramOptions)
  {
    GifHeaderParser localGifHeaderParser = parserPool.obtain(paramByteBuffer);
    try
    {
      paramByteBuffer = decode(paramByteBuffer, paramInt1, paramInt2, localGifHeaderParser, paramOptions);
      parserPool.release(localGifHeaderParser);
      return paramByteBuffer;
    }
    catch (Throwable paramByteBuffer)
    {
      parserPool.release(localGifHeaderParser);
      throw paramByteBuffer;
    }
  }
  
  public boolean handles(ByteBuffer paramByteBuffer, Options paramOptions)
    throws IOException
  {
    return (!((Boolean)paramOptions.getOption(GifOptions.DISABLE_ANIMATION)).booleanValue()) && (ImageHeaderParserUtils.getType(parsers, paramByteBuffer) == ImageHeaderParser.ImageType.GIF);
  }
  
  @VisibleForTesting
  static class GifDecoderFactory
  {
    GifDecoderFactory() {}
    
    GifDecoder build(GifDecoder.BitmapProvider paramBitmapProvider, GifHeader paramGifHeader, ByteBuffer paramByteBuffer, int paramInt)
    {
      return new StandardGifDecoder(paramBitmapProvider, paramGifHeader, paramByteBuffer, paramInt);
    }
  }
  
  @VisibleForTesting
  static class GifHeaderParserPool
  {
    private final Queue<GifHeaderParser> pool = Util.createQueue(0);
    
    GifHeaderParserPool() {}
    
    GifHeaderParser obtain(ByteBuffer paramByteBuffer)
    {
      try
      {
        GifHeaderParser localGifHeaderParser2 = (GifHeaderParser)pool.poll();
        GifHeaderParser localGifHeaderParser1 = localGifHeaderParser2;
        if (localGifHeaderParser2 == null) {
          localGifHeaderParser1 = new GifHeaderParser();
        }
        paramByteBuffer = localGifHeaderParser1.setData(paramByteBuffer);
        return paramByteBuffer;
      }
      catch (Throwable paramByteBuffer)
      {
        throw paramByteBuffer;
      }
    }
    
    void release(GifHeaderParser paramGifHeaderParser)
    {
      try
      {
        paramGifHeaderParser.clear();
        pool.offer(paramGifHeaderParser);
        return;
      }
      catch (Throwable paramGifHeaderParser)
      {
        throw paramGifHeaderParser;
      }
    }
  }
}
