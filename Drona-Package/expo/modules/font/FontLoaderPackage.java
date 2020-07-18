package expo.modules.font;

import android.content.Context;
import java.util.Collections;
import java.util.List;
import org.unimodules.core.BasePackage;

public class FontLoaderPackage
  extends BasePackage
{
  public FontLoaderPackage() {}
  
  public List createExportedModules(Context paramContext)
  {
    return Collections.singletonList(new FontLoaderModule(paramContext));
  }
}
