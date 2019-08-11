package controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import bean.AluguelBean;
import lookUp.AluguelLookUpList;
import lookUp.ClienteLookUpList;
import lookUp.EmpresaLookUpList;
import lookUp.MaquinaLookUp;
import model.AluguelModel;
import model.ClienteModel;
import model.EmpresaModel;
import model.MaquinaModel;
import resources.HUtil;

@ManagedBean(name = "aluguelMB")
@ViewScoped
public class AluguelController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5413477193703904733L;

	private List<AluguelLookUpList> alugueis;
	private List<AluguelLookUpList> filteredAlugueis;

	private AluguelBean aluguelSel;

	private ClienteLookUpList clienteSel;
	private EmpresaLookUpList empresaSel;
	private MaquinaLookUp maquinaSel2;
	private List<MaquinaLookUp> maquinas;

	private int step = 0;
	private boolean empresa = false;
	private boolean consultFrom = true;
	private List<Date> period;
	private float[] descontos = { 1.0f, 1.0f, 1.0f, 1.0f };

	@ManagedProperty("#{aluguelModel}")
	private AluguelModel aluguelService;

	@ManagedProperty("#{clienteModel}")
	private ClienteModel clienteService;

	@ManagedProperty("#{empresaModel}")
	private EmpresaModel empresaService;

	@ManagedProperty("#{maquinaModel}")
	private MaquinaModel maquinaService;

	@ManagedProperty("#{message}")
	private MessagesMB messageService;

	@PostConstruct
	public void init() {

	}

	public void nextStep(){
		if (clienteSel.getNome() != null) {
			if (this.step < 2) {
				this.step++;
			}else {
				System.out.println("Concluindo");
				save();
			}
		}else {
			
		}
		if (step == 2) {
			descontos[0] = HUtil.descNdias(getNdias());
			descontos[1] = HUtil.descHorasDias(getAluguelSel().getTempo_hd());
			descontos[2] = HUtil.descVip(getClienteSel().getN_alugueis());
			setTotal("" + getValor() * descontos[0] * descontos[1] * descontos[2]);
		}

	}

	public void backStep() {
		if (this.step > 0)
			this.step--;

	}

	public void save(){
		getAluguelSel().setN_funcionario_fk(AutenticationMB.getFuncionarioDaSessao().getN_funcionario());
		aluguelSel.setN_cliente_fk(getClienteSel().getN_cliente());
		aluguelSel.setN_maquina_fk(getMaquinaSel2().getN_maquina());
		aluguelSel.setData_ini(getPeriod().get(0));
		aluguelSel.setData_final(period.get(1));
		
		int id = aluguelService.create(aluguelSel);
		
		if (empresa) {
			aluguelSel.setN_aluguel(id);
			aluguelSel.setN_empresa_fk(getEmpresaSel().getN_empresa());
			aluguelService.setEmpresa(aluguelSel);
		}
		
		System.out.println("M�quina Alugada");
		messageService.info("M�quina Alugada");
		SystemMB.getSystem().redirect("/p/aluguel/listar.xhtml");
	}

	public void cancel() {

	}

	public String getForms(int i) {
		if (i == step)
			return "inherit";
		else
			return "none";
	}

	public String getDisabledAnterior() {
		if (0 != step)
			return "false";
		else
			return "true";
	}
	
	public String getValueProximo() {
		if (2 == step)
			return "Concluir";
		else
			return "Pr�ximo";
	}

	public void searchCliente() {
		String cpf = this.getClienteSel().getCpf();
		this.clienteSel = clienteService.read(this.clienteSel.getCpf());
		if (this.clienteSel != null)
			this.getAluguelSel().setN_cliente_fk(this.clienteSel.getN_cliente());
		else {
			this.getClienteSel().setCpf(cpf);
			System.out.println("Esse Cliente n�o Existe!");
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "Esse Cliente n�o Existe!"));

		}
	}

	public String showEmpresa() {
		if (empresa)
			return "inherit";
		else
			return "none";
	}

	public void searchEmpresa() {
		String cnpj = this.getEmpresaSel().getCnpj();
		this.empresaSel = empresaService.read(this.empresaSel.getCnpj());
		if (this.empresaSel != null)
			this.getAluguelSel().setN_empresa_fk(this.empresaSel.getN_empresa());
		else {
			this.getEmpresaSel().setCnpj(cnpj);
		}
	}

	public String showPlusCliente() {
		if (clienteSel.getNome() == null)
			return "inherit";
		else
			return "none";
	}

	public String showPlusEmpresa() {
		if (empresaSel.getRaz_social() == null)
			return "inherit";
		else
			return "none";
	}

	public String getMaquina_id_param() {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> paramMap = context.getExternalContext().getRequestParameterMap();
		String maquina_id = paramMap.get("maquina_id");

		return maquina_id;
	}

	public List<AluguelLookUpList> getAlugueis() {
		if (alugueis == null)
			alugueis = aluguelService.list();
		return alugueis;
	}

	public void setAlugueis(List<AluguelLookUpList> alugueis) {
		this.alugueis = alugueis;
	}

	public List<AluguelLookUpList> getFilteredAlugueis() {
		return filteredAlugueis;
	}

	public void setFilteredAlugueis(List<AluguelLookUpList> filteredAlugueis) {
		this.filteredAlugueis = filteredAlugueis;
	}

	public AluguelModel getAluguelService() {
		return aluguelService;
	}

	public void setAluguelService(AluguelModel aluguelService) {
		this.aluguelService = aluguelService;
	}

	public AluguelBean getAluguelSel() {
		if (aluguelSel == null)
			aluguelSel = new AluguelBean();
		return aluguelSel;
	}

	public void setAluguelSel(AluguelBean aluguelSel) {
		this.aluguelSel = aluguelSel;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public boolean isEmpresa() {
		return empresa;
	}

	public void setEmpresa(boolean empresa) {
		this.empresa = empresa;
	}

	public ClienteLookUpList getClienteSel() {
		if (clienteSel == null)
			clienteSel = new ClienteLookUpList();
		return clienteSel;
	}

	public void setClienteSel(ClienteLookUpList clienteSel) {
		this.clienteSel = clienteSel;
	}

	public EmpresaLookUpList getEmpresaSel() {
		if (empresaSel == null)
			empresaSel = new EmpresaLookUpList();
		return empresaSel;
	}

	public void setEmpresaSel(EmpresaLookUpList empresaSel) {
		this.empresaSel = empresaSel;
	}

	public MaquinaLookUp getMaquinaSel2() {
		if (maquinaSel2 == null) {
			maquinaSel2 = new MaquinaLookUp();
			if (consultFrom) {
				String i = getMaquina_id_param();
				if (i != null) {
					maquinaSel2.setN_maquina(Integer.parseInt(i));
				} else {
					consultFrom = false;
				}
			}
		}
		return maquinaSel2;
	}

	public void setClienteService(ClienteModel clienteService) {
		this.clienteService = clienteService;
	}

	public void setEmpresaService(EmpresaModel empresaService) {
		this.empresaService = empresaService;
	}

	public void setMessageService(MessagesMB messageService) {
		this.messageService = messageService;
	}

	public MessagesMB getMessageService() {
		return messageService;
	}

	public void setMaquinaService(MaquinaModel maquinaService) {
		this.maquinaService = maquinaService;
	}

	public List<Date> getPeriod() {
		if (period == null) {
			period = new ArrayList<Date>();
		}
		if (period.size() == 0) {
			period.add(new Date());
		}
		if (period.size() == 1) {
			period.add(period.get(0));
		}
		return period;
	}

	public void setPeriod(List<Date> period) {
		this.period = period;
	}

	public List<MaquinaLookUp> getMaquinas() {
		if (maquinas == null) {
			maquinas = maquinaService.list();
		}
		return maquinas;
	}

	public String getMaquinaSel() {
		return getMaquinaSel2().toString();
	}

	public void setMaquinaSel2(MaquinaLookUp maquinaSel2) {
		this.maquinaSel2 = maquinaSel2;
	}

	public void setMaquinaSel(String maquinaSel) {
		String a[] = maquinaSel.split(" - ");
		for (int i = 0; i < getMaquinas().size(); i++) {
			if (a[0].equals("" + maquinas.get(i).getN_registro())) {
				maquinaSel2 = maquinas.get(i);
			}
		}

	}

	public List<String> getListHoras() {
		List<String> list = new ArrayList<String>();
		for (int i = 1; i <= 12; i++) {
			list.add("" + i + "h");
		}
		return list;
	}

	public String getHoras() {
		System.out.println("buscando horas");
		String r = "" + (int) getAluguelSel().getTempo_hd() + "h";
		return r;
	}

	public void setHoras(String horas) {
		horas = horas.replaceAll("[^0-9]", "");
		try {
			getAluguelSel().setTempo_hd(Integer.parseInt(horas));
		} catch (NumberFormatException e) {
			getAluguelSel().setTempo_hd(0);
		}

	}

	public int getNdias() {
		return (int) ((getPeriod().get(1).getTime() - getPeriod().get(0).getTime()) / 86400000 + 1);
	}

	public float getValor() {
		return getNdias() * getMaquinaSel2().getValor_diaria();
	}

	public String getDescNdias() {
		return String.format("%.2f %%", descontos[0] * 100);
	}

	public String getDescHdias() {
		return String.format("%.2f %%", descontos[1] * 100);
	}

	public String getDescFidel() {
		return String.format("%.2f %%", descontos[2] * 100);
	}

	public String getDescPers() {
		return String.format("%.2f %%", descontos[3] * 100);
	}

	public float valNdiasf() {
		return getValor() * descontos[0];
	}

	public float valHdiasf() {
		return valNdiasf() * descontos[1];
	}

	public float valFidelf() {
		return valHdiasf() * descontos[2];
	}

	public float valPersf() {
		return valFidelf() * descontos[3];
	}

	public String getValNdias() {
		return String.format("R$%.2f", valNdiasf());
	}

	public String getValHdias() {
		return String.format("R$%.2f", valHdiasf());
	}

	public String getValFidel() {
		return String.format("R$%.2f", valFidelf());
	}

	public String getValPers() {
		return String.format("R$%.2f", valPersf());
	}

	public String getDelNdias() {
		return String.format("R$%.2f", valNdiasf() - getValor());
	}

	public String getDelHdias() {
		return String.format("R$%.2f", valHdiasf() - valNdiasf());
	}

	public String getDelFidel() {
		return String.format("R$%.2f", valFidelf() - valHdiasf());
	}

	public String getDelPers() {
		return String.format("R$%.2f", valPersf() - valFidelf());
	}

	public void setTotal(String total) {
		total = total.replaceAll("[^0-9.]", "");
		getAluguelSel().setVal_contratado(Float.parseFloat(total));
		descontos[3] = getAluguelSel().getVal_contratado() / valFidelf();
		System.out.println("" + descontos[3] + " " + getAluguelSel().getVal_contratado() + " " + valFidelf());

	}

	public String getTotal() {
		return String.format("R$%.2f", getAluguelSel().getVal_contratado());
	}

}
