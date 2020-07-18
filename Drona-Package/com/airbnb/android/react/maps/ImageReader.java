package com.airbnb.android.react.maps;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.AbstractDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class ImageReader
{
  private final Context context;
  private DataSource<CloseableReference<CloseableImage>> dataSource;
  private final ImageReadable db;
  private final DraweeHolder<?> logoHolder;
  private final ControllerListener<ImageInfo> mLogoControllerListener = new BaseControllerListener()
  {
    public void onFinalImageSet(String paramAnonymousString, ImageInfo paramAnonymousImageInfo, Animatable paramAnonymousAnimatable)
    {
      paramAnonymousImageInfo = this;
      try
      {
        paramAnonymousAnimatable = ImageReader.this;
        paramAnonymousString = this;
        paramAnonymousImageInfo = paramAnonymousString;
        paramAnonymousAnimatable = (CloseableReference)dataSource.getResult();
        if (paramAnonymousAnimatable != null) {
          try
          {
            paramAnonymousImageInfo = (CloseableImage)paramAnonymousAnimatable.get();
            if (paramAnonymousImageInfo != null)
            {
              boolean bool = paramAnonymousImageInfo instanceof CloseableStaticBitmap;
              if (bool)
              {
                paramAnonymousImageInfo = ((CloseableStaticBitmap)paramAnonymousImageInfo).getUnderlyingBitmap();
                if (paramAnonymousImageInfo != null)
                {
                  paramAnonymousImageInfo = paramAnonymousImageInfo.copy(Bitmap.Config.ARGB_8888, true);
                  ImageReader localImageReader = this$0;
                  db.setIconBitmap(paramAnonymousImageInfo);
                  this$0.db.setIconBitmapDescriptor(BitmapDescriptorFactory.fromBitmap(paramAnonymousImageInfo));
                }
              }
            }
          }
          catch (Throwable localImageReader)
          {
            paramAnonymousString = this;
            localThrowable = ???;
            paramAnonymousImageInfo = paramAnonymousAnimatable;
            paramAnonymousAnimatable = localThrowable;
            break label165;
          }
        }
        paramAnonymousString = this;
        this$0.dataSource.close();
        if (paramAnonymousAnimatable != null) {
          CloseableReference.closeSafely(paramAnonymousAnimatable);
        }
        this$0.db.update();
        return;
      }
      catch (Throwable paramAnonymousAnimatable)
      {
        Object localObject = null;
        paramAnonymousString = paramAnonymousImageInfo;
        paramAnonymousImageInfo = localObject;
        label165:
        this$0.dataSource.close();
        if (paramAnonymousImageInfo != null) {
          CloseableReference.closeSafely(paramAnonymousImageInfo);
        }
        throw paramAnonymousAnimatable;
      }
    }
  };
  private final Resources resources;
  
  public ImageReader(Context paramContext, Resources paramResources, ImageReadable paramImageReadable)
  {
    context = paramContext;
    resources = paramResources;
    db = paramImageReadable;
    logoHolder = DraweeHolder.create(createDraweeHeirarchy(paramResources), paramContext);
    logoHolder.onAttach();
  }
  
  private GenericDraweeHierarchy createDraweeHeirarchy(Resources paramResources)
  {
    return new GenericDraweeHierarchyBuilder(paramResources).setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER).setFadeDuration(0).build();
  }
  
  private BitmapDescriptor getBitmapDescriptorByName(String paramString)
  {
    return BitmapDescriptorFactory.fromResource(getDrawableResourceByName(paramString));
  }
  
  private int getDrawableResourceByName(String paramString)
  {
    return resources.getIdentifier(paramString, "drawable", context.getPackageName());
  }
  
  public void setImage(String paramString)
  {
    if (paramString == null)
    {
      db.setIconBitmapDescriptor(null);
      db.update();
      return;
    }
    if ((!paramString.startsWith("http://")) && (!paramString.startsWith("https://")) && (!paramString.startsWith("file://")) && (!paramString.startsWith("asset://")))
    {
      BitmapDescriptor localBitmapDescriptor = getBitmapDescriptorByName(paramString);
      if (localBitmapDescriptor != null)
      {
        db.setIconBitmapDescriptor(localBitmapDescriptor);
        db.setIconBitmap(BitmapFactory.decodeResource(resources, getDrawableResourceByName(paramString)));
      }
      db.update();
      return;
    }
    paramString = ImageRequestBuilder.newBuilderWithSource(Uri.parse(paramString)).build();
    dataSource = Fresco.getImagePipeline().fetchDecodedImage(paramString, this);
    paramString = ((PipelineDraweeControllerBuilder)((PipelineDraweeControllerBuilder)((PipelineDraweeControllerBuilder)Fresco.newDraweeControllerBuilder().setImageRequest(paramString)).setControllerListener(mLogoControllerListener)).setOldController(logoHolder.getController())).build();
    logoHolder.setController(paramString);
  }
}
