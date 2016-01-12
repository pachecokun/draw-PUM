import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class FrameJuego implements PaintCom,FrameOpciones.PalabraLister{

	private JFrame frmDrawPum;
	private JTextField txt_msg;
	Palabras palabras = new Palabras();
	Socket servidor;
	PrintWriter salida;
	ObjectInputStream entrada;
	volatile Juego juego = new Juego();
	String jugador = "";
	boolean turno = false;
	FrameOpciones opciones = null;
	
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
	public FrameJuego(String ip,int puerto,String nombre) throws Exception{
			
		servidor = new Socket(ip, puerto); 
		initialize();
		
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
}
	
	public Point getMousePosition(){
		PointerInfo a = MouseInfo.getPointerInfo();
		Point b = a.getLocation();
		b.setLocation(b.getX()-frmDrawPum.getX()-8, b.getY()-frmDrawPum.getY()-42);
		return b;
	}
	public void pintar(Point p, Color c, float s) {
		if(p!=null)
			salida.println("2&"+jugador+"&"+(int)p.getX()+"&"+(int)p.getY()+"&"+c.getRGB()+"&"+s);
		else
			salida.println("2&"+jugador+"&"+"0");
		salida.flush();
	}
	
	
	
	public void updateJuego(){
		if(!juego.getGanadores().isEmpty()){
			JOptionPane.showMessageDialog(null,"Ganadores: "+Arrays.toString(juego.getGanadores().toArray(new Jugador[0])).replace("[", "").replace("]", ""));
			frmDrawPum.dispose();
		}
		
		lbl_jugadores.setText(juego.getJugadoresString(jugador));
		
		lbl_msg.setText(juego.getMsgs());
		lbl_msg.validate();
		scroll_msg.validate();
		scroll_msg.getVerticalScrollBar().setValue(scroll_msg.getVerticalScrollBar().getMaximum()+10);
		
		if(juego.isLimpiar()){
			panel_paint.limpiar();
		}
		if(juego.getP()!=null){
			panel_paint.pintar(juego.getP(),juego.getC(), juego.getS());
		}
		else{
			panel_paint.p0 = null;
		}
		
		if(juego.getTurno()==0){
			if(juego.getJugadores().size()>2){
				btn_iniciar.setEnabled(true);
			}
		}else{
			btn_iniciar.setEnabled(false);
			if(juego.getJugadorTurno().getNombre().equals(jugador)){
				turno = true;
				if(juego.getPalabra()!=null){
					panel_paint.setPalabra(juego.getPalabra());
					panel_paint.setEnabled(true);
				}
				else{
					panel_paint.setPalabra("");
					opciones = opciones==null?new FrameOpciones(this):opciones;
				}
			}
			else{
				panel_paint.setPalabra("");
				panel_paint.setEnabled(false);
				turno = false;
			}
		}
		
	}

	PanelPaint panel_paint;
	JLabel lbl_jugadores;
	JLabel lbl_msg;
	JScrollPane scroll_msg;
	JButton btn_iniciar;
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmDrawPum = new JFrame();
		frmDrawPum.setResizable(false);
		frmDrawPum.setTitle("Draw PUM");
		frmDrawPum.setBounds(100, 100, 819, 501);
		frmDrawPum.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDrawPum.setLocationRelativeTo(null);
		
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
		
		JButton btnLimpiar = new JButton("Limpiar");
		btnLimpiar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(turno){
					salida.println("3&"+jugador+"&"+"0");
					salida.flush();
				}
			}
		});
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
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(slider_grosor, GroupLayout.PREFERRED_SIZE, 212, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(btnLimpiar, GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE))
						.addComponent(panel_3, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(panel_3, GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(btnLimpiar)
								.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
									.addComponent(slider_grosor, GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
									.addGroup(Alignment.LEADING, groupLayout.createParallelGroup(Alignment.LEADING, false)
										.addComponent(panel_colores, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
										.addComponent(btn_goma, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
							.addGap(10))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 184, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
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
				if(txt_msg.getText().length()>0){
					salida.println("1&"+jugador+"&"+txt_msg.getText());
					salida.flush();
					txt_msg.setText("");
				}
			}
		});
		txt_msg.setColumns(10);
		
		scroll_msg = new JScrollPane();
		
		btn_iniciar = new JButton("Iniciar juego");
		btn_iniciar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btn_iniciar.setEnabled(false);
				salida.println("4&"+jugador+"&0");
				salida.flush();
			}
		});
		btn_iniciar.setEnabled(false);
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
						.addComponent(btn_iniciar, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
						.addComponent(scroll_msg, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
						.addComponent(txt_msg, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(scroll_msg, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txt_msg, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btn_iniciar)
					.addContainerGap(9, Short.MAX_VALUE))
		);
		
		lbl_msg = new JLabel("New label");
		lbl_msg.setBackground(Color.WHITE);
		scroll_msg.setViewportView(lbl_msg);
		lbl_msg.setVerticalAlignment(SwingConstants.BOTTOM);
		panel_1.setLayout(gl_panel_1);
		frmDrawPum.getContentPane().setLayout(groupLayout);
	}

	@Override
	public void palabra(String palabra) {
		salida.println("5&"+jugador+"&"+palabra);
		salida.flush();
		opciones = null;
	}
}
