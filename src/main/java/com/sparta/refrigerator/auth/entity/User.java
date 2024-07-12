package com.sparta.refrigerator.auth.entity;

import com.sparta.refrigerator.auth.enumeration.UserAuth;
import com.sparta.refrigerator.common.TimeStamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="user")
@Getter
@NoArgsConstructor
public class User extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, length = 15)
    private String userName;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserAuth auth;
    @Column
    private String refreshToken;

    public User(String userName, String password, UserAuth auth) {
        this.userName = userName;
        this.password = password;
        this.auth = auth;
    }

    public void updateRefresh(String refresh) {
        this.refreshToken = refresh;
    }

    public void updateUserAuth(UserAuth auth){
        this.auth = auth;
    }

}
