package ua.nure.readict.entity;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class CurrentUser implements UserDetails {

    private final User user;                     // ← JPA-сутність
    private final Collection<? extends GrantedAuthority> authorities;

    public CurrentUser(User user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }
    /* решта isAccountNonExpired() ... isEnabled() — true */
}
