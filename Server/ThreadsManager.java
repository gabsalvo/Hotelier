import java.util.concurrent.atomic.AtomicInteger;

/**
 * Gestisce l'accesso concorrente di lettori e scrittori usando un algoritmo di controllo per evitare condizioni di race e deadlock.
 */
public class ThreadsManager {
  // Contatori atomici per gestire il numero di lettori e scrittori in attesa e attivi.
  private static AtomicInteger lettoriinattesa = new AtomicInteger(0);
  private static AtomicInteger scrittoriinattesa = new AtomicInteger(0);
  private static AtomicInteger lettoriattivi = new AtomicInteger(0);
  private static AtomicInteger scrittoriattivi = new AtomicInteger(0);

  // Variabile di condizione per sincronizzare l'accesso alle risorse.
  public static Object cv = new Object();

  /**
   * Metodo per acquisire l'accesso in lettura. I lettori devono attendere se un scrittore è attivo.
   */
  public static void acquisiscilettura() {
    lettoriinattesa.incrementAndGet(); // Incrementa il contatore di lettori in attesa.
    synchronized(cv) {
      while(scrittoriattivi.get() > 0) { // Attende finché ci sono scrittori attivi.
        try {
          cv.wait(); // Aspetta che la risorsa diventi disponibile.
        } catch(InterruptedException e) {
          System.err.printf("[SERVER] Errore: %s\n", e.getMessage());
          e.printStackTrace();
        }
      }
    }
    lettoriinattesa.decrementAndGet(); // Decrementa il contatore di lettori in attesa.
    lettoriattivi.incrementAndGet(); // Incrementa il contatore di lettori attivi.
  }

  /**
   * Metodo per rilasciare l'accesso in lettura.
   */
  public static void rilascialettura() {
    synchronized(cv) {
      lettoriattivi.decrementAndGet(); // Decrementa il contatore di lettori attivi.
      cv.notifyAll(); // Notifica tutti i thread in attesa che la risorsa è disponibile.
    }
  }

  /**
   * Metodo per acquisire l'accesso in scrittura. Gli scrittori devono attendere se altri scrittori o lettori sono attivi.
   */
  public static void acquisisciscrittura() {
    scrittoriinattesa.incrementAndGet(); // Incrementa il contatore di scrittori in attesa.
    synchronized(cv) {
      while(scrittoriattivi.get() > 0 || lettoriattivi.get() > 0) { // Attende finché ci sono altri scrittori o lettori attivi.
        try {
          cv.wait(); // Aspetta che la risorsa diventi disponibile.
        } catch (Exception e) {
          System.err.printf("[SERVER] Errore: %s\n", e.getMessage());
          e.printStackTrace();
        }
      }
      scrittoriattivi.incrementAndGet(); // Incrementa il contatore di scrittori attivi.
    }
    scrittoriinattesa.decrementAndGet(); // Decrementa il contatore di scrittori in attesa.
  }

  /**
   * Metodo per rilasciare l'accesso in scrittura.
   */
  public static void rilasciascrittura() {
    synchronized(cv) {
      scrittoriattivi.decrementAndGet(); // Decrementa il contatore di scrittori attivi.
      cv.notifyAll(); // Notifica tutti i thread in attesa che la risorsa è disponibile.
    }
  }
}
