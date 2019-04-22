package com.negen.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Setter
@Getter
@ToString
@Entity
@Table(name = "user")
public class User implements UserDetails {
    static Logger log = LoggerFactory.getLogger(User.class);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Column(name = "user_name")
    String userName;
    @Column(name = "user_pass")
    String userPass;
    @ManyToMany(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    List<UserRole> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        List<UserRole> roles = this.getRoles();
        for (UserRole userRole:roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(userRole.getRoleName()));
        }
        log.info("grantedAuthorities===>" + grantedAuthorities.toString());
        return grantedAuthorities;
    }
    @Override
    public String getPassword() {
        return this.userPass;
    }
    @Override
    public String getUsername() {
        return this.userName;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}
