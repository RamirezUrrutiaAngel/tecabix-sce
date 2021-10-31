package mx.tecabix.db.service;

import java.util.Optional;
import java.util.UUID;

import mx.tecabix.db.GenericSevice;
import mx.tecabix.db.entity.CajaRegistroTransaccionItem;

public interface CajaRegistroTransaccionItemService extends GenericSevice<CajaRegistroTransaccionItem, Long>{

	Optional<CajaRegistroTransaccionItem> findByClave(UUID uuid);
}
