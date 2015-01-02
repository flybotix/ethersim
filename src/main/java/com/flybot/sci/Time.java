package com.flybot.sci;

import java.util.Arrays;
import java.util.List;

public class Time extends Science
{
  public static final double SECONDS_PER_MINUTE = 60d,
      MINUTES_PER_HOUR = 60d;
  
  public static final Units
  SECOND = new Units()
  {
    public String getLabel() { return "s"; }
    public double getSiConversion() { return 1.0; }
  },
  MINUTE = new Units()
  {
    public String getLabel() { return "min"; }
    public double getSiConversion() { return SECONDS_PER_MINUTE; }
  },
  HOUR = new Units()
  {
    public String getLabel() { return "hr"; }
    public double getSiConversion() { return MINUTES_PER_HOUR*SECONDS_PER_MINUTE; }
  };
  
  public Time(Units pUnits, double pSeconds)
  {
    super(pUnits, pSeconds);
  }
  
  public Time()
  {
    this(SECOND, 0d);
  }

  @Override
  protected List<Units> getUnits()
  {
    return Arrays.asList(SECOND, MINUTE, HOUR);
  }
}
