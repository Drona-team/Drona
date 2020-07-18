package com.facebook.react.views.text.frescosupport;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.facebook.drawee.controller.AbstractDraweeControllerBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.fresco.ReactNetworkImageRequest;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.views.text.TextInlineImageSpan;

public class FrescoBasedReactTextInlineImageSpan
  extends TextInlineImageSpan
{
  @Nullable
  private final Object mCallerContext;
  @Nullable
  private Drawable mDrawable;
  private final AbstractDraweeControllerBuilder mDraweeControllerBuilder;
  private final DraweeHolder<GenericDraweeHierarchy> mDraweeHolder;
  private ReadableMap mHeaders;
  private int mHeight;
  @Nullable
  private TextView mTextView;
  private int mTintColor;
  private Uri mUri;
  private int mWidth;
  
  public FrescoBasedReactTextInlineImageSpan(Resources paramResources, int paramInt1, int paramInt2, int paramInt3, Uri paramUri, ReadableMap paramReadableMap, AbstractDraweeControllerBuilder paramAbstractDraweeControllerBuilder, Object paramObject)
  {
    mDraweeHolder = new DraweeHolder(GenericDraweeHierarchyBuilder.newInstance(paramResources).build());
    mDraweeControllerBuilder = paramAbstractDraweeControllerBuilder;
    mCallerContext = paramObject;
    mTintColor = paramInt3;
    if (paramUri == null) {
      paramUri = Uri.EMPTY;
    }
    mUri = paramUri;
    mHeaders = paramReadableMap;
    mWidth = ((int)PixelUtil.toPixelFromDIP(paramInt2));
    mHeight = ((int)PixelUtil.toPixelFromDIP(paramInt1));
  }
  
  public void draw(Canvas paramCanvas, CharSequence paramCharSequence, int paramInt1, int paramInt2, float paramFloat, int paramInt3, int paramInt4, int paramInt5, Paint paramPaint)
  {
    if (mDrawable == null)
    {
      paramCharSequence = ReactNetworkImageRequest.fromBuilderWithHeaders(ImageRequestBuilder.newBuilderWithSource(mUri), mHeaders);
      paramCharSequence = mDraweeControllerBuilder.reset().setOldController(mDraweeHolder.getController()).setCallerContext(mCallerContext).setImageRequest(paramCharSequence).build();
      mDraweeHolder.setController(paramCharSequence);
      mDraweeControllerBuilder.reset();
      mDrawable = mDraweeHolder.getTopLevelDrawable();
      mDrawable.setBounds(0, 0, mWidth, mHeight);
      if (mTintColor != 0) {
        mDrawable.setColorFilter(mTintColor, PorterDuff.Mode.SRC_IN);
      }
      mDrawable.setCallback(mTextView);
    }
    paramCanvas.save();
    paramInt1 = (int)(paramPaint.descent() - paramPaint.ascent());
    paramCanvas.translate(paramFloat, paramInt4 + (int)paramPaint.descent() - paramInt1 / 2 - (mDrawable.getBounds().bottom - mDrawable.getBounds().top) / 2);
    mDrawable.draw(paramCanvas);
    paramCanvas.restore();
  }
  
  public Drawable getDrawable()
  {
    return mDrawable;
  }
  
  public int getHeight()
  {
    return mHeight;
  }
  
  public int getSize(Paint paramPaint, CharSequence paramCharSequence, int paramInt1, int paramInt2, Paint.FontMetricsInt paramFontMetricsInt)
  {
    if (paramFontMetricsInt != null)
    {
      ascent = (-mHeight);
      descent = 0;
      top = ascent;
      bottom = 0;
    }
    return mWidth;
  }
  
  public int getWidth()
  {
    return mWidth;
  }
  
  public void onAttachedToWindow()
  {
    mDraweeHolder.onAttach();
  }
  
  public void onDetachedFromWindow()
  {
    mDraweeHolder.onDetach();
  }
  
  public void onFinishTemporaryDetach()
  {
    mDraweeHolder.onAttach();
  }
  
  public void onStartTemporaryDetach()
  {
    mDraweeHolder.onDetach();
  }
  
  public void setTextView(TextView paramTextView)
  {
    mTextView = paramTextView;
  }
}
