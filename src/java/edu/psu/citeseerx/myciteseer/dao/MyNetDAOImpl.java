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
import org.springframework.context.ApplicationContextException;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Friend;
import edu.psu.citeseerx.myciteseer.domain.UserMessage;

import javax.sql.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.List;

public class MyNetDAOImpl extends JdbcDaoSupport implements MyNetDAO {

    private static final String GET_FRIENDS =
        "select userid, firstName, middleName, lastName from "+
        "MCSFriends, users where host=? and friend=userid";
    private String INSERT_NEW_FRIEND =
        "insert into MCSFriends (host, friend) values(?, ?)";
    private static String GET_MESSAGES =
        "select msgfrom, msgbody, time, viewed from MCSMessage where msgto=?";
    private String INSERT_NEW_MESSAGE =
        "insert into MCSMessage (msgfrom, msgto, msgbody, viewed) "+
        "values(?, ?, ?, 0)";
    
    protected FriendsQuery getFriendsMapping;
    protected AddFriend addFriend;
    protected GetMessages getMessagesMapping;
    protected SendMessage sendMessage;
    
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    }
    
    protected void initMappingSqlQueries() {
        getFriendsMapping = new FriendsQuery(getDataSource());
        addFriend = new AddFriend(getDataSource());
        getMessagesMapping = new GetMessages(getDataSource());
        sendMessage = new SendMessage(getDataSource());
        
    }
    
    
    public void sendMsg(Account account, String msgTo, String msgBody) {
        sendMessage.run(account, msgTo, msgBody);
    }
    
    
    public List<UserMessage> getMessages(Account account) {
        return getMessagesMapping.run(account);
    }
    
    
    public void addFriend(Account account, String friendId) {
        addFriend.run(account, friendId);
    }
    
    
    public List<Friend> getFriends(Account account) {
        return getFriendsMapping.run(account);
    }
    
    
    public List<Friend> getFriendsOfFriend(Account account, String friendId) {
        //TODO
        return null;
    }
    
    
    class FriendsQuery extends MappingSqlQuery {

        public FriendsQuery(DataSource dataSource) {
            super(dataSource, GET_FRIENDS);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
 
        protected Friend mapRow(ResultSet rs, int rowNum) throws SQLException {
            Friend friend = new Friend();
            friend.setId(rs.getString("userid"));
            friend.setFirstName(rs.getString("firstName"));
            friend.setMiddleName(rs.getString("middleName"));
            friend.setLastName(rs.getString("lastName"));
            return friend;
        }
        
        public List<Friend> run(Account account) {
            return execute(account.getUsername());
        }

    }
    
    
    class AddFriend extends SqlUpdate {
        
        public AddFriend(DataSource dataSource) {
            super(dataSource, INSERT_NEW_FRIEND);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(Account account, String friendId) {
            Object[] params = new Object[] {
                    account.getUsername(),
                    friendId
            };
            return update(params);
        }
       
    }
    
    
    class GetMessages extends MappingSqlQuery {
        
        public GetMessages(DataSource dataSource) {
            super(dataSource, GET_MESSAGES);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        protected UserMessage mapRow(ResultSet rs, int rowNum) 
        throws SQLException {
            UserMessage msg = new UserMessage();
            msg.setMessageFrom(rs.getString("msgfrom"));
            msg.setMessageBody(rs.getString("msgbody"));
            java.sql.Timestamp timestamp = rs.getTimestamp("time");
            msg.setMessageTime(String.valueOf(timestamp));
            return msg;
        }
        
        public List<UserMessage> run(Account account) {
            return execute(account.getUsername());
        }
        
    }
    
    
    class SendMessage extends SqlUpdate {
        
        public SendMessage(DataSource dataSource) {
            super(dataSource, INSERT_NEW_MESSAGE);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.BLOB));
            compile();
        }
        
        public int run(Account account, String msgTo, String msgBody) {
            Object[] params = new Object[] {
                    account.getUsername(),
                    msgTo,
                    msgBody
            };
            return update(params);
        }
        
    }
    
} //- class MyNetDAOImpl
