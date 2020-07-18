package com.facebook.drawee.backends.pipeline;

import android.content.Context;
import com.facebook.common.logging.FLog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import com.facebook.soloader.SoLoader;
import java.io.IOException;

public class Fresco
{
  private static final Class<?> TAG = Fresco.class;
  private static PipelineDraweeControllerBuilderSupplier sDraweeControllerBuilderSupplier;
  private static volatile boolean sIsInitialized;
  
  private Fresco() {}
  
  public static PipelineDraweeControllerBuilderSupplier getDraweeControllerBuilderSupplier()
  {
    return sDraweeControllerBuilderSupplier;
  }
  
  public static ImagePipeline getImagePipeline()
  {
    return getImagePipelineFactory().getImagePipeline();
  }
  
  public static ImagePipelineFactory getImagePipelineFactory()
  {
    return ImagePipelineFactory.getInstance();
  }
  
  public static boolean hasBeenInitialized()
  {
    return sIsInitialized;
  }
  
  public static void initialize(Context paramContext)
  {
    initialize(paramContext, null, null);
  }
  
  public static void initialize(Context paramContext, ImagePipelineConfig paramImagePipelineConfig)
  {
    initialize(paramContext, paramImagePipelineConfig, null);
  }
  
  public static void initialize(Context paramContext, ImagePipelineConfig paramImagePipelineConfig, DraweeConfig paramDraweeConfig)
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("Fresco#initialize");
    }
    if (sIsInitialized) {
      FLog.w(TAG, "Fresco has already been initialized! `Fresco.initialize(...)` should only be called 1 single time to avoid memory leaks!");
    } else {
      sIsInitialized = true;
    }
    try
    {
      boolean bool = FrescoSystrace.isTracing();
      if (bool) {
        FrescoSystrace.beginSection("Fresco.initialize->SoLoader.init");
      }
      SoLoader.init(paramContext, 0);
      bool = FrescoSystrace.isTracing();
      if (bool) {
        FrescoSystrace.endSection();
      }
      paramContext = paramContext.getApplicationContext();
      if (paramImagePipelineConfig == null) {
        ImagePipelineFactory.initialize(paramContext);
      } else {
        ImagePipelineFactory.initialize(paramImagePipelineConfig);
      }
      initializeDrawee(paramContext, paramDraweeConfig);
      if (FrescoSystrace.isTracing())
      {
        FrescoSystrace.endSection();
        return;
      }
    }
    catch (IOException paramContext)
    {
      if (FrescoSystrace.isTracing()) {
        FrescoSystrace.endSection();
      }
      throw new RuntimeException("Could not initialize SoLoader", paramContext);
    }
  }
  
  private static void initializeDrawee(Context paramContext, DraweeConfig paramDraweeConfig)
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("Fresco.initializeDrawee");
    }
    sDraweeControllerBuilderSupplier = new PipelineDraweeControllerBuilderSupplier(paramContext, paramDraweeConfig);
    SimpleDraweeView.initialize(sDraweeControllerBuilderSupplier);
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
  }
  
  public static PipelineDraweeControllerBuilder newDraweeControllerBuilder()
  {
    return sDraweeControllerBuilderSupplier.getFolder();
  }
  
  public static void shutDown()
  {
    sDraweeControllerBuilderSupplier = null;
    SimpleDraweeView.shutDown();
    ImagePipelineFactory.shutDown();
  }
}
