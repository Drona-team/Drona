package email.apptailor.googlesignin;

import android.net.Uri;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.auth.util.signin.GoogleSignInAccount;
import com.google.android.gms.auth.util.signin.GoogleSignInOptions;
import com.google.android.gms.auth.util.signin.GoogleSignInOptions.Builder;
import com.google.android.gms.common.package_6.ApiException;
import com.google.android.gms.common.package_6.Scope;
import com.google.android.gms.tasks.Task;
import java.util.Iterator;
import java.util.Set;

public class Utils
{
  public Utils() {}
  
  static Scope[] createScopesArray(ReadableArray paramReadableArray)
  {
    int j = paramReadableArray.size();
    Scope[] arrayOfScope = new Scope[j];
    int i = 0;
    while (i < j)
    {
      arrayOfScope[i] = new Scope(paramReadableArray.getString(i));
      i += 1;
    }
    return arrayOfScope;
  }
  
  public static int getExceptionCode(Task paramTask)
  {
    paramTask = paramTask.getException();
    if ((paramTask instanceof ApiException)) {
      return ((ApiException)paramTask).getStatusCode();
    }
    return 8;
  }
  
  static GoogleSignInOptions getSignInOptions(Scope[] paramArrayOfScope, String paramString1, boolean paramBoolean1, boolean paramBoolean2, String paramString2, String paramString3)
  {
    paramArrayOfScope = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestScopes(new Scope("email"), paramArrayOfScope);
    if ((paramString1 != null) && (!paramString1.isEmpty()))
    {
      paramArrayOfScope.requestIdToken(paramString1);
      if (paramBoolean1) {
        paramArrayOfScope.requestServerAuthCode(paramString1, paramBoolean2);
      }
    }
    if ((paramString2 != null) && (!paramString2.isEmpty())) {
      paramArrayOfScope.setAccountName(paramString2);
    }
    if ((paramString3 != null) && (!paramString3.isEmpty())) {
      paramArrayOfScope.setHostedDomain(paramString3);
    }
    return paramArrayOfScope.build();
  }
  
  static WritableMap getUserProperties(GoogleSignInAccount paramGoogleSignInAccount)
  {
    Object localObject1 = paramGoogleSignInAccount.getPhotoUrl();
    Object localObject2 = Arguments.createMap();
    ((WritableMap)localObject2).putString("id", paramGoogleSignInAccount.getId());
    ((WritableMap)localObject2).putString("name", paramGoogleSignInAccount.getDisplayName());
    ((WritableMap)localObject2).putString("givenName", paramGoogleSignInAccount.getGivenName());
    ((WritableMap)localObject2).putString("familyName", paramGoogleSignInAccount.getFamilyName());
    ((WritableMap)localObject2).putString("email", paramGoogleSignInAccount.getEmail());
    if (localObject1 != null) {
      localObject1 = ((Uri)localObject1).toString();
    } else {
      localObject1 = null;
    }
    ((WritableMap)localObject2).putString("photo", (String)localObject1);
    localObject1 = Arguments.createMap();
    ((WritableMap)localObject1).putMap("user", (ReadableMap)localObject2);
    ((WritableMap)localObject1).putString("idToken", paramGoogleSignInAccount.getIdToken());
    ((WritableMap)localObject1).putString("serverAuthCode", paramGoogleSignInAccount.getServerAuthCode());
    localObject2 = Arguments.createArray();
    paramGoogleSignInAccount = paramGoogleSignInAccount.getGrantedScopes().iterator();
    while (paramGoogleSignInAccount.hasNext())
    {
      String str = ((Scope)paramGoogleSignInAccount.next()).toString();
      if (str.startsWith("http")) {
        ((WritableArray)localObject2).pushString(str);
      }
    }
    ((WritableMap)localObject1).putArray("scopes", (ReadableArray)localObject2);
    return localObject1;
  }
  
  static String scopesToString(ReadableArray paramReadableArray)
  {
    StringBuilder localStringBuilder = new StringBuilder("oauth2:");
    int i = 0;
    while (i < paramReadableArray.size())
    {
      localStringBuilder.append(paramReadableArray.getString(i));
      localStringBuilder.append(" ");
      i += 1;
    }
    return localStringBuilder.toString().trim();
  }
}
