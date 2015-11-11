package superstar.db.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

@Entity
public class Publication {

	/**
	 * ID
	 */
	@Id
	private int PMID;

	/**
	 * year published
	 */
	private int yearPublished;
	/**
	 * title
	 */
	private String title;
	/**
	 * author list
	 */
	// @Transient
	@ManyToMany(fetch = FetchType.EAGER)
	private List<Author> AULst = new ArrayList<Author>();
	/**
	 * paper type
	 */
	private String paperType = "atypical";
	/**
	 * journal
	 */
	private String journal;
	// temp array
	@Transient
	public String authors;

	public Publication() {
		super();
	}

	public Publication(int pMID, int yearPublished, String title, String paperType, String journal) {
		super();
		PMID = pMID;
		this.yearPublished = yearPublished;
		this.title = title;
		this.paperType = paperType;
		this.journal = journal;
	}

	public List<Author> getAULst() {
		return AULst;
	}

	public String getJournal() {
		return journal;
	}

	public String getPaperType() {
		return paperType;
	}

	public int getPMID() {
		return PMID;
	}

	public String getTitle() {
		return title;
	}

	public int getYear() {
		return yearPublished;
	}

	public void setAULst(List<Author> aULst) {
		AULst = aULst;
	}

	public void setJournal(String tA) {
		journal = tA;
	}

	public void setPaperType(String pT) {
		paperType = pT;
	}

	public void setPMID(int pMID) {
		PMID = pMID;
	}

	public void setTitle(String tI) {
		title = tI;
	}

	public void setYearPublished(int yP) {
		yearPublished = yP;
	}

	@Override
	public String toString() {
		StringBuilder re = new StringBuilder(PMID + ";;" + yearPublished + ";;" + title + ";;"
				+ paperType + ";;" + journal + ";;");
		for (Author author : AULst) {
			re.append(author.getID() + ";");
		}
		return re.toString();
	}
}
