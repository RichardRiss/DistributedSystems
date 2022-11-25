import java.io.*;
import java.net.*;
import java.time.Instant;

import com.google.protobuf.*;


public class SocketClient {
    Proxy proxy = null;

    public void create_log(String host, int port,String msg) {
        Instant time = Instant.now();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(time.getEpochSecond())
                .setNanos(time.getNano()).build();
        LogMessage.log logmsg = LogMessage.log.newBuilder().setTimestamp(timestamp).setSeverityLvlValue(2)
                .setIndicator("Main").setMsg(msg).build();
        send_log(host, port, logmsg);
    }

    public void send_log(String host, int port, LogMessage.log msg) {
        Socket socket = null;
        try {
            //Create Socket
            socket = new Socket(host, port);

            //Instantiate new output stream
            OutputStream out = socket.getOutputStream();

            //Send the request
            msg.writeTo(out);
            out.flush();
            out.close();
            System.out.println("Request send");
            //Close Output on Socket to signalize the Server that we finished writing!
            //socket.shutdownOutput();

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

}