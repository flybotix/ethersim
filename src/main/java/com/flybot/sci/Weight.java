package com.flybot.sci;

import java.util.Arrays;
import java.util.List;

public class Weight extends Mass
{
  public static final double sGRAVITY_ACCEL_EARTH = 9.80665;
  
  public Weight(Mass pMass)
  {
    super(Force.NEWTON, pMass.si() * sGRAVITY_ACCEL_EARTH);
  }
  
  @Override
  protected List<Units> getUnits()
  {
    return Arrays.asList(Force.NEWTON);
  }

}
