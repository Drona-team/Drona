package com.facebook.react.views.text;

import androidx.annotation.Nullable;
import com.facebook.react.common.annotations.VisibleForTesting;
import com.facebook.react.uimanager.ReactShadowNodeImpl;

public class ReactRawTextShadowNode
  extends ReactShadowNodeImpl
{
  @VisibleForTesting
  public static final String PROP_TEXT = "text";
  @Nullable
  private String mText = null;
  
  public ReactRawTextShadowNode() {}
  
  public String getText()
  {
    return mText;
  }
  
  public boolean isVirtual()
  {
    return true;
  }
  
  public void setText(String paramString)
  {
    mText = paramString;
    markUpdated();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(getViewClass());
    localStringBuilder.append(" [text: ");
    localStringBuilder.append(mText);
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
}
