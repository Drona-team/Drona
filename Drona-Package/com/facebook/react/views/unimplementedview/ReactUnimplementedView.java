package com.facebook.react.views.unimplementedview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatTextView;

public class ReactUnimplementedView
  extends LinearLayout
{
  private AppCompatTextView mTextView;
  
  public ReactUnimplementedView(Context paramContext)
  {
    super(paramContext);
    mTextView = new AppCompatTextView(paramContext);
    mTextView.setLayoutParams(new LinearLayout.LayoutParams(-2, -1));
    mTextView.setGravity(17);
    mTextView.setTextColor(-1);
    setBackgroundColor(1442775040);
    setGravity(1);
    setOrientation(1);
    addView(mTextView);
  }
  
  public void setName(String paramString)
  {
    AppCompatTextView localAppCompatTextView = mTextView;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("'");
    localStringBuilder.append(paramString);
    localStringBuilder.append("' is not Fabric compatible yet.");
    localAppCompatTextView.setText(localStringBuilder.toString());
  }
}
