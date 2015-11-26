import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Palabras {
	
	ArrayList<String>  palabras = new ArrayList<>();
	
	public Palabras(){
		try{
		BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("palabras.txt")));
		
		while(br.ready()){
			palabras.add(br.readLine());
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String getPalabra(){
		return palabras.get((int)(Math.random()*palabras.size()));
	}
	
	public String[] getOpciones(){
		ArrayList<String> res = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			String w;
			do{
				w = palabras.get((int)(Math.random()*palabras.size()));
			}while(res.contains(w));
			res.add(w);
		}
		return res.toArray(new String[0]);
	}
}
