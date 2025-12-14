package top.openadexchange.model.adspecification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoAdSpecification {

    private int minDuration;
    private int maxDuration;
    private boolean skipable;
}
