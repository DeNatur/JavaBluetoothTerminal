package com.szymonstasik;

import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;


public class Bluetooth {

    List<RemoteDevice> devicesDiscovered = new ArrayList<>();
    boolean scanFinished = false;
    RemoteDevice hc05device;
    String hc05Url = "btspp://001A7DDA7111:1;authenticate=false;encrypt=false;master=false";
    //String hc05Url;

    private static OutputStream os;
    private static InputStream is;
    StreamConnection streamConnection;

    public static void main(String[] args) {
        try {
            new Bluetooth().go();
        } catch (Exception ex) {
            Logger.getLogger(Bluetooth.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void go() throws Exception {

    }
    protected void search(ListView<String> devices) throws Exception {
        scanFinished = false;
        devices.getItems().clear();

        LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, new DiscoveryListener() {
            @Override
            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                try {

                    String name = btDevice.getFriendlyName(false);
                    System.out.format("%s (%s)\n", name, btDevice.getBluetoothAddress());

                    devices.getItems().add(name + " - " + btDevice.getBluetoothAddress());
                    devicesDiscovered.add(btDevice);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void inquiryCompleted(int discType) {
                scanFinished = true;
                System.out.println("finished searching");
            }

            @Override
            public void serviceSearchCompleted(int transID, int respCode) {
            }

            @Override
            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
            }
        });
        while (!scanFinished) {
            //this is easier to understand (for me) as the thread stuff examples from bluecove
            Thread.sleep(500);
        }
    }
    protected boolean connect(Text text, int index) {

        System.out.println(index);

        try {
            String name = devicesDiscovered.get(index).getFriendlyName(false);
            System.out.format("%s (%s)\n", name, devicesDiscovered.get(index).getBluetoothAddress());
            String btUrl = hc05Url.substring(0,8)+devicesDiscovered.get(index).getBluetoothAddress()+hc05Url.substring(20);
            System.out.println(btUrl);
            streamConnection = (StreamConnection) Connector.open(btUrl);
            os = streamConnection.openOutputStream();
            is = streamConnection.openInputStream();
            text.setText("CONNECTED");
        } catch (IOException e) {
            e.printStackTrace();
        }




        return true;
    }

    public InputStream getInputStream(){
        return is;
    }
    public void sendMessege(String msg)  {
        try {
            os.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void disconnect(Text text) throws IOException {
        text.setText("DISCONNECTED");
        streamConnection.close();
        os.close();
        is.close();
    }
}