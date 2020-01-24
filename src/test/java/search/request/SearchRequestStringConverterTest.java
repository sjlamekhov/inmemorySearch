package search.request;

import org.junit.Test;
import search.ConditionType;

import java.util.Collections;

import static org.junit.Assert.*;

public class SearchRequestStringConverterTest {

    private SearchRequestConverter searchRequestStringConverter = new SearchRequestStringConverter();

    @Test
    public void convertToStringTest() {
        assertEquals("(attribute,EQ,value)", searchRequestStringConverter.convertToString(
                SearchRequest.Builder.newInstance()
                        .setAttributeToSearch("attribute")
                        .setConditionType(ConditionType.EQ)
                        .setValueToSearch("value")
                        .build())
        );
        assertEquals("((attribute,EQ,value)and(attributeAnd,EQ,valueAnd))", searchRequestStringConverter.convertToString(
                SearchRequest.Builder.newInstance()
                        .setAttributeToSearch("attribute")
                        .setConditionType(ConditionType.EQ)
                        .setValueToSearch("value")
                        .and(Collections.singleton(SearchRequest.Builder.newInstance()
                                .setAttributeToSearch("attributeAnd")
                                .setConditionType(ConditionType.EQ)
                                .setValueToSearch("valueAnd")
                                .build()))
                        .build())
        );
        assertEquals("((attribute,EQ,value)or(attributeAnd,EQ,valueAnd))", searchRequestStringConverter.convertToString(
                SearchRequest.Builder.newInstance()
                        .setAttributeToSearch("attribute")
                        .setConditionType(ConditionType.EQ)
                        .setValueToSearch("value")
                        .or(Collections.singleton(SearchRequest.Builder.newInstance()
                                .setAttributeToSearch("attributeAnd")
                                .setConditionType(ConditionType.EQ)
                                .setValueToSearch("valueAnd")
                                .build()))
                        .build())
        );
    }

}