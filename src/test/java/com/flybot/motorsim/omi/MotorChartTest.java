package com.flybot.motorsim.omi;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.flybot.apps.motorsim.MotorGraphPanel;
import com.flybot.powertrain.DcMotor;
import com.flybot.sci.AngularVelocity;
import com.flybot.sci.Current;
import com.flybot.sci.Torque;
import com.flybot.sci.Voltage;

public class MotorChartTest extends Application
{

  @Override
  public void start(Stage pPrimaryStage) throws Exception
  {
    StackPane pane = new StackPane();
    Scene scene = new Scene(pane);
    
    MotorGraphPanel mgp = new MotorGraphPanel();
    DcMotor motor = new DcMotor(
        new Voltage(12d), 
        new AngularVelocity(AngularVelocity.RPM, 5310),
        new Torque(Torque.NEWTON_METER, 2.42),
        new Current(133),
        new Current(2.7));
    motor.calculateProperties(1000);
    mgp.changed(null, null, motor);
    
    pane.getChildren().add(mgp.getGraphic());
    
    pPrimaryStage.setScene(scene);
    pPrimaryStage.setWidth(800);
    pPrimaryStage.setHeight(600);
    pPrimaryStage.show();
  }

  public static void main(String[] pArgs)
  {
    launch(pArgs);
  }
}
