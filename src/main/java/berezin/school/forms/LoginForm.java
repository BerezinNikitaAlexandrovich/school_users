package berezin.school.forms;
/**
* Класс LoginForm  - форма login, password заглавной страницы (шаблона) приложения
* Валидация вводимых данных
*/

import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;

public class LoginForm {

	/**
	* Логин. Не пустой, ограниченной длинны, служебное сообщение
	*/
	@NotEmpty(message="Ошибка! \"Логин\" не введен")
	@Size(min = 4, max = 10, message = "Длинна логина от 4 до 10 символов!")
	private String login;
	
	/**
	* Пароль. Не пустой, ограниченной длинны, служебное сообщение
	*/
	@NotEmpty(message="Ошибка! \"Пароль\" не введен")
	@Size(min = 4, max = 10, message = "Длинна пароля от 4 до 10 символов!")
	private String password;
	
	/** геттеры и сеттеры */
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
}
