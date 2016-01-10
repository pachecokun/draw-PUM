import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;


public class Servidor extends JFrame {

	ServerSocket ss;
	CopyOnWriteArrayList<Socket> conexiones = new CopyOnWriteArrayList<>();
	CopyOnWriteArrayList<ObjectOutputStream> salidas = new CopyOnWriteArrayList<>();
	CopyOnWriteArrayList<BufferedReader> entradas = new CopyOnWriteArrayList<>();
	volatile Juego juego = new Juego();
	
	Thread threadServer = new Thread(new Runnable() {
		@Override
		public void run() {
			try{
				while(true){
					Socket s = ss.accept();
					ObjectOutputStream o = new ObjectOutputStream(s.getOutputStream());
					o.writeObject(juego);
					o.flush();
					salidas.add(o);
					BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
					entradas.add(br);
					System.out.println("Cliente conectado");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	});
	
	Thread threadClientes = new Thread(new Runnable() {
		
		@Override
		public void run() {
			while(true){
				for(BufferedReader br:entradas){
					try {
						while(br.ready()){
							String aux = br.readLine();
							String msgs[] = aux.split("&");
							char c = aux.charAt(0);
							String msg = msgs[2];
							String remitente = msgs[1];
							
							if(c=='0'){
								juego.getJugadores().add(new Jugador(msg));
								System.out.println("Nuevo jugador: "+msg);
							}
							else if(c=='1'){
								juego.addMsg(remitente,msg);
								System.out.println("Nuevo mensaje: "+aux);
							}
							else if(c=='2'){
								if(msgs.length>3){
									juego.setP(new Point(Integer.parseInt(msgs[2]),Integer.parseInt(msgs[3])));
									juego.setC(new Color(Integer.parseInt(msgs[4])));
									juego.setS(Float.parseFloat(msgs[5]));
									System.out.println("Nueva imagen");
								}
							}
							for(ObjectOutputStream s2:salidas){
								s2.reset();
								s2.writeObject(juego);
								s2.flush();
							}
							System.out.println(salidas.size()+" Clientes actualizados");
							juego.setP(null);
							juego.setC(null);
							juego.setS(0);
							
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	});
	
	private JPanel contentPane;
	private JTextField txt_puerto;
	private JTextField txt_ip;
	private JTextField txt_puerto_1;
	private JTextField txt_nombre;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Servidor frame = new Servidor();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Servidor() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 242, 282);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Cliente", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Servidor", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JLabel lblIp = new JLabel("IP:");
		
		txt_ip = new JTextField();
		txt_ip.setColumns(10);
		
		JLabel label_1 = new JLabel("Puerto:");
		
		txt_puerto_1 = new JTextField();
		txt_puerto_1.setColumns(10);
		
		JLabel lblNombre = new JLabel("Nombre:");
		
		txt_nombre = new JTextField();
		txt_nombre.setColumns(10);
		
		JButton btn_cliente = new JButton("Conectar");
		btn_cliente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new FrameJuego(txt_ip.getText(), Integer.parseInt(txt_puerto_1.getText()), txt_nombre.getText());
			}
		});
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(btn_cliente)
						.addGroup(Alignment.LEADING, gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNombre)
								.addComponent(lblIp, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE))
							.addGap(14)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(txt_ip, GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
								.addComponent(txt_puerto_1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
								.addComponent(txt_nombre, 113, 113, Short.MAX_VALUE))))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(3)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblIp)
						.addComponent(txt_ip, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(9)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(label_1)
						.addComponent(txt_puerto_1, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNombre)
						.addComponent(txt_nombre, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btn_cliente)
					.addContainerGap(17, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		
		JLabel lblPuerto = new JLabel("Puerto:");
		
		txt_puerto = new JTextField();
		txt_puerto.setColumns(10);
		
		JButton btn_servidor = new JButton("Iniciar Servidor");
		btn_servidor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				iniciarServidor(Integer.parseInt(txt_puerto.getText()));
				btn_servidor.setEnabled(false);
				System.out.println("Servidor iniciado");
			}
		});
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
						.addComponent(btn_servidor)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblPuerto)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(txt_puerto, GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPuerto)
						.addComponent(txt_puerto, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(btn_servidor)
					.addContainerGap())
		);
		panel_1.setLayout(gl_panel_1);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(10)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(panel_1, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
						.addComponent(panel, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 200, Short.MAX_VALUE))
					.addContainerGap(246, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);
	}
	
	public void iniciarServidor(int puerto){
		try {
			
			ss = new ServerSocket(puerto);
			threadServer.start();
			threadClientes.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void detenerServidor(){
		try {
			threadServer.stop();
			threadClientes.stop();
			ss.close();
			ss = null;
			conexiones.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
