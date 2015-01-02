package com.flybot.apps.ethersim;

import com.flybot.sci.Acceleration;
import com.flybot.sci.Current;
import com.flybot.sci.Distance;
import com.flybot.sci.Science;
import com.flybot.sci.Speed;
import com.flybot.sci.Units;
import com.flybot.sci.Voltage;
import com.flybot.util.lang.IDisplayable;

public enum EResults implements IDisplayable
{
  Robot_Travel(Distance.FEET),
  Robot_Speed(Speed.FEET_PER_SECOND),
  Robot_Acceleration(Acceleration.FEET_PER_SECOND2),
  Current_Per_Motor(Current.AMP),
  Voltage_At_Motor(Voltage.VOLT),
  Is_Slipping(Science.NO_UNITS);
  
  public final Units mUnits;
  
  @Override
  public String getLabel()
  {
     return name().replaceAll("_", " ") + 
           " (" + mUnits.getLabel() + ")";
  }
  
  private EResults(Units pUnits)
  {
    mUnits = pUnits;
  }
  
  public double value(EtherSimResult pResult)
  {
    double result = -1;
    switch(this)
    {
      case Robot_Acceleration: result = pResult.mAcceleration.to(mUnits); break;
      case Current_Per_Motor: result = pResult.mCurrentPerMotor.to(mUnits); break;
      case Is_Slipping: result = pResult.mIsSlipping ? 1d : 0d; break;
      case Robot_Speed: result = pResult.mRobotSpeed.to(mUnits); break;
      case Robot_Travel: result = pResult.mCurrentRobotTravel.to(mUnits); break;
      case Voltage_At_Motor: result = pResult.mVoltageAtMotor.to(mUnits); break;
      default:
    }
    return result;
  }

  @Override
  public boolean isDisplayed()
  {
    return true;
  }
}
