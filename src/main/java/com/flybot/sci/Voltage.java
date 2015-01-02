package com.flybot.sci;

import java.util.Arrays;
import java.util.List;

public class Voltage extends Science
{
  public static final Units VOLT = new Units()
  {
    @Override public String getLabel() { return "V";  }
    @Override public double getSiConversion() { return 1d; }
  };
  public Voltage(double pVolts)
  {
    super(VOLT, pVolts);
  }

  public Voltage(Voltage pCopy)
  {
    this(pCopy.si());
  }
  
  public Voltage()
  {
    this(0d);
  }

  @Override
  protected List<Units> getUnits()
  {
    return Arrays.asList(VOLT);
  }
}
