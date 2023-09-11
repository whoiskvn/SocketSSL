import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class ServidorSSL {

    public static void main(String[] args){
        System.setProperty("javax.net.ssl.keyStore","C:/Users/kevin/keystore_servidor.jks");
        System.setProperty("javax.net.ssl.keyStorePassword","1234567");
        SSLServerSocketFactory socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        ServerSocket socket_servidor;

        try {
            socket_servidor = socket_factory.createServerSocket(50000);
            while (true) {
                Socket conexion = socket_servidor.accept();
                System.out.println("Cliente conectado: " + conexion.getInetAddress().getHostName());
                Thread cliente = new ClienteThread(conexion);
                cliente.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClienteThread extends Thread {

    private Socket conexion;

    public ClienteThread(Socket conexion) {
        this.conexion = conexion;
    }

    @Override
    public void run() {
        try {
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            String peticion = entrada.readUTF();
            String[] partes_peticion = peticion.split(" ");
            String tipo_peticion = partes_peticion[0];
            String nombre_archivo = partes_peticion[1];
            File archivo = new File(nombre_archivo);
            if (tipo_peticion.equals("PUT")) {
                long longitud_archivo = Long.parseLong(partes_peticion[2]); // obtener la longitud del archivo del mensaje
                byte[] buffer = new byte[4096]; // Aumentar tamaño del buffer
                int leidos = 0;
                FileOutputStream archivo_salida = new FileOutputStream(archivo);
                while (longitud_archivo > 0 && (leidos = entrada.read(buffer, 0, (int) Math.min(buffer.length, longitud_archivo))) != -1) {
                    archivo_salida.write(buffer, 0, leidos);
                    longitud_archivo -= leidos;
                }
                archivo_salida.close();
                if (longitud_archivo == 0 && archivo.length() == Long.parseLong(partes_peticion[2])) { // verificar si la escritura fue exitosa
                    salida.writeUTF("OK");
                    System.out.println("Archivo recibido con éxito: " + archivo.getAbsolutePath());
                } else {
                    salida.writeUTF("ERROR");
                    System.out.println("Error al recibir archivo: " + archivo.getAbsolutePath());
                }
            } else if (tipo_peticion.equals("GET")) {
                if (archivo.exists() && archivo.isFile()) {
                    FileInputStream archivo_entrada = new FileInputStream(archivo);
                    byte[] buffer = new byte[1024];
                    int leidos = 0;
                    salida.writeUTF("OK");
                    salida.writeLong(archivo.length());
                    while ((leidos = archivo_entrada.read(buffer)) > 0) {
                        salida.write(buffer, 0, leidos);
                    }
                    archivo_entrada.close();
                    System.out.println("Archivo enviado con éxito: " + archivo.getAbsolutePath());
                } else {
                    salida.writeUTF("ERROR");
                    System.out.println("Error al enviar archivo: " + archivo.getAbsolutePath());
                }
            }
            conexion.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

