package com.flybot.sci;

import java.util.Arrays;
import java.util.List;

public class AngularVelocity extends Science
{
  public static final double RADIANS_PER_ROTATION = Math.PI * 2d;
  
  public static final Units
  RAD_PER_SEC = new Units()
  {
    public String getLabel() { return "rad/s"; }
    public double getSiConversion() { return 1.0; }
  },
  RPM = new Units()
  {
    public String getLabel() { return "rpm"; }
    public double getSiConversion() { return RADIANS_PER_ROTATION / Time.SECONDS_PER_MINUTE; }
  };
  
  public AngularVelocity(Units pUnits, double pValue)
  {
    super(pUnits, pValue);
  }
  
  public AngularVelocity()
  {
    this(RAD_PER_SEC, 0d);
  }

  @Override
  protected List<Units> getUnits()
  {
    return Arrays.asList(RAD_PER_SEC, RPM);
  }
}
