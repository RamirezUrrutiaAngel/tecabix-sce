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
package mx.tecabix;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
public class Auth {

	public static boolean hash(Authentication authentication, String... authorities) {
		Collection<? extends GrantedAuthority> collectionAuthorities = authentication.getAuthorities();
		List<String> authoritiesList = Arrays.asList(authorities);
		for (GrantedAuthority grantedAuthority : collectionAuthorities) {
			if(authoritiesList.contains(grantedAuthority.getAuthority())) {
				return true;
			}
		}
		return false;
	}
}
