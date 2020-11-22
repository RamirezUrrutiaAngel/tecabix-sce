/*
 *   This file is part of Foobar.
 *
 *   Foobar is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Foobar is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 *
 */
package mx.tecabix.db.generic;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
public abstract class GenericSeviceImpl<Entity,Key> implements GenericSevice<Entity, Key>{

	private JpaRepository<Entity,Key> jpaRepository;
	
	protected abstract void postConstruct();
	
	
	@Override
	public Entity update(Entity entity) {
		entity = jpaRepository.save(entity);
		return entity;
	}

	@Override
	public Entity save(Entity entity) {
		entity = jpaRepository.save(entity);
		return entity;
	}

	@Override
	public List<Entity> saveAll(Iterable<Entity> entities) {
		List<Entity> result= jpaRepository.saveAll(entities);
		return result;
	}

	@Override
	public Optional<Entity> findById(Key id) {
		Optional<Entity> result = jpaRepository.findById(id);
		return result;
	}

	@Override
	public boolean existsById(Key id) {
		boolean result = jpaRepository.existsById(id);
		return result;
	}

	@Override
	public List<Entity> findAll() {
		List<Entity> result = jpaRepository.findAll();
		return result;
	}

	@Override
	public List<Entity> findAllById(Iterable<Key> ids) {
		List<Entity> result = jpaRepository.findAllById(ids);
		return result;
	}
	
	@Override
	public Page<Entity> findAll(Pageable pageable) {
		Page<Entity> result = jpaRepository.findAll(pageable);
		return result;
	}
	
	@Override
	public Page<Entity> findAll(int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Entity> entitys = this.findAll(pageable);
		return entitys;
	}

	@Override
	public long count() {
		long result = jpaRepository.count();
		return result;
	}

	@Override
	public void deleteById(Key id) {
		jpaRepository.deleteById(id);
	}

	@Override
	public void delete(Entity entity) {
		jpaRepository.delete(entity);
	}

	@Override
	public void deleteAll(Iterable<? extends Entity> entities) {
		jpaRepository.deleteAll(entities);
	}

	@Override
	public void deleteAll() {
		jpaRepository.deleteAll();
	}

	public JpaRepository<Entity, Key> getJpaRepository() {
		return jpaRepository;
	}

	public void setJpaRepository(JpaRepository<Entity, Key> jpaRepository) {
		this.jpaRepository = jpaRepository;
	}
}
