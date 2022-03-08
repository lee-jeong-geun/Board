package org.board.springboot.posts.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.board.springboot.user.domain.User;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Posts(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }
}
