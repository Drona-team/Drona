package com.facebook.react.views.text.frescosupport;

import android.view.View;
import androidx.annotation.Nullable;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeControllerBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewManager;

@ReactModule(name="RCTTextInlineImage")
public class FrescoBasedReactTextInlineImageViewManager
  extends ViewManager<View, FrescoBasedReactTextInlineImageShadowNode>
{
  public static final String REACT_CLASS = "RCTTextInlineImage";
  @Nullable
  private final Object mCallerContext;
  @Nullable
  private final AbstractDraweeControllerBuilder mDraweeControllerBuilder;
  
  public FrescoBasedReactTextInlineImageViewManager()
  {
    this(null, null);
  }
  
  public FrescoBasedReactTextInlineImageViewManager(AbstractDraweeControllerBuilder paramAbstractDraweeControllerBuilder, Object paramObject)
  {
    mDraweeControllerBuilder = paramAbstractDraweeControllerBuilder;
    mCallerContext = paramObject;
  }
  
  public FrescoBasedReactTextInlineImageShadowNode createShadowNodeInstance()
  {
    Object localObject;
    if (mDraweeControllerBuilder != null) {
      localObject = mDraweeControllerBuilder;
    } else {
      localObject = Fresco.newDraweeControllerBuilder();
    }
    return new FrescoBasedReactTextInlineImageShadowNode((AbstractDraweeControllerBuilder)localObject, mCallerContext);
  }
  
  public View createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    throw new IllegalStateException("RCTTextInlineImage doesn't map into a native view");
  }
  
  public String getName()
  {
    return "RCTTextInlineImage";
  }
  
  public Class getShadowNodeClass()
  {
    return FrescoBasedReactTextInlineImageShadowNode.class;
  }
  
  public void updateExtraData(View paramView, Object paramObject) {}
}