package expo.modules.package_3.video;

public abstract class FullscreenVideoPlayerPresentationChangeProgressListener
  implements FullscreenVideoPlayerPresentationChangeListener
{
  public FullscreenVideoPlayerPresentationChangeProgressListener() {}
  
  public void onFullscreenPlayerDidDismiss() {}
  
  public void onFullscreenPlayerDidPresent() {}
  
  void onFullscreenPlayerPresentationError(String paramString) {}
  
  void onFullscreenPlayerPresentationInterrupted() {}
  
  void onFullscreenPlayerPresentationTriedToInterrupt() {}
  
  public void onFullscreenPlayerWillDismiss() {}
  
  public void onFullscreenPlayerWillPresent() {}
}
