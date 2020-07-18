package expo.modules.barcodescanner.scanners;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.unimodules.interfaces.barcodescanner.BarCodeScanner;
import org.unimodules.interfaces.barcodescanner.BarCodeScannerSettings;

public abstract class ExpoBarCodeScanner
  implements BarCodeScanner
{
  protected List<Integer> mBarCodeTypes;
  protected Context mContext;
  
  ExpoBarCodeScanner(Context paramContext)
  {
    mContext = paramContext;
  }
  
  boolean areNewAndOldBarCodeTypesEqual(List paramList)
  {
    if (mBarCodeTypes == null) {
      return false;
    }
    HashSet localHashSet = new HashSet(mBarCodeTypes);
    paramList = new HashSet(paramList);
    if (localHashSet.size() == paramList.size())
    {
      localHashSet.removeAll(paramList);
      return localHashSet.isEmpty();
    }
    return false;
  }
  
  public abstract boolean isAvailable();
  
  List parseBarCodeTypesFromSettings(BarCodeScannerSettings paramBarCodeScannerSettings)
  {
    Object localObject1 = paramBarCodeScannerSettings.getTypes();
    if ((localObject1 != null) && ((localObject1 instanceof List)))
    {
      paramBarCodeScannerSettings = new ArrayList();
      localObject1 = ((List)localObject1).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        Object localObject2 = ((Iterator)localObject1).next();
        if ((localObject2 instanceof Number)) {
          paramBarCodeScannerSettings.add(Integer.valueOf(((Number)localObject2).intValue()));
        }
      }
      return paramBarCodeScannerSettings;
    }
    return null;
  }
}
