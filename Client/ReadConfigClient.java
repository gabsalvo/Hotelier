import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
public class ReadConfigClient {
  private static final String CONFIG_FILE = "client.properties";
  private int port;
  private int udpPort;
  private String udpAddress;
  private String hostname;

  /**
   * Metodo che legge il file di configurazione del client.
   * @throws FileNotFoundException se il file non esiste
   * @throws IOException se si verifica un errore durante la lettura
   */

  public ReadConfigClient() throws IOException {
    loadConfig();
  }

  private void loadConfig() throws IOException {
    try (InputStream input = getClass().getResourceAsStream(CONFIG_FILE)) {
      if (input == null) {
        throw new IOException("Configuration file '" + CONFIG_FILE + "' not found in the classpath.");
      }
      Properties prop = new Properties();
      prop.load(input);
      port = Integer.parseInt(prop.getProperty("port"));
      udpPort = Integer.parseInt(prop.getProperty("udpPort"));
      udpAddress = prop.getProperty("udpAddress");
      hostname = prop.getProperty("hostname");
    }
  }
  public int getPort() {
    return port;
  }

  public int getUdpPort() {
    return udpPort;
  }

  public String getUdpAddress() {
    return udpAddress;
  }

  public String getHostname() {
    return hostname;
  }
}
