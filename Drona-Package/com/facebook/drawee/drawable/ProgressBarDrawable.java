package com.facebook.drawee.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Path.FillType;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class ProgressBarDrawable
  extends Drawable
  implements CloneableDrawable
{
  private int mBackgroundColor = Integer.MIN_VALUE;
  private int mBarWidth = 20;
  private int mColor = -2147450625;
  private boolean mHideWhenZero = false;
  private boolean mIsVertical = false;
  private int mLevel = 0;
  private int mPadding = 10;
  private final Paint mPaint = new Paint(1);
  private final Path mPath = new Path();
  private int mRadius = 0;
  private final RectF mRect = new RectF();
  
  public ProgressBarDrawable() {}
  
  private void drawBar(Canvas paramCanvas, int paramInt)
  {
    mPaint.setColor(paramInt);
    mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    mPath.reset();
    mPath.setFillType(Path.FillType.EVEN_ODD);
    mPath.addRoundRect(mRect, Math.min(mRadius, mBarWidth / 2), Math.min(mRadius, mBarWidth / 2), Path.Direction.CW);
    paramCanvas.drawPath(mPath, mPaint);
  }
  
  private void drawHorizontalBar(Canvas paramCanvas, int paramInt1, int paramInt2)
  {
    Rect localRect = getBounds();
    paramInt1 = (localRect.width() - mPadding * 2) * paramInt1 / 10000;
    int i = left + mPadding;
    int j = bottom - mPadding - mBarWidth;
    mRect.set(i, j, i + paramInt1, j + mBarWidth);
    drawBar(paramCanvas, paramInt2);
  }
  
  private void drawVerticalBar(Canvas paramCanvas, int paramInt1, int paramInt2)
  {
    Rect localRect = getBounds();
    paramInt1 = (localRect.height() - mPadding * 2) * paramInt1 / 10000;
    int i = left + mPadding;
    int j = top + mPadding;
    mRect.set(i, j, i + mBarWidth, j + paramInt1);
    drawBar(paramCanvas, paramInt2);
  }
  
  public Drawable cloneDrawable()
  {
    ProgressBarDrawable localProgressBarDrawable = new ProgressBarDrawable();
    mBackgroundColor = mBackgroundColor;
    mColor = mColor;
    mPadding = mPadding;
    mBarWidth = mBarWidth;
    mLevel = mLevel;
    mRadius = mRadius;
    mHideWhenZero = mHideWhenZero;
    mIsVertical = mIsVertical;
    return localProgressBarDrawable;
  }
  
  public void draw(Canvas paramCanvas)
  {
    if ((mHideWhenZero) && (mLevel == 0)) {
      return;
    }
    if (mIsVertical)
    {
      drawVerticalBar(paramCanvas, 10000, mBackgroundColor);
      drawVerticalBar(paramCanvas, mLevel, mColor);
      return;
    }
    drawHorizontalBar(paramCanvas, 10000, mBackgroundColor);
    drawHorizontalBar(paramCanvas, mLevel, mColor);
  }
  
  public int getBackgroundColor()
  {
    return mBackgroundColor;
  }
  
  public int getBarWidth()
  {
    return mBarWidth;
  }
  
  public int getColor()
  {
    return mColor;
  }
  
  public boolean getHideWhenZero()
  {
    return mHideWhenZero;
  }
  
  public boolean getIsVertical()
  {
    return mIsVertical;
  }
  
  public int getOpacity()
  {
    return DrawableUtils.getOpacityFromColor(mPaint.getColor());
  }
  
  public boolean getPadding(Rect paramRect)
  {
    paramRect.set(mPadding, mPadding, mPadding, mPadding);
    return mPadding != 0;
  }
  
  public int getRadius()
  {
    return mRadius;
  }
  
  protected boolean onLevelChange(int paramInt)
  {
    mLevel = paramInt;
    invalidateSelf();
    return true;
  }
  
  public void setAlpha(int paramInt)
  {
    mPaint.setAlpha(paramInt);
  }
  
  public void setBackgroundColor(int paramInt)
  {
    if (mBackgroundColor != paramInt)
    {
      mBackgroundColor = paramInt;
      invalidateSelf();
    }
  }
  
  public void setBarWidth(int paramInt)
  {
    if (mBarWidth != paramInt)
    {
      mBarWidth = paramInt;
      invalidateSelf();
    }
  }
  
  public void setColor(int paramInt)
  {
    if (mColor != paramInt)
    {
      mColor = paramInt;
      invalidateSelf();
    }
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    mPaint.setColorFilter(paramColorFilter);
  }
  
  public void setHideWhenZero(boolean paramBoolean)
  {
    mHideWhenZero = paramBoolean;
  }
  
  public void setIsVertical(boolean paramBoolean)
  {
    if (mIsVertical != paramBoolean)
    {
      mIsVertical = paramBoolean;
      invalidateSelf();
    }
  }
  
  public void setPadding(int paramInt)
  {
    if (mPadding != paramInt)
    {
      mPadding = paramInt;
      invalidateSelf();
    }
  }
  
  public void setRadius(int paramInt)
  {
    if (mRadius != paramInt)
    {
      mRadius = paramInt;
      invalidateSelf();
    }
  }
}
