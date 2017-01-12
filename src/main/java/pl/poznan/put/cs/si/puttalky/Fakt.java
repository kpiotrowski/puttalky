package pl.poznan.put.cs.si.puttalky;

import java.util.HashSet;
import java.util.Set;

/** Author: agalawrynowicz<br>
 * Date: 19-Dec-2016 */

public class Fakt {
	
	private String nazwa;
	private Set<String> wartosc;
	
	public Fakt(){}
	
	public Fakt(String nazwa, Set<String> wartosc)
	{
		this.nazwa=nazwa;
		this.wartosc = wartosc;
	}
	public Fakt(String nazwa, String wartosc){
		this.nazwa=nazwa;
		this.wartosc = new HashSet<String>();
		this.wartosc.add(wartosc);
	}

	
	public String getNazwa() {
        return this.nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public Set<String> getWartosc() {
        return this.wartosc;
    }

    public void setWartosc(Set<String> wartosc) {
        this.wartosc = wartosc;
    }
	

}
