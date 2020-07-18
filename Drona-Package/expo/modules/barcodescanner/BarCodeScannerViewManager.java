package expo.modules.barcodescanner;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import org.unimodules.core.ModuleRegistry;
import org.unimodules.core.ViewManager;
import org.unimodules.core.ViewManager.ViewManagerType;
import org.unimodules.interfaces.barcodescanner.BarCodeScannerSettings;

public class BarCodeScannerViewManager
  extends ViewManager<BarCodeScannerView>
{
  private static final String PAGE_KEY = "ExpoBarCodeScannerView";
  private ModuleRegistry mModuleRegistry;
  
  public BarCodeScannerViewManager() {}
  
  public BarCodeScannerView createViewInstance(Context paramContext)
  {
    return new BarCodeScannerView(paramContext, mModuleRegistry);
  }
  
  public List getExportedEventNames()
  {
    ArrayList localArrayList = new ArrayList(Events.values().length);
    Events[] arrayOfEvents = Events.values();
    int j = arrayOfEvents.length;
    int i = 0;
    while (i < j)
    {
      localArrayList.add(arrayOfEvents[i].toString());
      i += 1;
    }
    return localArrayList;
  }
  
  public String getName()
  {
    return "ExpoBarCodeScannerView";
  }
  
  public ViewManager.ViewManagerType getViewManagerType()
  {
    return ViewManager.ViewManagerType.GROUP;
  }
  
  public void onCreate(ModuleRegistry paramModuleRegistry)
  {
    mModuleRegistry = paramModuleRegistry;
  }
  
  public void setBarCodeTypes(BarCodeScannerView paramBarCodeScannerView, final ArrayList paramArrayList)
  {
    if (paramArrayList == null) {
      return;
    }
    paramBarCodeScannerView.setBarCodeScannerSettings(new BarCodeScannerSettings() {});
  }
  
  public void setType(BarCodeScannerView paramBarCodeScannerView, int paramInt)
  {
    paramBarCodeScannerView.setCameraType(paramInt);
  }
  
  public static enum Events
  {
    EVENT_ON_BAR_CODE_SCANNED("onBarCodeScanned");
    
    private final String mName;
    
    private Events(String paramString)
    {
      mName = paramString;
    }
    
    public String toString()
    {
      return mName;
    }
  }
}
