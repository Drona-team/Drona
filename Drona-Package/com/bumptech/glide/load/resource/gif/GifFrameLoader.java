package com.bumptech.glide.load.resource.gif;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

class GifFrameLoader
{
  private final BitmapPool bitmapPool;
  private final List<FrameCallback> callbacks = new ArrayList();
  private DelayTarget current;
  private Bitmap firstFrame;
  private final GifDecoder gifDecoder;
  private final Handler handler;
  private boolean isCleared;
  private boolean isLoadPending;
  private boolean isRunning;
  private DelayTarget next;
  @Nullable
  private OnEveryFrameListener onEveryFrameListener;
  private DelayTarget pendingTarget;
  private RequestBuilder<Bitmap> requestBuilder;
  final RequestManager requestManager;
  private boolean startFromFirstFrame;
  private Transformation<Bitmap> transformation;
  
  GifFrameLoader(Glide paramGlide, GifDecoder paramGifDecoder, int paramInt1, int paramInt2, Transformation paramTransformation, Bitmap paramBitmap)
  {
    this(paramGlide.getBitmapPool(), Glide.with(paramGlide.getContext()), paramGifDecoder, null, getRequestBuilder(Glide.with(paramGlide.getContext()), paramInt1, paramInt2), paramTransformation, paramBitmap);
  }
  
  GifFrameLoader(BitmapPool paramBitmapPool, RequestManager paramRequestManager, GifDecoder paramGifDecoder, Handler paramHandler, RequestBuilder paramRequestBuilder, Transformation paramTransformation, Bitmap paramBitmap)
  {
    requestManager = paramRequestManager;
    paramRequestManager = paramHandler;
    if (paramHandler == null) {
      paramRequestManager = new Handler(Looper.getMainLooper(), new FrameLoaderCallback());
    }
    bitmapPool = paramBitmapPool;
    handler = paramRequestManager;
    requestBuilder = paramRequestBuilder;
    gifDecoder = paramGifDecoder;
    setFrameTransformation(paramTransformation, paramBitmap);
  }
  
  private static Key getFrameSignature()
  {
    return new ObjectKey(Double.valueOf(Math.random()));
  }
  
  private int getFrameSize()
  {
    return Util.getBitmapByteSize(getCurrentFrame().getWidth(), getCurrentFrame().getHeight(), getCurrentFrame().getConfig());
  }
  
  private static RequestBuilder getRequestBuilder(RequestManager paramRequestManager, int paramInt1, int paramInt2)
  {
    return paramRequestManager.asBitmap().apply(((RequestOptions)((RequestOptions)RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE).useAnimationPool(true)).skipMemoryCache(true)).override(paramInt1, paramInt2));
  }
  
  private void loadNextFrame()
  {
    if (isRunning)
    {
      if (isLoadPending) {
        return;
      }
      if (startFromFirstFrame)
      {
        boolean bool;
        if (pendingTarget == null) {
          bool = true;
        } else {
          bool = false;
        }
        Preconditions.checkArgument(bool, "Pending target must be null when starting from the first frame");
        gifDecoder.resetFrameIndex();
        startFromFirstFrame = false;
      }
      if (pendingTarget != null)
      {
        DelayTarget localDelayTarget = pendingTarget;
        pendingTarget = null;
        onFrameReady(localDelayTarget);
        return;
      }
      isLoadPending = true;
      int i = gifDecoder.getNextDelay();
      long l1 = SystemClock.uptimeMillis();
      long l2 = i;
      gifDecoder.advance();
      next = new DelayTarget(handler, gifDecoder.getCurrentFrameIndex(), l1 + l2);
      requestBuilder.apply(RequestOptions.signatureOf(getFrameSignature())).load(gifDecoder).into(next);
    }
  }
  
  private void recycleFirstFrame()
  {
    if (firstFrame != null)
    {
      bitmapPool.put(firstFrame);
      firstFrame = null;
    }
  }
  
  private void start()
  {
    if (isRunning) {
      return;
    }
    isRunning = true;
    isCleared = false;
    loadNextFrame();
  }
  
  private void stop()
  {
    isRunning = false;
  }
  
  void clear()
  {
    callbacks.clear();
    recycleFirstFrame();
    stop();
    if (current != null)
    {
      requestManager.clear(current);
      current = null;
    }
    if (next != null)
    {
      requestManager.clear(next);
      next = null;
    }
    if (pendingTarget != null)
    {
      requestManager.clear(pendingTarget);
      pendingTarget = null;
    }
    gifDecoder.clear();
    isCleared = true;
  }
  
  ByteBuffer getBuffer()
  {
    return gifDecoder.getData().asReadOnlyBuffer();
  }
  
  Bitmap getCurrentFrame()
  {
    if (current != null) {
      return current.getResource();
    }
    return firstFrame;
  }
  
  int getCurrentIndex()
  {
    if (current != null) {
      return current.index;
    }
    return -1;
  }
  
  Bitmap getFirstFrame()
  {
    return firstFrame;
  }
  
  int getFrameCount()
  {
    return gifDecoder.getFrameCount();
  }
  
  Transformation getFrameTransformation()
  {
    return transformation;
  }
  
  int getHeight()
  {
    return getCurrentFrame().getHeight();
  }
  
  int getLoopCount()
  {
    return gifDecoder.getTotalIterationCount();
  }
  
  int getSize()
  {
    return gifDecoder.getByteSize() + getFrameSize();
  }
  
  int getWidth()
  {
    return getCurrentFrame().getWidth();
  }
  
  void onFrameReady(DelayTarget paramDelayTarget)
  {
    if (onEveryFrameListener != null) {
      onEveryFrameListener.onFrameReady();
    }
    isLoadPending = false;
    if (isCleared)
    {
      handler.obtainMessage(2, paramDelayTarget).sendToTarget();
      return;
    }
    if (!isRunning)
    {
      pendingTarget = paramDelayTarget;
      return;
    }
    if (paramDelayTarget.getResource() != null)
    {
      recycleFirstFrame();
      DelayTarget localDelayTarget = current;
      current = paramDelayTarget;
      int i = callbacks.size() - 1;
      while (i >= 0)
      {
        ((FrameCallback)callbacks.get(i)).onFrameReady();
        i -= 1;
      }
      if (localDelayTarget != null) {
        handler.obtainMessage(2, localDelayTarget).sendToTarget();
      }
    }
    loadNextFrame();
  }
  
  void setFrameTransformation(Transformation paramTransformation, Bitmap paramBitmap)
  {
    transformation = ((Transformation)Preconditions.checkNotNull(paramTransformation));
    firstFrame = ((Bitmap)Preconditions.checkNotNull(paramBitmap));
    requestBuilder = requestBuilder.apply(new RequestOptions().transform(paramTransformation));
  }
  
  void setNextStartFromFirstFrame()
  {
    Preconditions.checkArgument(isRunning ^ true, "Can't restart a running animation");
    startFromFirstFrame = true;
    if (pendingTarget != null)
    {
      requestManager.clear(pendingTarget);
      pendingTarget = null;
    }
  }
  
  void setOnEveryFrameReadyListener(OnEveryFrameListener paramOnEveryFrameListener)
  {
    onEveryFrameListener = paramOnEveryFrameListener;
  }
  
  void subscribe(FrameCallback paramFrameCallback)
  {
    if (!isCleared)
    {
      if (!callbacks.contains(paramFrameCallback))
      {
        boolean bool = callbacks.isEmpty();
        callbacks.add(paramFrameCallback);
        if (bool) {
          start();
        }
      }
      else
      {
        throw new IllegalStateException("Cannot subscribe twice in a row");
      }
    }
    else {
      throw new IllegalStateException("Cannot subscribe to a cleared frame loader");
    }
  }
  
  void unsubscribe(FrameCallback paramFrameCallback)
  {
    callbacks.remove(paramFrameCallback);
    if (callbacks.isEmpty()) {
      stop();
    }
  }
  
  @VisibleForTesting
  static class DelayTarget
    extends SimpleTarget<Bitmap>
  {
    private final Handler handler;
    final int index;
    private Bitmap resource;
    private final long targetTime;
    
    DelayTarget(Handler paramHandler, int paramInt, long paramLong)
    {
      handler = paramHandler;
      index = paramInt;
      targetTime = paramLong;
    }
    
    Bitmap getResource()
    {
      return resource;
    }
    
    public void onResourceReady(Bitmap paramBitmap, Transition paramTransition)
    {
      resource = paramBitmap;
      paramBitmap = handler.obtainMessage(1, this);
      handler.sendMessageAtTime(paramBitmap, targetTime);
    }
  }
  
  public static abstract interface FrameCallback
  {
    public abstract void onFrameReady();
  }
  
  private class FrameLoaderCallback
    implements Handler.Callback
  {
    static final int MSG_CLEAR = 2;
    static final int MSG_DELAY = 1;
    
    FrameLoaderCallback() {}
    
    public boolean handleMessage(Message paramMessage)
    {
      if (what == 1)
      {
        paramMessage = (GifFrameLoader.DelayTarget)obj;
        onFrameReady(paramMessage);
        return true;
      }
      if (what == 2)
      {
        paramMessage = (GifFrameLoader.DelayTarget)obj;
        requestManager.clear(paramMessage);
      }
      return false;
    }
  }
  
  @VisibleForTesting
  static abstract interface OnEveryFrameListener
  {
    public abstract void onFrameReady();
  }
}
