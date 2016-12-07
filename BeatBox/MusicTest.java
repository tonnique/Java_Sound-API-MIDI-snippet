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
		/* нам нужен объект Sequencer. Это главная часть MIDI-устройства/инструмента, 
		 * который мы используем. Объект Sequencer синтезирует "композицию" из информации 
		 * в формате MIDI. Но мы не создаем собственный синтезатор, а запрашиваем его у класса MidiSystem.
		 */
		try {
			// Creating and opening a sequencer
			Sequencer player = MidiSystem.getSequencer();
			player.open();
			
			/** Регистрируем события синтезатором. Метод, отвечающий за регистрацию, принимает объект
			 *  слушателя и целочисленный массив, представляющий собой список событий ControllerEvent,
			 * которые нам нужны. Нас интересует только одно событие - #127. 
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
				
				/** Вот так мы ловим ритм - добавляем наше собственное событие ControllerEvent 
				 * (176 означает, что тип события - ControllerEvent) с аргументом для события 
				 * номер #127. Оно ничего не будет делать! Мы вставляем его лишь для того, чтобы 
				 * иметь возможность реагировать на воспроизведение каждой ноты. Другими словами, 
				 * его единственная цель - запуск чего нибудь, что можно отслеживать (мы можем 
				 * следить за событиями включения/выключения воспроизведения нот). Заметьте, что 
				 * мы запускаем это событие в тот же самый момент, когда включается воспроизведение 
				 * ноты. Поэтому когда произойдет событие включения воспроизведения ноты, мы сразу 
				 * узнаем об этом, так как наше событие запуститься в тоже самое время.  
				 */
				track.add(makeEvent(176, 1, 127, 0, i));
				
				track.add(makeEvent(128, 1, r, 100, i+2));				
			}
			
			// передаем последовательность синтезатору как будто вставляем CD в проигрыватель
			player.setSequence(seq);
			player.setTempoInBPM(220); // установить темп
			
			// запускаем синтезатор (как будто нажимаем Play)
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
	
	/** Вспомогательный статич. метод, который будет создавать сообщения и возвращать MidiEvent.
	 *   
	 *   Четыре параметра для сообщения, и событие
	 *    
	 *   @param comd - команда для Midi Сиквенсера
	 *   @param chan - номер канала
	 *   @param one - команда 1 (напр. нота
	 *   @param two - команда 2 (напр. длительность ноты и сила нажатия)
	 *   @param tick Cобытие которое происходит в момент появления данного сообщения
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
		System.out.print("ля-");		
	}
	
	private class MyDrawPanel extends JPanel implements ControllerEventListener {
		
		boolean msg = false; // присвоим значение true, когда получим событие
		
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
