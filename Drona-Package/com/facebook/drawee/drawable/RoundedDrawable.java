package com.facebook.drawee.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Path.FillType;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import java.util.Arrays;

public abstract class RoundedDrawable
  extends Drawable
  implements Rounded, TransformAwareDrawable
{
  @VisibleForTesting
  final RectF mBitmapBounds = new RectF();
  protected int mBorderColor = 0;
  protected final Path mBorderPath = new Path();
  @VisibleForTesting
  final float[] mBorderRadii = new float[8];
  protected float mBorderWidth = 0.0F;
  @VisibleForTesting
  final Matrix mBoundsTransform = new Matrix();
  private final float[] mCornerRadii = new float[8];
  private final Drawable mDelegate;
  @VisibleForTesting
  final RectF mDrawableBounds = new RectF();
  @Nullable
  @VisibleForTesting
  RectF mInsideBorderBounds;
  @Nullable
  @VisibleForTesting
  float[] mInsideBorderRadii;
  @Nullable
  @VisibleForTesting
  Matrix mInsideBorderTransform;
  @VisibleForTesting
  final Matrix mInverseParentTransform = new Matrix();
  protected boolean mIsCircle = false;
  private boolean mIsPathDirty = true;
  protected boolean mIsShaderTransformDirty = true;
  private float mPadding = 0.0F;
  private boolean mPaintFilterBitmap = false;
  @VisibleForTesting
  final Matrix mParentTransform = new Matrix();
  protected final Path mPath = new Path();
  @VisibleForTesting
  final Matrix mPrevBoundsTransform = new Matrix();
  @Nullable
  @VisibleForTesting
  Matrix mPrevInsideBorderTransform;
  @VisibleForTesting
  final Matrix mPrevParentTransform = new Matrix();
  @VisibleForTesting
  final RectF mPrevRootBounds = new RectF();
  protected boolean mRadiiNonZero = false;
  @VisibleForTesting
  final RectF mRootBounds = new RectF();
  private boolean mScaleDownInsideBorders = false;
  @VisibleForTesting
  final Matrix mTransform = new Matrix();
  @Nullable
  private TransformCallback mTransformCallback;
  
  RoundedDrawable(Drawable paramDrawable)
  {
    mDelegate = paramDrawable;
  }
  
  public void clearColorFilter()
  {
    mDelegate.clearColorFilter();
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("RoundedDrawable#draw");
    }
    mDelegate.draw(paramCanvas);
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
  }
  
  public int getAlpha()
  {
    return mDelegate.getAlpha();
  }
  
  public int getBorderColor()
  {
    return mBorderColor;
  }
  
  public float getBorderWidth()
  {
    return mBorderWidth;
  }
  
  public ColorFilter getColorFilter()
  {
    return mDelegate.getColorFilter();
  }
  
  public int getIntrinsicHeight()
  {
    return mDelegate.getIntrinsicHeight();
  }
  
  public int getIntrinsicWidth()
  {
    return mDelegate.getIntrinsicWidth();
  }
  
  public int getOpacity()
  {
    return mDelegate.getOpacity();
  }
  
  public float getPadding()
  {
    return mPadding;
  }
  
  public boolean getPaintFilterBitmap()
  {
    return mPaintFilterBitmap;
  }
  
  public float[] getRadii()
  {
    return mCornerRadii;
  }
  
  public boolean getScaleDownInsideBorders()
  {
    return mScaleDownInsideBorders;
  }
  
  public boolean isCircle()
  {
    return mIsCircle;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    mDelegate.setBounds(paramRect);
  }
  
  public void setAlpha(int paramInt)
  {
    mDelegate.setAlpha(paramInt);
  }
  
  public void setBorder(int paramInt, float paramFloat)
  {
    if ((mBorderColor != paramInt) || (mBorderWidth != paramFloat))
    {
      mBorderColor = paramInt;
      mBorderWidth = paramFloat;
      mIsPathDirty = true;
      invalidateSelf();
    }
  }
  
  public void setCircle(boolean paramBoolean)
  {
    mIsCircle = paramBoolean;
    mIsPathDirty = true;
    invalidateSelf();
  }
  
  public void setColorFilter(int paramInt, PorterDuff.Mode paramMode)
  {
    mDelegate.setColorFilter(paramInt, paramMode);
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    mDelegate.setColorFilter(paramColorFilter);
  }
  
  public void setPadding(float paramFloat)
  {
    if (mPadding != paramFloat)
    {
      mPadding = paramFloat;
      mIsPathDirty = true;
      invalidateSelf();
    }
  }
  
  public void setPaintFilterBitmap(boolean paramBoolean)
  {
    if (mPaintFilterBitmap != paramBoolean)
    {
      mPaintFilterBitmap = paramBoolean;
      invalidateSelf();
    }
  }
  
  public void setRadii(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat == null)
    {
      Arrays.fill(mCornerRadii, 0.0F);
      mRadiiNonZero = false;
    }
    else
    {
      boolean bool2;
      if (paramArrayOfFloat.length == 8) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      Preconditions.checkArgument(bool2, "radii should have exactly 8 values");
      System.arraycopy(paramArrayOfFloat, 0, mCornerRadii, 0, 8);
      mRadiiNonZero = false;
      int i = 0;
      while (i < 8)
      {
        bool2 = mRadiiNonZero;
        boolean bool1;
        if (paramArrayOfFloat[i] > 0.0F) {
          bool1 = true;
        } else {
          bool1 = false;
        }
        mRadiiNonZero = (bool2 | bool1);
        i += 1;
      }
    }
    mIsPathDirty = true;
    invalidateSelf();
  }
  
  public void setRadius(float paramFloat)
  {
    boolean bool3 = false;
    boolean bool1 = paramFloat < 0.0F;
    if (!bool1) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    Preconditions.checkState(bool2);
    Arrays.fill(mCornerRadii, paramFloat);
    boolean bool2 = bool3;
    if (bool1) {
      bool2 = true;
    }
    mRadiiNonZero = bool2;
    mIsPathDirty = true;
    invalidateSelf();
  }
  
  public void setScaleDownInsideBorders(boolean paramBoolean)
  {
    if (mScaleDownInsideBorders != paramBoolean)
    {
      mScaleDownInsideBorders = paramBoolean;
      mIsPathDirty = true;
      invalidateSelf();
    }
  }
  
  public void setTransformCallback(TransformCallback paramTransformCallback)
  {
    mTransformCallback = paramTransformCallback;
  }
  
  boolean shouldRound()
  {
    return (mIsCircle) || (mRadiiNonZero) || (mBorderWidth > 0.0F);
  }
  
  protected void updatePath()
  {
    if (mIsPathDirty)
    {
      mBorderPath.reset();
      mRootBounds.inset(mBorderWidth / 2.0F, mBorderWidth / 2.0F);
      int i;
      if (mIsCircle)
      {
        f1 = Math.min(mRootBounds.width(), mRootBounds.height()) / 2.0F;
        mBorderPath.addCircle(mRootBounds.centerX(), mRootBounds.centerY(), f1, Path.Direction.CW);
      }
      else
      {
        i = 0;
        while (i < mBorderRadii.length)
        {
          mBorderRadii[i] = (mCornerRadii[i] + mPadding - mBorderWidth / 2.0F);
          i += 1;
        }
        mBorderPath.addRoundRect(mRootBounds, mBorderRadii, Path.Direction.CW);
      }
      mRootBounds.inset(-mBorderWidth / 2.0F, -mBorderWidth / 2.0F);
      mPath.reset();
      float f2 = mPadding;
      if (mScaleDownInsideBorders) {
        f1 = mBorderWidth;
      } else {
        f1 = 0.0F;
      }
      float f1 = f2 + f1;
      mRootBounds.inset(f1, f1);
      boolean bool = mIsCircle;
      RoundedDrawable localRoundedDrawable = this;
      if (bool)
      {
        mPath.addCircle(mRootBounds.centerX(), mRootBounds.centerY(), Math.min(mRootBounds.width(), mRootBounds.height()) / 2.0F, Path.Direction.CW);
      }
      else
      {
        bool = mScaleDownInsideBorders;
        if (bool)
        {
          if (mInsideBorderRadii == null) {
            mInsideBorderRadii = new float[8];
          }
          i = 0;
          while (i < mBorderRadii.length)
          {
            mInsideBorderRadii[i] = (mCornerRadii[i] - mBorderWidth);
            i += 1;
          }
          mPath.addRoundRect(mRootBounds, mInsideBorderRadii, Path.Direction.CW);
        }
        else
        {
          mPath.addRoundRect(mRootBounds, mCornerRadii, Path.Direction.CW);
        }
      }
      localRoundedDrawable = this;
      RectF localRectF = mRootBounds;
      f1 = -f1;
      localRectF.inset(f1, f1);
      mPath.setFillType(Path.FillType.WINDING);
      mIsPathDirty = false;
    }
  }
  
  protected void updateTransform()
  {
    if (mTransformCallback != null)
    {
      mTransformCallback.getTransform(mParentTransform);
      mTransformCallback.getRootBounds(mRootBounds);
    }
    else
    {
      mParentTransform.reset();
      mRootBounds.set(getBounds());
    }
    mBitmapBounds.set(0.0F, 0.0F, getIntrinsicWidth(), getIntrinsicHeight());
    mDrawableBounds.set(mDelegate.getBounds());
    mBoundsTransform.setRectToRect(mBitmapBounds, mDrawableBounds, Matrix.ScaleToFit.FILL);
    if (mScaleDownInsideBorders)
    {
      if (mInsideBorderBounds == null) {
        mInsideBorderBounds = new RectF(mRootBounds);
      } else {
        mInsideBorderBounds.set(mRootBounds);
      }
      mInsideBorderBounds.inset(mBorderWidth, mBorderWidth);
      if (mInsideBorderTransform == null) {
        mInsideBorderTransform = new Matrix();
      }
      mInsideBorderTransform.setRectToRect(mRootBounds, mInsideBorderBounds, Matrix.ScaleToFit.FILL);
    }
    else if (mInsideBorderTransform != null)
    {
      mInsideBorderTransform.reset();
    }
    if ((!mParentTransform.equals(mPrevParentTransform)) || (!mBoundsTransform.equals(mPrevBoundsTransform)) || ((mInsideBorderTransform != null) && (!mInsideBorderTransform.equals(mPrevInsideBorderTransform))))
    {
      mIsShaderTransformDirty = true;
      mParentTransform.invert(mInverseParentTransform);
      mTransform.set(mParentTransform);
      if (mScaleDownInsideBorders) {
        mTransform.postConcat(mInsideBorderTransform);
      }
      mTransform.preConcat(mBoundsTransform);
      mPrevParentTransform.set(mParentTransform);
      mPrevBoundsTransform.set(mBoundsTransform);
      if (mScaleDownInsideBorders)
      {
        if (mPrevInsideBorderTransform == null) {
          mPrevInsideBorderTransform = new Matrix(mInsideBorderTransform);
        } else {
          mPrevInsideBorderTransform.set(mInsideBorderTransform);
        }
      }
      else if (mPrevInsideBorderTransform != null) {
        mPrevInsideBorderTransform.reset();
      }
    }
    if (!mRootBounds.equals(mPrevRootBounds))
    {
      mIsPathDirty = true;
      mPrevRootBounds.set(mRootBounds);
    }
  }
}
