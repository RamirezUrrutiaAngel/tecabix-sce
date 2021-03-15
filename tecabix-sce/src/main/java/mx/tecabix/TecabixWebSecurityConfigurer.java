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

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Configuration
@EnableWebSecurity
public class TecabixWebSecurityConfigurer extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private DataSource dataSource;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// permite las peticiones POST con el security 
		http.csrf().disable()
		
		.authorizeRequests()
		
		// No requieren autentificacion
		.antMatchers(
				"/usuario/findIsExist",
				"/catalogo/findByTipoNombre",
				"/catalogo/findByTipoAndNombre",
				"/estado/all",
				"/estado/all-join-municipio"
				).permitAll()
		
		// las peticiones tienen que estar autentificadas
		.anyRequest().authenticated().and().httpBasic();

	}
	
	@Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication()
		.dataSource(dataSource)
		.usersByUsernameQuery("SELECT nombre, psw, true FROM tecabix_sce.usuario u WHERE u.id_estatus = 1 AND u.nombre = ?")
		.authoritiesByUsernameQuery("SELECT u.nombre AS USUARIO, a.nombre AS ROL FROM tecabix_sce.authority a JOIN tecabix_sce.perfil_authority pa ON (a.id_authority = pa.id_authority) JOIN tecabix_sce.perfil p ON (pa.id_perfil = p.id_perfil) JOIN tecabix_sce.usuario u ON ( p.id_perfil = u.id_perfil) WHERE u.id_estatus = 1 AND a.id_estatus = 1 AND p.id_estatus = 1 AND  u.nombre = ?")
		.passwordEncoder(passwordEncoder());
    }
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}