/**
 * Project: myplayapp
 * Objet: Commentaire d'un utilisateur sur un jeu.'
 */
package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * Commentaire sur un jeu de la part d'un utilisateur connecté, proposant 
 * un titre (<code>title</code>), un <code>content</code>, l'utilisateur 
 * emmeteur (<code>user</code>) ainsi que le jeu (<code>Game</code>) cible 
 * du commentaire.
 * Des commentaires (commments) sont potentiellement attachés en réponse à celui-ci,
 * et ce commmentaire peut donc lui-même avoir un <code>parent</code>.
 * Enfin un numéro unique de commentaire pour un jeu permet d'identifié l'ordre de ceux-ci.
 *
 * @author frederic
 * @see app.models.Game
 * @see app.models.User
 */
@Entity
public class Comment extends Model {
	/**
	 * Numéro du commentaire pour ce jeu
	 */
	public Integer number;
	
	/**
	 * nom de la liste de jeux
	 */
	@Required
	public String title;

	/**
	 * description pour cette liste de jeux
	 */
	@Required
	@Lob
	public String content;

	/**
	 * Utilisateur propriétaire de la liste
	 */
	@Required
	@ManyToOne
	public User user;

	/**
	 * Commentaires enfant de celui-ci.
	 */
	@OneToMany
	public List<Comment> childComments;

	/**
	 * Commentaire parent.
	 */
	@ManyToOne
	public Comment parent;
	
	/**
	 * jeu (Game) cible du commentaire.
	 */
	@ManyToOne
	public GameListItem gameListItem;

	/**
	 * Constructeur par défaut d'une Comment.
	 * @param title
	 * @param content
	 * @param user
	 * @param game
	 */
	public Comment(String title, String content, User user, GameListItem gameItem, Comment parent, List<Comment> childComments) {
		this.title = title;
		this.content = content;
		this.user = user;
		this.gameListItem = gameItem;
		this.parent = parent;
		this.childComments = childComments;
	}
}
