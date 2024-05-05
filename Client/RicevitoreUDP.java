import java.io.IOException;
import java.net.*;
public class RicevitoreUDP implements Runnable {
  private String udpAddress;
  private int udpPort;
  private volatile boolean active;

  public RicevitoreUDP(String udpAddress, int udpPort, boolean active) {
    this.udpAddress = udpAddress;
    this.udpPort = udpPort;
    this.active = active;
  }

  @Override
  public void run() {
    try {
      byte[] buffer = new byte[1024];
      InetAddress ia = InetAddress.getByName(udpAddress);
      MulticastSocket ms = new MulticastSocket(udpPort);

      NetworkInterface networkInterface = NetworkInterface.getByInetAddress(ia);
      ms.joinGroup(new InetSocketAddress(ia, udpPort), networkInterface);

      while (active && !Thread.currentThread().isInterrupted()) {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
          ms.setSoTimeout(1000);
          ms.receive(packet);
          String msg = new String(packet.getData(), packet.getOffset(), packet.getLength());
          System.out.println(msg);
        } catch (SocketTimeoutException e) {
          continue;
        }
      }

      if (!ms.isClosed()) {
        ms.leaveGroup(new InetSocketAddress(ia, udpPort), networkInterface);
        ms.close();
      }
    } catch (IOException e) {
      System.err.printf("[UDP RECEIVER] Error: %s\n", e.getMessage());
      e.printStackTrace();
    }
  }

  public void stop() {
    this.active = false;
  }
}
