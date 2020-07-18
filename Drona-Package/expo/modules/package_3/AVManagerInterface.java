package expo.modules.package_3;

import android.content.Context;
import expo.modules.package_3.video.VideoView;
import org.unimodules.core.ModuleRegistry;
import org.unimodules.core.Promise;
import org.unimodules.core.arguments.ReadableArguments;

public abstract interface AVManagerInterface
{
  public abstract void abandonAudioFocusIfUnused();
  
  public abstract void acquireAudioFocus()
    throws AudioFocusNotAcquiredException;
  
  public abstract void getAudioRecordingStatus(Promise paramPromise);
  
  public abstract Context getContext();
  
  public abstract ModuleRegistry getModuleRegistry();
  
  public abstract void getStatusForSound(Integer paramInteger, Promise paramPromise);
  
  public abstract void getStatusForVideo(Integer paramInteger, Promise paramPromise);
  
  public abstract float getVolumeForDuckAndFocus(boolean paramBoolean, float paramFloat);
  
  public abstract void loadForSound(ReadableArguments paramReadableArguments1, ReadableArguments paramReadableArguments2, Promise paramPromise);
  
  public abstract void loadForVideo(Integer paramInteger, ReadableArguments paramReadableArguments1, ReadableArguments paramReadableArguments2, Promise paramPromise);
  
  public abstract void pauseAudioRecording(Promise paramPromise);
  
  public abstract void prepareAudioRecorder(ReadableArguments paramReadableArguments, Promise paramPromise);
  
  public abstract void registerVideoViewForAudioLifecycle(VideoView paramVideoView);
  
  public abstract void replaySound(Integer paramInteger, ReadableArguments paramReadableArguments, Promise paramPromise);
  
  public abstract void replayVideo(Integer paramInteger, ReadableArguments paramReadableArguments, Promise paramPromise);
  
  public abstract void setAudioIsEnabled(Boolean paramBoolean);
  
  public abstract void setAudioMode(ReadableArguments paramReadableArguments);
  
  public abstract void setStatusForSound(Integer paramInteger, ReadableArguments paramReadableArguments, Promise paramPromise);
  
  public abstract void setStatusForVideo(Integer paramInteger, ReadableArguments paramReadableArguments, Promise paramPromise);
  
  public abstract void startAudioRecording(Promise paramPromise);
  
  public abstract void stopAudioRecording(Promise paramPromise);
  
  public abstract void unloadAudioRecorder(Promise paramPromise);
  
  public abstract void unloadForSound(Integer paramInteger, Promise paramPromise);
  
  public abstract void unloadForVideo(Integer paramInteger, Promise paramPromise);
  
  public abstract void unregisterVideoViewForAudioLifecycle(VideoView paramVideoView);
}
