package com.google.android.exoplayer2.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SlidingPercentile
{
  private static final Comparator<Sample> INDEX_COMPARATOR = -..Lambda.SlidingPercentile.IHMSNRVWSvKImU2XQD2j4ISb4-U.INSTANCE;
  private static final int MAX_RECYCLED_SAMPLES = 5;
  private static final int SORT_ORDER_BY_INDEX = 1;
  private static final int SORT_ORDER_BY_VALUE = 0;
  private static final int SORT_ORDER_NONE = -1;
  private static final Comparator<Sample> VALUE_COMPARATOR = -..Lambda.SlidingPercentile.UufTq1Ma5g1qQu0Vqc6f2CE68bE.INSTANCE;
  private int currentSortOrder;
  private final int maxWeight;
  private int nextSampleIndex;
  private int recycledSampleCount;
  private final Sample[] recycledSamples;
  private final ArrayList<Sample> samples;
  private int totalWeight;
  
  public SlidingPercentile(int paramInt)
  {
    maxWeight = paramInt;
    recycledSamples = new Sample[5];
    samples = new ArrayList();
    currentSortOrder = -1;
  }
  
  private void ensureSortedByIndex()
  {
    if (currentSortOrder != 1)
    {
      Collections.sort(samples, INDEX_COMPARATOR);
      currentSortOrder = 1;
    }
  }
  
  private void ensureSortedByValue()
  {
    if (currentSortOrder != 0)
    {
      Collections.sort(samples, VALUE_COMPARATOR);
      currentSortOrder = 0;
    }
  }
  
  public void addSample(int paramInt, float paramFloat)
  {
    ensureSortedByIndex();
    Object localObject;
    if (recycledSampleCount > 0)
    {
      localObject = recycledSamples;
      i = recycledSampleCount - 1;
      recycledSampleCount = i;
      localObject = localObject[i];
    }
    else
    {
      localObject = new Sample(null);
    }
    int i = nextSampleIndex;
    nextSampleIndex = (i + 1);
    index = i;
    weight = paramInt;
    value = paramFloat;
    samples.add(localObject);
    totalWeight += paramInt;
    while (totalWeight > maxWeight)
    {
      paramInt = totalWeight - maxWeight;
      localObject = (Sample)samples.get(0);
      if (weight <= paramInt)
      {
        totalWeight -= weight;
        samples.remove(0);
        if (recycledSampleCount < 5)
        {
          Sample[] arrayOfSample = recycledSamples;
          paramInt = recycledSampleCount;
          recycledSampleCount = (paramInt + 1);
          arrayOfSample[paramInt] = localObject;
        }
      }
      else
      {
        weight -= paramInt;
        totalWeight -= paramInt;
      }
    }
  }
  
  public float getPercentile(float paramFloat)
  {
    ensureSortedByValue();
    float f = totalWeight;
    int i = 0;
    int j = 0;
    while (i < samples.size())
    {
      Sample localSample = (Sample)samples.get(i);
      j += weight;
      if (j >= paramFloat * f) {
        return value;
      }
      i += 1;
    }
    if (samples.isEmpty()) {
      return NaN.0F;
    }
    return samples.get(samples.size() - 1)).value;
  }
  
  private static class Sample
  {
    public int index;
    public float value;
    public int weight;
    
    private Sample() {}
  }
}
