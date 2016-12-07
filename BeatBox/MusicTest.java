package BeatBox;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.sound.midi.*;


public class MusicTest extends JFrame implements ActionListener, ControllerEventListener {
	
	JButton playButton = new JButton("Play");
	MyDrawPanel drawPanel = new MyDrawPanel();
		
	public MusicTest() {
		super("MIDI-music");
		setControls();
		pack();		
	}
	
	private void setControls() {
		Container c = getContentPane();
		
		JPanel buttonPanel = new JPanel();
	
		
		buttonPanel.add(playButton);
		playButton.addActionListener(this);
				
		c.add(drawPanel, BorderLayout.CENTER);		
		c.add(buttonPanel, BorderLayout.SOUTH);		
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource().equals(playButton)) {			
			play();
		}		
	}
	
	
	public void play() {
		/* ��� ����� ������ Sequencer. ��� ������� ����� MIDI-����������/�����������, 
		 * ������� �� ����������. ������ Sequencer ����������� "����������" �� ���������� 
		 * � ������� MIDI. �� �� �� ������� ����������� ����������, � ����������� ��� � ������ MidiSystem.
		 */
		try {
			// Creating and opening a sequencer
			Sequencer player = MidiSystem.getSequencer();
			player.open();
			
			/** ������������ ������� ������������. �����, ���������� �� �����������, ��������� ������
			 *  ��������� � ������������� ������, �������������� ����� ������ ������� ControllerEvent,
			 * ������� ��� �����. ��� ���������� ������ ���� ������� - #127. 
			 */			
			player.addControllerEventListener(drawPanel, new int[] {127});
			
			// Creating a sequence			
			Sequence seq = new Sequence(Sequence.PPQ, 4);
			
			// Request a track from the sequence 
			// Remember - track is located inside of the sequence, and MIDI data is inside of a track
			Track track = seq.createTrack();
			
			// Creating a group of events. we want the piano notes to be raising from note 5 to 61
			int r = 0;
			for (int i=0; i < 200; i+=3) {
				r = (int) ((Math.random() * 126) + 1);
				track.add(makeEvent(144, 1, r, 100, i));
				
				/** ��� ��� �� ����� ���� - ��������� ���� ����������� ������� ControllerEvent 
				 * (176 ��������, ��� ��� ������� - ControllerEvent) � ���������� ��� ������� 
				 * ����� #127. ��� ������ �� ����� ������! �� ��������� ��� ���� ��� ����, ����� 
				 * ����� ����������� ����������� �� ��������������� ������ ����. ������� �������, 
				 * ��� ������������ ���� - ������ ���� ������, ��� ����� ����������� (�� ����� 
				 * ������� �� ��������� ���������/���������� ��������������� ���). ��������, ��� 
				 * �� ��������� ��� ������� � ��� �� ����� ������, ����� ���������� ��������������� 
				 * ����. ������� ����� ���������� ������� ��������� ��������������� ����, �� ����� 
				 * ������ �� ����, ��� ��� ���� ������� ����������� � ���� ����� �����.  
				 */
				track.add(makeEvent(176, 1, 127, 0, i));
				
				track.add(makeEvent(128, 1, r, 100, i+2));				
			}
			
			// �������� ������������������ ����������� ��� ����� ��������� CD � �������������
			player.setSequence(seq);
			player.setTempoInBPM(220); // ���������� ����
			
			// ��������� ���������� (��� ����� �������� Play)
			player.start();
		}
		catch(MidiUnavailableException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
		catch(InvalidMidiDataException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {			

			@Override
			public void run() {
				MusicTest mt = new MusicTest();
				mt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mt.setVisible(true);
			}
		});		
	}
	
	/** ��������������� ������. �����, ������� ����� ��������� ��������� � ���������� MidiEvent.
	 *   
	 *   ������ ��������� ��� ���������, � �������
	 *    
	 *   @param comd - ������� ��� Midi ����������
	 *   @param chan - ����� ������
	 *   @param one - ������� 1 (����. ����
	 *   @param two - ������� 2 (����. ������������ ���� � ���� �������)
	 *   @param tick C������ ������� ���������� � ������ ��������� ������� ���������
	 */
	public static MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
		MidiEvent event = null;
		
		try {
			ShortMessage a = new ShortMessage();
			a.setMessage(comd, chan, one, two);
			event = new MidiEvent(a, tick);
		}
		catch (InvalidMidiDataException ex) { 
			System.out.println(ex.getMessage());
			ex.printStackTrace();			
		}
		
		return event;
	}

	@Override
	public void controlChange(ShortMessage arg0) {
		System.out.print("��-");		
	}
	
	private class MyDrawPanel extends JPanel implements ControllerEventListener {
		
		boolean msg = false; // �������� �������� true, ����� ������� �������
		
		public MyDrawPanel() {
			setPreferredSize(new Dimension(200, 200));
		}
		
		@Override
		public void controlChange(ShortMessage event) {
			msg = true;
			repaint();			
		}
		
		@Override
		public void paintComponent(Graphics g) {
			if (msg) {
				Graphics2D g2 = (Graphics2D) g;
				
				int r = (int) (Math.random() * 250);
				int gr = (int) (Math.random() * 250);
				int b = (int) (Math.random() * 250);
				
				g2.setColor(new Color(r, gr, b));
				int height = (int) ((Math.random() * 120) + 10);
				int width = (int) ((Math.random() * 120) + 10);
				int x = (int) ((Math.random() * 40) + 10);
				int y = (int) ((Math.random() * 40) + 10);
				g2.fillRect(x, y, width, height);
				msg = false;				
			}
		}		
	}	
}
