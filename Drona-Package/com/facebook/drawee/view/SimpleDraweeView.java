package com.facebook.drawee.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Supplier;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.R.styleable;
import com.facebook.drawee.controller.AbstractDraweeControllerBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.SimpleDraweeControllerBuilder;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.systrace.FrescoSystrace;

public class SimpleDraweeView
  extends GenericDraweeView
{
  private static Supplier<? extends AbstractDraweeControllerBuilder> sDraweecontrollerbuildersupplier;
  private AbstractDraweeControllerBuilder mControllerBuilder;
  
  public SimpleDraweeView(Context paramContext)
  {
    super(paramContext);
    init(paramContext, null);
  }
  
  public SimpleDraweeView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init(paramContext, paramAttributeSet);
  }
  
  public SimpleDraweeView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramContext, paramAttributeSet);
  }
  
  public SimpleDraweeView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    init(paramContext, paramAttributeSet);
  }
  
  public SimpleDraweeView(Context paramContext, GenericDraweeHierarchy paramGenericDraweeHierarchy)
  {
    super(paramContext, paramGenericDraweeHierarchy);
    init(paramContext, null);
  }
  
  private void init(Context paramContext, AttributeSet paramAttributeSet)
  {
    try
    {
      boolean bool = FrescoSystrace.isTracing();
      if (bool) {
        FrescoSystrace.beginSection("SimpleDraweeView#init");
      }
      bool = isInEditMode();
      if (bool)
      {
        getTopLevelDrawable().setVisible(true, false);
        getTopLevelDrawable().invalidateSelf();
      }
      else
      {
        Preconditions.checkNotNull(sDraweecontrollerbuildersupplier, "SimpleDraweeView was not initialized!");
        mControllerBuilder = ((AbstractDraweeControllerBuilder)sDraweecontrollerbuildersupplier.getFolder());
      }
      if (paramAttributeSet != null)
      {
        paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SimpleDraweeView);
        try
        {
          bool = paramContext.hasValue(R.styleable.SimpleDraweeView_actualImageUri);
          if (bool)
          {
            setImageURI(Uri.parse(paramContext.getString(R.styleable.SimpleDraweeView_actualImageUri)), null);
          }
          else
          {
            bool = paramContext.hasValue(R.styleable.SimpleDraweeView_actualImageResource);
            if (bool)
            {
              int i = paramContext.getResourceId(R.styleable.SimpleDraweeView_actualImageResource, -1);
              if (i != -1)
              {
                bool = isInEditMode();
                if (bool) {
                  setImageResource(i);
                } else {
                  setActualImageResource(i);
                }
              }
            }
          }
          paramContext.recycle();
        }
        catch (Throwable paramAttributeSet)
        {
          paramContext.recycle();
          throw paramAttributeSet;
        }
      }
      if (FrescoSystrace.isTracing())
      {
        FrescoSystrace.endSection();
        return;
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
  
  public static void initialize(Supplier paramSupplier)
  {
    sDraweecontrollerbuildersupplier = paramSupplier;
  }
  
  public static void shutDown()
  {
    sDraweecontrollerbuildersupplier = null;
  }
  
  protected AbstractDraweeControllerBuilder getControllerBuilder()
  {
    return mControllerBuilder;
  }
  
  public void setActualImageResource(int paramInt)
  {
    setActualImageResource(paramInt, null);
  }
  
  public void setActualImageResource(int paramInt, Object paramObject)
  {
    setImageURI(UriUtil.getUriForResourceId(paramInt), paramObject);
  }
  
  public void setImageRequest(ImageRequest paramImageRequest)
  {
    setController(mControllerBuilder.setImageRequest(paramImageRequest).setOldController(getController()).build());
  }
  
  public void setImageResource(int paramInt)
  {
    super.setImageResource(paramInt);
  }
  
  public void setImageURI(Uri paramUri)
  {
    setImageURI(paramUri, null);
  }
  
  public void setImageURI(Uri paramUri, Object paramObject)
  {
    setController(mControllerBuilder.setCallerContext(paramObject).setUri(paramUri).setOldController(getController()).build());
  }
  
  public void setImageURI(String paramString)
  {
    setImageURI(paramString, null);
  }
  
  public void setImageURI(String paramString, Object paramObject)
  {
    if (paramString != null) {
      paramString = Uri.parse(paramString);
    } else {
      paramString = null;
    }
    setImageURI(paramString, paramObject);
  }
}
