package com.flybot.sci;

import java.util.Arrays;
import java.util.List;

public class Resistance extends Science
{
  public static final Units OHM = new Units()
  {
    public String getLabel() { return "\u2126"; }
    public double getSiConversion() { return 1.0; }
  };
  
  public Resistance(double pOhms)
  {
    super(OHM, pOhms);
  }
  
  public Resistance()
  {
    this(0d);
  }

  @Override
  protected List<Units> getUnits()
  {
    return Arrays.asList(OHM);
  }
}
