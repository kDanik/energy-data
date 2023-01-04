package energyData.domain;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@ToString
@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"timestamp", "energy_type_id"})
}, indexes = @Index(name="timestampTypeIndex", columnList = "timestamp, energy_type_id", unique = true))
public class EnergyConsumptionEntry {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Instant has to be used to avoid issues with automatic converting time using timezone
    @NotNull
    private Long timestamp;

    @NotNull
    private Double valueInMw;

    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "energy_type_id")
    @NotNull
    private EnergyType energyType;
}
