package mx.tecabix.db.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.tecabix.db.entity.Empresa;
import mx.tecabix.db.repository.EmpresaRepository;
import mx.tecabix.db.service.EmpresaService;

@Service
public class EmpresaServiceImpl implements EmpresaService{

	@Autowired
	private EmpresaRepository empresaRespository;

	@Override
	public Empresa save(Empresa save) {
		save = empresaRespository.save(save);
		return save;
	}
}
