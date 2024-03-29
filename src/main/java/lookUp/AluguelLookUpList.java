package lookUp;

import java.io.Serializable;
import java.util.Date;


public class AluguelLookUpList implements Serializable  {
	private int n_aluguel;
	private Date data_ini;
	private Date data_entregue;
	private Date data_final;
	
	private int n_registro;
	private float potencia;
	private float val_contratado;
	private float valor_pago;
	public Date getData_ini() {
		return data_ini;
	}
	public void setData_ini(Date data_ini) {
		this.data_ini = data_ini;
	}
	public Date getData_entregue() {
		return data_entregue;
	}
	public void setData_entregue(Date data_entregue) {
		this.data_entregue = data_entregue;
	}
	public int getN_registro() {
		return n_registro;
	}
	public void setN_registro(int n_registro) {
		this.n_registro = n_registro;
	}
	public float getPotencia() {
		return potencia;
	}
	public void setPotencia(float potencia) {
		this.potencia = potencia;
	}
	public float getVal_contratado() {
		return val_contratado;
	}
	public void setVal_contratado(float val_contratado) {
		this.val_contratado = val_contratado;
	}
	public float getValor_pago() {
		return valor_pago;
	}
	public void setValor_pago(float valor_pago) {
		this.valor_pago = valor_pago;
	}
	
	
	public int getN_aluguel() {
		return n_aluguel;
	}
	public void setN_aluguel(int n_aluguel) {
		this.n_aluguel = n_aluguel;
	}
	
	public Date getData_final() {
		return data_final;
	}
	public void setData_final(Date data_final) {
		this.data_final = data_final;
	}
	
	@Override
	public String toString() {
		return "AluguelLookUpList [data_ini=" + data_ini + ", data_entregue=" + data_entregue + ", n_registro="
				+ n_registro + ", potencia=" + potencia + ", val_contratado=" + val_contratado + ", valor_pago="
				+ valor_pago + "]";
	}
	
	
}
