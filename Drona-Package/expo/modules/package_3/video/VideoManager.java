package expo.modules.package_3.video;

import android.content.Context;
import com.yqritc.scalablevideoview.ScalableType;
import expo.modules.av.video.VideoViewWrapper;
import java.util.HashMap;
import java.util.Map;
import org.unimodules.core.ExportedModule;
import org.unimodules.core.ModuleRegistry;
import org.unimodules.core.Promise;
import org.unimodules.core.interfaces.services.UIManager;

public class VideoManager
  extends ExportedModule
{
  private static final String NAME = "ExpoVideoManager";
  private ModuleRegistry mModuleRegistry;
  
  public VideoManager(Context paramContext)
  {
    super(paramContext);
  }
  
  private void tryRunWithVideoView(Integer paramInteger, VideoViewCallback paramVideoViewCallback, Promise paramPromise)
  {
    ((UIManager)mModuleRegistry.getModule(UIManager.class)).addUIBlock(paramInteger.intValue(), new VideoManager.2(this, paramVideoViewCallback, paramPromise), VideoViewWrapper.class);
  }
  
  public Map getConstants()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("ScaleNone", Integer.toString(ScalableType.LEFT_TOP.ordinal()));
    localHashMap.put("ScaleToFill", Integer.toString(ScalableType.FIT_XY.ordinal()));
    localHashMap.put("ScaleAspectFit", Integer.toString(ScalableType.FIT_CENTER.ordinal()));
    localHashMap.put("ScaleAspectFill", Integer.toString(ScalableType.CENTER_CROP.ordinal()));
    return localHashMap;
  }
  
  public String getName()
  {
    return "ExpoVideoManager";
  }
  
  public void onCreate(ModuleRegistry paramModuleRegistry)
  {
    mModuleRegistry = paramModuleRegistry;
  }
  
  public void setFullscreen(Integer paramInteger, Boolean paramBoolean, Promise paramPromise)
  {
    tryRunWithVideoView(paramInteger, new VideoManager.1(this, paramPromise, paramBoolean), paramPromise);
  }
  
  abstract interface VideoViewCallback
  {
    public abstract void runWithVideoView(VideoView paramVideoView);
  }
}
