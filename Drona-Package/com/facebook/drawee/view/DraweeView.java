package com.facebook.drawee.view;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import com.facebook.common.internal.Objects;
import com.facebook.common.internal.Objects.ToStringHelper;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.interfaces.DraweeHierarchy;
import com.facebook.imagepipeline.systrace.FrescoSystrace;

public class DraweeView<DH extends DraweeHierarchy>
  extends ImageView
{
  private static boolean sGlobalLegacyVisibilityHandlingEnabled;
  private float mAspectRatio = 0.0F;
  private DraweeHolder<DH> mDraweeHolder;
  private boolean mInitialised = false;
  private boolean mLegacyVisibilityHandlingEnabled = false;
  private final AspectRatioMeasure.Spec mMeasureSpec = new AspectRatioMeasure.Spec();
  
  public DraweeView(Context paramContext)
  {
    super(paramContext);
    init(paramContext);
  }
  
  public DraweeView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramContext);
  }
  
  public DraweeView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramContext);
  }
  
  public DraweeView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    init(paramContext);
  }
  
  private void init(Context paramContext)
  {
    try
    {
      boolean bool1 = FrescoSystrace.isTracing();
      if (bool1) {
        FrescoSystrace.beginSection("DraweeView#init");
      }
      bool1 = mInitialised;
      if (bool1)
      {
        if (FrescoSystrace.isTracing()) {
          FrescoSystrace.endSection();
        }
      }
      else
      {
        bool1 = true;
        mInitialised = true;
        mDraweeHolder = DraweeHolder.create(null, paramContext);
        int i = Build.VERSION.SDK_INT;
        if (i >= 21)
        {
          ColorStateList localColorStateList = getImageTintList();
          if (localColorStateList == null)
          {
            if (FrescoSystrace.isTracing()) {
              FrescoSystrace.endSection();
            }
          }
          else {
            setColorFilter(localColorStateList.getDefaultColor());
          }
        }
        else
        {
          boolean bool2 = sGlobalLegacyVisibilityHandlingEnabled;
          if (bool2)
          {
            i = getApplicationInfotargetSdkVersion;
            if (i >= 24) {}
          }
          else
          {
            bool1 = false;
          }
          mLegacyVisibilityHandlingEnabled = bool1;
          if (FrescoSystrace.isTracing())
          {
            FrescoSystrace.endSection();
            return;
          }
        }
      }
    }
    catch (Throwable paramContext)
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      throw paramContext;
    }
  }
  
  private void maybeOverrideVisibilityHandling()
  {
    if (mLegacyVisibilityHandlingEnabled)
    {
      Drawable localDrawable = getDrawable();
      if (localDrawable != null)
      {
        boolean bool;
        if (getVisibility() == 0) {
          bool = true;
        } else {
          bool = false;
        }
        localDrawable.setVisible(bool, false);
      }
    }
  }
  
  public static void setGlobalLegacyVisibilityHandlingEnabled(boolean paramBoolean)
  {
    sGlobalLegacyVisibilityHandlingEnabled = paramBoolean;
  }
  
  protected void doAttach()
  {
    mDraweeHolder.onAttach();
  }
  
  protected void doDetach()
  {
    mDraweeHolder.onDetach();
  }
  
  public float getAspectRatio()
  {
    return mAspectRatio;
  }
  
  public DraweeController getController()
  {
    return mDraweeHolder.getController();
  }
  
  public DraweeHierarchy getHierarchy()
  {
    return mDraweeHolder.getHierarchy();
  }
  
  public Drawable getTopLevelDrawable()
  {
    return mDraweeHolder.getTopLevelDrawable();
  }
  
  public boolean hasController()
  {
    return mDraweeHolder.getController() != null;
  }
  
  public boolean hasHierarchy()
  {
    return mDraweeHolder.hasHierarchy();
  }
  
  protected void onAttach()
  {
    doAttach();
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    maybeOverrideVisibilityHandling();
    onAttach();
  }
  
  protected void onDetach()
  {
    doDetach();
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    maybeOverrideVisibilityHandling();
    onDetach();
  }
  
  public void onFinishTemporaryDetach()
  {
    super.onFinishTemporaryDetach();
    maybeOverrideVisibilityHandling();
    onAttach();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    mMeasureSpec.width = paramInt1;
    mMeasureSpec.height = paramInt2;
    AspectRatioMeasure.updateMeasureSpec(mMeasureSpec, mAspectRatio, getLayoutParams(), getPaddingLeft() + getPaddingRight(), getPaddingTop() + getPaddingBottom());
    super.onMeasure(mMeasureSpec.width, mMeasureSpec.height);
  }
  
  public void onStartTemporaryDetach()
  {
    super.onStartTemporaryDetach();
    maybeOverrideVisibilityHandling();
    onDetach();
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (mDraweeHolder.onTouchEvent(paramMotionEvent)) {
      return true;
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  protected void onVisibilityChanged(View paramView, int paramInt)
  {
    super.onVisibilityChanged(paramView, paramInt);
    maybeOverrideVisibilityHandling();
  }
  
  public void setAspectRatio(float paramFloat)
  {
    if (paramFloat == mAspectRatio) {
      return;
    }
    mAspectRatio = paramFloat;
    requestLayout();
  }
  
  public void setController(DraweeController paramDraweeController)
  {
    mDraweeHolder.setController(paramDraweeController);
    super.setImageDrawable(mDraweeHolder.getTopLevelDrawable());
  }
  
  public void setHierarchy(DraweeHierarchy paramDraweeHierarchy)
  {
    mDraweeHolder.setHierarchy(paramDraweeHierarchy);
    super.setImageDrawable(mDraweeHolder.getTopLevelDrawable());
  }
  
  public void setImageBitmap(Bitmap paramBitmap)
  {
    init(getContext());
    mDraweeHolder.setController(null);
    super.setImageBitmap(paramBitmap);
  }
  
  public void setImageDrawable(Drawable paramDrawable)
  {
    init(getContext());
    mDraweeHolder.setController(null);
    super.setImageDrawable(paramDrawable);
  }
  
  public void setImageResource(int paramInt)
  {
    init(getContext());
    mDraweeHolder.setController(null);
    super.setImageResource(paramInt);
  }
  
  public void setImageURI(Uri paramUri)
  {
    init(getContext());
    mDraweeHolder.setController(null);
    super.setImageURI(paramUri);
  }
  
  public void setLegacyVisibilityHandlingEnabled(boolean paramBoolean)
  {
    mLegacyVisibilityHandlingEnabled = paramBoolean;
  }
  
  public String toString()
  {
    Objects.ToStringHelper localToStringHelper = Objects.toStringHelper(this);
    String str;
    if (mDraweeHolder != null) {
      str = mDraweeHolder.toString();
    } else {
      str = "<no holder set>";
    }
    return localToStringHelper.addValue("holder", str).toString();
  }
}