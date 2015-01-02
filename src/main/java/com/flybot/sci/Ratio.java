package com.flybot.sci;

import java.util.Arrays;
import java.util.List;

public class Ratio extends Science
{
  public static final Units
  PCT = new Units()
  {
    public String getLabel() { return "%"; }
    public double getSiConversion() { return 1.0; }
  },
  RATIO = new Units()
  {
    public String getLabel() { return ": 1"; }
    public double getSiConversion() { return 1.0; }
  };

  public Ratio(Units pUnits, double pValue)
  {
    super(pUnits, pValue);
  }
  
  public Ratio()
  {
    this(PCT, 1d);
  }

  @Override
  protected List<Units> getUnits()
  {
    return Arrays.asList(PCT, RATIO);
  }

}
