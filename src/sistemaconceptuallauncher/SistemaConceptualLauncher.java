/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package sistemaconceptuallauncher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import papeleria.Papeleria;

/**
 *
 * @author heber
 */
public class SistemaConceptualLauncher {

    /**
     * @param args the command line arguments
     */
    private static boolean actualizado = true;
    private static Progreso progreso = new Progreso();

    public static void main(String[] args) {
        System.out.println("INICIALIZANDO EL PROGRAMA");
        try {
            File papejar = new File("./lib/Papeleria.jar");
            if (existe(papejar)) {
                verificarIntegridad();
            } else {
                descargarJar();
                reiniciarLauncher();
            }
        } catch (Exception ex) {
            Logger.getLogger(SistemaConceptualLauncher.class.getName()).log(Level.SEVERE, null, ex);
            reiniciarLauncher();

        } finally {
            try {
                if (actualizado) {
                    progreso.dispose();
                    Runtime.getRuntime().exec("java -jar ./lib/Papeleria.jar");
                } else {
                    reiniciarLauncher();
                }
            } catch (IOException ex) {
                Logger.getLogger(SistemaConceptualLauncher.class.getName()).log(Level.SEVERE, null, ex);
            }

            File integridad = new File("Integridad.txt");
            System.out.println("Se elimino el archivo de Integridad: " + integridad.delete());
        }
    }

    private static void verificarIntegridad() throws IOException {
        try {
            System.out.println("VERSION LOCAL: " + Papeleria.getVersion());
            System.out.println("\nVerificando Integridad: \n");

            File directorio = new File("./lib/lib");
            if (!directorio.exists()) {
                if (directorio.mkdirs()) {
                    System.out.println("Directorio de dependencias creado");
                } else {
                    System.out.println("Error al crear directorio");
                }
            }

            descargarDependecias("../Integridad.txt", "https://hazihell.xyz/java/Integridad.txt");
            List<String> urls = leerArchivo("Integridad.txt");
            String versionEnServer = urls.get(0);
            System.out.println("VERSION EN NUBE: " + versionEnServer);
            String versionLocal = "";

            versionLocal = Papeleria.getVersion();

            //System.out.println("|"+ versionEnServer + "|" + versionLocal + "|");
            if (!versionEnServer.equals(versionLocal)) {
                int decision = JOptionPane.showConfirmDialog(null, "Hay una actualización diponible\n¿Desea descargarla ahora?", "Actualización", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (decision == JOptionPane.YES_OPTION) {
                    descargarJar();

                }
            }

            File archivo;
            for (int i = 2; i < urls.size(); i++) {
                archivo = new File("./lib/" + urls.get(i));
                // System.out.println("./lib/" + urls.get(i));
                if (!existe(archivo)) {
                    //actualizado = false;
                    System.out.println("\nfalta dependecia: " + getName(urls.get(i)));
                    descargarDependecias(urls.get(i), "https://hazihell.xyz/java/" + urls.get(i));
                }
            }
        } catch (NoSuchMethodError err) {
            descargarJar();
            reiniciarLauncher();
        } catch (NoClassDefFoundError err) {
            descargarJar();
            reiniciarLauncher();
        }

    }

    public static boolean existe(File archivo) throws IOException {
        FileReader fr = null;

        try {
            fr = new FileReader(archivo);
        } catch (FileNotFoundException ex) {
            return false;
        } finally {
            // En el finally cerramos el fichero, para asegurarnos
            // que se cierra tanto si todo va bien como si salta 
            // una excepcion.
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return true;
    }

    public static void descargarDependecias(String name, String url) throws MalformedURLException, IOException {

        File file = new File("./lib/" + name);

        //Establece la conexion con la url mediante una clase URLConnection
        URLConnection conn = new URL(url).openConnection();
        conn.connect();

        System.out.println("\n>> URL: " + url);
        System.out.println(">> Nombre: " + name);
        System.out.println(">> tamaño: " + conn.getContentLength() + " bytes");

        InputStream in = conn.getInputStream();

        OutputStream out = new FileOutputStream(file);

        int b = 0;

        //se descarga el archivo
        if (name != "../Integridad.txt") {
            progreso.setDetalles(name, conn.getContentLength());
            progreso.setVisible(true);
        }
        for (int j = 0; j <= conn.getContentLength(); j++) {
            b = in.read();
            progreso.setProgreso(j);
            if (b != -1) {
                out.write(b);
            }
        }

        out.close();
        in.close();
    }

    public static void descargarJar() throws MalformedURLException, IOException {
        System.out.println("\nSE DESCARGARA EL JAR PRINCIPAL");
        File directorio = new File("./lib");
        if (!directorio.exists()) {
            if (directorio.mkdirs()) {
                System.out.println("Directorio de dependencias creado");
            } else {
                System.out.println("Error al crear directorio");
            }
        }

        String name = "./lib/Papeleria.jar";
        String url = "https://hazihell.xyz/java/Papeleria.jar";

        File file = new File(name);

        //Establece la conexion con la url mediante una clase URLConnection
        URLConnection conn = new URL(url).openConnection();
        conn.connect();

        System.out.println(">> URL: " + url);
        System.out.println(">> Nombre: " + name);
        System.out.println(">> tamaño: " + conn.getContentLength() + " bytes");

        InputStream in = conn.getInputStream();

        OutputStream out = new FileOutputStream(file);

        int b = 0;

        //se descarga el archivo
        progreso.setDetalles(name, conn.getContentLength());
        progreso.setVisible(true);
        for (int j = 0; j <= conn.getContentLength(); j++) {
            b = in.read();
            progreso.setProgreso(j);
            if (b != -1) {
                out.write(b);
            }
        }

        System.out.println("\nSE HA DESCARGADO EL JAR");

        out.close();
        in.close();
    }

    public static List<String> leerArchivo(String ruta) {
        List<String> urls = new ArrayList<String>();
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;

        try {
            // Apertura del fichero y creacion de BufferedReader para poder
            // hacer una lectura comoda (disponer del metodo readLine()).
            archivo = new File(ruta);
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);

            // Lectura del fichero
            String linea;
            while ((linea = br.readLine()) != null) {
                urls.add(linea);
                //System.out.println(linea);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // En el finally cerramos el fichero, para asegurarnos
            // que se cierra tanto si todo va bien como si salta 
            // una excepcion.
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        return urls;
    }

    private static String getName(String url) {
        for (int i = url.length() - 1; i > 0; i--) {
            if (url.charAt(i) == '/') {
                return url.substring(i + 1);
            }
        }
        return url;
    }

    private static boolean reiniciarLauncher() {
        try {
            Thread.sleep(5000);
            System.out.println("SE REINICIARA EL LAUNCHER: ");
            System.out.println("REINICIANDO: " + Runtime.getRuntime().exec("java -jar Launcher.jar"));
            System.exit(0);
            //SistemaConceptualLauncher.main(args);
            actualizado = false;
            return true;
        } catch (IOException ex) {
            Logger.getLogger(SistemaConceptualLauncher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RuntimeException ex) {
            Logger.getLogger(SistemaConceptualLauncher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(SistemaConceptualLauncher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
