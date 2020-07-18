package expo.modules.filesystem;

import android.content.Context;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import org.unimodules.core.interfaces.InternalModule;
import org.unimodules.interfaces.filesystem.FilePermissionModuleInterface;
import org.unimodules.interfaces.filesystem.Permission;

public class FilePermissionModule
  implements FilePermissionModuleInterface, InternalModule
{
  public FilePermissionModule() {}
  
  public List getExportedInterfaces()
  {
    return Collections.singletonList(FilePermissionModuleInterface.class);
  }
  
  protected EnumSet getExternalPathPermissions(String paramString)
  {
    paramString = new File(paramString);
    if ((paramString.canWrite()) && (paramString.canRead()))
    {
      paramString = Permission.READ;
      Permission localPermission = Permission.WRITE;
      return EnumSet.of((Enum)paramString, (Enum)localPermission);
    }
    if (paramString.canWrite()) {
      return EnumSet.of((Enum)Permission.WRITE);
    }
    if (paramString.canRead()) {
      return EnumSet.of((Enum)Permission.READ);
    }
    return EnumSet.noneOf(Permission.class);
  }
  
  protected EnumSet getInternalPathPermissions(String paramString, Context paramContext)
  {
    try
    {
      paramString = new File(paramString).getCanonicalPath();
      paramContext = getInternalPaths(paramContext).iterator();
      boolean bool;
      do
      {
        bool = paramContext.hasNext();
        if (!bool) {
          break label125;
        }
        Object localObject = paramContext.next();
        localObject = (String)localObject;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append((String)localObject);
        localStringBuilder.append("/");
        bool = paramString.startsWith(localStringBuilder.toString());
        if (bool) {
          break;
        }
        bool = ((String)localObject).equals(paramString);
      } while (!bool);
      paramContext = Permission.READ;
      paramString = Permission.WRITE;
      paramContext = (Enum)paramContext;
      paramString = (Enum)paramString;
      paramString = EnumSet.of(paramContext, paramString);
      return paramString;
      label125:
      return null;
    }
    catch (IOException paramString)
    {
      for (;;) {}
    }
    return EnumSet.noneOf(Permission.class);
  }
  
  protected List getInternalPaths(Context paramContext)
    throws IOException
  {
    return Arrays.asList(new String[] { paramContext.getFilesDir().getCanonicalPath(), paramContext.getCacheDir().getCanonicalPath() });
  }
  
  public EnumSet getPathPermissions(Context paramContext, String paramString)
  {
    EnumSet localEnumSet = getInternalPathPermissions(paramString, paramContext);
    paramContext = localEnumSet;
    if (localEnumSet == null) {
      paramContext = getExternalPathPermissions(paramString);
    }
    return paramContext;
  }
}
