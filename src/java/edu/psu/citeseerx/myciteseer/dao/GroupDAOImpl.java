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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;

import edu.psu.citeseerx.myciteseer.acl.PermissionManager;
import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Group;
import edu.psu.citeseerx.myciteseer.domain.GroupMember;

/**
 * GroupDAO implementation using MYSQL as a persistent storage.
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Rev: 191 $$ $$Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $$
 */
public class GroupDAOImpl extends JdbcDaoSupport implements GroupDAO {

	protected InsertGroup insertGroup;
	protected InsertGroupMember insertGroupMember;
	protected GetGroupMapping getGroupMapping;
	protected GroupExistsMapping groupExistsMapping;
	protected GetGroupsMapping getGroupsMapping;
	protected DeleteGroup deleteGroup;
	protected GetGroupMembersMapping getGroupMembersMapping;
	protected UpdateGroup updateGroup;
	protected RemoveMember removeMember;
	protected ValidateUser validateUser;
	protected GetGroupAuthoritiesByUsernameMapping 
		getGroupAuthoritiesByUsernameMapping;
	protected GetIsGroupMemberMapping getIsGroupMemberMapping;
	
	private PermissionManager permissionManager;
	
	/**
	 * @param permissionManager Object to manipulate ACL entries related to
	 *        groups.
	 */
	public void setPermissionManager(PermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	} //- setPermissionManager
	
	/* (non-Javadoc)
	 * @see org.springframework.dao.support.DaoSupport#initDao()
	 */
	protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
	
	protected void initMappingSqlQueries() {
		this.insertGroup = new InsertGroup(getDataSource());
		this.insertGroupMember = new InsertGroupMember(getDataSource());
		this.getGroupMapping = new GetGroupMapping(getDataSource());
		this.groupExistsMapping = new GroupExistsMapping(getDataSource());
		this.getGroupsMapping = new GetGroupsMapping(getDataSource());
		this.deleteGroup = new DeleteGroup(getDataSource());
		this.getGroupMembersMapping = 
			new GetGroupMembersMapping(getDataSource());
		this.updateGroup = new UpdateGroup(getDataSource());
		this.removeMember = new RemoveMember(getDataSource());
		this.validateUser = new ValidateUser(getDataSource());
		this.getGroupAuthoritiesByUsernameMapping =
			new GetGroupAuthoritiesByUsernameMapping(getDataSource());
		this.getIsGroupMemberMapping = 
			new GetIsGroupMemberMapping(getDataSource());
	} //- initMappingSqlQueries
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#addGroup(edu.psu.citeseerx.myciteseer.domain.Group)
	 */
	public Group addGroup(Group group) throws DataAccessException {
		Long groupID = insertGroup.run(group);
		group.setId(groupID);
		
		// Add the owner of the group as a member
		addMember(group, group.getOwner(), false);
		return group;
	} //- addGroup

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#addMember(edu.psu.citeseerx.myciteseer.domain.Group, java.lang.String, boolean)
	 */
	public void addMember(Group group, String userid, boolean validating)
			throws DataAccessException {
		insertGroupMember.run(group.getId(), userid, validating);
		if (group.getOwner().toLowerCase().compareTo(userid.toLowerCase()) == 0) {
			permissionManager.addAdminPermission(group, userid);
		}else{
			permissionManager.addReadPermission(group, userid);
		}
	} //- addMember

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#isNameRepeated(edu.psu.citeseerx.myciteseer.domain.Group, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public boolean isNameRepeated(Group group, Account account)
			throws DataAccessException {
		boolean exists = false;

		List<Group> groups = groupExistsMapping.run(group, account);

		if (groups.size() > 0 && (groups.get(0).getId() != group.getId()) ) {
			exists = true;
        }
		return exists;
	} //- isNameRepeated
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#getGroup(long, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public Group getGroup(long groupID)
			throws DataAccessException {

		Group theGroup = null;
		List<Group> groups = getGroupMapping.run(groupID);
		if (groups.size() > 0) {
			theGroup = (Group)groups.get(0);
		}
		return theGroup;
	} //- getGroup
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#getGroups(java.lang.String)
	 */
	public List<Group> getGroups(String username) throws DataAccessException {
		return getGroupsMapping.run(username);
	} //- getGroups
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#deleteGroup(edu.psu.citeseerx.myciteseer.domain.Group)
	 */
	public void deleteGroup(Group group)
			throws DataAccessException {
		deleteGroup.run(group);
		
		// delete the group ACL information
		permissionManager.deleteACL(group);

	} //- deleteGroup


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#getMembers(edu.psu.citeseerx.myciteseer.domain.Group)
	 */
	public List<GroupMember> getMembers(Group group)
			throws DataAccessException {
		return getGroupMembersMapping.run(group);
	} //- getGroupMembers
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#updateGroup(edu.psu.citeseerx.myciteseer.domain.Group)
	 */
	public void updateGroup(Group group)
			throws DataAccessException {
		updateGroup.run(group);
	} //- updateGroup

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#leaveGroup(edu.psu.citeseerx.myciteseer.domain.Group, java.lang.String)
	 */
	public void leaveGroup(Group group, String userid)
			throws DataAccessException {
		removeMember.run(group, userid);

		// Remove permissions
		permissionManager.deletePermissions(group, userid);
	} //- leaveGroup

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#removeMember(edu.psu.citeseerx.myciteseer.domain.Group, java.lang.String)
	 */
	public void removeMember(Group group, String userid)
			throws DataAccessException {
		removeMember.run(group, userid);
		
		// Remove permissions
		permissionManager.deletePermissions(group, userid);
	} //- removeMember

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#validateUser(long, java.lang.String)
	 */
	public void validateUser(Group group, String userid)
			throws DataAccessException {
		validateUser.run(group, userid);
	} //- validateUser
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#getGroupAuthorities(java.lang.String)
	 */
	public List<GrantedAuthority> getGroupAuthorities(String username)
			throws DataAccessException {
		return getGroupAuthoritiesByUsernameMapping.run(username);
	} //- getGroupAuthorities
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#isMember(edu.psu.citeseerx.myciteseer.domain.Group, java.lang.String)
	 */
	public boolean isMember(Group group, String userid)
			throws DataAccessException {
		List<String> users = getIsGroupMemberMapping.run(group, userid);
		return !(users.isEmpty());
	} //- isMember



	/*
	 * Utility classes providing SQL access
	 */
	private static final String DEF_INSERT_GROUP_STATEMENT = 
		"insert into groups values (NULL, ?, ?, ?, ?)";

	protected class InsertGroup extends SqlUpdate {
		public InsertGroup(DataSource ds) {
			setDataSource(ds);
			setSql(DEF_INSERT_GROUP_STATEMENT);
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			setReturnGeneratedKeys(true);
			compile();
		} //- InsertGroup.InsertGroup
		
		public Long run(Group group) {
			Object[] params = new Object[] {
					group.getName(),
					group.getDescription(),
					group.getOwner(),
					group.getAuthority()
			};
			KeyHolder holder = new GeneratedKeyHolder();
			update(params, holder);
			return new Long(holder.getKey().longValue());
			
		} //- InsertGroup.run
	} //- class InsertGroup
	
	private static final String DEF_INSERT_GROUP_MEMBERS_STATEMENT = 
		"insert into group_members values (?, ?, ?)";
	
	protected class InsertGroupMember extends SqlUpdate {
		public InsertGroupMember(DataSource ds) {
			setDataSource(ds);
			setSql(DEF_INSERT_GROUP_MEMBERS_STATEMENT);
			declareParameter(new SqlParameter(Types.BIGINT));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.TINYINT));
			compile();
		} //- InsertGroupMember.InsertGroupMember
		
		public int run(long groupID, String userid, boolean validating) {
			Object[] params = new Object[] {
					groupID, userid, new Boolean(validating)
			};
			return update(params);
		} //- InsertGroupMember.run
	} //- class InsertGroupMember
	
	private static final String DEF_GROUP_NAME_ALREADY_EXISTS_QUERY =
		"select id, name, description, owner, authority from groups g " +
		"where name = ?";
	
	protected class GroupExistsMapping extends MappingSqlQuery {
		public GroupExistsMapping(DataSource ds) {
			super(ds, DEF_GROUP_NAME_ALREADY_EXISTS_QUERY);
			declareParameter(new SqlParameter(Types.VARCHAR));
		} //- GroupExistsMapping.GroupExistsMapping
		
		protected Group mapRow(ResultSet rs, int rownum) throws SQLException {
			Group group = new Group();
			
			group.setId(rs.getLong("id"));
			group.setName(rs.getString("name"));
			group.setDescription(rs.getString("description"));
			group.setOwner(rs.getString("owner"));
			group.setAuthority(rs.getString("authority"));
			return group;
		} //- GroupExistsMapping.MapRow
		
		public List<Group> run(Group group, Account account) {
            Object[] params = new Object[] { group.getName() };
            return execute(params);
        } //- GroupExistsMapping.run
	} //- GroupExistsMapping
	
	private static final String DEF_GET_GROUP_QUERY =
		"select id, name, description, owner, authority from groups " +
		"where id = ?";
	
	protected class GetGroupMapping extends MappingSqlQuery {
		public GetGroupMapping(DataSource ds) {
			super(ds, DEF_GET_GROUP_QUERY);
			declareParameter(new SqlParameter(Types.BIGINT));
		} //- GetGroupMapping.GetCollectionMapping
		
		protected Group mapRow(ResultSet rs, int rownum) throws SQLException {
			Group group = new Group();
			
			group.setId(rs.getLong("id"));
			group.setName(rs.getString("name"));
			group.setDescription(rs.getString("description"));
			group.setOwner(rs.getString("owner"));
			group.setAuthority(rs.getString("authority"));
			return group;
		} //- GetGroupMapping.mapRow
		
		public List<Group> run(long groupID) {
            Object[] params = new Object[] {groupID};

            return execute(params);
        } //- GetGroupMapping.run
	} //- class GetGroupMapping
	
	private static final String DEF_GET_USER_GROUPS_QUERY = 
		"select g.id, g.name, g.description, g.owner, g.authority, " +
		"gm.userid from groups g inner join group_members gm on " +
		"g.id = gm.groupid where gm.userid = ?";
	
	protected class GetGroupsMapping extends MappingSqlQuery {

		public GetGroupsMapping(DataSource ds) {
			super(ds, DEF_GET_USER_GROUPS_QUERY);
			declareParameter(new SqlParameter(Types.VARCHAR));
		} //- GetGroupsMapping.GetGroupsMapping
		
		protected Group mapRow(ResultSet rs, int rownum) throws SQLException {
			Group group = new Group();
			group.setId(rs.getLong("id"));
			group.setName(rs.getString("name"));
			group.setDescription(rs.getString("description"));
			group.setOwner(rs.getString("owner"));
			group.setAuthority(rs.getString("authority"));
			return group;
		} //- GetGroupsMapping.mapRow
		
		public List<Group> run(String username) {
            Object[] params = new Object[] { username };
            return execute(params);
        } //- GetGroupsMapping.run
	} // class GetGroupsMapping
	
	private static final String DEF_DELETE_GROUP_STATEMENT = 
		"delete from groups where id = ?";
	
	protected class DeleteGroup extends SqlUpdate {
		public DeleteGroup(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_DELETE_GROUP_STATEMENT);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        } //- DeleteGroup.DeleteGroup
		
		public int run(Group group) {
			Object[] params = new Object[] {
					group.getId()
			};
            return update(params);
		} //- DeleteGroup.run
	} //- class DeleteGroup
	
	private static final String DEF_GET_GROUP_MEMBERS_QUERY =
		"select u.userid, u.firstName, u.middleName, u.lastName, u.email, " +
		"u.affil1, u.affil2, u.country, u.province, u.webPage, " +
		"m.groupid, m.validating FROM users u " +
		"inner join group_members m ON u.userid = m.userid WHERE m.groupid = ?";
	
	protected class GetGroupMembersMapping extends MappingSqlQuery {
		public GetGroupMembersMapping(DataSource ds) {
			super(ds, DEF_GET_GROUP_MEMBERS_QUERY);
			declareParameter(new SqlParameter(Types.BIGINT));
		} //- GetGroupMembersMapping.GetGroupMembersMapping
		
		protected GroupMember mapRow(ResultSet rs, int rownum) 
		throws SQLException {
			GroupMember groupMember = new GroupMember();
			Account member= new Account();
			member.setUsername(rs.getString("userid"));
			member.setFirstName(rs.getString("firstName"));
			member.setMiddleName(rs.getString("middleName"));
			member.setLastName(rs.getString("lastName"));
			member.setEmail(rs.getString("email"));
			member.setAffiliation1(rs.getString("affil1"));
			member.setAffiliation2(rs.getString("affil2"));
			member.setCountry(rs.getString("country"));
			member.setProvince(rs.getString("province"));
			member.setWebPage(rs.getString("webPage"));
			groupMember.setGroupId(rs.getLong("groupID"));
			groupMember.setMember(member);
			groupMember.setValidating(rs.getBoolean("validating"));
			return groupMember;
		} //- GetGroupMembersMapping.mapRow
		
		public List<GroupMember> run(Group group) {
            Object[] params = new Object[] { group.getId() };
            return execute(params);
        } //- GetGroupMembersMapping.run
	} //- class GetGroupMembersMapping
	
	
	private static final String DEF_UPDATE_GROUP_STATEMENT =
		"update groups set name = ?, description = ? where id = ?";
	
	protected class UpdateGroup extends SqlUpdate {
		public UpdateGroup(DataSource ds) {
			setDataSource(ds);
			setSql(DEF_UPDATE_GROUP_STATEMENT);
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.VARCHAR));
			declareParameter(new SqlParameter(Types.BIGINT));
		} //- UpdateGroup.UpdateGroup
		
		int run(Group group) {
			Object[] params = new Object[] {
					group.getName(),
					group.getDescription(),
					group.getId()
			};
			return update(params);
		} //- UpdateGroup.run
	} //- class UpdateGroup
	
	private static final String DEF_DEL_USER_FROM_GROUP_STMT =
		"delete group_members gm from group_members gm inner join groups g " + 
		"on g.id = gm.groupid where gm.groupid = ? and gm.userid = ? and " +
		"g.owner <> gm.userid";
	
	protected class RemoveMember extends SqlUpdate {
		public RemoveMember(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_DEL_USER_FROM_GROUP_STMT);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- RemoveUserGroup.RemoveUserGroup
		
		public int run(Group group, String userid) {
			Object[] params = new Object[] {group.getId(), userid};
            return update(params);
		} //- RemoveUserGroup.run
	} //- class RemoveUserGroup
	
	private final static String DEF_VALIDATE_USER_STMT =
		"update group_members set validating = 0 where groupid = ? " +
		"and userid = ?";
	
	protected class ValidateUser extends SqlUpdate {
		public ValidateUser(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_VALIDATE_USER_STMT);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        } //- ValidateUser.ValidateUserGroup
		
		public int run(Group group, String userid) {
			Object[] params = new Object[] {group.getId(), userid};
            return update(params);
		} //- ValidateUser.run
	} //- class ValidateUser
	
	private static final String DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY =
		"select g.authority from groups g inner join group_members gm on " +
		"g.id = gm.groupid where gm.userid = ?";
	
	protected class GetGroupAuthoritiesByUsernameMapping extends MappingSqlQuery {
		public GetGroupAuthoritiesByUsernameMapping(DataSource ds) {
			super(ds, DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY);
			declareParameter(new SqlParameter(Types.VARCHAR));
		} //- GetGroupAuthoritiesByUsernameMapping.GetGroupAuthoritiesByUsernameMapping
		
		protected GrantedAuthority mapRow(ResultSet rs, int rownum) 
		throws SQLException {
			GrantedAuthority authority = 
				new GrantedAuthorityImpl(rs.getString("authority"));
			return authority;
		} //- GetGroupAuthoritiesByUsernameMapping.mapRow
		
		public List<GrantedAuthority> run(String username) {
			Object[] params = new Object[] { username };
			return execute(params);
		} //- GetUserNotInGroupByIDMapping.run
	} //- class GetUserNotInGroupByIDMapping
	
	private static final String DEF_IS_GROUP_MEMBER_QUERY =
		"select userid from group_members where groupid = ? and userid = ?";
	
	protected class GetIsGroupMemberMapping extends MappingSqlQuery {
		public GetIsGroupMemberMapping(DataSource ds) {
			super(ds, DEF_IS_GROUP_MEMBER_QUERY);
			declareParameter(new SqlParameter(Types.BIGINT));
			declareParameter(new SqlParameter(Types.VARCHAR));
		} //- GetIsGroupMemberMapping.GetIsGroupMemberMapping 
		
		protected String mapRow(ResultSet rs, int rownum) throws SQLException {
			String username  = rs.getString("userid");
			return username;
		} //- GetIsGroupMemberMapping.mapRow
		
		public List<String> run(Group group, String username) {
			Object[] params = new Object[] {group.getId(), username };
			return execute(params);
		} //- GetIsGroupMemberMapping.run
	} //- class GetIsGroupMemberMapping
} //- GroupDAOImpl
