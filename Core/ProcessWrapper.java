package Core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ProcessWrapper {
	
	public OutputStreamWriter osw;
	public BufferedReader br;
	public Process p;
	
	public ProcessWrapper(Process p) {
		osw = new OutputStreamWriter(p.getOutputStream());
		br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		this.p = p;
	}

}
