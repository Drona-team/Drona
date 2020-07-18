package androidx.appcompat.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import androidx.annotation.RestrictTo;
import androidx.core.graphics.drawable.DrawableCompat;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
class DrawableContainer
  extends Drawable
  implements Drawable.Callback
{
  private static final boolean DEBUG = false;
  private static final boolean DEFAULT_DITHER = true;
  private static final String TAG = "DrawableContainer";
  private int mAlpha = 255;
  private Runnable mAnimationRunnable;
  private BlockInvalidateCallback mBlockInvalidateCallback;
  private int mCurIndex = -1;
  private Drawable mCurrDrawable;
  private DrawableContainerState mDrawableContainerState;
  private long mEnterAnimationEnd;
  private long mExitAnimationEnd;
  private boolean mHasAlpha;
  private Rect mHotspotBounds;
  private Drawable mLastDrawable;
  private int mLastIndex = -1;
  private boolean mMutated;
  
  DrawableContainer() {}
  
  private void initializeDrawableForDisplay(Drawable paramDrawable)
  {
    if (mBlockInvalidateCallback == null) {
      mBlockInvalidateCallback = new BlockInvalidateCallback();
    }
    paramDrawable.setCallback(mBlockInvalidateCallback.wrap(paramDrawable.getCallback()));
    try
    {
      int i = mDrawableContainerState.mEnterFadeDuration;
      if (i <= 0)
      {
        bool = mHasAlpha;
        if (bool) {
          paramDrawable.setAlpha(mAlpha);
        }
      }
      boolean bool = mDrawableContainerState.mHasColorFilter;
      if (bool)
      {
        paramDrawable.setColorFilter(mDrawableContainerState.mColorFilter);
      }
      else
      {
        bool = mDrawableContainerState.mHasTintList;
        if (bool) {
          DrawableCompat.setTintList(paramDrawable, mDrawableContainerState.mTintList);
        }
        bool = mDrawableContainerState.mHasTintMode;
        if (bool) {
          DrawableCompat.setTintMode(paramDrawable, mDrawableContainerState.mTintMode);
        }
      }
      paramDrawable.setVisible(isVisible(), true);
      paramDrawable.setDither(mDrawableContainerState.mDither);
      paramDrawable.setState(getState());
      paramDrawable.setLevel(getLevel());
      paramDrawable.setBounds(getBounds());
      i = Build.VERSION.SDK_INT;
      if (i >= 23) {
        paramDrawable.setLayoutDirection(getLayoutDirection());
      }
      i = Build.VERSION.SDK_INT;
      if (i >= 19) {
        paramDrawable.setAutoMirrored(mDrawableContainerState.mAutoMirrored);
      }
      Rect localRect = mHotspotBounds;
      i = Build.VERSION.SDK_INT;
      if ((i >= 21) && (localRect != null)) {
        paramDrawable.setHotspotBounds(left, top, right, bottom);
      }
      paramDrawable.setCallback(mBlockInvalidateCallback.unwrap());
      return;
    }
    catch (Throwable localThrowable)
    {
      paramDrawable.setCallback(mBlockInvalidateCallback.unwrap());
      throw localThrowable;
    }
  }
  
  private boolean needsMirroring()
  {
    return (isAutoMirrored()) && (DrawableCompat.getLayoutDirection(this) == 1);
  }
  
  static int resolveDensity(Resources paramResources, int paramInt)
  {
    if (paramResources != null) {
      paramInt = getDisplayMetricsdensityDpi;
    }
    if (paramInt == 0) {
      return 160;
    }
    return paramInt;
  }
  
  void animate(boolean paramBoolean)
  {
    int j = 1;
    mHasAlpha = true;
    long l = SystemClock.uptimeMillis();
    if (mCurrDrawable != null)
    {
      if (mEnterAnimationEnd != 0L) {
        if (mEnterAnimationEnd <= l)
        {
          mCurrDrawable.setAlpha(mAlpha);
          mEnterAnimationEnd = 0L;
        }
        else
        {
          i = (int)((mEnterAnimationEnd - l) * 255L) / mDrawableContainerState.mEnterFadeDuration;
          mCurrDrawable.setAlpha((255 - i) * mAlpha / 255);
          i = 1;
          break label111;
        }
      }
    }
    else {
      mEnterAnimationEnd = 0L;
    }
    int i = 0;
    label111:
    if (mLastDrawable != null)
    {
      if (mExitAnimationEnd != 0L) {
        if (mExitAnimationEnd <= l)
        {
          mLastDrawable.setVisible(false, false);
          mLastDrawable = null;
          mLastIndex = -1;
          mExitAnimationEnd = 0L;
        }
        else
        {
          i = (int)((mExitAnimationEnd - l) * 255L) / mDrawableContainerState.mExitFadeDuration;
          mLastDrawable.setAlpha(i * mAlpha / 255);
          i = j;
        }
      }
    }
    else {
      mExitAnimationEnd = 0L;
    }
    if ((paramBoolean) && (i != 0)) {
      scheduleSelf(mAnimationRunnable, l + 16L);
    }
  }
  
  public void applyTheme(Resources.Theme paramTheme)
  {
    mDrawableContainerState.applyTheme(paramTheme);
  }
  
  public boolean canApplyTheme()
  {
    return mDrawableContainerState.canApplyTheme();
  }
  
  void clearMutated()
  {
    mDrawableContainerState.clearMutated();
    mMutated = false;
  }
  
  DrawableContainerState cloneConstantState()
  {
    return mDrawableContainerState;
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (mCurrDrawable != null) {
      mCurrDrawable.draw(paramCanvas);
    }
    if (mLastDrawable != null) {
      mLastDrawable.draw(paramCanvas);
    }
  }
  
  public int getAlpha()
  {
    return mAlpha;
  }
  
  public int getChangingConfigurations()
  {
    return super.getChangingConfigurations() | mDrawableContainerState.getChangingConfigurations();
  }
  
  public final Drawable.ConstantState getConstantState()
  {
    if (mDrawableContainerState.canConstantState())
    {
      mDrawableContainerState.mChangingConfigurations = getChangingConfigurations();
      return mDrawableContainerState;
    }
    return null;
  }
  
  public Drawable getCurrent()
  {
    return mCurrDrawable;
  }
  
  int getCurrentIndex()
  {
    return mCurIndex;
  }
  
  public void getHotspotBounds(Rect paramRect)
  {
    if (mHotspotBounds != null)
    {
      paramRect.set(mHotspotBounds);
      return;
    }
    super.getHotspotBounds(paramRect);
  }
  
  public int getIntrinsicHeight()
  {
    if (mDrawableContainerState.isConstantSize()) {
      return mDrawableContainerState.getConstantHeight();
    }
    if (mCurrDrawable != null) {
      return mCurrDrawable.getIntrinsicHeight();
    }
    return -1;
  }
  
  public int getIntrinsicWidth()
  {
    if (mDrawableContainerState.isConstantSize()) {
      return mDrawableContainerState.getConstantWidth();
    }
    if (mCurrDrawable != null) {
      return mCurrDrawable.getIntrinsicWidth();
    }
    return -1;
  }
  
  public int getLayoutDirection()
  {
    throw new Error("Unresolved compilation error: Method <androidx.appcompat.graphics.drawable.DrawableContainer: int getLayoutDirection()> does not exist!");
  }
  
  public int getMinimumHeight()
  {
    if (mDrawableContainerState.isConstantSize()) {
      return mDrawableContainerState.getConstantMinimumHeight();
    }
    if (mCurrDrawable != null) {
      return mCurrDrawable.getMinimumHeight();
    }
    return 0;
  }
  
  public int getMinimumWidth()
  {
    if (mDrawableContainerState.isConstantSize()) {
      return mDrawableContainerState.getConstantMinimumWidth();
    }
    if (mCurrDrawable != null) {
      return mCurrDrawable.getMinimumWidth();
    }
    return 0;
  }
  
  public int getOpacity()
  {
    if ((mCurrDrawable != null) && (mCurrDrawable.isVisible())) {
      return mDrawableContainerState.getOpacity();
    }
    return -2;
  }
  
  public void getOutline(Outline paramOutline)
  {
    if (mCurrDrawable != null) {
      mCurrDrawable.getOutline(paramOutline);
    }
  }
  
  public boolean getPadding(Rect paramRect)
  {
    Rect localRect = mDrawableContainerState.getConstantPadding();
    int i;
    boolean bool;
    if (localRect != null)
    {
      paramRect.set(localRect);
      i = left;
      int j = top;
      int k = bottom;
      if ((right | i | j | k) != 0) {
        bool = true;
      } else {
        bool = false;
      }
    }
    else if (mCurrDrawable != null)
    {
      bool = mCurrDrawable.getPadding(paramRect);
    }
    else
    {
      bool = super.getPadding(paramRect);
    }
    if (needsMirroring())
    {
      i = left;
      left = right;
      right = i;
    }
    return bool;
  }
  
  public void invalidateDrawable(Drawable paramDrawable)
  {
    if (mDrawableContainerState != null) {
      mDrawableContainerState.invalidateCache();
    }
    if ((paramDrawable == mCurrDrawable) && (getCallback() != null)) {
      getCallback().invalidateDrawable(this);
    }
  }
  
  public boolean isAutoMirrored()
  {
    return mDrawableContainerState.mAutoMirrored;
  }
  
  public boolean isStateful()
  {
    return mDrawableContainerState.isStateful();
  }
  
  public void jumpToCurrentState()
  {
    int i;
    if (mLastDrawable != null)
    {
      mLastDrawable.jumpToCurrentState();
      mLastDrawable = null;
      mLastIndex = -1;
      i = 1;
    }
    else
    {
      i = 0;
    }
    if (mCurrDrawable != null)
    {
      mCurrDrawable.jumpToCurrentState();
      if (mHasAlpha) {
        mCurrDrawable.setAlpha(mAlpha);
      }
    }
    if (mExitAnimationEnd != 0L)
    {
      mExitAnimationEnd = 0L;
      i = 1;
    }
    if (mEnterAnimationEnd != 0L)
    {
      mEnterAnimationEnd = 0L;
      i = 1;
    }
    if (i != 0) {
      invalidateSelf();
    }
  }
  
  public Drawable mutate()
  {
    if ((!mMutated) && (super.mutate() == this))
    {
      DrawableContainerState localDrawableContainerState = cloneConstantState();
      localDrawableContainerState.mutate();
      setConstantState(localDrawableContainerState);
      mMutated = true;
    }
    return this;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    if (mLastDrawable != null) {
      mLastDrawable.setBounds(paramRect);
    }
    if (mCurrDrawable != null) {
      mCurrDrawable.setBounds(paramRect);
    }
  }
  
  public boolean onLayoutDirectionChanged(int paramInt)
  {
    return mDrawableContainerState.setLayoutDirection(paramInt, getCurrentIndex());
  }
  
  protected boolean onLevelChange(int paramInt)
  {
    if (mLastDrawable != null) {
      return mLastDrawable.setLevel(paramInt);
    }
    if (mCurrDrawable != null) {
      return mCurrDrawable.setLevel(paramInt);
    }
    return false;
  }
  
  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    if (mLastDrawable != null) {
      return mLastDrawable.setState(paramArrayOfInt);
    }
    if (mCurrDrawable != null) {
      return mCurrDrawable.setState(paramArrayOfInt);
    }
    return false;
  }
  
  public void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong)
  {
    if ((paramDrawable == mCurrDrawable) && (getCallback() != null)) {
      getCallback().scheduleDrawable(this, paramRunnable, paramLong);
    }
  }
  
  boolean selectDrawable(int paramInt)
  {
    if (paramInt == mCurIndex) {
      return false;
    }
    long l = SystemClock.uptimeMillis();
    if (mDrawableContainerState.mExitFadeDuration > 0)
    {
      if (mLastDrawable != null) {
        mLastDrawable.setVisible(false, false);
      }
      if (mCurrDrawable != null)
      {
        mLastDrawable = mCurrDrawable;
        mLastIndex = mCurIndex;
        mExitAnimationEnd = (mDrawableContainerState.mExitFadeDuration + l);
      }
      else
      {
        mLastDrawable = null;
        mLastIndex = -1;
        mExitAnimationEnd = 0L;
      }
    }
    else if (mCurrDrawable != null)
    {
      mCurrDrawable.setVisible(false, false);
    }
    if ((paramInt >= 0) && (paramInt < mDrawableContainerState.mNumChildren))
    {
      Drawable localDrawable = mDrawableContainerState.getChild(paramInt);
      mCurrDrawable = localDrawable;
      mCurIndex = paramInt;
      if (localDrawable != null)
      {
        if (mDrawableContainerState.mEnterFadeDuration > 0) {
          mEnterAnimationEnd = (l + mDrawableContainerState.mEnterFadeDuration);
        }
        initializeDrawableForDisplay(localDrawable);
      }
    }
    else
    {
      mCurrDrawable = null;
      mCurIndex = -1;
    }
    if ((mEnterAnimationEnd != 0L) || (mExitAnimationEnd != 0L))
    {
      if (mAnimationRunnable == null) {
        mAnimationRunnable = new Runnable()
        {
          public void run()
          {
            animate(true);
            invalidateSelf();
          }
        };
      } else {
        unscheduleSelf(mAnimationRunnable);
      }
      animate(true);
    }
    invalidateSelf();
    return true;
  }
  
  public void setAlpha(int paramInt)
  {
    if ((!mHasAlpha) || (mAlpha != paramInt))
    {
      mHasAlpha = true;
      mAlpha = paramInt;
      if (mCurrDrawable != null)
      {
        if (mEnterAnimationEnd == 0L)
        {
          mCurrDrawable.setAlpha(paramInt);
          return;
        }
        animate(false);
      }
    }
  }
  
  public void setAutoMirrored(boolean paramBoolean)
  {
    if (mDrawableContainerState.mAutoMirrored != paramBoolean)
    {
      mDrawableContainerState.mAutoMirrored = paramBoolean;
      if (mCurrDrawable != null) {
        DrawableCompat.setAutoMirrored(mCurrDrawable, mDrawableContainerState.mAutoMirrored);
      }
    }
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    mDrawableContainerState.mHasColorFilter = true;
    if (mDrawableContainerState.mColorFilter != paramColorFilter)
    {
      mDrawableContainerState.mColorFilter = paramColorFilter;
      if (mCurrDrawable != null) {
        mCurrDrawable.setColorFilter(paramColorFilter);
      }
    }
  }
  
  void setConstantState(DrawableContainerState paramDrawableContainerState)
  {
    mDrawableContainerState = paramDrawableContainerState;
    if (mCurIndex >= 0)
    {
      mCurrDrawable = paramDrawableContainerState.getChild(mCurIndex);
      if (mCurrDrawable != null) {
        initializeDrawableForDisplay(mCurrDrawable);
      }
    }
    mLastIndex = -1;
    mLastDrawable = null;
  }
  
  void setCurrentIndex(int paramInt)
  {
    selectDrawable(paramInt);
  }
  
  public void setDither(boolean paramBoolean)
  {
    if (mDrawableContainerState.mDither != paramBoolean)
    {
      mDrawableContainerState.mDither = paramBoolean;
      if (mCurrDrawable != null) {
        mCurrDrawable.setDither(mDrawableContainerState.mDither);
      }
    }
  }
  
  public void setEnterFadeDuration(int paramInt)
  {
    mDrawableContainerState.mEnterFadeDuration = paramInt;
  }
  
  public void setExitFadeDuration(int paramInt)
  {
    mDrawableContainerState.mExitFadeDuration = paramInt;
  }
  
  public void setHotspot(float paramFloat1, float paramFloat2)
  {
    if (mCurrDrawable != null) {
      DrawableCompat.setHotspot(mCurrDrawable, paramFloat1, paramFloat2);
    }
  }
  
  public void setHotspotBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (mHotspotBounds == null) {
      mHotspotBounds = new Rect(paramInt1, paramInt2, paramInt3, paramInt4);
    } else {
      mHotspotBounds.set(paramInt1, paramInt2, paramInt3, paramInt4);
    }
    if (mCurrDrawable != null) {
      DrawableCompat.setHotspotBounds(mCurrDrawable, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  public void setTintList(ColorStateList paramColorStateList)
  {
    mDrawableContainerState.mHasTintList = true;
    if (mDrawableContainerState.mTintList != paramColorStateList)
    {
      mDrawableContainerState.mTintList = paramColorStateList;
      DrawableCompat.setTintList(mCurrDrawable, paramColorStateList);
    }
  }
  
  public void setTintMode(PorterDuff.Mode paramMode)
  {
    mDrawableContainerState.mHasTintMode = true;
    if (mDrawableContainerState.mTintMode != paramMode)
    {
      mDrawableContainerState.mTintMode = paramMode;
      DrawableCompat.setTintMode(mCurrDrawable, paramMode);
    }
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = super.setVisible(paramBoolean1, paramBoolean2);
    if (mLastDrawable != null) {
      mLastDrawable.setVisible(paramBoolean1, paramBoolean2);
    }
    if (mCurrDrawable != null) {
      mCurrDrawable.setVisible(paramBoolean1, paramBoolean2);
    }
    return bool;
  }
  
  public void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable)
  {
    if ((paramDrawable == mCurrDrawable) && (getCallback() != null)) {
      getCallback().unscheduleDrawable(this, paramRunnable);
    }
  }
  
  final void updateDensity(Resources paramResources)
  {
    mDrawableContainerState.updateDensity(paramResources);
  }
  
  static class BlockInvalidateCallback
    implements Drawable.Callback
  {
    private Drawable.Callback mCallback;
    
    BlockInvalidateCallback() {}
    
    public void invalidateDrawable(Drawable paramDrawable) {}
    
    public void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong)
    {
      if (mCallback != null) {
        mCallback.scheduleDrawable(paramDrawable, paramRunnable, paramLong);
      }
    }
    
    public void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable)
    {
      if (mCallback != null) {
        mCallback.unscheduleDrawable(paramDrawable, paramRunnable);
      }
    }
    
    public Drawable.Callback unwrap()
    {
      Drawable.Callback localCallback = mCallback;
      mCallback = null;
      return localCallback;
    }
    
    public BlockInvalidateCallback wrap(Drawable.Callback paramCallback)
    {
      mCallback = paramCallback;
      return this;
    }
  }
  
  static abstract class DrawableContainerState
    extends Drawable.ConstantState
  {
    boolean mAutoMirrored;
    boolean mCanConstantState;
    int mChangingConfigurations;
    boolean mCheckedConstantSize;
    boolean mCheckedConstantState;
    boolean mCheckedOpacity;
    boolean mCheckedPadding;
    boolean mCheckedStateful;
    int mChildrenChangingConfigurations;
    ColorFilter mColorFilter;
    int mConstantHeight;
    int mConstantMinimumHeight;
    int mConstantMinimumWidth;
    Rect mConstantPadding;
    boolean mConstantSize;
    int mConstantWidth;
    int mDensity = 160;
    boolean mDither;
    SparseArray<Drawable.ConstantState> mDrawableFutures;
    Drawable[] mDrawables;
    int mEnterFadeDuration;
    int mExitFadeDuration;
    boolean mHasColorFilter;
    boolean mHasTintList;
    boolean mHasTintMode;
    int mLayoutDirection;
    boolean mMutated;
    int mNumChildren;
    int mOpacity;
    final DrawableContainer mOwner;
    Resources mSourceRes;
    boolean mStateful;
    ColorStateList mTintList;
    PorterDuff.Mode mTintMode;
    boolean mVariablePadding;
    
    DrawableContainerState(DrawableContainerState paramDrawableContainerState, DrawableContainer paramDrawableContainer, Resources paramResources)
    {
      int j = 0;
      mVariablePadding = false;
      mConstantSize = false;
      mDither = true;
      mEnterFadeDuration = 0;
      mExitFadeDuration = 0;
      mOwner = paramDrawableContainer;
      if (paramResources != null) {
        paramDrawableContainer = paramResources;
      } else if (paramDrawableContainerState != null) {
        paramDrawableContainer = mSourceRes;
      } else {
        paramDrawableContainer = null;
      }
      mSourceRes = paramDrawableContainer;
      int i;
      if (paramDrawableContainerState != null) {
        i = mDensity;
      } else {
        i = 0;
      }
      mDensity = DrawableContainer.resolveDensity(paramResources, i);
      if (paramDrawableContainerState != null)
      {
        mChangingConfigurations = mChangingConfigurations;
        mChildrenChangingConfigurations = mChildrenChangingConfigurations;
        mCheckedConstantState = true;
        mCanConstantState = true;
        mVariablePadding = mVariablePadding;
        mConstantSize = mConstantSize;
        mDither = mDither;
        mMutated = mMutated;
        mLayoutDirection = mLayoutDirection;
        mEnterFadeDuration = mEnterFadeDuration;
        mExitFadeDuration = mExitFadeDuration;
        mAutoMirrored = mAutoMirrored;
        mColorFilter = mColorFilter;
        mHasColorFilter = mHasColorFilter;
        mTintList = mTintList;
        mTintMode = mTintMode;
        mHasTintList = mHasTintList;
        mHasTintMode = mHasTintMode;
        if (mDensity == mDensity)
        {
          if (mCheckedPadding)
          {
            mConstantPadding = new Rect(mConstantPadding);
            mCheckedPadding = true;
          }
          if (mCheckedConstantSize)
          {
            mConstantWidth = mConstantWidth;
            mConstantHeight = mConstantHeight;
            mConstantMinimumWidth = mConstantMinimumWidth;
            mConstantMinimumHeight = mConstantMinimumHeight;
            mCheckedConstantSize = true;
          }
        }
        if (mCheckedOpacity)
        {
          mOpacity = mOpacity;
          mCheckedOpacity = true;
        }
        if (mCheckedStateful)
        {
          mStateful = mStateful;
          mCheckedStateful = true;
        }
        paramDrawableContainer = mDrawables;
        mDrawables = new Drawable[paramDrawableContainer.length];
        mNumChildren = mNumChildren;
        paramDrawableContainerState = mDrawableFutures;
        if (paramDrawableContainerState != null) {
          mDrawableFutures = paramDrawableContainerState.clone();
        } else {
          mDrawableFutures = new SparseArray(mNumChildren);
        }
        int k = mNumChildren;
        i = j;
        while (i < k)
        {
          if (paramDrawableContainer[i] != null)
          {
            paramDrawableContainerState = paramDrawableContainer[i].getConstantState();
            if (paramDrawableContainerState != null) {
              mDrawableFutures.put(i, paramDrawableContainerState);
            } else {
              mDrawables[i] = paramDrawableContainer[i];
            }
          }
          i += 1;
        }
      }
      mDrawables = new Drawable[10];
      mNumChildren = 0;
    }
    
    private void createAllFutures()
    {
      if (mDrawableFutures != null)
      {
        int j = mDrawableFutures.size();
        int i = 0;
        while (i < j)
        {
          int k = mDrawableFutures.keyAt(i);
          Drawable.ConstantState localConstantState = (Drawable.ConstantState)mDrawableFutures.valueAt(i);
          mDrawables[k] = prepareDrawable(localConstantState.newDrawable(mSourceRes));
          i += 1;
        }
        mDrawableFutures = null;
      }
    }
    
    private Drawable prepareDrawable(Drawable paramDrawable)
    {
      if (Build.VERSION.SDK_INT >= 23) {
        paramDrawable.setLayoutDirection(mLayoutDirection);
      }
      paramDrawable = paramDrawable.mutate();
      paramDrawable.setCallback(mOwner);
      return paramDrawable;
    }
    
    public final int addChild(Drawable paramDrawable)
    {
      int i = mNumChildren;
      if (i >= mDrawables.length) {
        growArray(i, i + 10);
      }
      paramDrawable.mutate();
      paramDrawable.setVisible(false, true);
      paramDrawable.setCallback(mOwner);
      mDrawables[i] = paramDrawable;
      mNumChildren += 1;
      int j = mChildrenChangingConfigurations;
      mChildrenChangingConfigurations = (paramDrawable.getChangingConfigurations() | j);
      invalidateCache();
      mConstantPadding = null;
      mCheckedPadding = false;
      mCheckedConstantSize = false;
      mCheckedConstantState = false;
      return i;
    }
    
    final void applyTheme(Resources.Theme paramTheme)
    {
      if (paramTheme != null)
      {
        createAllFutures();
        int j = mNumChildren;
        Drawable[] arrayOfDrawable = mDrawables;
        int i = 0;
        while (i < j)
        {
          if ((arrayOfDrawable[i] != null) && (arrayOfDrawable[i].canApplyTheme()))
          {
            arrayOfDrawable[i].applyTheme(paramTheme);
            mChildrenChangingConfigurations |= arrayOfDrawable[i].getChangingConfigurations();
          }
          i += 1;
        }
        updateDensity(paramTheme.getResources());
      }
    }
    
    public boolean canApplyTheme()
    {
      int j = mNumChildren;
      Drawable[] arrayOfDrawable = mDrawables;
      int i = 0;
      while (i < j)
      {
        Object localObject = arrayOfDrawable[i];
        if (localObject != null)
        {
          if (((Drawable)localObject).canApplyTheme()) {
            return true;
          }
        }
        else
        {
          localObject = (Drawable.ConstantState)mDrawableFutures.get(i);
          if ((localObject != null) && (((Drawable.ConstantState)localObject).canApplyTheme())) {
            return true;
          }
        }
        i += 1;
      }
      return false;
    }
    
    public boolean canConstantState()
    {
      try
      {
        if (mCheckedConstantState)
        {
          boolean bool = mCanConstantState;
          return bool;
        }
        createAllFutures();
        mCheckedConstantState = true;
        int j = mNumChildren;
        Drawable[] arrayOfDrawable = mDrawables;
        int i = 0;
        while (i < j)
        {
          if (arrayOfDrawable[i].getConstantState() == null)
          {
            mCanConstantState = false;
            return false;
          }
          i += 1;
        }
        mCanConstantState = true;
        return true;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    final void clearMutated()
    {
      mMutated = false;
    }
    
    protected void computeConstantSize()
    {
      mCheckedConstantSize = true;
      createAllFutures();
      int j = mNumChildren;
      Drawable[] arrayOfDrawable = mDrawables;
      mConstantHeight = -1;
      mConstantWidth = -1;
      int i = 0;
      mConstantMinimumHeight = 0;
      mConstantMinimumWidth = 0;
      while (i < j)
      {
        Drawable localDrawable = arrayOfDrawable[i];
        int k = localDrawable.getIntrinsicWidth();
        if (k > mConstantWidth) {
          mConstantWidth = k;
        }
        k = localDrawable.getIntrinsicHeight();
        if (k > mConstantHeight) {
          mConstantHeight = k;
        }
        k = localDrawable.getMinimumWidth();
        if (k > mConstantMinimumWidth) {
          mConstantMinimumWidth = k;
        }
        k = localDrawable.getMinimumHeight();
        if (k > mConstantMinimumHeight) {
          mConstantMinimumHeight = k;
        }
        i += 1;
      }
    }
    
    final int getCapacity()
    {
      return mDrawables.length;
    }
    
    public int getChangingConfigurations()
    {
      return mChangingConfigurations | mChildrenChangingConfigurations;
    }
    
    public final Drawable getChild(int paramInt)
    {
      Drawable localDrawable = mDrawables[paramInt];
      if (localDrawable != null) {
        return localDrawable;
      }
      if (mDrawableFutures != null)
      {
        int i = mDrawableFutures.indexOfKey(paramInt);
        if (i >= 0)
        {
          localDrawable = prepareDrawable(((Drawable.ConstantState)mDrawableFutures.valueAt(i)).newDrawable(mSourceRes));
          mDrawables[paramInt] = localDrawable;
          mDrawableFutures.removeAt(i);
          if (mDrawableFutures.size() != 0) {
            return localDrawable;
          }
          mDrawableFutures = null;
          return localDrawable;
        }
      }
      return null;
      return localDrawable;
    }
    
    public final int getChildCount()
    {
      return mNumChildren;
    }
    
    public final int getConstantHeight()
    {
      if (!mCheckedConstantSize) {
        computeConstantSize();
      }
      return mConstantHeight;
    }
    
    public final int getConstantMinimumHeight()
    {
      if (!mCheckedConstantSize) {
        computeConstantSize();
      }
      return mConstantMinimumHeight;
    }
    
    public final int getConstantMinimumWidth()
    {
      if (!mCheckedConstantSize) {
        computeConstantSize();
      }
      return mConstantMinimumWidth;
    }
    
    public final Rect getConstantPadding()
    {
      if (mVariablePadding) {
        return null;
      }
      if ((mConstantPadding == null) && (!mCheckedPadding))
      {
        createAllFutures();
        Rect localRect = new Rect();
        int j = mNumChildren;
        Drawable[] arrayOfDrawable = mDrawables;
        Object localObject1 = null;
        int i = 0;
        while (i < j)
        {
          Object localObject3 = localObject1;
          if (arrayOfDrawable[i].getPadding(localRect))
          {
            Object localObject2 = localObject1;
            if (localObject1 == null) {
              localObject2 = new Rect(0, 0, 0, 0);
            }
            if (left > left) {
              left = left;
            }
            if (top > top) {
              top = top;
            }
            if (right > right) {
              right = right;
            }
            localObject3 = localObject2;
            if (bottom > bottom)
            {
              bottom = bottom;
              localObject3 = localObject2;
            }
          }
          i += 1;
          localObject1 = localObject3;
        }
        mCheckedPadding = true;
        mConstantPadding = localObject1;
        return localObject1;
      }
      return mConstantPadding;
    }
    
    public final int getConstantWidth()
    {
      if (!mCheckedConstantSize) {
        computeConstantSize();
      }
      return mConstantWidth;
    }
    
    public final int getEnterFadeDuration()
    {
      return mEnterFadeDuration;
    }
    
    public final int getExitFadeDuration()
    {
      return mExitFadeDuration;
    }
    
    public final int getOpacity()
    {
      if (mCheckedOpacity) {
        return mOpacity;
      }
      createAllFutures();
      int m = mNumChildren;
      Drawable[] arrayOfDrawable = mDrawables;
      if (m > 0) {
        i = arrayOfDrawable[0].getOpacity();
      } else {
        i = -2;
      }
      int k = 1;
      int j = i;
      int i = k;
      while (i < m)
      {
        j = Drawable.resolveOpacity(j, arrayOfDrawable[i].getOpacity());
        i += 1;
      }
      mOpacity = j;
      mCheckedOpacity = true;
      return j;
    }
    
    public void growArray(int paramInt1, int paramInt2)
    {
      Drawable[] arrayOfDrawable = new Drawable[paramInt2];
      System.arraycopy(mDrawables, 0, arrayOfDrawable, 0, paramInt1);
      mDrawables = arrayOfDrawable;
    }
    
    void invalidateCache()
    {
      mCheckedOpacity = false;
      mCheckedStateful = false;
    }
    
    public final boolean isConstantSize()
    {
      return mConstantSize;
    }
    
    public final boolean isStateful()
    {
      if (mCheckedStateful) {
        return mStateful;
      }
      createAllFutures();
      int j = mNumChildren;
      Drawable[] arrayOfDrawable = mDrawables;
      boolean bool2 = false;
      int i = 0;
      boolean bool1;
      for (;;)
      {
        bool1 = bool2;
        if (i >= j) {
          break;
        }
        if (arrayOfDrawable[i].isStateful())
        {
          bool1 = true;
          break;
        }
        i += 1;
      }
      mStateful = bool1;
      mCheckedStateful = true;
      return bool1;
    }
    
    void mutate()
    {
      int j = mNumChildren;
      Drawable[] arrayOfDrawable = mDrawables;
      int i = 0;
      while (i < j)
      {
        if (arrayOfDrawable[i] != null) {
          arrayOfDrawable[i].mutate();
        }
        i += 1;
      }
      mMutated = true;
    }
    
    public final void setConstantSize(boolean paramBoolean)
    {
      mConstantSize = paramBoolean;
    }
    
    public final void setEnterFadeDuration(int paramInt)
    {
      mEnterFadeDuration = paramInt;
    }
    
    public final void setExitFadeDuration(int paramInt)
    {
      mExitFadeDuration = paramInt;
    }
    
    final boolean setLayoutDirection(int paramInt1, int paramInt2)
    {
      int j = mNumChildren;
      Drawable[] arrayOfDrawable = mDrawables;
      int i = 0;
      boolean bool3;
      for (boolean bool2 = false; i < j; bool2 = bool3)
      {
        bool3 = bool2;
        if (arrayOfDrawable[i] != null)
        {
          boolean bool1;
          if (Build.VERSION.SDK_INT >= 23) {
            bool1 = arrayOfDrawable[i].setLayoutDirection(paramInt1);
          } else {
            bool1 = false;
          }
          bool3 = bool2;
          if (i == paramInt2) {
            bool3 = bool1;
          }
        }
        i += 1;
      }
      mLayoutDirection = paramInt1;
      return bool2;
    }
    
    public final void setVariablePadding(boolean paramBoolean)
    {
      mVariablePadding = paramBoolean;
    }
    
    final void updateDensity(Resources paramResources)
    {
      if (paramResources != null)
      {
        mSourceRes = paramResources;
        int i = DrawableContainer.resolveDensity(paramResources, mDensity);
        int j = mDensity;
        mDensity = i;
        if (j != i)
        {
          mCheckedConstantSize = false;
          mCheckedPadding = false;
        }
      }
    }
  }
}
