package com.flybot.sci;

import java.util.Arrays;
import java.util.List;

public class Speed extends Science
{
  public static final Units
  MILE_PER_HOUR = new Units()
  {
    public String getLabel() { return "mph"; }
    public double getSiConversion() { return Distance.FEET_PER_MILE
        / Time.MINUTES_PER_HOUR / Time.SECONDS_PER_MINUTE; }
  },
  METER_PER_SECOND = new Units()
  {
    public String getLabel() { return "m/s"; }
    public double getSiConversion() { return 1.0; }
  },
  FEET_PER_SECOND = new Units()
  {
    public String getLabel() { return "ft/s"; }
    public double getSiConversion() { return Distance.INCHES_PER_FOOT * Distance.METERS_PER_INCH; }
  };
  
  public Speed(Units pUnits, double pValue)
  {
    super(pUnits, pValue);
  }

  public Speed(Speed pCopy)
  {
    this(pCopy.mPreferredUnits, pCopy.si());
  }
  
  public Speed()
  {
    this(METER_PER_SECOND, 0d);
  }

  @Override
  protected List<Units> getUnits()
  {
    return Arrays.asList(METER_PER_SECOND, FEET_PER_SECOND, MILE_PER_HOUR);
  }
}
