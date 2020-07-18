package email.apptailor.googlesignin;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.google.android.gms.common.SignInButton;

public class RNGoogleSigninButtonViewManager
  extends SimpleViewManager<SignInButton>
{
  public RNGoogleSigninButtonViewManager() {}
  
  protected SignInButton createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    SignInButton localSignInButton = new SignInButton(paramThemedReactContext);
    localSignInButton.setSize(0);
    localSignInButton.setColorScheme(2);
    localSignInButton.setOnClickListener(new RNGoogleSigninButtonViewManager.1(this, paramThemedReactContext));
    return localSignInButton;
  }
  
  public String getName()
  {
    return "RNGoogleSigninButton";
  }
  
  public void setColor(SignInButton paramSignInButton, int paramInt)
  {
    paramSignInButton.setColorScheme(paramInt);
  }
  
  public void setDisabled(SignInButton paramSignInButton, boolean paramBoolean)
  {
    paramSignInButton.setEnabled(paramBoolean ^ true);
  }
  
  public void setSize(SignInButton paramSignInButton, int paramInt)
  {
    paramSignInButton.setSize(paramInt);
  }
}
