package util;

import event.FireAlarmSystemEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Parser to split a message generated by a fire alarm system into the
 * contained fields and creates an event.
 *
 * @author Sebastian Mundry
 * @author Benjamin Gorny
 * @see event.FireAlarmSystemEvent
 */
public class FireAlarmSystemEventParser {
  /** Logs the activity of the parser. */
  private static final Logger s_logger = Logger.getLogger(FireAlarmSystemEventParser.class.getName());

  /**
   * Parses the given alarm message into its fields and returns an
   * event.
   *
   * @param message The message to be parsed.
   * @return The event described by the given message.
   */
  public static FireAlarmSystemEvent parse(String message) {
    // Split the given message into its fields.
    List<String> fields = splitMessage(message);

    // Create the event using the parsed fields.
    String type = fields.get(0);
    type = type.isEmpty() ? null : type;
    String group = fields.get(1);
    group = group.isEmpty() ? null : group;
    String alarmUnit = fields.get(2);
    alarmUnit = alarmUnit.isEmpty() ? null : alarmUnit;

    return new FireAlarmSystemEvent(type, group, alarmUnit);
  }

  /**
   * Splits the received message into fields delimited by ';'.
   *
   * @param message The message to split into its fields.
   * @return A list of fields in the message.
   */
  private static List<String> splitMessage(String message) {
    return splitMessage(message, ";");
  }

  /**
   * Splits the received message into fields delimited by the given
   * delimiter.
   *
   * @param message   The message to split into its fields.
   * @param delimiter The symbol the fields are delimited by.
   * @return A list of fields in the message.
   */
  private static List<String> splitMessage(String message, String delimiter) {
    s_logger.info(String.format("Parsing message '%s' using delimiter '%s'.", message, delimiter));
    String delim = delimiter == null ? ";" : delimiter;
    List<String> fields = new LinkedList<String>();

    // Return every single character in the message if the delimiter is
    // empty.
    if (delim.isEmpty()) {
      s_logger.info("Delimiter is empty. Breaking up the message into characters.");
      for (int i = 0; i < message.length(); i++) {
        fields.add(String.valueOf(message.charAt(i)));
      }
      return fields;
    }

    // Split the message into its fields by jumping from delimiter
    // position to delimiter position.
    int i = message.indexOf(delim); // First occurrence of the delimiter if any.
    int prevPos = 0; // Delimiter position from the previous field.
    while (i > -1) {
      fields.add(message.substring(prevPos, i).trim());
      prevPos = i + delim.length();
      i = message.indexOf(";", prevPos);
    }
    fields.add(message.substring(prevPos, message.length()));

    if (s_logger.isLoggable(Level.INFO)) {
      if (fields.size() == 0) {
        s_logger.warning(String.format("No fields found in message '%s'.", message));
      } else {
        StringBuilder log = new StringBuilder(message.length());
        log.append("Found fields ");
        i = 0;
        for (String field : fields) {
          if (i++ > 0) {
            log.append(", ");
          }
          log.append("'");
          log.append(field);
          log.append("'");
        }
        log.append(" in message '");
        log.append(message);
        log.append("'.");
        s_logger.info(log.toString());
      }
    }

    return fields;
  }
}
