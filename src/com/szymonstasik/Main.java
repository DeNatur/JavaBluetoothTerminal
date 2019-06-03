package com.szymonstasik;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;


public class Main extends Application {

    final Menu menuFile = new Menu("File");
    final Menu menuBt = new Menu("Bluetooth");
    //final Menu menuWindows = new Menu("Window");


    ListView<String> devices = new ListView<String>();

    private static Bluetooth bt = new Bluetooth();
    private static Text terminal = new Text();
    private static Text msg = new Text();
    private static Button btn = new Button();
    private static TextField chatField = new TextField();
    private static Button btnLeft = new Button();
    private static Button btnRight = new Button();
    private static Button btnUp = new Button();
    private static Button btnDown = new Button();
    private  static GridPane keyGrid = new GridPane();

    private  static String colorGrey = "-fx-background-color: #e2e2e2";
    private static String colorRed = "-fx-background-color: #b20000";


    @Override
    public void start(Stage primaryStage) throws Exception {
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        final BooleanProperty firstTime = new SimpleBooleanProperty(true); // Variable to store the focus on stage load
        //search();
        Group root = new Group();
        GridPane grid = new GridPane();
        GridPane msgPane = new GridPane();
        msgPane.setStyle("-fx-background-color: #ffffff");

        Pane terminalPane = new Pane();
        msgPane.add(msg,1,1);
        terminalPane.getChildren().add(terminal);

        VBox box = new VBox();
        box.getChildren().add(grid);
        terminal.maxWidth(10);
        //terminal.setY(50);
        buttons();
        chatField.focusedProperty().addListener((observable,oldValue,newValue) ->{
            if(newValue && firstTime.get()){
                box.requestFocus(); // Delegate the focus to container
                firstTime.setValue(false); // Variable value changed for future references
            }
        } );

        //LIST VIEW OF BLUETOOTH DEVICES



        Text term = new Text();
        term.setStyle("-fx-background-color: #ffffff");
        term.setText("Terminal");


        msgPane.setPrefHeight(50);
        devices.setPrefSize(150,100);
        grid.add(msgPane, 3, 2);
        grid.add(terminalPane, 3, 3);
        grid.add(chatField, 3, 0);
        grid.add(keyGrid, 5, 2);
        grid.add(btn,5,0);
        grid.add(devices, 1, 2);

        //grid.setGridLinesVisible(true);
        grid.setHgap(20);
        grid.setVgap(20);
        btn.setText("SEND");
        btn.setOnAction(event -> sendData());

        box.setStyle("-fx-background-color:\n" +
                "        linear-gradient(#686868 0%, #232723 25%, #373837 75%, #757575 100%),\n" +
                "        linear-gradient(#020b02, #3a3a3a),\n" +
                "        linear-gradient(#9d9e9d 0%, #6b6a6b 20%, #343534 80%, #242424 100%),\n" +
                "        linear-gradient(#8a8a8a 0%, #6b6a6b 20%, #343534 80%, #262626 100%),\n" +
                "        linear-gradient(#777777 0%, #606060 50%, #505250 51%, #2a2b2a 100%);");

        //
        // ------------ MENU BAR ------------

        // --- Menu File
        MenuItem exitMenuItem = new MenuItem("Exit");
        //MenuItem shutdownMenuItem = new MenuItem("Shutdown");
        //MenuItem rebootMenuItem = new MenuItem("Reboot");
        exitMenuItem.setOnAction(event -> exit());
        //shutdownMenuItem.setOnAction(event -> bt.sendMessege("exit"));
       // rebootMenuItem.setOnAction(event -> bt.sendMessege("reboot"));
        menuFile.getItems().addAll(exitMenuItem);

        // --- Menu Bluetooth
        MenuItem refreshMenuItem = new MenuItem("Refresh");
        MenuItem connectMenuItem = new MenuItem("Connect");
        MenuItem disconnectMenuItem = new MenuItem("Disconnect");
        //MenuItem windowsMenuItem = new MenuItem("windows");
        refreshMenuItem.setOnAction(event -> search());
        connectMenuItem.setOnAction(event -> {
            try {
                bt.connect(msg, devices.getSelectionModel().getSelectedIndex());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        disconnectMenuItem.setOnAction(event -> {
            try {
                bt.disconnect(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        //windowsMenuItem.setOnAction(event -> bt.sendMessege("View"));
        menuBt.getItems().addAll(refreshMenuItem,connectMenuItem,disconnectMenuItem);

        // --- Menu Windows
        //MenuItem rsMenuItem = new MenuItem("RS");
        //MenuItem killMenuItem = new MenuItem("Kill RS");
        //killMenuItem.setOnAction(event -> bt.sendMessege("krs"));
        //rsMenuItem.setOnAction(event -> bt.sendMessege("rs"));//menuWindows.getItems().addAll(rsMenuItem,killMenuItem);



        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menuFile, menuBt );
        root.getChildren().addAll(box, menuBar);

        Scene scene = new Scene(root);

        KeyListener(scene);



        primaryStage.setTitle("Bluetooth");
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    private void exit() {
        Platform.exit();
        System.exit(0);
    }

    private void search() {
        System.out.println("searching...");
        try {
            bt.search(devices);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Sends the data through serial
    private void sendData() {
        bt.sendMessege(chatField.getText());
    }


    //Listen for pressing and realising arrow keys
    private void KeyListener(Scene scene) {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case UP:
                        btnUp.setStyle(colorRed );
                        bt.sendMessege("U");
                        break;
                    case DOWN:
                        btnDown.setStyle(colorRed );
                        bt.sendMessege("D");
                        break;
                    case LEFT:
                        btnLeft.setStyle(colorRed );
                        bt.sendMessege("L");
                        break;
                    case RIGHT:
                        btnRight.setStyle(colorRed );
                        bt.sendMessege("R");
                        break;

                }
            }

        });
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case UP:
                        btnUp.setStyle(colorGrey);
                        //bt.sendMessege("r");
                        break;
                    case DOWN:
                        btnDown.setStyle(colorGrey);
                        //bt.sendMessege("r");
                        break;
                    case LEFT:
                        btnLeft.setStyle(colorGrey);
                        //bt.sendMessege("r");
                        break;
                    case RIGHT:
                        btnRight.setStyle(colorGrey);
                        //bt.sendMessege("r");
                        break;

                }
            }
        });
    }


    //Sets the buttons default values
    private void buttons() {
        double buttonSize = 30;

        keyGrid.add(btnUp, 2, 1);
        btnUp.setText(" ");
        btnLeft.setText(" ");
        btnRight.setText(" ");
        btnDown.setText(" ");
        keyGrid.add(btnLeft, 1, 2);
        keyGrid.add(btnDown, 2, 3);
        keyGrid.add(btnRight, 3, 2);
        btnUp.setStyle(colorGrey);
        btnDown.setStyle(colorGrey);
        btnLeft.setStyle(colorGrey);
        btnRight.setStyle(colorGrey);

        btnUp.setMinSize(buttonSize,buttonSize);
        btnDown.setMinSize(buttonSize,buttonSize);
        btnRight.setMinSize(buttonSize,buttonSize);
        btnLeft.setMinSize(buttonSize,buttonSize);
    }


    public static void main(String[] args) {
        launch(args);
    }

}

class SendReceive extends Thread {
    private final InputStream mIs;
    private final Text mTxt;

    public SendReceive(Text txt, InputStream is){
        mTxt = txt;
        mIs = is;
    }

    public void run(){
        byte[] buffer = new byte[1024];
        int bytes;
        while (true){
            try {
                bytes = mIs.read(buffer);
                Thread.sleep(100);
                String tempMsg = new String(buffer,0, bytes);
                mTxt.setText(tempMsg);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


}







