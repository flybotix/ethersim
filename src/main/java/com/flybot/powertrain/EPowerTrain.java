package com.flybot.powertrain;

import com.flybot.util.lang.IDisplayable;

public enum EPowerTrain implements IDisplayable
{
   NUM_MOTORS_PER_GEARBOX("",2d, "# of Motors per Gearbox"),
   NUM_GEARBOXES_IN_DT("",2d, "# of Gearboxes in Drive Train"),
   SPEED_LOSS_CONSTANT("%",81d, "Speed Loss Constant"),
   DRIVETRAIN_EFFICIENCY("%",90d, "Drivetrain Efficiency"),
   ROBOT_WEIGHT("lbs",(154), "Total Robot Weight"),
   PCT_WEIGHT_ON_DRV_WHL("%",100d, "% of Weight on Drive Wheels");
   
   public String mUnits;
   public double mDefault;
   public String mLabel;
   
   public String toString(){ return mLabel; }
   
   private EPowerTrain(String pUnits, double pDefault, String pLabel)
   {
      mUnits = pUnits;
      mDefault = pDefault;
      mLabel = pLabel;
   }

  @Override
  public boolean isDisplayed()
  {
    return true;
  }
  
  @Override
  public String getLabel()
  {
     return mLabel;
  }
}