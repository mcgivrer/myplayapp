/**
 * Project myplayapp Play! tutorial
 */
package models;

import javax.persistence.Entity;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * Game est une entité représentant un jeu video dans la bibliothèque.
 * @author frederic
 */
@Entity
public class Game extends Model {
	@Required
	public String title;
	@Required
	public String platform;
	@Required
	public String description;
	public Boolean publish;
	public String testContent;
	public String developerStudio;
	public String editor;
	@Required
	public Integer yearOfPublication;
	public Integer note;
	public String cover;

	/**
	 * Default constructor for persistence purpose.
	 * @param title
	 * @param Game
	 * @param description
	 * @param publish
	 * @param testContent
	 * @param developerStudio
	 * @param editor
	 * @param yearOfPublication
	 * @param note
	 * @param cover
	 */
	public Game(String title, String Game, String description, Boolean publish,
			String testContent, String developerStudio, String editor,
			Integer yearOfPublication, Integer note, String cover) {
		this.title=title;
		this.description=description;
		this.publish = publish;
		this.testContent=testContent;
		this.developerStudio=developerStudio;
		this.editor=editor;
		this.yearOfPublication = yearOfPublication;
		this.note = note;
		this.cover = cover;
	}
	
	/**
	 * Displayed name in list
	 */
	public String toString(){
		return this.title + "(" + this.yearOfPublication + ")";
	}
	
}