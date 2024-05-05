import java.util.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.*;

/**
 * La classe Citta gestisce le informazioni relative alle città e agli hotel in esse contenuti.
 */
public class Citta {

  private String nomecitta; // Nome della città
  public LinkedList<Hotel> listaHotel; // Lista degli hotel presenti nella città

  public static ArrayList<Citta> listacitta = new ArrayList<>(20); // Lista statica di tutte le città

  /**
   * Costruttore che inizializza una città con il nome specificato e una nuova lista di hotel.
   * @param nomecitta Il nome della città da creare.
   */
  public Citta(String nomecitta) {
    this.nomecitta = nomecitta;
    this.listaHotel = new LinkedList<>();
  }

  /**
   * Metodo statico per creare una città e aggiungerla alla lista statica delle città se non esiste già.
   * @param nomecitta Il nome della città da aggiungere.
   */
  public static void creacittadajson(String nomecitta) {
    Citta citta = new Citta(nomecitta);
    for (Citta c : listacitta) {
      if (c.equals(citta))
        return;
    }
    listacitta.add(citta);
  }

  /**
   * Metodo statico che cerca una città nella lista statica per nome e la restituisce se esiste.
   * @param nomecitta Il nome della città da cercare.
   * @return Citta se trovata, altrimenti null.
   */
  public static Citta buildcitta(String nomecitta) {
    for (Citta c : listacitta) {
      if (c.getnomecitta().equals(nomecitta))
        return c;
    }
    return null;
  }

  /**
   * Comparator locale per ordinare gli hotel all'interno di una città basandosi su vari criteri di ranking.
   */
  static class LocalComparator implements Comparator<Hotel> {
    @Override
    public int compare(Hotel h1, Hotel h2) {
      double mediarankingh1 = h1.getpunti();
      double mediarankingh2 = h2.getpunti();

      if (mediarankingh1 == mediarankingh2) {
        if (h1.GlobalScoreArray.size() == h2.GlobalScoreArray.size()) {
          return (int) (h1.calcola_media_date() - h2.calcola_media_date());
        } else {
          return (int) (h2.getpunti() - h1.getpunti());
        }
      } else {
        return (int) (mediarankingh2 - mediarankingh1);
      }
    }
  }

  /**
   * Confronta l'oggetto Citta corrente con un altro oggetto Citta.
   * @param c L'oggetto Citta da confrontare.
   * @return true se i nomi delle città sono uguali, false altrimenti.
   */
  public boolean equals(Citta c) {
    if (c == null)
      return false;
    return this.nomecitta.equals(c.getnomecitta());
  }

  @Override
  public boolean equals(Object c) {
    if (c instanceof Citta)
      return equals((Citta) c);
    else
      return false;
  }

  /**
   * Ordina gli hotel nella città basandosi sui ranking e determina se l'hotel al primo posto è cambiato.
   * @return true se l'hotel al primo posto è cambiato, false altrimenti.
   */
  public synchronized boolean sorthotelincitta() {
    Hotel head = listaHotel.getFirst();
    Collections.sort(listaHotel, new LocalComparator());
    Hotel newhead = listaHotel.getFirst();
    return !head.equals(newhead);
  }

  /**
   * Restituisce l'indice di un hotel nella lista ordinata della città.
   * @param hotel L'hotel di cui si vuole conoscere l'indice.
   * @return l'indice dell'hotel nella lista, -1 se l'hotel non è presente.
   */
  public synchronized int getindice(Hotel hotel) {
    int i = 1;
    for (Hotel h : listaHotel) {
      if (h.equals(hotel))
        return i;
      i++;
    }
    return -1;
  }

  /**
   * Restituisce una rappresentazione in formato stringa della lista degli hotel di una città.
   * @return Una stringa che elenca tutti gli hotel nella città.
   */
  public String hotelincittatoString() {
    StringBuilder res = new StringBuilder();
    for (Hotel hotel : listaHotel) {
      res.append(hotel.toString());
      res.append("\n");
    }
    return res.length() == 0 ? "NON CI SONO HOTEL IN " + this.getnomecitta() : res.toString();
  }

  /**
   * Restituisce il nome della città.
   * @return Il nome della città.
   */
  public String getnomecitta() {
    return this.nomecitta;
  }
}
