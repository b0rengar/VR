/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import messages.ClientUserDataMessage;

/**
 *
 * @author Fabian
 */
public class UserDataLog {

    private File file;
    private BufferedWriter bw; 
    private FileWriter fw;
    private PrintWriter output;
    
    public UserDataLog(){
    }
    
    private void startLogFile() throws IOException{
       DateFormat df = new SimpleDateFormat("dd_MM_yy");
       Date dateobj = new Date();
       String filename = "UserDataLog_" + df.format(dateobj) + ".log"; 
       file = new File(filename);
       if(!file.exists()){
           file.createNewFile();
       }
       fw = new FileWriter(file.getAbsoluteFile(),true);
       bw = new BufferedWriter(fw); 
       output = new PrintWriter(bw);
    }
    
    private void closeLogFile() throws IOException{
        output.close();
    }
    
    public void writeLogFile(ClientUserDataMessage msg) throws IOException{
        startLogFile();
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date dateobj = new Date();
        StringBuffer LogLine = new StringBuffer();
	LogLine.append(df.format(dateobj));
	LogLine.append("\tUser: ");
	LogLine.append(msg.getPlayerName());
	LogLine.append("\tOxigen: ");
        LogLine.append(msg.getO2());
	LogLine.append("\tPulse: ");
        LogLine.append(msg.getPulse());
	LogLine.append("\tLocation (x,y,z): ");
        LogLine.append(msg.getLocation().toString());
	LogLine.append(System.getProperty("line.separator"));
        output.write(LogLine.toString());
        closeLogFile();
    }
    
}
