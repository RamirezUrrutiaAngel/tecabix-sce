package mx.tecabix.db.service.impl;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import mx.tecabix.db.GenericSeviceImpl;
import mx.tecabix.db.entity.CajaRegistroTransaccionItem;
import mx.tecabix.db.repository.CajaRegistroTransaccionItemRepository;
import mx.tecabix.db.service.CajaRegistroTransaccionItemService;

public class CajaRegistroTransaccionItemServiceImpl extends GenericSeviceImpl<CajaRegistroTransaccionItem, Long>
implements CajaRegistroTransaccionItemService{

	@Autowired
	private CajaRegistroTransaccionItemRepository cajaRegistroTransaccionItemRepository;
	
	@Override
	@PostConstruct
	protected void postConstruct() {
		setJpaRepository(cajaRegistroTransaccionItemRepository);
	}
	
	@Override
	public Optional<CajaRegistroTransaccionItem> findByClave(UUID uuid) {
		return cajaRegistroTransaccionItemRepository.findByClave(uuid);
	}
}
