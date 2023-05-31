package com.carrental.models;

import com.carrental.models.enums.AuthorityType;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CR_AUTHORITY")
public class Authority implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthorityType name;

    @ManyToMany(mappedBy = "authorities", cascade = CascadeType.DETACH)
    private Set<User> users;

    @PreRemove
    public void removeUser() {
        users.forEach(user -> {
            user.removeAuthorities(this);
        });
    }

    @Override
    public String toString() {
        return "Authority{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Authority authority = (Authority) o;
        return Objects.equals(id, authority.id) &&
                name == authority.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}