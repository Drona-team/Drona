package email.apptailor.googlesignin;

import android.accounts.Account;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.util.signin.GoogleSignIn;
import com.google.android.gms.auth.util.signin.GoogleSignInAccount;
import com.google.android.gms.auth.util.signin.GoogleSignInClient;
import com.google.android.gms.auth.util.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.Task;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class RNGoogleSigninModule
  extends ReactContextBaseJavaModule
{
  public static final String ERROR_USER_RECOVERABLE_AUTH = "ERROR_USER_RECOVERABLE_AUTH";
  public static final String MODULE_NAME = "RNGoogleSignin";
  public static final String PLAY_SERVICES_NOT_AVAILABLE = "PLAY_SERVICES_NOT_AVAILABLE";
  public static final int RC_SIGN_IN = 9001;
  public static final int REQUEST_CODE_RECOVER_AUTH = 53294;
  private static final String SHOULD_RECOVER = "SHOULD_RECOVER";
  private GoogleSignInClient _apiClient;
  private PendingAuthRecovery pendingAuthRecovery;
  private PromiseWrapper promiseWrapper = new PromiseWrapper();
  
  public RNGoogleSigninModule(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
    paramReactApplicationContext.addActivityEventListener(new RNGoogleSigninActivityEventListener(null));
  }
  
  private void handleSignInTaskResult(Task paramTask)
  {
    try
    {
      paramTask = paramTask.getResult(com.google.android.gms.common.api.ApiException.class);
      paramTask = (GoogleSignInAccount)paramTask;
      if (paramTask == null)
      {
        paramTask = promiseWrapper;
        paramTask.reject("RNGoogleSignin", "GoogleSignInAccount instance was null");
        return;
      }
      paramTask = Utils.getUserProperties(paramTask);
      PromiseWrapper localPromiseWrapper = promiseWrapper;
      localPromiseWrapper.resolve(paramTask);
      return;
    }
    catch (com.google.android.gms.common.package_6.ApiException paramTask)
    {
      int i = paramTask.getStatusCode();
      paramTask = GoogleSignInStatusCodes.getStatusCodeString(i);
      promiseWrapper.reject(String.valueOf(i), paramTask);
    }
  }
  
  private void handleSignOutOrRevokeAccessTask(Task paramTask, Promise paramPromise)
  {
    if (paramTask.isSuccessful())
    {
      paramPromise.resolve(null);
      return;
    }
    int i = Utils.getExceptionCode(paramTask);
    paramPromise.reject(String.valueOf(i), GoogleSignInStatusCodes.getStatusCodeString(i));
  }
  
  private void rejectWithNullClientError(Promise paramPromise)
  {
    paramPromise.reject("RNGoogleSignin", "apiClient is null - call configure first");
  }
  
  private void rerunFailedAuthTokenTask()
  {
    WritableMap localWritableMap = pendingAuthRecovery.getUserProperties();
    if (localWritableMap != null)
    {
      new AccessTokenRetrievalTask().execute(new WritableMap[] { localWritableMap, null });
      return;
    }
    promiseWrapper.reject("RNGoogleSignin", "rerunFailedAuthTokenTask: recovery failed");
  }
  
  private void startTokenRetrievalTaskWithRecovery(GoogleSignInAccount paramGoogleSignInAccount)
  {
    paramGoogleSignInAccount = Utils.getUserProperties(paramGoogleSignInAccount);
    WritableMap localWritableMap = Arguments.createMap();
    localWritableMap.putBoolean("SHOULD_RECOVER", true);
    new AccessTokenRetrievalTask().execute(new WritableMap[] { paramGoogleSignInAccount, localWritableMap });
  }
  
  public void clearCachedAccessToken(String paramString, Promise paramPromise)
  {
    promiseWrapper.setPromiseWithInProgressCheck(paramPromise, "clearCachedAccessToken");
    new TokenClearingTask().execute(new String[] { paramString });
  }
  
  public void configure(ReadableMap paramReadableMap, Promise paramPromise)
  {
    Object localObject;
    if (paramReadableMap.hasKey("scopes")) {
      localObject = paramReadableMap.getArray("scopes");
    } else {
      localObject = Arguments.createArray();
    }
    String str1;
    if (paramReadableMap.hasKey("webClientId")) {
      str1 = paramReadableMap.getString("webClientId");
    } else {
      str1 = null;
    }
    boolean bool1;
    if ((paramReadableMap.hasKey("offlineAccess")) && (paramReadableMap.getBoolean("offlineAccess"))) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    boolean bool2;
    if ((paramReadableMap.hasKey("forceCodeForRefreshToken")) && (paramReadableMap.getBoolean("forceCodeForRefreshToken"))) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    String str2;
    if (paramReadableMap.hasKey("accountName")) {
      str2 = paramReadableMap.getString("accountName");
    } else {
      str2 = null;
    }
    if (paramReadableMap.hasKey("hostedDomain")) {
      paramReadableMap = paramReadableMap.getString("hostedDomain");
    } else {
      paramReadableMap = null;
    }
    paramReadableMap = Utils.getSignInOptions(Utils.createScopesArray((ReadableArray)localObject), str1, bool1, bool2, str2, paramReadableMap);
    _apiClient = GoogleSignIn.getClient(getReactApplicationContext(), paramReadableMap);
    paramPromise.resolve(null);
  }
  
  public Map getConstants()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("BUTTON_SIZE_ICON", Integer.valueOf(2));
    localHashMap.put("BUTTON_SIZE_STANDARD", Integer.valueOf(0));
    localHashMap.put("BUTTON_SIZE_WIDE", Integer.valueOf(1));
    localHashMap.put("BUTTON_COLOR_AUTO", Integer.valueOf(2));
    localHashMap.put("BUTTON_COLOR_LIGHT", Integer.valueOf(1));
    localHashMap.put("BUTTON_COLOR_DARK", Integer.valueOf(0));
    localHashMap.put("SIGN_IN_CANCELLED", String.valueOf(12501));
    localHashMap.put("SIGN_IN_REQUIRED", String.valueOf(4));
    localHashMap.put("IN_PROGRESS", "ASYNC_OP_IN_PROGRESS");
    localHashMap.put("PLAY_SERVICES_NOT_AVAILABLE", "PLAY_SERVICES_NOT_AVAILABLE");
    return localHashMap;
  }
  
  public void getCurrentUser(Promise paramPromise)
  {
    Object localObject = GoogleSignIn.getLastSignedInAccount(getReactApplicationContext());
    if (localObject == null) {
      localObject = null;
    } else {
      localObject = Utils.getUserProperties((GoogleSignInAccount)localObject);
    }
    paramPromise.resolve(localObject);
  }
  
  public String getName()
  {
    return "RNGoogleSignin";
  }
  
  public PromiseWrapper getPromiseWrapper()
  {
    return promiseWrapper;
  }
  
  public void getTokens(Promise paramPromise)
  {
    GoogleSignInAccount localGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(getReactApplicationContext());
    if (localGoogleSignInAccount == null)
    {
      paramPromise.reject("RNGoogleSignin", "getTokens requires a user to be signed in");
      return;
    }
    promiseWrapper.setPromiseWithInProgressCheck(paramPromise, "getTokens");
    startTokenRetrievalTaskWithRecovery(localGoogleSignInAccount);
  }
  
  public void isSignedIn(Promise paramPromise)
  {
    boolean bool;
    if (GoogleSignIn.getLastSignedInAccount(getReactApplicationContext()) != null) {
      bool = true;
    } else {
      bool = false;
    }
    paramPromise.resolve(Boolean.valueOf(bool));
  }
  
  public void playServicesAvailable(boolean paramBoolean, Promise paramPromise)
  {
    Activity localActivity = getCurrentActivity();
    if (localActivity == null)
    {
      Log.w("RNGoogleSignin", "could not determine playServicesAvailable, activity is null");
      paramPromise.reject("RNGoogleSignin", "activity is null");
      return;
    }
    GoogleApiAvailability localGoogleApiAvailability = GoogleApiAvailability.getInstance();
    int i = localGoogleApiAvailability.isGooglePlayServicesAvailable(localActivity);
    if (i != 0)
    {
      if ((paramBoolean) && (localGoogleApiAvailability.isUserResolvableError(i))) {
        localGoogleApiAvailability.getErrorDialog(localActivity, i, 2404).show();
      }
      paramPromise.reject("PLAY_SERVICES_NOT_AVAILABLE", "Play services not available");
      return;
    }
    paramPromise.resolve(Boolean.valueOf(true));
  }
  
  public void revokeAccess(Promise paramPromise)
  {
    if (_apiClient == null)
    {
      rejectWithNullClientError(paramPromise);
      return;
    }
    _apiClient.revokeAccess().addOnCompleteListener(new RNGoogleSigninModule.4(this, paramPromise));
  }
  
  public void signIn(Promise paramPromise)
  {
    if (_apiClient == null)
    {
      rejectWithNullClientError(paramPromise);
      return;
    }
    Activity localActivity = getCurrentActivity();
    if (localActivity == null)
    {
      paramPromise.reject("RNGoogleSignin", "activity is null");
      return;
    }
    promiseWrapper.setPromiseWithInProgressCheck(paramPromise, "signIn");
    UiThreadUtil.runOnUiThread(new RNGoogleSigninModule.2(this, localActivity));
  }
  
  public void signInSilently(Promise paramPromise)
  {
    if (_apiClient == null)
    {
      rejectWithNullClientError(paramPromise);
      return;
    }
    promiseWrapper.setPromiseWithInProgressCheck(paramPromise, "signInSilently");
    UiThreadUtil.runOnUiThread(new RNGoogleSigninModule.1(this));
  }
  
  public void signOut(Promise paramPromise)
  {
    if (_apiClient == null)
    {
      rejectWithNullClientError(paramPromise);
      return;
    }
    _apiClient.signOut().addOnCompleteListener(new RNGoogleSigninModule.3(this, paramPromise));
  }
  
  class AccessTokenRetrievalTask
    extends AsyncTask<WritableMap, Void, Void>
  {
    private WeakReference<co.apptailor.googlesignin.RNGoogleSigninModule> weakModuleRef;
    
    AccessTokenRetrievalTask()
    {
      weakModuleRef = new WeakReference(this$1);
    }
    
    private void attemptRecovery(RNGoogleSigninModule paramRNGoogleSigninModule, Exception paramException, WritableMap paramWritableMap)
    {
      Activity localActivity = paramRNGoogleSigninModule.getCurrentActivity();
      if (localActivity == null)
      {
        RNGoogleSigninModule.access$802(paramRNGoogleSigninModule, null);
        paramRNGoogleSigninModule = promiseWrapper;
        paramWritableMap = new StringBuilder();
        paramWritableMap.append("Cannot attempt recovery auth because app is not in foreground. ");
        paramWritableMap.append(paramException.getLocalizedMessage());
        paramRNGoogleSigninModule.reject("RNGoogleSignin", paramWritableMap.toString());
        return;
      }
      RNGoogleSigninModule.access$802(paramRNGoogleSigninModule, new PendingAuthRecovery(paramWritableMap));
      localActivity.startActivityForResult(((UserRecoverableAuthException)paramException).getIntent(), 53294);
    }
    
    private void handleException(RNGoogleSigninModule paramRNGoogleSigninModule, Exception paramException, WritableMap paramWritableMap1, WritableMap paramWritableMap2)
    {
      if ((paramException instanceof UserRecoverableAuthException))
      {
        int i;
        if ((paramWritableMap2 != null) && (paramWritableMap2.hasKey("SHOULD_RECOVER")) && (paramWritableMap2.getBoolean("SHOULD_RECOVER"))) {
          i = 1;
        } else {
          i = 0;
        }
        if (i != 0)
        {
          attemptRecovery(paramRNGoogleSigninModule, paramException, paramWritableMap1);
          return;
        }
        promiseWrapper.reject("ERROR_USER_RECOVERABLE_AUTH", paramException);
        return;
      }
      promiseWrapper.reject("RNGoogleSignin", paramException);
    }
    
    private void insertAccessTokenIntoUserProperties(RNGoogleSigninModule paramRNGoogleSigninModule, WritableMap paramWritableMap)
      throws IOException, GoogleAuthException
    {
      String str = paramWritableMap.getMap("user").getString("email");
      paramWritableMap.putString("accessToken", GoogleAuthUtil.getToken(paramRNGoogleSigninModule.getReactApplicationContext(), new Account(str, "com.google"), Utils.scopesToString(paramWritableMap.getArray("scopes"))));
    }
    
    protected Void doInBackground(WritableMap... paramVarArgs)
    {
      WritableMap localWritableMap = paramVarArgs[0];
      RNGoogleSigninModule localRNGoogleSigninModule = (RNGoogleSigninModule)weakModuleRef.get();
      if (localRNGoogleSigninModule == null) {
        return null;
      }
      try
      {
        insertAccessTokenIntoUserProperties(localRNGoogleSigninModule, localWritableMap);
        localRNGoogleSigninModule.getPromiseWrapper().resolve(localWritableMap);
        return null;
      }
      catch (Exception localException)
      {
        if (paramVarArgs.length >= 2) {
          paramVarArgs = paramVarArgs[1];
        } else {
          paramVarArgs = null;
        }
        handleException(localRNGoogleSigninModule, localException, localWritableMap, paramVarArgs);
      }
      return null;
    }
  }
  
  class RNGoogleSigninActivityEventListener
    extends BaseActivityEventListener
  {
    private RNGoogleSigninActivityEventListener() {}
    
    public void onActivityResult(Activity paramActivity, int paramInt1, int paramInt2, Intent paramIntent)
    {
      if (paramInt1 == 9001)
      {
        paramActivity = GoogleSignIn.getSignedInAccountFromIntent(paramIntent);
        RNGoogleSigninModule.this.handleSignInTaskResult(paramActivity);
        return;
      }
      if (paramInt1 == 53294)
      {
        if (paramInt2 == -1)
        {
          RNGoogleSigninModule.this.rerunFailedAuthTokenTask();
          return;
        }
        promiseWrapper.reject("RNGoogleSignin", "Failed authentication recovery attempt, probably user-rejected.");
      }
    }
  }
  
  class TokenClearingTask
    extends AsyncTask<String, Void, Void>
  {
    private WeakReference<co.apptailor.googlesignin.RNGoogleSigninModule> weakModuleRef;
    
    TokenClearingTask()
    {
      weakModuleRef = new WeakReference(this$1);
    }
    
    protected Void doInBackground(String... paramVarArgs)
    {
      RNGoogleSigninModule localRNGoogleSigninModule = (RNGoogleSigninModule)weakModuleRef.get();
      if (localRNGoogleSigninModule == null) {
        return null;
      }
      try
      {
        ReactApplicationContext localReactApplicationContext = localRNGoogleSigninModule.getReactApplicationContext();
        paramVarArgs = paramVarArgs[0];
        GoogleAuthUtil.clearToken(localReactApplicationContext, paramVarArgs);
        localRNGoogleSigninModule.getPromiseWrapper().resolve(null);
        return null;
      }
      catch (Exception paramVarArgs)
      {
        promiseWrapper.reject("RNGoogleSignin", paramVarArgs);
      }
      return null;
    }
  }
}
