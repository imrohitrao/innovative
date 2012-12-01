package com.digitald4.common.dao;
/**Copy Right Frank todo */
/**Description of class, (we need to get this from somewhere, database? xml?)*/
import com.digitald4.common.dao.DataAccessObject;
import com.digitald4.common.jpa.EntityManagerHelper;
import com.digitald4.common.jpa.PrimaryKey;
import com.digitald4.common.model.GeneralData;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Vector;
import javax.persistence.Cache;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.TypedQuery;
public abstract class GeneralDataDAO extends DataAccessObject{
	public static enum KEY_PROPERTY{ID};
	public static enum PROPERTY{ID,GROUP_ID,IN_GROUP_ID,NAME,RANK,ACTIVE,DESCRIPTION};
	private Integer id;
	private Integer groupId;
	private Integer inGroupId;
	private String name;
	private double rank;
	private boolean active = true;
	private String description;
	private Collection<GeneralData> generalDatas;
	private GeneralData group;
	public static GeneralData getInstance(Integer id){
		return getInstance(id, true);
	}
	public static GeneralData getInstance(Integer id, boolean fetch){
		if(isNull(id))return null;
		EntityManager em = EntityManagerHelper.getEntityManager();
		PrimaryKey pk = new PrimaryKey(id);
		Cache cache = em.getEntityManagerFactory().getCache();
		GeneralData o = null;
		if(cache != null && cache.contains(GeneralData.class, pk))
			o = em.find(GeneralData.class, pk);
		if(o==null && fetch)
			o = em.find(GeneralData.class, pk);
		return o;
	}
	public static Collection<GeneralData> getAll(){
		return getNamedCollection("findAll");
	}
	public static Collection<GeneralData> getAllActive(){
		return getNamedCollection("findAllActive");
	}
	public static Collection<GeneralData> getCollection(String[] props, Object... values){
		String qlString = "SELECT o FROM GeneralData o";
		if(props != null && props.length > 0){
			qlString += " WHERE";
			int p=0;
			for(String prop:props){
				if(p > 0)
					qlString +=" AND";
				if(values[p]==null)
					qlString += " o."+prop+" IS NULL";
				else
					qlString += " o."+prop+" = ?"+(p+1);
				p++;
			}
		}
		return getCollection(qlString,values);
	}
	public synchronized static Collection<GeneralData> getCollection(String jpql, Object... values){
		EntityManager em = EntityManagerHelper.getEntityManager();
		TypedQuery<GeneralData> tq = em.createQuery(jpql,GeneralData.class);
		if(values != null && values.length > 0){
			int p=1;
			for(Object value:values)
				if(value != null)
					tq = tq.setParameter(p++, value);
		}
		return tq.getResultList();
	}
	public synchronized static Collection<GeneralData> getNamedCollection(String name, Object... values){
		EntityManager em = EntityManagerHelper.getEntityManager();
		TypedQuery<GeneralData> tq = em.createNamedQuery(name,GeneralData.class);
		if(values != null && values.length > 0){
			int p=1;
			for(Object value:values)
				if(value != null)
					tq = tq.setParameter(p++, value);
		}
		return tq.getResultList();
	}
	public GeneralDataDAO(){}
	public GeneralDataDAO(Integer id){
		this.id=id;
	}
	public GeneralDataDAO(GeneralDataDAO orig){
		super(orig);
		copyFrom(orig);
	}
	public void copyFrom(GeneralDataDAO orig){
		this.groupId=orig.getGroupId();
		this.inGroupId=orig.getInGroupId();
		this.name=orig.getName();
		this.rank=orig.getRank();
		this.active=orig.isActive();
		this.description=orig.getDescription();
	}
	public String getHashKey(){
		return getHashKey(getKeyValues());
	}
	public Object[] getKeyValues(){
		return new Object[]{id};
	}
	@Override
	public int hashCode(){
		return PrimaryKey.hashCode(getKeyValues());
	}
	@Id
	@SequenceGenerator(name="GENERAL_SEQ",sequenceName="GENERAL_SEQ")
	@Column(name="ID",nullable=false)
	public Integer getId(){
		return id;
	}
	public void setId(Integer id){
		if(isSame(id, getId()))return;
		Integer oldValue = getId();
		this.id=id;
		setProperty("ID", id, oldValue);
	}
	@Column(name="GROUP_ID",nullable=true)
	public Integer getGroupId(){
		return groupId;
	}
	public void setGroupId(Integer groupId){
		if(isSame(groupId, getGroupId()))return;
		Integer oldValue = getGroupId();
		this.groupId=groupId;
		setProperty("GROUP_ID", groupId, oldValue);
		group=null;
	}
	@Column(name="IN_GROUP_ID",nullable=false)
	public Integer getInGroupId(){
		return inGroupId;
	}
	public void setInGroupId(Integer inGroupId){
		if(isSame(inGroupId, getInGroupId()))return;
		Integer oldValue = getInGroupId();
		this.inGroupId=inGroupId;
		setProperty("IN_GROUP_ID", inGroupId, oldValue);
	}
	@Column(name="NAME",nullable=false,length=64)
	public String getName(){
		return name;
	}
	public void setName(String name){
		if(isSame(name, getName()))return;
		String oldValue = getName();
		this.name=name;
		setProperty("NAME", name, oldValue);
	}
	@Column(name="RANK",nullable=true)
	public double getRank(){
		return rank;
	}
	public void setRank(double rank){
		if(isSame(rank, getRank()))return;
		double oldValue = getRank();
		this.rank=rank;
		setProperty("RANK", rank, oldValue);
	}
	@Column(name="ACTIVE",nullable=true)
	public boolean isActive(){
		return active;
	}
	public void setActive(boolean active){
		if(isSame(active, isActive()))return;
		boolean oldValue = isActive();
		this.active=active;
		setProperty("ACTIVE", active, oldValue);
	}
	@Column(name="DESCRIPTION",nullable=true,length=256)
	public String getDescription(){
		return description;
	}
	public void setDescription(String description){
		if(isSame(description, getDescription()))return;
		String oldValue = getDescription();
		this.description=description;
		setProperty("DESCRIPTION", description, oldValue);
	}
	public GeneralData getGroup(){
		if(group==null)
			group=GeneralData.getInstance(getGroupId());
		return group;
	}
	public void setGroup(GeneralData group){
		setGroupId(group==null?0:group.getId());
		this.group=group;
	}
	public Collection<GeneralData> getGeneralDatas(){
		if(isNewInstance() || generalDatas != null){
			if(generalDatas == null)
				generalDatas = new TreeSet<GeneralData>();
			return generalDatas;
		}
		return GeneralData.getNamedCollection("findByGroup",getId());
	}
	public void addGeneralData(GeneralData generalData){
		generalData.setGroup((GeneralData)this);
		if(isNewInstance() || generalDatas != null)
			getGeneralDatas().add(generalData);
		else
			generalData.insert();
	}
	public void removeGeneralData(GeneralData generalData){
		if(isNewInstance() || generalDatas != null)
			getGeneralDatas().remove(generalData);
		else
			generalData.delete();
	}
	public GeneralData copy(){
		GeneralData cp = new GeneralData((GeneralData)this);
		copyChildrenTo(cp);
		return cp;
	}
	public void copyChildrenTo(GeneralDataDAO cp){
		super.copyChildrenTo(cp);
		for(GeneralData child:getGeneralDatas())
			cp.addGeneralData(child.copy());
	}
	public Vector<String> getDifference(GeneralDataDAO o){
		Vector<String> diffs = super.getDifference(o);
		if(!isSame(getId(),o.getId())) diffs.add("ID");
		if(!isSame(getGroupId(),o.getGroupId())) diffs.add("GROUP_ID");
		if(!isSame(getInGroupId(),o.getInGroupId())) diffs.add("IN_GROUP_ID");
		if(!isSame(getName(),o.getName())) diffs.add("NAME");
		if(!isSame(getRank(),o.getRank())) diffs.add("RANK");
		if(!isSame(isActive(),o.isActive())) diffs.add("ACTIVE");
		if(!isSame(getDescription(),o.getDescription())) diffs.add("DESCRIPTION");
		return diffs;
	}
	public void insertParents(){
		if(group != null && group.isNewInstance())
				group.insert();
	}
	public void insertChildren(){
		if(generalDatas != null){
			for(GeneralData generalData:getGeneralDatas())
				generalData.setGroup((GeneralData)this);
		}
		if(generalDatas != null){
			for(GeneralData generalData:getGeneralDatas())
				if(generalData.isNewInstance())
					generalData.insert();
			generalDatas = null;
		}
	}
}