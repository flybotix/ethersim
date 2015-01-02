package com.flybot.apps.simplecalc;

import static com.flybot.apps.ethersim.EInputs.G;
import static com.flybot.apps.ethersim.EInputs.Kf;
import static com.flybot.apps.ethersim.EInputs.Kro;
import static com.flybot.apps.ethersim.EInputs.M;
import static com.flybot.apps.ethersim.EInputs.n;
import static com.flybot.apps.ethersim.EInputs.r;
import static com.flybot.apps.ethersim.EInputs.uk;
import static com.flybot.apps.ethersim.EInputs.us;
import static com.flybot.apps.simplecalc.EOutput.DRIVE_TRAIN_ADJ_SPD;
import static com.flybot.apps.simplecalc.EOutput.DRIVE_TRAIN_FREE_SPD;
import static com.flybot.apps.simplecalc.EOutput.GEAR_RATIO;
import static com.flybot.apps.simplecalc.EOutput.PUSHING_MATCH_CURRENT_PER_MOTOR;
import static com.flybot.powertrain.EMotorProperty.FREE_CURRENT;
import static com.flybot.powertrain.EMotorProperty.FREE_SPEED;
import static com.flybot.powertrain.EMotorProperty.STALL_CURRENT;
import static com.flybot.powertrain.EMotorProperty.STALL_TORQUE;
import static com.flybot.powertrain.EPowerTrain.DRIVETRAIN_EFFICIENCY;
import static com.flybot.powertrain.EPowerTrain.NUM_GEARBOXES_IN_DT;
import static com.flybot.powertrain.EPowerTrain.NUM_MOTORS_PER_GEARBOX;
import static com.flybot.powertrain.EPowerTrain.PCT_WEIGHT_ON_DRV_WHL;
import static com.flybot.powertrain.EPowerTrain.ROBOT_WEIGHT;
import static com.flybot.powertrain.EPowerTrain.SPEED_LOSS_CONSTANT;
import static com.flybot.powertrain.EWheelProperty.DIAMETERS;
import static com.flybot.powertrain.EWheelProperty.UK;
import static com.flybot.powertrain.EWheelProperty.US;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import com.flybot.apps.OmisThrownTogether;
import com.flybot.apps.common.ITrigger;
import com.flybot.apps.common.MotorInputPanel;
import com.flybot.apps.common.WheelInputPanel;
import com.flybot.apps.ethersim.EInputs;
import com.flybot.apps.ethersim.EtherSim;
import com.flybot.apps.ethersim.EtherSimGraph;
import com.flybot.fxwidgets.BorderedTitlePane;
import com.flybot.powertrain.EMotorProperty;
import com.flybot.powertrain.EPowerTrain;
import com.flybot.powertrain.EWheelProperty;
import com.flybot.sci.Distance;
import com.flybot.sci.Force;
import com.flybot.sci.Mass;
import com.flybot.sci.Ratio;
import com.flybot.sci.Value;
import com.flybot.sci.Voltage;

public class SimpleCalculator extends OmisThrownTogether
{
  private BorderPane mLayout = new BorderPane();
  private MotorInputPanel mMotorInputs;
  private WheelInputPanel mWheelInputs;
  private Map<EPowerTrain, Control> mPowerTrainFields = new HashMap<>();
  private Map<Integer, Control> mDrivingGearFields = new HashMap<>();
  private Map<Integer, Control> mDrivenGearFields = new HashMap<>();
  private Map<Integer, Map<EOutput, TextField>> mOutputFields = new HashMap<>();
  private int mNumGearConfigs = 2; // TODO add two-speed drivetrain support
  private int mNumStages = 4;
  private final TextField mRatioSpread = new TextField("2.06");
  private final EtherSim mSim = new EtherSim();

  public SimpleCalculator()
  {
  }
  
  @Override
  public void init()
  {
    VBox v = new VBox();
    v.getChildren().add(createMotorInputPanel());
    v.getChildren().add(createGearboxInputPanel());
    v.getChildren().add(createWheelInputPanel());
    v.getChildren().add(createPowerTrainInputPanel());
//    mLayout.setLeft(createMotorInputPanel());
//    mLayout.setRight(createGearboxInputPanel());
    mLayout.setLeft(v);
//    mLayout.setBottom(createOutputPanel());
    
//    mLayout.setRight(web);
    EtherSimGraph graph = new EtherSimGraph(mSim);
    VBox.setVgrow(graph.getDisplay(), Priority.ALWAYS);
    
    VBox right = new VBox();
    right.setFillWidth(true);
    right.getChildren().add(graph.getDisplay());
    right.getChildren().add(createOutputPanel());
    mLayout.setCenter(right);
    
    bindEverything();
    recalculate();
  }
  
  @Override public String getAppName()
  {
    return "JVN Calculator v1.0";
  }

  /**
   * Recalculates based upon the input fields and sets the output
   * fields accordingly
   */
  private void recalculate()
  {
    double spread = Double.parseDouble(mRatioSpread.getText());
    double ratio = 1d;
    for(int i = 1 ; i <= mNumStages; i++)
    {
      ratio *= getSafely(i, mDrivingGearFields);
      ratio /= getSafely(i, mDrivenGearFields);
    }
    mSim.set(G, new Ratio(Ratio.PCT, 1/ratio));
    set(GEAR_RATIO,0, 1/ratio);
    set(GEAR_RATIO,1, 1/ratio*spread);
    
    mSim.set(r, new Distance(Distance.INCH, get(DIAMETERS)/2));
    mSim.set(us, new Value(get(US)));
    mSim.set(uk, new Value(get(UK)));
    mSim.set(n, new Value(get(NUM_GEARBOXES_IN_DT) * get(NUM_MOTORS_PER_GEARBOX)));
    mSim.set(M, new Mass(Mass.LBM, get(ROBOT_WEIGHT)));
    mSim.set(Kf, new Ratio(Ratio.PCT, get(DRIVETRAIN_EFFICIENCY)/100));
    mSim.set(Kro, new Force(Force.LBF, (1-get(SPEED_LOSS_CONSTANT)/100)*get(ROBOT_WEIGHT)));
    mSim.set(EInputs.Krv, new Force(Force.LBF, (1-get(DRIVETRAIN_EFFICIENCY)/100)*get(ROBOT_WEIGHT)));
//    System.out.println("Kro = " + (1-get(SPEED_LOSS_CONSTANT)/100)*get(ROBOT_WEIGHT));
//    System.out.println("Krv = " + (1-get(DRIVETRAIN_EFFICIENCY)/100)*get(ROBOT_WEIGHT));
    mSim.set(EInputs.Vbat, new Voltage(12d));
    mSim.recalculate();

    double speedFtPerSec = get(FREE_SPEED)// @ rot/min
        / 60d // 1min/60s, now @ rot/s
        * Math.PI * get(DIAMETERS) // 1Circum"/1rot , @ "/s
        / 12d // 1ft/12", now @ ft/s off Motor
        * ratio; // @ ft/s through gearing
    set(DRIVE_TRAIN_FREE_SPD, 0, speedFtPerSec);
    set(DRIVE_TRAIN_FREE_SPD, 1, speedFtPerSec/spread);

    speedFtPerSec *= get(SPEED_LOSS_CONSTANT)/100d;
    set(DRIVE_TRAIN_ADJ_SPD, 0, speedFtPerSec);
    set(DRIVE_TRAIN_ADJ_SPD, 1, speedFtPerSec/spread);

    double current = (get(STALL_CURRENT) - get(FREE_CURRENT)) / get(STALL_TORQUE);
    current *= get(ROBOT_WEIGHT)*get(PCT_WEIGHT_ON_DRV_WHL)/100d*get(US)/get(NUM_GEARBOXES_IN_DT)*4.44822161526*get(DIAMETERS)*0.0254/2/(get(DRIVETRAIN_EFFICIENCY)/100)/get(NUM_MOTORS_PER_GEARBOX)*ratio;
    current += get(FREE_CURRENT);
    set(PUSHING_MATCH_CURRENT_PER_MOTOR, 0, current);
    set(PUSHING_MATCH_CURRENT_PER_MOTOR, 1, current/spread);
  }

  /**
   * Adds listeners to the input fields and input combo boxes
   */
  private void bindEverything()
  {
    ITrigger trigger = ()->recalculate();
    addListenerToAll(mDrivenGearFields, trigger);
    addListenerToAll(mDrivingGearFields, trigger);
    addListenerToAll(mMotorInputs.getFields(), trigger);
    addListenerToAll(mPowerTrainFields, trigger);
    addListenerToAll(mWheelInputs.getFields(), trigger);
    mMotorInputs.getSelectedMotorProperty().valueProperty().addListener(c->{
      mSim.mMotor.setValue(mMotorInputs.getSelectedMotorProperty().getValue().toDcMotor());
      mSim.recalculate();
    });
  }

  /*
   * Utility methods so the calculation code is easier to read
   */
  private double get(EMotorProperty pEnum){ return getSafely(pEnum, mMotorInputs.getFields()); }
  private double get(EPowerTrain pEnum){ return getSafely(pEnum, mPowerTrainFields); }
  private double get(EWheelProperty pEnum){ return getSafely(pEnum, mWheelInputs.getFields()); }

  private void set(EOutput pEnum, int pSpeed, double pValue)
  {
    mOutputFields.get(pSpeed).get(pEnum).setText(pEnum.mFormat.format(pValue));
  }

  /**
   * @return the Results panel at the bottom
   */
  private Parent createOutputPanel()
  {
    for(int i = 0; i < mNumGearConfigs ; i++)
    {
      mOutputFields.put(i, new HashMap<>());         
    }
    GridPane grid = new GridPane();
    grid.setHgap(32d);
    grid.setVgap(4d);
    grid.setAlignment(Pos.CENTER);
    for(int i = 0; i < EOutput.values().length; i++)
    {
      EOutput m = EOutput.values()[i];
      grid.add(new Label(m.mLabel), i+1, 0);
    }
    for(int i = 0; i < mNumGearConfigs; i++)
    {
      grid.add(new Label((i == 0 ? "High" : "Low") + " Gear: "), 0, i+2);
      Map<EOutput, TextField> fields = mOutputFields.get(i);
      for(int oi = 0; oi < EOutput.values().length; oi++)
      {
        EOutput o = EOutput.values()[oi];
        fields.put(o, new TextField());
        fields.get(o).setEditable(false);
        grid.add(fields.get(o), oi+1, i+2);
      }
    }
    BorderedTitlePane btp = new BorderedTitlePane("Outputs", grid);
    return btp;
  }
  
  private Parent createWheelInputPanel()
  {
    mWheelInputs = new WheelInputPanel();
    return mWheelInputs.getDisplay();
  }

  /**
   * @return the Middle panel with the power trian info
   */
  private Parent createPowerTrainInputPanel()
  {
    GridPane grid = createSimpleGrid(
        EnumSet.allOf(EPowerTrain.class), mPowerTrainFields, 
        k->new TextField(Double.toString(k.mDefault)), 
        u->u.mUnits);
    return new BorderedTitlePane("Robot Properties", grid);
  }

  /**
   * @return the Gearbox panel on the right side
   */
  private Parent createGearboxInputPanel()
  {
    VBox pane = new VBox();
    pane.setAlignment(Pos.CENTER);
    GridPane grid = new GridPane();
    grid.add(new Label("Driving Gear"), 0, 0);
    grid.add(new Label("Driven Gear"), 1, 0);
    for(int i = 1; i <= mNumStages ; i++)
    {
      int driving = 1;
      int driven = 1;
      switch(i)
      { // 2014 KOP, AM14U
        case 1:driving = 14; driven = 50; break;
        case 2:driving = 19; driven = 45; break;
      }
      mDrivingGearFields.put(i, new TextField(Integer.toString(driving)));
      mDrivenGearFields.put(i, new TextField(Integer.toString(driven)));
      grid.add(mDrivingGearFields.get(i), 0, i);
      grid.add(mDrivenGearFields.get(i), 1, i);
    }
    
    pane.getChildren().add(grid);
    
    CheckBox multigear = new CheckBox("2-Speed Drivetrain");
    pane.getChildren().add(multigear);
    
    HBox ratiopane = new HBox();
    ratiopane.setAlignment(Pos.CENTER);
    ratiopane.getChildren().add(new Label("Shifter Ratio Spread"));
    ratiopane.getChildren().add(mRatioSpread);
    
    multigear.setOnAction(e->{mRatioSpread.setDisable(!multigear.isSelected());});
    mRatioSpread.setDisable(true);
    pane.getChildren().add(ratiopane);

    return new BorderedTitlePane("Gearing Input", pane);
  }

  /**
   * @return the Motor Input Panel on the left
   * TODO add a motor graph
   */
  private Parent createMotorInputPanel()
  {
    mMotorInputs = new MotorInputPanel();
    return mMotorInputs.getDisplay();
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
