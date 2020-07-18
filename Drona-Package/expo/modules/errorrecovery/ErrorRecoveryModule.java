package expo.modules.errorrecovery;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.Map;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.unimodules.core.ExportedModule;
import org.unimodules.core.Promise;

@Metadata(bv={1, 0, 3}, d1={"\000<\n\002\030\002\n\002\030\002\n\000\n\002\030\002\n\002\b\002\n\002\030\002\n\002\b\003\n\002\020\016\n\000\n\002\020$\n\002\020\000\n\002\b\002\n\002\020\002\n\002\b\002\n\002\030\002\n\002\b\002\b\026\030\0002\0020\001B\r\022\006\020\002\032\0020\003?\006\002\020\004J\n\020\t\032\004\030\0010\nH\024J\026\020\013\032\020\022\004\022\0020\n\022\006\022\004\030\0010\r0\fH\026J\b\020\016\032\0020\nH\026J\032\020\017\032\0020\0202\b\020\021\032\004\030\0010\n2\006\020\022\032\0020\023H\007J\020\020\024\032\0020\0202\006\020\021\032\0020\nH\024R\024\020\005\032\0020\006X?\004?\006\b\n\000\032\004\b\007\020\b?\006\025"}, d2={"Lexpo/modules/errorrecovery/ErrorRecoveryModule;", "Lorg/unimodules/core/ExportedModule;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "mSharedPreferences", "Landroid/content/SharedPreferences;", "getMSharedPreferences", "()Landroid/content/SharedPreferences;", "consumeRecoveryProps", "", "getConstants", "", "", "getName", "saveRecoveryProps", "", "props", "promise", "Lorg/unimodules/core/Promise;", "setRecoveryProps", "expo-error-recovery_release"}, k=1, mv={1, 1, 15})
public class ErrorRecoveryModule
  extends ExportedModule
{
  @NotNull
  private final SharedPreferences mSharedPreferences;
  
  public ErrorRecoveryModule(Context paramContext)
  {
    super(paramContext);
    paramContext = paramContext.getApplicationContext().getSharedPreferences("expo.modules.errorrecovery.store", 0);
    Intrinsics.checkExpressionValueIsNotNull(paramContext, "context.applicationConte?RE, Context.MODE_PRIVATE)");
    mSharedPreferences = paramContext;
  }
  
  protected String consumeRecoveryProps()
  {
    String str = getMSharedPreferences().getString("recoveredProps", null);
    if (str != null)
    {
      getMSharedPreferences().edit().remove("recoveredProps").commit();
      return str;
    }
    return null;
  }
  
  public Map getConstants()
  {
    return MapsKt.mapOf(TuplesKt.to("recoveredProps", consumeRecoveryProps()));
  }
  
  protected SharedPreferences getMSharedPreferences()
  {
    return mSharedPreferences;
  }
  
  public String getName()
  {
    return "ExpoErrorRecovery";
  }
  
  public final void saveRecoveryProps(String paramString, Promise paramPromise)
  {
    Intrinsics.checkParameterIsNotNull(paramPromise, "promise");
    if (paramString != null) {
      setRecoveryProps(paramString);
    }
    paramPromise.resolve(null);
  }
  
  protected void setRecoveryProps(String paramString)
  {
    Intrinsics.checkParameterIsNotNull(paramString, "props");
    getMSharedPreferences().edit().putString("recoveredProps", paramString).commit();
  }
}
