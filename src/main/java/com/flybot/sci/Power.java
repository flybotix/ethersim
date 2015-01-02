package com.flybot.sci;

import java.util.Arrays;
import java.util.List;

public class Power extends Science
{
  public static final Units WATT = new Units()
  {
    public String getLabel() { return "w"; }
    public double getSiConversion() { return 1.0; }
  };
  
  public Power(double pWatts)
  {
    super(WATT, pWatts);
  }
  
  public static Power from(Voltage pVolts, Current pCurrent)
  {
    return new Power(pVolts.si() * pCurrent.si());
  }
  
  public static Power from(Torque pTorque, AngularVelocity pVelocity)
  {
    return new Power(pTorque.si() * pVelocity.si());
  }

  @Override
  protected List<Units> getUnits()
  {
    return Arrays.asList(WATT);
  }

}
