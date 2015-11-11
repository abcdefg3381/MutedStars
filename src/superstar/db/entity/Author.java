package superstar.db.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import maggie.network.entity.Node;

@Entity
public class Author implements Node {
	public static int CO_AUTHOR_STUB = 0;
	public static int CO_AUTHOR_HARVESTED = 1;
	public static int DEAD_SUPERSTAR_ANTICIPATED = 2;
	public static int DEAD_SUPERSTAR_SUDDEN = 3;
	public static int DEAD_SUPERSTAR_ANTICIPATED_NEIGHBORED = 4;
	public static int DEAD_SUPERSTAR_SUDDEN_NEIGHBORED = 5;
	public static int SUPERSTAR_STUB = 6;
	public static int SUPERSTAR_HARVESTED = 7;
	public static int SUPERSTAR_EMERITUS = 8;
	public static int TEMP_STATUS = 9;
	public static int HIGH_DEGREE = 11;
	// cohn za's status is set to 10 FROM 3

	/**
	 * ID
	 */
	@Id
	private String ID;
	/**
	 * Author roles. See the use of static values.
	 */
	private int status;
	/**
	 * family name
	 */
	private String family;
	/**
	 * first given name
	 */
	private String first;
	/**
	 * abbrev. of middle name
	 */
	private String middle;
	/**
	 * other component of name, i.e. Jr. 3rd, etc
	 */
	private String other;
	/**
	 * year of birth
	 */
	private int yearBorn;
	/**
	 * year of death
	 */
	private int yearDied;
	/**
	 * list of publications
	 */
	// @Transient
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "AULst", cascade = { CascadeType.PERSIST,
			CascadeType.MERGE })
	private List<Publication> publication = new ArrayList<Publication>();
	/**
	 * node degree
	 */
	@Transient
	private int degree;
	@Transient
	private int inDegree;
	@Transient
	private int outDegree;
	@Transient
	private float strength;
	@Transient
	private float inStrength;
	@Transient
	private float outStrength;
	@Transient
	private boolean old = false;
	/**
	 * distance to center node
	 */
	@Transient
	private int distance = 99;

	public Author() {
		super();
	}

	public Author(String iD) {
		super();
		ID = iD;
	}

	public Author(String family, String first, String middle, String other) {
		super();

		this.family = family;
		this.first = first;
		this.middle = middle;
		this.other = other;

		// form ID
		setID(family.toLowerCase() + " " + first.toLowerCase().charAt(0));
		if (middle != null && middle.length() > 0)
			ID = ID.concat("" + middle.toLowerCase().charAt(0));
		if (other.equals("Jr.")) {
			ID = ID.concat(" jr");
		} else if (other.equals("3rd")) {
			ID = ID.concat(" 3rd");
		}
	}

	@Override
	public void addDegree(int i) {
		this.degree += i;
	}

	@Override
	public void addInDegree() {
		inDegree++;
	}

	@Override
	public void addOutDegree() {
		outDegree++;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Author) {
			Author au = (Author) obj;
			return au.getID().equals(this.getID());
		}
		return false;
	}

	@Override
	public int getDegree() {
		return degree;
	}

	public int getDistance() {
		return distance;
	}

	public String getFamily() {
		return family;
	}

	public String getFirst() {
		return first;
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public float getInStrength() {
		return inStrength;
	}

	public String getMiddle() {
		return middle;
	}

	@Override
	public String getName() {
		return getFamily();
	}

	public String getOther() {
		return other;
	}

	@Override
	public float getOutStrength() {
		return outStrength;
	}

	public List<Publication> getPubs() {
		return publication;
	}

	/**
	 * <ul>
	 * <li>0 = in faculty roaster;
	 * <li>1 = anticipated death;
	 * <li>2 = sudden death;
	 * <li>3 = in faculty roaster with all the publications.
	 * <ul>
	 */
	public int getStatus() {
		return status;
	}

	@Override
	public float getStrength() {
		return strength;
	}

	public int getYearBorn() {
		return yearBorn;
	}

	public int getYearDied() {
		return yearDied;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public void setID(String iD) {
		ID = iD.toLowerCase();
	}

	@Override
	public void setInStrength(float f) {
		this.inStrength = f;
	}

	public void setMiddle(String abbrev) {
		this.middle = abbrev;
	}

	public void setOther(String other) {
		this.other = other;
	}

	@Override
	public void setOutStrength(float f) {
		this.outStrength = f;
	}

	public void setPubs(List<Publication> pubs) {
		this.publication = pubs;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setYearBorn(int yearBorn) {
		this.yearBorn = yearBorn;
	}

	public void setYearDied(int yearDied) {
		this.yearDied = yearDied;
	}

	@Override
	public String toString() {
		return getFamily() + ", " + getFirst() + " " + getMiddle() + " " + getOther();
	}

	public boolean isOld() {
		return old;
	}

	public void setOld(boolean old) {
		this.old = old;
	}
}
