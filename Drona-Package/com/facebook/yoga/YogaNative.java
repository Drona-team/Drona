package com.facebook.yoga;

import com.facebook.proguard.annotations.DoNotStrip;
import com.facebook.soloader.SoLoader;

@DoNotStrip
public class YogaNative
{
  static
  {
    SoLoader.loadLibrary("yoga");
  }
  
  public YogaNative() {}
  
  static native void jni_YGConfigFree(long paramLong);
  
  static native long jni_YGConfigNew();
  
  static native void jni_YGConfigSetExperimentalFeatureEnabled(long paramLong, int paramInt, boolean paramBoolean);
  
  static native void jni_YGConfigSetLogger(long paramLong, Object paramObject);
  
  static native void jni_YGConfigSetPointScaleFactor(long paramLong, float paramFloat);
  
  static native void jni_YGConfigSetPrintTreeFlag(long paramLong, boolean paramBoolean);
  
  static native void jni_YGConfigSetShouldDiffLayoutWithoutLegacyStretchBehaviour(long paramLong, boolean paramBoolean);
  
  static native void jni_YGConfigSetUseLegacyStretchBehaviour(long paramLong, boolean paramBoolean);
  
  static native void jni_YGConfigSetUseWebDefaults(long paramLong, boolean paramBoolean);
  
  static native void jni_YGNodeCalculateLayout(long paramLong, float paramFloat1, float paramFloat2, long[] paramArrayOfLong, YogaNodeJNIBase[] paramArrayOfYogaNodeJNIBase);
  
  static native void jni_YGNodeClearChildren(long paramLong);
  
  static native long jni_YGNodeClone(long paramLong);
  
  static native void jni_YGNodeCopyStyle(long paramLong1, long paramLong2);
  
  static native void jni_YGNodeFree(long paramLong);
  
  static native void jni_YGNodeInsertChild(long paramLong1, long paramLong2, int paramInt);
  
  static native boolean jni_YGNodeIsDirty(long paramLong);
  
  static native boolean jni_YGNodeIsReferenceBaseline(long paramLong);
  
  static native void jni_YGNodeMarkDirty(long paramLong);
  
  static native void jni_YGNodeMarkDirtyAndPropogateToDescendants(long paramLong);
  
  static native long jni_YGNodeNew();
  
  static native long jni_YGNodeNewWithConfig(long paramLong);
  
  static native void jni_YGNodePrint(long paramLong);
  
  static native void jni_YGNodeRemoveChild(long paramLong1, long paramLong2);
  
  static native void jni_YGNodeReset(long paramLong);
  
  static native void jni_YGNodeSetHasBaselineFunc(long paramLong, boolean paramBoolean);
  
  static native void jni_YGNodeSetHasMeasureFunc(long paramLong, boolean paramBoolean);
  
  static native void jni_YGNodeSetIsReferenceBaseline(long paramLong, boolean paramBoolean);
  
  static native void jni_YGNodeSetStyleInputs(long paramLong, float[] paramArrayOfFloat, int paramInt);
  
  static native int jni_YGNodeStyleGetAlignContent(long paramLong);
  
  static native int jni_YGNodeStyleGetAlignItems(long paramLong);
  
  static native int jni_YGNodeStyleGetAlignSelf(long paramLong);
  
  static native float jni_YGNodeStyleGetAspectRatio(long paramLong);
  
  static native float jni_YGNodeStyleGetBorder(long paramLong, int paramInt);
  
  static native int jni_YGNodeStyleGetDirection(long paramLong);
  
  static native int jni_YGNodeStyleGetDisplay(long paramLong);
  
  static native float jni_YGNodeStyleGetFlex(long paramLong);
  
  static native long jni_YGNodeStyleGetFlexBasis(long paramLong);
  
  static native int jni_YGNodeStyleGetFlexDirection(long paramLong);
  
  static native float jni_YGNodeStyleGetFlexGrow(long paramLong);
  
  static native float jni_YGNodeStyleGetFlexShrink(long paramLong);
  
  static native int jni_YGNodeStyleGetFlexWrap(long paramLong);
  
  static native long jni_YGNodeStyleGetHeight(long paramLong);
  
  static native int jni_YGNodeStyleGetJustifyContent(long paramLong);
  
  static native long jni_YGNodeStyleGetMargin(long paramLong, int paramInt);
  
  static native long jni_YGNodeStyleGetMaxHeight(long paramLong);
  
  static native long jni_YGNodeStyleGetMaxWidth(long paramLong);
  
  static native long jni_YGNodeStyleGetMinHeight(long paramLong);
  
  static native long jni_YGNodeStyleGetMinWidth(long paramLong);
  
  static native int jni_YGNodeStyleGetOverflow(long paramLong);
  
  static native long jni_YGNodeStyleGetPadding(long paramLong, int paramInt);
  
  static native long jni_YGNodeStyleGetPosition(long paramLong, int paramInt);
  
  static native int jni_YGNodeStyleGetPositionType(long paramLong);
  
  static native long jni_YGNodeStyleGetWidth(long paramLong);
  
  static native void jni_YGNodeStyleSetAlignContent(long paramLong, int paramInt);
  
  static native void jni_YGNodeStyleSetAlignItems(long paramLong, int paramInt);
  
  static native void jni_YGNodeStyleSetAlignSelf(long paramLong, int paramInt);
  
  static native void jni_YGNodeStyleSetAspectRatio(long paramLong, float paramFloat);
  
  static native void jni_YGNodeStyleSetBorder(long paramLong, int paramInt, float paramFloat);
  
  static native void jni_YGNodeStyleSetDirection(long paramLong, int paramInt);
  
  static native void jni_YGNodeStyleSetDisplay(long paramLong, int paramInt);
  
  static native void jni_YGNodeStyleSetFlex(long paramLong, float paramFloat);
  
  static native void jni_YGNodeStyleSetFlexBasis(long paramLong, float paramFloat);
  
  static native void jni_YGNodeStyleSetFlexBasisAuto(long paramLong);
  
  static native void jni_YGNodeStyleSetFlexBasisPercent(long paramLong, float paramFloat);
  
  static native void jni_YGNodeStyleSetFlexDirection(long paramLong, int paramInt);
  
  static native void jni_YGNodeStyleSetFlexGrow(long paramLong, float paramFloat);
  
  static native void jni_YGNodeStyleSetFlexShrink(long paramLong, float paramFloat);
  
  static native void jni_YGNodeStyleSetFlexWrap(long paramLong, int paramInt);
  
  static native void jni_YGNodeStyleSetHeight(long paramLong, float paramFloat);
  
  static native void jni_YGNodeStyleSetHeightAuto(long paramLong);
  
  static native void jni_YGNodeStyleSetHeightPercent(long paramLong, float paramFloat);
  
  static native void jni_YGNodeStyleSetJustifyContent(long paramLong, int paramInt);
  
  static native void jni_YGNodeStyleSetMargin(long paramLong, int paramInt, float paramFloat);
  
  static native void jni_YGNodeStyleSetMarginAuto(long paramLong, int paramInt);
  
  static native void jni_YGNodeStyleSetMarginPercent(long paramLong, int paramInt, float paramFloat);
  
  static native void jni_YGNodeStyleSetMaxHeight(long paramLong, float paramFloat);
  
  static native void jni_YGNodeStyleSetMaxHeightPercent(long paramLong, float paramFloat);
  
  static native void jni_YGNodeStyleSetMaxWidth(long paramLong, float paramFloat);
  
  static native void jni_YGNodeStyleSetMaxWidthPercent(long paramLong, float paramFloat);
  
  static native void jni_YGNodeStyleSetMinHeight(long paramLong, float paramFloat);
  
  static native void jni_YGNodeStyleSetMinHeightPercent(long paramLong, float paramFloat);
  
  static native void jni_YGNodeStyleSetMinWidth(long paramLong, float paramFloat);
  
  static native void jni_YGNodeStyleSetMinWidthPercent(long paramLong, float paramFloat);
  
  static native void jni_YGNodeStyleSetOverflow(long paramLong, int paramInt);
  
  static native void jni_YGNodeStyleSetPadding(long paramLong, int paramInt, float paramFloat);
  
  static native void jni_YGNodeStyleSetPaddingPercent(long paramLong, int paramInt, float paramFloat);
  
  static native void jni_YGNodeStyleSetPosition(long paramLong, int paramInt, float paramFloat);
  
  static native void jni_YGNodeStyleSetPositionPercent(long paramLong, int paramInt, float paramFloat);
  
  static native void jni_YGNodeStyleSetPositionType(long paramLong, int paramInt);
  
  static native void jni_YGNodeStyleSetWidth(long paramLong, float paramFloat);
  
  static native void jni_YGNodeStyleSetWidthAuto(long paramLong);
  
  static native void jni_YGNodeStyleSetWidthPercent(long paramLong, float paramFloat);
}
