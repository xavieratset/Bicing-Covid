/*
 * Classe per guardar cada element
 * de la web amb la informació del COVID
 */

package at.project.covid;

import java.sql.Timestamp;

public class CovidInfo {
	
	private String nom;
	private String codi;
	private Timestamp data_ini;
	private Timestamp data_fi;
	private String residencia;
	private float iepg_confirmat;
	private float r0_confirmat_m;
	private float taxa_casos_confirmat;
	private int casos_confirmat;
	private float taxa_pcr;
	private int pcr;
	private float perc_pcr_positives;
	private int ingressos_total;
	private int ingressos_critic;
	private int exitus;
	
	
	public CovidInfo() {
		super();
	}


	public CovidInfo(String nom, String codi, Timestamp data_ini, Timestamp data_fi, String residencia,
			float iepg_confirmat, float r0_confirmat_m, float taxa_casos_confirmat, int casos_confirmat, float taxa_pcr,
			int pcr, float perc_pcr_positives, int ingressos_total, int ingressos_critic, int exitus) {
		super();
		this.nom = nom;
		this.codi = codi;
		this.data_ini = data_ini;
		this.data_fi = data_fi;
		this.residencia = residencia;
		this.iepg_confirmat = iepg_confirmat;
		this.r0_confirmat_m = r0_confirmat_m;
		this.taxa_casos_confirmat = taxa_casos_confirmat;
		this.casos_confirmat = casos_confirmat;
		this.taxa_pcr = taxa_pcr;
		this.pcr = pcr;
		this.perc_pcr_positives = perc_pcr_positives;
		this.ingressos_total = ingressos_total;
		this.ingressos_critic = ingressos_critic;
		this.exitus = exitus;
	}


	public String getNom() {
		return nom;
	}


	public void setNom(String nom) {
		this.nom = nom;
	}


	public String getCodi() {
		return codi;
	}


	public void setCodi(String codi) {
		this.codi = codi;
	}


	public Timestamp getData_ini() {
		return data_ini;
	}


	public void setData_ini(Timestamp data_ini) {
		this.data_ini = data_ini;
	}


	public Timestamp getData_fi() {
		return data_fi;
	}


	public void setData_fi(Timestamp data_fi) {
		this.data_fi = data_fi;
	}


	public String getResidencia() {
		return residencia;
	}


	public void setResidencia(String residencia) {
		this.residencia = residencia;
	}


	public float getIepg_confirmat() {
		return iepg_confirmat;
	}


	public void setIepg_confirmat(float iepg_confirmat) {
		this.iepg_confirmat = iepg_confirmat;
	}


	public float getR0_confirmat_m() {
		return r0_confirmat_m;
	}


	public void setR0_confirmat_m(float r0_confirmat_m) {
		this.r0_confirmat_m = r0_confirmat_m;
	}


	public float getTaxa_casos_confirmat() {
		return taxa_casos_confirmat;
	}


	public void setTaxa_casos_confirmat(float taxa_casos_confirmat) {
		this.taxa_casos_confirmat = taxa_casos_confirmat;
	}


	public int getCasos_confirmat() {
		return casos_confirmat;
	}


	public void setCasos_confirmat(int casos_confirmat) {
		this.casos_confirmat = casos_confirmat;
	}


	public float getTaxa_pcr() {
		return taxa_pcr;
	}


	public void setTaxa_pcr(float taxa_pcr) {
		this.taxa_pcr = taxa_pcr;
	}


	public int getPcr() {
		return pcr;
	}


	public void setPcr(int pcr) {
		this.pcr = pcr;
	}


	public float getPerc_pcr_positives() {
		return perc_pcr_positives;
	}


	public void setPerc_pcr_positives(float perc_pcr_positives) {
		this.perc_pcr_positives = perc_pcr_positives;
	}


	public int getIngressos_total() {
		return ingressos_total;
	}


	public void setIngressos_total(int ingressos_total) {
		this.ingressos_total = ingressos_total;
	}


	public int getIngressos_critic() {
		return ingressos_critic;
	}


	public void setIngressos_critic(int ingressos_critic) {
		this.ingressos_critic = ingressos_critic;
	}


	public int getExitus() {
		return exitus;
	}


	public void setExitus(int exitus) {
		this.exitus = exitus;
	}


	/*
	 * Mètode toString modificat per adaptar el format del text a la pàgina web
	*/
	@Override
	public String toString() {
		return "&emsp;<b>Comarca: " + nom + "</b><br>&emsp;&emsp;data inici: " + data_ini + ",&emsp; data final: " + data_fi
				+ "<br>&emsp;&emsp;Residencia: " + residencia + ", &emsp;Risc de rebrot: " + iepg_confirmat + ", &emsp;r0 confirmat: " + r0_confirmat_m
				+ ", &emsp;taxa casos confirmats: " + taxa_casos_confirmat + ", &emsp;casos confirmats: " + casos_confirmat
				+ "<br>&emsp;&emsp;taxa PCR: " + taxa_pcr + ", &emsp;número PCR realitzades: " + pcr + ", &emsp;Percentatge PCR positives: " + perc_pcr_positives
				+ "<br>&emsp;&emsp;Ingressos totals = " + ingressos_total + ", &emsp;Ingressos crítics: " + ingressos_critic + ", &emsp;Defuncions=" + exitus + "<br><br>";
	}
	
	/*
	 * Mètode toString modificat per adaptar el format del text al
	 * format d'un missatge de Telegram
	*/
	public String telegramInfo() {
		return "CovidInfo\n[nom=" + nom + ",\n data_ini=" + data_ini + ",\n data_fi=" + data_fi
				+ ",\n residencia=" + residencia + ",\n iepg_confirmat=" + iepg_confirmat + ",\n r0_confirmat_m="
				+ r0_confirmat_m + ",\n taxa_casos_confirmat=" + taxa_casos_confirmat + ",\n casos_confirmat="
				+ casos_confirmat + ",\n taxa_pcr=" + taxa_pcr + ",\n pcr=" + pcr + ",\n perc_pcr_positives="
				+ perc_pcr_positives + ",\n ingressos_total=" + ingressos_total + ",\n ingressos_critic=" + ingressos_critic
				+ ",\n defuncions=" + exitus + "]";
	}
	
}
