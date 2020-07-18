package expo.modules.package_3;

import android.content.Context;
import org.unimodules.core.ExportedModule;
import org.unimodules.core.ModuleRegistry;
import org.unimodules.core.Promise;
import org.unimodules.core.arguments.ReadableArguments;
import org.unimodules.interfaces.permissions.Permissions;
import org.unimodules.interfaces.permissions.Permissions.-CC;

public class AVModule
  extends ExportedModule
{
  private AVManagerInterface mAVManager;
  private ModuleRegistry mModuleRegistry;
  
  public AVModule(Context paramContext)
  {
    super(paramContext);
  }
  
  public void getAudioRecordingStatus(Promise paramPromise)
  {
    mAVManager.getAudioRecordingStatus(paramPromise);
  }
  
  public String getName()
  {
    return "ExponentAV";
  }
  
  public void getPermissionsAsync(Promise paramPromise)
  {
    Permissions.-CC.getPermissionsWithPermissionsManager((Permissions)mModuleRegistry.getModule(Permissions.class), paramPromise, new String[] { "android.permission.RECORD_AUDIO" });
  }
  
  public void getStatusForSound(Integer paramInteger, Promise paramPromise)
  {
    mAVManager.getStatusForSound(paramInteger, paramPromise);
  }
  
  public void getStatusForVideo(Integer paramInteger, Promise paramPromise)
  {
    mAVManager.getStatusForVideo(paramInteger, paramPromise);
  }
  
  public void loadForSound(ReadableArguments paramReadableArguments1, ReadableArguments paramReadableArguments2, Promise paramPromise)
  {
    mAVManager.loadForSound(paramReadableArguments1, paramReadableArguments2, paramPromise);
  }
  
  public void loadForVideo(Integer paramInteger, ReadableArguments paramReadableArguments1, ReadableArguments paramReadableArguments2, Promise paramPromise)
  {
    mAVManager.loadForVideo(paramInteger, paramReadableArguments1, paramReadableArguments2, paramPromise);
  }
  
  public void onCreate(ModuleRegistry paramModuleRegistry)
  {
    mModuleRegistry = paramModuleRegistry;
    mAVManager = ((AVManagerInterface)paramModuleRegistry.getModule(expo.modules.av.AVManagerInterface.class));
  }
  
  public void pauseAudioRecording(Promise paramPromise)
  {
    mAVManager.pauseAudioRecording(paramPromise);
  }
  
  public void prepareAudioRecorder(ReadableArguments paramReadableArguments, Promise paramPromise)
  {
    mAVManager.prepareAudioRecorder(paramReadableArguments, paramPromise);
  }
  
  public void replaySound(Integer paramInteger, ReadableArguments paramReadableArguments, Promise paramPromise)
  {
    mAVManager.replaySound(paramInteger, paramReadableArguments, paramPromise);
  }
  
  public void replayVideo(Integer paramInteger, ReadableArguments paramReadableArguments, Promise paramPromise)
  {
    mAVManager.replayVideo(paramInteger, paramReadableArguments, paramPromise);
  }
  
  public void requestPermissionsAsync(Promise paramPromise)
  {
    Permissions.-CC.askForPermissionsWithPermissionsManager((Permissions)mModuleRegistry.getModule(Permissions.class), paramPromise, new String[] { "android.permission.RECORD_AUDIO" });
  }
  
  public void setAudioIsEnabled(Boolean paramBoolean, Promise paramPromise)
  {
    mAVManager.setAudioIsEnabled(paramBoolean);
    paramPromise.resolve(null);
  }
  
  public void setAudioMode(ReadableArguments paramReadableArguments, Promise paramPromise)
  {
    mAVManager.setAudioMode(paramReadableArguments);
    paramPromise.resolve(null);
  }
  
  public void setStatusForSound(Integer paramInteger, ReadableArguments paramReadableArguments, Promise paramPromise)
  {
    mAVManager.setStatusForSound(paramInteger, paramReadableArguments, paramPromise);
  }
  
  public void setStatusForVideo(Integer paramInteger, ReadableArguments paramReadableArguments, Promise paramPromise)
  {
    mAVManager.setStatusForVideo(paramInteger, paramReadableArguments, paramPromise);
  }
  
  public void startAudioRecording(Promise paramPromise)
  {
    mAVManager.startAudioRecording(paramPromise);
  }
  
  public void stopAudioRecording(Promise paramPromise)
  {
    mAVManager.stopAudioRecording(paramPromise);
  }
  
  public void unloadAudioRecorder(Promise paramPromise)
  {
    mAVManager.unloadAudioRecorder(paramPromise);
  }
  
  public void unloadForSound(Integer paramInteger, Promise paramPromise)
  {
    mAVManager.unloadForSound(paramInteger, paramPromise);
  }
  
  public void unloadForVideo(Integer paramInteger, Promise paramPromise)
  {
    mAVManager.unloadForVideo(paramInteger, paramPromise);
  }
}
