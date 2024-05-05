import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Logger;

/**
 * Classe che gestisce la terminazione ordinata di un server.
 * Si occupa di chiudere la socket del server, aggiornare i file di stato e terminare il pool di thread in modo sicuro.
 */
public class ServerTerminationHandler extends Thread {
  private static final Logger logger = Logger.getLogger(ServerTerminationHandler.class.getName());

  private int maxDelay;
  private ExecutorService threadPool;
  private ServerSocket serverSocket;
  private String userFilePath, hotelFilePath;

  /**
   * Costruttore del gestore di terminazione del server.
   *
   * @param maxDelay Tempo massimo in millisecondi per attendere la terminazione dei thread nel pool prima di forzarla.
   * @param threadPool Pool di thread del server da terminare.
   * @param serverSocket Socket del server da chiudere.
   * @param userFilePath Percorso del file degli utenti da aggiornare.
   * @param hotelFilePath Percorso del file degli hotel da aggiornare.
   */
  public ServerTerminationHandler(int maxDelay, ExecutorService threadPool, ServerSocket serverSocket,
                                  String userFilePath, String hotelFilePath) {
    this.maxDelay = maxDelay;
    this.threadPool = threadPool;
    this.serverSocket = serverSocket;
    this.userFilePath = userFilePath;
    this.hotelFilePath = hotelFilePath;
  }

  /**
   * Metodo run del thread che gestisce la terminazione del server.
   * Esegue l'aggiornamento dei file, chiude la socket del server e tenta di terminare il pool di thread.
   */
  @Override
  public void run() {
    logger.info("[SERVER] Initiating termination...");
    updateFiles();
    closeServerSocket();
    terminateThreadPool();
    logger.info("[SERVER] Termination completed.");
  }

  /**
   * Aggiorna i file degli utenti e degli hotel.
   * Logga un errore in caso di eccezioni durante l'aggiornamento.
   */
  private void updateFiles() {
    try {
      User.aggiorna_utenti(userFilePath);  // Metodo statico per aggiornare i dati degli utenti.
      Hotel.aggiorna_hotel(hotelFilePath); // Metodo statico per aggiornare i dati degli hotel.
    } catch (IOException e) {
      logger.severe(String.format("[SERVER] Error updating files: %s", e.getMessage()));
    }
  }

  /**
   * Chiude la socket del server.
   * Logga un errore se la socket non può essere chiusa correttamente.
   */
  private void closeServerSocket() {
    try {
      serverSocket.close();
    } catch (IOException e) {
      logger.severe(String.format("[SERVER] Error closing server socket: %s", e.getMessage()));
    }
  }

  /**
   * Termina il pool di thread.
   * Prova a terminare il pool ordinatamente e, se necessario, forza la chiusura dopo un certo intervallo di tempo.
   */
  private void terminateThreadPool() {
    threadPool.shutdown();
    try {
      if (!threadPool.awaitTermination(maxDelay, TimeUnit.MILLISECONDS)) {
        threadPool.shutdownNow();
        if (!threadPool.isTerminated()) {
          logger.warning("[SERVER] Not all tasks terminated successfully.");
        }
      }
    } catch (InterruptedException e) {
      threadPool.shutdownNow();
      Thread.currentThread().interrupt();  // Mantenimento dello stato di interruzione.
    }
  }
}
