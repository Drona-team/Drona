package expo.modules.barcodescanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.BaseBundle;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.unimodules.core.ExportedModule;
import org.unimodules.core.ModuleRegistry;
import org.unimodules.core.Promise;
import org.unimodules.interfaces.barcodescanner.BarCodeScanner;
import org.unimodules.interfaces.barcodescanner.BarCodeScannerResult;
import org.unimodules.interfaces.barcodescanner.BarCodeScannerSettings;
import org.unimodules.interfaces.imageloader.ImageLoader;
import org.unimodules.interfaces.imageloader.ImageLoader.ResultListener;
import org.unimodules.interfaces.permissions.Permissions;
import org.unimodules.interfaces.permissions.Permissions.-CC;

public class BarCodeScannerModule
  extends ExportedModule
{
  private static final String ERROR_TAG = "E_BARCODE_SCANNER";
  private static final String PAGE_KEY = "ExpoBarCodeScannerModule";
  private static final Map<String, Object> VALID_BARCODE_TYPES = Collections.unmodifiableMap(new HashMap() {});
  private final BarCodeScannerProvider mBarCodeScannerProvider = new BarCodeScannerProvider();
  private ModuleRegistry mModuleRegistry;
  
  public BarCodeScannerModule(Context paramContext)
  {
    super(paramContext);
  }
  
  public Map getConstants()
  {
    Collections.unmodifiableMap(new HashMap()
    {
      private Map getBarCodeConstants()
      {
        return BarCodeScannerModule.VALID_BARCODE_TYPES;
      }
      
      private Map getTypeConstants()
      {
        Collections.unmodifiableMap(new HashMap() {});
      }
    });
  }
  
  public String getName()
  {
    return "ExpoBarCodeScannerModule";
  }
  
  public void getPermissionsAsync(Promise paramPromise)
  {
    Permissions.-CC.getPermissionsWithPermissionsManager((Permissions)mModuleRegistry.getModule(Permissions.class), paramPromise, new String[] { "android.permission.CAMERA" });
  }
  
  public void onCreate(ModuleRegistry paramModuleRegistry)
  {
    mModuleRegistry = paramModuleRegistry;
  }
  
  public void requestPermissionsAsync(Promise paramPromise)
  {
    Permissions.-CC.askForPermissionsWithPermissionsManager((Permissions)mModuleRegistry.getModule(Permissions.class), paramPromise, new String[] { "android.permission.CAMERA" });
  }
  
  public void scanFromURLAsync(final String paramString, List paramList, final Promise paramPromise)
  {
    final ArrayList localArrayList = new ArrayList();
    if (paramList != null)
    {
      int i = 0;
      while (i < paramList.size())
      {
        localArrayList.add(Integer.valueOf(((Double)paramList.get(i)).intValue()));
        i += 1;
      }
    }
    ((ImageLoader)mModuleRegistry.getModule(ImageLoader.class)).loadImageForManipulationFromURL(paramString, new ImageLoader.ResultListener()
    {
      public void onFailure(Throwable paramAnonymousThrowable)
      {
        Promise localPromise = paramPromise;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Could not get the image from given url: '");
        localStringBuilder.append(paramString);
        localStringBuilder.append("'");
        localPromise.reject("E_BARCODE_SCANNER_IMAGE_RETRIEVAL_ERROR", localStringBuilder.toString(), paramAnonymousThrowable);
      }
      
      public void onSuccess(Bitmap paramAnonymousBitmap)
      {
        Object localObject = mBarCodeScannerProvider.createBarCodeDetectorWithContext(getContext());
        ((BarCodeScanner)localObject).setSettings(new BarCodeScannerSettings() {});
        localObject = ((BarCodeScanner)localObject).scanMultiple(paramAnonymousBitmap);
        paramAnonymousBitmap = new ArrayList();
        localObject = ((List)localObject).iterator();
        while (((Iterator)localObject).hasNext())
        {
          BarCodeScannerResult localBarCodeScannerResult = (BarCodeScannerResult)((Iterator)localObject).next();
          if (localArrayList.contains(Integer.valueOf(localBarCodeScannerResult.getType())))
          {
            Bundle localBundle = new Bundle();
            localBundle.putString("data", localBarCodeScannerResult.getValue());
            localBundle.putInt("type", localBarCodeScannerResult.getType());
            paramAnonymousBitmap.add(localBundle);
          }
        }
        paramPromise.resolve(paramAnonymousBitmap);
      }
    });
  }
}
