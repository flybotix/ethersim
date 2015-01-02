package com.flybot.powertrain;

import com.flybot.sci.AngularVelocity;
import com.flybot.sci.Current;
import com.flybot.sci.Power;
import com.flybot.sci.Torque;
import com.flybot.sci.Voltage;

public class DcMotor
{
  private final Torque mStallTorque;
  private final Current mStallCurrent;
  private final Current mFreeCurrent;
  private final AngularVelocity mFreeSpeed;
  private final Voltage mRatedVoltage;
  public final String mName;
  
  private Torque[] mCalculatedTorques;
  private Current[] mCalculatedCurrent;
  private double[] mCalculatedEfficiency;
  private AngularVelocity[] mCalculatedAngularVelocities;
  private Power[] mOutputPower;
  private Power[] mInputPower;
  private Power mPeakPower;
  
  public DcMotor(Voltage pRatedVoltage, AngularVelocity pFreeSpeed, 
      Torque pStallTorque, Current pStallCurrent, Current pFreeCurrent)
  {
    this(pRatedVoltage, pFreeSpeed, pStallTorque, pStallCurrent, pFreeCurrent, "");
  }

  public DcMotor(Voltage pRatedVoltage, AngularVelocity pFreeSpeed, 
      Torque pStallTorque, Current pStallCurrent, Current pFreeCurrent,
      String pName)
  {
    mRatedVoltage = pRatedVoltage;
    mFreeSpeed = pFreeSpeed;
    mStallTorque = pStallTorque;
    mFreeCurrent = pFreeCurrent;
    mStallCurrent = pStallCurrent;
    mName = pName;
  }
  
  public void calculateProperties(int pNumDataPoints)
  {
    mCalculatedTorques = new Torque[pNumDataPoints];
    mCalculatedCurrent = new Current[pNumDataPoints];
    mCalculatedEfficiency = new double[pNumDataPoints];
    mOutputPower = new Power[pNumDataPoints];
    mInputPower = new Power[pNumDataPoints];
    mCalculatedAngularVelocities = new AngularVelocity[pNumDataPoints];
    
    double tq_incr = mStallTorque.si() / (double)(pNumDataPoints-1);
    double cur_incr = (mStallCurrent.si()-mFreeCurrent.si()) / (double) (pNumDataPoints-1);
    double spd_decr = mFreeSpeed.si() / (double)(pNumDataPoints-1);
    double peakpower = -1;
    for(int i = 0; i < pNumDataPoints; i++)
    {
      mCalculatedTorques[i] = new Torque(Torque.NEWTON_METER, tq_incr * i);//Torque.from(Torque.EUnits.NEWTON_METER, tq_incr *i);
      mCalculatedCurrent[i] = new Current(cur_incr * i + mFreeCurrent.si());
      mCalculatedAngularVelocities[i] = 
          new AngularVelocity(AngularVelocity.RAD_PER_SEC, mFreeSpeed.si() - i * spd_decr);
//          AngularVelocity.from(
//          AngularVelocity.EUnits.RAD_PER_SEC, mFreeSpeed.mRadPerSec - i*spd_decr);
      mInputPower[i] = Power.from(mRatedVoltage, mCalculatedCurrent[i]);
      mOutputPower[i] = Power.from(mCalculatedTorques[i], mCalculatedAngularVelocities[i]);
      peakpower = Math.max(peakpower, mOutputPower[i].si());
      if(mInputPower[i].si() == 0)
      {
        mCalculatedEfficiency[i] = 0;
      }
      else
      {
        mCalculatedEfficiency[i] = mOutputPower[i].si() / mInputPower[i].si();
      }
    }
    mPeakPower = new Power(peakpower);
  }

  public Power getPeakPower()
  {
    return mPeakPower;
  }
  
  /**
   * @return the stallTorque
   */
  public Torque getStallTorque()
  {
    return mStallTorque;
  }

  /**
   * @return the stallCurrent
   */
  public Current getStallCurrent()
  {
    return mStallCurrent;
  }

  /**
   * @return the freeCurrent
   */
  public Current getFreeCurrent()
  {
    return mFreeCurrent;
  }

  /**
   * @return the freeSpeed
   */
  public AngularVelocity getFreeSpeed()
  {
    return mFreeSpeed;
  }

  /**
   * @return the ratedVoltage
   */
  public Voltage getRatedVoltage()
  {
    return mRatedVoltage;
  }

  /**
   * @return the calculatedTorques
   */
  public Torque[] getCalculatedTorques()
  {
    return mCalculatedTorques;
  }

  /**
   * @return the calculatedCurrent
   */
  public Current[] getCalculatedCurrent()
  {
    return mCalculatedCurrent;
  }

  /**
   * @return the calculatedEfficiency.  All values should range 0-1
   */
  public double[] getCalculatedEfficiency()
  {
    return mCalculatedEfficiency;
  }

  /**
   * @return the calculatedAngularVelocities
   */
  public AngularVelocity[] getCalculatedAngularVelocities()
  {
    return mCalculatedAngularVelocities;
  }

  /**
   * @return the outputPower
   */
  public Power[] getOutputPower()
  {
    return mOutputPower;
  }

  /**
   * @return the inputPower
   */
  public Power[] getInputPower()
  {
    return mInputPower;
  }
  
  public String toString()
  {
    return mName;
  }
}
