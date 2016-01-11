import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import java.awt.Font;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FrameOpciones {

	public interface PalabraLister{
		void palabra(String palabra);
	}
	
	
	private JFrame frame;
	PalabraLister listener;
	private JLabel lbl_tiempo;
	int tiempo = 10;
	private JButton p1;
	private JButton p2;
	private JButton p3;
	Timer t;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FrameOpciones window = new FrameOpciones(null);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FrameOpciones(PalabraLister listener) {
		initialize();
		this.listener = listener;
		frame.setVisible(true);
		tiempo = 10;
		t = new Timer(1000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(tiempo == 0){
					double n = Math.random();
					if(n<0.33){
						listener.palabra(p1.getText());
					}else if(n>0.66){
						listener.palabra(p2.getText());
					}else {
						listener.palabra(p3.getText());
					}
					t.stop();
					frame.dispose();
				}
				lbl_tiempo.setText(""+(tiempo--));
			}
		});
		t.start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Seleccione la palabra a dibujar");
		frame.setAlwaysOnTop(true);
		frame.setResizable(false);
		frame.setBounds(100, 100, 288, 210);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		JLabel lblNewLabel = new JLabel("Seleccione una palabra:");
		lblNewLabel.setFont(new Font("Arial", Font.BOLD, 16));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		String []opciones = new Palabras().getOpciones();
		
		p2 = new JButton(opciones[0]);
		p2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listener.palabra(p2.getText());
				frame.dispose();
				t.stop();
			}
		});
		
		p1 = new JButton(opciones[1]);
		p1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				listener.palabra(p1.getText());
				frame.dispose();
				t.stop();
			}
		});
		
		p3 = new JButton(opciones[2]);
		p3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listener.palabra(p3.getText());
				frame.dispose();
				t.stop();
			}
		});
		
		JLabel lblTiempoRestante = new JLabel("Tiempo restante:");
		
		lbl_tiempo = new JLabel("0");
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 269, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(83)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(p2, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(p1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
								.addComponent(p3, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblTiempoRestante)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lbl_tiempo)))
					.addContainerGap(13, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(p1)
					.addGap(11)
					.addComponent(p2)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(p3)
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTiempoRestante)
						.addComponent(lbl_tiempo))
					.addGap(7))
		);
		frame.getContentPane().setLayout(groupLayout);
	}
}
