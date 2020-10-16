package mx.tecabix.db.service;

import java.util.List;
import java.util.Optional;

import mx.tecabix.db.entity.Authority;

public interface AuthorityService {

	List<Authority> findAll();
	Optional<Authority>  findById(Integer id);
}
