package org.board.springboot.auth.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.board.springboot.user.domain.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity(name = "auth_user")
@NoArgsConstructor
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime lastLogin;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
