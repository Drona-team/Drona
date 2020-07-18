package com.facebook.drawee.drawable;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import com.facebook.common.internal.VisibleForTesting;

public class OrientedDrawable
  extends ForwardingDrawable
{
  private int mExifOrientation;
  private int mRotationAngle;
  @VisibleForTesting
  final Matrix mRotationMatrix = new Matrix();
  private final Matrix mTempMatrix = new Matrix();
  private final RectF mTempRectF = new RectF();
  
  public OrientedDrawable(Drawable paramDrawable, int paramInt)
  {
    this(paramDrawable, paramInt, 0);
  }
  
  public OrientedDrawable(Drawable paramDrawable, int paramInt1, int paramInt2)
  {
    super(paramDrawable);
    mRotationAngle = (paramInt1 - paramInt1 % 90);
    if ((paramInt2 < 0) || (paramInt2 > 8)) {
      paramInt2 = 0;
    }
    mExifOrientation = paramInt2;
  }
  
  public void draw(Canvas paramCanvas)
  {
    if ((mRotationAngle <= 0) && ((mExifOrientation == 0) || (mExifOrientation == 1)))
    {
      super.draw(paramCanvas);
      return;
    }
    int i = paramCanvas.save();
    paramCanvas.concat(mRotationMatrix);
    super.draw(paramCanvas);
    paramCanvas.restoreToCount(i);
  }
  
  public int getIntrinsicHeight()
  {
    if ((mExifOrientation != 5) && (mExifOrientation != 7) && (mRotationAngle % 180 == 0)) {
      return super.getIntrinsicHeight();
    }
    return super.getIntrinsicWidth();
  }
  
  public int getIntrinsicWidth()
  {
    if ((mExifOrientation != 5) && (mExifOrientation != 7) && (mRotationAngle % 180 == 0)) {
      return super.getIntrinsicWidth();
    }
    return super.getIntrinsicHeight();
  }
  
  public void getTransform(Matrix paramMatrix)
  {
    getParentTransform(paramMatrix);
    if (!mRotationMatrix.isIdentity()) {
      paramMatrix.preConcat(mRotationMatrix);
    }
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    Drawable localDrawable = getCurrent();
    if ((mRotationAngle <= 0) && ((mExifOrientation == 0) || (mExifOrientation == 1)))
    {
      localDrawable.setBounds(paramRect);
      return;
    }
    int i = mExifOrientation;
    if (i != 2)
    {
      if (i != 7)
      {
        switch (i)
        {
        default: 
          mRotationMatrix.setRotate(mRotationAngle, paramRect.centerX(), paramRect.centerY());
          break;
        case 5: 
          mRotationMatrix.setRotate(270.0F, paramRect.centerX(), paramRect.centerY());
          mRotationMatrix.postScale(1.0F, -1.0F);
          break;
        case 4: 
          mRotationMatrix.setScale(1.0F, -1.0F);
          break;
        }
      }
      else
      {
        mRotationMatrix.setRotate(270.0F, paramRect.centerX(), paramRect.centerY());
        mRotationMatrix.postScale(-1.0F, 1.0F);
      }
    }
    else {
      mRotationMatrix.setScale(-1.0F, 1.0F);
    }
    mTempMatrix.reset();
    mRotationMatrix.invert(mTempMatrix);
    mTempRectF.set(paramRect);
    mTempMatrix.mapRect(mTempRectF);
    localDrawable.setBounds((int)mTempRectF.left, (int)mTempRectF.top, (int)mTempRectF.right, (int)mTempRectF.bottom);
  }
}
