package expo.modules.package_3.video;

import android.app.Dialog;
import android.content.Context;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import java.lang.ref.WeakReference;
import org.unimodules.core.ModuleRegistry;
import org.unimodules.core.interfaces.services.KeepAwakeManager;

public class FullscreenVideoPlayer
  extends Dialog
{
  private final FrameLayout mContainerView;
  private Handler mKeepScreenOnHandler;
  private Runnable mKeepScreenOnUpdater;
  private ModuleRegistry mModuleRegistry;
  private FrameLayout mParent;
  private WeakReference<expo.modules.av.video.FullscreenVideoPlayerPresentationChangeListener> mUpdateListener;
  private final VideoView mVideoView;
  
  FullscreenVideoPlayer(Context paramContext, VideoView paramVideoView, ModuleRegistry paramModuleRegistry)
  {
    super(paramContext, 16973834);
    mModuleRegistry = paramModuleRegistry;
    setCancelable(false);
    mVideoView = paramVideoView;
    mContainerView = new FrameLayout(paramContext);
    setContentView(mContainerView, generateDefaultLayoutParams());
    mKeepScreenOnUpdater = new KeepScreenOnUpdater();
    mKeepScreenOnHandler = new Handler();
  }
  
  private FrameLayout.LayoutParams generateDefaultLayoutParams()
  {
    return new FrameLayout.LayoutParams(-1, -1);
  }
  
  public void dismiss()
  {
    mVideoView.setOverridingUseNativeControls(null);
    FullscreenVideoPlayerPresentationChangeListener localFullscreenVideoPlayerPresentationChangeListener = (FullscreenVideoPlayerPresentationChangeListener)mUpdateListener.get();
    if (localFullscreenVideoPlayerPresentationChangeListener != null) {
      localFullscreenVideoPlayerPresentationChangeListener.onFullscreenPlayerWillDismiss();
    }
    super.dismiss();
  }
  
  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    FullscreenVideoPlayerPresentationChangeListener localFullscreenVideoPlayerPresentationChangeListener = (FullscreenVideoPlayerPresentationChangeListener)mUpdateListener.get();
    if (localFullscreenVideoPlayerPresentationChangeListener != null) {
      localFullscreenVideoPlayerPresentationChangeListener.onFullscreenPlayerDidPresent();
    }
    mVideoView.setOverridingUseNativeControls(Boolean.valueOf(true));
    mKeepScreenOnHandler.post(mKeepScreenOnUpdater);
  }
  
  public void onBackPressed()
  {
    super.onBackPressed();
    if (isShowing()) {
      dismiss();
    }
  }
  
  protected void onStart()
  {
    mParent = ((FrameLayout)mVideoView.getParent());
    mParent.removeView(mVideoView);
    mContainerView.addView(mVideoView, generateDefaultLayoutParams());
    super.onStart();
  }
  
  protected void onStop()
  {
    mKeepScreenOnHandler.removeCallbacks(mKeepScreenOnUpdater);
    mContainerView.removeView(mVideoView);
    mParent.addView(mVideoView, generateDefaultLayoutParams());
    mParent.requestLayout();
    mParent = null;
    super.onStop();
    FullscreenVideoPlayerPresentationChangeListener localFullscreenVideoPlayerPresentationChangeListener = (FullscreenVideoPlayerPresentationChangeListener)mUpdateListener.get();
    if (localFullscreenVideoPlayerPresentationChangeListener != null) {
      localFullscreenVideoPlayerPresentationChangeListener.onFullscreenPlayerDidDismiss();
    }
  }
  
  void setUpdateListener(FullscreenVideoPlayerPresentationChangeListener paramFullscreenVideoPlayerPresentationChangeListener)
  {
    mUpdateListener = new WeakReference(paramFullscreenVideoPlayerPresentationChangeListener);
  }
  
  public void show()
  {
    FullscreenVideoPlayerPresentationChangeListener localFullscreenVideoPlayerPresentationChangeListener = (FullscreenVideoPlayerPresentationChangeListener)mUpdateListener.get();
    if (localFullscreenVideoPlayerPresentationChangeListener != null) {
      localFullscreenVideoPlayerPresentationChangeListener.onFullscreenPlayerWillPresent();
    }
    super.show();
  }
  
  class KeepScreenOnUpdater
    implements Runnable
  {
    private static final long UPDATE_KEEP_SCREEN_ON_FLAG_MS = 200L;
    private final WeakReference<expo.modules.av.video.FullscreenVideoPlayer> mFullscreenPlayer;
    
    KeepScreenOnUpdater()
    {
      mFullscreenPlayer = new WeakReference(this$1);
    }
    
    public void run()
    {
      FullscreenVideoPlayer localFullscreenVideoPlayer = (FullscreenVideoPlayer)mFullscreenPlayer.get();
      if (localFullscreenVideoPlayer != null)
      {
        Window localWindow = localFullscreenVideoPlayer.getWindow();
        if (localWindow != null)
        {
          boolean bool = mVideoView.getStatus().containsKey("isPlaying");
          int j = 1;
          int i;
          if ((bool) && (mVideoView.getStatus().getBoolean("isPlaying"))) {
            i = 1;
          } else {
            i = 0;
          }
          Object localObject = mModuleRegistry;
          if (localObject != null)
          {
            localObject = (KeepAwakeManager)((ModuleRegistry)localObject).getModule(KeepAwakeManager.class);
            if ((localObject == null) || (!((KeepAwakeManager)localObject).isActivated())) {
              j = 0;
            }
            if ((i == 0) && (j == 0)) {
              localWindow.addFlags(128);
            } else {
              localWindow.addFlags(128);
            }
          }
        }
        mKeepScreenOnHandler.postDelayed(this, 200L);
      }
    }
  }
}
