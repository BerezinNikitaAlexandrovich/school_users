package berezin.school.user;
/**
 * Класс сущности User. 
 * Используется для хранения записи таблицы БД school.users
 * Переопределение equals и hashCode
 */

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

/** Указываем, что класс является сущностью БД (таблица users) */
@Entity
@Table(name = "users")
public class User implements Serializable {

	private static final long serialVersionUID = 10L;

	/**
	 * id - поле id БД 
	 * Идентификатор записи GenerationType.IDENTITY - значение
	 * генерируется СУБД, инкрементное поле
	 */
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * Логин пользователя 
	 * login - поле login БД (уникальное значение) обязательное
	 * для заполнения размер - от 4 до 10 символов (валидация)
	 */
	@NotNull(message = "Заполните поле login")
	@Size(min = 4, max = 10, message = "Длинна логина от 4 до 10 символов!")
	@Column(name = "login")
	private String login;

	/**
	 * Пароль пользователя 
	 * password - поле password БД обязательное для заполнения
	 * размер - от 4 до 10 символов (валидация)
	 */
	@NotNull(message = "Заполните поле password")
	@Size(min = 4, max = 10, message = "Длинна пароля от 4 до 10 символов!")
	@Column(name = "password")
	private String password;

	/**
	 * Имя пользователя 
	 * name - поле name БД размер - от 0 до 15 символов (валидация)
	 */
	@Size(min = 0, max = 15, message = "Длинна имени от 0 до 15 символов!")
	@Column(name = "name")
	private String name;

	/**
	 * Фамилия пользователя 
	 * surName - поле surname БД размер - от 0 до 15 символов
	 * (валидация)
	 */
	@Size(min = 0, max = 15, message = "Длинна фамилии от 0 до 15 символов!")
	@Column(name = "surname")
	private String surName;

	/**
	 * Адрес пользователя 
	 * adress - поле adress БД размер - от 0 до 100 символов
	 * (валидация)
	 */
	@Size(min = 0, max = 100, message = "Длинна адреса от 0 до 100 символов!")
	@Column(name = "adress")
	private String adress;

	/**
	 * Дата рождения пользователя birthDate - поле birth_date БД 
	 * TemporalType.DATE - в БД храниться только дата, время усечено 
	 * ISO.DATE - формат yyyy-mm-dd,
	 * корректен для работы с БД и формой ввода даты в UI валидация в UI
	 */
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "birth_date")
	private Date birthDate;

	/**
	 * Информация о пользователе 
	 * info - поле info БД размер - от 0 до 1000 символов
	 * (валидация)
	 */
	@Size(min = 0, max = 1000, message = "Длинна сообщения от 0 до 1000 символов!")
	@Column(name = "info")
	private String info;

	/** геттеры и сеттеры класса */
	public Integer getId() {
		return id;
	}

	public String getLogin() {
		return login;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurName() {
		return surName;
	}

	public void setSurName(String surName) {
		this.surName = surName;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	/** конец геттеров и сеттеров класса */

	/** Переопределение метода hashCode */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		return result;
	}

	/** Переопределение метода equals */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (login == null) {
			if (other.login != null)
				return false;
		} else if (!login.equals(other.login))
			return false;
		return true;
	}

}
