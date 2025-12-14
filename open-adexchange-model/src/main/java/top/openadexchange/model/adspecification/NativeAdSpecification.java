package top.openadexchange.model.adspecification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NativeAdSpecification {

    private String ctaText;
    private String ctaUrl;
}
