package org.board.springboot.posts.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.board.springboot.comment.domain.Comment;
import org.board.springboot.user.domain.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity(name = "posts")
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "posts_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private int viewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "posts")
    private List<Comment> commentList = new ArrayList<>();

    @Builder
    public Posts(String title, String content, int viewCount, User user) {
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.user = user;
        user.getPostsList().add(this);
    }

    public void viewCountUpdate(int viewCount) {
        this.viewCount = viewCount;
    }
}
