import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SocketServer  implements Runnable{
    int port = 80;
    final ExecutorService ThreadPool = Executors.newFixedThreadPool(10);

    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("New Http Server started on " + server.getLocalPort());

            //Create the context for the server.
            while (server.isBound()) {
                Socket socket = server.accept();
                ThreadPool.submit(new RequestHandler(socket));
            }


        } catch (IOException e) {
            System.out.println("Unable to create server on port " + this.port);
        }
    }

    private class RequestHandler implements Runnable {
        private final Socket socket;


    private RequestHandler(Socket socket) {
        this.socket = socket;
    }

        @Override
        public void run() {
            System.out.println("Handling Client request");
            try {
                InputStream is = socket.getInputStream();
                Path path = Path.of("./Uebung04/src/LogMessage.log").toAbsolutePath();
                Files.copy(is,path, StandardCopyOption.REPLACE_EXISTING);
                is.close();
                System.out.println("File written to " + path.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


