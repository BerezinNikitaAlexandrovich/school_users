package berezin.school.config;
/**
* Класс MVCConfig конфигурации расположения ресурсов проекта 
* (вместо использования  mvc-dispatcher-servlet.xml ).
* Переопределение метода addViewControllers
*/
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MVCConfig extends WebMvcConfigurerAdapter {
	
	/**
	* Метод addViewControllers
	* Указание соостветствия пути (URL) - представлению (шаблону, HTML страницы)
	* Redirect - с "/" на "/index"
	*/
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {	
		registry.addRedirectViewController("/", "/index");
	}

}
