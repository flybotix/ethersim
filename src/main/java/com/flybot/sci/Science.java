package com.flybot.sci;

import java.text.DecimalFormat;
import java.util.List;

public abstract class Science
{
  public static final Units NO_UNITS = new Units()
  {
    public String getLabel() { return ""; }
    public double getSiConversion() { return 1.0; }
  };
  
  private double mValueSI;
  
  protected Units mPreferredUnits;
  
  protected Science(Units pUnits, double pValue)
  {
    setValueAndUnits(pUnits, pValue);
  }
  
  /**
   * @return the SI value of this object
   */
  public final double si()
  {
    return mValueSI;
  }
  
  /**
   * Sets the preferred units of this object.  Will check this against the
   * subclass getUnits() method
   * @param pUnits
   */
  public final void setPreferredUnits(Units pUnits)
  {
    checkUnits(pUnits);
    mPreferredUnits = pUnits;
  }
  
  /**
   * @return the preferred units
   */
  public Units getPreferredUnits()
  {
    return mPreferredUnits;
  }
  
  /**
   * @return the si() value divided by the preferred units conversion
   */
  public double getValue()
  {
    return to(getPreferredUnits());
  }
  
  /**
   * Sets the value and changes the preferred units.
   * 
   * The SI value will beome pUnits.getSiConversion() * pValue
   * @param pUnits
   * @param pValue
   */
  public final void setValueAndUnits(Units pUnits, double pValue)
  {
    setPreferredUnits(pUnits);
    setValue(pValue);
  }
  
  /**
   * Sets the value.  Is expected to the of the same units as what
   * getPreferredUnits() returns
   * @param pValue
   */
  public final void setValue(double pValue)
  {
    mValueSI = getPreferredUnits().getSiConversion() * pValue;
  }
  
  /**
   * Converts the si value to the target units and returns the result
   * @param pUnits
   * @return
   */
  public double to(Units pUnits)
  {
    checkUnits(pUnits);
    return si() / pUnits.getSiConversion();
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    return sFORMAT.format(getValue());
  }
  
  private void checkUnits(Units pUnits)
  {
    if(pUnits == null)
    {
      throw new IllegalArgumentException(getClass().getCanonicalName() +
          " cannot have null units.  Use Science.NO_UNITS for a value with no units.");
    }
    if(!getUnits().contains(pUnits))
    {
      throw new IllegalArgumentException(getClass().getCanonicalName() + 
          " cannot contain units " + pUnits.getLabel());
    }
  }
  
  protected abstract List<Units> getUnits();
  
  public static final DecimalFormat sFORMAT = new DecimalFormat("0.000");
}
