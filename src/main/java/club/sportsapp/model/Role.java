package club.sportsapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PRIVATE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "roles_capabilities",
                joinColumns = @JoinColumn(name = "role_id"),
                inverseJoinColumns = @JoinColumn(name = "capability_id"))
    private Set<Capability> capabilities = new HashSet<>();

    public Set<Capability> getAllCapabilities() {
        return Set.copyOf(capabilities);
    }

    public void addCapability(Capability capability) {
        if (capabilities == null) capabilities = new HashSet<>();
        capabilities.add(capability);
        capability.getRoles().add(this);
    }

    public void removeCapability(Capability capability) {
        if (capabilities == null) return;
        capabilities.remove(capability);
        capability.getRoles().remove(this);
    }

    public Set<User> getAllUsers() {
        return Set.copyOf(users);
    }

    public void addUser(User user) {
        if (users == null) users = new HashSet<>();
        users.add(user);
        user.setRole(this);
    }

    public void removeUser(User user) {
        if (users == null) return;
        users.remove(user);
        user.setRole(null);
    }

    public void addAllUsers(Collection<User> users) {
        users.forEach(this::addUser);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Role role)) return false;
        return Objects.equals(getName(), role.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }
}
