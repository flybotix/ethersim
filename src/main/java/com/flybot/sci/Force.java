package com.flybot.sci;

import java.util.Arrays;
import java.util.List;

public class Force extends Science
{
  public static final double sNEWTON_PER_LBF = 4.448222;
  
  public static final Units
  NEWTON = new Units()
  {
    public String getLabel() { return "N"; }
    public double getSiConversion() { return 1.0; }
  },
  LBF = new Units()
  {
    public String getLabel() { return "lbf"; }
    public double getSiConversion() { return sNEWTON_PER_LBF; }
  };
  
  public Force(Units pUnits, double pValue)
  {
    super(pUnits, pValue);
  }
  
  public Force()
  {
    this(NEWTON, 0d);
  }

  @Override
  protected List<Units> getUnits()
  {
    return Arrays.asList(NEWTON, LBF);
  }
}
