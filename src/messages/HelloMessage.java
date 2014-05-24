/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author fibu
 */

@Serializable
public class HelloMessage extends AbstractMessage {
    private String hello;       // custom message data
    public HelloMessage() {}    // empty constructor
    public HelloMessage(String s) { hello = s; } // custom constructor
}
