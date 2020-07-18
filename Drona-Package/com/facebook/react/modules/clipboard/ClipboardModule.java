package com.facebook.react.modules.clipboard;

import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipboardManager;
import android.content.Context;
import com.facebook.react.bridge.ContextBaseJavaModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name="Clipboard")
public class ClipboardModule
  extends ContextBaseJavaModule
{
  public static final String NAME = "Clipboard";
  
  public ClipboardModule(Context paramContext)
  {
    super(paramContext);
  }
  
  private ClipboardManager getClipboardService()
  {
    Context localContext = getContext();
    getContext();
    return (ClipboardManager)localContext.getSystemService("clipboard");
  }
  
  public String getName()
  {
    return "Clipboard";
  }
  
  public void getString(Promise paramPromise)
  {
    try
    {
      Object localObject1 = getClipboardService();
      Object localObject2 = ((ClipboardManager)localObject1).getPrimaryClip();
      if (localObject2 != null)
      {
        int i = ((ClipData)localObject2).getItemCount();
        if (i >= 1)
        {
          localObject1 = ((ClipboardManager)localObject1).getPrimaryClip().getItemAt(0);
          localObject2 = new StringBuilder();
          ((StringBuilder)localObject2).append("");
          ((StringBuilder)localObject2).append(((ClipData.Item)localObject1).getText());
          paramPromise.resolve(((StringBuilder)localObject2).toString());
          return;
        }
      }
      paramPromise.resolve("");
      return;
    }
    catch (Exception localException)
    {
      paramPromise.reject(localException);
    }
  }
  
  public void setString(String paramString)
  {
    paramString = ClipData.newPlainText(null, paramString);
    getClipboardService().setPrimaryClip(paramString);
  }
}
