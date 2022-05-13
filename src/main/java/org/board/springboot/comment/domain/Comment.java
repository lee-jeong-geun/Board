package org.board.springboot.comment.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.board.springboot.posts.domain.Posts;
import org.board.springboot.user.domain.User;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posts_id")
    private Posts posts;

    @Builder
    public Comment(String content, User user, Posts posts) {
        this.content = content;
        this.user = user;
        this.posts = posts;
        user.getCommentList().add(this);
        posts.getCommentList().add(this);
    }
}
