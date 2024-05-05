import java.util.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

/**
 * La classe Hotel rappresenta un albergo e gestisce le relative informazioni e valutazioni.
 */
public class Hotel {

  private String nome; // Il nome dell'hotel
  static ArrayList<Hotel> listahotel; // Lista statica che contiene tutti gli hotel

  private String nomecitta; // Il nome della città in cui si trova l'hotel
  private int id; // Identificativo numerico unico per l'hotel
  private String descrizione; // Descrizione dell'hotel
  private String telefono; // Numero di telefono dell'hotel
  private String[] servizi; // Array di stringhe dei servizi offerti dall'hotel
  private int rate; // Valutazione complessiva dell'hotel
  private ratings ratings; // Oggetto contenente le valutazioni dettagliate dell'hotel

  // Liste per gestire le valutazioni specifiche e globali
  public transient List<Nupla> GlobalScoreArray; // Lista delle valutazioni globali dell'hotel
  private transient List<Nupla> posizioneArray, puliziaArray, servizioArray, prezzoArray; // Liste delle valutazioni per ciascuna categoria

  public transient Citta citta; // Riferimento all'oggetto Citta associato all'hotel
  private static final Gson gson = new GsonBuilder().setPrettyPrinting().create(); // Oggetto Gson per la serializzazione JSON
  private static final Type hm_type = new TypeToken<ArrayList<Hotel>>(){}.getType(); // Tipo utilizzato per la deserializzazione degli hotel

  /**
   * Costruttore per creare un'istanza di Hotel.
   */
  public Hotel(String nome, String nomecitta, int id, String descrizione, String telefono, String[] servizi, int rate, ratings ratings) {
    this.nome = nome;
    this.nomecitta = nomecitta;
    this.citta = Citta.buildcitta(nomecitta);
    this.id = id;
    this.descrizione = descrizione;
    this.telefono = telefono;
    this.servizi = servizi;
    this.rate = rate;
    this.ratings = ratings;
    this.GlobalScoreArray = new LinkedList<>();
    this.posizioneArray = new LinkedList<>();
    this.puliziaArray = new LinkedList<>();
    this.servizioArray = new LinkedList<>();
    this.prezzoArray = new LinkedList<>();
  }

  /**
   * Metodo statico che tenta di costruire un hotel dal nome e dalla città specificati.
   * Ritorna l'hotel trovato nella lista degli hotel se esiste.
   */
  public static synchronized Hotel buildHotel(String nome, String citta) {
    Hotel hotelnuovo = new Hotel(nome, citta,0,"","", new String [0],0,new ratings(0, 0, 0, 0));
    for (Hotel h : listahotel) {
      if (h.equals(hotelnuovo)) {
        return h; // Restituisce l'istanza uguale trovata
      }
    }

    return null;
  }
  /**
   * Importa la lista degli hotel da un file JSON specificato.
   */
  public static void importadajson(String filenome) {

    try {

      JsonReader reader = new JsonReader(new FileReader(filenome));
      listahotel = gson.fromJson(reader, hm_type);
      if (listahotel == null) {
        listahotel = new ArrayList <Hotel> (Integer.MAX_VALUE);
      }
      reader.close();

    }
    catch(IOException e) {
      System.err.printf("[HOTEL] Errore: %s\n", e.getMessage());
      e.printStackTrace();
    }


    for(Hotel h: listahotel) {
      String nomedicitta = h.getnomecitta();
      //crea un'istanza di Citta corrispondente a nomecitta ed evita che nella lista di Citta vi siano duplicati
      Citta.creacittadajson(nomedicitta);
      h.citta = Citta.buildcitta(nomedicitta);
      h.citta.listaHotel.add(h);

      h.GlobalScoreArray = new LinkedList<>();
      h.GlobalScoreArray.add(new Nupla(null, h.rate));

      h.posizioneArray= new LinkedList<>();
      h.posizioneArray.add(new Nupla(null, h.ratings.posizione));

      h.prezzoArray = new LinkedList<>();
      h.prezzoArray.add(new Nupla(null, h.ratings.prezzo));

      h.puliziaArray = new LinkedList<>();
      h.puliziaArray.add(new Nupla(null, h.ratings.pulizia));

      h.servizioArray = new LinkedList<>();
      h.servizioArray.add(new Nupla(null, h.ratings.servizi));
    }
    //per ogni città ordino gli hotel in base al ranking
    for(Citta c: Citta.listacitta) {
      c.sorthotelincitta();
    }
  }

  /**
   * Aggiorna il file JSON contenente la lista degli hotel.
   */
  public synchronized static void aggiorna_hotel(String nomefile) {
    try {
      JsonWriter jsonWriter = new JsonWriter(new FileWriter(nomefile));

      jsonWriter.setIndent("  ");

      jsonWriter.beginArray(); // Inizia l'array JSON

      for (Hotel h : listahotel) {
        gson.toJson(h, Hotel.class, jsonWriter); // Serializza l'oggetto Hotel nel file JSON
      }

      jsonWriter.endArray(); // Fine dell'array JSON
      jsonWriter.close();
    }
    catch (IOException e) {
      System.err.printf("[HOTEL] Errore: %s\n", e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Metodo che consente di inserire una recensione per un hotel, aggiornando le valutazioni.
   */
  public synchronized boolean [] inserisci_recensione(String citta,
                                                      int GlobalScore, int [] SingleScores, User Utente) {
    //il metodo è synchronized affinché non vengano inserite più recensioni sullo stesso hotel contemporaneamente
    boolean [] res = new boolean[2];
    res[0] = false;
    res[1] = false;
    Citta citta2 = Citta.buildcitta(citta);
    if(citta2==null) {
      return res;
    }
    if(GlobalScore<0 || GlobalScore>5)
      return res;
    for(int i =0;i<SingleScores.length;i++)
      if(SingleScores[i]<0 || SingleScores[i]>5)
        return res;

    //se questo hotel non è in citta restituisco false
    //non è un problema se questa scansione viene fatta da più thread contemporaneamente
    //perché la lista degli hotel di ciascuna citta non viene mai modificata
    if(!citta2.listaHotel.contains(this))
      return res;

    this.GlobalScoreArray.add(new Nupla( Utente, GlobalScore));
    this.posizioneArray.add(new Nupla(Utente, SingleScores[0]));
    this.puliziaArray.add(new Nupla(Utente, SingleScores[1]));
    this.servizioArray.add(new Nupla(Utente, SingleScores[2]));
    this.prezzoArray.add(new Nupla(Utente, SingleScores[3]));

    this.rate = calcola_media(GlobalScoreArray);
    this.ratings.posizione = calcola_media(posizioneArray);
    this.ratings.pulizia = calcola_media(puliziaArray);
    this.ratings.prezzo = calcola_media(prezzoArray);
    this.ratings.servizi = calcola_media(servizioArray);


    //dopo aver salvato la recensione, riordino il ranking della citta dell'hotel
    res[0] = true;
    res[1] = this.citta.sorthotelincitta();
    return res;
  }

  /**
   * Calcola la media delle valutazioni in base ai punteggi delle recensioni.
   */
  private synchronized int calcola_media(List <Nupla> listarecensioni) {
    int dimensione = listarecensioni.size();
    if(dimensione==0)
      return 0;
    int somma =0;

    for(Nupla nupla: listarecensioni) {
      somma+=nupla.getpunteggio();
    }
    return somma/dimensione;
  }


  /**
   * Calcola la media delle date delle recensioni.
   */
  public synchronized long calcola_media_date() {
    List <Nupla> listarecensioni = GlobalScoreArray;
    int dimensione = listarecensioni.size();
    if(dimensione==0)
      return 0;
    long somma = 0;
    for(Nupla nupla : listarecensioni) {
      Date data = nupla.getdata();
      somma+=data.getTime();
    }
    return somma/dimensione;
  }


  /**
   * Restituisce una rappresentazione in formato stringa dell'hotel, includendo nome, città, valutazioni e servizi.
   */
  public synchronized String toString() {

    String res;
    res = "nome hotel: " + nome + "\n";
    res += "citta: " + nomecitta + "\n";
    res += ("punti: "+ rate+"\n");
    res += ("punti posizione: "+ ratings.posizione+"\n");
    res += ("punti pulizia: "+ ratings.pulizia+"\n");
    res += ("punti servizio: "+ ratings.servizi+"\n");
    res += ("punti prezzo: "+ ratings.prezzo+"\n");

    int posizioneranking = this.citta.getindice(this);
    res+= ("posizione ranking: "+ posizioneranking + "\n") ;
    res+=("descrizione: " + this.descrizione+"\n");
    res+=("telefono: " + this.telefono+"\n");
    if(this.servizi.length!=0) {
      res+="servizi:";
      for(String servizio : servizi) {
        res+=(" "+ servizio);
      }
      res+="\n";
    }
    res+="\n";
    return res;
  }

  /**
   * Confronta questo Hotel con un altro oggetto per determinare se sono uguali.
   */
  public boolean equals (Hotel h) {
    return this.nome.equals(h.nome) && this.citta.equals(h.citta);
  }

  public boolean equals (Object h) {
    if(h instanceof Hotel)
      return equals((Hotel) h);
    else return false;
  }

  /**
   * Ottiene il punteggio totale dell'hotel.
   */
  public int getpunti() {
    return this.rate;
  }

  /**
   * Ottiene il nome dell'hotel.
   */
  public String getnome() {
    return this.nome;
  }

  /**
   * Ottiene il nome della città dell'hotel.
   */
  public String getnomecitta() {
    return this.nomecitta;
  }

  /**
   * Classe interna per gestire le valutazioni specifiche dell'hotel.
   */
  private static class ratings {
    public int pulizia, posizione, servizi, prezzo;
    public ratings(int pulizia, int posizione, int servizi, int prezzo) {
      this.pulizia = pulizia;
      this.posizione = posizione;
      this.servizi = servizi;
      this.prezzo = prezzo;
    }
  }
}

/**
 * Classe Nupla utilizzata per gestire i dati delle recensioni degli utenti.
 */
class Nupla {
  private Date data;
  private User utente;
  private double punteggio;

  public Nupla(User utente, int punteggio) {
    this.data = new Date();
    this.utente = utente;
    this.punteggio = punteggio;
  }

  /**
   * Ottiene il punteggio della recensione.
   */
  public double getpunteggio() {
    return this.punteggio;
  }

  /**
   * Ottiene la data della recensione.
   */
  public Date getdata() {
    return this.data;
  }

  /**
   * Ottiene l'utente che ha lasciato la recensione.
   */
  public User getutente() {
    return this.utente;
  }
}
