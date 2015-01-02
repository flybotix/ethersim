package com.flybot.apps.simplecalc;

import java.text.DecimalFormat;

import com.flybot.util.lang.IDisplayable;

public enum EOutput implements IDisplayable
{
   DRIVE_TRAIN_FREE_SPD(new DecimalFormat("0.00 ft/s"), "Drive Train Free-Speed"),
   DRIVE_TRAIN_ADJ_SPD(new DecimalFormat("0.00 ft/s"), "Drive Train Adjusted Speed"),
   PUSHING_MATCH_CURRENT_PER_MOTOR(new DecimalFormat("0.00 A"), "Pushing Match Current Per Motor"),
   GEAR_RATIO(new DecimalFormat("0.00 : 1"), "Overall Gear Ratio");
   
   public DecimalFormat mFormat;
   public String mLabel;
   
   private EOutput(DecimalFormat pFormat, String pLabel)
   {
      mFormat = pFormat;
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