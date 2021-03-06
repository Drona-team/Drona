package com.google.android.exoplayer2.pc;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.Parameters;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.ParametersBuilder;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.SelectionOverride;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo;
import com.google.android.exoplayer2.util.Assertions;
import java.util.Arrays;

public class TrackSelectionView
  extends LinearLayout
{
  private boolean allowAdaptiveSelections;
  private final ComponentListener componentListener;
  private final CheckedTextView defaultView;
  private final CheckedTextView disableView;
  private final LayoutInflater inflater;
  private boolean isDisabled;
  @Nullable
  private DefaultTrackSelector.SelectionOverride override;
  private int rendererIndex;
  private final int selectableItemBackgroundResourceId;
  private TrackGroupArray trackGroups;
  private TrackNameProvider trackNameProvider;
  private DefaultTrackSelector trackSelector;
  private CheckedTextView[][] trackViews;
  
  public TrackSelectionView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public TrackSelectionView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public TrackSelectionView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    paramAttributeSet = paramContext.getTheme().obtainStyledAttributes(new int[] { 16843534 });
    selectableItemBackgroundResourceId = paramAttributeSet.getResourceId(0, 0);
    paramAttributeSet.recycle();
    inflater = LayoutInflater.from(paramContext);
    componentListener = new ComponentListener(null);
    trackNameProvider = new DefaultTrackNameProvider(getResources());
    disableView = ((CheckedTextView)inflater.inflate(17367055, this, false));
    disableView.setBackgroundResource(selectableItemBackgroundResourceId);
    disableView.setText(R.string.exo_track_selection_none);
    disableView.setEnabled(false);
    disableView.setFocusable(true);
    disableView.setOnClickListener(componentListener);
    disableView.setVisibility(8);
    addView(disableView);
    addView(inflater.inflate(R.layout.exo_list_divider, this, false));
    defaultView = ((CheckedTextView)inflater.inflate(17367055, this, false));
    defaultView.setBackgroundResource(selectableItemBackgroundResourceId);
    defaultView.setText(R.string.exo_track_selection_auto);
    defaultView.setEnabled(false);
    defaultView.setFocusable(true);
    defaultView.setOnClickListener(componentListener);
    addView(defaultView);
  }
  
  private void applySelection()
  {
    DefaultTrackSelector.ParametersBuilder localParametersBuilder = trackSelector.buildUponParameters();
    localParametersBuilder.setRendererDisabled(rendererIndex, isDisabled);
    if (override != null) {
      localParametersBuilder.setSelectionOverride(rendererIndex, trackGroups, override);
    } else {
      localParametersBuilder.clearSelectionOverrides(rendererIndex);
    }
    trackSelector.setParameters(localParametersBuilder);
  }
  
  public static Pair getDialog(Activity paramActivity, CharSequence paramCharSequence, DefaultTrackSelector paramDefaultTrackSelector, int paramInt)
  {
    paramActivity = new AlertDialog.Builder(paramActivity);
    View localView = LayoutInflater.from(paramActivity.getContext()).inflate(R.layout.exo_track_selection_dialog, null);
    TrackSelectionView localTrackSelectionView = (TrackSelectionView)localView.findViewById(R.id.exo_track_selection_view);
    localTrackSelectionView.init(paramDefaultTrackSelector, paramInt);
    paramDefaultTrackSelector = new -..Lambda.TrackSelectionView.Fni8J5PCSkla_I2ocA1wVoa_7Zg(localTrackSelectionView);
    return Pair.create(paramActivity.setTitle(paramCharSequence).setView(localView).setPositiveButton(17039370, paramDefaultTrackSelector).setNegativeButton(17039360, null).create(), localTrackSelectionView);
  }
  
  private static int[] getTracksAdding(int[] paramArrayOfInt, int paramInt)
  {
    paramArrayOfInt = Arrays.copyOf(paramArrayOfInt, paramArrayOfInt.length + 1);
    paramArrayOfInt[(paramArrayOfInt.length - 1)] = paramInt;
    return paramArrayOfInt;
  }
  
  private static int[] getTracksRemoving(int[] paramArrayOfInt, int paramInt)
  {
    int[] arrayOfInt = new int[paramArrayOfInt.length - 1];
    int m = paramArrayOfInt.length;
    int i = 0;
    int k;
    for (int j = 0; i < m; j = k)
    {
      int n = paramArrayOfInt[i];
      k = j;
      if (n != paramInt)
      {
        arrayOfInt[j] = n;
        k = j + 1;
      }
      i += 1;
    }
    return arrayOfInt;
  }
  
  private void onClick(View paramView)
  {
    if (paramView == disableView) {
      onDisableViewClicked();
    } else if (paramView == defaultView) {
      onDefaultViewClicked();
    } else {
      onTrackViewClicked(paramView);
    }
    updateViewStates();
  }
  
  private void onDefaultViewClicked()
  {
    isDisabled = false;
    override = null;
  }
  
  private void onDisableViewClicked()
  {
    isDisabled = true;
    override = null;
  }
  
  private void onTrackViewClicked(View paramView)
  {
    isDisabled = false;
    Object localObject = (Pair)paramView.getTag();
    int i = ((Integer)first).intValue();
    int j = ((Integer)second).intValue();
    if ((override != null) && (override.groupIndex == i) && (allowAdaptiveSelections))
    {
      int k = override.length;
      localObject = override.tracks;
      if (((CheckedTextView)paramView).isChecked())
      {
        if (k == 1)
        {
          override = null;
          isDisabled = true;
          return;
        }
        override = new DefaultTrackSelector.SelectionOverride(i, getTracksRemoving((int[])localObject, j));
        return;
      }
      override = new DefaultTrackSelector.SelectionOverride(i, getTracksAdding((int[])localObject, j));
      return;
    }
    override = new DefaultTrackSelector.SelectionOverride(i, new int[] { j });
  }
  
  private void updateViewStates()
  {
    disableView.setChecked(isDisabled);
    CheckedTextView localCheckedTextView = defaultView;
    boolean bool;
    if ((!isDisabled) && (override == null)) {
      bool = true;
    } else {
      bool = false;
    }
    localCheckedTextView.setChecked(bool);
    int i = 0;
    while (i < trackViews.length)
    {
      int j = 0;
      while (j < trackViews[i].length)
      {
        localCheckedTextView = trackViews[i][j];
        if ((override != null) && (override.groupIndex == i) && (override.containsTrack(j))) {
          bool = true;
        } else {
          bool = false;
        }
        localCheckedTextView.setChecked(bool);
        j += 1;
      }
      i += 1;
    }
  }
  
  private void updateViews()
  {
    int i = getChildCount() - 1;
    while (i >= 3)
    {
      removeViewAt(i);
      i -= 1;
    }
    MappingTrackSelector.MappedTrackInfo localMappedTrackInfo;
    if (trackSelector == null) {
      localMappedTrackInfo = null;
    } else {
      localMappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
    }
    if ((trackSelector != null) && (localMappedTrackInfo != null))
    {
      disableView.setEnabled(true);
      defaultView.setEnabled(true);
      trackGroups = localMappedTrackInfo.getTrackGroups(rendererIndex);
      Object localObject = trackSelector.getParameters();
      isDisabled = ((DefaultTrackSelector.Parameters)localObject).getRendererDisabled(rendererIndex);
      override = ((DefaultTrackSelector.Parameters)localObject).getSelectionOverride(rendererIndex, trackGroups);
      trackViews = new CheckedTextView[trackGroups.length][];
      i = 0;
      while (i < trackGroups.length)
      {
        localObject = trackGroups.context(i);
        int j;
        if ((allowAdaptiveSelections) && (trackGroups.context(i).length > 1) && (localMappedTrackInfo.getAdaptiveSupport(rendererIndex, i, false) != 0)) {
          j = 1;
        } else {
          j = 0;
        }
        trackViews[i] = new CheckedTextView[length];
        int k = 0;
        while (k < length)
        {
          if (k == 0) {
            addView(inflater.inflate(R.layout.exo_list_divider, this, false));
          }
          int m;
          if (j != 0) {
            m = 17367056;
          } else {
            m = 17367055;
          }
          CheckedTextView localCheckedTextView = (CheckedTextView)inflater.inflate(m, this, false);
          localCheckedTextView.setBackgroundResource(selectableItemBackgroundResourceId);
          localCheckedTextView.setText(trackNameProvider.getTrackName(((TrackGroup)localObject).getFormat(k)));
          if (localMappedTrackInfo.getTrackSupport(rendererIndex, i, k) == 4)
          {
            localCheckedTextView.setFocusable(true);
            localCheckedTextView.setTag(Pair.create(Integer.valueOf(i), Integer.valueOf(k)));
            localCheckedTextView.setOnClickListener(componentListener);
          }
          else
          {
            localCheckedTextView.setFocusable(false);
            localCheckedTextView.setEnabled(false);
          }
          trackViews[i][k] = localCheckedTextView;
          addView(localCheckedTextView);
          k += 1;
        }
        i += 1;
      }
      updateViewStates();
      return;
    }
    disableView.setEnabled(false);
    defaultView.setEnabled(false);
  }
  
  public void init(DefaultTrackSelector paramDefaultTrackSelector, int paramInt)
  {
    trackSelector = paramDefaultTrackSelector;
    rendererIndex = paramInt;
    updateViews();
  }
  
  public void setAllowAdaptiveSelections(boolean paramBoolean)
  {
    if (allowAdaptiveSelections != paramBoolean)
    {
      allowAdaptiveSelections = paramBoolean;
      updateViews();
    }
  }
  
  public void setShowDisableOption(boolean paramBoolean)
  {
    CheckedTextView localCheckedTextView = disableView;
    int i;
    if (paramBoolean) {
      i = 0;
    } else {
      i = 8;
    }
    localCheckedTextView.setVisibility(i);
  }
  
  public void setTrackNameProvider(TrackNameProvider paramTrackNameProvider)
  {
    trackNameProvider = ((TrackNameProvider)Assertions.checkNotNull(paramTrackNameProvider));
    updateViews();
  }
  
  class ComponentListener
    implements View.OnClickListener
  {
    private ComponentListener() {}
    
    public void onClick(View paramView)
    {
      TrackSelectionView.this.onClick(paramView);
    }
  }
}
