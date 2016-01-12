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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;


public class Servidor extends JFrame {

	ServerSocket ss;
	CopyOnWriteArrayList<Socket> conexiones = new CopyOnWriteArrayList<>();
	CopyOnWriteArrayList<ObjectOutputStream> salidas = new CopyOnWriteArrayList<>();
	CopyOnWriteArrayList<BufferedReader> entradas = new CopyOnWriteArrayList<>();
	volatile Juego juego = new Juego();
	int tiempo = 60;
	Boolean enviando = false;
	
	Timer tiempoTurno = new Timer(1000, new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(tiempo==60||tiempo==30||tiempo==10||tiempo<6){
				juego.addMsg("", "Quedan "+tiempo+" segundos");
				updateClientes();
			}
			if(tiempo==0){
				juego.turno();
				juego.setLimpiar(true);
				juego.addMsg("", juego.getJugadorTurno().getNombre()+" seleccionando palabra");
				updateClientes();
				tiempoTurno.stop();
			}
			tiempo--;
		}
	});
	
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
								
								if(juego.getTurno()>0&&juego.getPalabra()!=null&&cadena(msg).equals(cadena(juego.getPalabra()))){
								
									for(Jugador j:juego.getJugadores()){
										if(remitente.equals(j.getNombre())){
											if(j!=juego.getJugadorTurno()){
												j.setPuntos(j.getPuntos()+1);
												juego.addMsg("", j.getNombre()+" adivinó la palabra");
												tiempoTurno.stop();
												juego.turno();
												juego.setLimpiar(true);
												juego.addMsg("", juego.getJugadorTurno().getNombre()+" seleccionando palabra");
											}
											break;
										}
									}
								}
								else if(juego.getPalabra()==null||!cadena(msg).contains(cadena(juego.getPalabra()))){
									juego.addMsg(remitente,msg);
								}
								System.out.println("Nuevo mensaje: "+aux);
							}
							else if(c=='2'){
								if(msgs.length>3){
									juego.setP(new Point(Integer.parseInt(msgs[2]),Integer.parseInt(msgs[3])));
									juego.setC(new Color(Integer.parseInt(msgs[4])));
									juego.setS(Float.parseFloat(msgs[5]));
								}
							}
							else if(c=='3'){
								juego.setLimpiar(true);
							}
							else if(c=='4'){
								juego.turno();
								juego.addMsg("", juego.getJugadorTurno().getNombre()+" seleccionando palabra");
								System.out.println("Juego iniciado, turno de "+juego.getJugadorTurno().getNombre());
							}
							else if(c=='5'){
								juego.setPalabra(msg);
								juego.addMsg("", "La palabra ha sido seleccionada");
								tiempo = 60;
								tiempoTurno.start();
							}
							
							updateClientes();
							
							juego.setP(null);
							juego.setC(null);
							juego.setS(0);
							juego.setLimpiar(false);
							
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	});
	
	public void updateClientes(){
		while(enviando);
		enviando = true;
		for(ObjectOutputStream s2:salidas){
			try{
				s2.reset();
				s2.writeObject(juego);
				s2.flush();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		enviando = false;
	}
	
	String cadena(String s){
		return s.trim().toLowerCase().replace("á","a").replace("é","e").replace("í","i").replace("ó","o").replace("ú","u");
	}
	
	private JPanel contentPane;
	private JTextField txt_puerto;
	private JTextField txt_ip;
	private JTextField txt_puerto_1;
	private JTextField txt_nombre;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
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
		setBounds(100, 100, 291, 346);
		setLocationRelativeTo(null);
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
				try{
					new FrameJuego(txt_ip.getText(), Integer.parseInt(txt_puerto_1.getText()), txt_nombre.getText());
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Error al iniciar conexión, verifique que los parámetros sean correctos");
				}
			}
		});
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(label_1, GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
								.addComponent(lblIp, GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
								.addComponent(lblNombre, GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(txt_nombre, 113, 142, Short.MAX_VALUE)
								.addComponent(txt_ip, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
								.addComponent(txt_puerto_1, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE)))
						.addComponent(btn_cliente, Alignment.TRAILING))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(txt_ip, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblIp))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(txt_puerto_1, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE))
					.addGap(9)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNombre)
						.addComponent(txt_nombre, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
					.addComponent(btn_cliente)
					.addContainerGap())
		);
		panel.setLayout(gl_panel);
		
		JLabel lblPuerto = new JLabel("Puerto:");
		
		txt_puerto = new JTextField();
		txt_puerto.setColumns(10);
		
		JButton btn_servidor = new JButton("Iniciar Servidor");
		btn_servidor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					iniciarServidor(Integer.parseInt(txt_puerto.getText()));
					btn_servidor.setEnabled(false);
					txt_puerto.setEnabled(false);
					System.out.println("Servidor iniciado");
				}catch(Exception e){
					JOptionPane.showMessageDialog(null, "No se pudo iniciar servidor, verifique qeu el puerto no está ocupado.");
				}
			}
		});
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
						.addComponent(btn_servidor)
						.addGroup(Alignment.LEADING, gl_panel_1.createSequentialGroup()
							.addComponent(lblPuerto)
							.addPreferredGap(ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
							.addComponent(txt_puerto, GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE)))
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
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
					.addGap(10)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
						.addComponent(panel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);
	}
	
	public void iniciarServidor(int puerto)throws Exception{
		ss = new ServerSocket(puerto);
		threadServer.start();
		threadClientes.start();
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
