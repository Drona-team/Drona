package com.airbnb.android.react.lottie;

import android.graphics.Color;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.SimpleColorFilter;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieValueCallback;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import java.lang.ref.WeakReference;

public class LottieAnimationViewPropertyManager
{
  private String animationJson;
  private String animationName;
  private boolean animationNameDirty;
  private ReadableArray colorFilters;
  private Boolean enableMergePaths;
  private String imageAssetsFolder;
  private Boolean loop;
  private Float progress;
  private ImageView.ScaleType scaleType;
  private Float speed;
  private final WeakReference<LottieAnimationView> viewWeakReference;
  
  public LottieAnimationViewPropertyManager(LottieAnimationView paramLottieAnimationView)
  {
    viewWeakReference = new WeakReference(paramLottieAnimationView);
  }
  
  public void commitChanges()
  {
    LottieAnimationView localLottieAnimationView = (LottieAnimationView)viewWeakReference.get();
    if (localLottieAnimationView == null) {
      return;
    }
    if (animationJson != null)
    {
      localLottieAnimationView.setAnimationFromJson(animationJson, Integer.toString(animationJson.hashCode()));
      animationJson = null;
    }
    if (animationNameDirty)
    {
      localLottieAnimationView.setAnimation(animationName);
      animationNameDirty = false;
    }
    if (progress != null)
    {
      localLottieAnimationView.setProgress(progress.floatValue());
      progress = null;
    }
    int i;
    if (loop != null)
    {
      if (loop.booleanValue()) {
        i = -1;
      } else {
        i = 0;
      }
      localLottieAnimationView.setRepeatCount(i);
      loop = null;
    }
    if (speed != null)
    {
      localLottieAnimationView.setSpeed(speed.floatValue());
      speed = null;
    }
    if (scaleType != null)
    {
      localLottieAnimationView.setScaleType(scaleType);
      scaleType = null;
    }
    if (imageAssetsFolder != null)
    {
      localLottieAnimationView.setImageAssetsFolder(imageAssetsFolder);
      imageAssetsFolder = null;
    }
    if (enableMergePaths != null)
    {
      localLottieAnimationView.enableMergePathsForKitKatAndAbove(enableMergePaths.booleanValue());
      enableMergePaths = null;
    }
    if ((colorFilters != null) && (colorFilters.size() > 0))
    {
      i = 0;
      while (i < colorFilters.size())
      {
        Object localObject2 = colorFilters.getMap(i);
        Object localObject1 = ((ReadableMap)localObject2).getString("color");
        localObject2 = ((ReadableMap)localObject2).getString("keypath");
        localObject1 = new SimpleColorFilter(Color.parseColor((String)localObject1));
        localObject2 = new KeyPath(new String[] { localObject2, "**" });
        localObject1 = new LottieValueCallback(localObject1);
        localLottieAnimationView.addValueCallback((KeyPath)localObject2, LottieProperty.COLOR_FILTER, (LottieValueCallback)localObject1);
        i += 1;
      }
    }
  }
  
  public void setAnimationJson(String paramString)
  {
    animationJson = paramString;
  }
  
  public void setAnimationName(String paramString)
  {
    animationName = paramString;
    animationNameDirty = true;
  }
  
  public void setColorFilters(ReadableArray paramReadableArray)
  {
    colorFilters = paramReadableArray;
  }
  
  public void setEnableMergePaths(boolean paramBoolean)
  {
    enableMergePaths = Boolean.valueOf(paramBoolean);
  }
  
  public void setImageAssetsFolder(String paramString)
  {
    imageAssetsFolder = paramString;
  }
  
  public void setLoop(boolean paramBoolean)
  {
    loop = Boolean.valueOf(paramBoolean);
  }
  
  public void setProgress(Float paramFloat)
  {
    progress = paramFloat;
  }
  
  public void setScaleType(ImageView.ScaleType paramScaleType)
  {
    scaleType = paramScaleType;
  }
  
  public void setSpeed(float paramFloat)
  {
    speed = Float.valueOf(paramFloat);
  }
}
