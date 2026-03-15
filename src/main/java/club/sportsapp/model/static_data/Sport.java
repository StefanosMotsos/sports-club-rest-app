package club.sportsapp.model.static_data;

import club.sportsapp.model.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sports")
public class Sport{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "sport", fetch = FetchType.LAZY)
    public Set<Member> members = new HashSet<>();

    public Set<Member> getAllMembers() {
        return Collections.unmodifiableSet(members);
    }

    public void addMember(Member member) {
        if (members == null) members = new HashSet<>();
        members.add(member);
        member.setSport(this);
    }

    public void removeMember(Member member) {
        if (members == null) return;
        members.remove(member);
        member.setSport(null);
    }
}
