package com.flybot.apps;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SingleMotorGraph extends Application
{
  private enum INPUTS
  {
    sVOLTAGE_INPUT("Rated Voltage (V)"),
    sFREE_SPEED_INPUT("Free Speed (RPM)"),
    sSTALL_TORQUE_INPUT("Stall Torque (Nm)"),
    sFREE_CURRENT_INPUT("Free Current (A)"),
    sSTALL_CURRENT_INPUT("Stall Current (A)");
    private INPUTS(String pDisplay)
    {
      mDisplayString = pDisplay;
    }
    private String mDisplayString;
  }
  
  private static int sINPUT_WIDTH = 120;
  private static int sPADDING = 8;
  
  private static int sNUM_INPUTS = INPUTS.values().length;
  
  private Label[] mDisplayTexts = new Label[sNUM_INPUTS];
  private TextField[] mInputFields = new TextField[sNUM_INPUTS];
  private Button mCalculateButton = new Button("Calculate");

  @Override
  public void start(Stage pStage) throws Exception
  {
    createLayout(pStage);
    mCalculateButton.setOnAction((event) -> { updateChart(); });
    pStage.show();
  }
  
  private void updateChart()
  {
    
  }
  
  private void createLayout(Stage pStage)
  {
    pStage.setTitle("Single Motor Graph");
    
    GridPane maingrid = new GridPane();
    maingrid.setAlignment(Pos.CENTER);
    maingrid.setHgap(10);
    maingrid.setVgap(10);
    maingrid.setPadding(new Insets(sPADDING, sPADDING, sPADDING, sPADDING));

    Scene scene = new Scene(maingrid, 800, 600);
    pStage.setScene(scene);
    
    Text motorinputtitle = new Text("Motor Input");
    
    int row = 0;
    int center_start = 2;
    int center_span = 1;
    maingrid.add(motorinputtitle, center_start, row++, center_span, 1);

    for(INPUTS input : INPUTS.values())
    {
      int i = input.ordinal();
      mDisplayTexts[i] = new Label(input.mDisplayString);
      mDisplayTexts[i].setWrapText(true);
      
      mInputFields[i] = new TextField();
      maingrid.add(mDisplayTexts[i], i, row);
      maingrid.add(mInputFields[i], i, row+1);
    }
    
    row+=2;
    
    maingrid.add(mCalculateButton, center_start, row++, center_span, 1);
    
  }
  
  public static void main(String[] pArgs)
  {
    launch(pArgs);
  }
}
