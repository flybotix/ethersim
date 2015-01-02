package com.flybot.sci;

import java.util.Arrays;
import java.util.List;

public class Distance extends Science
{
  public static final double
    MILLIMETERS_PER_METER = 1000,
    METERS_PER_INCH = 0.0254,
    INCHES_PER_FOOT = 12,
    FEET_PER_MILE = 5280;
  
  public static final Units
  METER = new Units()
  {
    public String getLabel() { return "m"; }
    public double getSiConversion() { return 1.0; }
  },
  MILLIMETER = new Units()
  {
    public String getLabel() { return "mm"; }
    public double getSiConversion() { return 1d/MILLIMETERS_PER_METER; }
  },
  INCH = new Units()
  {
    public String getLabel() { return "in"; }
    public double getSiConversion() { return METERS_PER_INCH; }
  },
  FEET = new Units()
  {
    public String getLabel() { return "ft"; }
    public double getSiConversion() { return INCHES_PER_FOOT * METERS_PER_INCH; }
  },
  MILE = new Units()
  {
    public String getLabel() { return "mi"; }
    public double getSiConversion() { return FEET.getSiConversion() * FEET_PER_MILE; }
  };
  
  public Distance(Units pUnits, double pValue)
  {
    super(pUnits, pValue);
  }

  public Distance(Distance pCopy)
  {
    this(pCopy.mPreferredUnits, pCopy.si());
  }
  
  public Distance()
  {
    this(METER, 0d);
  }

  @Override
  protected List<Units> getUnits()
  {
    return Arrays.asList(METER, MILLIMETER, INCH, FEET, MILE);
  }
}
