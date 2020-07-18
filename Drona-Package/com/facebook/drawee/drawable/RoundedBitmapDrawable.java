package com.facebook.drawee.drawable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import java.lang.ref.WeakReference;
import javax.annotation.Nullable;

public class RoundedBitmapDrawable
  extends RoundedDrawable
{
  @Nullable
  private final Bitmap mBitmap;
  private final Paint mBorderPaint = new Paint(1);
  private WeakReference<Bitmap> mLastBitmap;
  private final Paint mPaint = new Paint();
  
  public RoundedBitmapDrawable(Resources paramResources, Bitmap paramBitmap)
  {
    this(paramResources, paramBitmap, null);
  }
  
  public RoundedBitmapDrawable(Resources paramResources, Bitmap paramBitmap, Paint paramPaint)
  {
    super(new BitmapDrawable(paramResources, paramBitmap));
    mBitmap = paramBitmap;
    if (paramPaint != null) {
      mPaint.set(paramPaint);
    }
    mPaint.setFlags(1);
    mBorderPaint.setStyle(Paint.Style.STROKE);
  }
  
  public static RoundedBitmapDrawable fromBitmapDrawable(Resources paramResources, BitmapDrawable paramBitmapDrawable)
  {
    return new RoundedBitmapDrawable(paramResources, paramBitmapDrawable.getBitmap(), paramBitmapDrawable.getPaint());
  }
  
  private void updatePaint()
  {
    if ((mLastBitmap == null) || (mLastBitmap.get() != mBitmap))
    {
      mLastBitmap = new WeakReference(mBitmap);
      mPaint.setShader(new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
      mIsShaderTransformDirty = true;
    }
    if (mIsShaderTransformDirty)
    {
      mPaint.getShader().setLocalMatrix(mTransform);
      mIsShaderTransformDirty = false;
    }
    mPaint.setFilterBitmap(getPaintFilterBitmap());
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("RoundedBitmapDrawable#draw");
    }
    if (!shouldRound())
    {
      super.draw(paramCanvas);
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
    }
    else
    {
      updateTransform();
      updatePath();
      updatePaint();
      int i = paramCanvas.save();
      paramCanvas.concat(mInverseParentTransform);
      paramCanvas.drawPath(mPath, mPaint);
      if (mBorderWidth > 0.0F)
      {
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setColor(DrawableUtils.multiplyColorAlpha(mBorderColor, mPaint.getAlpha()));
        paramCanvas.drawPath(mBorderPath, mBorderPaint);
      }
      paramCanvas.restoreToCount(i);
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
    }
  }
  
  Paint getPaint()
  {
    return mPaint;
  }
  
  public void setAlpha(int paramInt)
  {
    super.setAlpha(paramInt);
    if (paramInt != mPaint.getAlpha())
    {
      mPaint.setAlpha(paramInt);
      super.setAlpha(paramInt);
      invalidateSelf();
    }
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    super.setColorFilter(paramColorFilter);
    mPaint.setColorFilter(paramColorFilter);
  }
  
  boolean shouldRound()
  {
    return (super.shouldRound()) && (mBitmap != null);
  }
}