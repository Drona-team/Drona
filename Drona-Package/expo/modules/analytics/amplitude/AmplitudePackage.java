package expo.modules.analytics.amplitude;

import android.content.Context;
import java.util.Collections;
import java.util.List;
import org.unimodules.core.BasePackage;

public class AmplitudePackage
  extends BasePackage
{
  public AmplitudePackage() {}
  
  public List createExportedModules(Context paramContext)
  {
    return Collections.singletonList(new AmplitudeModule(paramContext));
  }
}
