package com.flybot.sci;

import java.util.Arrays;
import java.util.List;


public class Acceleration extends Science
{
  public static final Units
  FEET_PER_SECOND2 = new Units()
  {
    public String getLabel() { return "ft/s^2"; }
    public double getSiConversion() { return Distance.INCHES_PER_FOOT * Distance.METERS_PER_INCH; }
  },
  METER_PER_SECOND2 = new Units()
  {
    public String getLabel() { return "m/s^2"; }
    public double getSiConversion() { return 1d; }
  };
  
  public Acceleration(Units pUnits, double pValue)
  {
    super(pUnits, pValue);
  }

  public Acceleration(Acceleration pCopy)
  {
    this(pCopy.mPreferredUnits, pCopy.si());
  }
  
  public Acceleration()
  {
    this(METER_PER_SECOND2, 0d);
  }

  @Override
  protected List<Units> getUnits()
  {
    return Arrays.asList(METER_PER_SECOND2, FEET_PER_SECOND2);
  }
}
