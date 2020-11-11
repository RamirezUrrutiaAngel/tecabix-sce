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
		.antMatchers("/usuario/findIsExist").permitAll()
		
		// las peticiones tienen que estar autentificadas
		.anyRequest().authenticated().and().httpBasic();

	}
	
	@Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication()
		.dataSource(dataSource)
		.usersByUsernameQuery("SELECT nombre, psw, true FROM tecabix_sce.usuario u WHERE u.id_estatus = 1 AND u.nombre = ?")
		.authoritiesByUsernameQuery("SELECT u.nombre AS USUARIO, a.nombre AS ROL FROM tecabix_sce.authority a JOIN tecabix_sce.perfil_authority pa ON (a.id_authority = pa.id_authority) JOIN tecabix_sce.perfil p ON (pa.id_perfil = p.id_perfil) JOIN tecabix_sce.usuario u ON ( p.id_perfil = u.id_perfil) WHERE u.id_estatus = 1 AND  u.nombre = ?")
		.passwordEncoder(passwordEncoder());
    }
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}