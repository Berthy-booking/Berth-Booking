package app.database.entity;

import app.database.entity.enums.AccountKind;
import app.database.entity.enums.AccountRole;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Convert(converter = AccountKind.Converter.class)
    private AccountKind kind;

    private String email;
    private byte[] passwordHash;
    private byte[] salt;

    private String googleMail;

    @NotNull
    private LocalDateTime joinDateTime = LocalDateTime.now();

    @ElementCollection(targetClass = AccountRole.class)
    @CollectionTable(name = "account_role", joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "role")
    @Convert(converter = AccountRole.Converter.class)
    private Set<AccountRole> roles = new HashSet<>();
}
