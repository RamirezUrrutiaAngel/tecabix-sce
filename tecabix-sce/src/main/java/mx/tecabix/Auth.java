package mx.tecabix;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

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
