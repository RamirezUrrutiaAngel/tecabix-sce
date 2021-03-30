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
package mx.tecabix.service;

import java.io.Serializable;

import org.springframework.data.domain.Page;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
public class PageGeneric implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected int number;
	protected int numberOfElements;
	protected int size;
	protected long totalElements; 
	protected int totalPages;
	
	public PageGeneric() {}
	
	public PageGeneric(Page<?> page) {
		this.number = page.getNumber();
		this.numberOfElements = page.getNumberOfElements();
		this.size = page.getSize();
		this.totalElements = page.getTotalElements();
		this.totalPages = page.getTotalPages();
	}

	public final int getNumber() {
		return number;
	}

	public final void setNumber(int number) {
		this.number = number;
	}

	public final int getNumberOfElements() {
		return numberOfElements;
	}

	public final void setNumberOfElements(int numberOfElements) {
		this.numberOfElements = numberOfElements;
	}

	public final int getSize() {
		return size;
	}

	public final void setSize(int size) {
		this.size = size;
	}

	public final long getTotalElements() {
		return totalElements;
	}

	public final void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	public final int getTotalPages() {
		return totalPages;
	}

	public final void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
}
