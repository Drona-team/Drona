package com.airbnb.lottie;

import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.os.Build.VERSION;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.collection.SparseArrayCompat;
import com.airbnb.lottie.manager.FontAssetManager;
import com.airbnb.lottie.manager.ImageAssetManager;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.model.KeyPathElement;
import com.airbnb.lottie.model.Marker;
import com.airbnb.lottie.model.layer.BaseLayer;
import com.airbnb.lottie.model.layer.CompositionLayer;
import com.airbnb.lottie.parser.LayerParser;
import com.airbnb.lottie.utils.BaseLottieAnimator;
import com.airbnb.lottie.utils.Logger;
import com.airbnb.lottie.utils.LottieValueAnimator;
import com.airbnb.lottie.utils.MiscUtils;
import com.airbnb.lottie.value.LottieFrameInfo;
import com.airbnb.lottie.value.LottieValueCallback;
import com.airbnb.lottie.value.SimpleLottieValueCallback;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class LottieDrawable
  extends Drawable
  implements Drawable.Callback, Animatable
{
  public static final int INFINITE = -1;
  private static final String PAGE_KEY = "LottieDrawable";
  public static final int RESTART = 1;
  public static final int REVERSE = 2;
  private int alpha = 255;
  private final LottieValueAnimator animator = new LottieValueAnimator();
  private final Set<ColorFilterData> colorFilterData = new HashSet();
  private LottieComposition composition;
  @Nullable
  private CompositionLayer compositionLayer;
  private boolean enableMergePaths;
  @Nullable
  FontAssetDelegate fontAssetDelegate;
  @Nullable
  private FontAssetManager fontAssetManager;
  @Nullable
  private ImageAssetDelegate imageAssetDelegate;
  @Nullable
  private ImageAssetManager imageAssetManager;
  @Nullable
  private String imageAssetsFolder;
  private boolean isDirty = false;
  private final ArrayList<LazyCompositionTask> lazyCompositionTasks = new ArrayList();
  private final Matrix matrix = new Matrix();
  private boolean performanceTrackingEnabled;
  private float scale = 1.0F;
  private boolean systemAnimationsEnabled = true;
  @Nullable
  TextDelegate textDelegate;
  
  public LottieDrawable()
  {
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        if (compositionLayer != null) {
          compositionLayer.setProgress(animator.getAnimatedValueAbsolute());
        }
      }
    });
  }
  
  private void buildCompositionLayer()
  {
    compositionLayer = new CompositionLayer(this, LayerParser.parse(composition), composition.getLayers(), composition);
  }
  
  private Context getContext()
  {
    Drawable.Callback localCallback = getCallback();
    if (localCallback == null) {
      return null;
    }
    if ((localCallback instanceof View)) {
      return ((View)localCallback).getContext();
    }
    return null;
  }
  
  private FontAssetManager getFontAssetManager()
  {
    if (getCallback() == null) {
      return null;
    }
    if (fontAssetManager == null) {
      fontAssetManager = new FontAssetManager(getCallback(), fontAssetDelegate);
    }
    return fontAssetManager;
  }
  
  private ImageAssetManager getImageAssetManager()
  {
    if (getCallback() == null) {
      return null;
    }
    if ((imageAssetManager != null) && (!imageAssetManager.hasSameContext(getContext()))) {
      imageAssetManager = null;
    }
    if (imageAssetManager == null) {
      imageAssetManager = new ImageAssetManager(getCallback(), imageAssetsFolder, imageAssetDelegate, composition.getImages());
    }
    return imageAssetManager;
  }
  
  private float getMaxScale(Canvas paramCanvas)
  {
    return Math.min(paramCanvas.getWidth() / composition.getBounds().width(), paramCanvas.getHeight() / composition.getBounds().height());
  }
  
  private void updateBounds()
  {
    if (composition == null) {
      return;
    }
    float f = getScale();
    setBounds(0, 0, (int)(composition.getBounds().width() * f), (int)(composition.getBounds().height() * f));
  }
  
  public void addAnimatorListener(Animator.AnimatorListener paramAnimatorListener)
  {
    animator.addListener(paramAnimatorListener);
  }
  
  public void addAnimatorUpdateListener(ValueAnimator.AnimatorUpdateListener paramAnimatorUpdateListener)
  {
    animator.addUpdateListener(paramAnimatorUpdateListener);
  }
  
  public void addValueCallback(final KeyPath paramKeyPath, final Object paramObject, final LottieValueCallback paramLottieValueCallback)
  {
    if (compositionLayer == null)
    {
      lazyCompositionTasks.add(new LazyCompositionTask()
      {
        public void compressFile(LottieComposition paramAnonymousLottieComposition)
        {
          addValueCallback(paramKeyPath, paramObject, paramLottieValueCallback);
        }
      });
      return;
    }
    KeyPathElement localKeyPathElement = paramKeyPath.getResolvedElement();
    int i = 1;
    boolean bool;
    if (localKeyPathElement != null)
    {
      paramKeyPath.getResolvedElement().addValueCallback(paramObject, paramLottieValueCallback);
    }
    else
    {
      paramKeyPath = resolveKeyPath(paramKeyPath);
      i = 0;
      while (i < paramKeyPath.size())
      {
        ((KeyPath)paramKeyPath.get(i)).getResolvedElement().addValueCallback(paramObject, paramLottieValueCallback);
        i += 1;
      }
      bool = true ^ paramKeyPath.isEmpty();
    }
    if (bool)
    {
      invalidateSelf();
      if (paramObject == LottieProperty.TIME_REMAP) {
        setProgress(getProgress());
      }
    }
  }
  
  public void addValueCallback(KeyPath paramKeyPath, Object paramObject, final SimpleLottieValueCallback paramSimpleLottieValueCallback)
  {
    addValueCallback(paramKeyPath, paramObject, new LottieValueCallback()
    {
      public Object getValue(LottieFrameInfo paramAnonymousLottieFrameInfo)
      {
        return paramSimpleLottieValueCallback.getValue(paramAnonymousLottieFrameInfo);
      }
    });
  }
  
  public void cancelAnimation()
  {
    lazyCompositionTasks.clear();
    animator.cancel();
  }
  
  public void clearComposition()
  {
    if (animator.isRunning()) {
      animator.cancel();
    }
    composition = null;
    compositionLayer = null;
    imageAssetManager = null;
    animator.clearComposition();
    invalidateSelf();
  }
  
  public void draw(Canvas paramCanvas)
  {
    isDirty = false;
    Way.beginSection("Drawable#draw");
    if (compositionLayer == null) {
      return;
    }
    float f1 = scale;
    float f3 = getMaxScale(paramCanvas);
    float f2 = f3;
    if (f1 > f3)
    {
      f1 = scale / f3;
    }
    else
    {
      f2 = f1;
      f1 = 1.0F;
    }
    int i = -1;
    if (f1 > 1.0F)
    {
      i = paramCanvas.save();
      f3 = composition.getBounds().width() / 2.0F;
      float f4 = composition.getBounds().height() / 2.0F;
      float f5 = f3 * f2;
      float f6 = f4 * f2;
      paramCanvas.translate(getScale() * f3 - f5, getScale() * f4 - f6);
      paramCanvas.scale(f1, f1, f5, f6);
    }
    matrix.reset();
    matrix.preScale(f2, f2);
    compositionLayer.draw(paramCanvas, matrix, alpha);
    Way.endSection("Drawable#draw");
    if (i > 0) {
      paramCanvas.restoreToCount(i);
    }
  }
  
  public void enableMergePathsForKitKatAndAbove(boolean paramBoolean)
  {
    if (enableMergePaths == paramBoolean) {
      return;
    }
    if (Build.VERSION.SDK_INT < 19)
    {
      Logger.warning("Merge paths are not supported pre-Kit Kat.");
      return;
    }
    enableMergePaths = paramBoolean;
    if (composition != null) {
      buildCompositionLayer();
    }
  }
  
  public boolean enableMergePathsForKitKatAndAbove()
  {
    return enableMergePaths;
  }
  
  public void endAnimation()
  {
    lazyCompositionTasks.clear();
    animator.endAnimation();
  }
  
  public int getAlpha()
  {
    return alpha;
  }
  
  public LottieComposition getComposition()
  {
    return composition;
  }
  
  public int getFrame()
  {
    return (int)animator.getFrame();
  }
  
  public Bitmap getImageAsset(String paramString)
  {
    ImageAssetManager localImageAssetManager = getImageAssetManager();
    if (localImageAssetManager != null) {
      return localImageAssetManager.bitmapForId(paramString);
    }
    return null;
  }
  
  public String getImageAssetsFolder()
  {
    return imageAssetsFolder;
  }
  
  public int getIntrinsicHeight()
  {
    if (composition == null) {
      return -1;
    }
    return (int)(composition.getBounds().height() * getScale());
  }
  
  public int getIntrinsicWidth()
  {
    if (composition == null) {
      return -1;
    }
    return (int)(composition.getBounds().width() * getScale());
  }
  
  public float getMaxFrame()
  {
    return animator.getMaxFrame();
  }
  
  public float getMinFrame()
  {
    return animator.getMinFrame();
  }
  
  public int getOpacity()
  {
    return -3;
  }
  
  public PerformanceTracker getPerformanceTracker()
  {
    if (composition != null) {
      return composition.getPerformanceTracker();
    }
    return null;
  }
  
  public float getProgress()
  {
    return animator.getAnimatedValueAbsolute();
  }
  
  public int getRepeatCount()
  {
    return animator.getRepeatCount();
  }
  
  public int getRepeatMode()
  {
    return animator.getRepeatMode();
  }
  
  public float getScale()
  {
    return scale;
  }
  
  public float getSpeed()
  {
    return animator.getSpeed();
  }
  
  public TextDelegate getTextDelegate()
  {
    return textDelegate;
  }
  
  public Typeface getTypeface(String paramString1, String paramString2)
  {
    FontAssetManager localFontAssetManager = getFontAssetManager();
    if (localFontAssetManager != null) {
      return localFontAssetManager.getTypeface(paramString1, paramString2);
    }
    return null;
  }
  
  public boolean hasMasks()
  {
    return (compositionLayer != null) && (compositionLayer.hasMasks());
  }
  
  public boolean hasMatte()
  {
    return (compositionLayer != null) && (compositionLayer.hasMatte());
  }
  
  public void invalidateDrawable(Drawable paramDrawable)
  {
    paramDrawable = getCallback();
    if (paramDrawable == null) {
      return;
    }
    paramDrawable.invalidateDrawable(this);
  }
  
  public void invalidateSelf()
  {
    if (isDirty) {
      return;
    }
    isDirty = true;
    Drawable.Callback localCallback = getCallback();
    if (localCallback != null) {
      localCallback.invalidateDrawable(this);
    }
  }
  
  public boolean isAnimating()
  {
    return animator.isRunning();
  }
  
  public boolean isLooping()
  {
    return animator.getRepeatCount() == -1;
  }
  
  public boolean isMergePathsEnabledForKitKatAndAbove()
  {
    return enableMergePaths;
  }
  
  public boolean isRunning()
  {
    return isAnimating();
  }
  
  public void loop(boolean paramBoolean)
  {
    LottieValueAnimator localLottieValueAnimator = animator;
    int i;
    if (paramBoolean) {
      i = -1;
    } else {
      i = 0;
    }
    localLottieValueAnimator.setRepeatCount(i);
  }
  
  public void pauseAnimation()
  {
    lazyCompositionTasks.clear();
    animator.pauseAnimation();
  }
  
  public void playAnimation()
  {
    if (compositionLayer == null)
    {
      lazyCompositionTasks.add(new LazyCompositionTask()
      {
        public void compressFile(LottieComposition paramAnonymousLottieComposition)
        {
          playAnimation();
        }
      });
      return;
    }
    if ((systemAnimationsEnabled) || (getRepeatCount() == 0)) {
      animator.playAnimation();
    }
    if (!systemAnimationsEnabled)
    {
      float f;
      if (getSpeed() < 0.0F) {
        f = getMinFrame();
      } else {
        f = getMaxFrame();
      }
      setFrame((int)f);
    }
  }
  
  public void removeAllAnimatorListeners()
  {
    animator.removeAllListeners();
  }
  
  public void removeAllUpdateListeners()
  {
    animator.removeAllUpdateListeners();
  }
  
  public void removeAnimatorListener(Animator.AnimatorListener paramAnimatorListener)
  {
    animator.removeListener(paramAnimatorListener);
  }
  
  public void removeAnimatorUpdateListener(ValueAnimator.AnimatorUpdateListener paramAnimatorUpdateListener)
  {
    animator.removeUpdateListener(paramAnimatorUpdateListener);
  }
  
  public List resolveKeyPath(KeyPath paramKeyPath)
  {
    if (compositionLayer == null)
    {
      Logger.warning("Cannot resolve KeyPath. Composition is not set yet.");
      return Collections.emptyList();
    }
    ArrayList localArrayList = new ArrayList();
    compositionLayer.resolveKeyPath(paramKeyPath, 0, localArrayList, new KeyPath(new String[0]));
    return localArrayList;
  }
  
  public void resumeAnimation()
  {
    if (compositionLayer == null)
    {
      lazyCompositionTasks.add(new LazyCompositionTask()
      {
        public void compressFile(LottieComposition paramAnonymousLottieComposition)
        {
          resumeAnimation();
        }
      });
      return;
    }
    animator.resumeAnimation();
  }
  
  public void reverseAnimationSpeed()
  {
    animator.reverseAnimationSpeed();
  }
  
  public void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong)
  {
    paramDrawable = getCallback();
    if (paramDrawable == null) {
      return;
    }
    paramDrawable.scheduleDrawable(this, paramRunnable, paramLong);
  }
  
  public void setAlpha(int paramInt)
  {
    alpha = paramInt;
    invalidateSelf();
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    Logger.warning("Use addColorFilter instead.");
  }
  
  public boolean setComposition(LottieComposition paramLottieComposition)
  {
    if (composition == paramLottieComposition) {
      return false;
    }
    isDirty = false;
    clearComposition();
    composition = paramLottieComposition;
    buildCompositionLayer();
    animator.setComposition(paramLottieComposition);
    setProgress(animator.getAnimatedFraction());
    setScale(scale);
    updateBounds();
    Iterator localIterator = new ArrayList(lazyCompositionTasks).iterator();
    while (localIterator.hasNext())
    {
      ((LazyCompositionTask)localIterator.next()).compressFile(paramLottieComposition);
      localIterator.remove();
    }
    lazyCompositionTasks.clear();
    paramLottieComposition.setPerformanceTrackingEnabled(performanceTrackingEnabled);
    return true;
  }
  
  public void setFontAssetDelegate(FontAssetDelegate paramFontAssetDelegate)
  {
    fontAssetDelegate = paramFontAssetDelegate;
    if (fontAssetManager != null) {
      fontAssetManager.setDelegate(paramFontAssetDelegate);
    }
  }
  
  public void setFrame(final int paramInt)
  {
    if (composition == null)
    {
      lazyCompositionTasks.add(new LazyCompositionTask()
      {
        public void compressFile(LottieComposition paramAnonymousLottieComposition)
        {
          setFrame(paramInt);
        }
      });
      return;
    }
    animator.setFrame(paramInt);
  }
  
  public void setImageAssetDelegate(ImageAssetDelegate paramImageAssetDelegate)
  {
    imageAssetDelegate = paramImageAssetDelegate;
    if (imageAssetManager != null) {
      imageAssetManager.setDelegate(paramImageAssetDelegate);
    }
  }
  
  public void setImagesAssetsFolder(String paramString)
  {
    imageAssetsFolder = paramString;
  }
  
  public void setMaxFrame(final int paramInt)
  {
    if (composition == null)
    {
      lazyCompositionTasks.add(new LazyCompositionTask()
      {
        public void compressFile(LottieComposition paramAnonymousLottieComposition)
        {
          setMaxFrame(paramInt);
        }
      });
      return;
    }
    animator.setMaxFrame(paramInt + 0.99F);
  }
  
  public void setMaxFrame(final String paramString)
  {
    if (composition == null)
    {
      lazyCompositionTasks.add(new LazyCompositionTask()
      {
        public void compressFile(LottieComposition paramAnonymousLottieComposition)
        {
          setMaxFrame(paramString);
        }
      });
      return;
    }
    Object localObject = composition.getMarker(paramString);
    if (localObject != null)
    {
      setMaxFrame((int)(startFrame + durationFrames));
      return;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Cannot find marker with name ");
    ((StringBuilder)localObject).append(paramString);
    ((StringBuilder)localObject).append(".");
    throw new IllegalArgumentException(((StringBuilder)localObject).toString());
  }
  
  public void setMaxProgress(final float paramFloat)
  {
    if (composition == null)
    {
      lazyCompositionTasks.add(new LazyCompositionTask()
      {
        public void compressFile(LottieComposition paramAnonymousLottieComposition)
        {
          setMaxProgress(paramFloat);
        }
      });
      return;
    }
    setMaxFrame((int)MiscUtils.lerp(composition.getStartFrame(), composition.getEndFrame(), paramFloat));
  }
  
  public void setMinAndMaxFrame(final int paramInt1, final int paramInt2)
  {
    if (composition == null)
    {
      lazyCompositionTasks.add(new LazyCompositionTask()
      {
        public void compressFile(LottieComposition paramAnonymousLottieComposition)
        {
          setMinAndMaxFrame(paramInt1, paramInt2);
        }
      });
      return;
    }
    animator.setMinAndMaxFrames(paramInt1, paramInt2 + 0.99F);
  }
  
  public void setMinAndMaxFrame(final String paramString)
  {
    if (composition == null)
    {
      lazyCompositionTasks.add(new LazyCompositionTask()
      {
        public void compressFile(LottieComposition paramAnonymousLottieComposition)
        {
          setMinAndMaxFrame(paramString);
        }
      });
      return;
    }
    Object localObject = composition.getMarker(paramString);
    if (localObject != null)
    {
      int i = (int)startFrame;
      setMinAndMaxFrame(i, (int)durationFrames + i);
      return;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Cannot find marker with name ");
    ((StringBuilder)localObject).append(paramString);
    ((StringBuilder)localObject).append(".");
    throw new IllegalArgumentException(((StringBuilder)localObject).toString());
  }
  
  public void setMinAndMaxProgress(final float paramFloat1, final float paramFloat2)
  {
    if (composition == null)
    {
      lazyCompositionTasks.add(new LazyCompositionTask()
      {
        public void compressFile(LottieComposition paramAnonymousLottieComposition)
        {
          setMinAndMaxProgress(paramFloat1, paramFloat2);
        }
      });
      return;
    }
    setMinAndMaxFrame((int)MiscUtils.lerp(composition.getStartFrame(), composition.getEndFrame(), paramFloat1), (int)MiscUtils.lerp(composition.getStartFrame(), composition.getEndFrame(), paramFloat2));
  }
  
  public void setMinFrame(final int paramInt)
  {
    if (composition == null)
    {
      lazyCompositionTasks.add(new LazyCompositionTask()
      {
        public void compressFile(LottieComposition paramAnonymousLottieComposition)
        {
          setMinFrame(paramInt);
        }
      });
      return;
    }
    animator.setMinFrame(paramInt);
  }
  
  public void setMinFrame(final String paramString)
  {
    if (composition == null)
    {
      lazyCompositionTasks.add(new LazyCompositionTask()
      {
        public void compressFile(LottieComposition paramAnonymousLottieComposition)
        {
          setMinFrame(paramString);
        }
      });
      return;
    }
    Object localObject = composition.getMarker(paramString);
    if (localObject != null)
    {
      setMinFrame((int)startFrame);
      return;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Cannot find marker with name ");
    ((StringBuilder)localObject).append(paramString);
    ((StringBuilder)localObject).append(".");
    throw new IllegalArgumentException(((StringBuilder)localObject).toString());
  }
  
  public void setMinProgress(final float paramFloat)
  {
    if (composition == null)
    {
      lazyCompositionTasks.add(new LazyCompositionTask()
      {
        public void compressFile(LottieComposition paramAnonymousLottieComposition)
        {
          setMinProgress(paramFloat);
        }
      });
      return;
    }
    setMinFrame((int)MiscUtils.lerp(composition.getStartFrame(), composition.getEndFrame(), paramFloat));
  }
  
  public void setPerformanceTrackingEnabled(boolean paramBoolean)
  {
    performanceTrackingEnabled = paramBoolean;
    if (composition != null) {
      composition.setPerformanceTrackingEnabled(paramBoolean);
    }
  }
  
  public void setProgress(final float paramFloat)
  {
    if (composition == null)
    {
      lazyCompositionTasks.add(new LazyCompositionTask()
      {
        public void compressFile(LottieComposition paramAnonymousLottieComposition)
        {
          setProgress(paramFloat);
        }
      });
      return;
    }
    animator.setFrame(MiscUtils.lerp(composition.getStartFrame(), composition.getEndFrame(), paramFloat));
  }
  
  public void setRepeatCount(int paramInt)
  {
    animator.setRepeatCount(paramInt);
  }
  
  public void setRepeatMode(int paramInt)
  {
    animator.setRepeatMode(paramInt);
  }
  
  public void setScale(float paramFloat)
  {
    scale = paramFloat;
    updateBounds();
  }
  
  public void setSpeed(float paramFloat)
  {
    animator.setSpeed(paramFloat);
  }
  
  void setSystemAnimationsAreEnabled(Boolean paramBoolean)
  {
    systemAnimationsEnabled = paramBoolean.booleanValue();
  }
  
  public void setTextDelegate(TextDelegate paramTextDelegate)
  {
    textDelegate = paramTextDelegate;
  }
  
  public void start()
  {
    playAnimation();
  }
  
  public void stop()
  {
    endAnimation();
  }
  
  public void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable)
  {
    paramDrawable = getCallback();
    if (paramDrawable == null) {
      return;
    }
    paramDrawable.unscheduleDrawable(this, paramRunnable);
  }
  
  public Bitmap updateBitmap(String paramString, Bitmap paramBitmap)
  {
    ImageAssetManager localImageAssetManager = getImageAssetManager();
    if (localImageAssetManager == null)
    {
      Logger.warning("Cannot update bitmap. Most likely the drawable is not added to a View which prevents Lottie from getting a Context.");
      return null;
    }
    paramString = localImageAssetManager.updateBitmap(paramString, paramBitmap);
    invalidateSelf();
    return paramString;
  }
  
  public boolean useTextGlyphs()
  {
    return (textDelegate == null) && (composition.getCharacters().size() > 0);
  }
  
  private static class ColorFilterData
  {
    @Nullable
    final ColorFilter colorFilter;
    @Nullable
    final String contentName;
    final String layerName;
    
    ColorFilterData(String paramString1, String paramString2, ColorFilter paramColorFilter)
    {
      layerName = paramString1;
      contentName = paramString2;
      colorFilter = paramColorFilter;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof ColorFilterData)) {
        return false;
      }
      paramObject = (ColorFilterData)paramObject;
      return (hashCode() == paramObject.hashCode()) && (colorFilter == colorFilter);
    }
    
    public int hashCode()
    {
      int i;
      if (layerName != null) {
        i = 527 * layerName.hashCode();
      } else {
        i = 17;
      }
      int j = i;
      if (contentName != null) {
        j = i * 31 * contentName.hashCode();
      }
      return j;
    }
  }
  
  private static abstract interface LazyCompositionTask
  {
    public abstract void compressFile(LottieComposition paramLottieComposition);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface RepeatMode {}
}
