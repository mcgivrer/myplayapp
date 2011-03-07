package ext;

import java.io.StringWriter;

import jj.play.org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import jj.play.org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
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
}