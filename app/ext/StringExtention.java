/**
 * 
 */
package ext;

import play.templates.JavaExtensions;

/**
 * Opération de base sur les chaînes de caractères dans le template engine. 
 * @author FDELORME
 *
 */
public class StringExtention extends JavaExtensions {
	
	/**
	 * Mise en minusules de la chaine en entrée
	 * @param value
	 * @return
	 */
	public static String toLower(String value){
		return value.toLowerCase();
	}
	/**
	 * Mise en Majusules de la chaine en entrée
	 * @param value
	 * @return
	 */
	public static String toUpper(String value){
		return value.toUpperCase();
	}
	/**
	 * Mise en Majuscle de la prmière lettre de la chaine en entrée
	 * @param value
	 * @return
	 */
	public static String toCapitilize(String value){
		return value.substring(0,1).toUpperCase()+value.substring(1,value.length()-1).toLowerCase();
	}
}