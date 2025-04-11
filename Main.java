import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class Main {
    private static final String ARCHIVO_LOG = "registro_contrasenas.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExecutorService executor = Executors.newFixedThreadPool(5); // 5 hilos

        while (true) {
            System.out.print("Ingrese una contraseña (o escriba 'salir' para terminar): ");
            String contrasena = scanner.nextLine();

            if (contrasena.equalsIgnoreCase("salir")) break;

            executor.execute(() -> {
                boolean esValida = validarContrasena(contrasena);
                String resultado = contrasena + " -> " + (esValida ? "VÁLIDA" : "INVÁLIDA");
                System.out.println(resultado);
                escribirEnArchivo(resultado);
            });
        }

        executor.shutdown();
        System.out.println("Validación finalizada.");
        scanner.close();
    }

    private static boolean validarContrasena(String contrasena) {
        Pattern longitud = Pattern.compile(".{8,}");
        Pattern especiales = Pattern.compile("[!@#$%^&*()_+\\-=\\{};':\"\\\\|,.<>/?]");
        Pattern mayusculas = Pattern.compile(".*[A-Z].*[A-Z].*");
        Pattern minusculas = Pattern.compile(".*[a-z].*[a-z].*[a-z].*");
        Pattern numeros = Pattern.compile(".*\\d.*");

        return longitud.matcher(contrasena).matches()
                && especiales.matcher(contrasena).find()
                && mayusculas.matcher(contrasena).matches()
                && minusculas.matcher(contrasena).matches()
                && numeros.matcher(contrasena).find();
    }

    private static void escribirEnArchivo(String texto) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fechaHora = LocalDateTime.now().format(formato);
        String entradaLog = fechaHora + " - " + texto;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_LOG, true))) {
            writer.write(entradaLog);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }
}
