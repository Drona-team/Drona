package expo.modules.package_3.video;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.BaseBundle;
import android.os.Bundle;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.yqritc.scalablevideoview.ScalableType;
import expo.modules.package_3.AudioEventHandler;
import expo.modules.package_3.player.PlayerData;
import expo.modules.package_3.player.PlayerData.FullscreenPresenter;
import expo.modules.package_3.player.PlayerData.StatusUpdateListener;
import org.unimodules.core.ModuleRegistry;
import org.unimodules.core.Promise;
import org.unimodules.core.arguments.ReadableArguments;
import org.unimodules.core.interfaces.services.EventEmitter;

@SuppressLint({"ViewConstructor"})
public class VideoView
  extends FrameLayout
  implements AudioEventHandler, FullscreenVideoPlayerPresentationChangeListener, PlayerData.FullscreenPresenter
{
  private final expo.modules.package_3.AVManagerInterface mAVModule;
  private EventEmitter mEventEmitter;
  private FullscreenVideoPlayer mFullscreenPlayer = null;
  private FullscreenVideoPlayerPresentationChangeProgressListener mFullscreenPlayerPresentationChangeProgressListener = null;
  private FullscreenVideoPlayerPresentationChangeProgressListener mFullscreenVideoPlayerPresentationOnLoadChangeListener = null;
  private boolean mIsLoaded = false;
  private MediaController mMediaController = null;
  private final Runnable mMediaControllerUpdater = new Runnable()
  {
    public void run()
    {
      if (mMediaController != null) {
        mMediaController.updateControls();
      }
    }
  };
  private Boolean mOverridingUseNativeControls = null;
  private PlayerData mPlayerData = null;
  private ScalableType mResizeMode = ScalableType.LEFT_TOP;
  private boolean mShouldShowFullscreenPlayerOnLoad = false;
  private Bundle mStatusToSet = new Bundle();
  private final PlayerData.StatusUpdateListener mStatusUpdateListener = new PlayerData.StatusUpdateListener()
  {
    public void onStatusUpdate(Bundle paramAnonymousBundle)
    {
      post(mMediaControllerUpdater);
      mEventEmitter.emit(VideoView.this.getReactId(), VideoViewManager.Events.EVENT_STATUS_UPDATE.toString(), paramAnonymousBundle);
    }
  };
  private boolean mUseNativeControls = false;
  private VideoTextureView mVideoTextureView = null;
  private VideoViewWrapper mVideoViewWrapper;
  private Pair<Integer, Integer> mVideoWidthHeight = null;
  
  public VideoView(Context paramContext, VideoViewWrapper paramVideoViewWrapper, ModuleRegistry paramModuleRegistry)
  {
    super(paramContext);
    mVideoViewWrapper = paramVideoViewWrapper;
    mEventEmitter = ((EventEmitter)paramModuleRegistry.getModule(EventEmitter.class));
    mAVModule = ((expo.modules.package_3.AVManagerInterface)paramModuleRegistry.getModule(expo.modules.av.AVManagerInterface.class));
    mAVModule.registerVideoViewForAudioLifecycle(this);
    mVideoTextureView = new VideoTextureView(paramContext, this);
    addView(mVideoTextureView, generateDefaultLayoutParams());
    mFullscreenPlayer = new FullscreenVideoPlayer(paramContext, this, paramModuleRegistry);
    mFullscreenPlayer.setUpdateListener(this);
    mMediaController = new MediaController(getContext());
    mMediaController.setAnchorView(this);
    maybeUpdateMediaControllerForUseNativeControls();
  }
  
  private void callFullscreenCallbackWithUpdate(VideoViewManager.FullscreenPlayerUpdate paramFullscreenPlayerUpdate)
  {
    Bundle localBundle = new Bundle();
    localBundle.putInt("fullscreenUpdate", paramFullscreenPlayerUpdate.getValue());
    localBundle.putBundle("status", getStatus());
    mEventEmitter.emit(getReactId(), VideoViewManager.Events.EVENT_FULLSCREEN_PLAYER_UPDATE.toString(), localBundle);
  }
  
  private void callOnError(String paramString)
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("error", paramString);
    mEventEmitter.emit(getReactId(), VideoViewManager.Events.EVENT_ERROR.toString(), localBundle);
  }
  
  private void callOnReadyForDisplay(Pair paramPair)
  {
    if ((paramPair != null) && (mIsLoaded))
    {
      int i = ((Integer)first).intValue();
      int j = ((Integer)second).intValue();
      if (i != 0)
      {
        if (j == 0) {
          return;
        }
        Bundle localBundle = new Bundle();
        localBundle.putInt("width", i);
        localBundle.putInt("height", j);
        if (i > j) {
          paramPair = "landscape";
        } else {
          paramPair = "portrait";
        }
        localBundle.putString("orientation", paramPair);
        paramPair = new Bundle();
        paramPair.putBundle("naturalSize", localBundle);
        paramPair.putBundle("status", mPlayerData.getStatus());
        mEventEmitter.emit(getReactId(), VideoViewManager.Events.EVENT_READY_FOR_DISPLAY.toString(), paramPair);
      }
    }
  }
  
  private int getReactId()
  {
    return mVideoViewWrapper.getId();
  }
  
  private void saveFullscreenPlayerStateForOnLoad(boolean paramBoolean, FullscreenVideoPlayerPresentationChangeProgressListener paramFullscreenVideoPlayerPresentationChangeProgressListener)
  {
    mShouldShowFullscreenPlayerOnLoad = paramBoolean;
    if (mFullscreenVideoPlayerPresentationOnLoadChangeListener != null) {
      mFullscreenVideoPlayerPresentationOnLoadChangeListener.onFullscreenPlayerPresentationInterrupted();
    }
    mFullscreenVideoPlayerPresentationOnLoadChangeListener = paramFullscreenVideoPlayerPresentationChangeProgressListener;
  }
  
  private boolean shouldUseNativeControls()
  {
    if (mOverridingUseNativeControls != null) {
      return mOverridingUseNativeControls.booleanValue();
    }
    return mUseNativeControls;
  }
  
  public void ensureFullscreenPlayerIsDismissed()
  {
    ensureFullscreenPlayerIsDismissed(null);
  }
  
  public void ensureFullscreenPlayerIsDismissed(FullscreenVideoPlayerPresentationChangeProgressListener paramFullscreenVideoPlayerPresentationChangeProgressListener)
  {
    if (!mIsLoaded)
    {
      saveFullscreenPlayerStateForOnLoad(false, paramFullscreenVideoPlayerPresentationChangeProgressListener);
      return;
    }
    if (mFullscreenPlayerPresentationChangeProgressListener != null)
    {
      if (paramFullscreenVideoPlayerPresentationChangeProgressListener != null) {
        paramFullscreenVideoPlayerPresentationChangeProgressListener.onFullscreenPlayerPresentationTriedToInterrupt();
      }
    }
    else
    {
      if (isBeingPresentedFullscreen())
      {
        if (paramFullscreenVideoPlayerPresentationChangeProgressListener != null) {
          mFullscreenPlayerPresentationChangeProgressListener = paramFullscreenVideoPlayerPresentationChangeProgressListener;
        }
        mFullscreenPlayer.dismiss();
        return;
      }
      if (paramFullscreenVideoPlayerPresentationChangeProgressListener != null) {
        paramFullscreenVideoPlayerPresentationChangeProgressListener.onFullscreenPlayerDidDismiss();
      }
    }
  }
  
  public void ensureFullscreenPlayerIsPresented()
  {
    ensureFullscreenPlayerIsPresented(null);
  }
  
  public void ensureFullscreenPlayerIsPresented(FullscreenVideoPlayerPresentationChangeProgressListener paramFullscreenVideoPlayerPresentationChangeProgressListener)
  {
    if (!mIsLoaded)
    {
      saveFullscreenPlayerStateForOnLoad(true, paramFullscreenVideoPlayerPresentationChangeProgressListener);
      return;
    }
    if (mFullscreenPlayerPresentationChangeProgressListener != null)
    {
      if (paramFullscreenVideoPlayerPresentationChangeProgressListener != null) {
        paramFullscreenVideoPlayerPresentationChangeProgressListener.onFullscreenPlayerPresentationTriedToInterrupt();
      }
    }
    else
    {
      if (!isBeingPresentedFullscreen())
      {
        if (paramFullscreenVideoPlayerPresentationChangeProgressListener != null) {
          mFullscreenPlayerPresentationChangeProgressListener = paramFullscreenVideoPlayerPresentationChangeProgressListener;
        }
        mFullscreenPlayer.show();
        return;
      }
      if (paramFullscreenVideoPlayerPresentationChangeProgressListener != null) {
        paramFullscreenVideoPlayerPresentationChangeProgressListener.onFullscreenPlayerDidPresent();
      }
    }
  }
  
  public Bundle getStatus()
  {
    if (mPlayerData == null) {
      return PlayerData.getUnloadedStatus();
    }
    return mPlayerData.getStatus();
  }
  
  public void handleAudioFocusGained()
  {
    if (mPlayerData != null) {
      mPlayerData.handleAudioFocusGained();
    }
  }
  
  public void handleAudioFocusInterruptionBegan()
  {
    if (mPlayerData != null) {
      mPlayerData.handleAudioFocusInterruptionBegan();
    }
  }
  
  public boolean isBeingPresentedFullscreen()
  {
    return mFullscreenPlayer.isShowing();
  }
  
  public void maybeUpdateMediaControllerForUseNativeControls()
  {
    maybeUpdateMediaControllerForUseNativeControls(true);
  }
  
  public void maybeUpdateMediaControllerForUseNativeControls(boolean paramBoolean)
  {
    if ((mPlayerData != null) && (mMediaController != null))
    {
      mMediaController.updateControls();
      mMediaController.setEnabled(shouldUseNativeControls());
      if ((shouldUseNativeControls()) && (paramBoolean))
      {
        mMediaController.show();
        return;
      }
      mMediaController.hide();
    }
  }
  
  void onDropViewInstance()
  {
    mAVModule.unregisterVideoViewForAudioLifecycle(this);
    unloadPlayerAndMediaController();
  }
  
  public void onFullscreenPlayerDidDismiss()
  {
    mMediaController.updateControls();
    callFullscreenCallbackWithUpdate(VideoViewManager.FullscreenPlayerUpdate.FULLSCREEN_PLAYER_DID_DISMISS);
    if (mFullscreenPlayerPresentationChangeProgressListener != null)
    {
      mFullscreenPlayerPresentationChangeProgressListener.onFullscreenPlayerDidDismiss();
      mFullscreenPlayerPresentationChangeProgressListener = null;
    }
  }
  
  public void onFullscreenPlayerDidPresent()
  {
    mMediaController.updateControls();
    callFullscreenCallbackWithUpdate(VideoViewManager.FullscreenPlayerUpdate.FULLSCREEN_PLAYER_DID_PRESENT);
    if (mFullscreenPlayerPresentationChangeProgressListener != null)
    {
      mFullscreenPlayerPresentationChangeProgressListener.onFullscreenPlayerDidPresent();
      mFullscreenPlayerPresentationChangeProgressListener = null;
    }
  }
  
  public void onFullscreenPlayerWillDismiss()
  {
    callFullscreenCallbackWithUpdate(VideoViewManager.FullscreenPlayerUpdate.FULLSCREEN_PLAYER_WILL_DISMISS);
    if (mFullscreenPlayerPresentationChangeProgressListener != null) {
      mFullscreenPlayerPresentationChangeProgressListener.onFullscreenPlayerWillDismiss();
    }
  }
  
  public void onFullscreenPlayerWillPresent()
  {
    callFullscreenCallbackWithUpdate(VideoViewManager.FullscreenPlayerUpdate.FULLSCREEN_PLAYER_WILL_PRESENT);
    if (mFullscreenPlayerPresentationChangeProgressListener != null) {
      mFullscreenPlayerPresentationChangeProgressListener.onFullscreenPlayerWillPresent();
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if ((paramBoolean) && (mPlayerData != null)) {
      mVideoTextureView.scaleVideoSize(mPlayerData.getVideoWidthHeight(), mResizeMode);
    }
  }
  
  public void onPause()
  {
    if (mPlayerData != null)
    {
      ensureFullscreenPlayerIsDismissed();
      mPlayerData.onPause();
    }
  }
  
  public void onResume()
  {
    if (mPlayerData != null) {
      mPlayerData.onResume();
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((shouldUseNativeControls()) && (mMediaController != null)) {
      mMediaController.show();
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public void pauseImmediately()
  {
    if (mPlayerData != null) {
      mPlayerData.pauseImmediately();
    }
  }
  
  public boolean requiresAudioFocus()
  {
    return (mPlayerData != null) && (mPlayerData.requiresAudioFocus());
  }
  
  public void setFullscreenMode(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      ensureFullscreenPlayerIsPresented();
      return;
    }
    ensureFullscreenPlayerIsDismissed();
  }
  
  void setOverridingUseNativeControls(Boolean paramBoolean)
  {
    mOverridingUseNativeControls = paramBoolean;
    maybeUpdateMediaControllerForUseNativeControls();
  }
  
  void setResizeMode(ScalableType paramScalableType)
  {
    mResizeMode = paramScalableType;
    if (mPlayerData != null) {
      mVideoTextureView.scaleVideoSize(mPlayerData.getVideoWidthHeight(), mResizeMode);
    }
  }
  
  public void setSource(ReadableArguments paramReadableArguments1, ReadableArguments paramReadableArguments2, Promise paramPromise)
  {
    PlayerData localPlayerData = mPlayerData;
    Object localObject = null;
    if (localPlayerData != null)
    {
      mStatusToSet.putAll(mPlayerData.getStatus());
      mPlayerData.release();
      mPlayerData = null;
      mIsLoaded = false;
    }
    if (paramReadableArguments2 != null) {
      mStatusToSet.putAll(paramReadableArguments2.toBundle());
    }
    paramReadableArguments2 = localObject;
    if (paramReadableArguments1 != null) {
      paramReadableArguments2 = paramReadableArguments1.getString("uri");
    }
    if (paramReadableArguments2 == null)
    {
      if (paramPromise != null) {
        paramPromise.resolve(PlayerData.getUnloadedStatus());
      }
    }
    else
    {
      mEventEmitter.emit(getReactId(), VideoViewManager.Events.EVENT_LOAD_START.toString(), new Bundle());
      paramReadableArguments2 = new Bundle();
      paramReadableArguments2.putAll(mStatusToSet);
      mStatusToSet = new Bundle();
      mPlayerData = PlayerData.createUnloadedPlayerData(mAVModule, getContext(), paramReadableArguments1, paramReadableArguments2);
      mPlayerData.setErrorListener(new VideoView.3(this));
      mPlayerData.setVideoSizeUpdateListener(new VideoView.4(this));
      mPlayerData.setFullscreenPresenter(this);
      mPlayerData.load(paramReadableArguments2, new VideoView.5(this, paramPromise));
    }
  }
  
  public void setStatus(ReadableArguments paramReadableArguments, Promise paramPromise)
  {
    paramReadableArguments = paramReadableArguments.toBundle();
    mStatusToSet.putAll(paramReadableArguments);
    if (mPlayerData != null)
    {
      new Bundle().putAll(mStatusToSet);
      mStatusToSet = new Bundle();
      mPlayerData.setStatus(paramReadableArguments, paramPromise);
      return;
    }
    if (paramPromise != null) {
      paramPromise.resolve(PlayerData.getUnloadedStatus());
    }
  }
  
  void setUseNativeControls(boolean paramBoolean)
  {
    mUseNativeControls = paramBoolean;
    maybeUpdateMediaControllerForUseNativeControls();
  }
  
  public void tryUpdateVideoSurface(Surface paramSurface)
  {
    if (mPlayerData != null) {
      mPlayerData.tryUpdateVideoSurface(paramSurface);
    }
  }
  
  public void unloadPlayerAndMediaController()
  {
    ensureFullscreenPlayerIsDismissed();
    if (mMediaController != null)
    {
      mMediaController.hide();
      mMediaController.setEnabled(false);
      mMediaController.setAnchorView(null);
      mMediaController = null;
    }
    if (mPlayerData != null)
    {
      mPlayerData.release();
      mPlayerData = null;
    }
    mIsLoaded = false;
  }
  
  public void updateVolumeMuteAndDuck()
  {
    if (mPlayerData != null) {
      mPlayerData.updateVolumeMuteAndDuck();
    }
  }
}
