package com.facebook.react.views.image;

import android.graphics.PorterDuff.Mode;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeControllerBuilder;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.BaseViewManager;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.yoga.YogaConstants;
import java.util.Map;

@ReactModule(name="RCTImageView")
public class ReactImageManager
  extends SimpleViewManager<ReactImageView>
{
  public static final String REACT_CLASS = "RCTImageView";
  @Nullable
  private final Object mCallerContext;
  @Nullable
  private AbstractDraweeControllerBuilder mDraweeControllerBuilder;
  @Nullable
  private GlobalImageLoadListener mGlobalImageLoadListener;
  
  public ReactImageManager()
  {
    mDraweeControllerBuilder = null;
    mCallerContext = null;
  }
  
  public ReactImageManager(AbstractDraweeControllerBuilder paramAbstractDraweeControllerBuilder, GlobalImageLoadListener paramGlobalImageLoadListener, Object paramObject)
  {
    mDraweeControllerBuilder = paramAbstractDraweeControllerBuilder;
    mGlobalImageLoadListener = paramGlobalImageLoadListener;
    mCallerContext = paramObject;
  }
  
  public ReactImageManager(AbstractDraweeControllerBuilder paramAbstractDraweeControllerBuilder, Object paramObject)
  {
    this(paramAbstractDraweeControllerBuilder, null, paramObject);
  }
  
  public ReactImageView createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    return new ReactImageView(paramThemedReactContext, getDraweeControllerBuilder(), mGlobalImageLoadListener, getCallerContext());
  }
  
  public Object getCallerContext()
  {
    return mCallerContext;
  }
  
  public AbstractDraweeControllerBuilder getDraweeControllerBuilder()
  {
    if (mDraweeControllerBuilder == null) {
      mDraweeControllerBuilder = Fresco.newDraweeControllerBuilder();
    }
    return mDraweeControllerBuilder;
  }
  
  public Map getExportedCustomDirectEventTypeConstants()
  {
    return MapBuilder.get(ImageLoadEvent.eventNameForType(4), MapBuilder.get("registrationName", "onLoadStart"), ImageLoadEvent.eventNameForType(2), MapBuilder.get("registrationName", "onLoad"), ImageLoadEvent.eventNameForType(1), MapBuilder.get("registrationName", "onError"), ImageLoadEvent.eventNameForType(3), MapBuilder.get("registrationName", "onLoadEnd"));
  }
  
  public String getName()
  {
    return "RCTImageView";
  }
  
  protected void onAfterUpdateTransaction(ReactImageView paramReactImageView)
  {
    super.onAfterUpdateTransaction(paramReactImageView);
    paramReactImageView.maybeUpdateView();
  }
  
  public void setBlurRadius(ReactImageView paramReactImageView, float paramFloat)
  {
    paramReactImageView.setBlurRadius(paramFloat);
  }
  
  public void setBorderColor(ReactImageView paramReactImageView, Integer paramInteger)
  {
    if (paramInteger == null)
    {
      paramReactImageView.setBorderColor(0);
      return;
    }
    paramReactImageView.setBorderColor(paramInteger.intValue());
  }
  
  public void setBorderRadius(ReactImageView paramReactImageView, int paramInt, float paramFloat)
  {
    float f = paramFloat;
    if (!YogaConstants.isUndefined(paramFloat)) {
      f = PixelUtil.toPixelFromDIP(paramFloat);
    }
    if (paramInt == 0)
    {
      paramReactImageView.setBorderRadius(f);
      return;
    }
    paramReactImageView.setBorderRadius(f, paramInt - 1);
  }
  
  public void setBorderWidth(ReactImageView paramReactImageView, float paramFloat)
  {
    paramReactImageView.setBorderWidth(paramFloat);
  }
  
  public void setDefaultSource(ReactImageView paramReactImageView, String paramString)
  {
    paramReactImageView.setDefaultSource(paramString);
  }
  
  public void setFadeDuration(ReactImageView paramReactImageView, int paramInt)
  {
    paramReactImageView.setFadeDuration(paramInt);
  }
  
  public void setHeaders(ReactImageView paramReactImageView, ReadableMap paramReadableMap)
  {
    paramReactImageView.setHeaders(paramReadableMap);
  }
  
  public void setLoadHandlersRegistered(ReactImageView paramReactImageView, boolean paramBoolean)
  {
    paramReactImageView.setShouldNotifyLoadEvents(paramBoolean);
  }
  
  public void setLoadingIndicatorSource(ReactImageView paramReactImageView, String paramString)
  {
    paramReactImageView.setLoadingIndicatorSource(paramString);
  }
  
  public void setOverlayColor(ReactImageView paramReactImageView, Integer paramInteger)
  {
    if (paramInteger == null)
    {
      paramReactImageView.setOverlayColor(0);
      return;
    }
    paramReactImageView.setOverlayColor(paramInteger.intValue());
  }
  
  public void setProgressiveRenderingEnabled(ReactImageView paramReactImageView, boolean paramBoolean)
  {
    paramReactImageView.setProgressiveRenderingEnabled(paramBoolean);
  }
  
  public void setResizeMethod(ReactImageView paramReactImageView, String paramString)
  {
    if ((paramString != null) && (!"auto".equals(paramString)))
    {
      if ("resize".equals(paramString))
      {
        paramReactImageView.setResizeMethod(ImageResizeMethod.RESIZE);
        return;
      }
      if ("scale".equals(paramString))
      {
        paramReactImageView.setResizeMethod(ImageResizeMethod.SCALE);
        return;
      }
      paramReactImageView = new StringBuilder();
      paramReactImageView.append("Invalid resize method: '");
      paramReactImageView.append(paramString);
      paramReactImageView.append("'");
      throw new JSApplicationIllegalArgumentException(paramReactImageView.toString());
    }
    paramReactImageView.setResizeMethod(ImageResizeMethod.AUTO);
  }
  
  public void setResizeMode(ReactImageView paramReactImageView, String paramString)
  {
    paramReactImageView.setScaleType(ImageResizeMode.toScaleType(paramString));
    paramReactImageView.setTileMode(ImageResizeMode.toTileMode(paramString));
  }
  
  public void setSource(ReactImageView paramReactImageView, ReadableArray paramReadableArray)
  {
    paramReactImageView.setSource(paramReadableArray);
  }
  
  public void setTintColor(ReactImageView paramReactImageView, Integer paramInteger)
  {
    if (paramInteger == null)
    {
      paramReactImageView.clearColorFilter();
      return;
    }
    paramReactImageView.setColorFilter(paramInteger.intValue(), PorterDuff.Mode.SRC_IN);
  }
}
