package com.facebook.drawee.drawable;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.VisibleForTesting;
import java.util.Arrays;

public class FadeDrawable
  extends ArrayDrawable
{
  @VisibleForTesting
  public static final int TRANSITION_NONE = 2;
  @VisibleForTesting
  public static final int TRANSITION_RUNNING = 1;
  @VisibleForTesting
  public static final int TRANSITION_STARTING = 0;
  @VisibleForTesting
  int mAlpha;
  @VisibleForTesting
  int[] mAlphas;
  private final int mDefaultLayerAlpha;
  private final boolean mDefaultLayerIsOn;
  @VisibleForTesting
  int mDurationMs;
  @VisibleForTesting
  boolean[] mIsLayerOn;
  private final Drawable[] mLayers;
  @VisibleForTesting
  int mPreventInvalidateCount;
  @VisibleForTesting
  int[] mStartAlphas;
  @VisibleForTesting
  long mStartTimeMs;
  @VisibleForTesting
  int mTransitionState;
  
  public FadeDrawable(Drawable[] paramArrayOfDrawable)
  {
    this(paramArrayOfDrawable, false);
  }
  
  public FadeDrawable(Drawable[] paramArrayOfDrawable, boolean paramBoolean)
  {
    super(paramArrayOfDrawable);
    int i = paramArrayOfDrawable.length;
    boolean bool = true;
    if (i < 1) {
      bool = false;
    }
    Preconditions.checkState(bool, "At least one layer required!");
    mLayers = paramArrayOfDrawable;
    mStartAlphas = new int[paramArrayOfDrawable.length];
    mAlphas = new int[paramArrayOfDrawable.length];
    i = 255;
    mAlpha = 255;
    mIsLayerOn = new boolean[paramArrayOfDrawable.length];
    mPreventInvalidateCount = 0;
    mDefaultLayerIsOn = paramBoolean;
    if (!mDefaultLayerIsOn) {
      i = 0;
    }
    mDefaultLayerAlpha = i;
    resetInternal();
  }
  
  private void drawDrawableWithAlpha(Canvas paramCanvas, Drawable paramDrawable, int paramInt)
  {
    if ((paramDrawable != null) && (paramInt > 0))
    {
      mPreventInvalidateCount += 1;
      paramDrawable.mutate().setAlpha(paramInt);
      mPreventInvalidateCount -= 1;
      paramDrawable.draw(paramCanvas);
    }
  }
  
  private void resetInternal()
  {
    mTransitionState = 2;
    Arrays.fill(mStartAlphas, mDefaultLayerAlpha);
    mStartAlphas[0] = 255;
    Arrays.fill(mAlphas, mDefaultLayerAlpha);
    mAlphas[0] = 255;
    Arrays.fill(mIsLayerOn, mDefaultLayerIsOn);
    mIsLayerOn[0] = true;
  }
  
  private boolean updateAlphas(float paramFloat)
  {
    int i = 0;
    boolean bool2 = true;
    while (i < mLayers.length)
    {
      int j;
      if (mIsLayerOn[i] != 0) {
        j = 1;
      } else {
        j = -1;
      }
      mAlphas[i] = ((int)(mStartAlphas[i] + j * 255 * paramFloat));
      if (mAlphas[i] < 0) {
        mAlphas[i] = 0;
      }
      if (mAlphas[i] > 255) {
        mAlphas[i] = 255;
      }
      boolean bool1 = bool2;
      if (mIsLayerOn[i] != 0)
      {
        bool1 = bool2;
        if (mAlphas[i] < 255) {
          bool1 = false;
        }
      }
      bool2 = bool1;
      if (mIsLayerOn[i] == 0)
      {
        bool2 = bool1;
        if (mAlphas[i] > 0) {
          bool2 = false;
        }
      }
      i += 1;
    }
    return bool2;
  }
  
  public void beginBatchMode()
  {
    mPreventInvalidateCount += 1;
  }
  
  public void draw(Canvas paramCanvas)
  {
    int k = mTransitionState;
    int i = 2;
    int j = 0;
    switch (k)
    {
    default: 
      break;
    case 2: 
      bool1 = true;
      i = j;
      break;
    case 1: 
      if (mDurationMs > 0) {
        bool1 = true;
      } else {
        bool1 = false;
      }
      Preconditions.checkState(bool1);
      bool2 = updateAlphas((float)(getCurrentTimeMs() - mStartTimeMs) / mDurationMs);
      bool1 = bool2;
      if (!bool2) {
        i = 1;
      }
      mTransitionState = i;
      i = j;
      break;
    }
    System.arraycopy(mAlphas, 0, mStartAlphas, 0, mLayers.length);
    mStartTimeMs = getCurrentTimeMs();
    float f;
    if (mDurationMs == 0) {
      f = 1.0F;
    } else {
      f = 0.0F;
    }
    boolean bool2 = updateAlphas(f);
    boolean bool1 = bool2;
    if (!bool2) {
      i = 1;
    }
    mTransitionState = i;
    i = j;
    while (i < mLayers.length)
    {
      drawDrawableWithAlpha(paramCanvas, mLayers[i], mAlphas[i] * mAlpha / 255);
      i += 1;
    }
    if (!bool1) {
      invalidateSelf();
    }
  }
  
  public void endBatchMode()
  {
    mPreventInvalidateCount -= 1;
    invalidateSelf();
  }
  
  public void fadeInAllLayers()
  {
    mTransitionState = 0;
    Arrays.fill(mIsLayerOn, true);
    invalidateSelf();
  }
  
  public void fadeInLayer(int paramInt)
  {
    mTransitionState = 0;
    mIsLayerOn[paramInt] = true;
    invalidateSelf();
  }
  
  public void fadeOutAllLayers()
  {
    mTransitionState = 0;
    Arrays.fill(mIsLayerOn, false);
    invalidateSelf();
  }
  
  public void fadeOutLayer(int paramInt)
  {
    mTransitionState = 0;
    mIsLayerOn[paramInt] = false;
    invalidateSelf();
  }
  
  public void fadeToLayer(int paramInt)
  {
    mTransitionState = 0;
    Arrays.fill(mIsLayerOn, false);
    mIsLayerOn[paramInt] = true;
    invalidateSelf();
  }
  
  public void fadeUpToLayer(int paramInt)
  {
    mTransitionState = 0;
    boolean[] arrayOfBoolean = mIsLayerOn;
    paramInt += 1;
    Arrays.fill(arrayOfBoolean, 0, paramInt, true);
    Arrays.fill(mIsLayerOn, paramInt, mLayers.length, false);
    invalidateSelf();
  }
  
  public void finishTransitionImmediately()
  {
    mTransitionState = 2;
    int i = 0;
    while (i < mLayers.length)
    {
      int[] arrayOfInt = mAlphas;
      int j;
      if (mIsLayerOn[i] != 0) {
        j = 255;
      } else {
        j = 0;
      }
      arrayOfInt[i] = j;
      i += 1;
    }
    invalidateSelf();
  }
  
  public int getAlpha()
  {
    return mAlpha;
  }
  
  protected long getCurrentTimeMs()
  {
    return SystemClock.uptimeMillis();
  }
  
  public int getTransitionDuration()
  {
    return mDurationMs;
  }
  
  public int getTransitionState()
  {
    return mTransitionState;
  }
  
  public void hideLayerImmediately(int paramInt)
  {
    mIsLayerOn[paramInt] = false;
    mAlphas[paramInt] = 0;
    invalidateSelf();
  }
  
  public void invalidateSelf()
  {
    if (mPreventInvalidateCount == 0) {
      super.invalidateSelf();
    }
  }
  
  public boolean isDefaultLayerIsOn()
  {
    return mDefaultLayerIsOn;
  }
  
  public boolean isLayerOn(int paramInt)
  {
    return mIsLayerOn[paramInt];
  }
  
  public void reset()
  {
    resetInternal();
    invalidateSelf();
  }
  
  public void setAlpha(int paramInt)
  {
    if (mAlpha != paramInt)
    {
      mAlpha = paramInt;
      invalidateSelf();
    }
  }
  
  public void setTransitionDuration(int paramInt)
  {
    mDurationMs = paramInt;
    if (mTransitionState == 1) {
      mTransitionState = 0;
    }
  }
  
  public void showLayerImmediately(int paramInt)
  {
    mIsLayerOn[paramInt] = true;
    mAlphas[paramInt] = 255;
    invalidateSelf();
  }
}
