import com.google.protobuf.Timestamp;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.*;
import java.time.Instant;


public class SocketClient {
    Proxy proxy = null;
    String host = "127.0.0.1";
    String reponsestring = "";


    private Document build_request() throws ParserConfigurationException {
        // Build the request document
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        return builder.newDocument();
    }

    public void log_msg(int serverity, String msg,String indicator) {
        Instant time = Instant.now();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(time.getEpochSecond())
                .setNanos(time.getNano()).build();

        LogMessage.log log = LogMessage.log.newBuilder()
                .setTimestamp(timestamp)
                .setMsg(msg)
                .setIndicator(String.valueOf(indicator))
                .setSeverityLvlValue(serverity)
                .build();

        try {
            Document request = build_request();

            Element methodCall = request.createElement("LogMessage");
            request.appendChild(methodCall);

            Element methodName = request.createElement("methodName");
            Text text = request.createTextNode("LogMessage");
            methodName.appendChild(text);
            methodCall.appendChild(methodName);

            Element params = request.createElement("msg");
            Text msgtext = request.createTextNode(log.getMsg().toString());
            params.appendChild(msgtext);
            methodCall.appendChild(params);

            send_request(request);

        } catch (ParserConfigurationException e) {
            System.out.println("Unable to Build Base Document.");
            e.printStackTrace();
        }

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

    public String getRecord(int key) {
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

        return this.reponsestring;
    }

    public int getSize() {
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

        return Integer.parseInt(this.reponsestring);
    }

    public SocketClient(String host) {
        this.host = host;
    }

    public SocketClient(String host, String proxy_address, int proxy_port) {
        this.host = host;
        this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy_address, proxy_port));
    }

    public void read_response(HttpURLConnection connection) {
        //Read response
        InputStream in = null;
        try {
            int status = connection.getResponseCode();
            System.out.println("Server returned status " + status);
            in = connection.getInputStream();

            //Build Parser
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();

            //Parse response
            Document response = builder.parse(in);

            //close connection
            in.close();
            connection.disconnect();

            //Print the response
            System.out.println("Request returned: " + getStringFromDocument(response));
            NodeList values = response.getElementsByTagName("value");
            for (int i = 0; i < values.getLength(); i++) {
                Node value = values.item(i).getFirstChild();
                if (value != null) {
                    if (value.getNodeName().equals("string")) {
                        this.reponsestring = value.getTextContent();
                    }
                }
            }


        } catch (ParserConfigurationException | IOException | SAXException e) {
            System.out.println(e.getClass());
            e.printStackTrace();
            connection.disconnect();
        }
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

            System.out.println("Request send: " + getStringFromDocument(request));
            read_response(connection);

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
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    //method to convert Document to String
    public String getStringFromDocument(Document doc)
    {
        try
        {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        }
        catch(TransformerException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}