package com.flybot.apps.simplecalc;

import com.flybot.apps.simplecalc.SimpleCalculator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SimpleCalculatorTest extends Application
{
   public static void main(String[] pArgs)
   {
      launch(pArgs);
   }

   @Override
   public void start(Stage pStage) throws Exception
   {
      String css = this.getClass().getResource("sandbox.css").toExternalForm();
      
      SimpleCalculator calc = new SimpleCalculator();

      Scene scene = new Scene(calc.getDisplay());
      scene.getStylesheets().add(css);
      pStage.setScene(scene);
      pStage.setWidth(1280);
      pStage.setHeight(720);
      pStage.setTitle("Simple Calculator");
      pStage.show();
   }
}
