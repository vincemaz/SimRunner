package org.schambon.loadsimrunner.config;

import com.mongodb.client.model.CollationAlternate;
import com.mongodb.client.model.CollationCaseFirst;
import com.mongodb.client.model.CollationMaxVariable;
import com.mongodb.client.model.CollationStrength;
import org.bson.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IndexDefinitionTest {

    @Test
    public void testLegacyIndex() {
        var config = Document.parse("{ 'field1': 1, 'field2': -1 }");
        var indexDef = new IndexDefinition(config);
        assertEquals(config, indexDef.getKeys());
    }

    @Test
    public void testIndexWithKey() {
        var config = Document.parse("{ " +
                "'key':{'field1':1, 'field2': -1}" +
                "}");
        var indexDef = new IndexDefinition(config);
        assertEquals(config.get("key"), indexDef.getKeys());
    }

    @Test
    public void testUniqueOption() {
        var config = Document.parse("{ " +
                "'key':{'field1':1}, 'unique': true" +
                "}");
        var indexDef = new IndexDefinition(config);
        assertTrue(indexDef.getOptions().isUnique());
    }

    @Test
    public void testNameOption() {
        var config = Document.parse("{ 'key':{'field1':1}, 'name': 'myindex'}");
        var indexDef = new IndexDefinition(config);
        assertEquals("myindex", indexDef.getOptions().getName());
    }

    @Test
    public void testpartialFilterExpressionOption(){
        var config = Document.parse("{ 'key':{'field1':1}, 'partialFilterExpression': { 'field1': { $eq: 'A' }}}");
        var indexDef = new IndexDefinition(config);
        var optionDocument = Objects.requireNonNull(indexDef.getOptions().getPartialFilterExpression()).toBsonDocument();
        assertEquals("{\"field1\": {\"$eq\": \"A\"}}", optionDocument.toJson());
    }

    @Test
    public void testSparseOption() {
        var config = Document.parse("{ 'key':{'field1':1}, 'sparse': true}");
        var indexDef = new IndexDefinition(config);
        assertTrue(indexDef.getOptions().isSparse());
    }

    @Test
    public void testExpireAfterSeconds() {
        var config = Document.parse("{ 'key':{'field1':1}, 'expireAfterSeconds': 180}");
        var indexDef = new IndexDefinition(config);
        assertEquals(180, indexDef.getOptions().getExpireAfter(TimeUnit.SECONDS));
    }

    @Test
    public void testHiddenOption() {
        var config = Document.parse("{ 'key':{'field1':1}, 'hidden': true}");
        var indexDef = new IndexDefinition(config);
        assertTrue(indexDef.getOptions().isHidden());
    }

    @Test
    public void testStorageEngineOption() {
        var config = Document.parse("{ 'key':{'field1':1}, " +
                "'storageEngine': {" +
                    "wiredTiger: { configString: 'block_compressor=zlib' }" +
                "}}");
        var indexDef = new IndexDefinition(config);
        var optionDocument = Objects.requireNonNull(indexDef.getOptions().getStorageEngine()).toBsonDocument();
        assertEquals("{\"wiredTiger\": {\"configString\": \"block_compressor=zlib\"}}", optionDocument.toJson());
    }

    @Test
    public void testCollationOption() {
        var config = Document.parse("{ 'key':{'field1':1}, " +
                "'collation': {" +
                    "'locale': 'fr', " +
                    "'caseLevel': true, " +
                    "'caseFirst': 'upper', " +
                    "'strength': 1," +
                    "'numericOrdering': true, " +
                    "'alternate': 'shifted', " +
                    "'maxVariable': 'space', " +
                    "'backwards': true, " +
                    "'normalization': true " +
                "}}");
        var indexDef = new IndexDefinition(config);
        var collation = indexDef.getOptions().getCollation();
        Assertions.assertNotNull(collation);
        assertEquals("fr", collation.getLocale());
        assertEquals(Boolean.TRUE, collation.getCaseLevel());
        assertEquals(CollationCaseFirst.UPPER, collation.getCaseFirst());
        assertEquals(CollationStrength.PRIMARY, collation.getStrength());
        assertEquals(Boolean.TRUE, collation.getNumericOrdering());
        assertEquals(CollationAlternate.SHIFTED, collation.getAlternate());
        assertEquals(CollationMaxVariable.SPACE, collation.getMaxVariable());
        assertEquals(Boolean.TRUE, collation.getBackwards());
        assertEquals(Boolean.TRUE, collation.getNormalization());
    }

    @Test
    public void testWeightsOption() {
        var config = Document.parse("{ 'key':{'field1':'text'}, 'weights': {'field1': 5} }");
        var indexDef = new IndexDefinition(config);
        var optionDocument = Objects.requireNonNull(indexDef.getOptions().getWeights()).toBsonDocument();
        assertEquals("{\"field1\": 5}", optionDocument.toJson());
    }

    @Test
    public void testDefaultLanguageOption() {
        var config = Document.parse("{ 'key':{'field1':'text'}, 'default_language': 'french'}");
        var indexDef = new IndexDefinition(config);
        assertEquals("french", indexDef.getOptions().getDefaultLanguage());
    }

    @Test
    public void testLanguageOverrideOption() {
        var config = Document.parse("{ 'key':{'field1':'text'}, 'language_override': 'field2'}");
        var indexDef = new IndexDefinition(config);
        assertEquals("field2", indexDef.getOptions().getLanguageOverride());
    }

    @Test
    public void testTextIndexVersionOption() {
        var config = Document.parse("{ 'key':{'field1':'text'}, 'textIndexVersion': 3}");
        var indexDef = new IndexDefinition(config);
        assertEquals(3, indexDef.getOptions().getTextVersion());
    }

    @Test
    public void test2dsphereIndexVersionOption() {
        var config = Document.parse("{ 'key':{'field1':'2dsphere'}, '2dsphereIndexVersion': 3}");
        var indexDef = new IndexDefinition(config);
        assertEquals(3, indexDef.getOptions().getSphereVersion());
    }

    @Test
    public void testBitsOption() {
        var config = Document.parse("{ 'key':{'field1':'2d'}, 'bits': 3}");
        var indexDef = new IndexDefinition(config);
        assertEquals(3, indexDef.getOptions().getBits());
    }

    @Test
    public void testMinOption() {
        var config = Document.parse("{ 'key':{'field1':'2d'}, 'min': -90.0}");
        var indexDef = new IndexDefinition(config);
        assertEquals(Double.valueOf(-90), indexDef.getOptions().getMin());
        config = Document.parse("{ 'key':{'field1':'2d'}, 'min': 90}");
        indexDef = new IndexDefinition(config);
        assertEquals(Double.valueOf(90), indexDef.getOptions().getMin());
    }

    @Test
    public void testMaxOption() {
        var config = Document.parse("{ 'key':{'field1':'2d'}, 'max': -90.0}");
        var indexDef = new IndexDefinition(config);
        assertEquals(Double.valueOf(-90), indexDef.getOptions().getMax());
        config = Document.parse("{ 'key':{'field1':'2d'}, 'max': 90}");
        indexDef = new IndexDefinition(config);
        assertEquals(Double.valueOf(90), indexDef.getOptions().getMax());
    }

    @Test
    public void testWildcardProjectionOption() {
        var config = Document.parse("{ 'key':{'$**': 1}, " +
                "'wildcardProjection' : {" +
                    "'_id' : 0" +
                "}}");
        var indexDef = new IndexDefinition(config);
        var optionDocument = Objects.requireNonNull(indexDef.getOptions().getWildcardProjection()).toBsonDocument();
        assertEquals("{\"_id\": 0}", optionDocument.toJson());

    }
}
