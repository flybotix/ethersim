package com.flybot.motor;


import com.flybot.powertrain.DcMotor;
import com.flybot.sci.AngularVelocity;
import com.flybot.sci.Current;
import com.flybot.sci.Power;
import com.flybot.sci.Torque;
import com.flybot.sci.Voltage;

public class DcMotorTest
{
  public static void main(String[] pArgs)
  {
    KnownDcMotors();
  }
  
  public static void KnownDcMotors()
  {
    int num_data_points = 100;
    int pwridx_1 = num_data_points/2 - 10;
    int pwridx_2 = num_data_points/2 + 10;
    Voltage ratedVoltage = new Voltage(12d);

    // CIM, 2014
    Torque cimstalltorque = new Torque(Torque.NEWTON_METER, 2.42d);//Torque.from(Torque.EUnits.NEWTON_METER, 2.42d);
    AngularVelocity cimfreespeed = new AngularVelocity(AngularVelocity.RPM, 5310d);//AngularVelocity.from(AngularVelocity.EUnits.RPM, 5310d);
    Current cimfreecurrent = new Current(2.7d);
    Current cimstallcurrent = new Current(133d);
    
    DcMotor cim = new DcMotor(
        ratedVoltage, cimfreespeed, cimstalltorque, cimfreecurrent, cimstallcurrent);
    cim.calculateProperties(num_data_points);
    Power[] cimpwr = cim.getOutputPower();
    double pwrmax = Double.MIN_VALUE;
    for(int i = pwridx_1; i < pwridx_2; i++)
    {
      pwrmax = Math.max(pwrmax, cimpwr[i].si());
    }
    System.out.println(pwrmax);
    assert(336d < pwrmax && pwrmax < 337d);
    
    
    // BAG, 2014
    
    // RS-550, 2014
  }
}
