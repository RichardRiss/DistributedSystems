import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import javax.xml.parsers.*;


public class SocketServer {
    int port = 80;
    Map<Integer, String> db = new HashMap<>();


    public Integer getSize() {
        //determines, how many records are stored in the database.
        return db.size();
    }


    public void addRecord(int key,String record) {
        //writes a database record to the database using a given key
        this.db.put(key, record);
        System.out.println("Entry " + key + " : " + record + "added to database.");
    }

    public String getRecord(int key) {
        //reads a database record from the database given a key
        String result = "";

        if (this.db.containsKey(key)) {
            result = this.db.get(key);
        } else {
            System.out.println("Requested record not in database.");
        }
        return result;
    }


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


    class RequestHandler implements HttpHandler {
        //Request Handler method
        @Override
        public void handle(HttpExchange t) throws IOException {
            try {
                //Create XML Parser
                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = builderFactory.newDocumentBuilder();


                //Message request received
                System.out.println("New Request received. Processing...");
                Document request = builder.parse(t.getRequestBody());
                System.out.println("Request was: " + request);

                //Parse the request method
                NodeList methods = request.getElementsByTagName("methodName");
                Node method = methods.item(0);
                String methodname = method.getTextContent();;
                //Parse the request params
                NodeList params = request.getElementsByTagName("params");

                //Call corresponding method
                String StrResponse = "";
                switch (methodname) {
                    case "getRecord" -> {
                        NodeList keylist = request.getElementsByTagName("int");
                        Node keynode = keylist.item(0);
                        int key = Integer.parseInt(keynode.getNodeValue());
                        StrResponse = SocketServer.this.getRecord(key);
                    }
                    case "addRecord" -> {
                        NodeList keylist = request.getElementsByTagName("int");
                        Node keynode = keylist.item(0);
                        int key = Integer.parseInt(keynode.getNodeValue());
                        NodeList recordlist = request.getElementsByTagName("string");
                        Node recordnode = recordlist.item(0);
                        String record = recordnode.getTextContent();
                        SocketServer.this.addRecord(key, record);
                    }
                    case "getSize" -> {
                        StrResponse = String.valueOf(SocketServer.this.getSize());
                    }
                    default -> StrResponse = "No method with this name.";
                }

                //Create the response document
                Document response = builder.newDocument();
                Element methodResponse = response.createElement("methodResponse");
                response.appendChild(methodResponse);

                Element Params = response.createElement("params");
                methodResponse.appendChild(Params);

                Element param = response.createElement("param");
                Params.appendChild(param);

                Element value = response.createElement("value");
                param.appendChild(value);

                Element str = response.createElement("string");
                Text resp = response.createTextNode(StrResponse);
                str.appendChild(resp);
                param.appendChild(str);


                //Read the request, set the parameters
                String encoding = "UTF-8";
                t.getResponseHeaders().set("Content-Type", "text/html; charset=" + encoding);
                t.getResponseHeaders().set("Accept-Ranges", "bytes");
                OutputStream ostream = t.getResponseBody();
                //build response and send back
                t.sendResponseHeaders(200, response.toString().getBytes().length);
                try {
                    ostream.write(response.toString().getBytes(encoding));
                } catch (Exception e) {
                    System.out.println("Unable to send response.");
                    System.out.println(e);
                }
                System.out.println("Response sent.");
                //close connection
                ostream.flush();
                ostream.close();

            } catch (SAXException | ParserConfigurationException e) {
                System.out.println("Error Handling Request" + e.getClass());
                e.printStackTrace();
            }
        }
    }

}


