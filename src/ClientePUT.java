import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class ClientePUT {

    public static void main (String[] args){

        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);
        String fileName = args[2];

        // Leemos el archivo del disco local
        File file = new File(fileName);
        byte[] fileContent = new byte[(int) file.length()];
        try {
            FileInputStream fis = new FileInputStream(file);
            fis.read(fileContent);
            fis.close();
        } catch (IOException e) {
            System.out.println("Error al leer el archivo del disco local");
            return;
        }

        // Creamos el socket seguro y nos conectamos al servidor
        System.setProperty("javax.net.ssl.trustStore","C:/Users/kevin/OneDrive - Instituto Politecnico Nacional/Documents/Escom/SistemasDistribuidos/Tarea4/src/keystore_cliente.jks");
        System.setProperty("javax.net.ssl.trustStorePassword","1234567");
        SSLSocketFactory cliente = (SSLSocketFactory) SSLSocketFactory.getDefault();
        Socket conexion;
        try {
            conexion = cliente.createSocket(serverIP, serverPort);
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            System.out.println("Conexion realizada con el servidor. Enviando archivo...");

            // Enviamos la petición PUT al servidor con el nombre del archivo, la longitud y el contenido
            salida.writeUTF("PUT " + file.getName() + " " + file.length());
            salida.write(fileContent);

            // Esperamos la respuesta del servidor
            String response = entrada.readUTF();

            // Mostramos el resultado al usuario
            if (response.equals("OK")) {
                System.out.println("El archivo fue enviado al servidor con exito");
            } else {
                System.out.println("Error al enviar el archivo al servidor");
            }

            conexion.close();
        } catch (IOException e) {
            System.out.println("Error al conectarse al servidor");
            e.printStackTrace();
        }
    }
}

