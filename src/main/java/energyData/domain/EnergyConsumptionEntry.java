package energyData.domain;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@Entity
// uniqueConstraints should only take real columnNames from database.
// Somehow it does that only for one of the fields, and for dateTime it doesn't allow date_time.
// Probably it is bug in hibernate
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"dateTime", "energy_type_id"})
})
public class EnergyConsumptionEntry {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDateTime dateTime;

    @NotNull
    private Double valueInMw;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "energy_type_id")
    @NotNull
    private EnergyType energyType;
}
