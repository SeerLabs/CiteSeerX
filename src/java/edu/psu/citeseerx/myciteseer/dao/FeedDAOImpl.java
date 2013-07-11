package edu.psu.citeseerx.myciteseer.dao;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.context.ApplicationContextException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.sql.DataSource;
import java.util.List;
import java.util.Random;

import edu.psu.citeseerx.myciteseer.domain.Feed;

public class FeedDAOImpl extends JdbcDaoSupport implements FeedDAO {
    
    protected InsertFeed insertFeed;
    protected GetFeeds getFeeds;
    protected GetFeed getFeed;
    protected DeleteFeed deleteFeed;
    
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
    
    
    protected void initMappingSqlQueries() {
        insertFeed = new InsertFeed(getDataSource());
        getFeeds = new GetFeeds(getDataSource());
        getFeed = new GetFeed(getDataSource());
        deleteFeed = new DeleteFeed(getDataSource());
    } //- initMappingSqlQueries
    
    
    public void addFeed(Feed feed) {
        if (feed.getTitle() == null) {
            feed.setTitle("Unnamed Feed");
        }
        Random random = new Random(System.currentTimeMillis());
        long id = random.nextLong();
        while (getFeed.run(id) != null) {
            id = random.nextLong();
        }
        feed.setId(id);
        insertFeed.run(feed);
        
    }  //- addFeed;
    
    
    public List<Feed> getFeeds(String userid) {
        return getFeeds.run(userid);
    } //- getFeeds
    
    
    public Feed getFeed(long id) {
        return getFeed.run(id);
    } //- getFeed
    
    
    public void deleteFeed(long id, String userid) {
        deleteFeed.run(id, userid);
    } //- deleteFeed
    
    
    private static final String DEF_INSERT_FEED_STMT =
        "insert into feeds values (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

    protected class InsertFeed extends SqlUpdate {
        
        public InsertFeed(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_FEED_STMT);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(Feed feed) {
            Object[] params = new Object[] {
                    new Long(feed.getId()), feed.getUserid(), feed.getTitle(),
                    feed.getType(), feed.getDesc(), feed.getParams()
            };
            return update(params);
        }
        
    }  //- class InsertFeed
    
    
    private static final String DEF_GET_FEEDS_QUERY =
        "select id, userid, title, type, desc, params " +
        "from feeds where userid=?";
    
    protected class GetFeeds extends MappingSqlQuery {
        
        public GetFeeds(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_FEEDS_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public Feed mapRow(ResultSet rs, int rowNum) throws SQLException {
            return mapFeed(rs);
        }
        
        public List<Feed> run(String userid) {
            return execute(userid);
        }
        
    }  //- class GetFeeds
    
    
    private static final String DEF_GET_FEED_QUERY =
        "select id, userid, title, type, desc, params from feeds where id=?";
    
    protected class GetFeed extends MappingSqlQuery {
        
        public GetFeed(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_FEED_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public Feed mapRow(ResultSet rs, int rowNum) throws SQLException {
            return mapFeed(rs);
        }
        
        public Feed run(long id) {
            List<Feed> feeds = execute(id);
            if (feeds.size() > 0) {
                return (Feed)feeds.get(0);
            } else {
                return null;
            }
        }
        
    }  //- class GetFeed
    
    
    private static Feed mapFeed(ResultSet rs) throws SQLException {
        Feed feed = new Feed();
        feed.setId(rs.getLong("id"));
        feed.setUserid(rs.getString("userid"));
        feed.setTitle(rs.getString("title"));
        feed.setType(rs.getString("type"));
        feed.setDesc(rs.getString("desc"));
        feed.setParams(rs.getString("params"));
        return feed;
        
    }  //- mapFeed
    
    
    private static final String DEF_DEL_FEED_STMT =
        "delete from feeds where id=? and userid=?";
    
    protected class DeleteFeed extends SqlUpdate {
        
        public DeleteFeed(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DEL_FEED_STMT);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(long id, String userid) {
            return update(new Object[] { new Long(id), userid } );
        }
        
    }  //- class DeleteFeed
    
}  //- class FeedDAOImpl
