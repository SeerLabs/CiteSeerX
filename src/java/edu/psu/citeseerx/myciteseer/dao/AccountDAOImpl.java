/*
 * Copyright 2007 Penn State University
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.psu.citeseerx.myciteseer.dao;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;

import edu.psu.citeseerx.myciteseer.domain.Account;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * AccountDAO implementation using MySQL as a persistent storage 
 * @author Isaac Councill
 * @version $$Rev$$ $$Date$$
 */
public class AccountDAOImpl extends JdbcDaoSupport implements AccountDAO {
    
    protected MappingSqlQuery usersByUsernameMapping;
    protected UsersByNamePassMapping usersByNamePassMapping;
    protected UsersByEmailMapping usersByEmailMapping;
    protected MappingSqlQuery authoritiesByUsernameMapping;
    protected InsertAccount insertUser;
    protected UpdateAccount updateUser;
    protected ChangePassword changePassword;
    protected InsertActivation insertActivation;
    protected DeleteActivation deleteActivation;
    protected GetActivation getActivation;
    protected DeleteAuthorities deleteAuthorities;
    protected InsertAuthority insertAuthority;
    protected InsertTicket insertTicket;
    protected DeleteTicket deleteTicket;
    protected ValidTicketMapping validTicketMapping;
    protected AllUsersMapping allUsersMapping;
    protected GetUserIndexTime getUserIndexTime;
    protected GetUsersSinceTimeMapping getUsersSinceTimeMapping;
    protected InsertUserIndexTime insertUserIndexTime;
    protected UpdateUserIndexTime updateUserIndexTime;
    protected GetDisabledUserInternalId getDisabledUserInternalId;
    protected GroupDAO groupDAO;
    protected UpdateAppid updateAppid;
    
    /**
	 * @param groupDAO DAO object to access group related information
	 */
	public void setGroupDAO(GroupDAO groupDAO) {
		this.groupDAO = groupDAO;
	} //- setGroupDAO

	private boolean isGroupsEnable = false;
	
	/**
	 * @param isGroupsEnable the isGroupsEnable to set
	 */
	public void setGroupsEnable(boolean isGroupsEnable) {
		this.isGroupsEnable = isGroupsEnable;
	} //- setGroupsEnable

	protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
    
    protected void initMappingSqlQueries() {
        this.usersByUsernameMapping =
            new UsersByUsernameMapping(getDataSource());
        this.usersByEmailMapping = new UsersByEmailMapping(getDataSource());
        this.usersByNamePassMapping =
            new UsersByNamePassMapping(getDataSource());
        this.authoritiesByUsernameMapping =
            new AuthoritiesByUsernameMapping(getDataSource());
        this.insertUser = new InsertAccount(getDataSource());
        this.updateUser = new UpdateAccount(getDataSource());
        this.changePassword = new ChangePassword(getDataSource());
        this.insertActivation = new InsertActivation(getDataSource());
        this.deleteActivation = new DeleteActivation(getDataSource());
        this.getActivation = new GetActivation(getDataSource());
        this.deleteAuthorities = new DeleteAuthorities(getDataSource());
        this.insertAuthority = new InsertAuthority(getDataSource());
        this.insertTicket = new InsertTicket(getDataSource());
        this.deleteTicket = new DeleteTicket(getDataSource());
        this.validTicketMapping = new ValidTicketMapping(getDataSource());
        this.allUsersMapping = new AllUsersMapping(getDataSource());
        this.getUserIndexTime = new GetUserIndexTime(getDataSource());
        this.getUsersSinceTimeMapping = 
        	new GetUsersSinceTimeMapping(getDataSource());
        this.insertUserIndexTime = new InsertUserIndexTime(getDataSource());
        this.updateUserIndexTime = new UpdateUserIndexTime(getDataSource());
        this.getDisabledUserInternalId = 
        	new GetDisabledUserInternalId(getDataSource());
        this.updateAppid = new UpdateAppid(getDataSource());
        
    }  //- initMappingSqlQueries
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getAccount(java.lang.String)
     */
    public Account getAccount(String username) throws UsernameNotFoundException,
            DataAccessException {
        Account user = getAccountOrNull(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
        
    }  //- getAccount
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getAccountOrNull(java.lang.String)
     */
    public Account getAccountOrNull(String username)
    throws UsernameNotFoundException, DataAccessException {
        List<Account> users = usersByUsernameMapping.execute(username);
        if (users.size() == 0) {
            return null;
        }
        Account user = (Account)users.get(0);
        List<GrantedAuthority> authorities = 
        	authoritiesByUsernameMapping.execute(username);
        Iterator<GrantedAuthority> it;
        for (it = authorities.iterator(); it.hasNext(); ) {
            GrantedAuthority authority = it.next();
            user.addGrantedAuthority(authority);
        }

        if (isGroupsEnable) {
        	// Load group authorities.
        	authorities = groupDAO.getGroupAuthorities(username);
        	for (it = authorities.iterator(); it.hasNext(); ) {
                GrantedAuthority authority = it.next();
                user.addGrantedAuthority(authority);
            }
        }
        return user;
        
    }  //- getAccountOrNull

    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getAccount(java.lang.String, java.lang.String)
     */
    public Account getAccount(String username, String password)
            throws UsernameNotFoundException, DataAccessException {
        List<Account> users = usersByNamePassMapping.run(username, password);
        if (users.size() == 0) {
            throw new UsernameNotFoundException("User not found");
        }
        Account user = users.get(0);
        return user;
        
    }  //- getAccount
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getAccountByEmail(java.lang.String)
     */
    public Account getAccountByEmail(String emailAddress) {
        List<Account> users = usersByEmailMapping.execute(emailAddress);
        if (users.size() == 0) {
            return null;
        }
        Account user = users.get(0);
        return user;
        
    }  //- getAccountByEmail
    
    
    /* (non-Javadoc)
     * @see org.springframework.security.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
     */
    public UserDetails loadUserByUsername(String name)
            throws UsernameNotFoundException, DataAccessException {
        return getAccount(name);
    } //- loadUserByUsername
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#insertAccount(edu.psu.citeseerx.myciteseer.domain.Account)
     */
    public void insertAccount(Account account) throws DataAccessException {
        System.err.println("Inserting account");
        insertUser.run(account);
        // Stores any authorities given to the user at creation time
        if (account.getAuthorities().length > 0) {
            for (GrantedAuthority authority : account.getAuthorities()) {
                if (!authority.getAuthority().equals("HOLDER")) {
                    insertAuthority.run(account.getUsername(),
                            authority.getAuthority());
                }
            }
        }
    } //- insertAccount


    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#updateAccount(edu.psu.citeseerx.myciteseer.domain.Account)
     */
    public void updateAccount(Account account) {
        updateUser.run(account);
        if (account.getAuthorities().length > 0) {
            deleteAuthorities.run(account.getUsername());
            for (GrantedAuthority authority : account.getAuthorities()) {
                if (!authority.getAuthority().equals("HOLDER") &&
                		!authority.getAuthority().contains("GROUP_")) {
                    insertAuthority.run(account.getUsername(),
                            authority.getAuthority());
                }
            }
        }
    } //- updateAccount
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#changePassword(edu.psu.citeseerx.myciteseer.domain.Account)
     */
    public void changePassword(Account account) {
        changePassword.run(account);
    } //- changePassword
    
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#storeActivationCode(java.lang.String, java.lang.String)
     */
    public void storeActivationCode(String username, String code) {
        System.err.println("STORING ACTIVATION");
        try {
            insertActivation.run(username, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } //- storeActivationCode
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#deleteActivationCode(java.lang.String)
     */
    public void deleteActivationCode(String username) {
        deleteActivation.run(username);
    } //- deleteActivationCode
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#isValidActivationCode(java.lang.String, java.lang.String)
     */
    public boolean isValidActivationCode(String username, String code) {
        List<Object> values = getActivation.run(username, code);
        if (values.size() > 0) {
            return true;
        } else {
            return false;
        }
    } //- isValidActivationCode
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#storeInvitationTicket(java.lang.String)
     */
    public void storeInvitationTicket(String ticket)
    throws DataAccessException {
        insertTicket.update(ticket);
    } //- storeInvitationTicket
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#deleteInvitationTicket(java.lang.String)
     */
    public void deleteInvitationTicket(String ticket)
    throws DataAccessException {
        deleteTicket.update(ticket);
    } //- deleteInvitationTicket
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#isValidInvitationTicket(java.lang.String)
     */
    public boolean isValidInvitationTicket(String ticket)
    throws DataAccessException {
        List<Long> ids = validTicketMapping.execute(ticket);
        if (ids.size() > 0) {
            return true;
        } else {
            return false;
        }
    } //- isValidInvitationTicket

    
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getUsers(java.lang.Long, int)
	 */
	public List<Account> getUsers(Long start, int amount) 
	throws DataAccessException {
		return allUsersMapping.run(start, amount);
	} //- getAllUsers
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getUsersSinceTime(java.util.Date, java.lang.Long, int)
	 */
	public List<Account> getUsersSinceTime(Date time, Long start, int amount)
			throws DataAccessException {
		return getUsersSinceTimeMapping.run(time, start, amount);
	} //- getUsersSinceTime

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getUserLastIndexTime()
	 */
	public Date getUserLastIndexTime() throws DataAccessException {
        Date lastIndex = getUserIndexTime.run();
        if (lastIndex == null) {
            lastIndex = new Date(0);
        }
        return lastIndex;
        
    }  //- getLastIndexTime
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#setUsersLastIndexTime(java.util.Date)
	 */
	public void setUsersLastIndexTime(Date time) throws DataAccessException {
		Date lastIndex = getUserIndexTime.run();
        if (lastIndex == null) {
            insertUserIndexTime.run(time);
        } else {
            updateUserIndexTime.run(time);
        }
		
	} //- setUsersLastIndexTime
	
	/* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#changeAppid(edu.psu.citeseerx.myciteseer.domain.Account)
     */
    public void changeAppid(Account account) throws DataAccessException {
        updateAppid.run(account);
        
    } //- changeAppid

    /* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getDisabled(java.util.Date)
	 */
	public List<Long> getDisabled(Date date) throws DataAccessException {
		return getDisabledUserInternalId.run(date);
	}

	private static final String DEF_AUTHORITIES_BY_USERNAME_QUERY =
        "select userid, authority from authorities where userid=?";

    protected class AuthoritiesByUsernameMapping extends MappingSqlQuery {
        protected AuthoritiesByUsernameMapping(DataSource ds) {
            super(ds, DEF_AUTHORITIES_BY_USERNAME_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
            String roleName = rs.getString("authority");
            GrantedAuthority authority = new GrantedAuthorityImpl(roleName);
            return authority;
        }
        
    }  //- class AuthoritiesByUsernameMapping
    
    
    protected abstract class UserMapping extends MappingSqlQuery {
        protected UserMapping(DataSource ds, String query) {
            super(ds, query);
        } //- UserMapping.UserMapping

        protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
            Account account = new Account();
            account.setUsername(rs.getString("userid"));
            account.setPassword(rs.getString("password"));
            account.setFirstName(rs.getString("firstName"));
            account.setMiddleName(rs.getString("middleName"));
            account.setLastName(rs.getString("lastName"));
            account.setEmail(rs.getString("email"));
            account.setAffiliation1(rs.getString("affil1"));
            account.setAffiliation2(rs.getString("affil2"));
            account.setEnabled(rs.getBoolean("enabled"));
            account.setCountry(rs.getString("country"));
            account.setProvince(rs.getString("province"));
            account.setWebPage(rs.getString("webPage"));
            account.setInternalId(rs.getLong("internalid"));
            
            long updated = rs.getTimestamp("updated").getTime();
            account.setUpdated(new Date(updated));
            account.setAppid(rs.getString("appid"));
            return account;
            
        }  //- UserMapping.mapRow
        
    }  //- class UserMapping
    
    
    private static final String DEF_USERS_BY_USERNAME_QUERY =
        "select userid, password, firstName, middleName, lastName, " +
        "email, affil1, affil2, enabled, country, province, webPage, " +
        "internalid, updated, appid from users where userid=?";
    
    protected class UsersByUsernameMapping extends UserMapping {

        protected final static String query = DEF_USERS_BY_USERNAME_QUERY; 

        protected UsersByUsernameMapping(DataSource ds) {
            super(ds, query);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } // - UsersByUsernameMapping.UsersByUsernameMapping
                
    }  //- class UsersByUsernameMapping

    
    private static final String DEF_USERS_BY_EMAIL_QUERY =
        "select userid, password, firstName, middleName, lastName, " +
        "email, affil1, affil2, enabled, country, province, webPage, " +
        "internalid, updated, appid from users where email=?";
    
    protected class UsersByEmailMapping extends UserMapping {

        protected final static String query = DEF_USERS_BY_EMAIL_QUERY; 

        protected UsersByEmailMapping(DataSource ds) {
            super(ds, query);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- UsersByEmailMapping.UsersByEmailMapping
                
    }  //- class UsersByEmailMapping

    
    private static final String DEF_USERS_BY_NAMEPASS_QUERY =
        "select userid, password, firstName, middleName, lastName, email, " +
        "affil1, affil2, enabled, country, province, webPage, internalid, " +
        "updated, appid from users where userid=? and password=?";
    
    protected class UsersByNamePassMapping extends UserMapping {
        
        protected final static String query = DEF_USERS_BY_NAMEPASS_QUERY;
        
        protected UsersByNamePassMapping(DataSource ds) {
            super(ds, query);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- UsersByNamePassMapping.UsersByNamePassMapping
        
        public List<Account> run(String username, String password) {
            Object[] params = new Object[] { username, password };
            return execute(params);
        } //- UsersByNamePassMapping.run
        
    }  //- class UsersByNamePassMapping
    
    
    private static final String DEF_INSERT_USER_STATEMENT =
        "insert into users values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
        " null, null, null)";

    protected class InsertAccount extends SqlUpdate {
        public InsertAccount(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_INSERT_USER_STATEMENT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.TINYINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
            
        }  //- InsertAccount.InsertAccount
        
        public int run(Account account) {
            Object[] params = new Object[] {
                    account.getUsername(), account.getPassword(),
                    account.getFirstName(), account.getMiddleName(),
                    account.getLastName(), account.getEmail(),
                    account.getAffiliation1(), account.getAffiliation2(),
                    new Boolean(account.isEnabled()), account.getCountry(),
                    account.getProvince(), account.getWebPage() };
            return update(params);
            
        }  //- InsertAccount.run
        
    }  //- class InsertAccount

    
    private static final String DEF_UPDATE_USER_STATEMENT =
        "update users set firstName=?, middleName=?, "+
        "lastName=?, email=?, affil1=?, affil2=?, enabled=?, " +
        "country=?, province=?, webPage=? where userid=?";
    
    protected class UpdateAccount extends SqlUpdate {
        public UpdateAccount(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_UPDATE_USER_STATEMENT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.TINYINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
            
        }  //- UpdateAccount.UpdateAccount
        
        public int run(Account account) {
            System.err.println(account.toString());
            Object[] params = new Object[] {
                    account.getFirstName(), account.getMiddleName(),
                    account.getLastName(), account.getEmail(),
                    account.getAffiliation1(), account.getAffiliation2(),
                    new Boolean(account.isEnabled()), account.getCountry(),
                    account.getProvince(), account.getWebPage(),
                    account.getUsername() };
            return update(params);
            
        }  //- UpdateAccount.run
        
    }  //- class UpdateAccount
    
    
    private static final String DEF_CHANGE_PASSWD_STATEMENT =
        "update users set password=? where userid=?";
    
    protected class ChangePassword extends SqlUpdate {
        public ChangePassword(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_CHANGE_PASSWD_STATEMENT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- ChangePassword.ChangePassword
        
        public int run(Account account) {
            Object[] params = new Object[] {
                    account.getPassword(), account.getUsername()
            };
            return update(params);
        } //- ChangePassword.run
        
    }  //- class ChangePassword
    
   
    private static final String DEF_INSERT_ACTIVATION_STATEMENT =
        "insert into activation values (NULL, ?, ?, NULL)";

    protected class InsertActivation extends SqlUpdate {
        public InsertActivation(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_INSERT_ACTIVATION_STATEMENT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- InsertActivation.InsertActivation
        
        public int run(String username, String code) {
            Object[] params = new Object[] { username, code };
            return update(params);
        } //- InsertActivation.run
        
    }  //- class InsertActivation
    
    
    private static final String DEF_DELETE_ACTIVATION_STATEMENT =
        "delete from activation where userid=?";

    protected class DeleteActivation extends SqlUpdate {
        public DeleteActivation(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_DELETE_ACTIVATION_STATEMENT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- DeleteActivation.DeleteActivation
        
        public int run(String username) {
            Object[] params = new Object[] { username };
            return update(params);
        } //- DeleteActivation.run
        
    }  //- class DeleteActivation
    
    
    private final static String DEF_GET_ACTIVATION_QUERY =
        "select userid, code, created from activation where userid=? and " +
        "code=?";
    
    protected class GetActivation extends MappingSqlQuery {
        public GetActivation(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_GET_ACTIVATION_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- GetActivation.GetActivation

        protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
            List<Object> values = new ArrayList<Object>();
            values.add(rs.getString("userid"));
            values.add(rs.getString("code"));
            values.add(rs.getTimestamp("created"));
            return values;
            
        } //- GetActivation.mapRow
        
        public List<Object> run(String username, String code) {
            Object[] params = new Object[] { username, code };
            return execute(params);
        } //- GetActivation.run
        
    }  //- class GetActivation
    
    
    private final static String DEF_DEL_AUTHORITIES_STMT =
        "delete from authorities where userid=?";
    
    protected class DeleteAuthorities extends SqlUpdate {
        public DeleteAuthorities(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_DEL_AUTHORITIES_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- DeleteAuthorities.DeleteAuthorities
        
        public int run(String username) {
            Object[] params = new Object[] { username };
            return update(params);
        } //- DeleteAuthorities.run
        
    } //- class DeleteAuthorities
    
    
    private final static String DEF_INSERT_AUTHORITY_STMT =
        "insert into authorities values (NULL, ?, ?)";
    
    protected class InsertAuthority extends SqlUpdate {
        public InsertAuthority(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_INSERT_AUTHORITY_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- InsertAuthority.InsertAuthority
        
        public int run(String username, String authority) {
            Object[] params = new Object[] { username, authority };
            return update(params);
        } //- InsertAuthority.run
        
    } //- class InsertAuthority
    
    
    private final static String DEF_INS_TICKET_STMT =
        "insert into invitations values (NULL, ?)";
    
    protected class InsertTicket extends SqlUpdate {
        public InsertTicket(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_INS_TICKET_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- InsertTicket.InsertTicket
    } //- class InsertTicket
    
    
    private final static String DEF_DEL_TICKET_STMT =
        "delete from invitations where ticket=?";
    
    protected class DeleteTicket extends SqlUpdate {
        public DeleteTicket(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_DEL_TICKET_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- DeleteTicket.DeleteTicket
    } //- class DeleteTicket
    
    
    private final static String DEF_VALID_TICKET_QUERY =
        "select id from invitations where ticket=?";
    
    protected class ValidTicketMapping extends MappingSqlQuery {
        public ValidTicketMapping(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_VALID_TICKET_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- ValidTicketMapping.ValidTicketMapping
        
        public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getLong("id");
        } //- ValidTicketMapping.mapRow
    } //- class ValidTicketMapping
    
    private final static String DEF_GET_ALL_USERS_QUERY =
    	"select userid, password, firstName, middleName, lastName, " +
        "email, affil1, affil2, enabled, country, province, webPage, " +
        "internalid, updated, appid from users where internalid > ? limit ? ";
    
    protected class AllUsersMapping extends UserMapping {

        protected final static String query = DEF_GET_ALL_USERS_QUERY; 

        protected AllUsersMapping(DataSource ds) {
            super(ds, query);
            compile();
        } // - AllUsersMapping.AllUsersMapping
        
        public List<Account> run(Long start, int amount) {
            Object[] params = new Object[] { start, new Integer(amount) };
            return execute(params);
        } //- AllUsersMapping.run
        
    }  //- class UsersByUsernameMapping
    
    private final static String DEF_GET_USERS_SINCE_TIME_QUERY =
    	"select userid, password, firstName, middleName, lastName, " +
        "email, affil1, affil2, enabled, country, province, webPage, " +
        "internalid, updated, appid from users where updated >= ? " +
        "and internalid > ?  order by internalid asc limit ?";
    
    protected class GetUsersSinceTimeMapping extends UserMapping {

        protected final static String query = DEF_GET_USERS_SINCE_TIME_QUERY; 

        protected GetUsersSinceTimeMapping(DataSource ds) {
            super(ds, query);
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        } // - GetUsersSinceTimeMapping.GetUsersSinceTimeMapping
        
        public List<Account> run(Date time, Long start, int amount) {
        	Object[] params = {
                    new Timestamp(time.getTime()), start, new Integer(amount)
            };
            return execute(params);
        } //- GetUsersSinceTimeMapping.run
        
    }  //- class GetUsersSinceTimeMapping
    
    private static final String DEF_GET_USER_INDEX_TIME_QUERY =
        "select lastupdate from indexTime where param=\"userIndexTime\"";
    
    protected class GetUserIndexTime extends MappingSqlQuery {
        
        public GetUserIndexTime(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_USER_INDEX_TIME_QUERY);
            compile();
        } //- GetUserIndexTime.GetUserIndexTime
        
        public Date mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Date(rs.getTimestamp(1).getTime());
        } //- GetUserIndexTime.mapRow
        
        public Date run() {
            List<Date> list = execute();
            if (list.isEmpty()) {
                return null;
            } else {
                return (Date)list.get(0);
            }
        } //-GetUserIndexTime.run
        
    }  //- class GetUserIndexTime
    
    private static final String DEF_INSERT_USER_INDEX_TIME_STMT =
        "insert into indexTime values (\"userIndexTime\", ?)";
    
    protected class InsertUserIndexTime extends SqlUpdate {
        
        public InsertUserIndexTime(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_USER_INDEX_TIME_STMT);
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            compile();
        } //- InsertUserIndexTime.InsertUserIndexTime
        
        public int run(Date date) {
            return update(new Object[] { new Timestamp(date.getTime()) });
        } //- InsertUserIndexTime.run
        
    }  //- class InsertIndexTime
    
    private static final String DEF_UPDATE_USER_INDEX_TIME_STMT =
        "update indexTime set lastupdate=? where param=\"userIndexTime\"";
    
    protected class UpdateUserIndexTime extends SqlUpdate {
        
        public UpdateUserIndexTime(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_USER_INDEX_TIME_STMT);
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            compile();
        } //- UpdateUserIndexTime.UpdateUserIndexTime
        
        public int run(Date date) {
            return update(new Object[] { new Timestamp(date.getTime()) });
        } //- UpdateUserIndexTime.run
        
    }  //- class UpdateIndexTime
    
    private static final String DEF_GET_DISABLED_USER_INTERNALID_QUERY =
        "select internalid from users where enabled=0 and updated < ?";
    
    protected class GetDisabledUserInternalId extends MappingSqlQuery {
        
        public GetDisabledUserInternalId(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_DISABLED_USER_INTERNALID_QUERY);
            declareParameter(new SqlParameter(Types.TIMESTAMP));
            compile();
        } //- GetDisabledUserInternalId.GetDisabledUserInternalId
        
        public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
        	return rs.getLong(1);
        } //- GetDisabledUserInternalId.mapRow
        
        public List<Long> run(Date date) {
        	return execute(new Object[] { new Timestamp(date.getTime()) });
        } //-GetDisabledUserInternalId.run
        
    }  //- class GetDisabledUserInternalId
    
    private static final String DEF_CHANGE_APPID_STATEMENT =
        "update users set appid=? where userid=?";
    
    protected class UpdateAppid extends SqlUpdate {
        public UpdateAppid(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_CHANGE_APPID_STATEMENT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- UpdateAppid.UpdateAppid
        
        public int run(Account account) {
            Object[] params = new Object[] {
                    account.getAppid(), account.getUsername()
            };
            return update(params);
        } //- UpdateAppid.run
        
    }  //- class UpdateAppid
}  //- class AccountDAOImpl
