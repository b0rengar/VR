package gui;

import datamodel.sensors.Sensor;
import datamodel.sensors.SensorChangeListener;
import datamodel.sensors.SensorGroup;
import datamodel.sensors.SensorManager;
import event.FireAlarmSystemEvent;
import event.FireAlarmSystemEventTypes;

import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import static event.FireAlarmSystemEventTypes.*;

/**
 * Graphic interface to create and trigger fire alarm system events.
 *
 * @author Sebastian Mundry
 * @author Benjamin Gorny
 * @see event.FireAlarmSystemEvent
 */
public class FireAlarmSystemEventTriggerDialog extends JDialog implements SensorChangeListener {
  private JPanel m_contentPane;
  private JButton m_buttonTrigger;
  private JButton m_buttonCancel;
  private JComboBox m_comboBoxEvents;
  private JComboBox m_comboBoxGroups;
  private JComboBox m_comboBoxUnits;
  private JButton m_buttonTriggerAll;
  private JTable m_tableSensorStatus;

  public FireAlarmSystemEventTriggerDialog() {
    setContentPane(m_contentPane);
    setModal(true);
    getRootPane().setDefaultButton(m_buttonTrigger);
    setTitle("BMZ Simulator");

    m_buttonTrigger.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onTrigger();
      }
    });

    m_buttonTriggerAll.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onTriggerAll();
      }
    });
    m_buttonCancel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onCancel();
      }
    });

    // call onCancel() when cross is clicked
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        onCancel();
      }
    });

    // call onCancel() on ESCAPE
    m_contentPane.registerKeyboardAction(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onCancel();
      }
    }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    // Close the dialog on Ctrl+Q
    m_contentPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK), "actionMapKey");
    m_contentPane.getActionMap().put("actionMapKey", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent event) {
        onCancel();
      }
    });

    DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<String>();
    for (FireAlarmSystemEventTypes event : FireAlarmSystemEventTypes.values()) {
      comboBoxModel.addElement(event.getName());
    }
    m_comboBoxEvents.setModel(comboBoxModel);

    DefaultComboBoxModel<SensorGroup> comboBoxGroupsModel = new DefaultComboBoxModel<SensorGroup>();
    for (SensorGroup sensorGroup : SensorManager.getInstance().getSensorGroups()) {
      comboBoxGroupsModel.addElement(sensorGroup);
    }
    m_comboBoxGroups.setModel(comboBoxGroupsModel);
    m_comboBoxGroups.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        m_comboBoxUnits.setModel(getUnitsComboBoxModel(((SensorGroup) ((JComboBox) e.getSource()).getSelectedItem()).getId()));
      }
    });

    if (m_comboBoxGroups.getModel().getSize() > 0) {
      m_comboBoxUnits.setModel(getUnitsComboBoxModel(((SensorGroup) m_comboBoxGroups.getModel().getElementAt(0)).getId()));
    }
    List<Sensor> sensors = SensorManager.getInstance().getSensors();
    TableModel model = new DefaultTableModel(new String[]{"Sensor", "Status"}, sensors.size());
    int row = 0;
    for (Sensor sensor : sensors) {
      if (sensor.getStatus() != READY) {
        model.setValueAt(sensor, row, 0);
        model.setValueAt(sensor.getStatus().getName(), row, 1);
        row++;
      }
    }
    ((DefaultTableModel) model).setRowCount(row);
    m_tableSensorStatus.setModel(model);

    SensorManager.getInstance().addSensorChangeListener(this);
  }

  public static void main(String[] args) {
    FireAlarmSystemEventTriggerDialog dialog = new FireAlarmSystemEventTriggerDialog();
    dialog.pack();
    dialog.setVisible(true);
    //System.exit(0);
  }

  private void onTrigger() {
    String type = (String) m_comboBoxEvents.getSelectedItem();
    String group = String.valueOf(((SensorGroup) m_comboBoxGroups.getSelectedItem()).getId());
    String unit = String.valueOf(((Sensor) m_comboBoxUnits.getSelectedItem()).getId());
    FireAlarmSystemEvent event = new FireAlarmSystemEvent(type, group, unit);
    
    SensorManager.getInstance().alarmEvent(event);
    
    //System.out.println(event);
    // send event for processing...
  }

  private void onTriggerAll() {
    String type = (String) m_comboBoxEvents.getSelectedItem();
    SensorGroup selectedGroup = (SensorGroup) m_comboBoxGroups.getSelectedItem();
    for (Sensor sensor : selectedGroup.getSensors()) {
      FireAlarmSystemEvent event = new FireAlarmSystemEvent(type, String.valueOf(selectedGroup.getId()), String.valueOf(sensor.getId()));
      SensorManager.getInstance().alarmEvent(event);
    }
  }
  private void onCancel() {
    dispose();
  }

  /**
   * Assemble a new model for the combo box listing the sensor units based
   * on the given group number to ensure only validate units are displayed
   * and selectable.
   *
   * @param sensorGroupId The sensor group to list the sensors of.
   * @return A list of all sensor within the given group. If no group was
   * found by the given id the list will be empty.
   */
  private static ComboBoxModel<Sensor> getUnitsComboBoxModel(int sensorGroupId) {
    SensorGroup sensorGroup = SensorManager.getInstance().getSensorGroup(sensorGroupId);
    DefaultComboBoxModel<Sensor> comboBoxUnitsModel = new DefaultComboBoxModel<Sensor>();

    if (sensorGroup == null) {
      return comboBoxUnitsModel;
    }
    for (Sensor sensor : sensorGroup.getSensors()) {
      comboBoxUnitsModel.addElement(sensor);
    }
    return comboBoxUnitsModel;
  }

  /**
   * Update the given sensor's status in the table view.
   *
   * @param sensor The sensor whose status has changed.
   */
  @Override
  public void sensorChanged(Sensor sensor) {
    int rowCount = m_tableSensorStatus.getModel().getRowCount();
    // Lookup the sensor whether it is already listed in the table.
    for (int i = 0; i < rowCount; i++) {
      if (m_tableSensorStatus.getModel().getValueAt(i, 0).equals(sensor)) {
        // If the status was changed back to ready remove the sensor
        // from the list.
        // Otherwise update the status.
        if (sensor.getStatus() == READY) {
          ((DefaultTableModel) m_tableSensorStatus.getModel()).removeRow(i);
        } else {
          m_tableSensorStatus.getModel().setValueAt(sensor.getStatus().getName(), i, 1);
        }
        return;
      }
    }

    // Add the sensor to the table as it is not yet listed.
    // Remain unchanged if the sensor's status is READY.
    if (sensor.getStatus() != READY) {
      ((DefaultTableModel) m_tableSensorStatus.getModel()).addRow(new Object[]{sensor, sensor.getStatus().getName()});
    }
  }
  {
    // GUI initializer generated by IntelliJ IDEA GUI Designer
    // >>> IMPORTANT!! <<<
    // DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO
   * NOT edit this method OR call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    m_contentPane = new JPanel();
    m_contentPane.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(10, 10, 10, 10), -1, -1));
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
    m_contentPane.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
    final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
    panel1.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    final JPanel panel2 = new JPanel();
    panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
    panel1.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    m_buttonTrigger = new JButton();
    m_buttonTrigger.setText("Auslösen");
    panel2.add(m_buttonTrigger, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    m_buttonCancel = new JButton();
    m_buttonCancel.setText("Abbrechen");
    panel2.add(m_buttonCancel, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    m_buttonTriggerAll = new JButton();
    m_buttonTriggerAll.setText("Alle Auslösen");
    panel2.add(m_buttonTriggerAll, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
    m_contentPane.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    final JPanel panel4 = new JPanel();
    panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
    panel3.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    final JLabel label1 = new JLabel();
    label1.setText("Wählen Sie den Ereignistyp!");
    panel4.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    m_comboBoxEvents = new JComboBox();
    panel4.add(m_comboBoxEvents, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JPanel panel5 = new JPanel();
    panel5.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
    panel3.add(panel5, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    final JLabel label2 = new JLabel();
    label2.setText("Gruppe");
    panel5.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    m_comboBoxGroups = new JComboBox();
    panel5.add(m_comboBoxGroups, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JPanel panel6 = new JPanel();
    panel6.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
    panel3.add(panel6, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    final JLabel label3 = new JLabel();
    label3.setText("Melder");
    panel6.add(label3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    m_comboBoxUnits = new JComboBox();
    panel6.add(m_comboBoxUnits, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
    panel3.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    final JPanel panel7 = new JPanel();
    panel7.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
    m_contentPane.add(panel7, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    final JLabel label4 = new JLabel();
    label4.setText("Melderzustand");
    panel7.add(label4, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JScrollPane scrollPane1 = new JScrollPane();
    panel7.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    m_tableSensorStatus = new JTable();
    scrollPane1.setViewportView(m_tableSensorStatus);
  }

  /** @noinspection ALL */
  public JComponent $$$getRootComponent$$$() { return m_contentPane; }
}
