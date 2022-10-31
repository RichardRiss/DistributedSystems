import com.sun.net.httpserver.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.concurrent.Executors;


public class SocketServer {
    int port = 80;

    public SocketServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost",port), 0);
            //Create the context for the server.
            server.createContext("/index.html", new RequestHandler());
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            System.out.println("New Http Server started on " + server.getAddress());

        } catch (IOException e) {
            System.out.println("Unable to create server on port " + this.port);
        }
    }


    static class RequestHandler implements HttpHandler {
        //Request Handler method
        @Override
        public void handle(HttpExchange t) throws IOException {
            //Message request received
            System.out.println("New Request received. Processing...");
            Scanner s = new Scanner(t.getRequestBody()).useDelimiter("\\A");
            String request = s.hasNext() ? s.next() : "";
            if (request.length() > 0) System.out.println("Request was: " + request);
            //get response file
            String response = "";
            Path path = Path.of("./Uebung01/src/index.html").toAbsolutePath();
            try {
                response = Files.readString(path);
            } catch (Exception e) {
                System.out.println("Unable to read response file.");
                System.out.println(e);
            }

            //Read the request, set the parameters
            String encoding = "UTF-8";
            t.getResponseHeaders().set("Content-Type", "text/html; charset=" + encoding);
            t.getResponseHeaders().set("Accept-Ranges", "bytes");
            OutputStream ostream = t.getResponseBody();
            //build response and send back
            t.sendResponseHeaders(200, response.getBytes().length);
            try {
                ostream.write(response.getBytes(encoding));
            } catch (Exception e) {
                System.out.println("Unable to send response.");
                System.out.println(e);
            }
            System.out.println("Response sent.");
            //close connection
            ostream.flush();
            ostream.close();
        }
    }

}


