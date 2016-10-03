package im.tox.gui.forms;

import im.tox.gui.domain.Friend;

import javax.swing.*;

public class MainViewBase extends JFrame {

  public JList<String> messages;
  public JTabbedPane tabbedPane1;
  public JButton connectButton;
  public JCheckBox enableIPv6CheckBox;
  public JCheckBox enableUdpCheckBox;
  public JRadioButton noneRadioButton;
  public JRadioButton httpRadioButton;
  public JRadioButton socksRadioButton;
  public JTextField proxyHost;
  public JTextField bootstrapHost;
  public JTextField bootstrapPort;
  public JTextField bootstrapKey;
  public JButton bootstrapButton;
  public JPanel contentPane;
  public JTextField proxyPort;
  public JTextField friendId;
  public JButton addFriendButton;
  public JTextField friendRequest;
  public JList<Friend> friendList;
  public JTextField messageText;
  public JRadioButton messageRadioButton;
  public JRadioButton actionRadioButton;
  public JButton sendButton;
  public JTextField selfPublicKey;
  public JTextField fileName;
  public JProgressBar fileProgress;
  public JButton sendFileButton;

  public MainViewBase() {
    setContentPane(contentPane);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

}
