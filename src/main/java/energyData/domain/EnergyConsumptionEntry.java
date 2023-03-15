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
@IdClass(EnergyConsumptionEntryId.class)
@Table(indexes = @Index(name = "timestampTypeIndex", columnList = "timestamp, energy_type_id", unique = true))
public class EnergyConsumptionEntry {
    // Instant has to be used to avoid issues with automatic converting time using timezone
    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "energy_type_id")
    @NotNull
    @Id
    @ToString.Exclude
    private EnergyType energyType;

    @NotNull
    @Id
    private Long timestamp;

    @NotNull
    private Double valueInMw;
}
