package BeatBox;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.midi.*;
import javax.swing.*;

public class MusicTest extends JFrame implements ActionListener {

	JSpinner instrument = new JSpinner(new SpinnerNumberModel(0, 0, 127, 1));
	JSpinner note = new JSpinner(new SpinnerNumberModel(0, 0, 127, 1));
	JButton playButton = new JButton("Play");
		
	public MusicTest() {
		super("MIDI-music");
		setControls();
		pack();		
	}
	
	private void setControls() {
		Container c = getContentPane();
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(instrument);
		buttonPanel.add(note);
		c.add(buttonPanel, BorderLayout.NORTH);
		c.add(playButton, BorderLayout.SOUTH);
		
		playButton.addActionListener(this);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource().equals(playButton)) {
			int instrument = ((SpinnerNumberModel) this.instrument.getModel()).getNumber().intValue();
			int note = ((SpinnerNumberModel) this.note.getModel()).getNumber().intValue();
			play(instrument, note);
		}
		
	}
	
	
	public void play(int instrument, int note) {
		/* нам нужен объект Sequencer. Это главная часть MIDI-устройства/инструмента, 
		 * который мы используем. Объект Sequencer синтезирует "композицию" из информации 
		 * в формате MIDI. Но мы не создаем собственный синтезатор, а запрашиваем его у класса MidiSystem.
		 */
		try {
			// Получаем синтезатор и открываем его, чтобы использовать. (изночально он не открыт)
			Sequencer player = MidiSystem.getSequencer();
			player.open();
			
			// не обращайте внимания на аргументы для конструктора синтезатора. 
			// Просто укажите эти аргументы
			Sequence seq = new Sequence(Sequence.PPQ, 4);
			
			// Запрашиваем трек у последовательности. 
			// Помните, что трек содержится внутри последовательности, а MIDI-данные - в треке
			Track track = seq.createTrack();
			
			//MidiEvent event = null;
			
			// Помещаем в трек MIDI-сообщения. Этот код по большей части заранее подготовлен
			// Нужно лишь позаботиться об аргументах для метода setMessage() и конструктора MidiEvent.
			
			ShortMessage first = new ShortMessage();
			first.setMessage(192, 1, instrument, 0);
			MidiEvent changeInstrument = new MidiEvent(first, 1);
			track.add(changeInstrument);
			
			ShortMessage a = new ShortMessage();			
			a.setMessage(144, 1, note, 100);			
			MidiEvent noteOn = new MidiEvent(a, 1);
			track.add(noteOn);
			
			ShortMessage b = new ShortMessage();
			b.setMessage(128, 1, 44, 100);
			MidiEvent noteOff = new MidiEvent(b, 16);
			track.add(noteOff);
			
			// передаем последовательность синтезатору как будто вставляем CD в проигрыватель
			player.setSequence(seq);
			
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
}
