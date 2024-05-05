import javax.naming.CommunicationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ClientCommandsHandler {

  private static BufferedReader in;
  private static PrintWriter out;

  public ClientCommandsHandler(BufferedReader in, PrintWriter out) {
    this.in = in;
    this.out = out;
  }

  /**
   * @param username
   * @param password
   * stampa su schermo se l'operazione è andata a buon fine o no
   */
  public void register(String username, String password) {
    if(password.isEmpty()) {
      System.out.println("Registrazione non riuscita");
      return;
    }

    try {
      //comunico al server che il client vuole registrarsi
      out.println(1);
      //spedisco il nome utente
      out.println(username);
      //spedisco la password
      out.println(password);

      String reply = in.readLine();
      String[] parts = reply.split(",");
      int res = Integer.parseInt(parts[0]);
      if(res==1)
        System.out.println("Utente registrato correttamente");
      else
        System.out.println("Registrazione non riuscita");
    }
    catch(IOException e) {
      System.err.printf("[CLIENT] Errore: %s\n", e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * stampa su schermo se l'operazione è andata a buon fine o no.
   * restituisce un booleano perché il suo risultato sarà assegnato alla variabile stato.
   * stato vale true se l'utente è loggato, false altrimenti.
   * @param username
   * @param password
   * @return true se il login è avvenuto con successo, false altrimenti
   */
  public boolean login(String username, String password) {
    int res =0;
    try {
      out.println(2);
      //spedisco il nome utente
      out.println(username);
      //spedisco la password
      out.println(password);

      String reply = in.readLine();
      String[] parts = reply.split(",");
      res = Integer.parseInt(parts[0]);

    }
    catch(IOException e) {
      System.err.printf("[CLIENT] Errore: %s\n", e.getMessage());
      e.printStackTrace();
    }
    if(res==1) {
      System.out.println("Accesso riuscito");
      return true;
    }
    else {
      System.out.println("Accesso non riuscito");
      return false;
    }
  }

  /**
   * stampa su schermo se l'operazione è andata a buon fine o no.
   * restituisce un booleano perché il suo risultato sarà assegnato alla variabile stato.
   * stato vale true se l'utente è loggato, false altrimenti.
   * @param username
   * @return false se il logout è avvenuto con successo, true altrimenti.
   *
   */
  public boolean logout(String username) {
    int res=0;
    try {
      out.println(3);
      //spedisco il nome utente
      out.println(username);

      String reply = in.readLine();
      String[] parts = reply.split(",");
      res = Integer.parseInt(parts[0]);
    }
    catch(IOException e) {
      System.err.printf("[CLIENT] Errore: %s\n", e.getMessage());
      e.printStackTrace();
    }
    if(res==1) {

      System.out.println("Log-out riuscito");
      return false;
    }
    else {
      System.out.println("Log-out non riuscito");
      return true;
    }
  }

  /**
   * Si limita a spedire al server il nome dell'hotel e la rispettiva città.
   * Si assicura di ricevere la completa descrizione dell'hotel richiesta e la stampa su terminale.
   * @param nomeHotel
   * @param città
   */
  public void searchHotel(String nomeHotel, String città) {
    out.println(4);
    //spedisco il nomeHotel
    out.println(nomeHotel);
    //spedisco la città
    out.println(città);

    leggihotel();
  }

  /**
   * Spedisce al server il nome della città di cui l'utente vuole visionare il ranking degli hotel.
   * Stampa su terminale quanto ricevuto.
   * @param città
   */
  public void serchAllHotels(String città) {
    out.println(5);

    //spedisco la città
    out.println(città);

    leggihotel();
  }

  /**
   * Si occupa di leggere da socket la descrizione di uno o pià hotel
   */
  private void leggihotel() {
    try {
      int dimensione = Integer.parseInt(in.readLine());
      StringBuilder res = new StringBuilder();
      int letti = 0;
      String line;

      while(letti<dimensione-2) {
        line = in.readLine();
        if(line==null)
          break;
        res.append(line);
        res.append("\n");
        letti+=line.length()+1;

      }

      System.out.println(res);

    } catch( IOException e) {
      System.err.printf("[CLIENT] Errore: %s\n", e.getMessage());
      e.printStackTrace();
    }

  }
  /**
   * spedisce al server nomehotel e citta per verificare se l'hotel che si vuole recensire esiste
   * @param nomehotel
   * @param nomeCittà
   * @return 1 se l'hotel che si vuole recensire esiste, 0 altrimenti
   */
  public int tryInsertReview(String nomehotel, String nomeCittà) {
    int res=0;
    try {
      out.println(6);
      //spedisco il nomeHotel
      out.println(nomehotel);
      //spedisco la città
      out.println(nomeCittà);
      String reply = in.readLine();
      String[] parts = reply.split(",");
      res = Integer.parseInt(parts[0]);
      if(res==0)
        System.out.println("Hotel o Città non trovati");
      return res;

    }
    catch(IOException e) {
      System.err.printf("[CLIENT] Errore: %s\n", e.getMessage());
      e.printStackTrace();
    }
    return res;
  }

  /**
   * Comunica al server il punteggio da attribuire all'hotel comunicato con tryInsertReview
   * Stampa su terminale se la registrazione della recensione è avvenuta con successo o no.
   * @param GlobalScore
   * @param SingleScores
   */

  public void insertReview(int GlobalScore, int [] SingleScores ) {
    try {

      out.println(GlobalScore);
      for(int score: SingleScores)
        out.println(score);

      String reply = in.readLine();
      String[] parts = reply.split(",");
      int res = Integer.parseInt(parts[0]);
      if(res==1)
        System.out.println("Recensione registrata con successo");
      else
        System.out.println("Recensione non registrata");
    }
    catch(IOException e) {
      System.err.printf("[CLIENT] Errore: %s\n", e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Comunica al server che l'utente vuole visualizzare il proprio distintivo.
   * Riceve il tipo di distintivo sottoforma di intero, lo decodifica e stampa il tipo di distintivo su terminale.
   * @throws CommunicationException
   */
  public void showMyBadges() throws CommunicationException {
    try {
      out.println(7);
      String reply = in.readLine();
      String[] parts = reply.split(",");
      int livello = Integer.parseInt(parts[0]);

      switch(livello) {
        case 1:
          System.out.println("Recensore");
          break;
        case 2:
          System.out.println("Recensore esperto");
          break;
        case 3:
          System.out.println("Contributore");
          break;
        case 4:
          System.out.println("Contributore esperto");
          break;
        case 5:
          System.out.println("Contributore super");
          break;
        default:
          throw new CommunicationException();
      }
    }
    catch(IOException e) {
      System.err.printf("[CLIENT] Errore: %s\n", e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * comunica al server che l'utente vuole terminare la connessione.
   */
  public static void goodbye() {
    out.println(-1);
  }

}
