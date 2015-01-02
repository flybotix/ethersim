package com.flybot.powertrain;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

import com.flybot.util.lang.IDisplayable;


public enum EWheelProperty implements IDisplayable
{
   TYPE("Description", new TextField()),
   PROVIDER("Provider", new TextField()),
   DIAMETERS("Wheel Diameter (\")", new ComboBox<Double>()),
   US("Static Coeff", new TextField()),
   UK("Kinetic Coeff", new TextField());
   
   public String toString(){ return mDescription; }
   
   public String mDescription;
   public Control mControl;
   
   private EWheelProperty(String pDescription, Control pControl)
   {
      mDescription = pDescription;
      mControl = pControl;
   }

  @Override
  public boolean isDisplayed()
  {
    return (this != TYPE);
  }
  
  @Override
  public String getLabel()
  {
     return mDescription;
  }
}
