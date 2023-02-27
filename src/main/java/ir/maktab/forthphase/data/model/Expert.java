package ir.maktab.forthphase.data.model;

import ir.maktab.forthphase.data.model.enums.ExpertStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class Expert extends Person implements UserDetails {

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    //private boolean enabled;

    private String aboutMe;

    @Column(columnDefinition = "double precision default 0")
    private double rating;

    @Column(columnDefinition = "boolean default false")
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    ExpertStatus expertStatus;

    private byte[] image;

    @OneToMany(mappedBy = "expert", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Opinion> opinions = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            joinColumns = @JoinColumn(name = "expert_id"),
            inverseJoinColumns = @JoinColumn(name = "sub_service_id"))
    private Set<SubServices> subServices = new HashSet<>();

    @Override
    public String toString() {
        return "Expert{" +

                "aboutMe='" + aboutMe + '\'' +
                ", rating=" + rating +
                ", isActive=" + isActive +
                ", expertStatus=" + expertStatus +
                ", firstName='" + this.getFirstName() + '\'' +
                ", lastName='" + this.getLastName() + '\'' +
                ", email='" + this.getEmail() + '\'' +
                ", registerDate=" + this.getRegisterDate() +
                ", credit=" + this.getCredit() +
                ", nationalCode='" + this.getNationalCode() + '\'' +
                '}' + "\n";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
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
        return isActive;
    }
}
