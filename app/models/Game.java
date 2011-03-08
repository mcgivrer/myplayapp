/**
 * Project myplayapp Play! tutorial
 */
package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * Game est une entité représentant un jeu video dans la bibliothèque.
 * @author McGivrer
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
	@Required
	@ManyToOne
	public User author;
	
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
	 * @param author
	 */
	public Game(String title, String Game, String description, Boolean publish,
			String testContent, String developerStudio, String editor,
			Integer yearOfPublication, Integer note, String cover, User author) {
		this.title=title;
		this.description=description;
		this.publish = publish;
		this.testContent=testContent;
		this.developerStudio=developerStudio;
		this.editor=editor;
		this.yearOfPublication = yearOfPublication;
		this.note = note;
		this.cover = cover;
		this.author = author;
	}
	/**
	 * Displayed name in list
	 */
	public String toString(){
		return this.title + " - ( " + this.platform + " / "+ this.yearOfPublication + " )";
	}
}
