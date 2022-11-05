import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.print.Doc;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.*;

public class SocketClient {
    Proxy proxy = null;
    String host = "127.0.0.1";
    int proxy_port = 0;

    private Document build_request() throws ParserConfigurationException {
        // Build the request document
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        return builder.newDocument();
    }


    public void addRecord(int key, String record) {
        try {
            Document request = build_request();

            Element methodCall = request.createElement("methodCall");
            request.appendChild(methodCall);

            Element methodName = request.createElement("methodName");
            Text text = request.createTextNode("addRecord");
            methodName.appendChild(text);
            methodCall.appendChild(methodName);

            Element params = request.createElement("params");
            methodCall.appendChild(params);

            //Add Key
            Element param1 = request.createElement("param");
            params.appendChild(param1);

            Element value1 = request.createElement("value");
            param1.appendChild(value1);

            Element intElement = request.createElement("int");
            Text indexkey = request.createTextNode(String.valueOf(key));
            intElement.appendChild(indexkey);
            value1.appendChild(intElement);

            //Add record
            Element param2 = request.createElement("param");
            params.appendChild(param2);

            Element value2 = request.createElement("value");
            param2.appendChild(value2);

            Element strElement = request.createElement("string");
            Text indexrecord = request.createTextNode(record);
            strElement.appendChild(indexrecord);
            value2.appendChild(strElement);

            //send request
            send_request(request);


        } catch (ParserConfigurationException e) {
            System.out.println("Unable to Build Base Document.");
            e.printStackTrace();
        }
    }

    public void getRecord(int key) {
        try {
            Document request = build_request();

            Element methodCall = request.createElement("methodCall");
            request.appendChild(methodCall);

            Element methodName = request.createElement("methodName");
            Text text = request.createTextNode("getRecord");
            methodName.appendChild(text);
            methodCall.appendChild(methodName);

            Element params = request.createElement("params");
            methodCall.appendChild(params);

            //Add Key
            Element param1 = request.createElement("param");
            params.appendChild(param1);

            Element value1 = request.createElement("value");
            param1.appendChild(value1);

            Element intElement = request.createElement("int");
            Text indexkey = request.createTextNode(String.valueOf(key));
            intElement.appendChild(indexkey);
            value1.appendChild(intElement);

            //send request
            send_request(request);


        } catch (ParserConfigurationException e) {
            System.out.println("Unable to Build Base Document.");
            e.printStackTrace();
        }
    }

    public void getSize() {
        try {
            Document request = build_request();

            Element methodCall = request.createElement("methodCall");
            request.appendChild(methodCall);

            Element methodName = request.createElement("methodName");
            Text text = request.createTextNode("getSize");
            methodName.appendChild(text);
            methodCall.appendChild(methodName);

            Element params = request.createElement("params");
            methodCall.appendChild(params);

            //send request
            send_request(request);


        } catch (ParserConfigurationException e) {
            System.out.println("Unable to Build Base Document.");
            e.printStackTrace();
        }
    }

    public SocketClient(String host) {
        this.host = host;
    }


    public SocketClient(String host, String proxy_address, int proxy_port) {
        this.host = host;
        this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy_address, proxy_port));
    }
    private void send_request(Document request) {

        try {
            // Create URL Object from host address
            URL url = new URL(this.host);

            //Create Connection
            URLConnection conn;
            if (this.proxy != null) {
                conn = url.openConnection(this.proxy);
            } else {
                conn = url.openConnection();
            }
            HttpURLConnection connection = (HttpURLConnection) conn;
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            OutputStream out = connection.getOutputStream();

            //Transmit the request
            TransformerFactory xformFactory = TransformerFactory.newInstance();
            Transformer idTransform = xformFactory.newTransformer();
            Source input = new DOMSource(request);
            Result output = new StreamResult(out);
            idTransform.transform(input, output);

            out.flush();
            out.close();

            //Read responde
            BufferedReader buff = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            //Print the response
            String inputline;
            while ((inputline = buff.readLine()) != null) {
                System.out.println(inputline);
            }
            buff.close();

        } catch (MalformedURLException e) {
            System.out.println("URL Exception:" + e);
        } catch (IOException e) {
            System.out.println("IO Exception:");
            e.printStackTrace();
            System.out.println(e);

        } catch (TransformerException e) {
            System.out.println("Transformer Exception:");
            e.printStackTrace();
        }

    }
}