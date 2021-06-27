package sample;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;

public class Main extends Application {
    public static ObservableList<String> circlesList = FXCollections.observableArrayList();
    public static HashMap<String, Integer> circleMap = new HashMap<>();
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("fxmls/Menu.fxml"));
        primaryStage.setTitle("C-EDGE");
        primaryStage.setScene(new Scene(root, 1200, 330));
        primaryStage.show();
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        SqlQuery q = new SqlQuery();
        q.setQuery("select * from circles");
        ResultSet rs = q.sql();
        while (rs.next()){
            circlesList.add(rs.getString(2));
            circleMap.put(rs.getString(2),rs.getInt(1));
        }
        launch(args);
    }
}
//class GetNetworkAddress {
//
//    public static String GetAddress(String addressType) {
//        String address = "";
//        InetAddress lanIp = null;
//        try {
//
//            String ipAddress = null;
//            Enumeration<NetworkInterface> net = null;
//            net = NetworkInterface.getNetworkInterfaces();
//
//            while (net.hasMoreElements()) {
//                NetworkInterface element = net.nextElement();
//                Enumeration<InetAddress> addresses = element.getInetAddresses();
//
//                while (addresses.hasMoreElements() && element.getHardwareAddress().length > 0 && !isVMMac(element.getHardwareAddress())) {
//                    InetAddress ip = addresses.nextElement();
//                    if (ip instanceof Inet4Address) {
//
//                        if (ip.isSiteLocalAddress()) {
//                            ipAddress = ip.getHostAddress();
//                            lanIp = InetAddress.getByName(ipAddress);
//                        }
//
//                    }
//
//                }
//            }
//
//            if (lanIp == null)
//                return null;
//
//            if (addressType.equals("ip")) {
//
//                address = lanIp.toString().replaceAll("^/+", "");
//
//            } else if (addressType.equals("mac")) {
//
//                address = getMacAddress(lanIp);
//
//            } else {
//
//                throw new Exception("Specify \"ip\" or \"mac\"");
//
//            }
//
//        } catch (UnknownHostException ex) {
//
//            ex.printStackTrace();
//
//        } catch (SocketException ex) {
//
//            ex.printStackTrace();
//
//        } catch (Exception ex) {
//
//            ex.printStackTrace();
//
//        }
//
//        return address;
//
//    }
//
//    private static String getMacAddress(InetAddress ip) {
//        String address = null;
//        try {
//
//            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
//            byte[] mac = network.getHardwareAddress();
//
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < mac.length; i++) {
//                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
//            }
//            address = sb.toString();
//
//        } catch (SocketException ex) {
//
//            ex.printStackTrace();
//
//        }
//
//        return address;
//    }
//
//    private static boolean isVMMac(byte[] mac) {
//        if(null == mac) return false;
//        byte invalidMacs[][] = {
//                {0x00, 0x05, 0x69},             //VMWare
//                {0x00, 0x1C, 0x14},             //VMWare
//                {0x00, 0x0C, 0x29},             //VMWare
//                {0x00, 0x50, 0x56},             //VMWare
//                {0x08, 0x00, 0x27},             //Virtualbox
//                {0x0A, 0x00, 0x27},             //Virtualbox
//                {0x00, 0x03, (byte)0xFF},       //Virtual-PC
//                {0x00, 0x15, 0x5D}              //Hyper-V
//        };
//
//        for (byte[] invalid: invalidMacs){
//            if (invalid[0] == mac[0] && invalid[1] == mac[1] && invalid[2] == mac[2]) return true;
//        }
//
//        return false;
//    }
//
//}
