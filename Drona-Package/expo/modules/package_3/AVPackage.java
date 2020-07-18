package expo.modules.package_3;

import android.content.Context;
import expo.modules.package_3.player.datasource.SharedCookiesDataSourceFactoryProvider;
import expo.modules.package_3.video.VideoManager;
import expo.modules.package_3.video.VideoViewManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.unimodules.core.BasePackage;
import org.unimodules.core.ExportedModule;
import org.unimodules.core.interfaces.InternalModule;

public class AVPackage
  extends BasePackage
{
  public AVPackage() {}
  
  public List createExportedModules(Context paramContext)
  {
    return Arrays.asList(new ExportedModule[] { new AVModule(paramContext), new VideoManager(paramContext) });
  }
  
  public List createInternalModules(Context paramContext)
  {
    return Arrays.asList(new InternalModule[] { new AVManager(paramContext), new SharedCookiesDataSourceFactoryProvider() });
  }
  
  public List createViewManagers(Context paramContext)
  {
    return Collections.singletonList(new VideoViewManager());
  }
}
