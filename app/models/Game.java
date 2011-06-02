/**
 * Project: myplayapp Play! tutorial
 * Objet: Fiche d'un jeu contenant toutes les informations technique et un test 
 * minimaliste.
 */
package models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.engine.CascadeStyle;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * Game est une entité représentant un jeu video dans la bibliothèque. Les 
 * attributs title, platform, description, editor et developerStudio permettent 
 * une qualification technique du jeu, tandis que note et testContent fournisse
 * une analyse qualitative de ce jeu.
 * l'attribut cover' apporte un élément graphique de distinction: la pochette 
 * de la boite de jeu.
 * @see app.models.User
 * @author McGivrer
 */
@Entity
public class Game extends Model {
    /**
     * titre du jeu
     */
	@Required
	@MaxSize(60)
	public String title;

    /**
     * Plateforme d'accueil du jeu (wii, ps2, ps3, x360, psp, ds, 3ds).
     */
	@Required
	@MaxSize(10)
	public String platform;
	
	/**
	 * une description sommaire du jeu permettant une affichage court dans les pages
	 */
	@Required
	@Lob
	@MaxSize(1000)	
	public String description;
	
	/**
	 * flag de publication du jeu : s'il est vrai le jeux peut-être attaché 
	 * dans les listes des utilisateurs.
	 */
	public Boolean publish;
	
	/**
	 * testContent offre un test plus complet au format textile
	 * @see http://www.textism.com/tools/textile/
	 */
	@Lob
	public String testContent;
	
	/**
	 * Studio de développement du jeu. Le champs est en saisie libre. mais une saisie 
	 * en mode autocomplete sera proposée en création/modification.
	 */
	public String developerStudio;
	
	/**
	 * Editeur du jeu. Le champs est en saisie libre. mais une saisie 
	 * en mode autocomplete sera proposée en création/modification.
	 */
	public String editor;
	
	/**
	 * Date de publication du jeu.
	 */
	@Required
	public Date yearOfPublication;
	
	/**
	 * Note attribuée au jeu par le testeur.
	 */
	public Integer note;
	
	/**
	 * Image de la jaquette de la boite du jeu.
	 */
	@Required
	public String cover;
	
	/**
	 * Date de création de la fiche du jeu.
	 */
	@Required
	public Date createdAt = new Date();


	/**
	 * Auteur de la fiche du jeux
	 */
	@Required
	@ManyToOne
	public User author;
	
	/**
	 * Item des liste des jeux référençant ce jeu.
	 */
	@OneToMany(mappedBy="game",cascade=CascadeType.REMOVE)
	public List<GameListItem> listItems;
	
	/**
	 * Commentaires des utilisateurs liés au jeu.
	 */
	@OneToMany
	public List<Comment> comments;
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
			Date yearOfPublication, Integer note, String cover, User author) {
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
	
	/**
	 * Return shorter description limited to 90 characters.
	 */
	public String getShortDescription(){
		return (this.description.length()>90?this.description.substring(0,90):this.description);
	}
	
	/**
	 * Return shorter title limited to 30 characters.
	 */
	public String getShortTitle(){
		return (this.title.length()>30?this.title.substring(0,30)+"...":this.title);
	}
}
