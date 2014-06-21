package event;

/**
 * Immutable class representing an event triggered by a fire alarm
 * system.
 *
 * @author Sebastian Mundry
 * @author Benjamin Gorny
 */
public class FireAlarmSystemEvent {
  /**
   * The type of event triggered. E.g.: alarm, malfunction, status,
   * etc.
   */
  private String m_type;
  /**
   * The group the event belongs to. In case of an alarm that may be the
   * number of the group the unit that triggered the alarm is in. In
   * case of a malfunction that may be the type of malfunction.
   */
  private String m_group;
  /** The unit that triggered the event. May be blank. */
  private String m_alarmUnit;

  /**
   * Creates a new instance describing an event using the given values.
   *
   * @param type      The type of event triggered. E.g.: alarm,
   *                  malfunction, status, etc.
   * @param group     The group the event belongs to. In case of an
   *                  alarm that may be the number of the group the unit
   *                  that triggered the alarm is in. In case of a
   *                  malfunction that may be the type of malfunction.
   * @param alarmUnit The unit that triggered the event. May be blank.
   */
  public FireAlarmSystemEvent(String type, String group, String alarmUnit) {
    m_type = type;
    m_group = group;
    m_alarmUnit = alarmUnit;
  }

  /**
   * Returns the type of event.
   *
   * @return The type of event.
   */
  public String getType() {
    return m_type;
  }

  /**
   * Returns the group the event trigger belongs to.
   *
   * @return The group the event trigger belongs to.
   */
  public String getGroup() {
    return m_group;
  }

  /**
   * Return the unit that triggered the event.
   *
   * @return The unit that triggered the event.
   */
  public String getAlarmUnit() {
    return m_alarmUnit;
  }

  @Override
  public int hashCode() {
    return (97 * ((97 * ((m_type != null) ? m_type.hashCode() : 0)) + ((m_group != null) ? m_group.hashCode() : 0))) + ((m_alarmUnit != null) ? m_alarmUnit.hashCode() : 0);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    FireAlarmSystemEvent that = (FireAlarmSystemEvent) o;
    return !(m_alarmUnit != null ? !m_alarmUnit.equals(that.m_alarmUnit) : that.m_alarmUnit != null) && !(m_group != null ? !m_group.equals(that.m_group) : that.m_group != null) && !(m_type != null ? !m_type.equals(that.m_type) : that.m_type != null);
  }

  @Override
  public String toString() {
    return (m_type == null ? '-' : m_type) + ";" + (m_group == null ? '-' : m_group) + ";" + (m_alarmUnit == null ? '-' : m_alarmUnit);
  }
}
