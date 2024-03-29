package controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;

import bean.ClienteBean;
import bean.EndClienteBean;
import bean.TelClienteBean;
import lookUp.ClienteLookUpList;
import model.ClienteModel;
import model.EndClienteModel;
import model.TelClienteModel;

@ManagedBean(name = "clienteMB")
@ViewScoped
public class ClienteControler implements Serializable{
	//--------------------------Sel Atributes -------------------------//
	private List<ClienteLookUpList> clientes;
	private List<ClienteLookUpList> filteredClientes;
	private List<EndClienteBean> enderecos;
	private List<TelClienteBean> tels;
	private List<TelClienteBean> telsToRemove;
	private ClienteLookUpList selectedCliente;
	private ClienteBean cliente;
	private TelClienteBean cliente_tel;
	private EndClienteBean cliente_end;
	
	//-------------------------Atributes--------------------------------//
	private String cliente_id_param;
	private Boolean clienteEdicao;

	
	//------------------------Managed Propertys-------------------------//
	@ManagedProperty("#{clienteModel}")
	private ClienteModel clienteService;
	@ManagedProperty("#{endClienteModel}")
	private EndClienteModel endClienteService;
	@ManagedProperty("#{telClienteModel}")
	private TelClienteModel telClienteService;

	@PostConstruct
	public void init() {
		clientes = clienteService.list();
		cliente = null;
		cliente_id_param = null;
		clienteEdicao = false;
		cliente_end = null;
		cliente_tel = null;
		enderecos = null;
		tels = null;
	}
	
	public void setClienteService(ClienteModel clienteService) {
		this.clienteService = clienteService;
	}

	public ClienteModel getClienteService() {
		return clienteService;
	}
	
	public EndClienteModel getEndClienteService() {
		return endClienteService;
	}

	public void setEndClienteService(EndClienteModel endClienteService) {
		this.endClienteService = endClienteService;
	}

	public TelClienteModel getTelClienteService() {
		return telClienteService;
	}

	public void setTelClienteService(TelClienteModel telClienteService) {
		this.telClienteService = telClienteService;
	}
	
	//---------------------- Sels Get Set --------------------------//

	public ClienteLookUpList getSelectedCliente() {
		return selectedCliente;
	}

	public void setSelectedCliente(ClienteLookUpList selectedCliente) {
		this.selectedCliente = selectedCliente;
	}

	public List<ClienteLookUpList> getClientes() {
		return clientes;
	}

	public List<ClienteLookUpList> getFilteredClientes() {
		return filteredClientes;
	}

	public void setFilteredClientes(List<ClienteLookUpList> filteredClientes) {
		this.filteredClientes = filteredClientes;
	}
	
	public void setCliente_tel(TelClienteBean cliente_tel) {
		this.cliente_tel = cliente_tel;
	}
	
	public TelClienteBean getCliente_tel() {
		if (cliente_tel == null)
			cliente_tel = new TelClienteBean();
		return cliente_tel;
	}
	
	public EndClienteBean getCliente_end() {
		if (cliente_end == null)
			cliente_end = new EndClienteBean();
		return cliente_end;
	}

	public void setCliente_end(EndClienteBean cliente_end) {
		this.cliente_end = cliente_end;
	}
	
	public ClienteBean getCliente() {
		String param_id = getCliente_id_param();
		if (cliente == null) {
			if (param_id == null) {
				cliente = new ClienteBean();
				return cliente;
			}
			cliente = clienteService.read(getCliente_id_param());
		}
		return cliente;
	}

	public void setCliente(ClienteBean cliente) {
		this.cliente = cliente;
	}

	public List<EndClienteBean> getEnderecos() {
		if (enderecos == null)
			enderecos = endClienteService.readEnderecos(getCliente_id_param());
		return enderecos;
	}

	public void setEnderecos(List<EndClienteBean> enderecos) {
		this.enderecos = enderecos;
	}

	public List<TelClienteBean> getTels() {
		if (tels == null)
			tels = telClienteService.readTels(getCliente_id_param());
		return tels;
	}

	public void setTels(List<TelClienteBean> tels) {
		this.tels = tels;
	}
	
	//------------------------ Gets Sets -----------------------//

	public Boolean getClienteEdicao() {
		return clienteEdicao;
	}

	public void setClienteEdicao(Boolean clienteEdicao) {
		this.clienteEdicao = clienteEdicao;
	}

	public String getCliente_id_param() {
		if (cliente_id_param == null) {
			FacesContext context = FacesContext.getCurrentInstance();
			Map<String, String> paramMap = context.getExternalContext().getRequestParameterMap();
			cliente_id_param = paramMap.get("cliente_id");
		}

		return cliente_id_param;
	}

	public void setCliente_id_param(String cliente_id_param) {
		this.cliente_id_param = cliente_id_param;
	}

	//---------------------- Methods ------------------------------------//

	public void onEnderecoRowEdit(RowEditEvent event) {
		EndClienteBean end_edited = (EndClienteBean) event.getObject();
		end_edited.setEdited(true);
	}

	public void onEnderecoRowCancel(RowEditEvent event) {
		EndClienteBean end_edited = (EndClienteBean) event.getObject();

		if (end_edited.getN_end() != -1)
			endClienteService.delete(end_edited.getN_end());

		enderecos.remove(enderecos.indexOf(end_edited));

		PrimeFaces.current().ajax().update(":form3:enderecos");
	}

	public void onEnderecoAddNew() {
		enderecos.add(cliente_end);
		cliente_end = new EndClienteBean();
		PrimeFaces.current().executeScript("PF('dlg2').hide()");

		System.out.println("Novo Endereço Adicionado.");
	}

	public void onTelRowEdit(RowEditEvent event) {
		TelClienteBean tel_edited = (TelClienteBean) event.getObject();
		tel_edited.setEdited(true);
	}

	public void onTelRowCancel(RowEditEvent event) {
		TelClienteBean tel_edited = (TelClienteBean) event.getObject();

		if (tel_edited.getN_telefone() != -1) {
			telsToRemove  = telsToRemove == null?new ArrayList<TelClienteBean>():telsToRemove;
			telsToRemove.add(tel_edited);
			//telClienteService.delete(tel_edited.getN_telefone());
		}

		tels.remove(tels.indexOf(tel_edited));

		PrimeFaces.current().ajax().update(":form2:tels");
	}

	public void onTelAddNew() {
		this.tels.add(cliente_tel);
		cliente_tel = new TelClienteBean();
		PrimeFaces.current().executeScript("PF('dlg1').hide()");

		System.out.println("Novo Número Adicionado.");
	}

	public void createCliente() throws Exception {
		if (cliente != null) {
			if (clienteService.create(cliente)) {
				cliente_end.setN_cliente_fk(cliente.getN_cliente());
				cliente_tel.setN_cliente_fk(cliente.getN_cliente());
				if (endClienteService.create(cliente_end) && telClienteService.create(cliente_tel)) {
					System.out.println("Cliente cadastrado com sucesso.");
					PrimeFaces.current().executeScript(
							"Swal.fire({ type: 'success', title: 'Tudo certo...', text: 'Cliente cadastrado com sucesso.', timer: 4000,"
									+ "showConfirmButton: false})");
					Thread.sleep(5000);
					SystemMB.getSystem().redirect("/p/cliente/listar.xhtml");
					return;
				}
			}
			System.out.println("Error ao tentar cadastrar cliente.");
			PrimeFaces.current().executeScript(
					"Swal.fire({ type: 'error', title: 'Oopss...', text: 'Impossível cadastrar cliente.'})");
		}
	}

	public void salvarCliente() {
		for (EndClienteBean end : enderecos) {
			if (end.getN_end() == -1) {
				end.setN_cliente_fk(cliente.getN_cliente());
				endClienteService.create(end);
				continue;
			}
			if (end.isEdited())
				endClienteService.update(end);
		}

		for (TelClienteBean tel : tels) {
			if (tel.getN_telefone() == -1) {
				tel.setN_cliente_fk(cliente.getN_cliente());
				telClienteService.create(tel);
				continue;
			}
			if (tel.isEdited())
				telClienteService.update(tel);
		}
		if(telsToRemove != null)
		for (TelClienteBean tel : telsToRemove) {
			telClienteService.delete(tel.getN_telefone());
		}

		clienteService.update(cliente);
		PrimeFaces.current().executeScript(
				"Swal.fire({ type: 'success', title: 'Tudo certo...', text: 'Cliente atualizado.', timer: 4000})");
	}

	public void excluirTel() {
		for (TelClienteBean tel : tels) {
			if (tel.getN_telefone() != -1)
				telClienteService.delete(tel.getN_telefone());
		}
		if(telsToRemove!=null) {
			for (TelClienteBean tel : telsToRemove) {
				if (tel.getN_telefone() != -1)
					telClienteService.delete(tel.getN_telefone());
			}
		}
	}

	public void excluirEndereco() {
		for (EndClienteBean end : enderecos) {
			if (end.getN_end() != -1)
				endClienteService.delete(end.getN_end());
		}
	}

	public void excluirCliente() {
		if (cliente != null) {
//			if (enderecos.size() > 0)
				excluirEndereco();
//			if (tels.size() > 0)
				excluirTel();

			clienteService.delete(cliente.getN_cliente());
			PrimeFaces.current().executeScript(
					"Swal.fire({ type: 'success', title: 'Tudo certo...', text: 'Cliente excluído.', timer: 4000})");
			return;
		}
		System.out.println("Error: Cliente não foi instanciado.");
	}

	public void setClientCpf(String cpf) {
		cliente.setCpf(cpf.replaceAll("[^0-9]", ""));
	}

	public String getClientCpf() {
		String r = cliente.getCpf();
		if (r != null)
			return r;
//			return r.substring(0, 2) + "." + r.substring(3, 5) + "." + r.substring(6, 8) + "-" + r.substring(9, 11);
		return "";
	}

	public void setClientPhone(String phone) {
		phone = phone.replaceAll("[^0-9]", "");
		try {
			cliente_tel.setNumero_tel(Integer.parseInt(phone));
		} catch (NumberFormatException e) {

		}

	}

	public String getClientPhone() {
		return String.format("%d-%d", cliente_tel.getNumero_tel() / 10000, cliente_tel.getNumero_tel() % 10000);
	}
	
	public String getClientPhone(int phone) {
		return String.format("%d-%d", phone / 10000, phone % 10000);
	}

}
