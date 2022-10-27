import java.io.*;
import java.net.*;

public class SocketClient {
    Proxy proxy = null;
    public void normal_socket(String host, int port) {
        this.normal_socket(host, port, "/index.html");
    }
    public void normal_socket(String host, int port, String subside) {
        Socket socket = null;
        try {
            //Create Socket
            socket = new Socket(host, port);

            //Instantiate new output stream
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            //Send the request
            out.println("GET " + subside + " HTTP/1.1");
            out.println("Host: " + host);
            out.println("");
            out.flush();
            //Close Output on Socket to signalize the Server that we finished writing!
            //socket.shutdownOutput();

            //Instantiate new input reader
            BufferedReader buff = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Print the response
            String inputline;
            while ((inputline = buff.readLine()) != null) {
                System.out.println(inputline);
            }
            buff.close();
        }

        catch (IOException e) {
            e.printStackTrace();
        }


        finally {
            if (socket != null)
                try {
                    socket.close();
                    System.out.println("Socket geschlossen...");
                } catch (IOException e) {
                    System.out.println("Socket nicht zu schliessen...");
                    e.printStackTrace();
                }
        }
    }


    public void url_socket(String host, String proxy_address, int proxy_port) {
        this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy_address, proxy_port));
        this.url_socket(host);
    }
    public void url_socket(String host) {

        try {
            // Create URL Object from host address
            URL url = new URL(host);

            //Connect to proxy if necessary
            URLConnection connection;
            if (proxy != null) {
                connection = url.openConnection(proxy);
            } else {
                connection = url.openConnection();
            }

            //Read responde
            BufferedReader buff = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            //Print the response
            String inputline;
            while ((inputline = buff.readLine()) != null) {
                System.out.println(inputline);
            }
            buff.close();

        } catch (MalformedURLException e) {
            System.out.println("URL Exception:");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO Exception:");
            e.printStackTrace();
            System.out.println(e.toString());

        }




    }
}