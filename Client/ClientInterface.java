public class ClientInterface {
  /**
   * Visualizza il menu dell'utente sulla console in base allo stato di login.
   * @param isLoggedIn stato di login dell'utente, true se l'utente è loggato.
   */
  public static void displayMenu(boolean isLoggedIn) {
    String header = "\033[1;33m🌟🌟🌟 Benvenuti al Nostro Servizio Hotel 🌟🌟🌟\033[0m";
    String divider = "\033[0;34m-------------------------------------------------\033[0m";

    if (isLoggedIn) {
      System.out.println(header);
      System.out.println(divider);
      System.out.println("\033[0;32m3 - 🔓 Effettua il Log Out\033[0m");
      System.out.println("\033[0;32m4 - 🔍 Cerca un hotel in una città\033[0m");
      System.out.println("\033[0;32m5 - 🏨 Visualizza tutti gli hotel in una città\033[0m");
      System.out.println("\033[0;32m6 - 🖋️ Invia una recensione\033[0m");
      System.out.println("\033[0;32m7 - 🎖️ Visualizza i tuoi distintivi\033[0m");
      System.out.println("\033[1;31m-1 - 🚪 Esci\033[0m");
      System.out.println(divider);
      System.out.println("\033[1;37mScegli un'opzione:\033[0m");
    } else {
      System.out.println(header);
      System.out.println(divider);
      System.out.println("\033[0;32m1 - 📝 Registrati\033[0m");
      System.out.println("\033[0;32m2 - 🔑 Effettua il Log In\033[0m");
      System.out.println("\033[0;32m4 - 🔍 Cerca un hotel in una città\033[0m");
      System.out.println("\033[1;31m-1 - 🚪 Esci\033[0m");
      System.out.println(divider);
      System.out.println("\033[1;37mScegli un'opzione:\033[0m");
    }
   }
  }

