import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;


public class DAS {
    public static void main(String[] args) throws Exception {

        DatagramSocket socket;
        boolean isSocketInUse = false;
        int port = 0;
        int number = 0;
        ArrayList<Integer> listOfNum = new ArrayList<>();

        if(args.length == 2) {
            try {
                port = Integer.parseInt(args[0]);
                number = Integer.parseInt(args[1]);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else
            throw new Exception();

        try {
            socket = new DatagramSocket(port);
            isSocketInUse = true;
            listOfNum.add(number);
        }catch (SocketException e){
            socket = new DatagramSocket();
            isSocketInUse = false;
        }

        if(isSocketInUse){
            master(socket, isSocketInUse, listOfNum);
        }else{
            slave(port, number, socket);
        }
    }

    public static void slave(int port, int number, DatagramSocket socket){
        try {
            InetAddress address = InetAddress.getByName("localhost");
            byte[] buff = String.valueOf(number).getBytes();
            DatagramPacket packet = new DatagramPacket(buff, buff.length, address, port);
            socket.send(packet);
        }catch(Exception e){
            e.printStackTrace();
        }
        socket.close();
    }

    public static void master(DatagramSocket socket, boolean is, ArrayList<Integer> list) {
        byte[] buff = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buff, buff.length);
        while (is) {
            try {
                socket.receive(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String received = new String(packet.getData(), 0, packet.getLength());
            int receivedInt = Integer.parseInt(received);
            if(receivedInt != 0 && receivedInt != -1){
                System.out.println(receivedInt);
                list.add(receivedInt);
            }

            if(receivedInt == 0){
                int sum = 0;
                for(Integer i : list){
                    sum += i;
                }

                int avg = (int)(sum/list.size());
                System.out.println(avg);

                try{
                    InetAddress address = InetAddress.getByName("255.255.255.255");
                    byte[] buff0 = String.valueOf(avg).getBytes();
                    DatagramPacket packet0 = new DatagramPacket(buff, buff.length, address, 60000);
                    socket.send(packet);
                    System.out.println(address);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(receivedInt == -1){
                try {
                    System.out.println(receivedInt);
                    InetAddress address = InetAddress.getByName("255.255.255.255");
                    byte[] buff1 = String.valueOf(receivedInt).getBytes();
                    DatagramPacket packet1 = new DatagramPacket(buff, buff.length, address, socket.getPort());
                    socket.send(packet);
                }catch (Exception e){

                }
                socket.close();
                break;
            }
        }
    }
}
