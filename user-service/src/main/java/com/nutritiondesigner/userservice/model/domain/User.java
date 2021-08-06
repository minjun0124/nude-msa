package com.nutritiondesigner.userservice.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nutritiondesigner.userservice.model.audit.BaseTimeEntity;
import com.nutritiondesigner.userservice.model.form.SignUpForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseTimeEntity {

    @JsonIgnore
    @Id
    @Column(name = "user_id")
    @GeneratedValue
    private Long id;

    @Column(name = "username", length = 50, unique = true)
    private String username;

    @JsonIgnore
    @Column(name = "password", length = 100)
    private String password;

    private String email;
    private String phone;

    @Embedded
    private Address address;

    /**
     * 활성화 여부
     */
    @JsonIgnore
    @Column(name = "activated")
    private boolean activated;

//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Question> questions = new ArrayList<>();
//
//    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
//    private Cart cart;

    @ManyToMany
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;

    public void updateInfo(SignUpForm signUpForm, String changePw) {
        password = changePw;
        email = signUpForm.getEmail();
        phone = signUpForm.getPhone();
        address = signUpForm.getAddress();
    }

    public void withdraw() {
        activated = false;
    }
}
