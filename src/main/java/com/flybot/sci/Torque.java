package com.flybot.sci;

import java.util.Arrays;
import java.util.List;

public class Torque extends Science
{
  public static final double sNM_PER_OZIN = 0.00706155;
  
  public static final Units
  NEWTON_METER = new Units()
  {
    public String getLabel() { return "N-m"; }
    public double getSiConversion() { return 1.0; }
  },
  OUNCE_INCH = new Units()
  {
    public String getLabel() { return "oz-in"; }
    public double getSiConversion() { return sNM_PER_OZIN; }
  };
  
  public Torque(Units pUnits, double pValue)
  {
    super(pUnits, pValue);
  }
  
  public Torque()
  {
    this(NEWTON_METER, 0d);
  }

  @Override
  protected List<Units> getUnits()
  {
    return Arrays.asList(NEWTON_METER, OUNCE_INCH);
  }
}
