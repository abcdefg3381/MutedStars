package superstar.logic.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import superstar.db.entity.Author;
import superstar.db.entity.Publication;

public class Entrez {
	private List<Publication> pubLst;

	public List<Publication> eFetch(String webEnv) {
		URL fetchQuery;
		String inputLine = null;
		pubLst = new ArrayList<Publication>();
		try {
			// send fetch and get response
			fetchQuery = new URL(generateFetchQuery(webEnv));
			// System.out.println(fetchQuery);
			URLConnection uc = fetchQuery.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			// analyze response
			Publication pu = null;
			while ((inputLine = in.readLine()) != null) {
				if (inputLine.startsWith("PMID")) {
					pu = new Publication();
					pu.setPMID(Integer.parseInt(inputLine.split("- ")[1]));
				} else if (inputLine.startsWith("DP")) {
					pu.setYearPublished(Integer.parseInt(inputLine.split("- ")[1].substring(0, 4)));
				} else if (inputLine.startsWith("TI")) {
					pu.setTitle(inputLine.split("- ")[1]);
				} else if (inputLine.startsWith("AU")) {
					pu.getAULst().add(new Author(inputLine.split("- ")[1].toLowerCase()));
				} else if (inputLine.startsWith("PT")) {
					if (inputLine.split("- ")[1].toLowerCase().equals("journal article"))
						pu.setPaperType(inputLine.split("- ")[1].toLowerCase());
				} else if (inputLine.startsWith("TA")) {
					pu.setJournal(inputLine.split("- ")[1].toLowerCase());
				} else if (inputLine.startsWith("SO")) {
					pubLst.add(pu);
				}
			}
		} catch (IOException e) {
			System.out.println("fetch failed");
			return eFetch(webEnv);
		} catch (Exception e) {
			System.err.println(inputLine);
			System.err.println(e.getMessage());
		}
		return pubLst;
	}

	public String eSearch(Author a) {
		URL searchQuery;
		String webEnv = null;
		String inputLine = null;
		try {
			// send search and get response
			searchQuery = new URL(generateSearchQuery(a));
			// System.out.println(searchQuery);
			URLConnection uc = searchQuery.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			StringBuilder xmlString = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				xmlString.append(inputLine);
			}
			in.close();
			// analyze xml response
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(
					new StringReader(xmlString.toString())));
			document.getDocumentElement().normalize();
			// retrieve search result
			NodeList nodeLst = document.getElementsByTagName("WebEnv");
			Element fstNmElmnt = (Element) nodeLst.item(0);
			NodeList fstNm = fstNmElmnt.getChildNodes();
			webEnv = (fstNm.item(0)).getNodeValue();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			return eSearch(a);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return webEnv;
	}

	public String eSearch(Author a, int yearStart, int yearEnd) {
		URL searchQuery;
		String webEnv = null;
		String inputLine = null;
		try {
			// send search and get response
			searchQuery = new URL(generateSearchQuery(a, yearStart, yearEnd));
			// System.out.println(searchQuery);
			URLConnection uc = searchQuery.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			StringBuilder xmlString = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				xmlString.append(inputLine);
			}
			in.close();
			// analyze xml response
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(
					new StringReader(xmlString.toString())));
			document.getDocumentElement().normalize();
			// retrieve search result
			NodeList nodeLst = document.getElementsByTagName("WebEnv");
			Element fstNmElmnt = (Element) nodeLst.item(0);
			NodeList fstNm = fstNmElmnt.getChildNodes();
			webEnv = (fstNm.item(0)).getNodeValue();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			return eSearch(a);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return webEnv;
	}

	private String generateFetchQuery(String webEnv) {
		return "http://www.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?rettype=medline"
				+ "&retmode=text&db=Pubmed&retmax=1000&query_key=1&WebEnv=" + webEnv;
	}

	/**
	 * query an author for publication without time constrain
	 * 
	 * @param a
	 * @return
	 */
	private String generateSearchQuery(Author a) {
		StringBuilder sb = new StringBuilder();
		sb.append("http://www.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?");
		sb.append("db=Pubmed&usehistory=n&term=(");
		try {
			sb.append(URLEncoder.encode("\"" + a.getID() + "\"[au])", "ASCII"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * query an author for publication during a period of time
	 * 
	 * @param a
	 * @param yearStart
	 * @param yearEnd
	 * @return
	 */
	private String generateSearchQuery(Author a, int yearStart, int yearEnd) {
		StringBuilder sb = new StringBuilder();
		sb.append("http://www.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?");
		sb.append("db=Pubmed&usehistory=n&term=(");
		try {
			sb.append(URLEncoder.encode("\"" + a.getID() + "\"[au]" + " AND " + yearStart + ":"
					+ yearEnd + "[dp]" + ")", "ASCII"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
