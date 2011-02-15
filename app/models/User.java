/**
 * Project myplayapp
 * Classe modélisant un utilisateur de l'application
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
import play.db.jpa.Model;

/**
 * User modélise un utilisateur pouvant se connecter.
 * @author McGivrer
 */
@Entity
public class User extends Model {
	
	public enum UserRole {
		ADMINISTRATOR,
		MODERATOR,
		USER
	}
	
	@Required
	@MaxSize(30)
	public String username;
	
	@Required
	@MinSize(4)
	@MaxSize(25)
	public String password;
	
	@MaxSize(50)
	public String firstname;
	
	@MaxSize(50)
	public String lastname;
	
	@Required
	@Email
	@MaxSize(100)
	public String email;
	
	@MaxSize(100)
	public String webblog;
	
	@MaxSize(255)
	public String image="test";
	
	@Required
	@MaxSize(2)
	public String status="a";
	
	@Required
	@Enumerated(EnumType.STRING)
	public UserRole role;
	
	@Required
	public String language;
	
	@Transient
	public String password2;

	@Transient
	public String gravatarHash;
	
	@OneToMany
	public List<Game> games;
	
	/**
	 * Constructeur paramétré par défaut.
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
			String language){
		this.username=username;
		this.password=password;
		this.firstname=firstname;
		this.lastname=lastname;
		this.email=email;
		this.webblog=webblog;
		this.image=image;
		this.status=status;
		this.role=role;
		this.language = language;
	}
	
	
	/**
	 * Initialisation des données calculées non persistantes.
	 */
	@PostLoad
	public void initializeTransientAttribute(){
		this.gravatarHash = play.libs.Crypto.passwordHash(this.email);		
	}
	
	/**
	 * simplified displayed string for List.
	 */
	public String toString(){
		return this.username + " (" + this.firstname + " " + this.lastname + ")";
	}
	
	/**
	 * Find Method to implement user connection. 
	 * @param email
	 * @param password
	 * @return
	 */
	public static User connect(String username, String password){
		return User.find("byUsernameAndPassword",username,password).first();
	}
	
	/**
	 * check if user have Administrator profile.
	 * @return
	 */
	public boolean isRole(String role){
		return role.equals(this.role.toString());
	}
	
	public String getGravatarHash(){
		return  play.libs.Crypto.passwordHash(this.email.trim());
	}

}