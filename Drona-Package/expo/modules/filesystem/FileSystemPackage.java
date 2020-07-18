package expo.modules.filesystem;

import android.content.Context;
import java.util.Collections;
import java.util.List;
import org.unimodules.core.BasePackage;

public class FileSystemPackage
  extends BasePackage
{
  public FileSystemPackage() {}
  
  public List createExportedModules(Context paramContext)
  {
    return Collections.singletonList(new FileSystemModule(paramContext));
  }
  
  public List createInternalModules(Context paramContext)
  {
    return Collections.singletonList(new FilePermissionModule());
  }
}
