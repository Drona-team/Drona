package expo.modules.errorrecovery;

import android.content.Context;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.unimodules.core.BasePackage;

@Metadata(bv={1, 0, 3}, d1={"\000\034\n\002\030\002\n\002\030\002\n\002\b\002\n\002\020 \n\002\030\002\n\000\n\002\030\002\n\000\030\0002\0020\001B\005?\006\002\020\002J\026\020\003\032\b\022\004\022\0020\0050\0042\006\020\006\032\0020\007H\026?\006\b"}, d2={"Lexpo/modules/errorrecovery/ErrorRecoveryPackage;", "Lorg/unimodules/core/BasePackage;", "()V", "createExportedModules", "", "Lorg/unimodules/core/ExportedModule;", "context", "Landroid/content/Context;", "expo-error-recovery_release"}, k=1, mv={1, 1, 15})
public final class ErrorRecoveryPackage
  extends BasePackage
{
  public ErrorRecoveryPackage() {}
  
  public List createExportedModules(Context paramContext)
  {
    Intrinsics.checkParameterIsNotNull(paramContext, "context");
    return CollectionsKt.listOf(new ErrorRecoveryModule(paramContext));
  }
}
