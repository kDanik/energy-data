package energyData.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
public class EnergyConsumptionEntry {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateTime;

    // maybe big decimal makes more sense
    private Float valueInGw;

    @ManyToOne
    @JoinColumn(name = "energie_type_id")
    private EnergyType energyType;
}
