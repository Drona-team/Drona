package com.google.android.exoplayer2.pc;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.Player.TextComponent;
import com.google.android.exoplayer2.Player.VideoComponent;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.Metadata.Entry;
import com.google.android.exoplayer2.metadata.configurations.ApicFrame;
import com.google.android.exoplayer2.pc.spherical.SingleTapListener;
import com.google.android.exoplayer2.pc.spherical.SphericalSurfaceView;
import com.google.android.exoplayer2.pc.spherical.SphericalSurfaceView.SurfaceListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class PlayerView
  extends FrameLayout
{
  public static final int SHOW_BUFFERING_ALWAYS = 2;
  public static final int SHOW_BUFFERING_NEVER = 0;
  public static final int SHOW_BUFFERING_WHEN_PLAYING = 1;
  private static final int SURFACE_TYPE_MONO360_VIEW = 3;
  private static final int SURFACE_TYPE_NONE = 0;
  private static final int SURFACE_TYPE_SURFACE_VIEW = 1;
  private static final int SURFACE_TYPE_TEXTURE_VIEW = 2;
  private final ImageView artworkView;
  @Nullable
  private final View bufferingView;
  private final ComponentListener componentListener;
  private final AspectRatioFrameLayout contentFrame;
  private final PlayerControlView controller;
  private boolean controllerAutoShow;
  private boolean controllerHideDuringAds;
  private boolean controllerHideOnTouch;
  private int controllerShowTimeoutMs;
  @Nullable
  private CharSequence customErrorMessage;
  @Nullable
  private Drawable defaultArtwork;
  @Nullable
  private ErrorMessageProvider<? super ExoPlaybackException> errorMessageProvider;
  @Nullable
  private final TextView errorMessageView;
  private boolean keepContentOnPlayerReset;
  private final FrameLayout overlayFrameLayout;
  private Player player;
  private int showBuffering;
  private final View shutterView;
  private final SubtitleView subtitleView;
  private final View surfaceView;
  private int textureViewRotation;
  private boolean useArtwork;
  private boolean useController;
  
  public PlayerView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public PlayerView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public PlayerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    if (isInEditMode())
    {
      contentFrame = null;
      shutterView = null;
      surfaceView = null;
      artworkView = null;
      subtitleView = null;
      bufferingView = null;
      errorMessageView = null;
      controller = null;
      componentListener = null;
      overlayFrameLayout = null;
      paramContext = new ImageView(paramContext);
      if (Util.SDK_INT >= 23) {
        configureEditModeLogoV23(getResources(), paramContext);
      } else {
        configureEditModeLogo(getResources(), paramContext);
      }
      addView(paramContext);
      return;
    }
    int i = R.layout.exo_player_view;
    boolean bool6;
    int i1;
    int j;
    boolean bool1;
    int k;
    int m;
    boolean bool3;
    int n;
    boolean bool4;
    if (paramAttributeSet != null)
    {
      localObject1 = paramContext.getTheme().obtainStyledAttributes(paramAttributeSet, R.styleable.PlayerView, 0, 0);
      try
      {
        bool6 = ((TypedArray)localObject1).hasValue(R.styleable.PlayerView_shutter_background_color);
        i1 = ((TypedArray)localObject1).getColor(R.styleable.PlayerView_shutter_background_color, 0);
        i = ((TypedArray)localObject1).getResourceId(R.styleable.PlayerView_player_layout_id, i);
        bool5 = ((TypedArray)localObject1).getBoolean(R.styleable.PlayerView_use_artwork, true);
        j = ((TypedArray)localObject1).getResourceId(R.styleable.PlayerView_default_artwork, 0);
        bool1 = ((TypedArray)localObject1).getBoolean(R.styleable.PlayerView_use_controller, true);
        k = ((TypedArray)localObject1).getInt(R.styleable.PlayerView_surface_type, 1);
        m = ((TypedArray)localObject1).getInt(R.styleable.PlayerView_resize_mode, 0);
        paramInt = ((TypedArray)localObject1).getInt(R.styleable.PlayerView_show_timeout, 5000);
        bool2 = ((TypedArray)localObject1).getBoolean(R.styleable.PlayerView_hide_on_touch, true);
        bool3 = ((TypedArray)localObject1).getBoolean(R.styleable.PlayerView_auto_show, true);
        n = ((TypedArray)localObject1).getInteger(R.styleable.PlayerView_show_buffering, 0);
        keepContentOnPlayerReset = ((TypedArray)localObject1).getBoolean(R.styleable.PlayerView_keep_content_on_player_reset, keepContentOnPlayerReset);
        bool4 = ((TypedArray)localObject1).getBoolean(R.styleable.PlayerView_hide_during_ads, true);
        ((TypedArray)localObject1).recycle();
      }
      catch (Throwable paramContext)
      {
        ((TypedArray)localObject1).recycle();
        throw paramContext;
      }
    }
    else
    {
      bool2 = true;
      n = 0;
      paramInt = 5000;
      bool3 = true;
      bool6 = false;
      i1 = 0;
      bool5 = true;
      j = 0;
      bool4 = true;
      k = 1;
      m = 0;
      bool1 = true;
    }
    LayoutInflater.from(paramContext).inflate(i, this);
    componentListener = new ComponentListener(null);
    setDescendantFocusability(262144);
    contentFrame = ((AspectRatioFrameLayout)findViewById(R.id.exo_content_frame));
    if (contentFrame != null) {
      setResizeModeRaw(contentFrame, m);
    }
    shutterView = findViewById(R.id.exo_shutter);
    if ((shutterView != null) && (bool6)) {
      shutterView.setBackgroundColor(i1);
    }
    if ((contentFrame != null) && (k != 0))
    {
      localObject1 = new ViewGroup.LayoutParams(-1, -1);
      switch (k)
      {
      default: 
        surfaceView = new SurfaceView(paramContext);
        break;
      case 3: 
        if (Util.SDK_INT >= 15) {
          bool6 = true;
        } else {
          bool6 = false;
        }
        Assertions.checkState(bool6);
        localObject2 = new SphericalSurfaceView(paramContext);
        ((SphericalSurfaceView)localObject2).setSurfaceListener(componentListener);
        ((SphericalSurfaceView)localObject2).setSingleTapListener(componentListener);
        surfaceView = ((View)localObject2);
        break;
      case 2: 
        surfaceView = new TextureView(paramContext);
      }
      surfaceView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
      contentFrame.addView(surfaceView, 0);
    }
    else
    {
      surfaceView = null;
    }
    overlayFrameLayout = ((FrameLayout)findViewById(R.id.exo_overlay));
    artworkView = ((ImageView)findViewById(R.id.exo_artwork));
    if ((bool5) && (artworkView != null)) {
      bool5 = true;
    } else {
      bool5 = false;
    }
    useArtwork = bool5;
    if (j != 0) {
      defaultArtwork = ContextCompat.getDrawable(getContext(), j);
    }
    subtitleView = ((SubtitleView)findViewById(R.id.exo_subtitles));
    if (subtitleView != null)
    {
      subtitleView.setUserDefaultStyle();
      subtitleView.setUserDefaultTextSize();
    }
    bufferingView = findViewById(R.id.exo_buffering);
    if (bufferingView != null) {
      bufferingView.setVisibility(8);
    }
    showBuffering = n;
    errorMessageView = ((TextView)findViewById(R.id.exo_error_message));
    if (errorMessageView != null) {
      errorMessageView.setVisibility(8);
    }
    Object localObject2 = (PlayerControlView)findViewById(R.id.exo_controller);
    Object localObject1 = findViewById(R.id.exo_controller_placeholder);
    if (localObject2 != null)
    {
      controller = ((PlayerControlView)localObject2);
    }
    else if (localObject1 != null)
    {
      controller = new PlayerControlView(paramContext, null, 0, paramAttributeSet);
      controller.setLayoutParams(((View)localObject1).getLayoutParams());
      paramContext = (ViewGroup)((View)localObject1).getParent();
      i = paramContext.indexOfChild((View)localObject1);
      paramContext.removeView((View)localObject1);
      paramContext.addView(controller, i);
    }
    else
    {
      controller = null;
    }
    boolean bool5 = false;
    if (controller == null) {
      paramInt = 0;
    }
    controllerShowTimeoutMs = paramInt;
    controllerHideOnTouch = bool2;
    controllerAutoShow = bool3;
    controllerHideDuringAds = bool4;
    boolean bool2 = bool5;
    if (bool1)
    {
      bool2 = bool5;
      if (controller != null) {
        bool2 = true;
      }
    }
    useController = bool2;
    hideController();
  }
  
  private static void applyTextureViewRotation(TextureView paramTextureView, int paramInt)
  {
    float f1 = paramTextureView.getWidth();
    float f2 = paramTextureView.getHeight();
    if ((f1 != 0.0F) && (f2 != 0.0F) && (paramInt != 0))
    {
      Matrix localMatrix = new Matrix();
      float f3 = f1 / 2.0F;
      float f4 = f2 / 2.0F;
      localMatrix.postRotate(paramInt, f3, f4);
      RectF localRectF1 = new RectF(0.0F, 0.0F, f1, f2);
      RectF localRectF2 = new RectF();
      localMatrix.mapRect(localRectF2, localRectF1);
      localMatrix.postScale(f1 / localRectF2.width(), f2 / localRectF2.height(), f3, f4);
      paramTextureView.setTransform(localMatrix);
      return;
    }
    paramTextureView.setTransform(null);
  }
  
  private void closeShutter()
  {
    if (shutterView != null) {
      shutterView.setVisibility(0);
    }
  }
  
  private static void configureEditModeLogo(Resources paramResources, ImageView paramImageView)
  {
    paramImageView.setImageDrawable(paramResources.getDrawable(R.drawable.exo_edit_mode_logo));
    paramImageView.setBackgroundColor(paramResources.getColor(R.color.exo_edit_mode_background_color));
  }
  
  private static void configureEditModeLogoV23(Resources paramResources, ImageView paramImageView)
  {
    paramImageView.setImageDrawable(paramResources.getDrawable(R.drawable.exo_edit_mode_logo, null));
    paramImageView.setBackgroundColor(paramResources.getColor(R.color.exo_edit_mode_background_color, null));
  }
  
  private void hideArtwork()
  {
    if (artworkView != null)
    {
      artworkView.setImageResource(17170445);
      artworkView.setVisibility(4);
    }
  }
  
  private boolean isDpadKey(int paramInt)
  {
    return (paramInt == 19) || (paramInt == 270) || (paramInt == 22) || (paramInt == 271) || (paramInt == 20) || (paramInt == 269) || (paramInt == 21) || (paramInt == 268) || (paramInt == 23);
  }
  
  private boolean isPlayingAd()
  {
    return (player != null) && (player.isPlayingAd()) && (player.getPlayWhenReady());
  }
  
  private void maybeShowController(boolean paramBoolean)
  {
    if ((isPlayingAd()) && (controllerHideDuringAds)) {
      return;
    }
    if (useController)
    {
      int i;
      if ((controller.isVisible()) && (controller.getShowTimeoutMs() <= 0)) {
        i = 1;
      } else {
        i = 0;
      }
      boolean bool = shouldShowControllerIndefinitely();
      if ((paramBoolean) || (i != 0) || (bool)) {
        showController(bool);
      }
    }
  }
  
  private boolean setArtworkFromMetadata(Metadata paramMetadata)
  {
    int i = 0;
    while (i < paramMetadata.length())
    {
      Metadata.Entry localEntry = paramMetadata.getFormat(i);
      if ((localEntry instanceof ApicFrame))
      {
        paramMetadata = pictureData;
        paramMetadata = BitmapFactory.decodeByteArray(paramMetadata, 0, paramMetadata.length);
        return setDrawableArtwork(new BitmapDrawable(getResources(), paramMetadata));
      }
      i += 1;
    }
    return false;
  }
  
  private boolean setDrawableArtwork(Drawable paramDrawable)
  {
    if (paramDrawable != null)
    {
      int i = paramDrawable.getIntrinsicWidth();
      int j = paramDrawable.getIntrinsicHeight();
      if ((i > 0) && (j > 0))
      {
        if (contentFrame != null) {
          contentFrame.setAspectRatio(i / j);
        }
        artworkView.setImageDrawable(paramDrawable);
        artworkView.setVisibility(0);
        return true;
      }
    }
    return false;
  }
  
  private static void setResizeModeRaw(AspectRatioFrameLayout paramAspectRatioFrameLayout, int paramInt)
  {
    paramAspectRatioFrameLayout.setResizeMode(paramInt);
  }
  
  private boolean shouldShowControllerIndefinitely()
  {
    if (player == null) {
      return true;
    }
    int i = player.getPlaybackState();
    if (controllerAutoShow)
    {
      if ((i == 1) || (i == 4)) {
        break label52;
      }
      if (!player.getPlayWhenReady()) {
        return true;
      }
    }
    return false;
    label52:
    return true;
  }
  
  private void showController(boolean paramBoolean)
  {
    if (!useController) {
      return;
    }
    PlayerControlView localPlayerControlView = controller;
    int i;
    if (paramBoolean) {
      i = 0;
    } else {
      i = controllerShowTimeoutMs;
    }
    localPlayerControlView.setShowTimeoutMs(i);
    controller.show();
  }
  
  public static void switchTargetView(Player paramPlayer, PlayerView paramPlayerView1, PlayerView paramPlayerView2)
  {
    if (paramPlayerView1 == paramPlayerView2) {
      return;
    }
    if (paramPlayerView2 != null) {
      paramPlayerView2.setPlayer(paramPlayer);
    }
    if (paramPlayerView1 != null) {
      paramPlayerView1.setPlayer(null);
    }
  }
  
  private boolean toggleControllerVisibility()
  {
    if ((useController) && (player != null))
    {
      if (!controller.isVisible())
      {
        maybeShowController(true);
        return true;
      }
      if (controllerHideOnTouch)
      {
        controller.hide();
        return true;
      }
    }
    else
    {
      return false;
    }
    return true;
  }
  
  private void updateBuffering()
  {
    if (bufferingView != null)
    {
      Object localObject = player;
      int k = 1;
      int j = 0;
      if ((localObject != null) && (player.getPlaybackState() == 2))
      {
        i = k;
        if (showBuffering == 2) {
          break label72;
        }
        if ((showBuffering == 1) && (player.getPlayWhenReady()))
        {
          i = k;
          break label72;
        }
      }
      int i = 0;
      label72:
      localObject = bufferingView;
      if (i != 0) {
        i = j;
      } else {
        i = 8;
      }
      ((View)localObject).setVisibility(i);
    }
  }
  
  private void updateErrorMessage()
  {
    if (errorMessageView != null)
    {
      if (customErrorMessage != null)
      {
        errorMessageView.setText(customErrorMessage);
        errorMessageView.setVisibility(0);
        return;
      }
      Object localObject2 = null;
      Object localObject1 = localObject2;
      if (player != null)
      {
        localObject1 = localObject2;
        if (player.getPlaybackState() == 1)
        {
          localObject1 = localObject2;
          if (errorMessageProvider != null) {
            localObject1 = player.getPlaybackError();
          }
        }
      }
      if (localObject1 != null)
      {
        localObject1 = (CharSequence)errorMessageProvider.getErrorMessage((Throwable)localObject1).second;
        errorMessageView.setText((CharSequence)localObject1);
        errorMessageView.setVisibility(0);
        return;
      }
      errorMessageView.setVisibility(8);
    }
  }
  
  private void updateForCurrentTrackSelections(boolean paramBoolean)
  {
    if ((player != null) && (!player.getCurrentTrackGroups().isEmpty()))
    {
      if ((paramBoolean) && (!keepContentOnPlayerReset)) {
        closeShutter();
      }
      TrackSelectionArray localTrackSelectionArray = player.getCurrentTrackSelections();
      int i = 0;
      while (i < length)
      {
        if ((player.getRendererType(i) == 2) && (localTrackSelectionArray.getChapters(i) != null))
        {
          hideArtwork();
          return;
        }
        i += 1;
      }
      closeShutter();
      if (useArtwork)
      {
        i = 0;
        while (i < length)
        {
          TrackSelection localTrackSelection = localTrackSelectionArray.getChapters(i);
          if (localTrackSelection != null)
          {
            int j = 0;
            while (j < localTrackSelection.length())
            {
              Metadata localMetadata = getFormatmetadata;
              if ((localMetadata != null) && (setArtworkFromMetadata(localMetadata))) {
                return;
              }
              j += 1;
            }
          }
          i += 1;
        }
        if (setDrawableArtwork(defaultArtwork)) {
          return;
        }
      }
      hideArtwork();
      return;
    }
    if (!keepContentOnPlayerReset)
    {
      hideArtwork();
      closeShutter();
    }
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if ((player != null) && (player.isPlayingAd()))
    {
      overlayFrameLayout.requestFocus();
      return super.dispatchKeyEvent(paramKeyEvent);
    }
    boolean bool2 = isDpadKey(paramKeyEvent.getKeyCode());
    boolean bool1 = false;
    int i;
    if ((bool2) && (useController) && (!controller.isVisible())) {
      i = 1;
    } else {
      i = 0;
    }
    if ((i != 0) || (dispatchMediaKeyEvent(paramKeyEvent)) || (super.dispatchKeyEvent(paramKeyEvent))) {
      bool1 = true;
    }
    if (bool1) {
      maybeShowController(true);
    }
    return bool1;
  }
  
  public boolean dispatchMediaKeyEvent(KeyEvent paramKeyEvent)
  {
    return (useController) && (controller.dispatchMediaKeyEvent(paramKeyEvent));
  }
  
  public boolean getControllerAutoShow()
  {
    return controllerAutoShow;
  }
  
  public boolean getControllerHideOnTouch()
  {
    return controllerHideOnTouch;
  }
  
  public int getControllerShowTimeoutMs()
  {
    return controllerShowTimeoutMs;
  }
  
  public Drawable getDefaultArtwork()
  {
    return defaultArtwork;
  }
  
  public FrameLayout getOverlayFrameLayout()
  {
    return overlayFrameLayout;
  }
  
  public Player getPlayer()
  {
    return player;
  }
  
  public int getResizeMode()
  {
    boolean bool;
    if (contentFrame != null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    return contentFrame.getResizeMode();
  }
  
  public SubtitleView getSubtitleView()
  {
    return subtitleView;
  }
  
  public boolean getUseArtwork()
  {
    return useArtwork;
  }
  
  public boolean getUseController()
  {
    return useController;
  }
  
  public View getVideoSurfaceView()
  {
    return surfaceView;
  }
  
  public void hideController()
  {
    if (controller != null) {
      controller.hide();
    }
  }
  
  public boolean isControllerVisible()
  {
    return (controller != null) && (controller.isVisible());
  }
  
  public void onPause()
  {
    if ((surfaceView instanceof SphericalSurfaceView)) {
      ((SphericalSurfaceView)surfaceView).onPause();
    }
  }
  
  public void onResume()
  {
    if ((surfaceView instanceof SphericalSurfaceView)) {
      ((SphericalSurfaceView)surfaceView).onResume();
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getActionMasked() != 0) {
      return false;
    }
    return toggleControllerVisibility();
  }
  
  public boolean onTrackballEvent(MotionEvent paramMotionEvent)
  {
    if ((useController) && (player != null))
    {
      maybeShowController(true);
      return true;
    }
    return false;
  }
  
  public void setAspectRatioListener(AspectRatioFrameLayout.AspectRatioListener paramAspectRatioListener)
  {
    boolean bool;
    if (contentFrame != null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    contentFrame.setAspectRatioListener(paramAspectRatioListener);
  }
  
  public void setControlDispatcher(ControlDispatcher paramControlDispatcher)
  {
    boolean bool;
    if (controller != null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    controller.setControlDispatcher(paramControlDispatcher);
  }
  
  public void setControllerAutoShow(boolean paramBoolean)
  {
    controllerAutoShow = paramBoolean;
  }
  
  public void setControllerHideDuringAds(boolean paramBoolean)
  {
    controllerHideDuringAds = paramBoolean;
  }
  
  public void setControllerHideOnTouch(boolean paramBoolean)
  {
    boolean bool;
    if (controller != null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    controllerHideOnTouch = paramBoolean;
  }
  
  public void setControllerShowTimeoutMs(int paramInt)
  {
    boolean bool;
    if (controller != null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    controllerShowTimeoutMs = paramInt;
    if (controller.isVisible()) {
      showController();
    }
  }
  
  public void setControllerVisibilityListener(PlayerControlView.VisibilityListener paramVisibilityListener)
  {
    boolean bool;
    if (controller != null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    controller.setVisibilityListener(paramVisibilityListener);
  }
  
  public void setCustomErrorMessage(CharSequence paramCharSequence)
  {
    boolean bool;
    if (errorMessageView != null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    customErrorMessage = paramCharSequence;
    updateErrorMessage();
  }
  
  public void setDefaultArtwork(Bitmap paramBitmap)
  {
    if (paramBitmap == null) {
      paramBitmap = null;
    } else {
      paramBitmap = new BitmapDrawable(getResources(), paramBitmap);
    }
    setDefaultArtwork(paramBitmap);
  }
  
  public void setDefaultArtwork(Drawable paramDrawable)
  {
    if (defaultArtwork != paramDrawable)
    {
      defaultArtwork = paramDrawable;
      updateForCurrentTrackSelections(false);
    }
  }
  
  public void setErrorMessageProvider(ErrorMessageProvider paramErrorMessageProvider)
  {
    if (errorMessageProvider != paramErrorMessageProvider)
    {
      errorMessageProvider = paramErrorMessageProvider;
      updateErrorMessage();
    }
  }
  
  public void setExtraAdGroupMarkers(long[] paramArrayOfLong, boolean[] paramArrayOfBoolean)
  {
    boolean bool;
    if (controller != null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    controller.setExtraAdGroupMarkers(paramArrayOfLong, paramArrayOfBoolean);
  }
  
  public void setFastForwardIncrementMs(int paramInt)
  {
    boolean bool;
    if (controller != null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    controller.setFastForwardIncrementMs(paramInt);
  }
  
  public void setKeepContentOnPlayerReset(boolean paramBoolean)
  {
    if (keepContentOnPlayerReset != paramBoolean)
    {
      keepContentOnPlayerReset = paramBoolean;
      updateForCurrentTrackSelections(false);
    }
  }
  
  public void setPlaybackPreparer(PlaybackPreparer paramPlaybackPreparer)
  {
    boolean bool;
    if (controller != null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    controller.setPlaybackPreparer(paramPlaybackPreparer);
  }
  
  public void setPlayer(Player paramPlayer)
  {
    boolean bool;
    if (Looper.myLooper() == Looper.getMainLooper()) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    if ((paramPlayer != null) && (paramPlayer.getApplicationLooper() != Looper.getMainLooper())) {
      bool = false;
    } else {
      bool = true;
    }
    Assertions.checkArgument(bool);
    if (player == paramPlayer) {
      return;
    }
    Object localObject;
    if (player != null)
    {
      player.removeListener(componentListener);
      localObject = player.getVideoComponent();
      if (localObject != null)
      {
        ((Player.VideoComponent)localObject).removeVideoListener(componentListener);
        if ((surfaceView instanceof TextureView)) {
          ((Player.VideoComponent)localObject).clearVideoTextureView((TextureView)surfaceView);
        } else if ((surfaceView instanceof SphericalSurfaceView)) {
          ((SphericalSurfaceView)surfaceView).setVideoComponent(null);
        } else if ((surfaceView instanceof SurfaceView)) {
          ((Player.VideoComponent)localObject).clearVideoSurfaceView((SurfaceView)surfaceView);
        }
      }
      localObject = player.getTextComponent();
      if (localObject != null) {
        ((Player.TextComponent)localObject).removeTextOutput(componentListener);
      }
    }
    player = paramPlayer;
    if (useController) {
      controller.setPlayer(paramPlayer);
    }
    if (subtitleView != null) {
      subtitleView.setCues(null);
    }
    updateBuffering();
    updateErrorMessage();
    updateForCurrentTrackSelections(true);
    if (paramPlayer != null)
    {
      localObject = paramPlayer.getVideoComponent();
      if (localObject != null)
      {
        if ((surfaceView instanceof TextureView)) {
          ((Player.VideoComponent)localObject).setVideoTextureView((TextureView)surfaceView);
        } else if ((surfaceView instanceof SphericalSurfaceView)) {
          ((SphericalSurfaceView)surfaceView).setVideoComponent((Player.VideoComponent)localObject);
        } else if ((surfaceView instanceof SurfaceView)) {
          ((Player.VideoComponent)localObject).setVideoSurfaceView((SurfaceView)surfaceView);
        }
        ((Player.VideoComponent)localObject).addVideoListener(componentListener);
      }
      localObject = paramPlayer.getTextComponent();
      if (localObject != null) {
        ((Player.TextComponent)localObject).addTextOutput(componentListener);
      }
      paramPlayer.addListener(componentListener);
      maybeShowController(false);
      return;
    }
    hideController();
  }
  
  public void setRepeatToggleModes(int paramInt)
  {
    boolean bool;
    if (controller != null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    controller.setRepeatToggleModes(paramInt);
  }
  
  public void setResizeMode(int paramInt)
  {
    boolean bool;
    if (contentFrame != null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    contentFrame.setResizeMode(paramInt);
  }
  
  public void setRewindIncrementMs(int paramInt)
  {
    boolean bool;
    if (controller != null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    controller.setRewindIncrementMs(paramInt);
  }
  
  public void setShowBuffering(int paramInt)
  {
    if (showBuffering != paramInt)
    {
      showBuffering = paramInt;
      updateBuffering();
    }
  }
  
  public void setShowBuffering(boolean paramBoolean)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
  }
  
  public void setShowMultiWindowTimeBar(boolean paramBoolean)
  {
    boolean bool;
    if (controller != null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    controller.setShowMultiWindowTimeBar(paramBoolean);
  }
  
  public void setShowShuffleButton(boolean paramBoolean)
  {
    boolean bool;
    if (controller != null) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    controller.setShowShuffleButton(paramBoolean);
  }
  
  public void setShutterBackgroundColor(int paramInt)
  {
    if (shutterView != null) {
      shutterView.setBackgroundColor(paramInt);
    }
  }
  
  public void setUseArtwork(boolean paramBoolean)
  {
    boolean bool;
    if ((paramBoolean) && (artworkView == null)) {
      bool = false;
    } else {
      bool = true;
    }
    Assertions.checkState(bool);
    if (useArtwork != paramBoolean)
    {
      useArtwork = paramBoolean;
      updateForCurrentTrackSelections(false);
    }
  }
  
  public void setUseController(boolean paramBoolean)
  {
    boolean bool;
    if ((paramBoolean) && (controller == null)) {
      bool = false;
    } else {
      bool = true;
    }
    Assertions.checkState(bool);
    if (useController == paramBoolean) {
      return;
    }
    useController = paramBoolean;
    if (paramBoolean)
    {
      controller.setPlayer(player);
      return;
    }
    if (controller != null)
    {
      controller.hide();
      controller.setPlayer(null);
    }
  }
  
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    if ((surfaceView instanceof SurfaceView)) {
      surfaceView.setVisibility(paramInt);
    }
  }
  
  public void showController()
  {
    showController(shouldShowControllerIndefinitely());
  }
  
  final class ComponentListener
    implements Player.EventListener, TextOutput, VideoListener, View.OnLayoutChangeListener, SphericalSurfaceView.SurfaceListener, SingleTapListener
  {
    private ComponentListener() {}
    
    public void onCues(List paramList)
    {
      if (subtitleView != null) {
        subtitleView.onCues(paramList);
      }
    }
    
    public void onLayoutChange(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8)
    {
      PlayerView.applyTextureViewRotation((TextureView)paramView, textureViewRotation);
    }
    
    public void onPlayerStateChanged(boolean paramBoolean, int paramInt)
    {
      PlayerView.this.updateBuffering();
      PlayerView.this.updateErrorMessage();
      if ((PlayerView.this.isPlayingAd()) && (controllerHideDuringAds))
      {
        hideController();
        return;
      }
      PlayerView.this.maybeShowController(false);
    }
    
    public void onPositionDiscontinuity(int paramInt)
    {
      if ((PlayerView.this.isPlayingAd()) && (controllerHideDuringAds)) {
        hideController();
      }
    }
    
    public void onRenderedFirstFrame()
    {
      if (shutterView != null) {
        shutterView.setVisibility(4);
      }
    }
    
    public boolean onSingleTapUp(MotionEvent paramMotionEvent)
    {
      return PlayerView.this.toggleControllerVisibility();
    }
    
    public void onTracksChanged(TrackGroupArray paramTrackGroupArray, TrackSelectionArray paramTrackSelectionArray)
    {
      PlayerView.this.updateForCurrentTrackSelections(false);
    }
    
    public void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
    {
      if (contentFrame == null) {
        return;
      }
      if ((paramInt2 != 0) && (paramInt1 != 0)) {
        paramFloat = paramInt1 * paramFloat / paramInt2;
      } else {
        paramFloat = 1.0F;
      }
      if ((surfaceView instanceof TextureView))
      {
        float f;
        if (paramInt3 != 90)
        {
          f = paramFloat;
          if (paramInt3 != 270) {}
        }
        else
        {
          f = 1.0F / paramFloat;
        }
        if (textureViewRotation != 0) {
          surfaceView.removeOnLayoutChangeListener(this);
        }
        PlayerView.access$402(PlayerView.this, paramInt3);
        if (textureViewRotation != 0) {
          surfaceView.addOnLayoutChangeListener(this);
        }
        PlayerView.applyTextureViewRotation((TextureView)surfaceView, textureViewRotation);
        paramFloat = f;
      }
      else if ((surfaceView instanceof SphericalSurfaceView))
      {
        paramFloat = 0.0F;
      }
      contentFrame.setAspectRatio(paramFloat);
    }
    
    public void surfaceChanged(Surface paramSurface)
    {
      if (player != null)
      {
        Player.VideoComponent localVideoComponent = player.getVideoComponent();
        if (localVideoComponent != null) {
          localVideoComponent.setVideoSurface(paramSurface);
        }
      }
    }
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public @interface ShowBuffering {}
}
