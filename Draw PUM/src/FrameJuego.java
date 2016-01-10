import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JScrollPane;

public class FrameJuego implements MousePos{

	private JFrame frmDrawPum;
	private JTextField txt_msg;
	Palabras palabras = new Palabras();
	Socket servidor;
	PrintWriter salida;
	ObjectInputStream entrada;
	volatile Juego juego = new Juego();
	String jugador = "";
	
	Thread threadComunicacion = new Thread(new Runnable() {
		
		@Override
		public void run() {
			try{
				while(true){
					juego = (Juego) entrada.readObject();
					updateJuego();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	});

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FrameJuego window = new FrameJuego("127.0.0.1",100,"David");
					window.frmDrawPum.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FrameJuego(String ip,int puerto,String nombre) {
		try{
			initialize();
			
			servidor = new Socket(ip, puerto); 
			entrada = new ObjectInputStream(servidor.getInputStream());
			threadComunicacion.start();
			
			Thread.sleep(100);
			
			for(Jugador j:juego.getJugadores()){
				if(nombre.equals(j.getNombre())){
					JOptionPane.showMessageDialog(null, "Ya hay un jugador con ese nombre");
					frmDrawPum.dispose();
					return;
				}
			}
			
			salida = new PrintWriter(servidor.getOutputStream());
			
			salida.println("0"+"&"+nombre+"&"+nombre);
			salida.flush();
						
			jugador = nombre;
			
			frmDrawPum.setVisible(true);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public Point getMousePosition(){
		PointerInfo a = MouseInfo.getPointerInfo();
		Point b = a.getLocation();
		b.setLocation(b.getX()-frmDrawPum.getX()-17, b.getY()-frmDrawPum.getY()-40);
		return b;
	}
	
	
	public void updateJuego(){
		lbl_jugadores.setText(juego.getJugadoresString());
		
		lbl_msg.setText(juego.getMsgs());

		if(juego.getGanador()!=null){
			JOptionPane.showMessageDialog(null,"Ganador: "+juego.getGanador().getNombre());
		}
		
	}

	PanelPaint panel_paint;
	JLabel lbl_jugadores;
	JLabel lbl_msg;
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmDrawPum = new JFrame();
		frmDrawPum.setResizable(false);
		frmDrawPum.setTitle("Draw PUM");
		frmDrawPum.setBounds(100, 100, 708, 403);
		frmDrawPum.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		JPanel panel_colores = new JPanel();
		panel_colores.setBorder(null);
		
		JSlider slider_grosor = new JSlider();
		slider_grosor.setValue(25);
		slider_grosor.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				panel_paint.plumaChanged(slider_grosor.getValue());
			}
		});
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_3.setBackground(Color.WHITE);
		
		JButton btn_goma = new JButton("");
		btn_goma.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel_paint.setColor(Color.white);
			}
		});
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		GroupLayout groupLayout = new GroupLayout(frmDrawPum.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(panel_colores, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btn_goma)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(slider_grosor, GroupLayout.PREFERRED_SIZE, 218, GroupLayout.PREFERRED_SIZE))
						.addComponent(panel_3, GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
						.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(panel_3, GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(slider_grosor, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
								.addComponent(panel_colores, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
								.addComponent(btn_goma, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addGap(11))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(panel, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 241, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())))
		);
		
		lbl_jugadores = new JLabel("New label");
		lbl_jugadores.setVerticalAlignment(SwingConstants.TOP);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lbl_jugadores, GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lbl_jugadores, GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
					.addContainerGap())
		);
		panel.setLayout(gl_panel);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		panel_paint = new PanelPaint(this);
		panel_3.add(panel_paint);
		panel_paint.setBorder(null);
		panel_colores.setLayout(new GridLayout(1, 4, 0, 0));
		
		JButton btn_negro = new JButton("");
		btn_negro.setBackground(Color.BLACK);
		btn_negro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panel_paint.setColor(Color.BLACK);
			}
		});
		panel_colores.add(btn_negro);
		
		JButton btn_rojo = new JButton("");
		btn_rojo.setBackground(Color.RED);
		btn_rojo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panel_paint.setColor(Color.red);
			}
		});
		panel_colores.add(btn_rojo);
		
		JButton btn_azul = new JButton("");
		btn_azul.setBackground(Color.BLUE);
		btn_azul.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panel_paint.setColor(Color.blue);
			}
		});
		panel_colores.add(btn_azul);
		
		JButton btn_verde = new JButton("");
		btn_verde.setBackground(Color.GREEN);
		btn_verde.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panel_paint.setColor(Color.GREEN);
			}
		});
		panel_colores.add(btn_verde);
		
		JButton btn_naranja = new JButton("");
		btn_naranja.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panel_paint.setColor(Color.orange);
			}
		});
		btn_naranja.setBackground(Color.ORANGE);
		panel_colores.add(btn_naranja);
		
		JButton btn_amarillo = new JButton("");
		btn_amarillo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel_paint.setColor(Color.yellow);
			}
		});
		btn_amarillo.setBackground(Color.YELLOW);
		panel_colores.add(btn_amarillo);
		
		JButton btn_rosa = new JButton("");
		btn_rosa.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel_paint.setColor(Color.pink);
			}
		});
		btn_rosa.setBackground(Color.PINK);
		panel_colores.add(btn_rosa);
		
		txt_msg = new JTextField();
		txt_msg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				salida.println("1&"+jugador+"&"+txt_msg.getText());
				salida.flush();
				txt_msg.setText("");
			}
		});
		txt_msg.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
						.addComponent(txt_msg, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(txt_msg, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		lbl_msg = new JLabel("New label");
		lbl_msg.setBackground(Color.WHITE);
		scrollPane.setViewportView(lbl_msg);
		lbl_msg.setVerticalAlignment(SwingConstants.BOTTOM);
		panel_1.setLayout(gl_panel_1);
		frmDrawPum.getContentPane().setLayout(groupLayout);
	}
}
