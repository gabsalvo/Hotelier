import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Starter {
  private Socket socket;
  private BufferedReader in;
  private PrintWriter out;
  private Scanner scanner;
  private String udpAddress;
  private int udpPort;

  private ReadConfigClient config;


  public Starter() {
    scanner = new Scanner(System.in);
    try {
      config = new ReadConfigClient(); // Assuming this loads configurations
      this.udpAddress = config.getUdpAddress();
      this.udpPort = config.getUdpPort();
    } catch (IOException e) {
      System.err.println("Error loading configuration: " + e.getMessage());
      // Handle the exception appropriately
    }
  }

  public void start() {
    try {
      // Use the class-level config object, already initialized in the constructor
      String hostname = config.getHostname();
      int port = config.getPort();

      socket = new Socket(hostname, port);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(socket.getOutputStream(), true);

      ClientCommandsHandler commandsHandler = new ClientCommandsHandler(in, out);
      RicevitoreUDP ricevitoreUDP = new RicevitoreUDP(udpAddress, udpPort, false);
      Thread ricevitoreUDPThread = new Thread(ricevitoreUDP);
      ricevitoreUDPThread.start();

      Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown()));

      ClientSessionManager sessionManager = new ClientSessionManager(commandsHandler, ricevitoreUDP, scanner);
      sessionManager.manageSession();  // Start the session management

    } catch (Throwable e) {
      System.err.printf("[CLIENT] Errore: %s\n", e.getMessage());
      e.printStackTrace();
    } finally {
      cleanUp();
    }
  }

  private void shutdown() {
    System.out.println("[CLIENT] Avvio terminazione...");
    try {
      scanner.close();
      in.close();
      out.close();
      socket.close();
      System.out.println("[CLIENT] Terminato");
    } catch (IOException e) {
      System.err.printf("[CLIENT] Errore: %s\n", e.getMessage());
      e.printStackTrace();
    }
  }

  private void cleanUp() {
    try {
      if (socket != null) socket.close();
      if (in != null) in.close();
      if (out != null) out.close();
      if (scanner != null) scanner.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
