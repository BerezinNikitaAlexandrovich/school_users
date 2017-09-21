package berezin.school.controllers;
/**
 * Контроллер ресурса. 
 * Реализация REST запросов
 * Обработка GET POST PUT DELETE запросов с предоставлением 
 * соответствующего представления (шаблона)
 * Обработка исключений.
 * Валидация входных данных
 */

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import berezin.school.forms.LoginForm;
import berezin.school.repositories.UserRepository;
import berezin.school.user.User;
import berezin.school.wrappers.MessageWrapper;

@RestController
public class ApplicationController {

	/**
	 * Автозаполнение. Автоматическое создание прокси классов, реализующих интерфейс
	 * UserRepository (выполнение запросов и получение данных от БД)
	 */
	@Autowired
	private UserRepository users;

	/**
	 * Главная страница ресурса Обработка GET запроса http://url_ресурса/index 
	 * 
	 * @return представление index
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ModelAndView index() {

		/**
		 * Возвращение преставления index с пустым служебным сообщением и пустой формой
		 * LoginForm
		 */
		return new ModelAndView("index").addObject("mw", new MessageWrapper()).addObject("loginForm", new LoginForm());
	}
	/** конец index() */

	
	/**
	 * Аутентификация и авторизация пользователя. Обработка POST запроса
	 * http://url_ресурса/login
	 * 
	 * @param loginForm объект класса, имя в шаблоне "loginForm", валидация на основе
	 *                  ограничений, описанных в классе LoginForm
	 * @param bindingResult если входные данные формы не прошли валидацию, то генерация
	 *                      экземпляра класса, содержащего информацию об ошибках в
	 *                      соответствующих полях
	 * @return представление index в случае ошибки
	 * @return представление по адресу (администратор)
	 * @return представление по адресу (пользователь)
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView login(
			@Valid @ModelAttribute("loginForm") LoginForm loginForm, 
			BindingResult bindingResult) {

		/**
		 * Если форма не прошла валидацию, возврат представления index, генерация
		 * служебного сообщения
		 */
		if (bindingResult.hasErrors()) {
			return new ModelAndView("index").addObject("mw", new MessageWrapper("Ошибка ввода данных"));
		}

		/**
		 * Если пользователь администратор, переход в меню админстратора GET запрос по
		 * адресу http://url_ресурса/adminMode?password={пароль администратора}
		 */
		if (loginForm.getLogin().equals("admin")) {
			return new ModelAndView("redirect:/adminMode?password=" + loginForm.getPassword());
		}

		/**
		 * Для обычных пользователей, переход в меню пользователя GET запрос по адресу
		 * http://url_ресурса/Users/{логин пользователя}?password={пароль пользователя}
		 */
		return new ModelAndView("redirect:/users/" + loginForm.getLogin() + "?password=" + loginForm.getPassword());
	}
	/** конец login() */
	

	/**
	 * Регистрация нового пользователя. Обработка GET запроса
	 * http://url_ресурса/userAddingView
	 * 
	 * @return представление userAddingMenu
	 */
	@RequestMapping(value = "/userAddingView", method = RequestMethod.GET)
	public ModelAndView addingUserMenu() {

		/** Добавление в представление userAddingMenu нового объекта класса User */
		return new ModelAndView("userAddingMenu").addObject("user", new User());
	}
	/** конец addingUserMenu() */
	
	
	/**
	 * Список всех существующих пользователей системы (без администратора). Обработка GET запроса 
	 * http://url_ресурса/users
	 * 
	 * @return представление userList
	 * @throws Exception если возникли проблемы с доступом к БД
	 */
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public ModelAndView users() throws Exception {

		/** Добавление в представление userList списка userList объектов класса User */
		return new ModelAndView("userList").addObject("userList", users.findAllLoginNotLikeAdmin());
	}
	/** конец users() */
	

	/**
	 * Пользователь с возможность редактирования. Доступ по логину паролю. 
	 * Обработка GET запроса 
	 * http://url_ресурса/users/{логин пользователя}?password={пароль пользователя}
	 * 
	 * @param login логин, используется в URL
	 * @param password пароль, параметр в запросе
	 * @return представление userMenu
	 * @return представление index, если возникла ошибка аутентификации
	 * @throws Exception если возникли проблемы с доступом к БД
	 */
	@RequestMapping(value = "/users/{login}", method = RequestMethod.GET)
	public ModelAndView userEditionMode(
			@PathVariable("login") String login,
			@RequestParam(value = "password", required = false, defaultValue = "") String password) 
			throws Exception {

		/** Поиск в БД пользователя по логину и паролю (аутентификация) */
		User user = users.findByLoginAndPassword(login, password);

		/**
		 * Если пользователь аутентифицирован, возврат представления userMenu(с
		 * возможностью редактирования), добавление объекта класса User с
		 * соответствующим логином и паролем
		 */
		if (user != null) {
			return new ModelAndView("userMenu").addObject("user", user);
		}

		/**
		 * Если пользователь не аутентифицирован, возврат представления index, генерация
		 * служебного сообщения
		 */
		return new ModelAndView("index").addObject("mw", new MessageWrapper("Неверный логин/пароль"))
				.addObject("loginForm", new LoginForm());
	}
	/** конец userEditionMode() */
	

	/**
	 * Добавление нового пользователя в БД. Обработка POST запроса
	 * http://url_ресурса/users
	 * 
	 * @param user объект класса, имя в шаблоне "user", валидация на основе
	 *             ограничений, описанных в классе User
	 * @param bindingResult если входные данные формы не прошли валидацию, то генерация
	 *                      экземпляра класса, содержащего информацию об ошибках в
	 *                      соответствующих полях
	 * @return представление userAddingMenu, если данные не прошли валидацию
	 * @return представление userAddingMen с созданием новой ошибки , если логин уже
	 *         существует в базе данных
	 * @return представление по адресу
	 * @throws Exception если возникли проблемы с доступом к БД
	 */
	@RequestMapping(value = "/users", method = RequestMethod.POST)
	public ModelAndView postUser(
			@Valid @ModelAttribute User user, BindingResult bindingResult) throws Exception {

		/** Если форма не прошла валидацию, возврат представления userAddingMenu */
		if (bindingResult.hasErrors()) {
			return new ModelAndView("userAddingMenu");
		}

		/**
		 * Если логин существует в системе, возврат представления userAddingMenu с
		 * добавлением указанной ошибки
		 */
		if (users.countByLogin(user.getLogin()) > 0) {
			bindingResult.addError(new FieldError("userForm", "login", "логин уже существует в системе"));
			return new ModelAndView("userAddingMenu");
		}

		/**
		 * Добавление пользователя в БД и GET запрос по адресу
		 * http://url_ресурса/Users/{логин пользователя}?password={пароль пользователя}
		 */
		users.save(user);
		return new ModelAndView("redirect:/users/" + user.getLogin() + "?password=" + user.getPassword());
	}
	/** конец postUser() */
	

	/**
	 * Обновление данных существующего пользователя в БД Обработка PUT запроса
	 * http://url_ресурса/users/{логин пользователя}?oldPassword={пароль пользователя}
	 * 
	 * @param user объект класса, имя в шаблоне "user", валидация на основе
	 *             ограничений, описанных в классе User
	 * @param bindingResult если входные данные формы не прошли валидацию, то генерация
	 *                      экземпляра класса, содержащего информацию об ошибках в
	 *                      соответствующих полях
	 * @param login используется в URL для поиска пользователя
	 * @param oldPassword параметр в запросе, используется для аутентификации пользователя
	 *                    при изменении данных
	 * @return представление userMenu, если данные не прошли валидацию
	 * @return представление по адресу в случае успешной транзакции
	 * @return представление index, если возникла ошибка аутентификации
	 * @throws Exception если возникли проблемы с доступом к БД
	 */
	@RequestMapping(value = "/users/{login}", method = RequestMethod.PUT)
	public ModelAndView updateUser(
			@Valid @ModelAttribute User user, BindingResult bindingResult,
			@PathVariable("login") String login,
			@RequestParam(value = "oldPassword", required = false, defaultValue = "") String oldPassword)
			throws Exception {

		/** Если форма не прошла валидацию */
		if (bindingResult.hasErrors()) {

			/**
			 * Если был введен новый пароль, отменяем его изменение просим пользователя
			 * повторить новый пароль еще раз
			 */
			if (!oldPassword.equals(user.getPassword())) {
				user.setPassword(oldPassword);
				bindingResult.addError(new FieldError("updateUserForm", "password", "Введите новый пароль еще раз"));
			}

			/** Возвращаем представление userMenu для устранения ошибок */
			return new ModelAndView("userMenu").addObject("user", user);
		}

		/**
		 * Если пользователь аутентифицирован, обновление данных в БД, GET запрос по
		 * адресу http://url_ресурса/Users/{логин пользователя}?password={пароль
		 * пользователя}
		 */
		if (users.findByLoginAndPassword(login, oldPassword) != null) {
			users.save(user);
			return new ModelAndView("redirect:/users/" + user.getLogin() + "?password=" + user.getPassword());
		}

		/**
		 * Если пользователь не аутентифицирован, возврат представления index, генерация
		 * служебного сообщения
		 */
		String message = "У вас нет прав доступа для изменения данных пользователя";
		return new ModelAndView("index").addObject("mw", new MessageWrapper(message)).addObject("loginForm",
				new LoginForm());
	}
	/** конец updateUser() */
	

	/**
	 * Удаление пользователя из БД. Обработка DELETE запроса
	 * http://url_ресурса/users/{логин пользователя}?password={пароль пользователя}
	 * 
	 * @param login используется в URL для поиска пользователя
	 * @param password параметр в запросе, используется для аутентификации пользователя
	 *                 при удалении данных
	 * @return представление index, с указанием сообщения
	 * @throws Exception если возникли проблемы с доступом к БД
	 */
	@RequestMapping(value = "/users/{login}", method = RequestMethod.DELETE)
	public ModelAndView deleteUser(
			@PathVariable("login") String login,
			@RequestParam(value = "password", required = false, defaultValue = "") String password) throws Exception {

		/** сообщение о статусе операции */
		String message;

		/**
		 * Если пользователь аутентифицирован, удаление пользователя, генерация
		 * служебного сообщения
		 */
		if (users.findByLoginAndPassword(login, password) != null) {
			users.deleteByLogin(login);
			message = "Пользователь " + login + " удален";
		}

		/** Если пользователь не аутентифицирован, генерация служебного сообщения */
		else {
			message = "У вас нет прав доступа для удаления пользователя";
		}

		/** Возврат представления index c соответствующим служебным сообщением */
		return new ModelAndView("index").addObject("mw", new MessageWrapper(message)).addObject("loginForm",
				new LoginForm());
	}
	/** конец deleteUser() */
	

	/**
	 * Просмотр данных пользователя. Обработка GET запроса
	 * http://url_ресурса/users/{логин пользователя}/showUser
	 * 
	 * @param login используется в URL для поиска пользователя
	 * @return представление userDetails
	 * @return представление index, если пользователь не найден
	 * @throws Exception если возникли проблемы с доступом к БД
	 */
	@RequestMapping(value = "/users/{login}/showUser", method = RequestMethod.GET)
	public ModelAndView showUser(@PathVariable("login") String login) throws Exception {

		/** Поиск пользователя по логину */
		User user = users.findByLogin(login);

		/**
		 * Если пользователь найден, возврат представления userDetails с добавлением
		 * объекта класса User с соответствующим логином
		 */
		if (user != null) {
			return new ModelAndView("userDetails").addObject("user", user);
		}

		/**
		 * Если пользователь не найден, возврат представления index, генерация
		 * служебного сообщения
		 */
		return new ModelAndView("index").addObject("mw", new MessageWrapper("Пользователь не найден"))
				.addObject("loginForm", new LoginForm());
	}
	/** конец showUser() */
	

	/**
	 * Меню администратора Обработка GET запроса
	 * http://url_ресурса/adminMode?password={пароль администратора}
	 * 
	 * @param password пароль администратора, параметр в запросе
	 * @return представление adminMenu
	 * @return представление index, если пользователь не найден
	 * @throws Exception если возникли проблемы с доступом к БД
	 */
	@RequestMapping(value = "/adminMode", method = RequestMethod.GET)
	public ModelAndView adminMode(
			@RequestParam(value = "password", required = false, defaultValue = "") String password) 
			throws Exception {
		
		/** Аутентификация администратора, возврат представления adminMenu*/
		User admin = users.findByLoginAndPassword("admin", password);
		if (admin != null) {
			return new ModelAndView("adminMenu").addObject("admin", admin);
		}
		
		/**
		 * Если администратор не аутентифицирован, возврат представления index, генерация
		 * служебного сообщения
		 */
		return new ModelAndView("index").addObject("mw", new MessageWrapper("Неверный логин/пароль"))
				.addObject("loginForm", new LoginForm());
	}
	/** конец adminMode() */
	

	/**
	 * Изменение пароля администратора Обработка PUT запроса
	 * http://url_ресурса/adminMode?oldPassword={пароль администратора}
	 * 
	 * * @param user объект класса, имя в шаблоне "admin", валидация на основе
	 *               ограничений, описанных в классе User
	 * @param bindingResult если входной пароль не прошел валидацию, то генерация
	 *                      экземпляра класса, содержащего информацию об ошибках в
	 *                      соответствующих полях
	 * @param oldPassword параметр в запросе, используется для аутентификации администратора
	 *                    при изменении данных
	 * @return представление adminMenu, если данные не прошли валидацию
	 * @return представление по адресу в случае успешной транзакции
	 * @return представление index, если ошибка аутентификации
	 * @throws Exception если возникли проблемы с доступом к БД
	 */
	@RequestMapping(value = "/adminMode/admin", method = RequestMethod.PUT)
	public ModelAndView updateAdmin(
			@Valid @ModelAttribute User admin, BindingResult bindingResult,
			@RequestParam(value = "oldPassword", required = false, defaultValue = "") String oldPassword)
			throws Exception {
		
		/** 
		 * Если пароль не прошел валидацию, создание новой ошибки, сохранение старого пароля, 
		 * возвращение представления adminMenu
		 *  */
		if (bindingResult.hasErrors()) {
			bindingResult.addError(new FieldError("updateUserForm", "password", "Введите новый пароль еще раз"));
			admin.setPassword(oldPassword);
			return new ModelAndView("adminMenu").addObject("password", admin);
		}
		
		/**При успешной аутентификации администратора обновление данных в БД, GET запрос по
		 * адресу http://url_ресурса/adminMode/?password={пароль администратора} */
		if (users.findByLoginAndPassword(admin.getLogin(), oldPassword) != null) {
			users.save(admin);
			return new ModelAndView("redirect:/adminMode/?password=" + admin.getPassword());
		}
		
		/**
		 * Если администратор не аутентифицирован, возврат представления index, генерация
		 * служебного сообщения
		 */
		String message = "У вас нет прав доступа для изменения данных администратора";
		return new ModelAndView("index").addObject("mw", new MessageWrapper(message)).addObject("loginForm",
				new LoginForm());
	}
	/** конец updateAdmin() */

	
	/**
	 * Список всех существующих пользователей системы (без администратора). Режим редактирования
	 * Обработка GET запроса http://url_ресурса/adminMode/users?password={пароль администратора}
	 * 
	 * @param password пароль администратора, параметр в запросе
	 * @return представление userList 
	 * @throws Exception если возникли проблемы с доступом к БД
	 */
	@RequestMapping(value = "/adminMode/users", method = RequestMethod.GET)
	public ModelAndView userListAdminMode(
			@RequestParam(value = "password", required = false, defaultValue = "") String password) 
			throws Exception {
		
		/**
		 * Если аутентификация администратора пройдена, добавление в представление 
		 * adminUserList списка userList объектов класса User и отправкой пароля 
		 * классом служебного сообщениея для формирования в шаблоне DELETE запроса
		 * */
		if (users.findByLoginAndPassword("admin", password) != null) {
			return new ModelAndView("adminUserList").addObject("userList", users.findAllLoginNotLikeAdmin())
					                                .addObject("mw", new MessageWrapper(password));
		}
		
		/**
		 * Если администратор не аутентифицирован, возврат представления index, генерация
		 * служебного сообщения
		 */
		return new ModelAndView("index").addObject("mw", new MessageWrapper("Неверный логин/пароль"))
				.addObject("loginForm", new LoginForm());
	}
	/** конец userListAdminMode() */

	
	/**
	 * Удаление пользователя из БД Администратором Обработка DELETE запроса
	 * http://url_ресурса/users/{логин пользователя}?password={пароль администратора}
	 * 
	 * @param login используется в URL для поиска пользователя
	 * @param password параметр в запросе, используется для авторизации администратора
	 *                 при удалении данных
	 * @return представление по адресу в случае успешной транзакции
	 * @return представление index, с указанием сообщения
	 * @throws Exception если возникли проблемы с доступом к БД
	 */
	@RequestMapping(value = "/adminMode/users/{login}", method = RequestMethod.DELETE)
	public ModelAndView deleteUserAdminMode(
			@PathVariable("login") String login,
			@RequestParam(value = "password", required = false, defaultValue = "") String password) 
			throws Exception {
		
		/** сообщение о статусе операции */
		String message;
		
		/**  Если аутентификация администратора пройдена */
		if (users.findByLoginAndPassword("admin", password) != null) {
			
			/**  
			 * Если пользователь существует в БД, удаление пользователя, GET запрос по адресу 
			 * http://url_ресурса/adminMode/users?password={пароль администратора}
			 */
			if (users.countByLogin(login) > 0) {
				users.deleteByLogin(login);
				return new ModelAndView("redirect:/adminMode/users?password=" + password);
			} 
			
			/** Генерация служебного сообщения при отсутсвии пользователя */
			else {
				message = "Пользователь " + login + " отсутствует в системе";
			}
			
		} 
		
		/** Генерация служебного сообщения при ошибке аутетификации */
		else {
			message = "У вас нет прав доступа для удаления пользователя";
		}
		
		/** При возникновении ошибок, возврат представления index с служебным сообщением */
		return new ModelAndView("index").addObject("mw", new MessageWrapper(message))
				                        .addObject("loginForm", new LoginForm());
	}
	/** конец deleteUserAdminMode() */
	

	/**
	 * Обработка исключений класса
	 * Класс исключений Exception
	 * 
	 * @param e - исключение 
	 * @return представление index, с указанием сообщения об ошибке
	 */
	@ExceptionHandler(value = { Exception.class })
	public ModelAndView handleException(Exception e) {
		
		return new ModelAndView("index").addObject("mw", new MessageWrapper("Ошибка базы данных"))
				                        .addObject("loginForm", new LoginForm());
	}
	/** конец handleException() */
	
}
