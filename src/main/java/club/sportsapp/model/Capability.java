package club.sportsapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "capabilities")
public class Capability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PRIVATE)
    @ManyToMany(mappedBy = "capabilities", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    public Set<Role> getAllRoles() {
        return Set.copyOf(roles);
    }

    public void addRole(Role role) {
        if (roles == null) roles = new HashSet<>();
        roles.add(role);
        role.getCapabilities().add(this);
    }

    public void removeRole(Role role) {
        if (roles == null) return;
        roles.remove(role);
        role.getCapabilities().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Capability that)) return false;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }
}
