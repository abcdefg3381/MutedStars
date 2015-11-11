package superstar.db.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Journal {
	/**
	 * Abbreviated Journal Title
	 */
	@Id
	private String titleAbbrev;

	private String ISSN, Total_Cites, Immediacy_Index, Articles, Cited_Half_Life,
			Eigenfactor_Score, Article_Influence_Score;

	private float Impact_Factor, Five_Year_Impact_Factor;

	public Journal() {
		super();
	}

	public Journal(String abbreviatedJournalTitle, String iSSN, String totalCites,
			String impactFactor, String fiveYearImpactFactor, String immediacyIndex,
			String articles, String citedHalfLife, String eigenfactorScore,
			String articleInfluenceScore) {
		super();
		titleAbbrev = abbreviatedJournalTitle;
		ISSN = iSSN;
		Total_Cites = totalCites;
		Impact_Factor = Float.parseFloat(impactFactor);
		Five_Year_Impact_Factor = Float.parseFloat(fiveYearImpactFactor);
		Immediacy_Index = immediacyIndex;
		Articles = articles;
		Cited_Half_Life = citedHalfLife;
		Eigenfactor_Score = eigenfactorScore;
		Article_Influence_Score = articleInfluenceScore;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Journal) {
			Journal j = (Journal) obj;
			return j.getTitleAbbrev().equals(this.getTitleAbbrev());
		}
		return false;
	}

	public String getArticle_Influence_Score() {
		return Article_Influence_Score;
	}

	public String getArticles() {
		return Articles;
	}

	public String getCited_Half_Life() {
		return Cited_Half_Life;
	}

	public String getEigenfactor_Score() {
		return Eigenfactor_Score;
	}

	public float getFive_Year_Impact_Factor() {
		return Five_Year_Impact_Factor;
	}

	public String getImmediacy_Index() {
		return Immediacy_Index;
	}

	public float getImpact_Factor() {
		return Impact_Factor;
	}

	public String getISSN() {
		return ISSN;
	}

	public String getTitleAbbrev() {
		return titleAbbrev;
	}

	public String getTotal_Cites() {
		return Total_Cites;
	}

	public void setArticle_Influence_Score(String articleInfluenceScore) {
		Article_Influence_Score = articleInfluenceScore;
	}

	public void setArticles(String articles) {
		Articles = articles;
	}

	public void setCited_Half_Life(String citedHalfLife) {
		Cited_Half_Life = citedHalfLife;
	}

	public void setEigenfactor_Score(String eigenfactorScore) {
		Eigenfactor_Score = eigenfactorScore;
	}

	public void setFive_Year_Impact_Factor(float fiveYearImpactFactor) {
		Five_Year_Impact_Factor = fiveYearImpactFactor;
	}

	public void setImmediacy_Index(String immediacyIndex) {
		Immediacy_Index = immediacyIndex;
	}

	public void setImpact_Factor(float impactFactor) {
		Impact_Factor = impactFactor;
	}

	public void setISSN(String iSSN) {
		ISSN = iSSN;
	}

	public void setTitleAbbrev(String abbreviatedJournalTitle) {
		titleAbbrev = abbreviatedJournalTitle;
	}

	public void setTotal_Cites(String totalCites) {
		Total_Cites = totalCites;
	}
}
