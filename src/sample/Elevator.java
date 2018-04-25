package sample;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.scene.Parent;
import java.util.*;


//电梯类 继承自java.util.Thread
public class Elevator extends Thread{
    public static enum Status {
        UP,DOWN,SUSPEND
    }
    int index;               //电梯编号
    int curFloor = 0;        //目前所在的楼层
    int currentElevator=1;
    int[] currentButtons = new int[20];  //目前电梯内部Button的状态
    Controller controller;

    Parent root;
    Status status;

    JFXComboBox elevatorSelector;
    JFXButton[] floorButtons = new JFXButton[20];
    JFXButton displayButton;


    PriorityQueue<Integer> destnationDown;
    PriorityQueue<Integer> destnationUp;


    //电梯的初始化函数
    public Elevator(Parent myRoot,int myIndex){


        root = myRoot;
        index = myIndex;
        status = Status.SUSPEND;
        displayButton =(JFXButton) myRoot.lookup("#display"+myIndex);
        elevatorSelector = (JFXComboBox) root.lookup("#ElevatorS");

        //将下降时的优先队列设置为大根堆
        Comparator<Integer> cmp;
        cmp = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 -o1;
            }
        };
        destnationDown = new PriorityQueue<>(cmp);
        destnationUp = new PriorityQueue<>();


        for(int i=0; i < 20; i++){
            floorButtons[i] =(JFXButton) root.lookup("#floor"+(i+1));
            currentButtons[i] = 0;
        }

    }

    public void run(){
        while(true){
            changeState();
            try {
                Thread.sleep(100);
                System.out.println("test" + index);
            }
            catch(Exception exc) {
                System.out.println("test" + index);
            }

            if(this.status == Status.UP){
                up();
            }else if (status == Status.DOWN){
                down();
            }

        }
    }

    //到达目标层的顶层
    public void up(){
        if(destnationUp.isEmpty()){
            status = Status.DOWN;
            return;
        }else if(destnationUp.peek() == 0){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    floorButtons[0].setStyle("-fx-text-fill:WHITE;-fx-background-color:#5264AE;-fx-font-size:10px;");
                }
            });
            currentButtons[0] = 0;
            destnationUp.poll();
            status = Status.SUSPEND;
            return;
        }


        try {
            Thread.sleep(1000);
        }catch (InterruptedException e){}


        curFloor++;
        if(destnationUp.peek() > curFloor ){                            //如果目标层在当前层上面

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    displayButton.setText(""+(curFloor + 1));
                }
            });
        }else if(destnationUp.peek() == curFloor){                     //如果到达一个目标层
            int temp = destnationUp.peek();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    displayButton.setText(""+(curFloor + 1));
                    if(currentElevator == index){
                    floorButtons[temp]
                            .setStyle("-fx-text-fill:WHITE;-fx-background-color:#5264AE;-fx-font-size:10px;");
                    }
                    displayButton.setText((curFloor + 1)+"Open");
                }
            });
            controller.setUpButtons(curFloor);
            try {
                Thread.sleep(3000);
            }catch (InterruptedException e){}
            currentButtons[destnationUp.peek()] = 0;
            destnationUp.poll();
        }

    }

    public void down(){
        try {
            Thread.sleep(900);
        }catch (InterruptedException e){

        }
        if(curFloor == 0){
            this.status = Status.SUSPEND;
            return;
        }


        curFloor--;
        if(destnationDown.isEmpty()){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    displayButton.setText(""+(curFloor + 1));
                }
            });
            return;
        }
        if(destnationDown.peek() < curFloor){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    displayButton.setText(""+(curFloor + 1));
                }
            });
        }else if(destnationDown.peek() == curFloor){
            int temp = destnationDown.peek();

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    displayButton.setText(""+(curFloor + 1));
                    if(currentElevator == index){
                        floorButtons[temp]
                                .setStyle("-fx-text-fill:WHITE;-fx-background-color:#5264AE;-fx-font-size:10px;");
                    }
                    displayButton.setText((curFloor + 1)+"Open");
                }
            });
            controller.setDownButtons(curFloor);
            try {
                Thread.sleep(3000);
            }catch (InterruptedException e){}



            currentButtons[destnationDown.peek()] = 0;
            destnationDown.poll();
            System.out.println("destnationDown"+index+destnationDown);

        }else;
    }

    public void changeState(){
        if(this.status == Status.SUSPEND && !destnationUp.isEmpty()){
            this.status = Status.UP;
        }else if(this.status == Status.SUSPEND && !destnationDown.isEmpty()){
            this.status = Status.DOWN;
        }
        System.out.println("Status"+index +":"+this.status);
        System.out.println("StatusUP"+index +":"+this.destnationUp);
        System.out.println("StatusDown"+index +":"+this.destnationDown);
        System.out.println("StatusFloor"+index +":"+this.curFloor);
    }

    public void addWorkList(int type,int floorIndex){
        if(type == 0){
            if(!destnationUp.contains(floorIndex))
                destnationUp.add(floorIndex);
        }else if(type == 1 && status == Status.SUSPEND){
            if(!destnationUp.contains(floorIndex))
                destnationUp.add(floorIndex);
        }else {
            if(!destnationDown.contains(floorIndex) && floorIndex != curFloor)
                destnationDown.add(floorIndex);
        }

    }

    public int[] getButtons(){

        return currentButtons;
    }

    public int getCurFloor(){
        return curFloor;
    }

    public Status getStatus(){
        return status;
    }

    public void setCurrentElevator(int index) {
        this.currentElevator = index;
    }

    public void setDestnation(int buttonIndex){
        currentButtons[buttonIndex] = 1;

        if(buttonIndex >= curFloor)
            if(!destnationUp.contains(buttonIndex))
                destnationUp.add(buttonIndex);
        else
            if(!destnationDown.contains(buttonIndex))
                destnationDown.add(buttonIndex);
    }

    public void setController(Controller controller){
        this.controller = controller;
    }


}
