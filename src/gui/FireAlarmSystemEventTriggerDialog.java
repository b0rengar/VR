package gui;

import datamodel.sensors.Sensor;
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
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Graphic interface to create and trigger fire alarm system events.
 *
 * @author Sebastian Mundry
 * @author Benjamin Gorny
 * @see event.FireAlarmSystemEvent
 */
public class FireAlarmSystemEventTriggerDialog extends JDialog {
  private JPanel m_contentPane;
  private JButton m_buttonTrigger;
  private JButton m_buttonCancel;
  private JComboBox m_comboBoxEvents;
  private JList m_listUnits;
  private JComboBox m_comboBoxGroups;
  private JComboBox m_comboBoxUnits;

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

    System.out.println(event);
    // send event for processing...
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
    panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
    panel1.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    m_buttonTrigger = new JButton();
    m_buttonTrigger.setText("Auslösen");
    panel2.add(m_buttonTrigger, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    m_buttonCancel = new JButton();
    m_buttonCancel.setText("Abbrechen");
    panel2.add(m_buttonCancel, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JPanel panel3 = new JPanel();
    panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
    m_contentPane.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    final JLabel label1 = new JLabel();
    label1.setText("Gruppe");
    panel3.add(label1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JLabel label2 = new JLabel();
    label2.setText("Melder");
    panel3.add(label2, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JPanel panel4 = new JPanel();
    panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
    panel3.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    final JLabel label3 = new JLabel();
    label3.setText("Wählen Sie den Ereignistyp!");
    panel4.add(label3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    m_comboBoxEvents = new JComboBox();
    panel4.add(m_comboBoxEvents, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    m_comboBoxGroups = new JComboBox();
    panel3.add(m_comboBoxGroups, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    m_comboBoxUnits = new JComboBox();
    panel3.add(m_comboBoxUnits, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JPanel panel5 = new JPanel();
    panel5.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
    m_contentPane.add(panel5, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    final JLabel label4 = new JLabel();
    label4.setText("Melderzustand");
    panel5.add(label4, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    m_listUnits = new JList();
    panel5.add(m_listUnits, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
  }

  /** @noinspection ALL */
  public JComponent $$$getRootComponent$$$() { return m_contentPane; }
}
