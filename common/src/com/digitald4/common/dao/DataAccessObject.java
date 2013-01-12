/**
 *           | Master Data Interface Version 2.0 |
 *
 * Copyright (c) 2006, Southern California Edison, Inc.
 * 					   Distribution Staff Engineering Team.
 * 	                   All rights reserved.
 *
 * This software has been developed exclusively for internal usage.
 * Unauthorized use is prohibited.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.digitald4.common.dao;
import java.util.Collection;
import java.util.HashMap;
import java.util.Observable;
import java.util.Vector;

import com.digitald4.common.jpa.Change;
import com.digitald4.common.jpa.ChangeLog;
import com.digitald4.common.jpa.Entity;
import com.digitald4.common.jpa.EntityManagerHelper;
import com.digitald4.common.jpa.PrimaryKey;
import com.digitald4.common.util.Calculate;

/**
 * Data Access Object
 *
 * @author Eddie Mayfield
 */
public abstract class DataAccessObject extends Observable implements Comparable<Object>, ChangeLog, Entity{
	private HashMap<String,Change> changes;
	
    /**
     * Creates a new instance of DBObject.
     */
    public DataAccessObject() {
    }

    /**
	 * @param orig The original object to get data from 
	 */
    public DataAccessObject(DataAccessObject orig){
	}

	/**
     * Checks if is new instance.
     *
     * @return true, if is new instance
     */
    public boolean isNewInstance(){
    	return EntityManagerHelper.getEntityManager()==null || !EntityManagerHelper.getEntityManager().contains(this);
    }
    
    /**
     * Compare to.
     *
     * @param o the o
     *
     * @return the int
     */
    public int compareTo(Object o){
    	//If this is the same exact object in memory then just say so
    	if(this == o)
    		return 0;
    	if(o instanceof DataAccessObject)
    		return (toString()+getHashKey()).compareTo(o.toString()+((DataAccessObject)o).getHashKey());
        return 0;
    }

    /**
     * Gets the hash key.
     *
     * @return the hash key
     */
    public abstract String getHashKey();

    /**
     * Refresh.
     *
     * @return true, if refresh
     *
     * @throws SQLException the SQL exception
     */
    public void refresh() {
    	EntityManagerHelper.getEntityManager().refresh(this);
    }
	
	public Collection<Change> getChanges(){
		return changes.values();
	}
	
	public void addChange(String prop, Object newValue, Object oldValue){
		if(changes == null)
			changes = new HashMap<String,Change>();
		changes.put(prop, new Change(prop,newValue,oldValue));
	}

	protected void setProperty(String prop, Object newValue, Object oldValue) {
		if(prop==null)return;
		if(isNewInstance()) return;
		addChange(prop, newValue,oldValue);
	}
	
	/**
	 * This does not insert into the database.  This call EntityManager merge.
	 */
	public void save(){
		if(changes!=null){
			EntityManagerHelper.getEntityManager().merge(this);
			changes.clear();
		}
	}

	public void delete() {
		EntityManagerHelper.getEntityManager().remove(this);
	}
    
    public void insertParents() throws Exception{
    }

    /**
     * Insert.
     * @throws Exception 
     *
     * @throws SQLException the SQL exception
     */
    public void insert() throws Exception {
    	insertParents();
    	if(isNewInstance()){
    		insertPreCheck();
    		EntityManagerHelper.getEntityManager().persist(this);
    	}
    	insertChildren();
    }
    
    public abstract void insertPreCheck() throws Exception;
    
    public void insertChildren() throws Exception { 	
    }
    
    public static boolean isSame(Object o, Object o2){
    	return Calculate.isSame(o, o2);
    }

    /**
     * Returns a string hash code of an object of this type with the
     * specified parameters. The hash code would be used to find the
     * object in a hash table.
     *
     * @param id - id for the object
     * @param planyear - planYear for the object
     * @param k1 the k1
     *
     * @return a string hash code of an object of this type with the specified parameters.
     */
    public static String getHashKey(Object[] keys){
    	return PrimaryKey.getHashKey(keys);
    }
    
    public static String getHashKey(Object key){
    	return PrimaryKey.getHashKey(key);
    }

    public static boolean isNull(Object... keys){
    	for(Object k:keys)
    		if(Calculate.isNull(k))
    			return true;
    	return false;
    }

    /**
     * To string.
     *
     * @return the string
     */
    public String toString(){
    	return getHashKey();
    }
    
    /**
	 * @param dao The object to compare to. 
	 */
    public Vector<String> getDifference(DataAccessObject dao){
    	return new Vector<String>();
    }
    
    /**
	 * @param cp The object to copy children to 
	 */
    public void copyChildrenTo(DataAccessObject cp){
    }
    
    public String formatProperty(String colname) {
    	colname = colname.toUpperCase();
    	if (colname.contains(".")) {
    		colname = colname.substring(colname.lastIndexOf('.')+1);
    	}
    	return colname;
    }

	public abstract Object getPropertyValue(String colName);
	
	public abstract void setPropertyValue(String colName, String value) throws Exception;
}
