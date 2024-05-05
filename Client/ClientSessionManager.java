import javax.naming.CommunicationException;
import java.util.Scanner;

public class ClientSessionManager {
  private ClientCommandsHandler commandsHandler;
  private RicevitoreUDP ricevitoreUDP;
  private Thread ricevitoreUDPThread;
  private boolean stato;
  private Scanner scanner;
  private String nomeutente, password, citta, nomehotel;

  public ClientSessionManager(ClientCommandsHandler commandsHandler, RicevitoreUDP ricevitoreUDP, Scanner scanner) {
    this.commandsHandler = commandsHandler;
    this.ricevitoreUDP = ricevitoreUDP;
    this.scanner = scanner;
    this.stato = false; // Initial state, not logged in
  }

  public void manageSession() {
    boolean continueRunning = true;
    while (continueRunning) {
      ClientInterface.displayMenu(stato);
      int comando = readCommand();
      continueRunning = processCommand(comando);
    }
  }

  private int readCommand() {
    try {
      return Integer.parseInt(scanner.nextLine());
    } catch (NumberFormatException e) {
      return 0; // Return an invalid command
    }
  }

  private boolean processCommand(int comando) {
    if (!check(stato, comando)) {
      return true; // Continue running if command is invalid
    }

    try {
      switch (comando) {
        case 1: handleRegistration(); break;
        case 2: handleLogin(); break;
        case 3: handleLogout(); break;
        case 4: handleHotelSearch(); break;
        case 5: handleAllHotelsSearch(); break;
        case 6: handleReviewInsertion(); break;
        case 7: handleShowBadges(); break;
        case -1: return handleExit();
        case 0: break;
        default: System.out.println("Comando non riconosciuto.");
      }
    } catch (Exception e) {
      System.err.println("Error processing command: " + e.getMessage());
      e.printStackTrace();
    }

    return true; // Continue running unless exit command is issued
  }

  private boolean handleExit() {
    ClientCommandsHandler.goodbye();
    if (stato) {
      ricevitoreUDP.stop(); // stop receiving messages
      ricevitoreUDPThread.interrupt();
      try {
        ricevitoreUDPThread.join();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
    return false; // Stop running
  }

  private void handleRegistration() {
    System.out.println("Scegli un nome utente:");
    nomeutente = scanner.nextLine();
    System.out.println("Scegli una password:");
    password = scanner.nextLine();
    commandsHandler.register(nomeutente, password);
  }

  private void handleLogin() {
    System.out.println("Nome utente:");
    nomeutente = scanner.nextLine();
    System.out.println("Password:");
    password = scanner.nextLine();
    stato = commandsHandler.login(nomeutente, password);
    if (stato) {
      ricevitoreUDPThread = new Thread(ricevitoreUDP);
      ricevitoreUDPThread.start();
    }
  }

  private void handleLogout() {
    System.out.println("Nome utente:");
    nomeutente = scanner.nextLine();
    stato = commandsHandler.logout(nomeutente);
    if (!stato) {
      ricevitoreUDPThread.interrupt();
      try {
        ricevitoreUDPThread.join();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  private void handleHotelSearch() {
    System.out.println("Nome hotel:");
    nomehotel = scanner.nextLine();
    System.out.println("Città:");
    citta = scanner.nextLine();
    commandsHandler.searchHotel(nomehotel, citta);
  }

  private void handleAllHotelsSearch() {
    System.out.println("Città:");
    citta = scanner.nextLine();
    commandsHandler.serchAllHotels(citta);
  }

  private void handleReviewInsertion() {
    System.out.println("Nome hotel:");
    nomehotel = scanner.nextLine();
    System.out.println("Città:");
    citta = scanner.nextLine();
    if (commandsHandler.tryInsertReview(nomehotel, citta) == 0) {
      System.out.println("Hotel o città non trovati.");
      return;
    }
    collectReviewDetails();
  }

  private void collectReviewDetails() {
    System.out.println("Punteggio globale:");
    int globalScore = Integer.parseInt(scanner.nextLine());
    int[] scores = new int[4];
    String[] scoreNames = {"Posizione", "Pulizia", "Servizio", "Prezzo"};
    for (int i = 0; i < scores.length; i++) {
      System.out.println("Punteggio " + scoreNames[i] + ":");
      scores[i] = Integer.parseInt(scanner.nextLine());
    }
    commandsHandler.insertReview(globalScore, scores);
  }

  private void handleShowBadges() throws CommunicationException {
    commandsHandler.showMyBadges();
  }

  private boolean check(boolean stato, int comando) {
    // Command checking logic, similar to the original implementation in HOTELIERCustomerClientMain
    return !(comando < -1 || comando > 7 || (!stato && (comando == 3 || comando > 4)) || (stato && (comando == 1 || comando == 2)));
  }
}
