import java.awt.Color;
import java.awt.Point;
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
	private ArrayList<Jugador> ganadores = new ArrayList<>();
	private ArrayList<Mensaje> msgs = new ArrayList<>();
	
	private int turno;
	private String palabra;
	private Jugador jugadorTurno;
	private Point p;
	private Color c;
	private float s;
	boolean limpiar;
	
	public void turno(){
		turno++;
		if(turno<=jugadores.size()*2){
			jugadorTurno = jugadores.get((turno-1)%jugadores.size());
			palabra = null;
		}
		else{
			ArrayList<Jugador> aux = new ArrayList<>();
			for(Jugador j:jugadores){
				if(aux.isEmpty()){
					aux.add(j);
				}
				else if(j.getPuntos()>aux.get(0).getPuntos()){
					aux.clear();
					aux.add(j);
				}
				else if(j.getPuntos()==aux.get(0).getPuntos()){
					aux.add(j);
				}
			}
			ganadores.clear();
			ganadores.addAll(aux);
		}
	}

	public ArrayList<Jugador> getJugadores() {
		return jugadores;
	}
	
	public String getJugadoresString(String jugador){
		String r = "<html>";
		for(Jugador j:jugadores){
			if(jugadorTurno==j){
				r+="<font color = green>";
			}
			if(j.getNombre().equals(jugador)){
				r+="<p><b>"+j.getPuntos()+" - "+j.getNombre()+"</b></p>";
			}
			else{
				r+= "<p>"+j.getPuntos()+" - "+j.getNombre()+"</p>";
			}
			if(jugadorTurno==j){
				r+="</font>";
			}
		}
		r += "</html>";
		return r;
	}

	
	
	public boolean isLimpiar() {
		return limpiar;
	}

	public void setLimpiar(boolean limpiar) {
		this.limpiar = limpiar;
	}

	public void setJugadores(ArrayList<Jugador> jugadores) {
		this.jugadores = jugadores;
	}

	public String getMsgs() {
		String r = "<html>";
		for(Mensaje m:msgs){
			if(m.getRemitente().isEmpty()){
				r += "<p><font color = green><b>"+m.getMsg()+"</b></font></p>";
			}
			else{
				r+= "<p>"+m.getRemitente()+" : "+m.getMsg()+"</p>";
			}
		}
		r += "</html>";
		return r;
	}

	public void addMsg(String remitente,String msg){
		while(msgs.size()>=11){
			msgs.remove(0);
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
	
	public ArrayList<Jugador> getGanadores() {
		return ganadores;
	}

	public Point getP() {
		return p;
	}

	public void setP(Point p) {
		this.p = p;
	}

	public Color getC() {
		return c;
	}

	public void setC(Color c) {
		this.c = c;
	}

	public float getS() {
		return s;
	}

	public void setS(float s) {
		this.s = s;
	}

	public void setMsgs(ArrayList<Mensaje> msgs) {
		this.msgs = msgs;
	}
	
	
}
