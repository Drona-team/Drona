package com.facebook.react.views.text;

import android.annotation.TargetApi;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.uimanager.NativeViewHierarchyOptimizer;
import com.facebook.react.uimanager.ReactShadowNode;
import com.facebook.react.uimanager.ReactShadowNodeImpl;
import com.facebook.react.uimanager.UIViewOperationQueue;
import com.facebook.yoga.YogaDirection;
import com.facebook.yoga.YogaMeasureFunction;
import com.facebook.yoga.YogaMeasureMode;
import com.facebook.yoga.YogaNode;
import java.util.ArrayList;
import java.util.Map;

@TargetApi(23)
public class ReactTextShadowNode
  extends ReactBaseTextShadowNode
{
  private static final TextPaint sTextPaintInstance = new TextPaint(1);
  @Nullable
  private Spannable mPreparedSpannableText;
  private boolean mShouldNotifyOnTextLayout;
  private final YogaMeasureFunction mTextMeasureFunction = new YogaMeasureFunction()
  {
    public long measure(YogaNode paramAnonymousYogaNode, float paramAnonymousFloat1, YogaMeasureMode paramAnonymousYogaMeasureMode1, float paramAnonymousFloat2, YogaMeasureMode paramAnonymousYogaMeasureMode2)
    {
      throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a23 = a22\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
    }
  };
  
  public ReactTextShadowNode()
  {
    initMeasureFunction();
  }
  
  private int getTextAlign()
  {
    int i = mTextAlign;
    if (getLayoutDirection() == YogaDirection.RIGHT)
    {
      if (i == 5) {
        return 3;
      }
      if (i == 3) {
        return 5;
      }
    }
    return i;
  }
  
  private void initMeasureFunction()
  {
    if (!isVirtual()) {
      setMeasureFunction(mTextMeasureFunction);
    }
  }
  
  public Iterable calculateLayoutOnChildren()
  {
    if (mInlineViews != null)
    {
      Object localObject = mInlineViews;
      ReactTextShadowNode localReactTextShadowNode = this;
      if (!((Map)localObject).isEmpty())
      {
        localObject = (Spanned)Assertions.assertNotNull(mPreparedSpannableText, "Spannable element has not been prepared in onBeforeLayout");
        int j = ((CharSequence)localObject).length();
        int i = 0;
        localObject = (TextInlineViewPlaceholderSpan[])((Spanned)localObject).getSpans(0, j, TextInlineViewPlaceholderSpan.class);
        ArrayList localArrayList = new ArrayList(localObject.length);
        j = localObject.length;
        while (i < j)
        {
          ReactShadowNode localReactShadowNode = localObject[i];
          Map localMap = mInlineViews;
          localReactShadowNode = (ReactShadowNode)localMap.get(Integer.valueOf(localReactShadowNode.getReactTag()));
          localReactShadowNode.calculateLayout();
          localArrayList.add(localReactShadowNode);
          i += 1;
        }
        return localArrayList;
      }
    }
    return null;
  }
  
  public boolean hoistNativeChildren()
  {
    return true;
  }
  
  public boolean isVirtualAnchor()
  {
    return false;
  }
  
  public void markUpdated()
  {
    super.markUpdated();
    super.dirty();
  }
  
  public void onBeforeLayout(NativeViewHierarchyOptimizer paramNativeViewHierarchyOptimizer)
  {
    mPreparedSpannableText = spannedFromShadowNode(this, null, true, paramNativeViewHierarchyOptimizer);
    markUpdated();
  }
  
  public void onCollectExtraUpdates(UIViewOperationQueue paramUIViewOperationQueue)
  {
    super.onCollectExtraUpdates(paramUIViewOperationQueue);
    if (mPreparedSpannableText != null)
    {
      ReactTextUpdate localReactTextUpdate = new ReactTextUpdate(mPreparedSpannableText, -1, mContainsImages, getPadding(4), getPadding(1), getPadding(5), getPadding(3), getTextAlign(), mTextBreakStrategy, mJustificationMode);
      paramUIViewOperationQueue.enqueueUpdateExtraData(getReactTag(), localReactTextUpdate);
    }
  }
  
  public void setShouldNotifyOnTextLayout(boolean paramBoolean)
  {
    mShouldNotifyOnTextLayout = paramBoolean;
  }
}
