package berezin.school.wrappers;
/**
* Класс MessageWrapper  - обертка служебных сообщений 
* Используется для вывода служебных сообщений в шаблонах Thyemleaf
*/

public class MessageWrapper {

	/** Хранение текста сообщения*/
   private String message;

   /** Пустой конструктор - при отсутствии необходимости выводить сообщения */
   public MessageWrapper() {
   }

   /** Конструктор */
   public MessageWrapper(String message) {
       this.message = message;
   }

   /** Геттер и сеттер */
   public String getMessage() {
       return message;
   }

   public void setMessage(String message) {
       this.message = message;
   }

}