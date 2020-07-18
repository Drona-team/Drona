package com.facebook.react.views.textinput;

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.annotations.VisibleForTesting;
import com.facebook.react.uimanager.ReactShadowNodeImpl;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIViewOperationQueue;
import com.facebook.react.views.text.ReactBaseTextShadowNode;
import com.facebook.react.views.text.ReactTextUpdate;
import com.facebook.react.views.text.TextAttributes;
import com.facebook.react.views.view.MeasureUtil;
import com.facebook.yoga.YogaMeasureFunction;
import com.facebook.yoga.YogaMeasureMode;
import com.facebook.yoga.YogaMeasureOutput;
import com.facebook.yoga.YogaNode;

@TargetApi(23)
@VisibleForTesting
public class ReactTextInputShadowNode
  extends ReactBaseTextShadowNode
  implements YogaMeasureFunction
{
  @VisibleForTesting
  public static final String PROP_PLACEHOLDER = "placeholder";
  @VisibleForTesting
  public static final String PROP_SELECTION = "selection";
  @VisibleForTesting
  public static final String PROP_TEXT = "text";
  @Nullable
  private EditText mDummyEditText;
  @Nullable
  private ReactTextInputLocalData mLocalData;
  private int mMostRecentEventCount = -1;
  @Nullable
  private String mPlaceholder = null;
  private int mSelectionEnd = -1;
  private int mSelectionStart = -1;
  @Nullable
  private String mText = null;
  
  public ReactTextInputShadowNode()
  {
    int i;
    if (Build.VERSION.SDK_INT < 23) {
      i = 0;
    } else {
      i = 1;
    }
    mTextBreakStrategy = i;
    initMeasureFunction();
  }
  
  private void initMeasureFunction()
  {
    setMeasureFunction(this);
  }
  
  public String getPlaceholder()
  {
    return mPlaceholder;
  }
  
  public String getText()
  {
    return mText;
  }
  
  public boolean isVirtualAnchor()
  {
    return true;
  }
  
  public boolean isYogaLeafNode()
  {
    return true;
  }
  
  public long measure(YogaNode paramYogaNode, float paramFloat1, YogaMeasureMode paramYogaMeasureMode1, float paramFloat2, YogaMeasureMode paramYogaMeasureMode2)
  {
    paramYogaNode = (EditText)Assertions.assertNotNull(mDummyEditText);
    if (mLocalData != null)
    {
      mLocalData.apply(paramYogaNode);
    }
    else
    {
      paramYogaNode.setTextSize(0, mTextAttributes.getEffectiveFontSize());
      if (mNumberOfLines != -1) {
        paramYogaNode.setLines(mNumberOfLines);
      }
      if ((Build.VERSION.SDK_INT >= 23) && (paramYogaNode.getBreakStrategy() != mTextBreakStrategy)) {
        paramYogaNode.setBreakStrategy(mTextBreakStrategy);
      }
    }
    paramYogaNode.setHint(getPlaceholder());
    paramYogaNode.measure(MeasureUtil.getMeasureSpec(paramFloat1, paramYogaMeasureMode1), MeasureUtil.getMeasureSpec(paramFloat2, paramYogaMeasureMode2));
    return YogaMeasureOutput.make(paramYogaNode.getMeasuredWidth(), paramYogaNode.getMeasuredHeight());
  }
  
  public void onCollectExtraUpdates(UIViewOperationQueue paramUIViewOperationQueue)
  {
    super.onCollectExtraUpdates(paramUIViewOperationQueue);
    if (mMostRecentEventCount != -1)
    {
      ReactTextUpdate localReactTextUpdate = new ReactTextUpdate(spannedFromShadowNode(this, getText(), false, null), mMostRecentEventCount, mContainsImages, getPadding(0), getPadding(1), getPadding(2), getPadding(3), mTextAlign, mTextBreakStrategy, mJustificationMode, mSelectionStart, mSelectionEnd);
      paramUIViewOperationQueue.enqueueUpdateExtraData(getReactTag(), localReactTextUpdate);
    }
  }
  
  public void setLocalData(Object paramObject)
  {
    Assertions.assertCondition(paramObject instanceof ReactTextInputLocalData);
    mLocalData = ((ReactTextInputLocalData)paramObject);
    dirty();
  }
  
  public void setMostRecentEventCount(int paramInt)
  {
    mMostRecentEventCount = paramInt;
  }
  
  public void setPadding(int paramInt, float paramFloat)
  {
    super.setPadding(paramInt, paramFloat);
    markUpdated();
  }
  
  public void setPlaceholder(String paramString)
  {
    mPlaceholder = paramString;
    markUpdated();
  }
  
  public void setSelection(ReadableMap paramReadableMap)
  {
    mSelectionEnd = -1;
    mSelectionStart = -1;
    if (paramReadableMap == null) {
      return;
    }
    if ((paramReadableMap.hasKey("start")) && (paramReadableMap.hasKey("end")))
    {
      mSelectionStart = paramReadableMap.getInt("start");
      mSelectionEnd = paramReadableMap.getInt("end");
      markUpdated();
    }
  }
  
  public void setText(String paramString)
  {
    mText = paramString;
    markUpdated();
  }
  
  public void setTextBreakStrategy(String paramString)
  {
    if (Build.VERSION.SDK_INT < 23) {
      return;
    }
    if ((paramString != null) && (!"simple".equals(paramString)))
    {
      if ("highQuality".equals(paramString))
      {
        mTextBreakStrategy = 1;
        return;
      }
      if ("balanced".equals(paramString))
      {
        mTextBreakStrategy = 2;
        return;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Invalid textBreakStrategy: ");
      localStringBuilder.append(paramString);
      throw new JSApplicationIllegalArgumentException(localStringBuilder.toString());
    }
    mTextBreakStrategy = 0;
  }
  
  public void setThemedContext(ThemedReactContext paramThemedReactContext)
  {
    super.setThemedContext(paramThemedReactContext);
    paramThemedReactContext = new EditText(getThemedContext());
    setDefaultPadding(4, ViewCompat.getPaddingStart(paramThemedReactContext));
    setDefaultPadding(1, paramThemedReactContext.getPaddingTop());
    setDefaultPadding(5, ViewCompat.getPaddingEnd(paramThemedReactContext));
    setDefaultPadding(3, paramThemedReactContext.getPaddingBottom());
    mDummyEditText = paramThemedReactContext;
    mDummyEditText.setPadding(0, 0, 0, 0);
    mDummyEditText.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
  }
}
