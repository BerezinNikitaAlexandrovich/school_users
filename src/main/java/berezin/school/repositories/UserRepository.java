package berezin.school.repositories;
/**
 * Интерфейс UserRepository. 
 * Расширение CrudRepository
 * Описание методов-запросов к БД
 */

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import berezin.school.user.User;

/**
 * Используем класс(сущность) User, тип первичного ключа Integer (поле User.id)
 */
public interface UserRepository extends CrudRepository<User, Integer> {

	/**
	 * Запрос
	 * Получить всех пользователей, кроме администратора
	 * SELECT * FROM users WHERE NOT login = 'admin
	 * @return - список пользователей без администратора
	 */
	@Query("from User user where not user.login = 'admin'")
	List<User> findAllLoginNotLikeAdmin();
	
	/**
	 * Запрос
	 * Получить пользователя по логину и паролю
	 * SELECT * FROM users WHERE login = ? AND  password = ?
	 * @param - login, password. Аннотация Param - связь параметров запроса с параметрами метода
	 * @return - объект класса Users с соответствующим логином и паролем
	 */
	@Query("from User user where user.login = :login and user.password = :password")
	User findByLoginAndPassword(@Param("login") String login, @Param("password") String password);

	/**
	 * Запрос
	 * Наличие пользователя имеющего соответвующий логин
	 * SELECT COUNT(*) FROM users WHERE users.login = 'nikita'
	 * @param login
	 * @return - 0, 1(т.к. поле login UNIQUE)
	 */
	int countByLogin(String login);
	
	/**
	 * Запрос
	 * Получить пользователя по логину
	 * SELECT * FROM users WHERE login = ?
	 * @param - login 
	 * @return - объект класса Users с соответсвующим логином
	 */
	User findByLogin(String login);
	
	/**
	 * Запрос
	 * Удаление пользователя по логину
	 * Метод - модифицирющий запрос, являющийся транзакцией 
	 * DELETE FROM users WHERE login = ?
	 * @param - login 
	 * @return - 0, 1(удалилась запись или нет)
	 */
	@Modifying
    @Transactional
	int deleteByLogin(String login);
}
