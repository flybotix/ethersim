package com.flybot.apps.ethersim;

import static com.flybot.apps.ethersim.EInputs.G;
import static com.flybot.apps.ethersim.EInputs.Kf;
import static com.flybot.apps.ethersim.EInputs.Kro;
import static com.flybot.apps.ethersim.EInputs.Krv;
import static com.flybot.apps.ethersim.EInputs.M;
import static com.flybot.apps.ethersim.EInputs.Rcom;
import static com.flybot.apps.ethersim.EInputs.Rone;
import static com.flybot.apps.ethersim.EInputs.Vbat;
import static com.flybot.apps.ethersim.EInputs.dt;
import static com.flybot.apps.ethersim.EInputs.n;
import static com.flybot.apps.ethersim.EInputs.r;
import static com.flybot.apps.ethersim.EInputs.tstop;
import static com.flybot.apps.ethersim.EInputs.uk;
import static com.flybot.apps.ethersim.EInputs.us;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import com.flybot.apps.common.ITrigger;
import com.flybot.powertrain.DcMotor;
import com.flybot.sci.Acceleration;
import com.flybot.sci.AngularVelocity;
import com.flybot.sci.Current;
import com.flybot.sci.Distance;
import com.flybot.sci.Science;
import com.flybot.sci.Speed;
import com.flybot.sci.Time;
import com.flybot.sci.Torque;
import com.flybot.sci.Voltage;

public class EtherSim
{
  private Set<ITrigger> mSimulatorListeners = new HashSet<>();
  public void addSimListener(ITrigger pListener)
  {
    mSimulatorListeners.add(pListener);
  }
  
  /*
   * User-modifiable fields.  They are declared final here so that the properties themselves
   * do not go out of scope.  This allows me to be lazy and not have to create getters for all of them
   * Bind away!
   */
  Map<EInputs, Property<Science>> mInputs = new HashMap<>();
  public final Property<DcMotor> mMotor = new SimpleObjectProperty<>(new DcMotor(
      new Voltage(12d),
      new AngularVelocity(AngularVelocity.RPM, 5310d),
      new Torque(Torque.OUNCE_INCH, 343.4),
      new Current(133),
      new Current(2.7)
      ));
  
  public final List<EtherSimResult> mResults = new LinkedList<>();
  public Property<Science> getProperty(EInputs pInput)
  {
    return mInputs.get(pInput);
  }
  
  public void set(EInputs pKey, Science pValue)
  {
    if(pKey.clazz.equals(pValue.getClass()))
    {
      mInputs.get(pKey).setValue(pValue);
    }
  }
  
  private double get(EInputs pInput)
  {
    Property<Science> p = getProperty(pInput);
    return p.getValue().si();
  }
  
  public EtherSim(boolean pRecalculateOnPropertyChanges)
  {
    for(EInputs ei : EInputs.values())
    {
      Science sci = null;
      try
      {
        sci = ei.clazz.newInstance();
        sci.setPreferredUnits(ei.unit);
        sci.setValue(ei.defaultvalue);
      } catch (InstantiationException | IllegalAccessException e)
      {
        System.err.println(ei.clazz.getCanonicalName() + 
            " does not have a default constructor!  Fix this in code.");
        System.exit(-1);
      }
      mInputs.put(ei, new SimpleObjectProperty<>(sci));
//    System.out.println(ei + " = " + sci.getValue() + " " + sci.getPreferredUnits());
//    System.out.println(ei + ":" + get(ei));
      if(pRecalculateOnPropertyChanges)
      {
        mInputs.get(ei).addListener(c->recalculate());
      }
    }
    if(pRecalculateOnPropertyChanges)
    {
      recalculate();
    }
  }
  
  /**
   * Creates an EtherSim without recalculating on any value changes on the input
   * This allows multiple EtherSim objects to be put on a Java8 stream and calculated
   * in parallel
   */
  public EtherSim()
  {
    this(false);
  }
  
  // cache variables
  double mTorqueOffset, mTorqueSlope,
  mKt,
  mTheoreticalRobotSpeed,
  mF2A;
  
  boolean mIsSlipping = false;
  Voltage mVoltageAtMotor = new Voltage(0);
  Speed mRobotSpeed = new Speed(Speed.METER_PER_SECOND, 0);
  Distance mCurrentTravel = new Distance(Distance.METER, 0);
  Current mCurrentPerMotor = new Current(0);
  Acceleration mAcceleration = new Acceleration(Acceleration.METER_PER_SECOND2, 0d);
  double mElapsedTime = 0d;
  double mWeight = 0;
  
  void reset()
  {
    mIsSlipping = false;
    mVoltageAtMotor = new Voltage(0);
    mRobotSpeed = new Speed(Speed.METER_PER_SECOND, 0d);
    mCurrentTravel = new Distance(Distance.METER, 0d);
    mCurrentPerMotor = new Current(0);
    mAcceleration = new Acceleration();
    mResults.clear();
  }
  
  public void recalculate()
  {
    reset();
    double wspec = mMotor.getValue().getFreeSpeed().si();
    double Vspec = mMotor.getValue().getRatedVoltage().si();
    double Tspec = mMotor.getValue().getStallTorque().si();
    double Ispec = mMotor.getValue().getStallCurrent().si();
    // calculate Derived Constants once:
    mTorqueOffset = (Tspec *get(Vbat)*wspec)/(Vspec*wspec+Ispec*get(Rone)*wspec+Ispec*get(n)*get(Rcom)*wspec);
    mTorqueSlope = (Tspec*Vspec)/(Vspec*wspec+Ispec*get(Rone)*wspec+Ispec*get(n)*get(Rcom)*wspec);
    mKt = Tspec/Ispec;
    mWeight = get(M)*9.80665;
    mF2A = get(r)/(get(n)*get(Kf)*get(G)*mKt); // vehicle total force to per-motor amps conversion
    mRobotSpeed = new Speed(Speed.METER_PER_SECOND, 0);
    mAcceleration=accel(mRobotSpeed.si()); // compute accel at t=0
    addResult();    // output values at t=0

    Heun();  // numerically integrate and output using Heun's method
    mSimulatorListeners.stream().forEach(c->c.trigger());
  }
  
  Acceleration accel(double pVelocity)
  {
    double 
    Wm, // motor speed associated with vehicle speed
    L,  // rolling resistance losses, Newtons
    Tm, // motor torque, Newtons
    Tw, // wheel torque, Newtons
    Ft, // available vehicle force due to wheel torque, Newtons
    F,  // slip-adjusted vehicle force due to wheel torque, Newtons
    Fa; // vehicle accel force, Newtons
    
    Wm = pVelocity/get(r)*get(G);
    Tm = mTorqueOffset-mTorqueSlope*Wm; // available torque at motor @ V
    Tw = get(Kf)*Tm*get(G); // available torque at one wheel @ V
    Ft = Tw/get(r)*get(n);  // available force at wheels @ V
    
    if(Ft > mWeight * get(us)) mIsSlipping = true;
    else if (Ft < mWeight * get(uk)) mIsSlipping = false;
    F = mIsSlipping ? mWeight * get(uk) : Ft;
    mCurrentPerMotor = new Current(F*mF2A);   // computed here for output
    mVoltageAtMotor = new Voltage(get(Vbat)-get(n)*mCurrentPerMotor.si()*get(Rcom)-mCurrentPerMotor.si()*get(Rone));;  // computed here for output
    L = get(Kro) + get(Krv)*pVelocity*pVelocity; // rolling resistance force
    Fa = F-L; // net force available for acceleration
    if (Fa<0) Fa=0;
    return new Acceleration(Acceleration.METER_PER_SECOND2, Fa/get(M));
  }

  void Heun(){ // numerical integration using Heun's Method
    Acceleration atmp;
    double Vtmp; // local scratch variables
    double deltat = get(dt);
    double stop = get(tstop);
    for (mElapsedTime=deltat; mElapsedTime<=stop; mElapsedTime+=deltat) {
      Vtmp = mRobotSpeed.si()+mAcceleration.si()*deltat;  // kickstart with Euler step
      atmp = accel(Vtmp);
      Vtmp = mRobotSpeed.si()+(mAcceleration.si()+atmp.si())/2*deltat; // recalc Vtmp trapezoidally
      mAcceleration = accel(Vtmp);  // update a
      mCurrentTravel = new Distance(Distance.METER, mCurrentTravel.si() + (mRobotSpeed.si()+Vtmp)/2*deltat);  // update x trapezoidally
      mRobotSpeed = new Speed(Speed.METER_PER_SECOND, Vtmp);            // update V
      addResult();
    }
  }
  
  private void addResult()
  {
    EtherSimResult result = new EtherSimResult(true, 
        new Time(Time.SECOND, mElapsedTime),
        new Distance(mCurrentTravel),
        new Speed(mRobotSpeed), 
        mIsSlipping, 
        new Acceleration(mAcceleration),
        new Current(mCurrentPerMotor.si()), 
        new Voltage(mVoltageAtMotor));
    mResults.add(result);
  }
  
  public EtherSim(Map<EInputs, Science> pInputs)
  {
    this(false);
    for(EInputs ei : pInputs.keySet())
    {
      mInputs.get(ei).setValue(pInputs.get(ei));
    }
    recalculate();
  }

  public static void main(String[] pArgs){
    if(pArgs != null && pArgs.length == EInputs.values().length)
    {
      Map<EInputs, Science> inputs = new HashMap<>();
      for(int i = 0 ; i < EInputs.values().length; i++)
      {
        EInputs e = EInputs.values()[i];
        Science sci = null;
        try
        {
          sci = e.clazz.newInstance();
          sci.setPreferredUnits(e.unit);
          sci.setValue(Double.parseDouble(pArgs[i]));
        } catch (InstantiationException | IllegalAccessException e1)
        {
          e1.printStackTrace();
          System.exit(-1);
        } catch (NumberFormatException nfe)
        {
          System.err.println("Argument # " + (i+1) + " is not a number");
          System.exit(-1);
        }
        inputs.put(e, sci);
      }
      new EtherSim(inputs).mResults.forEach(System.out::println);
    }
    else if(pArgs.length == 1)
    {
      StringBuilder sb = new StringBuilder();
      sb.append("There should be " + EInputs.values().length + " arguments to this executable.\n");
      sb.append("They should be in the following order:\n");
      for(int i = 0; i < EInputs.values().length; i++)
      {
        EInputs e = EInputs.values()[i];
        sb.append(e.name()).append('\t').append(e.unit).append('\t').append(e.label).append('\n');
      }
      System.out.println(sb);
      System.exit(-1);
    }
    else
    {
      new EtherSim(true).mResults.forEach(System.out::println);
    }
  }
}
