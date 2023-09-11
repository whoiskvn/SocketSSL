import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class ClienteGET {

    public static void main(String[] args) {

        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);
        String fileName = args[2];

        // Creamos el socket seguro y nos conectamos al servidor
        System.setProperty("javax.net.ssl.trustStore", "C:/Users/kevin/OneDrive - Instituto Politecnico Nacional/Documents/Escom/SistemasDistribuidos/Tarea4/src/keystore_cliente.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "1234567");
        SSLSocketFactory cliente = (SSLSocketFactory) SSLSocketFactory.getDefault();
        Socket conexion;
        try {
            conexion = cliente.createSocket(serverIP, serverPort);
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            System.out.println("Conexion realizada con el servidor. Solicitando archivo...");

            // Enviamos la petición GET al servidor con el nombre del archivo
            salida.writeUTF("GET " + fileName);

            // Esperamos la respuesta del servidor
            String response = entrada.readUTF();
            if (response.equals("ERROR")) {
                System.out.println("Error al solicitar el archivo al servidor");
                conexion.close();
                return;
            }

            // Recibimos el archivo del servidor y lo guardamos en disco local
            FileOutputStream fos = new FileOutputStream(new File("GET_"+fileName));
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = entrada.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.close();

            System.out.println("El archivo " + fileName + " fue descargado del servidor con exito");

            conexion.close();
        } catch (IOException e) {
            System.out.println("Error al conectarse al servidor");
            e.printStackTrace();
        }
    }
}
