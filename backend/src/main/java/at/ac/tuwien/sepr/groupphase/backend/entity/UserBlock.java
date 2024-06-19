package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_block")
@IdClass(UserBlockKey.class)
public class UserBlock {

    @Id
    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private ApplicationUser user;

    @Id
    @ManyToOne
    @JoinColumn(name = "BLOCKED_USER_ID")
    private ApplicationUser blockedUser;
}
