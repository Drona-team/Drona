package com.bumptech.glide.load.resource.gif;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Drawable.ConstantState;
import android.view.Gravity;
import androidx.annotation.VisibleForTesting;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat.AnimationCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Preconditions;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class GifDrawable
  extends Drawable
  implements GifFrameLoader.FrameCallback, Animatable, Animatable2Compat
{
  private static final int GRAVITY = 119;
  public static final int LOOP_FOREVER = -1;
  public static final int LOOP_INTRINSIC = 0;
  private List<Animatable2Compat.AnimationCallback> animationCallbacks;
  private boolean applyGravity;
  private Rect destRect;
  private boolean isRecycled;
  private boolean isRunning;
  private boolean isStarted;
  private boolean isVisible = true;
  private int loopCount;
  private int maxLoopCount = -1;
  private Paint paint;
  private final GifState state;
  
  public GifDrawable(Context paramContext, GifDecoder paramGifDecoder, Transformation paramTransformation, int paramInt1, int paramInt2, Bitmap paramBitmap)
  {
    this(new GifState(new GifFrameLoader(Glide.get(paramContext), paramGifDecoder, paramInt1, paramInt2, paramTransformation, paramBitmap)));
  }
  
  public GifDrawable(Context paramContext, GifDecoder paramGifDecoder, BitmapPool paramBitmapPool, Transformation paramTransformation, int paramInt1, int paramInt2, Bitmap paramBitmap)
  {
    this(paramContext, paramGifDecoder, paramTransformation, paramInt1, paramInt2, paramBitmap);
  }
  
  GifDrawable(GifState paramGifState)
  {
    state = ((GifState)Preconditions.checkNotNull(paramGifState));
  }
  
  GifDrawable(GifFrameLoader paramGifFrameLoader, Paint paramPaint)
  {
    this(new GifState(paramGifFrameLoader));
    paint = paramPaint;
  }
  
  private Drawable.Callback findCallback()
  {
    for (Drawable.Callback localCallback = getCallback(); (localCallback instanceof Drawable); localCallback = ((Drawable)localCallback).getCallback()) {}
    return localCallback;
  }
  
  private Rect getDestRect()
  {
    if (destRect == null) {
      destRect = new Rect();
    }
    return destRect;
  }
  
  private Paint getPaint()
  {
    if (paint == null) {
      paint = new Paint(2);
    }
    return paint;
  }
  
  private void notifyAnimationEndToListeners()
  {
    if (animationCallbacks != null)
    {
      int i = 0;
      int j = animationCallbacks.size();
      while (i < j)
      {
        ((Animatable2Compat.AnimationCallback)animationCallbacks.get(i)).onAnimationEnd(this);
        i += 1;
      }
    }
  }
  
  private void resetLoopCount()
  {
    loopCount = 0;
  }
  
  private void startRunning()
  {
    Preconditions.checkArgument(isRecycled ^ true, "You cannot start a recycled Drawable. Ensure thatyou clear any references to the Drawable when clearing the corresponding request.");
    if (state.frameLoader.getFrameCount() == 1)
    {
      invalidateSelf();
      return;
    }
    if (!isRunning)
    {
      isRunning = true;
      state.frameLoader.subscribe(this);
      invalidateSelf();
    }
  }
  
  private void stopRunning()
  {
    isRunning = false;
    state.frameLoader.unsubscribe(this);
  }
  
  public void clearAnimationCallbacks()
  {
    if (animationCallbacks != null) {
      animationCallbacks.clear();
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (isRecycled) {
      return;
    }
    if (applyGravity)
    {
      Gravity.apply(119, getIntrinsicWidth(), getIntrinsicHeight(), getBounds(), getDestRect());
      applyGravity = false;
    }
    paramCanvas.drawBitmap(state.frameLoader.getCurrentFrame(), null, getDestRect(), getPaint());
  }
  
  public ByteBuffer getBuffer()
  {
    return state.frameLoader.getBuffer();
  }
  
  public Drawable.ConstantState getConstantState()
  {
    return state;
  }
  
  public Bitmap getFirstFrame()
  {
    return state.frameLoader.getFirstFrame();
  }
  
  public int getFrameCount()
  {
    return state.frameLoader.getFrameCount();
  }
  
  public int getFrameIndex()
  {
    return state.frameLoader.getCurrentIndex();
  }
  
  public Transformation getFrameTransformation()
  {
    return state.frameLoader.getFrameTransformation();
  }
  
  public int getIntrinsicHeight()
  {
    return state.frameLoader.getHeight();
  }
  
  public int getIntrinsicWidth()
  {
    return state.frameLoader.getWidth();
  }
  
  public int getOpacity()
  {
    return -2;
  }
  
  public int getSize()
  {
    return state.frameLoader.getSize();
  }
  
  boolean isRecycled()
  {
    return isRecycled;
  }
  
  public boolean isRunning()
  {
    return isRunning;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    super.onBoundsChange(paramRect);
    applyGravity = true;
  }
  
  public void onFrameReady()
  {
    if (findCallback() == null)
    {
      stop();
      invalidateSelf();
      return;
    }
    invalidateSelf();
    if (getFrameIndex() == getFrameCount() - 1) {
      loopCount += 1;
    }
    if ((maxLoopCount != -1) && (loopCount >= maxLoopCount))
    {
      notifyAnimationEndToListeners();
      stop();
    }
  }
  
  public void recycle()
  {
    isRecycled = true;
    state.frameLoader.clear();
  }
  
  public void registerAnimationCallback(Animatable2Compat.AnimationCallback paramAnimationCallback)
  {
    if (paramAnimationCallback == null) {
      return;
    }
    if (animationCallbacks == null) {
      animationCallbacks = new ArrayList();
    }
    animationCallbacks.add(paramAnimationCallback);
  }
  
  public void setAlpha(int paramInt)
  {
    getPaint().setAlpha(paramInt);
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    getPaint().setColorFilter(paramColorFilter);
  }
  
  public void setFrameTransformation(Transformation paramTransformation, Bitmap paramBitmap)
  {
    state.frameLoader.setFrameTransformation(paramTransformation, paramBitmap);
  }
  
  void setIsRunning(boolean paramBoolean)
  {
    isRunning = paramBoolean;
  }
  
  public void setLoopCount(int paramInt)
  {
    if ((paramInt <= 0) && (paramInt != -1) && (paramInt != 0)) {
      throw new IllegalArgumentException("Loop count must be greater than 0, or equal to GlideDrawable.LOOP_FOREVER, or equal to GlideDrawable.LOOP_INTRINSIC");
    }
    if (paramInt == 0)
    {
      int i = state.frameLoader.getLoopCount();
      paramInt = i;
      if (i == 0) {
        paramInt = -1;
      }
      maxLoopCount = paramInt;
      return;
    }
    maxLoopCount = paramInt;
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    Preconditions.checkArgument(isRecycled ^ true, "Cannot change the visibility of a recycled resource. Ensure that you unset the Drawable from your View before changing the View's visibility.");
    isVisible = paramBoolean1;
    if (!paramBoolean1) {
      stopRunning();
    } else if (isStarted) {
      startRunning();
    }
    return super.setVisible(paramBoolean1, paramBoolean2);
  }
  
  public void start()
  {
    isStarted = true;
    resetLoopCount();
    if (isVisible) {
      startRunning();
    }
  }
  
  public void startFromFirstFrame()
  {
    Preconditions.checkArgument(isRunning ^ true, "You cannot restart a currently running animation.");
    state.frameLoader.setNextStartFromFirstFrame();
    start();
  }
  
  public void stop()
  {
    isStarted = false;
    stopRunning();
  }
  
  public boolean unregisterAnimationCallback(Animatable2Compat.AnimationCallback paramAnimationCallback)
  {
    if ((animationCallbacks != null) && (paramAnimationCallback != null)) {
      return animationCallbacks.remove(paramAnimationCallback);
    }
    return false;
  }
  
  static final class GifState
    extends Drawable.ConstantState
  {
    @VisibleForTesting
    final GifFrameLoader frameLoader;
    
    GifState(GifFrameLoader paramGifFrameLoader)
    {
      frameLoader = paramGifFrameLoader;
    }
    
    public int getChangingConfigurations()
    {
      return 0;
    }
    
    public Drawable newDrawable()
    {
      return new GifDrawable(this);
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      return newDrawable();
    }
  }
}
