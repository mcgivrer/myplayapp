/**
 * Project: myplayapp
 * Object: Classe modélisant un utilisateur de l'application
 */
package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Transient;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.URL;
import play.db.jpa.Model;

/**
 * la classe <code>User</code> modélise un utilisateur du site.
 * il peut se connecter au site (<code>User.connect(String,String)</code>).
 * il est potentiellement l'auteur de la fiche de jeu et du test qui va avec.
 * Cet utilisateur peut avoir l'un des 3 rôles porposés: Administrateur, Modérateur
 * ou simple utilisateur.
 * - L'administrateur possède tous les droits de modificaiton de contenu du site,
 * - les modérateurs sont des personnes habilitées par les administrateurs à agir
 * sur l'état publication des differentes entités,
 * - les utilisateurs quand à eux, peuvent constituer leur listes de jeux et 
 * attribuer leur propres notes et commentaires aux jeux de leur liste.
 *
 * ces rôles sont hiérarchiquement cumulatif: un administrateur possède les droits
 * du modérateur et du User.
 *
 * @see app.models.Game
 *
 * @author frederic
 */
@Entity
public class User extends Model {
    /**
     * Enumérateur listant les rôles des utilisateurs
     */
	public enum UserRole {
		ADMINISTRATOR, MODERATOR, USER, VISITOR
	}

    /**
     * Nom de connexion de l'utilisateur.
     */
	@Required
	@MaxSize(30)
	public String username;

    /**
    * Mot de passe de l'utilisateur. Une classe de test pourra être utlisée pour 
    * la validation du mot de passe.
    */
    @Required
	@MinSize(4)
	@MaxSize(25)
	public String password;

    /**
     * Prénom de l'utilisateur.
     */
	@MaxSize(50)
	public String firstname;

    /**
     * Nom de l'utilisateur.
     */
	@MaxSize(50)
	public String lastname;

    /**
     * Email de l'utilisateur.
     */
	@Required
	@Email
	@MaxSize(100)
	public String email;

    /**
     * url du blog de l'utilisateur.
     */
	@MaxSize(100)
	@URL
	public String webblog;

    /**
     * Image de l'utilisateur (avatar)
     */
	@MaxSize(255)
	public String image = "test";

	/**
	 * c=created, v=valide, a=actif, d=desactive, b=banni
	 */
	@Required
	@MaxSize(1)
	public String status = "v";

    /**
     * Role de l'utilisateur.
     */
	@Required
	@Enumerated(EnumType.STRING)
	public UserRole role;

    /**
     * Langage  préféré de l'utilisateur.
     */
	@Required
	@MaxSize(5)
	public String language;

    /**
     * hashcode pour le Gravatar de l'utilisateur.
     */
	@Transient
	public String gravatarHash;

    /**
     * Liste des fiches de jeux dont l'utilisateur est le créateur/rédacteur/testeur.
     */
	@OneToMany
	public List<Game> games;
	
	@OneToMany
	public List<GameList> gamesLists;
	
	public User(){
		this.role = UserRole.VISITOR;
	}

	/**
	 * Constructeur paramétré par défaut.
	 * 
	 * @param username
	 * @param password
	 * @param firstname
	 * @param lastname
	 * @param email
	 * @param webblog
	 * @param image
	 * @param status
	 * @param role
	 * @param language
	 */
	public User(
			String username, 
			String password, 
			String firstname,
			String lastname, 
			String email, 
			String webblog, 
			String image,
			String status, 
			UserRole role, 
			String language,
			List<GameList> gamesLists) {
		this.username = username;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.webblog = webblog;
		this.image = image;
		this.status = status;
		this.role = role;
		this.language = language;
		this.gamesLists = gamesLists;
	}

	/**
	 * Initialisation des données calculées non persistantes.
	 */
	@PostLoad
	public void initializeTransientAttribute() {
		if (null != this.email) {
			this.gravatarHash = play.libs.Crypto.passwordHash(this.email);
		}
	}

	/**
	 * simplified displayed string for List.
	 */
	public String toString() {
		return this.username + " (" + this.firstname + " " + this.lastname
				+ ")";
	}

	/**
	 * Méthode de connexion d'un utilsateur. Utilisé par le module <code>Security</code>
	 * @see app.controller.Security
	 * 
	 * @param email
	 * @param password
	 * @return
	 */
	public static User connect(String username, String password) {
		return User.find("byUsernameAndPassword", username, password).first();
	}

	/**
	 * check if user have Administrator profile.
	 * 
	 * @return
	 */
	public boolean isRole(String role) {
		return role.equals(this.role.toString());
	}

	/**
	 * Encode Gravatar hash.
	 * 
	 * @return
	 */
	public String getGravatarHash() {
		return play.libs.Crypto.passwordHash(this.email.trim());
	}
}
