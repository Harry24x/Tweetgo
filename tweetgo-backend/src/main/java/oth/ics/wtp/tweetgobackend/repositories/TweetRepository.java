package oth.ics.wtp.tweetgobackend.repositories;

import oth.ics.wtp.tweetgobackend.Entities.Tweet;
import oth.ics.wtp.tweetgobackend.Entities.User;
import org.springframework.data.domain.Pageable; // For pagination (at most 20 posts)
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {

    List<Tweet> findByAuthorOrderByTimestampDesc(User author, Pageable pageable);


    List<Tweet> findByAuthorInOrderByTimestampDesc(List<User> authors, Pageable pageable);


    @Query("SELECT p FROM Tweet p WHERE p.author IN :authors ORDER BY p.timestamp DESC")
    List<Tweet> findPostsByAuthors(@Param("authors") List<User> authors, Pageable pageable);


}