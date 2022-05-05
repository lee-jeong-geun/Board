package org.board.springboot.posts.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface PostsRepository extends JpaRepository<Posts, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from posts p where p.id = :id")
    Optional<Posts> findByIdForUpdate(@Param("id") Long id);
}
