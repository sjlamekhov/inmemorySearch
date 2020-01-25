package search.request;

import org.junit.Test;
import search.ConditionType;

import java.util.Collections;

import static org.junit.Assert.*;

public class SearchRequestStringConverterTest {

    private SearchRequestConverter searchRequestStringConverter = new SearchRequestStringConverter();

    @Test
    public void buildFromStringTest() {
        assertEquals(searchRequestStringConverter.buildFromString("(attribute,EQ,value)"),
                SearchRequest.Builder.newInstance()
                        .setAttributeToSearch("attribute")
                        .setConditionType(ConditionType.EQ)
                        .setValueToSearch("value")
                        .build()
        );
        assertEquals(searchRequestStringConverter.buildFromString("(attribute1,EQ,value1)and(attribute2,EQ,value2)"),
                SearchRequest.Builder.newInstance()
                        .setAttributeToSearch("attribute1")
                        .setConditionType(ConditionType.EQ)
                        .setValueToSearch("value1")
                        .and(Collections.singleton(SearchRequest.Builder.newInstance()
                                .setAttributeToSearch("attribute2")
                                .setConditionType(ConditionType.EQ)
                                .setValueToSearch("value2")
                                .build()))
                        .build()
        );
    }

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