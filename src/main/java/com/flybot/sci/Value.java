package com.flybot.sci;

import java.util.Arrays;
import java.util.List;

public class Value extends Science
{
  private final Units mUnits;
  public Value(String pUnits, double pValue)
  {
    super(new Units(){
      public String getLabel() { return pUnits; }
      public double getSiConversion() { return 1d; }}, 
    pValue);
    mUnits = getPreferredUnits();
  }
  
  public Value(double pValue)
  {
    super(NO_UNITS, pValue);
    mUnits = null;
  }
  
  public Value()
  {
    super(NO_UNITS, 0d);
    mUnits = null;
  }

  @Override
  protected List<Units> getUnits()
  {
    return Arrays.asList(NO_UNITS, mUnits);
  }

}
