package com.facebook.react.uimanager;

import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.common.ClearableSynchronizedPool;
import com.facebook.react.uimanager.annotations.ReactPropertyHolder;
import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaBaselineFunction;
import com.facebook.yoga.YogaConfig;
import com.facebook.yoga.YogaConstants;
import com.facebook.yoga.YogaDirection;
import com.facebook.yoga.YogaDisplay;
import com.facebook.yoga.YogaEdge;
import com.facebook.yoga.YogaFlexDirection;
import com.facebook.yoga.YogaJustify;
import com.facebook.yoga.YogaMeasureFunction;
import com.facebook.yoga.YogaNode;
import com.facebook.yoga.YogaOverflow;
import com.facebook.yoga.YogaPositionType;
import com.facebook.yoga.YogaValue;
import com.facebook.yoga.YogaWrap;
import java.util.ArrayList;
import java.util.Arrays;

@ReactPropertyHolder
public class ReactShadowNodeImpl
  implements ReactShadowNode<ReactShadowNodeImpl>
{
  private static final YogaConfig sYogaConfig = ;
  @Nullable
  private ArrayList<ReactShadowNodeImpl> mChildren;
  private final Spacing mDefaultPadding = new Spacing(0.0F);
  private Integer mHeightMeasureSpec;
  private boolean mIsLayoutOnly;
  @Nullable
  private ReactShadowNodeImpl mLayoutParent;
  @Nullable
  private ArrayList<ReactShadowNodeImpl> mNativeChildren;
  @Nullable
  private ReactShadowNodeImpl mNativeParent;
  private boolean mNodeUpdated = true;
  private final float[] mPadding = new float[9];
  private final boolean[] mPaddingIsPercent = new boolean[9];
  @Nullable
  private ReactShadowNodeImpl mParent;
  private int mReactTag;
  private int mRootTag;
  private int mScreenHeight;
  private int mScreenWidth;
  private int mScreenX;
  private int mScreenY;
  private boolean mShouldNotifyOnLayout;
  @Nullable
  private ThemedReactContext mThemedContext;
  private int mTotalNativeChildren = 0;
  @Nullable
  private String mViewClassName;
  private Integer mWidthMeasureSpec;
  private YogaNode mYogaNode;
  
  public ReactShadowNodeImpl()
  {
    if (!isVirtual())
    {
      YogaNode localYogaNode2 = (YogaNode)YogaNodePool.getInstance().acquire();
      YogaNode localYogaNode1 = localYogaNode2;
      if (localYogaNode2 == null) {
        localYogaNode1 = YogaNode.create(sYogaConfig);
      }
      mYogaNode = localYogaNode1;
      mYogaNode.setData(this);
      Arrays.fill(mPadding, NaN.0F);
      return;
    }
    mYogaNode = null;
  }
  
  private void getHierarchyInfoWithIndentation(StringBuilder paramStringBuilder, int paramInt)
  {
    int j = 0;
    int i = 0;
    while (i < paramInt)
    {
      paramStringBuilder.append("  ");
      i += 1;
    }
    paramStringBuilder.append("<");
    paramStringBuilder.append(getClass().getSimpleName());
    paramStringBuilder.append(" view='");
    paramStringBuilder.append(getViewClass());
    paramStringBuilder.append("' tag=");
    paramStringBuilder.append(getReactTag());
    if (mYogaNode != null)
    {
      paramStringBuilder.append(" layout='x:");
      paramStringBuilder.append(getScreenX());
      paramStringBuilder.append(" y:");
      paramStringBuilder.append(getScreenY());
      paramStringBuilder.append(" w:");
      paramStringBuilder.append(getLayoutWidth());
      paramStringBuilder.append(" h:");
      paramStringBuilder.append(getLayoutHeight());
      paramStringBuilder.append("'");
    }
    else
    {
      paramStringBuilder.append("(virtual node)");
    }
    paramStringBuilder.append(">\n");
    i = j;
    if (getChildCount() == 0) {
      return;
    }
    while (i < getChildCount())
    {
      getChildAt(i).getHierarchyInfoWithIndentation(paramStringBuilder, paramInt + 1);
      i += 1;
    }
  }
  
  private int getTotalNativeNodeContributionToParent()
  {
    NativeKind localNativeKind = getNativeKind();
    if (localNativeKind == NativeKind.NONE) {
      return mTotalNativeChildren;
    }
    if (localNativeKind == NativeKind.LEAF) {
      return 1 + mTotalNativeChildren;
    }
    return 1;
  }
  
  private void updateNativeChildrenCountInParent(int paramInt)
  {
    if (getNativeKind() != NativeKind.PARENT) {
      for (ReactShadowNodeImpl localReactShadowNodeImpl = getParent(); localReactShadowNodeImpl != null; localReactShadowNodeImpl = localReactShadowNodeImpl.getParent())
      {
        mTotalNativeChildren += paramInt;
        if (localReactShadowNodeImpl.getNativeKind() == NativeKind.PARENT) {
          return;
        }
      }
    }
  }
  
  private void updatePadding()
  {
    int i = 0;
    while (i <= 8)
    {
      if ((i != 0) && (i != 2) && (i != 4) && (i != 5))
      {
        if ((i != 1) && (i != 3))
        {
          if (YogaConstants.isUndefined(mPadding[i]))
          {
            mYogaNode.setPadding(YogaEdge.fromInt(i), mDefaultPadding.getRaw(i));
            break label243;
          }
        }
        else if ((YogaConstants.isUndefined(mPadding[i])) && (YogaConstants.isUndefined(mPadding[7])) && (YogaConstants.isUndefined(mPadding[8])))
        {
          mYogaNode.setPadding(YogaEdge.fromInt(i), mDefaultPadding.getRaw(i));
          break label243;
        }
      }
      else if ((YogaConstants.isUndefined(mPadding[i])) && (YogaConstants.isUndefined(mPadding[6])) && (YogaConstants.isUndefined(mPadding[8])))
      {
        mYogaNode.setPadding(YogaEdge.fromInt(i), mDefaultPadding.getRaw(i));
        break label243;
      }
      if (mPaddingIsPercent[i] != 0) {
        mYogaNode.setPaddingPercent(YogaEdge.fromInt(i), mPadding[i]);
      } else {
        mYogaNode.setPadding(YogaEdge.fromInt(i), mPadding[i]);
      }
      label243:
      i += 1;
    }
  }
  
  public void addChildAt(ReactShadowNodeImpl paramReactShadowNodeImpl, int paramInt)
  {
    if (mChildren == null) {
      mChildren = new ArrayList(4);
    }
    mChildren.add(paramInt, paramReactShadowNodeImpl);
    mParent = this;
    if ((mYogaNode != null) && (!isYogaLeafNode()))
    {
      Object localObject = mYogaNode;
      if (localObject != null)
      {
        mYogaNode.addChildAt((YogaNode)localObject, paramInt);
      }
      else
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Cannot add a child that doesn't have a YogaNode to a parent without a measure function! (Trying to add a '");
        ((StringBuilder)localObject).append(paramReactShadowNodeImpl.toString());
        ((StringBuilder)localObject).append("' to a '");
        ((StringBuilder)localObject).append(toString());
        ((StringBuilder)localObject).append("')");
        throw new RuntimeException(((StringBuilder)localObject).toString());
      }
    }
    markUpdated();
    paramInt = paramReactShadowNodeImpl.getTotalNativeNodeContributionToParent();
    mTotalNativeChildren += paramInt;
    updateNativeChildrenCountInParent(paramInt);
  }
  
  public final void addNativeChildAt(ReactShadowNodeImpl paramReactShadowNodeImpl, int paramInt)
  {
    NativeKind localNativeKind1 = getNativeKind();
    NativeKind localNativeKind2 = NativeKind.PARENT;
    boolean bool2 = false;
    if (localNativeKind1 == localNativeKind2) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Assertions.assertCondition(bool1);
    boolean bool1 = bool2;
    if (paramReactShadowNodeImpl.getNativeKind() != NativeKind.NONE) {
      bool1 = true;
    }
    Assertions.assertCondition(bool1);
    if (mNativeChildren == null) {
      mNativeChildren = new ArrayList(4);
    }
    mNativeChildren.add(paramInt, paramReactShadowNodeImpl);
    mNativeParent = this;
  }
  
  public void calculateLayout()
  {
    calculateLayout(NaN.0F, NaN.0F);
  }
  
  public void calculateLayout(float paramFloat1, float paramFloat2)
  {
    mYogaNode.calculateLayout(paramFloat1, paramFloat2);
  }
  
  public Iterable calculateLayoutOnChildren()
  {
    if (isVirtualAnchor()) {
      return null;
    }
    return mChildren;
  }
  
  public void dirty()
  {
    if (!isVirtual())
    {
      mYogaNode.dirty();
      return;
    }
    if (getParent() != null) {
      getParent().dirty();
    }
  }
  
  public boolean dispatchUpdates(float paramFloat1, float paramFloat2, UIViewOperationQueue paramUIViewOperationQueue, NativeViewHierarchyOptimizer paramNativeViewHierarchyOptimizer)
  {
    if (mNodeUpdated) {
      onCollectExtraUpdates(paramUIViewOperationQueue);
    }
    boolean bool2 = hasNewLayout();
    boolean bool1 = false;
    if (bool2)
    {
      float f1 = getLayoutX();
      float f2 = getLayoutY();
      paramFloat1 += f1;
      int n = Math.round(paramFloat1);
      paramFloat2 += f2;
      int k = Math.round(paramFloat2);
      int i1 = Math.round(paramFloat1 + getLayoutWidth());
      int m = Math.round(paramFloat2 + getLayoutHeight());
      int i = Math.round(f1);
      int j = Math.round(f2);
      n = i1 - n;
      k = m - k;
      if ((i != mScreenX) || (j != mScreenY) || (n != mScreenWidth) || (k != mScreenHeight)) {
        bool1 = true;
      }
      mScreenX = i;
      mScreenY = j;
      mScreenWidth = n;
      mScreenHeight = k;
      if (bool1)
      {
        if (paramNativeViewHierarchyOptimizer != null)
        {
          paramNativeViewHierarchyOptimizer.handleUpdateLayout(this);
          return bool1;
        }
        paramUIViewOperationQueue.enqueueUpdateLayout(getParent().getReactTag(), getReactTag(), getScreenX(), getScreenY(), getScreenWidth(), getScreenHeight());
        return bool1;
      }
    }
    else
    {
      return false;
    }
    return bool1;
  }
  
  public void dispose()
  {
    if (mYogaNode != null)
    {
      mYogaNode.reset();
      YogaNodePool.getInstance().release(mYogaNode);
    }
  }
  
  public final ReactShadowNodeImpl getChildAt(int paramInt)
  {
    if (mChildren != null) {
      return (ReactShadowNodeImpl)mChildren.get(paramInt);
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Index ");
    localStringBuilder.append(paramInt);
    localStringBuilder.append(" out of bounds: node has no children");
    throw new ArrayIndexOutOfBoundsException(localStringBuilder.toString());
  }
  
  public final int getChildCount()
  {
    if (mChildren == null) {
      return 0;
    }
    return mChildren.size();
  }
  
  public Integer getHeightMeasureSpec()
  {
    return mHeightMeasureSpec;
  }
  
  public String getHierarchyInfo()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    getHierarchyInfoWithIndentation(localStringBuilder, 0);
    return localStringBuilder.toString();
  }
  
  public final YogaDirection getLayoutDirection()
  {
    return mYogaNode.getLayoutDirection();
  }
  
  public final float getLayoutHeight()
  {
    return mYogaNode.getLayoutHeight();
  }
  
  public final ReactShadowNodeImpl getLayoutParent()
  {
    if (mLayoutParent != null) {
      return mLayoutParent;
    }
    return getNativeParent();
  }
  
  public final float getLayoutWidth()
  {
    return mYogaNode.getLayoutWidth();
  }
  
  public final float getLayoutX()
  {
    return mYogaNode.getLayoutX();
  }
  
  public final float getLayoutY()
  {
    return mYogaNode.getLayoutY();
  }
  
  public final int getNativeChildCount()
  {
    if (mNativeChildren == null) {
      return 0;
    }
    return mNativeChildren.size();
  }
  
  public NativeKind getNativeKind()
  {
    if ((!isVirtual()) && (!isLayoutOnly()))
    {
      if (hoistNativeChildren()) {
        return NativeKind.LEAF;
      }
      return NativeKind.PARENT;
    }
    return NativeKind.NONE;
  }
  
  public final int getNativeOffsetForChild(ReactShadowNodeImpl paramReactShadowNodeImpl)
  {
    int m = 0;
    int j = 0;
    int i = 0;
    int k;
    for (;;)
    {
      k = m;
      if (j >= getChildCount()) {
        break;
      }
      localObject = getChildAt(j);
      if (paramReactShadowNodeImpl == localObject)
      {
        k = 1;
        break;
      }
      i += ((ReactShadowNodeImpl)localObject).getTotalNativeNodeContributionToParent();
      j += 1;
    }
    if (k != 0) {
      return i;
    }
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Child ");
    ((StringBuilder)localObject).append(paramReactShadowNodeImpl.getReactTag());
    ((StringBuilder)localObject).append(" was not a child of ");
    ((StringBuilder)localObject).append(mReactTag);
    throw new RuntimeException(((StringBuilder)localObject).toString());
  }
  
  public final ReactShadowNodeImpl getNativeParent()
  {
    return mNativeParent;
  }
  
  public final float getPadding(int paramInt)
  {
    return mYogaNode.getLayoutPadding(YogaEdge.fromInt(paramInt));
  }
  
  public final ReactShadowNodeImpl getParent()
  {
    return mParent;
  }
  
  public final int getReactTag()
  {
    return mReactTag;
  }
  
  public final int getRootTag()
  {
    boolean bool;
    if (mRootTag != 0) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.assertCondition(bool);
    return mRootTag;
  }
  
  public int getScreenHeight()
  {
    return mScreenHeight;
  }
  
  public int getScreenWidth()
  {
    return mScreenWidth;
  }
  
  public int getScreenX()
  {
    return mScreenX;
  }
  
  public int getScreenY()
  {
    return mScreenY;
  }
  
  public final YogaValue getStyleHeight()
  {
    return mYogaNode.getHeight();
  }
  
  public final YogaValue getStylePadding(int paramInt)
  {
    return mYogaNode.getPadding(YogaEdge.fromInt(paramInt));
  }
  
  public final YogaValue getStyleWidth()
  {
    return mYogaNode.getWidth();
  }
  
  public final ThemedReactContext getThemedContext()
  {
    return (ThemedReactContext)Assertions.assertNotNull(mThemedContext);
  }
  
  public final int getTotalNativeChildren()
  {
    return mTotalNativeChildren;
  }
  
  public final String getViewClass()
  {
    return (String)Assertions.assertNotNull(mViewClassName);
  }
  
  public Integer getWidthMeasureSpec()
  {
    return mWidthMeasureSpec;
  }
  
  public final boolean hasNewLayout()
  {
    return (mYogaNode != null) && (mYogaNode.hasNewLayout());
  }
  
  public final boolean hasUnseenUpdates()
  {
    return mNodeUpdated;
  }
  
  public final boolean hasUpdates()
  {
    return (mNodeUpdated) || (hasNewLayout()) || (isDirty());
  }
  
  public boolean hoistNativeChildren()
  {
    return false;
  }
  
  public final int indexOf(ReactShadowNodeImpl paramReactShadowNodeImpl)
  {
    if (mChildren == null) {
      return -1;
    }
    return mChildren.indexOf(paramReactShadowNodeImpl);
  }
  
  public final int indexOfNativeChild(ReactShadowNodeImpl paramReactShadowNodeImpl)
  {
    Assertions.assertNotNull(mNativeChildren);
    return mNativeChildren.indexOf(paramReactShadowNodeImpl);
  }
  
  public boolean isDescendantOf(ReactShadowNodeImpl paramReactShadowNodeImpl)
  {
    for (ReactShadowNodeImpl localReactShadowNodeImpl = getParent(); localReactShadowNodeImpl != null; localReactShadowNodeImpl = localReactShadowNodeImpl.getParent()) {
      if (localReactShadowNodeImpl == paramReactShadowNodeImpl) {
        return true;
      }
    }
    return false;
  }
  
  public final boolean isDirty()
  {
    return (mYogaNode != null) && (mYogaNode.isDirty());
  }
  
  public final boolean isLayoutOnly()
  {
    return mIsLayoutOnly;
  }
  
  public boolean isMeasureDefined()
  {
    return mYogaNode.isMeasureDefined();
  }
  
  public boolean isVirtual()
  {
    return false;
  }
  
  public boolean isVirtualAnchor()
  {
    return false;
  }
  
  public boolean isYogaLeafNode()
  {
    return isMeasureDefined();
  }
  
  public final void markLayoutSeen()
  {
    if (mYogaNode != null) {
      mYogaNode.markLayoutSeen();
    }
  }
  
  public final void markUpdateSeen()
  {
    mNodeUpdated = false;
    if (hasNewLayout()) {
      markLayoutSeen();
    }
  }
  
  public void markUpdated()
  {
    if (mNodeUpdated) {
      return;
    }
    mNodeUpdated = true;
    ReactShadowNodeImpl localReactShadowNodeImpl = getParent();
    if (localReactShadowNodeImpl != null) {
      localReactShadowNodeImpl.markUpdated();
    }
  }
  
  public void onAfterUpdateTransaction() {}
  
  public void onBeforeLayout(NativeViewHierarchyOptimizer paramNativeViewHierarchyOptimizer) {}
  
  public void onCollectExtraUpdates(UIViewOperationQueue paramUIViewOperationQueue) {}
  
  public final void removeAllNativeChildren()
  {
    if (mNativeChildren != null)
    {
      int i = mNativeChildren.size() - 1;
      while (i >= 0)
      {
        mNativeChildren.get(i)).mNativeParent = null;
        i -= 1;
      }
      mNativeChildren.clear();
    }
  }
  
  public void removeAndDisposeAllChildren()
  {
    if (getChildCount() == 0) {
      return;
    }
    int j = 0;
    int i = getChildCount() - 1;
    while (i >= 0)
    {
      if ((mYogaNode != null) && (!isYogaLeafNode())) {
        mYogaNode.removeChildAt(i);
      }
      ReactShadowNodeImpl localReactShadowNodeImpl = getChildAt(i);
      mParent = null;
      j += localReactShadowNodeImpl.getTotalNativeNodeContributionToParent();
      localReactShadowNodeImpl.dispose();
      i -= 1;
    }
    ((ArrayList)Assertions.assertNotNull(mChildren)).clear();
    markUpdated();
    mTotalNativeChildren -= j;
    updateNativeChildrenCountInParent(-j);
  }
  
  public ReactShadowNodeImpl removeChildAt(int paramInt)
  {
    if (mChildren != null)
    {
      localObject = (ReactShadowNodeImpl)mChildren.remove(paramInt);
      mParent = null;
      if ((mYogaNode != null) && (!isYogaLeafNode())) {
        mYogaNode.removeChildAt(paramInt);
      }
      markUpdated();
      paramInt = ((ReactShadowNodeImpl)localObject).getTotalNativeNodeContributionToParent();
      mTotalNativeChildren -= paramInt;
      updateNativeChildrenCountInParent(-paramInt);
      return localObject;
    }
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Index ");
    ((StringBuilder)localObject).append(paramInt);
    ((StringBuilder)localObject).append(" out of bounds: node has no children");
    throw new ArrayIndexOutOfBoundsException(((StringBuilder)localObject).toString());
  }
  
  public final ReactShadowNodeImpl removeNativeChildAt(int paramInt)
  {
    Assertions.assertNotNull(mNativeChildren);
    ReactShadowNodeImpl localReactShadowNodeImpl = (ReactShadowNodeImpl)mNativeChildren.remove(paramInt);
    mNativeParent = null;
    return localReactShadowNodeImpl;
  }
  
  public void setAlignContent(YogaAlign paramYogaAlign)
  {
    mYogaNode.setAlignContent(paramYogaAlign);
  }
  
  public void setAlignItems(YogaAlign paramYogaAlign)
  {
    mYogaNode.setAlignItems(paramYogaAlign);
  }
  
  public void setAlignSelf(YogaAlign paramYogaAlign)
  {
    mYogaNode.setAlignSelf(paramYogaAlign);
  }
  
  public void setBaselineFunction(YogaBaselineFunction paramYogaBaselineFunction)
  {
    mYogaNode.setBaselineFunction(paramYogaBaselineFunction);
  }
  
  public void setBorder(int paramInt, float paramFloat)
  {
    mYogaNode.setBorder(YogaEdge.fromInt(paramInt), paramFloat);
  }
  
  public void setDefaultPadding(int paramInt, float paramFloat)
  {
    mDefaultPadding.writeLong(paramInt, paramFloat);
    updatePadding();
  }
  
  public void setDisplay(YogaDisplay paramYogaDisplay)
  {
    mYogaNode.setDisplay(paramYogaDisplay);
  }
  
  public void setFlex(float paramFloat)
  {
    mYogaNode.setFlex(paramFloat);
  }
  
  public void setFlexBasis(float paramFloat)
  {
    mYogaNode.setFlexBasis(paramFloat);
  }
  
  public void setFlexBasisAuto()
  {
    mYogaNode.setFlexBasisAuto();
  }
  
  public void setFlexBasisPercent(float paramFloat)
  {
    mYogaNode.setFlexBasisPercent(paramFloat);
  }
  
  public void setFlexDirection(YogaFlexDirection paramYogaFlexDirection)
  {
    mYogaNode.setFlexDirection(paramYogaFlexDirection);
  }
  
  public void setFlexGrow(float paramFloat)
  {
    mYogaNode.setFlexGrow(paramFloat);
  }
  
  public void setFlexShrink(float paramFloat)
  {
    mYogaNode.setFlexShrink(paramFloat);
  }
  
  public void setFlexWrap(YogaWrap paramYogaWrap)
  {
    mYogaNode.setWrap(paramYogaWrap);
  }
  
  public final void setIsLayoutOnly(boolean paramBoolean)
  {
    ReactShadowNodeImpl localReactShadowNodeImpl = getParent();
    boolean bool2 = false;
    if (localReactShadowNodeImpl == null) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Assertions.assertCondition(bool1, "Must remove from no opt parent first");
    if (mNativeParent == null) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Assertions.assertCondition(bool1, "Must remove from native parent first");
    boolean bool1 = bool2;
    if (getNativeChildCount() == 0) {
      bool1 = true;
    }
    Assertions.assertCondition(bool1, "Must remove all native children first");
    mIsLayoutOnly = paramBoolean;
  }
  
  public void setJustifyContent(YogaJustify paramYogaJustify)
  {
    mYogaNode.setJustifyContent(paramYogaJustify);
  }
  
  public void setLayoutDirection(YogaDirection paramYogaDirection)
  {
    mYogaNode.setDirection(paramYogaDirection);
  }
  
  public final void setLayoutParent(ReactShadowNodeImpl paramReactShadowNodeImpl)
  {
    mLayoutParent = paramReactShadowNodeImpl;
  }
  
  public void setLocalData(Object paramObject) {}
  
  public void setMargin(int paramInt, float paramFloat)
  {
    mYogaNode.setMargin(YogaEdge.fromInt(paramInt), paramFloat);
  }
  
  public void setMarginAuto(int paramInt)
  {
    mYogaNode.setMarginAuto(YogaEdge.fromInt(paramInt));
  }
  
  public void setMarginPercent(int paramInt, float paramFloat)
  {
    mYogaNode.setMarginPercent(YogaEdge.fromInt(paramInt), paramFloat);
  }
  
  public void setMeasureFunction(YogaMeasureFunction paramYogaMeasureFunction)
  {
    mYogaNode.setMeasureFunction(paramYogaMeasureFunction);
  }
  
  public void setMeasureSpecs(int paramInt1, int paramInt2)
  {
    mWidthMeasureSpec = Integer.valueOf(paramInt1);
    mHeightMeasureSpec = Integer.valueOf(paramInt2);
  }
  
  public void setOverflow(YogaOverflow paramYogaOverflow)
  {
    mYogaNode.setOverflow(paramYogaOverflow);
  }
  
  public void setPadding(int paramInt, float paramFloat)
  {
    mPadding[paramInt] = paramFloat;
    mPaddingIsPercent[paramInt] = false;
    updatePadding();
  }
  
  public void setPaddingPercent(int paramInt, float paramFloat)
  {
    mPadding[paramInt] = paramFloat;
    mPaddingIsPercent[paramInt] = (YogaConstants.isUndefined(paramFloat) ^ true);
    updatePadding();
  }
  
  public void setPosition(int paramInt, float paramFloat)
  {
    mYogaNode.setPosition(YogaEdge.fromInt(paramInt), paramFloat);
  }
  
  public void setPositionPercent(int paramInt, float paramFloat)
  {
    mYogaNode.setPositionPercent(YogaEdge.fromInt(paramInt), paramFloat);
  }
  
  public void setPositionType(YogaPositionType paramYogaPositionType)
  {
    mYogaNode.setPositionType(paramYogaPositionType);
  }
  
  public void setReactTag(int paramInt)
  {
    mReactTag = paramInt;
  }
  
  public final void setRootTag(int paramInt)
  {
    mRootTag = paramInt;
  }
  
  public void setShouldNotifyOnLayout(boolean paramBoolean)
  {
    mShouldNotifyOnLayout = paramBoolean;
  }
  
  public void setStyleAspectRatio(float paramFloat)
  {
    mYogaNode.setAspectRatio(paramFloat);
  }
  
  public void setStyleHeight(float paramFloat)
  {
    mYogaNode.setHeight(paramFloat);
  }
  
  public void setStyleHeightAuto()
  {
    mYogaNode.setHeightAuto();
  }
  
  public void setStyleHeightPercent(float paramFloat)
  {
    mYogaNode.setHeightPercent(paramFloat);
  }
  
  public void setStyleMaxHeight(float paramFloat)
  {
    mYogaNode.setMaxHeight(paramFloat);
  }
  
  public void setStyleMaxHeightPercent(float paramFloat)
  {
    mYogaNode.setMaxHeightPercent(paramFloat);
  }
  
  public void setStyleMaxWidth(float paramFloat)
  {
    mYogaNode.setMaxWidth(paramFloat);
  }
  
  public void setStyleMaxWidthPercent(float paramFloat)
  {
    mYogaNode.setMaxWidthPercent(paramFloat);
  }
  
  public void setStyleMinHeight(float paramFloat)
  {
    mYogaNode.setMinHeight(paramFloat);
  }
  
  public void setStyleMinHeightPercent(float paramFloat)
  {
    mYogaNode.setMinHeightPercent(paramFloat);
  }
  
  public void setStyleMinWidth(float paramFloat)
  {
    mYogaNode.setMinWidth(paramFloat);
  }
  
  public void setStyleMinWidthPercent(float paramFloat)
  {
    mYogaNode.setMinWidthPercent(paramFloat);
  }
  
  public void setStyleWidth(float paramFloat)
  {
    mYogaNode.setWidth(paramFloat);
  }
  
  public void setStyleWidthAuto()
  {
    mYogaNode.setWidthAuto();
  }
  
  public void setStyleWidthPercent(float paramFloat)
  {
    mYogaNode.setWidthPercent(paramFloat);
  }
  
  public void setThemedContext(ThemedReactContext paramThemedReactContext)
  {
    mThemedContext = paramThemedReactContext;
  }
  
  public final void setViewClassName(String paramString)
  {
    mViewClassName = paramString;
  }
  
  public final boolean shouldNotifyOnLayout()
  {
    return mShouldNotifyOnLayout;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("[");
    localStringBuilder.append(mViewClassName);
    localStringBuilder.append(" ");
    localStringBuilder.append(getReactTag());
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  public final void updateProperties(ReactStylesDiffMap paramReactStylesDiffMap)
  {
    ViewManagerPropertyUpdater.updateProps(this, paramReactStylesDiffMap);
    onAfterUpdateTransaction();
  }
}
