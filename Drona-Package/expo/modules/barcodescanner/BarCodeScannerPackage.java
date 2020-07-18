package expo.modules.barcodescanner;

import android.content.Context;
import java.util.Collections;
import java.util.List;
import org.unimodules.core.BasePackage;

public class BarCodeScannerPackage
  extends BasePackage
{
  public BarCodeScannerPackage() {}
  
  public List createExportedModules(Context paramContext)
  {
    return Collections.singletonList(new BarCodeScannerModule(paramContext));
  }
  
  public List createInternalModules(Context paramContext)
  {
    return Collections.singletonList(new BarCodeScannerProvider());
  }
  
  public List createViewManagers(Context paramContext)
  {
    return Collections.singletonList(new BarCodeScannerViewManager());
  }
}
