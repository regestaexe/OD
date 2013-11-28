package com.openDams.search_builder.controller;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.lowagie.text.Anchor;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;

public class RTFProducerController implements Controller{
	private final static Font boldFont = new Font(Font.UNDEFINED , 12, Font.BOLD);
	private final static Font bolditalicFont = new Font(Font.UNDEFINED , 12, Font.BOLDITALIC);
    private final static Font underlineFont = new Font(Font.UNDEFINED, 11, Font.UNDERLINE);
    private final static Font textFont = new Font(Font.UNDEFINED, 12, Font.NORMAL);
    private final static Font italicFont = new Font(Font.UNDEFINED, 12, Font.ITALIC);
    private final static Font titleFont = new Font(Font.UNDEFINED, 16, Font.BOLD);
    private final static Font h1Font = new Font(Font.UNDEFINED , 14, Font.BOLD);
    private final static Font h2Font = new Font(Font.UNDEFINED , 13, Font.BOLD);
    private final static Font h3Font = new Font(Font.UNDEFINED , 12, Font.BOLD);
    public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		ModelAndView mav = new ModelAndView("search_builder/rdf");
		
		arg1.setHeader("Content-Transfer-Encoding", "binary");
		arg1.setHeader("Pragma", "public");
		arg1.setHeader("Cache-Control", "cache");
		arg1.setHeader("Cache-Control", "must-revalidate");
		ServletOutputStream outputStream = arg1.getOutputStream();		
		Document document = new Document(PageSize.A4, 60, 60, 40, 40);
		if(arg0.getParameter("output_mode")!=null){
			if(arg0.getParameter("output_mode").equalsIgnoreCase("pdf")){
				arg1.setContentType("application/pdf");
				arg1.setHeader("Content-Disposition", "attachment; filename="+"DocumentoRicerca.pdf");
				@SuppressWarnings("unused")
				PdfWriter writer = PdfWriter.getInstance(document, outputStream);
			}else{
				arg1.setContentType("application/rtf");
				arg1.setHeader("Content-Disposition", "attachment; filename="+"DocumentoRicerca.rtf");
				@SuppressWarnings("unused")
				RtfWriter2 writer = RtfWriter2.getInstance(document,outputStream);
			}
		}else{
			arg1.setContentType("application/rtf");
			arg1.setHeader("Content-Disposition", "attachment; filename="+"DocumentoRicerca.rtf");
			@SuppressWarnings("unused")
			RtfWriter2 writer = RtfWriter2.getInstance(document,outputStream);
		}
        document.open();      
        String[] panel_orders = arg0.getParameter("panel_order").split(";");
        for (int i = 0; i < panel_orders.length; i++) {
			if(panel_orders[i].indexOf("title_panel_contents")!=-1){
				 document.addTitle(URLDecoder.decode(panel_orders[i],"UTF-8"));
				 cleanTitle(arg0.getParameter(panel_orders[i]), document);
			}else if (panel_orders[i].indexOf("index_panel_contents")!=-1) {
				 cleanIndex(arg0.getParameter(panel_orders[i]),document);
			}else if (panel_orders[i].indexOf("text_panel_contents")!=-1) {
				 cleanText(arg0.getParameter(panel_orders[i]),document);
			}else if (panel_orders[i].indexOf("archive_panel_contents")!=-1) {
				 cleanArchive(arg0.getParameter(panel_orders[i]),document);
			}
		}
        document.close();
		return mav;
	}
	private void cleanTitle(String title,Document document) throws DocumentException, UnsupportedEncodingException{
    	String cleanedTitle= URLDecoder.decode(title,"UTF-8").replaceAll("\\<.*?\\>","");    	
    	Paragraph titleParagraph = new Paragraph(cleanedTitle,titleFont);
    	document.add(titleParagraph);
    }
    private void cleanIndex(String index,Document document) throws DocumentException, UnsupportedEncodingException{
    	String[] indexVoices = URLDecoder.decode(index,"UTF-8").replaceAll("<!--start index voice-->","~~").replaceAll("<!--end index voice-->","~~").replaceAll("\\<.*?\\>","").split("~~");
    	List list = new List(false, 10);
    	Paragraph indexParagraph = new Paragraph();
    	indexParagraph.setFont(textFont);
    	for (int i = 0; i < indexVoices.length; i++) {
    		if(!indexVoices[i].trim().equals("") && !indexVoices[i].trim().equals("-"))    			
    			list.add(new ListItem(indexVoices[i]));
		}
    	indexParagraph.add(list);
    	document.add(new Paragraph(15," "));
    	document.add(indexParagraph);
    }
    private void cleanText(String text,Document document) throws DocumentException, UnsupportedEncodingException{		
		Paragraph titleParagraph = new Paragraph();
		convertHTMLToChunk(text, titleParagraph);
		titleParagraph.setAlignment(Paragraph.ALIGN_LEFT);
		document.add(new Paragraph(15," "));
		document.add(titleParagraph);   	
    }
    private void cleanArchive(String archive,Document document) throws DocumentException, UnsupportedEncodingException{   	
		Paragraph archiveParagraph = new Paragraph();
		convertHTMLToChunk(archive, archiveParagraph);
		archiveParagraph.setAlignment(Paragraph.ALIGN_LEFT);
		document.add(new Paragraph(15," "));
		document.add(archiveParagraph); 	
    }
    private void convertHTMLToChunk(String textHtml,Paragraph paragraph) throws UnsupportedEncodingException{  	
       String cleanedText = URLDecoder.decode(textHtml,"UTF-8");
       //System.out.println("########################################################################################");
       System.out.println(cleanedText);
       String htmlNoEmptyLine = cleanedText.replaceAll("(?m)^[ \t]*\r?\n","");	  
 	   String[] tagsToRemove = {"div","span","ul","li"};
 	   String noHtmlBefore = clearTags(htmlNoEmptyLine,tagsToRemove);
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
 					  //System.out.println("--------------------------->"+htmlRows[i]); 
 					  paragraph.add(getCunck(htmlTags,htmlRows[i]));  
 				   }
 				   htmlTags="";
 			   }			   	
 		   }		
 	   }
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
	public Object getCunck(String htmlTags,String text){
		String cleanedText = text.replaceAll("\\<.*?\\>","").replaceAll("(?m)^[ \t]*\r?\n","").trim();
		Object result = null;		
		Font default_font = textFont;
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
			default_font = h1Font;
		}
		if(htmlTags.indexOf("<h2>")!=-1){
			bold = true;
			default_font = h2Font;
		}
		if(htmlTags.indexOf("<h3>")!=-1){
			bold = true;
			default_font = h3Font;
		}
		if(bold && italic){
			default_font =   bolditalicFont;
		}else if(underline){
			default_font = underlineFont;
		}else if(italic){
			default_font = italicFont;
		}else if(bold){
			default_font = boldFont;
		}		
		if(href){
			 String link = StringUtils.substringBetween(htmlTags,"href=\"","\"");
			 Anchor anchor = new Anchor(cleanedText);
			 anchor.setName("LINK");
			 anchor.setReference(link);
			 anchor.setFont(default_font);
			 result = anchor;
		}else{
			result = new Chunk(cleanedText+"\n",default_font);
		}
		return result;
	}
}
