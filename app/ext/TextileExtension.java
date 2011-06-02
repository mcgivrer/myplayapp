package ext;

import java.io.StringWriter;

import jj.play.org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import jj.play.org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import jj.play.org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import jj.play.org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;
import play.templates.JavaExtensions;
/**
 * Classe de rendu de texte défini en respectant la norme Textile.
 * basé sur le travail de de Marc Deschamps:
 * @see https://gist.github.com/781700
 * @author frederic
 *
 */
public class TextileExtension extends JavaExtensions {

	/**
	 * Textilize <code>text</code>
	 * @param text to be interpreted as textile language markup.
	 * @return
	 */
	public static String textile(String text){
		StringWriter writer = new StringWriter();
		// Création du générateur de document HTML
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		// Empêche la génération des balises html et body.
		builder.setEmitAsDocument(false);
		// Création du parser Textile
		MarkupParser parser = new MarkupParser(new TextileLanguage());
		parser.setBuilder(builder);
		// On parse le code Textile
		parser.parse(text);
		// On le convertie en HTML
		return writer.toString();
	}
	
	/**
	 * Markup Converter to the <code>language</code> for the <code>text</code>.
	 * @param language=Textile,
	 * @param text
	 * @return
	 */
	public static String convert(String language, String text){
		StringWriter writer = new StringWriter();
		// Création du générateur de document HTML
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		// Empêche la génération des balises html et body.
		builder.setEmitAsDocument(false);
		// Création du parser Textile
		Class cl;
		try {
			cl = Class.forName(language+"Language");
			MarkupLanguage languageImpl = (MarkupLanguage) cl.newInstance();
			MarkupParser parser = new MarkupParser((MarkupLanguage)languageImpl);
			parser.setBuilder(builder);
			// On parse le code Textile
			parser.parse(text);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// On le convertie en HTML
		return writer.toString();
	}
	
}