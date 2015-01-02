package com.flybot.sci;

import java.util.Arrays;
import java.util.List;

public class Current extends Science
{
  public static final Units AMP = new Units()
  {
    public String getLabel() { return "A"; }
    public double getSiConversion() { return 1.0; }
  };
  
  public Current(double pAmps)
  {
    super(AMP, pAmps);
  }
  
  public Current()
  {
    this(0d);
  }

  @Override
  protected List<Units> getUnits()
  {
    return Arrays.asList(AMP);
  }
}
