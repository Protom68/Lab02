import javax.swing.*;
import java.io.*;
import java.lang.ref.WeakReference;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PolaczonaAplikacja {
    private static final String NAGŁÓWEK_CSV = "Czas pomiaru;Ciśnienie [hPa];Temperatura [°C];Wilgotność [%]";

    public static void main(String[] args) {
        // Wywołanie metody do archiwizacji plików
        String[] plikiDoArchiwizacji = {"plik1.txt", "plik2.txt"}; // Przykładowe pliki do archiwizacji
        String nazwaZipa = "zarchiwizowane_pliki.zip";
        try {
            archiwizujPliki(plikiDoArchiwizacji, nazwaZipa); // Wywołanie metody archiwizacji
        } catch (IOException e) {
            e.printStackTrace(); // Obsługa błędów wejścia-wyjścia
        }

        // Wywołanie metody do przeglądania danych
        SwingUtilities.invokeLater(PolaczonaAplikacja::utwórzIUWyświetl); // Uruchomienie interfejsu użytkownika

        // Pobieranie danych pogodowych
        LocalDateTime czasPomiaru = LocalDateTime.now();
        double ciśnienie = mierzCiśnienie(); // Pomiar ciśnienia (np. z czujnika)
        double temperatura = mierzTemperaturę(); // Pomiar temperatury (np. z czujnika)
        double wilgotność = mierzWilgotność(); // Pomiar wilgotności (np. z czujnika)

        // Zapis do pliku CSV
        String ścieżkaDoCSV = "dane_pogodowe.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ścieżkaDoCSV))) {
            writer.write(NAGŁÓWEK_CSV); // Zapis nagłówka CSV
            writer.newLine();
            String sformatowanyCzas = czasPomiaru.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // Formatowanie czasu
            String liniaDanych = String.format("%s;%f;%f;%f", sformatowanyCzas, ciśnienie, temperatura, wilgotność); // Formatowanie danych
            writer.write(liniaDanych); // Zapis danych do pliku
            writer.newLine();
            System.out.println("Dane pogodowe zostały zapisane do pliku: " + ścieżkaDoCSV); // Potwierdzenie zapisu danych
        } catch (IOException e) {
            e.printStackTrace(); // Obsługa błędów wejścia-wyjścia
        }
    }

    // Metoda archiwizacji plików
    public static void archiwizujPliki(String[] pliki, String nazwaZipa) throws IOException {
        List<WeakReference<FileInputStream>> inputStreams = new ArrayList<>();
        try (FileOutputStream fos = new FileOutputStream(nazwaZipa);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            for (String plik : pliki) {
                WeakReference<FileInputStream> inputStreamRef = new WeakReference<>(new FileInputStream(plik));
                inputStreams.add(inputStreamRef); // Dodanie słabej referencji do listy
                dodajDoZipa(inputStreamRef.get(), plik, zos); // Dodanie plików do archiwum
            }
        }
    }

    // Metoda dodająca plik do archiwum ZIP
    public static void dodajDoZipa(FileInputStream fis, String nazwaPliku, ZipOutputStream zos) throws IOException {
        File plik = new File(nazwaPliku);
        ZipEntry zipEntry = new ZipEntry(plik.getName()); // Utworzenie nowego wpisu w archiwum ZIP
        zos.putNextEntry(zipEntry); // Dodanie wpisu do archiwum
        byte[] bajty = new byte[1024];
        int długość;
        while ((długość = fis.read(bajty)) >= 0) {
            zos.write(bajty, 0, długość); // Zapis danych do archiwum
        }
        zos.closeEntry(); // Zamknięcie wpisu w archiwum
    }

    // Metoda pomiaru ciśnienia
    private static double mierzCiśnienie() {
        // Symulacja pomiaru ciśnienia
        return 1013.25; // Przykładowa wartość ciśnienia
    }

    // Metoda pomiaru temperatury
    private static double mierzTemperaturę() {
        // Symulacja pomiaru temperatury
        return 20.5; // Przykładowa temperatura w stopniach Celsiusza
    }

    // Metoda pomiaru wilgotności
    private static double mierzWilgotność() {
        // Symulacja pomiaru wilgotności
        return 60.0; // Przykładowa wilgotność w procentach
    }

    // Metoda tworząca interfejs przeglądarki danych
    private static void utwórzIUWyświetl() {
        JFrame ramka = new JFrame("Przeglądarka Danych");
        ramka.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel etykietaNawigacji = new JLabel("Panel Nawigacji");
        panel.add(etykietaNawigacji);

        JLabel etykietaWyświetlaniaDanych = new JLabel("Panel Wyświetlania Danych");
        panel.add(etykietaWyświetlaniaDanych);

        ramka.getContentPane().add(panel);
        ramka.pack();
        ramka.setVisible(true);

        // Dodanie nasłuchiwacza zdarzeń zamknięcia ramki, aby zwolnić zasoby
        ramka.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                // Zwolnienie zasobów interfejsu użytkownika
                ramka.dispose();
            }
        });
    }
}
