package com.facebook.yoga;

import com.facebook.proguard.annotations.DoNotStrip;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

@DoNotStrip
public abstract class YogaNodeJNIBase
  extends YogaNode
  implements Cloneable
{
  private static final byte BORDER = 4;
  private static final byte DOES_LEGACY_STRETCH_BEHAVIOUR = 8;
  private static final byte HAS_NEW_LAYOUT = 16;
  private static final byte LAYOUT_BORDER_START_INDEX = 14;
  private static final byte LAYOUT_DIRECTION_INDEX = 5;
  private static final byte LAYOUT_EDGE_SET_FLAG_INDEX = 0;
  private static final byte LAYOUT_HEIGHT_INDEX = 2;
  private static final byte LAYOUT_LEFT_INDEX = 3;
  private static final byte LAYOUT_MARGIN_START_INDEX = 6;
  private static final byte LAYOUT_PADDING_START_INDEX = 10;
  private static final byte LAYOUT_TOP_INDEX = 4;
  private static final byte LAYOUT_WIDTH_INDEX = 1;
  private static final byte MARGIN = 1;
  private static final byte PADDING = 2;
  @Nullable
  private YogaBaselineFunction mBaselineFunction;
  @Nullable
  private List<YogaNodeJNIBase> mChildren;
  @Nullable
  private Object mData;
  private boolean mHasNewLayout = true;
  @DoNotStrip
  private int mLayoutDirection = 0;
  @Nullable
  private YogaMeasureFunction mMeasureFunction;
  protected long mNativePointer;
  @Nullable
  @DoNotStrip
  private float[] mOnTextChangedListener = null;
  @Nullable
  private YogaNodeJNIBase mOwner;
  
  YogaNodeJNIBase()
  {
    this(YogaNative.jni_YGNodeNew());
  }
  
  private YogaNodeJNIBase(long paramLong)
  {
    if (paramLong != 0L)
    {
      mNativePointer = paramLong;
      return;
    }
    throw new IllegalStateException("Failed to allocate native memory");
  }
  
  YogaNodeJNIBase(YogaConfig paramYogaConfig)
  {
    this(YogaNative.jni_YGNodeNewWithConfig(mNativePointer));
  }
  
  private void clearChildren()
  {
    mChildren = null;
    YogaNative.jni_YGNodeClearChildren(mNativePointer);
  }
  
  private final long replaceChild(YogaNodeJNIBase paramYogaNodeJNIBase, int paramInt)
  {
    if (mChildren != null)
    {
      mChildren.remove(paramInt);
      mChildren.add(paramInt, paramYogaNodeJNIBase);
      mOwner = this;
      return mNativePointer;
    }
    throw new IllegalStateException("Cannot replace child. YogaNode does not have children");
  }
  
  private static YogaValue valueFromLong(long paramLong)
  {
    return new YogaValue(Float.intBitsToFloat((int)paramLong), (int)(paramLong >> 32));
  }
  
  public void addChildAt(YogaNode paramYogaNode, int paramInt)
  {
    paramYogaNode = (YogaNodeJNIBase)paramYogaNode;
    if (mOwner == null)
    {
      if (mChildren == null) {
        mChildren = new ArrayList(4);
      }
      mChildren.add(paramInt, paramYogaNode);
      mOwner = this;
      YogaNative.jni_YGNodeInsertChild(mNativePointer, mNativePointer, paramInt);
      return;
    }
    throw new IllegalStateException("Child already has a parent, it must be removed first.");
  }
  
  public final float baseline(float paramFloat1, float paramFloat2)
  {
    return mBaselineFunction.baseline(this, paramFloat1, paramFloat2);
  }
  
  public void calculateLayout(float paramFloat1, float paramFloat2)
  {
    Object localObject1 = new ArrayList();
    ((ArrayList)localObject1).add(this);
    int j = 0;
    int i = 0;
    while (i < ((ArrayList)localObject1).size())
    {
      localObject2 = getmChildren;
      if (localObject2 != null) {
        ((ArrayList)localObject1).addAll((Collection)localObject2);
      }
      i += 1;
    }
    localObject1 = (YogaNodeJNIBase[])((ArrayList)localObject1).toArray(new YogaNodeJNIBase[((ArrayList)localObject1).size()]);
    Object localObject2 = new long[localObject1.length];
    i = j;
    while (i < localObject1.length)
    {
      localObject2[i] = mNativePointer;
      i += 1;
    }
    YogaNative.jni_YGNodeCalculateLayout(mNativePointer, paramFloat1, paramFloat2, (long[])localObject2, (YogaNodeJNIBase[])localObject1);
  }
  
  public YogaNodeJNIBase cloneWithoutChildren()
  {
    try
    {
      Object localObject = super.clone();
      localObject = (YogaNodeJNIBase)localObject;
      long l = mNativePointer;
      l = YogaNative.jni_YGNodeClone(l);
      mOwner = null;
      mNativePointer = l;
      ((YogaNodeJNIBase)localObject).clearChildren();
      return localObject;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new RuntimeException(localCloneNotSupportedException);
    }
  }
  
  public void copyStyle(YogaNode paramYogaNode)
  {
    YogaNative.jni_YGNodeCopyStyle(mNativePointer, mNativePointer);
  }
  
  public void dirty()
  {
    YogaNative.jni_YGNodeMarkDirty(mNativePointer);
  }
  
  public void dirtyAllDescendants()
  {
    YogaNative.jni_YGNodeMarkDirtyAndPropogateToDescendants(mNativePointer);
  }
  
  public YogaAlign getAlignContent()
  {
    return YogaAlign.fromInt(YogaNative.jni_YGNodeStyleGetAlignContent(mNativePointer));
  }
  
  public YogaAlign getAlignItems()
  {
    return YogaAlign.fromInt(YogaNative.jni_YGNodeStyleGetAlignItems(mNativePointer));
  }
  
  public YogaAlign getAlignSelf()
  {
    return YogaAlign.fromInt(YogaNative.jni_YGNodeStyleGetAlignSelf(mNativePointer));
  }
  
  public float getAspectRatio()
  {
    return YogaNative.jni_YGNodeStyleGetAspectRatio(mNativePointer);
  }
  
  public float getBorder(YogaEdge paramYogaEdge)
  {
    return YogaNative.jni_YGNodeStyleGetBorder(mNativePointer, paramYogaEdge.intValue());
  }
  
  public YogaNodeJNIBase getChildAt(int paramInt)
  {
    if (mChildren != null) {
      return (YogaNodeJNIBase)mChildren.get(paramInt);
    }
    throw new IllegalStateException("YogaNode does not have children");
  }
  
  public int getChildCount()
  {
    if (mChildren == null) {
      return 0;
    }
    return mChildren.size();
  }
  
  public Object getData()
  {
    return mData;
  }
  
  public YogaDisplay getDisplay()
  {
    return YogaDisplay.fromInt(YogaNative.jni_YGNodeStyleGetDisplay(mNativePointer));
  }
  
  public boolean getDoesLegacyStretchFlagAffectsLayout()
  {
    return (mOnTextChangedListener != null) && (((int)mOnTextChangedListener[0] & 0x8) == 8);
  }
  
  public float getFlex()
  {
    return YogaNative.jni_YGNodeStyleGetFlex(mNativePointer);
  }
  
  public YogaValue getFlexBasis()
  {
    return valueFromLong(YogaNative.jni_YGNodeStyleGetFlexBasis(mNativePointer));
  }
  
  public YogaFlexDirection getFlexDirection()
  {
    return YogaFlexDirection.fromInt(YogaNative.jni_YGNodeStyleGetFlexDirection(mNativePointer));
  }
  
  public float getFlexGrow()
  {
    return YogaNative.jni_YGNodeStyleGetFlexGrow(mNativePointer);
  }
  
  public float getFlexShrink()
  {
    return YogaNative.jni_YGNodeStyleGetFlexShrink(mNativePointer);
  }
  
  public YogaValue getHeight()
  {
    return valueFromLong(YogaNative.jni_YGNodeStyleGetHeight(mNativePointer));
  }
  
  public YogaJustify getJustifyContent()
  {
    return YogaJustify.fromInt(YogaNative.jni_YGNodeStyleGetJustifyContent(mNativePointer));
  }
  
  public float getLayoutBorder(YogaEdge paramYogaEdge)
  {
    if (mOnTextChangedListener != null)
    {
      float[] arrayOfFloat = mOnTextChangedListener;
      int j = 0;
      if (((int)arrayOfFloat[0] & 0x4) == 4)
      {
        if (((int)mOnTextChangedListener[0] & 0x1) == 1) {
          i = 0;
        } else {
          i = 4;
        }
        if (((int)mOnTextChangedListener[0] & 0x2) != 2) {
          j = 4;
        }
        int i = 14 - i - j;
        switch (1.$SwitchMap$com$facebook$yoga$YogaEdge[paramYogaEdge.ordinal()])
        {
        default: 
          throw new IllegalArgumentException("Cannot get layout border of multi-edge shorthands");
        case 6: 
          if (getLayoutDirection() == YogaDirection.RIGHT) {
            return mOnTextChangedListener[i];
          }
          return mOnTextChangedListener[(i + 2)];
        case 5: 
          if (getLayoutDirection() == YogaDirection.RIGHT) {
            return mOnTextChangedListener[(i + 2)];
          }
          return mOnTextChangedListener[i];
        case 4: 
          return mOnTextChangedListener[(i + 3)];
        case 3: 
          return mOnTextChangedListener[(i + 2)];
        case 2: 
          return mOnTextChangedListener[(i + 1)];
        }
        return mOnTextChangedListener[i];
      }
    }
    return 0.0F;
  }
  
  public YogaDirection getLayoutDirection()
  {
    int i;
    if (mOnTextChangedListener != null) {
      i = (int)mOnTextChangedListener[5];
    } else {
      i = mLayoutDirection;
    }
    return YogaDirection.fromInt(i);
  }
  
  public float getLayoutHeight()
  {
    if (mOnTextChangedListener != null) {
      return mOnTextChangedListener[2];
    }
    return 0.0F;
  }
  
  public float getLayoutMargin(YogaEdge paramYogaEdge)
  {
    if ((mOnTextChangedListener != null) && (((int)mOnTextChangedListener[0] & 0x1) == 1))
    {
      switch (1.$SwitchMap$com$facebook$yoga$YogaEdge[paramYogaEdge.ordinal()])
      {
      default: 
        throw new IllegalArgumentException("Cannot get layout margins of multi-edge shorthands");
      case 6: 
        if (getLayoutDirection() == YogaDirection.RIGHT) {
          return mOnTextChangedListener[6];
        }
        return mOnTextChangedListener[8];
      case 5: 
        if (getLayoutDirection() == YogaDirection.RIGHT) {
          return mOnTextChangedListener[8];
        }
        return mOnTextChangedListener[6];
      case 4: 
        return mOnTextChangedListener[9];
      case 3: 
        return mOnTextChangedListener[8];
      case 2: 
        return mOnTextChangedListener[7];
      }
      return mOnTextChangedListener[6];
    }
    return 0.0F;
  }
  
  public float getLayoutPadding(YogaEdge paramYogaEdge)
  {
    if (mOnTextChangedListener != null)
    {
      float[] arrayOfFloat = mOnTextChangedListener;
      int i = 0;
      if (((int)arrayOfFloat[0] & 0x2) == 2)
      {
        if (((int)mOnTextChangedListener[0] & 0x1) != 1) {
          i = 4;
        }
        i = 10 - i;
        switch (1.$SwitchMap$com$facebook$yoga$YogaEdge[paramYogaEdge.ordinal()])
        {
        default: 
          throw new IllegalArgumentException("Cannot get layout paddings of multi-edge shorthands");
        case 6: 
          if (getLayoutDirection() == YogaDirection.RIGHT) {
            return mOnTextChangedListener[i];
          }
          return mOnTextChangedListener[(i + 2)];
        case 5: 
          if (getLayoutDirection() == YogaDirection.RIGHT) {
            return mOnTextChangedListener[(i + 2)];
          }
          return mOnTextChangedListener[i];
        case 4: 
          return mOnTextChangedListener[(i + 3)];
        case 3: 
          return mOnTextChangedListener[(i + 2)];
        case 2: 
          return mOnTextChangedListener[(i + 1)];
        }
        return mOnTextChangedListener[i];
      }
    }
    return 0.0F;
  }
  
  public float getLayoutWidth()
  {
    if (mOnTextChangedListener != null) {
      return mOnTextChangedListener[1];
    }
    return 0.0F;
  }
  
  public float getLayoutX()
  {
    if (mOnTextChangedListener != null) {
      return mOnTextChangedListener[3];
    }
    return 0.0F;
  }
  
  public float getLayoutY()
  {
    if (mOnTextChangedListener != null) {
      return mOnTextChangedListener[4];
    }
    return 0.0F;
  }
  
  public YogaValue getMargin(YogaEdge paramYogaEdge)
  {
    return valueFromLong(YogaNative.jni_YGNodeStyleGetMargin(mNativePointer, paramYogaEdge.intValue()));
  }
  
  public YogaValue getMaxHeight()
  {
    return valueFromLong(YogaNative.jni_YGNodeStyleGetMaxHeight(mNativePointer));
  }
  
  public YogaValue getMaxWidth()
  {
    return valueFromLong(YogaNative.jni_YGNodeStyleGetMaxWidth(mNativePointer));
  }
  
  public YogaValue getMinHeight()
  {
    return valueFromLong(YogaNative.jni_YGNodeStyleGetMinHeight(mNativePointer));
  }
  
  public YogaValue getMinWidth()
  {
    return valueFromLong(YogaNative.jni_YGNodeStyleGetMinWidth(mNativePointer));
  }
  
  public YogaOverflow getOverflow()
  {
    return YogaOverflow.fromInt(YogaNative.jni_YGNodeStyleGetOverflow(mNativePointer));
  }
  
  public YogaNodeJNIBase getOwner()
  {
    return mOwner;
  }
  
  public YogaValue getPadding(YogaEdge paramYogaEdge)
  {
    return valueFromLong(YogaNative.jni_YGNodeStyleGetPadding(mNativePointer, paramYogaEdge.intValue()));
  }
  
  public YogaNodeJNIBase getParent()
  {
    return getOwner();
  }
  
  public YogaValue getPosition(YogaEdge paramYogaEdge)
  {
    return valueFromLong(YogaNative.jni_YGNodeStyleGetPosition(mNativePointer, paramYogaEdge.intValue()));
  }
  
  public YogaPositionType getPositionType()
  {
    return YogaPositionType.fromInt(YogaNative.jni_YGNodeStyleGetPositionType(mNativePointer));
  }
  
  public YogaDirection getStyleDirection()
  {
    return YogaDirection.fromInt(YogaNative.jni_YGNodeStyleGetDirection(mNativePointer));
  }
  
  public YogaValue getWidth()
  {
    return valueFromLong(YogaNative.jni_YGNodeStyleGetWidth(mNativePointer));
  }
  
  public YogaWrap getWrap()
  {
    return YogaWrap.fromInt(YogaNative.jni_YGNodeStyleGetFlexWrap(mNativePointer));
  }
  
  public boolean hasNewLayout()
  {
    if (mOnTextChangedListener != null)
    {
      if (((int)mOnTextChangedListener[0] & 0x10) == 16) {
        return true;
      }
    }
    else {
      return mHasNewLayout;
    }
    return false;
  }
  
  public int indexOf(YogaNode paramYogaNode)
  {
    if (mChildren == null) {
      return -1;
    }
    return mChildren.indexOf(paramYogaNode);
  }
  
  public boolean isBaselineDefined()
  {
    return mBaselineFunction != null;
  }
  
  public boolean isDirty()
  {
    return YogaNative.jni_YGNodeIsDirty(mNativePointer);
  }
  
  public boolean isMeasureDefined()
  {
    return mMeasureFunction != null;
  }
  
  public boolean isReferenceBaseline()
  {
    return YogaNative.jni_YGNodeIsReferenceBaseline(mNativePointer);
  }
  
  public void markLayoutSeen()
  {
    if (mOnTextChangedListener != null) {
      mOnTextChangedListener[0] = ((int)mOnTextChangedListener[0] & 0xFFFFFFEF);
    }
    mHasNewLayout = false;
  }
  
  public final long measure(float paramFloat1, int paramInt1, float paramFloat2, int paramInt2)
  {
    if (isMeasureDefined()) {
      return mMeasureFunction.measure(this, paramFloat1, YogaMeasureMode.fromInt(paramInt1), paramFloat2, YogaMeasureMode.fromInt(paramInt2));
    }
    throw new RuntimeException("Measure function isn't defined!");
  }
  
  public void print()
  {
    YogaNative.jni_YGNodePrint(mNativePointer);
  }
  
  public YogaNodeJNIBase removeChildAt(int paramInt)
  {
    if (mChildren != null)
    {
      YogaNodeJNIBase localYogaNodeJNIBase = (YogaNodeJNIBase)mChildren.remove(paramInt);
      mOwner = null;
      YogaNative.jni_YGNodeRemoveChild(mNativePointer, mNativePointer);
      return localYogaNodeJNIBase;
    }
    throw new IllegalStateException("Trying to remove a child of a YogaNode that does not have children");
  }
  
  public void reset()
  {
    mMeasureFunction = null;
    mBaselineFunction = null;
    mData = null;
    mOnTextChangedListener = null;
    mHasNewLayout = true;
    mLayoutDirection = 0;
    YogaNative.jni_YGNodeReset(mNativePointer);
  }
  
  public void setAlignContent(YogaAlign paramYogaAlign)
  {
    YogaNative.jni_YGNodeStyleSetAlignContent(mNativePointer, paramYogaAlign.intValue());
  }
  
  public void setAlignItems(YogaAlign paramYogaAlign)
  {
    YogaNative.jni_YGNodeStyleSetAlignItems(mNativePointer, paramYogaAlign.intValue());
  }
  
  public void setAlignSelf(YogaAlign paramYogaAlign)
  {
    YogaNative.jni_YGNodeStyleSetAlignSelf(mNativePointer, paramYogaAlign.intValue());
  }
  
  public void setAspectRatio(float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetAspectRatio(mNativePointer, paramFloat);
  }
  
  public void setBaselineFunction(YogaBaselineFunction paramYogaBaselineFunction)
  {
    mBaselineFunction = paramYogaBaselineFunction;
    long l = mNativePointer;
    boolean bool;
    if (paramYogaBaselineFunction != null) {
      bool = true;
    } else {
      bool = false;
    }
    YogaNative.jni_YGNodeSetHasBaselineFunc(l, bool);
  }
  
  public void setBorder(YogaEdge paramYogaEdge, float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetBorder(mNativePointer, paramYogaEdge.intValue(), paramFloat);
  }
  
  public void setData(Object paramObject)
  {
    mData = paramObject;
  }
  
  public void setDirection(YogaDirection paramYogaDirection)
  {
    YogaNative.jni_YGNodeStyleSetDirection(mNativePointer, paramYogaDirection.intValue());
  }
  
  public void setDisplay(YogaDisplay paramYogaDisplay)
  {
    YogaNative.jni_YGNodeStyleSetDisplay(mNativePointer, paramYogaDisplay.intValue());
  }
  
  public void setFlex(float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetFlex(mNativePointer, paramFloat);
  }
  
  public void setFlexBasis(float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetFlexBasis(mNativePointer, paramFloat);
  }
  
  public void setFlexBasisAuto()
  {
    YogaNative.jni_YGNodeStyleSetFlexBasisAuto(mNativePointer);
  }
  
  public void setFlexBasisPercent(float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetFlexBasisPercent(mNativePointer, paramFloat);
  }
  
  public void setFlexDirection(YogaFlexDirection paramYogaFlexDirection)
  {
    YogaNative.jni_YGNodeStyleSetFlexDirection(mNativePointer, paramYogaFlexDirection.intValue());
  }
  
  public void setFlexGrow(float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetFlexGrow(mNativePointer, paramFloat);
  }
  
  public void setFlexShrink(float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetFlexShrink(mNativePointer, paramFloat);
  }
  
  public void setHeight(float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetHeight(mNativePointer, paramFloat);
  }
  
  public void setHeightAuto()
  {
    YogaNative.jni_YGNodeStyleSetHeightAuto(mNativePointer);
  }
  
  public void setHeightPercent(float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetHeightPercent(mNativePointer, paramFloat);
  }
  
  public void setIsReferenceBaseline(boolean paramBoolean)
  {
    YogaNative.jni_YGNodeSetIsReferenceBaseline(mNativePointer, paramBoolean);
  }
  
  public void setJustifyContent(YogaJustify paramYogaJustify)
  {
    YogaNative.jni_YGNodeStyleSetJustifyContent(mNativePointer, paramYogaJustify.intValue());
  }
  
  public void setMargin(YogaEdge paramYogaEdge, float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetMargin(mNativePointer, paramYogaEdge.intValue(), paramFloat);
  }
  
  public void setMarginAuto(YogaEdge paramYogaEdge)
  {
    YogaNative.jni_YGNodeStyleSetMarginAuto(mNativePointer, paramYogaEdge.intValue());
  }
  
  public void setMarginPercent(YogaEdge paramYogaEdge, float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetMarginPercent(mNativePointer, paramYogaEdge.intValue(), paramFloat);
  }
  
  public void setMaxHeight(float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetMaxHeight(mNativePointer, paramFloat);
  }
  
  public void setMaxHeightPercent(float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetMaxHeightPercent(mNativePointer, paramFloat);
  }
  
  public void setMaxWidth(float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetMaxWidth(mNativePointer, paramFloat);
  }
  
  public void setMaxWidthPercent(float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetMaxWidthPercent(mNativePointer, paramFloat);
  }
  
  public void setMeasureFunction(YogaMeasureFunction paramYogaMeasureFunction)
  {
    mMeasureFunction = paramYogaMeasureFunction;
    long l = mNativePointer;
    boolean bool;
    if (paramYogaMeasureFunction != null) {
      bool = true;
    } else {
      bool = false;
    }
    YogaNative.jni_YGNodeSetHasMeasureFunc(l, bool);
  }
  
  public void setMinHeight(float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetMinHeight(mNativePointer, paramFloat);
  }
  
  public void setMinHeightPercent(float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetMinHeightPercent(mNativePointer, paramFloat);
  }
  
  public void setMinWidth(float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetMinWidth(mNativePointer, paramFloat);
  }
  
  public void setMinWidthPercent(float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetMinWidthPercent(mNativePointer, paramFloat);
  }
  
  public void setOverflow(YogaOverflow paramYogaOverflow)
  {
    YogaNative.jni_YGNodeStyleSetOverflow(mNativePointer, paramYogaOverflow.intValue());
  }
  
  public void setPadding(YogaEdge paramYogaEdge, float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetPadding(mNativePointer, paramYogaEdge.intValue(), paramFloat);
  }
  
  public void setPaddingPercent(YogaEdge paramYogaEdge, float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetPaddingPercent(mNativePointer, paramYogaEdge.intValue(), paramFloat);
  }
  
  public void setPosition(YogaEdge paramYogaEdge, float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetPosition(mNativePointer, paramYogaEdge.intValue(), paramFloat);
  }
  
  public void setPositionPercent(YogaEdge paramYogaEdge, float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetPositionPercent(mNativePointer, paramYogaEdge.intValue(), paramFloat);
  }
  
  public void setPositionType(YogaPositionType paramYogaPositionType)
  {
    YogaNative.jni_YGNodeStyleSetPositionType(mNativePointer, paramYogaPositionType.intValue());
  }
  
  public void setStyleInputs(float[] paramArrayOfFloat, int paramInt)
  {
    YogaNative.jni_YGNodeSetStyleInputs(mNativePointer, paramArrayOfFloat, paramInt);
  }
  
  public void setWidth(float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetWidth(mNativePointer, paramFloat);
  }
  
  public void setWidthAuto()
  {
    YogaNative.jni_YGNodeStyleSetWidthAuto(mNativePointer);
  }
  
  public void setWidthPercent(float paramFloat)
  {
    YogaNative.jni_YGNodeStyleSetWidthPercent(mNativePointer, paramFloat);
  }
  
  public void setWrap(YogaWrap paramYogaWrap)
  {
    YogaNative.jni_YGNodeStyleSetFlexWrap(mNativePointer, paramYogaWrap.intValue());
  }
}
