package uk.gov.hmcts.ccd;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ComplexWithComplexList {
    private List<Simple> fields = Lists.newArrayList(new Simple(), new Simple());
}
