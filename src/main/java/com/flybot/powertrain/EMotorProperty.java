package com.flybot.powertrain;

import com.flybot.util.lang.IDisplayable;

public enum EMotorProperty implements IDisplayable
{
   NAME("", -1, "Motor Name"),
   VOLTAGE("V", 12d, "Rated Voltage"),
   FREE_SPEED("RPM", 5310d, "Free Speed"),
   STALL_TORQUE("N-m", 2.43, "Stall Torque"),
   FREE_CURRENT("A", 2.7, "Free Current"),
   STALL_CURRENT("A", 133d, "Stall Current");
   
   public String mUnits;
   public double mDefault;
   public String mLabel;
   
   public String toString() { return mLabel; }
   
   private EMotorProperty(String pUnits, double pDefault, String pLabel)
   {
      mUnits = pUnits;
      mDefault = pDefault;
      mLabel = pLabel;
   }

  @Override
  public boolean isDisplayed()
  {
    return (this != NAME);
  }
  
  @Override
  public String getLabel()
  {
     return mLabel;
  }
}