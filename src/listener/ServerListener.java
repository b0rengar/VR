package listener;

import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import messages.HelloMessage;

/**
 * test
 * @author normenhansen
 */
public class ServerListener implements MessageListener<HostedConnection>  {


    public void messageReceived(HostedConnection source, Message message) {
        if (message instanceof HelloMessage) {
          // do something with the message
          HelloMessage helloMessage = (HelloMessage) message;
          System.out.println("Server received '" +helloMessage +"' from client #"+source.getId() );
        } // else...
    }

}
