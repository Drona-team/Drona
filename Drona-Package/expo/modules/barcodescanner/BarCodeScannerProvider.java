package expo.modules.barcodescanner;

import android.content.Context;
import expo.modules.barcodescanner.scanners.ExpoBarCodeScanner;
import expo.modules.barcodescanner.scanners.GMVBarCodeScanner;
import expo.modules.barcodescanner.scanners.ZxingBarCodeScanner;
import java.util.Collections;
import java.util.List;
import org.unimodules.core.interfaces.InternalModule;
import org.unimodules.interfaces.barcodescanner.BarCodeScanner;

public class BarCodeScannerProvider
  implements InternalModule, org.unimodules.interfaces.barcodescanner.BarCodeScannerProvider
{
  public BarCodeScannerProvider() {}
  
  public BarCodeScanner createBarCodeDetectorWithContext(Context paramContext)
  {
    GMVBarCodeScanner localGMVBarCodeScanner = new GMVBarCodeScanner(paramContext);
    if (!localGMVBarCodeScanner.isAvailable()) {
      return new ZxingBarCodeScanner(paramContext);
    }
    return localGMVBarCodeScanner;
  }
  
  public List getExportedInterfaces()
  {
    return Collections.singletonList(org.unimodules.interfaces.barcodescanner.BarCodeScannerProvider.class);
  }
}
