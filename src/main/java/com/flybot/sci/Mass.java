package com.flybot.sci;

import java.util.Arrays;
import java.util.List;

public class Mass extends Science
{
  public static final double sKG_PER_LBM = 0.4535924;
  
  public static final Units
  LBM = new Units()
  {
    public String getLabel() { return "Lbs-Mass"; }
    public double getSiConversion() { return sKG_PER_LBM; }
  },
  KILOGRAMS = new Units()
  {
    public String getLabel() { return "Kg"; }
    public double getSiConversion() { return 1.0; }
  };
  
  public Mass(Units pUnits, double pValue)
  {
    super(pUnits, pValue);
  }
  
  public Mass()
  {
    this(KILOGRAMS, 0d);
  }

  @Override
  protected List<Units> getUnits()
  {
    return Arrays.asList(LBM, KILOGRAMS);
  }
}
