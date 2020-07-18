package expo.modules.capabilities.admob;

import android.content.Context;
import java.util.Arrays;
import java.util.List;
import org.unimodules.core.BasePackage;
import org.unimodules.core.ExportedModule;
import org.unimodules.core.ViewManager;

public class AdMobPackage
  extends BasePackage
{
  public AdMobPackage() {}
  
  public List createExportedModules(Context paramContext)
  {
    return Arrays.asList(new ExportedModule[] { new AdMobModule(paramContext), new AdMobInterstitialAdModule(paramContext), new AdMobRewardedVideoAdModule(paramContext) });
  }
  
  public List createViewManagers(Context paramContext)
  {
    return Arrays.asList(new ViewManager[] { new AdMobBannerViewManager(), new PublisherBannerViewManager() });
  }
}
