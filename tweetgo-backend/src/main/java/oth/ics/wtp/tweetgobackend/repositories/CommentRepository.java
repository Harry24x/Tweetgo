package oth.ics.wtp.tweetgobackend.repositories;

import oth.ics.wtp.tweetgobackend.Entities.Comment;
import oth.ics.wtp.tweetgobackend.Entities.Tweet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTweetOrderByTimestampDesc(Tweet tweet, Pageable pageable);
} 