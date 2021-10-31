package mx.tecabix.db.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.tecabix.db.entity.CajaRegistroTransaccionItem;

public interface CajaRegistroTransaccionItemRepository extends JpaRepository<CajaRegistroTransaccionItem, Long>{

	Optional<CajaRegistroTransaccionItem> findByClave(UUID uuid);

}
