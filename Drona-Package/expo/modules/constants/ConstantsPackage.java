package expo.modules.constants;

import android.content.Context;
import java.util.Collections;
import java.util.List;
import org.unimodules.core.BasePackage;

public class ConstantsPackage
  extends BasePackage
{
  public ConstantsPackage() {}
  
  public List createExportedModules(Context paramContext)
  {
    return Collections.singletonList(new ConstantsModule(paramContext));
  }
  
  public List createInternalModules(Context paramContext)
  {
    return Collections.singletonList(new ConstantsService(paramContext));
  }
}
