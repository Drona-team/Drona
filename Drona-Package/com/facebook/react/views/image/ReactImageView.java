package com.facebook.react.views.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import androidx.annotation.Nullable;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.controller.AbstractDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.controller.ForwardingControllerListener;
import com.facebook.drawee.drawable.AutoRotateDrawable;
import com.facebook.drawee.drawable.RoundedColorDrawable;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.generic.RoundingParams.RoundingMethod;
import com.facebook.drawee.view.DraweeView;
import com.facebook.drawee.view.GenericDraweeView;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.fresco.ReactNetworkImageRequest;
import com.facebook.react.uimanager.FloatUtil;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.views.imagehelper.ImageSource;
import com.facebook.react.views.imagehelper.MultiSourceHelper;
import com.facebook.react.views.imagehelper.MultiSourceHelper.MultiSourceResult;
import com.facebook.react.views.imagehelper.ResourceDrawableIdHelper;
import com.facebook.yoga.YogaConstants;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ReactImageView
  extends GenericDraweeView
{
  public static final int REMOTE_IMAGE_FADE_DURATION_MS = 300;
  public static final String REMOTE_TRANSPARENT_BITMAP_URI = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=";
  private static float[] sComputedCornerRadii = new float[4];
  private static final Matrix sInverse = new Matrix();
  private static final Matrix sMatrix = new Matrix();
  private static final Matrix sTileMatrix = new Matrix();
  private int mBackgroundColor = 0;
  @Nullable
  private RoundedColorDrawable mBackgroundImageDrawable;
  private int mBorderColor;
  @Nullable
  private float[] mBorderCornerRadii;
  private float mBorderRadius = NaN.0F;
  private float mBorderWidth;
  @Nullable
  private ImageSource mCachedImageSource;
  @Nullable
  private final Object mCallerContext;
  @Nullable
  private ControllerListener mControllerForTesting;
  @Nullable
  private ControllerListener mControllerListener;
  @Nullable
  private Drawable mDefaultImageDrawable;
  private final AbstractDraweeControllerBuilder mDraweeControllerBuilder;
  private int mFadeDurationMs = -1;
  @Nullable
  private GlobalImageLoadListener mGlobalImageLoadListener;
  private ReadableMap mHeaders;
  @Nullable
  private ImageSource mImageSource;
  private boolean mIsDirty;
  @Nullable
  private IterativeBoxBlurPostProcessor mIterativeBoxBlurPostProcessor;
  @Nullable
  private Drawable mLoadingImageDrawable;
  private int mOverlayColor;
  private boolean mProgressiveRenderingEnabled;
  private ImageResizeMethod mResizeMethod = ImageResizeMethod.AUTO;
  private final RoundedCornerPostprocessor mRoundedCornerPostprocessor;
  private ScalingUtils.ScaleType mScaleType = ImageResizeMode.defaultValue();
  private final List<ImageSource> mSources;
  private Shader.TileMode mTileMode = ImageResizeMode.defaultTileMode();
  private final TilePostprocessor mTilePostprocessor;
  
  public ReactImageView(Context paramContext, AbstractDraweeControllerBuilder paramAbstractDraweeControllerBuilder, GlobalImageLoadListener paramGlobalImageLoadListener, Object paramObject)
  {
    super(paramContext, buildHierarchy(paramContext));
    mDraweeControllerBuilder = paramAbstractDraweeControllerBuilder;
    mRoundedCornerPostprocessor = new RoundedCornerPostprocessor(null);
    mTilePostprocessor = new TilePostprocessor(null);
    mGlobalImageLoadListener = paramGlobalImageLoadListener;
    mCallerContext = paramObject;
    mSources = new LinkedList();
  }
  
  private static GenericDraweeHierarchy buildHierarchy(Context paramContext)
  {
    return new GenericDraweeHierarchyBuilder(paramContext.getResources()).setRoundingParams(RoundingParams.fromCornersRadius(0.0F)).build();
  }
  
  private void cornerRadii(float[] paramArrayOfFloat)
  {
    float f1;
    if (!YogaConstants.isUndefined(mBorderRadius)) {
      f1 = mBorderRadius;
    } else {
      f1 = 0.0F;
    }
    if ((mBorderCornerRadii != null) && (!YogaConstants.isUndefined(mBorderCornerRadii[0]))) {
      f2 = mBorderCornerRadii[0];
    } else {
      f2 = f1;
    }
    paramArrayOfFloat[0] = f2;
    if ((mBorderCornerRadii != null) && (!YogaConstants.isUndefined(mBorderCornerRadii[1]))) {
      f2 = mBorderCornerRadii[1];
    } else {
      f2 = f1;
    }
    paramArrayOfFloat[1] = f2;
    if ((mBorderCornerRadii != null) && (!YogaConstants.isUndefined(mBorderCornerRadii[2]))) {
      f2 = mBorderCornerRadii[2];
    } else {
      f2 = f1;
    }
    paramArrayOfFloat[2] = f2;
    float f2 = f1;
    if (mBorderCornerRadii != null)
    {
      f2 = f1;
      if (!YogaConstants.isUndefined(mBorderCornerRadii[3])) {
        f2 = mBorderCornerRadii[3];
      }
    }
    paramArrayOfFloat[3] = f2;
  }
  
  private boolean hasMultipleSources()
  {
    return mSources.size() > 1;
  }
  
  private boolean isTiled()
  {
    return mTileMode != Shader.TileMode.CLAMP;
  }
  
  private void setSourceImage()
  {
    mImageSource = null;
    Object localObject;
    if (mSources.isEmpty())
    {
      localObject = new ImageSource(getContext(), "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=");
      mSources.add(localObject);
    }
    else if (hasMultipleSources())
    {
      localObject = MultiSourceHelper.getBestSourceForSize(getWidth(), getHeight(), mSources);
      mImageSource = ((MultiSourceHelper.MultiSourceResult)localObject).getBestResult();
      mCachedImageSource = ((MultiSourceHelper.MultiSourceResult)localObject).getBestResultInCache();
      return;
    }
    mImageSource = ((ImageSource)mSources.get(0));
  }
  
  private boolean shouldResize(ImageSource paramImageSource)
  {
    if (mResizeMethod == ImageResizeMethod.AUTO)
    {
      if ((UriUtil.isLocalContentUri(paramImageSource.getUri())) || (UriUtil.isLocalFileUri(paramImageSource.getUri()))) {
        return true;
      }
    }
    else if (mResizeMethod == ImageResizeMethod.RESIZE) {
      return true;
    }
    return false;
  }
  
  private void warnImageSource(String paramString) {}
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  public void maybeUpdateView()
  {
    if (!mIsDirty) {
      return;
    }
    if (hasMultipleSources())
    {
      if (getWidth() <= 0) {
        return;
      }
      if (getHeight() <= 0) {
        return;
      }
    }
    setSourceImage();
    if (mImageSource == null) {
      return;
    }
    boolean bool = shouldResize(mImageSource);
    if (bool)
    {
      if (getWidth() <= 0) {
        return;
      }
      if (getHeight() <= 0) {
        return;
      }
    }
    if (isTiled())
    {
      if (getWidth() <= 0) {
        return;
      }
      if (getHeight() <= 0) {
        return;
      }
    }
    Object localObject1 = (GenericDraweeHierarchy)getHierarchy();
    ((GenericDraweeHierarchy)localObject1).setActualImageScaleType(mScaleType);
    if (mDefaultImageDrawable != null) {
      ((GenericDraweeHierarchy)localObject1).setPlaceholderImage(mDefaultImageDrawable, mScaleType);
    }
    if (mLoadingImageDrawable != null) {
      ((GenericDraweeHierarchy)localObject1).setPlaceholderImage(mLoadingImageDrawable, ScalingUtils.ScaleType.CENTER);
    }
    int j;
    if ((mScaleType != ScalingUtils.ScaleType.CENTER_CROP) && (mScaleType != ScalingUtils.ScaleType.FOCUS_CROP)) {
      j = 1;
    } else {
      j = 0;
    }
    Object localObject2 = ((GenericDraweeHierarchy)localObject1).getRoundingParams();
    cornerRadii(sComputedCornerRadii);
    ((RoundingParams)localObject2).setCornersRadii(sComputedCornerRadii[0], sComputedCornerRadii[1], sComputedCornerRadii[2], sComputedCornerRadii[3]);
    if (mBackgroundImageDrawable != null)
    {
      mBackgroundImageDrawable.setBorder(mBorderColor, mBorderWidth);
      mBackgroundImageDrawable.setRadii(((RoundingParams)localObject2).getCornersRadii());
      ((GenericDraweeHierarchy)localObject1).setBackgroundImage(mBackgroundImageDrawable);
    }
    if (j != 0) {
      ((RoundingParams)localObject2).setCornersRadius(0.0F);
    }
    ((RoundingParams)localObject2).setBorder(mBorderColor, mBorderWidth);
    if (mOverlayColor != 0) {
      ((RoundingParams)localObject2).setOverlayColor(mOverlayColor);
    } else {
      ((RoundingParams)localObject2).setRoundingMethod(RoundingParams.RoundingMethod.BITMAP_ONLY);
    }
    ((GenericDraweeHierarchy)localObject1).setRoundingParams((RoundingParams)localObject2);
    int i;
    if (mFadeDurationMs >= 0) {
      i = mFadeDurationMs;
    } else if (mImageSource.isResource()) {
      i = 0;
    } else {
      i = 300;
    }
    ((GenericDraweeHierarchy)localObject1).setFadeDuration(i);
    localObject1 = new LinkedList();
    if (j != 0) {
      ((List)localObject1).add(mRoundedCornerPostprocessor);
    }
    if (mIterativeBoxBlurPostProcessor != null) {
      ((List)localObject1).add(mIterativeBoxBlurPostProcessor);
    }
    if (isTiled()) {
      ((List)localObject1).add(mTilePostprocessor);
    }
    localObject2 = MultiPostprocessor.from((List)localObject1);
    if (bool) {
      localObject1 = new ResizeOptions(getWidth(), getHeight());
    } else {
      localObject1 = null;
    }
    ReactNetworkImageRequest localReactNetworkImageRequest = ReactNetworkImageRequest.fromBuilderWithHeaders(ImageRequestBuilder.newBuilderWithSource(mImageSource.getUri()).setPostprocessor((Postprocessor)localObject2).setResizeOptions((ResizeOptions)localObject1).setAutoRotateEnabled(true).setProgressiveRenderingEnabled(mProgressiveRenderingEnabled), mHeaders);
    if (mGlobalImageLoadListener != null) {
      mGlobalImageLoadListener.onLoadAttempt(mImageSource.getUri());
    }
    mDraweeControllerBuilder.reset();
    mDraweeControllerBuilder.setAutoPlayAnimations(true).setCallerContext(mCallerContext).setOldController(getController()).setImageRequest(localReactNetworkImageRequest);
    if (mCachedImageSource != null)
    {
      localObject1 = ImageRequestBuilder.newBuilderWithSource(mCachedImageSource.getUri()).setPostprocessor((Postprocessor)localObject2).setResizeOptions((ResizeOptions)localObject1).setAutoRotateEnabled(true).setProgressiveRenderingEnabled(mProgressiveRenderingEnabled).build();
      mDraweeControllerBuilder.setLowResImageRequest(localObject1);
    }
    if ((mControllerListener != null) && (mControllerForTesting != null))
    {
      localObject1 = new ForwardingControllerListener();
      ((ForwardingControllerListener)localObject1).addListener(mControllerListener);
      ((ForwardingControllerListener)localObject1).addListener(mControllerForTesting);
      mDraweeControllerBuilder.setControllerListener((ControllerListener)localObject1);
    }
    else if (mControllerForTesting != null)
    {
      mDraweeControllerBuilder.setControllerListener(mControllerForTesting);
    }
    else if (mControllerListener != null)
    {
      mDraweeControllerBuilder.setControllerListener(mControllerListener);
    }
    setController(mDraweeControllerBuilder.build());
    mIsDirty = false;
    mDraweeControllerBuilder.reset();
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if ((paramInt1 > 0) && (paramInt2 > 0))
    {
      boolean bool;
      if ((!mIsDirty) && (!hasMultipleSources()) && (!isTiled())) {
        bool = false;
      } else {
        bool = true;
      }
      mIsDirty = bool;
      maybeUpdateView();
    }
  }
  
  public void setBackgroundColor(int paramInt)
  {
    if (mBackgroundColor != paramInt)
    {
      mBackgroundColor = paramInt;
      mBackgroundImageDrawable = new RoundedColorDrawable(paramInt);
      mIsDirty = true;
    }
  }
  
  public void setBlurRadius(float paramFloat)
  {
    int i = (int)PixelUtil.toPixelFromDIP(paramFloat);
    if (i == 0) {
      mIterativeBoxBlurPostProcessor = null;
    } else {
      mIterativeBoxBlurPostProcessor = new IterativeBoxBlurPostProcessor(i);
    }
    mIsDirty = true;
  }
  
  public void setBorderColor(int paramInt)
  {
    mBorderColor = paramInt;
    mIsDirty = true;
  }
  
  public void setBorderRadius(float paramFloat)
  {
    if (!FloatUtil.floatsEqual(mBorderRadius, paramFloat))
    {
      mBorderRadius = paramFloat;
      mIsDirty = true;
    }
  }
  
  public void setBorderRadius(float paramFloat, int paramInt)
  {
    if (mBorderCornerRadii == null)
    {
      mBorderCornerRadii = new float[4];
      Arrays.fill(mBorderCornerRadii, NaN.0F);
    }
    if (!FloatUtil.floatsEqual(mBorderCornerRadii[paramInt], paramFloat))
    {
      mBorderCornerRadii[paramInt] = paramFloat;
      mIsDirty = true;
    }
  }
  
  public void setBorderWidth(float paramFloat)
  {
    mBorderWidth = PixelUtil.toPixelFromDIP(paramFloat);
    mIsDirty = true;
  }
  
  public void setControllerListener(ControllerListener paramControllerListener)
  {
    mControllerForTesting = paramControllerListener;
    mIsDirty = true;
    maybeUpdateView();
  }
  
  public void setDefaultSource(String paramString)
  {
    mDefaultImageDrawable = ResourceDrawableIdHelper.getInstance().getResourceDrawable(getContext(), paramString);
    mIsDirty = true;
  }
  
  public void setFadeDuration(int paramInt)
  {
    mFadeDurationMs = paramInt;
  }
  
  public void setHeaders(ReadableMap paramReadableMap)
  {
    mHeaders = paramReadableMap;
  }
  
  public void setLoadingIndicatorSource(String paramString)
  {
    paramString = ResourceDrawableIdHelper.getInstance().getResourceDrawable(getContext(), paramString);
    if (paramString != null) {
      paramString = new AutoRotateDrawable(paramString, 1000);
    } else {
      paramString = null;
    }
    mLoadingImageDrawable = paramString;
    mIsDirty = true;
  }
  
  public void setOverlayColor(int paramInt)
  {
    mOverlayColor = paramInt;
    mIsDirty = true;
  }
  
  public void setProgressiveRenderingEnabled(boolean paramBoolean)
  {
    mProgressiveRenderingEnabled = paramBoolean;
  }
  
  public void setResizeMethod(ImageResizeMethod paramImageResizeMethod)
  {
    mResizeMethod = paramImageResizeMethod;
    mIsDirty = true;
  }
  
  public void setScaleType(ScalingUtils.ScaleType paramScaleType)
  {
    mScaleType = paramScaleType;
    mIsDirty = true;
  }
  
  public void setShouldNotifyLoadEvents(boolean paramBoolean)
  {
    if (!paramBoolean) {
      mControllerListener = null;
    } else {
      mControllerListener = new BaseControllerListener()
      {
        public void onFailure(String paramAnonymousString, Throwable paramAnonymousThrowable)
        {
          val$mEventDispatcher.dispatchEvent(new ImageLoadEvent(getId(), 1, true, paramAnonymousThrowable.getMessage()));
        }
        
        public void onFinalImageSet(String paramAnonymousString, ImageInfo paramAnonymousImageInfo, Animatable paramAnonymousAnimatable)
        {
          if (paramAnonymousImageInfo != null)
          {
            val$mEventDispatcher.dispatchEvent(new ImageLoadEvent(getId(), 2, mImageSource.getSource(), paramAnonymousImageInfo.getWidth(), paramAnonymousImageInfo.getHeight()));
            val$mEventDispatcher.dispatchEvent(new ImageLoadEvent(getId(), 3));
          }
        }
        
        public void onSubmit(String paramAnonymousString, Object paramAnonymousObject)
        {
          val$mEventDispatcher.dispatchEvent(new ImageLoadEvent(getId(), 4));
        }
      };
    }
    mIsDirty = true;
  }
  
  public void setSource(ReadableArray paramReadableArray)
  {
    mSources.clear();
    int i;
    Object localObject1;
    if ((paramReadableArray != null) && (paramReadableArray.size() != 0))
    {
      int j = paramReadableArray.size();
      i = 0;
      if (j == 1)
      {
        paramReadableArray = paramReadableArray.getMap(0).getString("uri");
        localObject1 = new ImageSource(getContext(), paramReadableArray);
        mSources.add(localObject1);
        if (!Uri.EMPTY.equals(((ImageSource)localObject1).getUri())) {
          break label233;
        }
        warnImageSource(paramReadableArray);
        break label233;
      }
    }
    while (i < paramReadableArray.size())
    {
      Object localObject2 = paramReadableArray.getMap(i);
      localObject1 = ((ReadableMap)localObject2).getString("uri");
      localObject2 = new ImageSource(getContext(), (String)localObject1, ((ReadableMap)localObject2).getDouble("width"), ((ReadableMap)localObject2).getDouble("height"));
      mSources.add(localObject2);
      if (Uri.EMPTY.equals(((ImageSource)localObject2).getUri())) {
        warnImageSource((String)localObject1);
      }
      i += 1;
      continue;
      paramReadableArray = new ImageSource(getContext(), "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=");
      mSources.add(paramReadableArray);
    }
    label233:
    mIsDirty = true;
  }
  
  public void setTileMode(Shader.TileMode paramTileMode)
  {
    mTileMode = paramTileMode;
    mIsDirty = true;
  }
  
  private class RoundedCornerPostprocessor
    extends BasePostprocessor
  {
    private RoundedCornerPostprocessor() {}
    
    void getRadii(Bitmap paramBitmap, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
    {
      mScaleType.getTransform(ReactImageView.sMatrix, new Rect(0, 0, paramBitmap.getWidth(), paramBitmap.getHeight()), paramBitmap.getWidth(), paramBitmap.getHeight(), 0.0F, 0.0F);
      ReactImageView.sMatrix.invert(ReactImageView.sInverse);
      paramArrayOfFloat2[0] = ReactImageView.sInverse.mapRadius(paramArrayOfFloat1[0]);
      paramArrayOfFloat2[1] = paramArrayOfFloat2[0];
      paramArrayOfFloat2[2] = ReactImageView.sInverse.mapRadius(paramArrayOfFloat1[1]);
      paramArrayOfFloat2[3] = paramArrayOfFloat2[2];
      paramArrayOfFloat2[4] = ReactImageView.sInverse.mapRadius(paramArrayOfFloat1[2]);
      paramArrayOfFloat2[5] = paramArrayOfFloat2[4];
      paramArrayOfFloat2[6] = ReactImageView.sInverse.mapRadius(paramArrayOfFloat1[3]);
      paramArrayOfFloat2[7] = paramArrayOfFloat2[6];
    }
    
    public void process(Bitmap paramBitmap1, Bitmap paramBitmap2)
    {
      ReactImageView.this.cornerRadii(ReactImageView.sComputedCornerRadii);
      paramBitmap1.setHasAlpha(true);
      if ((FloatUtil.floatsEqual(ReactImageView.sComputedCornerRadii[0], 0.0F)) && (FloatUtil.floatsEqual(ReactImageView.sComputedCornerRadii[1], 0.0F)) && (FloatUtil.floatsEqual(ReactImageView.sComputedCornerRadii[2], 0.0F)) && (FloatUtil.floatsEqual(ReactImageView.sComputedCornerRadii[3], 0.0F)))
      {
        super.process(paramBitmap1, paramBitmap2);
        return;
      }
      Paint localPaint = new Paint();
      localPaint.setAntiAlias(true);
      localPaint.setShader(new BitmapShader(paramBitmap2, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
      paramBitmap1 = new Canvas(paramBitmap1);
      float[] arrayOfFloat = new float[8];
      getRadii(paramBitmap2, ReactImageView.sComputedCornerRadii, arrayOfFloat);
      Path localPath = new Path();
      localPath.addRoundRect(new RectF(0.0F, 0.0F, paramBitmap2.getWidth(), paramBitmap2.getHeight()), arrayOfFloat, Path.Direction.CW);
      paramBitmap1.drawPath(localPath, localPaint);
    }
  }
  
  private class TilePostprocessor
    extends BasePostprocessor
  {
    private TilePostprocessor() {}
    
    public CloseableReference process(Bitmap paramBitmap, PlatformBitmapFactory paramPlatformBitmapFactory)
    {
      Rect localRect = new Rect(0, 0, getWidth(), getHeight());
      mScaleType.getTransform(ReactImageView.sTileMatrix, localRect, paramBitmap.getWidth(), paramBitmap.getHeight(), 0.0F, 0.0F);
      Paint localPaint = new Paint();
      localPaint.setAntiAlias(true);
      paramBitmap = new BitmapShader(paramBitmap, mTileMode, mTileMode);
      paramBitmap.setLocalMatrix(ReactImageView.sTileMatrix);
      localPaint.setShader(paramBitmap);
      paramBitmap = paramPlatformBitmapFactory.createBitmap(getWidth(), getHeight());
      try
      {
        new Canvas((Bitmap)paramBitmap.get()).drawRect(localRect, localPaint);
        paramPlatformBitmapFactory = paramBitmap.clone();
        CloseableReference.closeSafely(paramBitmap);
        return paramPlatformBitmapFactory;
      }
      catch (Throwable paramPlatformBitmapFactory)
      {
        CloseableReference.closeSafely(paramBitmap);
        throw paramPlatformBitmapFactory;
      }
    }
  }
}
