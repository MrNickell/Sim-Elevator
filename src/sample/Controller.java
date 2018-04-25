package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.scene.Parent;


public class Controller {
    JFXButton[] upButtons = new JFXButton[5];
    JFXButton[] downButtons = new JFXButton[5];
    JFXButton[] floorButtons = new JFXButton[20];
    JFXComboBox elevatorSelector;
    JFXComboBox floorSelector;

    int floorIndex = 0;                    //当前楼层编号
    int elevatorIndex = 1;                 //电梯编号
    int[] elevatorPos = new int[5];        //每个电梯所处的实时位置
    int flrButState[][] = new int[20][2];  //下方的上下楼按键


    public void init(Parent root,Elevator[] elevators){

        //初始化20个楼层的按钮
        initFloorButtons(root,elevators);

        //初始化楼层与电梯选择器
        initSelector(root,elevators);

        //初始化outside 按钮
        initOutside(root,elevators);


    }

    //刷新电梯按键的状态
    public void changeElevator(Elevator[] elevators,int index){
        int[] currentButtons = elevators[index-1].getButtons();
        for(int i=0;i < 20;i++){
            if(currentButtons[i] == 0)
                floorButtons[i].setStyle("-fx-text-fill:WHITE;-fx-background-color:#5264AE;-fx-font-size:10px;");
            else
                floorButtons[i].setStyle("-fx-text-fill:WHITE;-fx-background-color:#FF0000;-fx-font-size:10px;");
        }
        for(int i = 0; i < 5;i++) {
            elevators[i].setCurrentElevator(elevatorIndex);
        }
    }

    //得到当前所处的外部楼层
    public int getFloorIndex(){
        int index1 =floorSelector.getValue().toString().
                charAt(floorSelector.getValue().toString().length()-2) -'0';
        int index2 =floorSelector.getValue().toString().
                charAt(floorSelector.getValue().toString().length()-3) -'0';
        if(index2 < 0)
            return index1 - 1;
        else
            return index1+10*index2 - 1;
    }

    private void initFloorButtons(Parent root,Elevator[] elevators){
        for(int i = 1; i <= 20; i++){
            flrButState[i-1][0] = 0;
            flrButState[i-1][1] = 0;

            floorButtons[i-1] = (JFXButton)root.lookup("#floor"+ i);
            int dest = i - 1;
            JFXButton tempButton = floorButtons[i-1];
            tempButton.setOnAction(e->{
                tempButton.setStyle("-fx-text-fill:WHITE;-fx-background-color:#FF0000;-fx-font-size:10px;");
                elevators[elevatorIndex-1].setDestnation(dest);
            });
        }
    }
    private void initSelector(Parent root,Elevator[] elevators){
        //初始化电梯下拉按钮
        elevatorSelector = (JFXComboBox) root.lookup("#ElevatorS");
        elevatorSelector.setOnAction(event -> {
            elevatorIndex =elevatorSelector.getValue().toString().
                    charAt(elevatorSelector.getValue().toString().length()-2) -'0';
            changeElevator(elevators,elevatorIndex);

        });

        //初始化楼层下拉按钮
        floorSelector = (JFXComboBox) root.lookup("#floorList");
        floorSelector.setOnAction(event -> {
            floorIndex = getFloorIndex();
            for(int i = 0; i < 5; i++){
                if(flrButState[floorIndex][0] == 1)
                    upButtons[i].setStyle("-fx-text-fill:WHITE;-fx-background-color:#FF0000;-fx-font-size:14px;");
                else
                    upButtons[i].setStyle("-fx-text-fill:WHITE;-fx-background-color:#5264AE;-fx-font-size:14px;");

                if(flrButState[floorIndex][1] == 1)
                    downButtons[i].setStyle("-fx-text-fill:WHITE;-fx-background-color:#FF0000;-fx-font-size:14px;");
                else
                    downButtons[i].setStyle("-fx-text-fill:WHITE;-fx-background-color:#5264AE;-fx-font-size:14px;");

            }

        });
    }
    private void initOutside(Parent root,Elevator[] elevators){
        for(int i=0; i < 5; i++){
            upButtons[i] = (JFXButton)root.lookup("#up" + (i + 1));
            downButtons[i] = (JFXButton)root.lookup("#down" + (i + 1));
        }

        for(int i=0; i < 5;i++){
            upButtons[i].setOnAction(event -> {
                for(int j = 0; j < 5; j++){
                    upButtons[j].setStyle("-fx-text-fill:WHITE;-fx-background-color:#FF0000;-fx-font-size:14px;");
                    flrButState[floorIndex][0] = 1;
                }
                dispatcher(0,elevators);
            });
            downButtons[i].setOnAction(event -> {
                for(int j = 0; j < 5; j++){
                    downButtons[j].setStyle("-fx-text-fill:WHITE;-fx-background-color:#FF0000;-fx-font-size:14px;");
                    flrButState[floorIndex][1] = 1;
                }
                dispatcher(1,elevators);

            });
        }
    }


    public void dispatcher(int buttonType,Elevator[] elevators){
        updateElePos(elevators);
        int[] score = new int[5];
        for(int i =0; i < 5; i++){
            if(elevators[i].getStatus() == Elevator.Status.UP && buttonType ==0){
                score[i] = floorIndex - elevators[i].getCurFloor();
                System.out.println("score"+i+":"+score[i]);
            }else if(elevators[i].getStatus() == Elevator.Status.DOWN && buttonType == 1){
                score[i] = elevators[i].getCurFloor() - floorIndex;
                System.out.println("score"+i+":"+score[i]);
            }else if(elevators[i].getStatus() == Elevator.Status.SUSPEND){
                score[i] = floorIndex;
                System.out.println("score"+i+":"+score[i]);
            }else {
                score[i] = -1;
                System.out.println("score"+i+":"+score[i]);
            }

        }

        int tempi = 0;
        for(int i = 0;i < 5;i++){
            if(score[i] < 0 ){
                tempi++;
            }else if(score[i] >0 && score[tempi] > score[i]){
                tempi = i;
            }
        }
        elevators[tempi].addWorkList(buttonType,floorIndex);
    }
    public void updateElePos(Elevator[] elevators){
        for(int i = 0; i < 5;i++){
            elevatorPos[i] = elevators[i].getCurFloor();
        }
    }
    public void setUpButtons(int floorIndex){
            flrButState[floorIndex][0] = 0;
            for(int i =0; i< 5;i++)
                upButtons[i].setStyle("-fx-text-fill:WHITE;-fx-background-color:#5264AE;-fx-font-size:14px;");

    }
    public void setDownButtons(int floorIndex){
        flrButState[floorIndex][1] = 0;
        for(int i =0; i< 5;i++)
            downButtons[i].setStyle("-fx-text-fill:WHITE;-fx-background-color:#5264AE;-fx-font-size:14px;");
    }


} 
