package http2;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        ServerSocket ss= new ServerSocket(8080);
        while(true){
            Socket s= ss.accept();
            BufferedReader in= new BufferedReader(new InputStreamReader(s.getInputStream()));
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            
            // LEGGO LA RIGA DI RICHIESTA HTTP E LA PARSERIZZO
            String firstLine= in.readLine();

            String[] request= firstLine.split(" ");  
            String method= request[0];       // METODO  (GET)
            String resource= request[1];    // URL (RISORSA)
            String version= request[2];     // VERS PROT. 

            // LEGGO L'intestazione Della RICHIESTA HTTP
            String header;
            do{
                header= in.readLine();
                System.out.println(header);
            }while(!header.isEmpty());

            //controllo se è presente url
            if(resource.equals("/")){ 
                resource="index.html";
            }
            File file = new File("htdocs/"+ resource);
            
            if(file.exists()){
                //costruisco risposta http
                // RIGA DI RISPOSTA
                out.writeBytes("HTTP/1.1 200 OK\n");
                // INTESTAZIONE
                out.writeBytes("Content-Length: "+ file.length()+"\n");
                // CHIAMO UNA FUNZ per GESTIRE le diverse estensioni di file richieste
                out.writeBytes("Content-Type: "+getContentType(file)+"\n");// 
                // RIGA VUOTA
                out.writeBytes("\n");
                // CORPO DELLA RISPOSTA
                InputStream input= new FileInputStream(file);
                byte[] buf = new byte[8192];
                int n;
                while((n= input.read(buf)) != -1){
                    out.write(buf,0, n);
                }
                input.close();
            }
            else{
                //RISPOSTA SE FILE NON TROVATO
                String msg="File non trovato";
                out.writeBytes("HTTP/1.1 404 Not Found\n");
                out.writeBytes("Content-Length: "+msg.length()+"\n");
                out.writeBytes("Content-Type: text/plain\n");
                out.writeBytes("\n");
                out.writeBytes(msg);
            }
            s.close();
        }
    }
    //FUNZIONE PER GESTIRE LE FUNZIONI
    private static String getContentType(File f){
        String[] s = f.getName().split("\\.");
        String ext = s[s.length -1];
        switch(ext){
            case "html":
                return "text/html";
            case "png":
                return "image/png";
            case "jpeg":
                return "image/jpeg";
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            default:
                return "";
        }
    }
}
