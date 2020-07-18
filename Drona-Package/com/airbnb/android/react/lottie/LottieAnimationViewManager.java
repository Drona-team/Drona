package com.airbnb.android.react.lottie;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.ContextWrapper;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import androidx.core.view.ViewCompat;
import com.airbnb.lottie.LottieAnimationView;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.common.MapBuilder.Builder;
import com.facebook.react.uimanager.BaseViewManager;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import java.util.Map;
import java.util.WeakHashMap;

class LottieAnimationViewManager
  extends SimpleViewManager<LottieAnimationView>
{
  private static final int COMMAND_PAUSE = 3;
  private static final int COMMAND_PLAY = 1;
  private static final int COMMAND_RESET = 2;
  private static final int COMMAND_RESUME = 4;
  private static final String PAGE_KEY = "LottieAnimationViewManager";
  private static final String REACT_CLASS = "LottieAnimationView";
  private static final int VERSION = 1;
  private Map<LottieAnimationView, LottieAnimationViewPropertyManager> propManagersMap = new WeakHashMap();
  
  LottieAnimationViewManager() {}
  
  private LottieAnimationViewPropertyManager getOrCreatePropertyManager(LottieAnimationView paramLottieAnimationView)
  {
    LottieAnimationViewPropertyManager localLottieAnimationViewPropertyManager2 = (LottieAnimationViewPropertyManager)propManagersMap.get(paramLottieAnimationView);
    LottieAnimationViewPropertyManager localLottieAnimationViewPropertyManager1 = localLottieAnimationViewPropertyManager2;
    if (localLottieAnimationViewPropertyManager2 == null)
    {
      localLottieAnimationViewPropertyManager1 = new LottieAnimationViewPropertyManager(paramLottieAnimationView);
      propManagersMap.put(paramLottieAnimationView, localLottieAnimationViewPropertyManager1);
    }
    return localLottieAnimationViewPropertyManager1;
  }
  
  private void sendOnAnimationFinishEvent(LottieAnimationView paramLottieAnimationView, boolean paramBoolean)
  {
    WritableMap localWritableMap = Arguments.createMap();
    localWritableMap.putBoolean("isCancelled", paramBoolean);
    for (Object localObject = paramLottieAnimationView.getContext(); (localObject instanceof ContextWrapper); localObject = ((ContextWrapper)localObject).getBaseContext()) {
      if ((localObject instanceof ReactContext))
      {
        localObject = (ReactContext)localObject;
        break label55;
      }
    }
    localObject = null;
    label55:
    if (localObject != null) {
      ((RCTEventEmitter)((ReactContext)localObject).getJSModule(RCTEventEmitter.class)).receiveEvent(paramLottieAnimationView.getId(), "animationFinish", localWritableMap);
    }
  }
  
  public LottieAnimationView createViewInstance(final ThemedReactContext paramThemedReactContext)
  {
    paramThemedReactContext = new LottieAnimationView(paramThemedReactContext);
    paramThemedReactContext.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    paramThemedReactContext.addAnimatorListener(new Animator.AnimatorListener()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        LottieAnimationViewManager.this.sendOnAnimationFinishEvent(paramThemedReactContext, true);
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        LottieAnimationViewManager.this.sendOnAnimationFinishEvent(paramThemedReactContext, false);
      }
      
      public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
      
      public void onAnimationStart(Animator paramAnonymousAnimator) {}
    });
    return paramThemedReactContext;
  }
  
  public Map getCommandsMap()
  {
    return MapBuilder.get("play", Integer.valueOf(1), "reset", Integer.valueOf(2), "pause", Integer.valueOf(3), "resume", Integer.valueOf(4));
  }
  
  public Map getExportedCustomBubblingEventTypeConstants()
  {
    return MapBuilder.builder().put("animationFinish", MapBuilder.get("phasedRegistrationNames", MapBuilder.get("bubbled", "onAnimationFinish"))).build();
  }
  
  public Map getExportedViewConstants()
  {
    return MapBuilder.builder().put("VERSION", Integer.valueOf(1)).build();
  }
  
  public String getName()
  {
    return "LottieAnimationView";
  }
  
  protected void onAfterUpdateTransaction(LottieAnimationView paramLottieAnimationView)
  {
    super.onAfterUpdateTransaction(paramLottieAnimationView);
    getOrCreatePropertyManager(paramLottieAnimationView).commitChanges();
  }
  
  public void receiveCommand(final LottieAnimationView paramLottieAnimationView, int paramInt, final ReadableArray paramReadableArray)
  {
    switch (paramInt)
    {
    default: 
      return;
    case 4: 
      new Handler(Looper.getMainLooper()).post(new Runnable()
      {
        public void run()
        {
          if (ViewCompat.isAttachedToWindow(paramLottieAnimationView)) {
            paramLottieAnimationView.resumeAnimation();
          }
        }
      });
      return;
    case 3: 
      new Handler(Looper.getMainLooper()).post(new Runnable()
      {
        public void run()
        {
          if (ViewCompat.isAttachedToWindow(paramLottieAnimationView)) {
            paramLottieAnimationView.pauseAnimation();
          }
        }
      });
      return;
    case 2: 
      new Handler(Looper.getMainLooper()).post(new Runnable()
      {
        public void run()
        {
          if (ViewCompat.isAttachedToWindow(paramLottieAnimationView))
          {
            paramLottieAnimationView.cancelAnimation();
            paramLottieAnimationView.setProgress(0.0F);
          }
        }
      });
      return;
    }
    new Handler(Looper.getMainLooper()).post(new Runnable()
    {
      public void run()
      {
        int i = paramReadableArray.getInt(0);
        int j = paramReadableArray.getInt(1);
        if ((i != -1) && (j != -1)) {
          if (i > j)
          {
            paramLottieAnimationView.setMinAndMaxFrame(j, i);
            paramLottieAnimationView.reverseAnimationSpeed();
          }
          else
          {
            paramLottieAnimationView.setMinAndMaxFrame(i, j);
          }
        }
        if (ViewCompat.isAttachedToWindow(paramLottieAnimationView))
        {
          paramLottieAnimationView.setProgress(0.0F);
          paramLottieAnimationView.playAnimation();
          return;
        }
        paramLottieAnimationView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener()
        {
          public void onViewAttachedToWindow(View paramAnonymous2View)
          {
            paramAnonymous2View = (LottieAnimationView)paramAnonymous2View;
            paramAnonymous2View.setProgress(0.0F);
            paramAnonymous2View.playAnimation();
            paramAnonymous2View.removeOnAttachStateChangeListener(this);
          }
          
          public void onViewDetachedFromWindow(View paramAnonymous2View)
          {
            val$view.removeOnAttachStateChangeListener(this);
          }
        });
      }
    });
  }
  
  public void setColorFilters(LottieAnimationView paramLottieAnimationView, ReadableArray paramReadableArray)
  {
    getOrCreatePropertyManager(paramLottieAnimationView).setColorFilters(paramReadableArray);
  }
  
  public void setEnableMergePaths(LottieAnimationView paramLottieAnimationView, boolean paramBoolean)
  {
    getOrCreatePropertyManager(paramLottieAnimationView).setEnableMergePaths(paramBoolean);
  }
  
  public void setImageAssetsFolder(LottieAnimationView paramLottieAnimationView, String paramString)
  {
    getOrCreatePropertyManager(paramLottieAnimationView).setImageAssetsFolder(paramString);
  }
  
  public void setLoop(LottieAnimationView paramLottieAnimationView, boolean paramBoolean)
  {
    getOrCreatePropertyManager(paramLottieAnimationView).setLoop(paramBoolean);
  }
  
  public void setProgress(LottieAnimationView paramLottieAnimationView, float paramFloat)
  {
    getOrCreatePropertyManager(paramLottieAnimationView).setProgress(Float.valueOf(paramFloat));
  }
  
  public void setResizeMode(LottieAnimationView paramLottieAnimationView, String paramString)
  {
    if ("cover".equals(paramString)) {
      paramString = ImageView.ScaleType.CENTER_CROP;
    } else if ("contain".equals(paramString)) {
      paramString = ImageView.ScaleType.CENTER_INSIDE;
    } else if ("center".equals(paramString)) {
      paramString = ImageView.ScaleType.CENTER;
    } else {
      paramString = null;
    }
    getOrCreatePropertyManager(paramLottieAnimationView).setScaleType(paramString);
  }
  
  public void setSourceJson(LottieAnimationView paramLottieAnimationView, String paramString)
  {
    getOrCreatePropertyManager(paramLottieAnimationView).setAnimationJson(paramString);
  }
  
  public void setSourceName(LottieAnimationView paramLottieAnimationView, String paramString)
  {
    Object localObject = paramString;
    if (!paramString.contains("."))
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append(paramString);
      ((StringBuilder)localObject).append(".json");
      localObject = ((StringBuilder)localObject).toString();
    }
    getOrCreatePropertyManager(paramLottieAnimationView).setAnimationName((String)localObject);
  }
  
  public void setSpeed(LottieAnimationView paramLottieAnimationView, double paramDouble)
  {
    getOrCreatePropertyManager(paramLottieAnimationView).setSpeed((float)paramDouble);
  }
}
