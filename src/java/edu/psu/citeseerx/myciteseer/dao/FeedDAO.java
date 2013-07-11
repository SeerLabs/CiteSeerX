package edu.psu.citeseerx.myciteseer.dao;

import java.util.List;
import edu.psu.citeseerx.myciteseer.domain.Feed;

public interface FeedDAO {

    public void addFeed(Feed feed);
    
    public List<Feed> getFeeds(String userid);
    
    public Feed getFeed(long id);
    
    public void deleteFeed(long id, String userid);
    
} //- interface FeedDAO
