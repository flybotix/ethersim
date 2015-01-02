package com.flybot.powertrain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Gearbox is a map of 1...N lists of gearsets.  The lists
 * can be different lengths and can diverge.
 * 
 * @author JesseK
 *
 * @param <KEY> - Rather than say "thou shalt refer to it as 
 * LOW GEAR and HIGH GEAR", you can
 * refer to a speed as anything you want.  Recommend to use an enum.
 * That way this class can hold powered takeoff and other uncommon
 * gearboxes.  The planned JSON implementations will use strings
 */
public class Gearbox<KEY>
{
  private final Map<KEY, List<Gearset>> mGearsets = new HashMap<>();
  
  public double getRatio(KEY pGearset)
  {
    double result = 1d;
    for(Gearset g : mGearsets.get(pGearset))
    {
      result *= g.getRatio().get();
    }
    return result;
  }

  public double getEfficiency(KEY pGearset)
  {
    double result = 1d;
    for(Gearset g : mGearsets.get(pGearset))
    {
      result *= g.getEfficiency();
    }
    return result;
  }
  
  public double getRatioDifference(KEY pGearset1, KEY pGearset2)
  {
    return -1;
  }
}
