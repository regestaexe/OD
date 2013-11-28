package com.openDams.search_builder.utility;
import java.awt.Color;

import org.apache.commons.lang.StringUtils;

import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;

public class RTFProducer {
   public RTFProducer() {
	   String html="";
	   html+="<div id=\"scheda_205371\"><div>";
	   html+="<div id=\"scheda\">";
	   html+="\r\n";
	   html+="<div>";
	   html+=" \r\n";
	   html+=" \r\n";
	   html+=" \r\n";
	   html+=" \r\n";
	   html+="	<h1 style=\"font-weight: bold;\">";
	   html+="		<strong><em>REGIONE LOMBARDIA</em></strong> <span class=\"if-empty-default\">LEGGE REGIONALE 29 dicembre 2009, n. 32</span>";
	   html+="	</h1>";
	   html+="	<a style=\"font-style: italic;\" href=\"#aa\">Gazzetta Ufficiale - 3a Serie Speciale Regioni n.  40  del  9-10-2010, pag. 13</a>  <span>Bilancio di previsione per l'esercizio finanziario 2010 e bilancio pluriennale 2010/2012 a legislazione vigente e programmatico. (010R1081)</span>";
	   html+="</div>";
	   html+="\r\n";
	   html+="\r\n";
	   html+="\r\n";
	   html+="\r\n";
	   html+="\r\n";
	   html+="\r\n";
	   html+="\r\n";
	   html+="\r\n";
	   html+="\r\n";
	   html+="<div>";
	   html+=" \r\n";
	   html+="	<ul class=\"container-lexPrincipale\">";
	   html+="		<li><em style=\"font-style: italic;\"><a title=\"visualizza il testo della legge\" href=\"http://www.normattiva.it/uri-res/N2Ls?urn:nir:regione.:legge:2009-12-29;32\" target=\"_new\">urn:nir:regione.:legge:2009-12-29;32</a></em>";
	   html+="		</li>";
	   html+="	</ul>";
	   html+=" \r\n";
	   html+=" \r\n";
	   html+="</div>";
	   html+="\r\n";
	   html+="\r\n";
	   html+="\r\n";
	   html+="\r\n";
	   html+="\r\n";
	   html+="\r\n";
	   html+="\r\n";
	   html+="\r\n";
	   html+="\r\n";
	   html+="\r\n";
	   html+="\r\n";
	   html+="\r\n";
	   html+="\r\n";
	   html+="</div></div></div>";
	   String htmlNoEmptyLine = html.replaceAll("(?m)^[ \t]*\r?\n","");	  
	   String[] tagsToRemove = {"div","span","ul","li"};
	   String noHtmlBefore = clearTags(htmlNoEmptyLine,tagsToRemove);
	   System.out.println(noHtmlBefore);
	   String cleanedHtmlBefore = splitHtml(noHtmlBefore);
	   String[] htmlRows = cleanedHtmlBefore.split("\r\n");
	   String htmlTags = "";
	   for (int i = 0; i < htmlRows.length; i++) {
		   if(!htmlRows[i].trim().equals("")){
			   if(isHtmlNode(htmlRows[i]) && !htmlRows[i].startsWith("</")){
				   if(htmlRows[i].indexOf(" ")!=-1){
					   if(htmlRows[i].indexOf("href=\"#")!=-1 || htmlRows[i].indexOf("href")==-1){
						   htmlTags+=htmlRows[i].substring(0, htmlRows[i].indexOf(" "))+">";
					   }else{
						   htmlTags+="<a href=\""+StringUtils.substringBetween(htmlRows[i],"href=\"","\"")+"\">";
					   }				  
				   }else{
					   htmlTags+=htmlRows[i]+"";
				   }
			   }else{	
				   if(!htmlRows[i].startsWith("</")){
					   getCunck(htmlTags,htmlRows[i]);
				   }
				   htmlTags="";
			   }			   	
		   }		
	   }
       /*System.out.println("-------------------------------------------------------------------------------------------------");
	   String noHtml = htmlNoEmptyLine;
       System.out.println(noHtml.trim());
       System.out.println("-------------------------------------------------------------------------------------------------");
       String noHtmlNoEmptyLine = noHtml.replaceAll("(?m)^[ \t]*\r?\n","");
       System.out.println(noHtmlNoEmptyLine.trim());*/
	   //System.out.println("<!--start index voice-->aaaaaaaaaaaaaaaaaaaaaa<!--end index voice-->".replaceAll("<!--start index voice-->","~~").replaceAll("<!--end index voice-->","~~"));
    }
	public static void main(String[] args) {
		new RTFProducer();
	}
	public String clearTags(String html,String... strings){
		String result = html;
		for (int i = 0; i < strings.length; i++) {
			result = result.replaceAll("</?"+strings[i]+"[^>]*?>","");
		}
		return result.replaceAll("(?m)^[ \t]*\r?\n","").trim();
	}
	public String splitHtml(String html){
		return html.replaceAll(">",">\r\n").replaceAll("</","\r\n</").replaceAll("<","\r\n<").replaceAll("(?m)^[ \t]*\r?\n","");
	}
	public boolean isHtmlNode(String toTest){
		String toTestLowered = toTest.toLowerCase();
		if(toTestLowered.startsWith("<b"))
			return true;
		else if(toTestLowered.startsWith("<a"))
			return true;
		else if(toTestLowered.startsWith("<strong"))
			return true;
		else if(toTestLowered.startsWith("<em"))
			return true;
		else if(toTestLowered.startsWith("<i"))
			return true;
		else if(toTestLowered.startsWith("<h1"))
			return true;
		else if(toTestLowered.startsWith("<h2"))
			return true;
		else if(toTestLowered.startsWith("<h3"))
			return true;
		else if(toTestLowered.startsWith("</"))
			return true;
		else
			return false;
	}
	public Chunk getCunck(String htmlTags,String text){
		String cleanedText = text.replaceAll("\\<.*?\\>","").replaceAll("(?m)^[ \t]*\r?\n","").trim();
		Chunk result = null;
		float h1_size = 14;
		float h2_size = 13;
		float h3_size = 12;
		float default_size = 11;
		String default_font_style = FontFactory.COURIER;
		int default_font = Font.NORMAL;
		boolean bold = false;
		boolean italic = false;
		boolean href = false;
		boolean underline = false;
		if(htmlTags.indexOf("<b>")!=-1){
			bold = true;
		}
		if(htmlTags.indexOf("href")!=-1){
			href=true;
			underline=true;
		}
		if(htmlTags.indexOf("<strong>")!=-1){
			bold = true;
		}
		if(htmlTags.indexOf("<em>")!=-1){
			italic = true;
		}
		if(htmlTags.indexOf("<i>")!=-1){
			italic = true;
		}
		if(htmlTags.indexOf("<h1>")!=-1){
			bold = true;
			default_size = h1_size;
		}
		if(htmlTags.indexOf("<h2>")!=-1){
			bold = true;
			default_size = h2_size;
		}
		if(htmlTags.indexOf("<h3>")!=-1){
			bold = true;
			default_size = h3_size;
		}
		if(bold && italic){
			default_font =   Font.BOLDITALIC;
		}else if(italic){
			default_font = Font.ITALIC;
		}else if(bold){
			default_font = Font.BOLD;
		}else if(underline){
			default_font = Font.UNDERLINE;
		}
		result = new Chunk(cleanedText, FontFactory.getFont(default_font_style, default_size, default_font, new Color(0,0,0)));
		if(href){
			result.setAnchor(StringUtils.substringBetween(htmlTags,"href=\"","\""));
		}
		return result;
	}
}
