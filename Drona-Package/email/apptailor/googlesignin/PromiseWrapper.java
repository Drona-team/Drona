package email.apptailor.googlesignin;

import android.util.Log;
import com.facebook.react.bridge.Promise;

public class PromiseWrapper
{
  public static final String ASYNC_OP_IN_PROGRESS = "ASYNC_OP_IN_PROGRESS";
  private String nameOfCallInProgress;
  private Promise promise;
  
  public PromiseWrapper() {}
  
  private void rejectPreviousPromiseBecauseNewOneIsInProgress(Promise paramPromise, String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Warning: previous promise did not settle and was overwritten. You've called \"");
    localStringBuilder.append(paramString);
    localStringBuilder.append("\" while \"");
    localStringBuilder.append(getNameOfCallInProgress());
    localStringBuilder.append("\" was already in progress and has not completed yet.");
    paramPromise.reject("ASYNC_OP_IN_PROGRESS", localStringBuilder.toString());
  }
  
  private void resetMembers()
  {
    promise = null;
    nameOfCallInProgress = null;
  }
  
  public String getNameOfCallInProgress()
  {
    return nameOfCallInProgress;
  }
  
  public void reject(String paramString1, String paramString2)
  {
    Promise localPromise = promise;
    if (localPromise == null)
    {
      Log.w("RNGoogleSignin", "cannot reject promise because it's null");
      return;
    }
    resetMembers();
    localPromise.reject(paramString1, paramString2);
  }
  
  public void reject(String paramString, Throwable paramThrowable)
  {
    Promise localPromise = promise;
    if (localPromise == null)
    {
      Log.w("RNGoogleSignin", "cannot reject promise because it's null");
      return;
    }
    resetMembers();
    localPromise.reject(paramString, paramThrowable.getLocalizedMessage(), paramThrowable);
  }
  
  public void resolve(Object paramObject)
  {
    Promise localPromise = promise;
    if (localPromise == null)
    {
      Log.w("RNGoogleSignin", "cannot resolve promise because it's null");
      return;
    }
    resetMembers();
    localPromise.resolve(paramObject);
  }
  
  public void setPromiseWithInProgressCheck(Promise paramPromise, String paramString)
  {
    if (promise != null) {
      rejectPreviousPromiseBecauseNewOneIsInProgress(promise, paramString);
    }
    promise = paramPromise;
    nameOfCallInProgress = paramString;
  }
}
