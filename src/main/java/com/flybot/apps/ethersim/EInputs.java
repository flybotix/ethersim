package com.flybot.apps.ethersim;

import com.flybot.sci.Distance;
import com.flybot.sci.Force;
import com.flybot.sci.Mass;
import com.flybot.sci.Ratio;
import com.flybot.sci.Resistance;
import com.flybot.sci.Science;
import com.flybot.sci.Time;
import com.flybot.sci.Units;
import com.flybot.sci.Value;
import com.flybot.sci.Voltage;
import com.flybot.util.lang.IDisplayable;

public enum EInputs implements IDisplayable
{
  Kro("Rolling Resistance O (lbf)", 0, Force.LBF, Force.class),
  Krv("Rolling Resistance V (lbf/(ft/s))", 0d, Science.NO_UNITS, Value.class),
  Kf("Drivetrain Efficiency (%)", 1d/*0.91*/, Ratio.PCT, Ratio.class),
  n("Number of Motors", 6d, Science.NO_UNITS, Value.class),
  G("Gear Ratio, #:1", 9.4, Ratio.RATIO, Ratio.class),
  r("Wheel Radius (in)", 3d, Distance.INCH, Distance.class),
  M("Vehicle Total Mass, lbm", 147d, Mass.LBM, Mass.class),
  uk("Coefficient of Kinetic Friction", 0.7, Science.NO_UNITS, Value.class),
  us("Coefficient of Static Friction", 1.1, Science.NO_UNITS, Value.class),
  Rcom("Battery/Wire resistance, Ohms", 0.013, Resistance.OHM, Resistance.class),
  Rone("PDB to Motor Resistance, Ohms", 0.002, Resistance.OHM, Resistance.class),
  Vbat("Battery Voltage", 12.7, Voltage.VOLT, Voltage.class),
  dt("Time Step of Graph (s)", 0.025, Time.SECOND, Time.class),
  tstop("End Time of Graph, (s)",2.5,Time.SECOND, Time.class);
  
  public final String label;
  public final Units unit;
  public final double defaultvalue;
  public final Class<? extends Science> clazz;
  
  private EInputs(String pLabel, double pDefaultValue, Units pUnits, Class<? extends Science> pClass)
  {
    label = pLabel;
    unit = pUnits;
    defaultvalue = pDefaultValue;
    clazz = pClass;
  }

  @Override
  public boolean isDisplayed()
  {
    return true;
  }
  
  @Override
  public String getLabel()
  {
    return label;
  }
}
