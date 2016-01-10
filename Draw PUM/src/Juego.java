import java.io.Serializable;
import java.util.ArrayList;

public class Juego implements Serializable{
	
	public class Mensaje implements Serializable{
		private String msg;
		private String remitente;
		public Mensaje(String msg, String remitente) {
			this.msg = msg;
			this.remitente = remitente;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String nombre) {
			this.msg = nombre;
		}
		public String getRemitente() {
			return remitente;
		}
		public void setRemitente(String remitente) {
			this.remitente = remitente;
		}
		
	}

	private ArrayList<Jugador> jugadores = new ArrayList<>();
	private ArrayList<Mensaje> msgs = new ArrayList<>();
	
	private int turno = -1;
	private String palabra = "";
	private Jugador jugadorTurno;
	private Jugador ganador;
	private byte[] imagen;
	
	public void turno(){
		turno++;
		if(turno<jugadores.size()){
			jugadorTurno = jugadores.get(turno);
		}
	}

	public ArrayList<Jugador> getJugadores() {
		return jugadores;
	}
	
	public String getJugadoresString(){
		String r = "<html>";
		for(Jugador j:jugadores){
			r+= j.getPuntos()+" - "+j.getNombre()+"<br/>";
		}
		r += "</html>";
		return r;
	}

	public void setJugadores(ArrayList<Jugador> jugadores) {
		this.jugadores = jugadores;
	}

	public String getMsgs() {
		String r = "<html>";
		for(Mensaje m:msgs){
			r+= "<br/>"+m.getRemitente()+" : "+m.getMsg();
		}
		r += "</html>";
		return r;
	}

	public void addMsg(String remitente,String msg){
		while(msgs.size()>=50){
			msgs.remove(484);
		}
		msgs.add(new Mensaje(msg, remitente));
	}

	public int getTurno() {
		return turno;
	}

	public void setTurno(int turno) {
		this.turno = turno;
	}

	public String getPalabra() {
		return palabra;
	}

	public void setPalabra(String palabra) {
		this.palabra = palabra;
	}

	public Jugador getJugadorTurno() {
		return jugadorTurno;
	}

	public void setJugadorTurno(Jugador jugadorTurno) {
		this.jugadorTurno = jugadorTurno;
	}

	public Jugador getGanador() {
		return ganador;
	}

	public void setGanador(Jugador ganador) {
		this.ganador = ganador;
	}

	public byte[] getImagen() {
		return imagen;
	}

	public void setImagen(byte[] imagen) {
		this.imagen = imagen;
	}
	
	
}
