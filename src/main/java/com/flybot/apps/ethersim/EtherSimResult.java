package com.flybot.apps.ethersim;

import com.flybot.sci.Acceleration;
import com.flybot.sci.Current;
import com.flybot.sci.Distance;
import com.flybot.sci.Speed;
import com.flybot.sci.Time;
import com.flybot.sci.Voltage;

public class EtherSimResult
{
  public final Time mElapsedTime;
  public final Distance mCurrentRobotTravel;
  public final Speed mRobotSpeed;
  public final Acceleration mAcceleration;
  public final Current mCurrentPerMotor;
  public final Voltage mVoltageAtMotor;
  public final boolean mIsSlipping;
  private final boolean mUseDefaultEnglishOutput;
  
  public EtherSimResult(boolean pUseDefaultEnglishOutput, 
      Time pElapsedTime, 
      Distance pCurrentRobotTravel, 
      Speed pRobotSpeed, 
      boolean pIsSlipping, 
      Acceleration pAcceleration, 
      Current pCurrentPerMotor, 
      Voltage pVoltageAtMotor)
  {
    mElapsedTime = pElapsedTime;
    mCurrentRobotTravel = pCurrentRobotTravel;
    mRobotSpeed = pRobotSpeed;
    mAcceleration = pAcceleration;
    mCurrentPerMotor = pCurrentPerMotor;
    mVoltageAtMotor = pVoltageAtMotor;
    mIsSlipping = pIsSlipping;
    mUseDefaultEnglishOutput = pUseDefaultEnglishOutput;
  }
  
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(mElapsedTime.si()).append(",");
    if(mUseDefaultEnglishOutput)
    {
      sb.append(mCurrentRobotTravel.to(Distance.FEET)).append(",");
      sb.append(mRobotSpeed.to(Speed.FEET_PER_SECOND)).append(",");
    }
    else
    {
      sb.append(mCurrentRobotTravel.si()).append(",");
      sb.append(mRobotSpeed.si()).append(",");
    }
    sb.append(mIsSlipping?1:0).append(",");
    if(mUseDefaultEnglishOutput)
    {
      sb.append(mAcceleration.to(Acceleration.FEET_PER_SECOND2)).append(",");
    }
    else
    {
      sb.append(mAcceleration.si()).append(",");
    }
    sb.append(mCurrentPerMotor.si()).append(",");
    sb.append(mVoltageAtMotor.si());
    return sb.toString();
  }
}
