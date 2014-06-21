package test.util;

import event.FireAlarmSystemEvent;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import util.FireAlarmSystemEventParser;

/**
 * @author Sebastian Mundry
 * @author Benjamin Gorny
 */
public class FireAlarmSystemEventParserTest {

  @Test(expected = IndexOutOfBoundsException.class)
  public void testParseEmptyArgument() throws Exception {
    FireAlarmSystemEventParser.parse("");
  }

  @Test
  public void testParseEmptyMessage() throws Exception {
    FireAlarmSystemEvent event = new FireAlarmSystemEvent(null, null, null);
    assertEquals(event, FireAlarmSystemEventParser.parse(";;"));
  }

  @Test
  public void testParse2Piece() throws Exception {
    FireAlarmSystemEvent event = new FireAlarmSystemEvent("brandalarm", "107", null);
    assertEquals(event, FireAlarmSystemEventParser.parse("brandalarm;107;"));
  }

  @Test
  public void testParse3Piece() throws Exception {
    FireAlarmSystemEvent event = new FireAlarmSystemEvent("brandalarm", "107", "12");
    assertEquals(event, FireAlarmSystemEventParser.parse("brandalarm;107;12"));
  }

  @Test
  public void testParse3PieceSecondEmpty() throws Exception {
    FireAlarmSystemEvent event = new FireAlarmSystemEvent("brandalarm", null, "12");
    assertEquals(event, FireAlarmSystemEventParser.parse("brandalarm;;12"));
  }

  @Test
  public void testParseExtraFields() throws Exception {
    FireAlarmSystemEvent event = new FireAlarmSystemEvent("brandalarm", "107", "12");
    assertEquals(event, FireAlarmSystemEventParser.parse("brandalarm;107;12;Extra;Fields;Are;Ignored"));
  }
}
