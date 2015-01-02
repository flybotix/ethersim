package com.flybot.apps.ethersim;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import com.flybot.apps.OmisThrownTogether;
import com.flybot.fxwidgets.BorderedTitlePane;
import com.flybot.powertrain.DcMotor;
import com.flybot.powertrain.Motor;
import com.flybot.sci.Science;

public class EtherSimApp extends OmisThrownTogether
{
  private BorderPane mLayout = new BorderPane();
  private Map<EInputs, Control> mInputFields = new HashMap<>();
  private EtherSimGraph mGraph;
  EtherSim sim = new EtherSim();
  
  public EtherSimApp()
  {
  }
  
  @Override public String getAppName()
  {
    return "EtherSim v1.0b";
  }
  
  private GridPane createInputs()
  {
    GridPane inputs = createSimpleGrid(
        EnumSet.allOf(EInputs.class), 
        mInputFields, 
        k->new TextField(Science.sFORMAT.format(k.defaultvalue)), 
        u->u.getLabel());
    Button recalcButton = new Button("Recalculate");
    ComboBox<DcMotor> motors = new ComboBox<>(
        FXCollections.observableArrayList(Motor.toDcMotors(sMotors)));
    int index = EInputs.values().length;
    inputs.add(motors, 1, index++, 3, 1);
    inputs.add(recalcButton, 1, index++, 3, 1);
    recalcButton.setOnAction(e->recalculate());
    sim.mMotor.bind(motors.valueProperty());
    return inputs;
  }
  
  private void recalculate()
  {

    for(EInputs ei : EInputs.values())
    {
      Science sci = null;
      try
      {
        sci = ei.clazz.newInstance();
      } catch (InstantiationException | IllegalAccessException e)
      {
        e.printStackTrace();
      }
      sci.setValueAndUnits(ei.unit, getSafely(ei, mInputFields));
      sim.getProperty(ei).setValue(sci);
    }
    sim.recalculate();
  }

  @Override
  public void init()
  {
    mLayout.setPadding(new Insets(3d));
    mGraph = new EtherSimGraph(sim);
    mLayout.setLeft(new BorderedTitlePane("Inputs", createInputs()));
    mLayout.setCenter(mGraph.getDisplay());
  }

  @Override
  public void destroy()
  {
    mLayout.getChildren().clear();    
  }

  @Override
  public Region getDisplay()
  {
    return mLayout;
  }
}
