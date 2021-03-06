package expo.modules.font;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import java.io.File;
import org.unimodules.core.ExportedModule;
import org.unimodules.core.ModuleRegistry;
import org.unimodules.core.Promise;
import org.unimodules.interfaces.constants.ConstantsInterface;
import org.unimodules.interfaces.font.FontManager;

public class FontLoaderModule
  extends ExportedModule
{
  private static final String ASSET_SCHEME = "asset://";
  private static final String EXPORTED_NAME = "ExpoFontLoader";
  private ModuleRegistry mModuleRegistry;
  
  public FontLoaderModule(Context paramContext)
  {
    super(paramContext);
  }
  
  private boolean isScoped()
  {
    ConstantsInterface localConstantsInterface = (ConstantsInterface)mModuleRegistry.getModule(ConstantsInterface.class);
    return (localConstantsInterface != null) && ("expo".equals(localConstantsInterface.getAppOwnership()));
  }
  
  public String getName()
  {
    return "ExpoFontLoader";
  }
  
  public void loadAsync(String paramString1, String paramString2, Promise paramPromise)
  {
    String str = "";
    try
    {
      boolean bool = isScoped();
      if (bool) {
        str = "ExpoFont-";
      }
      bool = paramString2.startsWith("asset://");
      if (bool)
      {
        localObject = getContext().getAssets();
        int i = "asset://".length();
        paramString2 = Typeface.createFromAsset((AssetManager)localObject, paramString2.substring(i + 1));
      }
      else
      {
        paramString2 = Typeface.createFromFile(new File(Uri.parse(paramString2).getPath()));
      }
      Object localObject = mModuleRegistry;
      localObject = ((ModuleRegistry)localObject).getModule(FontManager.class);
      localObject = (FontManager)localObject;
      if (localObject == null)
      {
        paramPromise.reject("E_NO_FONT_MANAGER", "There is no FontManager in module registry. Are you sure all the dependencies of expo-font are installed and linked?");
        return;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(str);
      localStringBuilder.append(paramString1);
      ((FontManager)localObject).setTypeface(localStringBuilder.toString(), 0, paramString2);
      paramPromise.resolve(null);
      return;
    }
    catch (Exception paramString1)
    {
      paramString2 = new StringBuilder();
      paramString2.append("Font.loadAsync unexpected exception: ");
      paramString2.append(paramString1.getMessage());
      paramPromise.reject("E_UNEXPECTED", paramString2.toString(), paramString1);
    }
  }
  
  public void onCreate(ModuleRegistry paramModuleRegistry)
  {
    mModuleRegistry = paramModuleRegistry;
  }
}
