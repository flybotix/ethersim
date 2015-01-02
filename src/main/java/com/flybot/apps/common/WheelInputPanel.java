package com.flybot.apps.common;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import com.flybot.apps.OmisThrownTogether;
import com.flybot.fxwidgets.BorderedTitlePane;
import com.flybot.powertrain.EWheelProperty;
import com.flybot.powertrain.Wheel;

public class WheelInputPanel extends OmisThrownTogether
{
  private Map<EWheelProperty, Control> mWheelFields = new HashMap<>();
  private Region mLayout;
  
  public WheelInputPanel()
  {
    initCommon();
    GridPane wheels = createSimpleGrid(EnumSet.allOf(EWheelProperty.class), 
        mWheelFields, k->k.mControl, u->"");

    ComboBox<Wheel> wheelcombo = createComboBox(sWheels, EnumSet.allOf(EWheelProperty.class), mWheelFields);
    wheels.add(new Label("COTS Wheel: "), 0, EWheelProperty.values().length);
    wheels.add(wheelcombo, 1, EWheelProperty.values().length);
    mLayout = new BorderedTitlePane("Wheel Inputs", wheels);
  }
  
  public Map<EWheelProperty, Control> getFields()
  {
    return mWheelFields;
  }

  @Override
  protected String getAppName()
  {
    return "Wheel Input Panel";
  }

  @Override
  public void init()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void destroy()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Region getDisplay()
  {
    return mLayout;
  }

}
